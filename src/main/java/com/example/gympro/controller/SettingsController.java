package com.example.gympro.controller;

import com.example.gympro.repository.settings.SettingsRepository;
import com.example.gympro.service.AuthorizationService;
import com.example.gympro.service.settings.SettingsService;
import com.example.gympro.viewModel.EventDiscountViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Controller cho màn hình Settings
 */
public class SettingsController {

    @FXML private TabPane tabPane;

    // Tab 1: Thông tin phòng gym
    @FXML private TextField txtGymName;
    @FXML private TextField txtGymAddress;
    @FXML private TextField txtGymPhone;
    @FXML private TextField txtGymEmail;
    @FXML private Button btnSaveBranch;

    // Tab 2: Cấu hình nghiệp vụ
    @FXML private TextField txtGraceDays;
    @FXML private TextField txtReminderDays;
    @FXML private TextField txtMemberCodePrefix;
    @FXML private TextField txtInvoicePrefix;
    @FXML private TextField txtCurrencySymbol;
    @FXML private Button btnSaveBusiness;

    // Tab 3: Chiết khấu sự kiện
    @FXML private TableView<EventDiscountViewModel> tblEventDiscounts;
    @FXML private TableColumn<EventDiscountViewModel, String> colEventName;
    @FXML private TableColumn<EventDiscountViewModel, String> colEventDescription;
    @FXML private TableColumn<EventDiscountViewModel, BigDecimal> colEventDiscountPercent;
    @FXML private TableColumn<EventDiscountViewModel, BigDecimal> colEventDiscountAmount;
    @FXML private TableColumn<EventDiscountViewModel, String> colEventStartDate;
    @FXML private TableColumn<EventDiscountViewModel, String> colEventEndDate;
    @FXML private TableColumn<EventDiscountViewModel, Boolean> colEventActive;
    @FXML private TableColumn<EventDiscountViewModel, Void> colEventActions;
    @FXML private Button btnAddEventDiscount;

    // Tab 4: Thông báo
    @FXML private CheckBox chkAutoReminder;
    @FXML private Button btnSaveNotification;

    // Tab 5: Bảo mật
    @FXML private TextField txtPasswordMinLength;
    @FXML private TextField txtSessionTimeout;
    @FXML private TextField txtMaxLoginAttempts;
    @FXML private TextField txtLockoutDuration;
    @FXML private Button btnSaveSecurity;

    // Tab 6: Hệ thống
    @FXML private Label lblAppVersion;
    @FXML private Label lblDbVersion;
    @FXML private Label lblDbInfo;
    @FXML private Button btnViewLogs;

    private final SettingsService settingsService = new SettingsService();
    private final AuthorizationService authService = new AuthorizationService();
    private final ObservableList<EventDiscountViewModel> eventDiscountList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Kiểm tra quyền truy cập
        if (!authService.canManageSettings()) {
            authService.showAccessDeniedAlert();
            tabPane.setDisable(true);
            return;
        }

        // Load dữ liệu cho tất cả tabs
        loadBranchInfo();
        loadBusinessSettings();
        loadNotificationSettings();
        loadSecuritySettings();
        loadSystemInfo();
        setupEventDiscountsTable();

        // Setup event handlers
        btnSaveBranch.setOnAction(e -> saveBranchInfo());
        btnSaveBusiness.setOnAction(e -> saveBusinessSettings());
        btnSaveNotification.setOnAction(e -> saveNotificationSettings());
        btnSaveSecurity.setOnAction(e -> saveSecuritySettings());
        btnAddEventDiscount.setOnAction(e -> showAddEventDiscountDialog());
        btnViewLogs.setOnAction(e -> showLogs());
    }

    // ========== Tab 1: Thông tin phòng gym ==========
    private void loadBranchInfo() {
        SettingsRepository.BranchInfo branch = settingsService.getBranchInfo();
        if (branch != null) {
            txtGymName.setText(branch.getName());
            txtGymAddress.setText(branch.getAddress());
            txtGymPhone.setText(branch.getPhone());
            // Email chưa có trong branch, có thể lấy từ settings
        }
    }

    private void saveBranchInfo() {
        String name = txtGymName.getText().trim();
        String address = txtGymAddress.getText().trim();
        String phone = txtGymPhone.getText().trim();

        if (name.isEmpty()) {
            showAlert("⚠️ Vui lòng nhập tên phòng gym!");
            return;
        }

        if (settingsService.updateBranchInfo(name, address, phone)) {
            showAlert("✅ Lưu thông tin phòng gym thành công!");
        } else {
            showAlert("❌ Lỗi khi lưu thông tin phòng gym!");
        }
    }

    // ========== Tab 2: Cấu hình nghiệp vụ ==========
    private void loadBusinessSettings() {
        txtGraceDays.setText(String.valueOf(settingsService.getGraceDays()));
        txtReminderDays.setText(String.valueOf(settingsService.getReminderDays()));
        txtMemberCodePrefix.setText(settingsService.getMemberCodePrefix());
        txtInvoicePrefix.setText(settingsService.getInvoicePrefix());
        txtCurrencySymbol.setText(settingsService.getCurrencySymbol());
    }

    private void saveBusinessSettings() {
        try {
            int graceDays = Integer.parseInt(txtGraceDays.getText().trim());
            int reminderDays = Integer.parseInt(txtReminderDays.getText().trim());
            String memberPrefix = txtMemberCodePrefix.getText().trim();
            String invoicePrefix = txtInvoicePrefix.getText().trim();
            String currencySymbol = txtCurrencySymbol.getText().trim();

            if (graceDays < 0 || reminderDays < 0) {
                showAlert("⚠️ Số ngày phải >= 0!");
                return;
            }

            if (memberPrefix.isEmpty() || invoicePrefix.isEmpty() || currencySymbol.isEmpty()) {
                showAlert("⚠️ Vui lòng điền đầy đủ thông tin!");
                return;
            }

            boolean success = settingsService.setGraceDays(graceDays)
                    && settingsService.setReminderDays(reminderDays)
                    && settingsService.setMemberCodePrefix(memberPrefix)
                    && settingsService.setInvoicePrefix(invoicePrefix)
                    && settingsService.setCurrencySymbol(currencySymbol);

            if (success) {
                showAlert("✅ Lưu cấu hình nghiệp vụ thành công!");
            } else {
                showAlert("❌ Lỗi khi lưu cấu hình nghiệp vụ!");
            }
        } catch (NumberFormatException e) {
            showAlert("⚠️ Vui lòng nhập số hợp lệ!");
        }
    }

    // ========== Tab 3: Chiết khấu sự kiện ==========
    private void setupEventDiscountsTable() {
        colEventName.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        colEventDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colEventDiscountPercent.setCellValueFactory(new PropertyValueFactory<>("discountPercent"));
        colEventDiscountAmount.setCellValueFactory(new PropertyValueFactory<>("discountAmount"));
        colEventStartDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStartDate() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getStartDate().toString()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colEventEndDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEndDate() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getEndDate().toString()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colEventActive.setCellValueFactory(new PropertyValueFactory<>("active"));

        // Format currency columns
        colEventDiscountPercent.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.0f%%", item.doubleValue()));
            }
        });
        colEventDiscountAmount.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item.doubleValue()));
            }
        });

        // Actions column
        colEventActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✏️ Sửa");
            private final Button btnDelete = new Button("🗑️ Xóa");
            private final javafx.scene.layout.HBox container = new javafx.scene.layout.HBox(5, btnEdit, btnDelete);

            {
                btnEdit.setOnAction(e -> {
                    EventDiscountViewModel item = getTableRow().getItem();
                    if (item != null) {
                        showEditEventDiscountDialog(item);
                    }
                });
                btnDelete.setOnAction(e -> {
                    EventDiscountViewModel item = getTableRow().getItem();
                    if (item != null) {
                        deleteEventDiscount(item);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });

        tblEventDiscounts.setItems(eventDiscountList);
        loadEventDiscounts();
    }

    private void loadEventDiscounts() {
        eventDiscountList.clear();
        eventDiscountList.addAll(settingsService.getEventDiscounts());
    }

    private void showAddEventDiscountDialog() {
        // TODO: Implement dialog để thêm event discount
        showAlert("📝 Tính năng đang phát triển: Thêm chiết khấu sự kiện");
    }

    private void showEditEventDiscountDialog(EventDiscountViewModel item) {
        // TODO: Implement dialog để sửa event discount
        showAlert("📝 Tính năng đang phát triển: Sửa chiết khấu sự kiện");
    }

    private void deleteEventDiscount(EventDiscountViewModel item) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Bạn có chắc muốn xóa sự kiện: " + item.getEventName() + "?");
        confirm.setContentText("Hành động này không thể hoàn tác.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            eventDiscountList.remove(item);
            settingsService.saveEventDiscounts(eventDiscountList);
            showAlert("✅ Đã xóa sự kiện thành công!");
        }
    }

    // ========== Tab 4: Thông báo ==========
    private void loadNotificationSettings() {
        chkAutoReminder.setSelected(settingsService.isAutoReminderEnabled());
    }

    private void saveNotificationSettings() {
        boolean enabled = chkAutoReminder.isSelected();
        if (settingsService.setAutoReminderEnabled(enabled)) {
            showAlert("✅ Lưu cấu hình thông báo thành công!");
        } else {
            showAlert("❌ Lỗi khi lưu cấu hình thông báo!");
        }
    }

    // ========== Tab 5: Bảo mật ==========
    private void loadSecuritySettings() {
        txtPasswordMinLength.setText(String.valueOf(settingsService.getPasswordMinLength()));
        txtSessionTimeout.setText(String.valueOf(settingsService.getSessionTimeout()));
        txtMaxLoginAttempts.setText(String.valueOf(settingsService.getMaxLoginAttempts()));
        txtLockoutDuration.setText(String.valueOf(settingsService.getLockoutDuration()));
    }

    private void saveSecuritySettings() {
        try {
            int passwordMinLength = Integer.parseInt(txtPasswordMinLength.getText().trim());
            int sessionTimeout = Integer.parseInt(txtSessionTimeout.getText().trim());
            int maxLoginAttempts = Integer.parseInt(txtMaxLoginAttempts.getText().trim());
            int lockoutDuration = Integer.parseInt(txtLockoutDuration.getText().trim());

            if (passwordMinLength < 6 || passwordMinLength > 20) {
                showAlert("⚠️ Độ dài mật khẩu phải từ 6-20 ký tự!");
                return;
            }

            if (sessionTimeout < 5 || sessionTimeout > 120) {
                showAlert("⚠️ Thời gian timeout phải từ 5-120 phút!");
                return;
            }

            boolean success = settingsService.setPasswordMinLength(passwordMinLength)
                    && settingsService.setSessionTimeout(sessionTimeout)
                    && settingsService.setMaxLoginAttempts(maxLoginAttempts)
                    && settingsService.setLockoutDuration(lockoutDuration);

            if (success) {
                showAlert("✅ Lưu cấu hình bảo mật thành công!");
            } else {
                showAlert("❌ Lỗi khi lưu cấu hình bảo mật!");
            }
        } catch (NumberFormatException e) {
            showAlert("⚠️ Vui lòng nhập số hợp lệ!");
        }
    }

    // ========== Tab 6: Hệ thống ==========
    private void loadSystemInfo() {
        lblAppVersion.setText("1.0.0");
        // TODO: Lấy version từ database hoặc properties
        lblDbVersion.setText("MySQL 8.0");
        lblDbInfo.setText("gympro@localhost:3306");
    }

    private void showLogs() {
        // TODO: Implement xem logs
        showAlert("📄 Tính năng đang phát triển: Xem logs");
    }

    // ========== Helper methods ==========
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
