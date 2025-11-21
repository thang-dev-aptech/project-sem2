package com.example.gympro.controller;

import com.example.gympro.service.ExcelExportService;
import com.example.gympro.service.PackageServiceInterface;
import com.example.gympro.service.PackageService;
import com.example.gympro.service.AuthorizationService;
import com.example.gympro.viewModel.Package;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class PackagesController {

    // === FXML - TABLEVIEW ===
    @FXML private TableView<Package> packagesTable;
    @FXML private TableColumn<Package, String> colCode;
    @FXML private TableColumn<Package, String> colName;
    @FXML private TableColumn<Package, BigDecimal> colPrice;
    @FXML private TableColumn<Package, Integer> colDuration;
    @FXML private TableColumn<Package, Boolean> colStatus;
    @FXML private TableColumn<Package, String> colDescription;
    @FXML private TableColumn<Package, LocalDateTime> colCreatedAt;
    @FXML private TableColumn<Package, LocalDateTime> colUpdatedAt;
    @FXML private TableColumn<Package, Void> colActions;

    // === FXML - TOOLBAR & FILTER ===
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    // === FXML - FORM CHI TIẾT ===
    @FXML private Label formTitle;
    @FXML private TextField codeField;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField durationField;
    @FXML private TextArea descriptionArea;
    @FXML private CheckBox isActiveCheckbox;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private VBox detailPane;

    // === SERVICE & DATA ===
    private final PackageServiceInterface packageService = new PackageService();
    private final AuthorizationService authService = new AuthorizationService();
    private final ExcelExportService excelExportService = new ExcelExportService();
    private final ObservableList<Package> packageData = FXCollections.observableArrayList();
    private Package selectedPackage = null;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    // === FXML - EXPORT BUTTON ===
    @FXML private Button exportButton;

    @FXML
    private void initialize() {
        initializeColumns();

        // Khởi tạo ComboBox filter
        statusFilter.setItems(FXCollections.observableArrayList(
                "Tất cả",
                "Hiển thị (Active)",
                "Ẩn (Inactive)"
        ));
        statusFilter.getSelectionModel().selectFirst();

        // Listener cho Tìm kiếm và Lọc
        searchField.textProperty().addListener((obs, oldV, newV) -> handleFilter());
        statusFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> handleFilter());

        loadPackages();
        packagesTable.setItems(packageData);

        // Setup export button
        if (exportButton != null) {
            exportButton.setOnAction(e -> handleExportExcel());
            exportButton.setDisable(false);
        }

        // Listener chọn hàng: CHỈ TẢI DATA VÀO FORM, KHÔNG KÍCH HOẠT CHẾ ĐỘ CHỈNH SỬA
        packagesTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    showPackageDetails(newValue);
                    detailPane.setDisable(newValue == null);
                }
        );

        // Thiết lập trạng thái ban đầu: Vô hiệu hóa form
        setFormEditable(false);
        detailPane.setDisable(true);
        deleteButton.setVisible(false);
        
        // Chỉ OWNER mới quản lý được gói tập
        if (!authService.canManagePackages()) {
            detailPane.setDisable(true);
            detailPane.setVisible(false);
        }
    }

    private void initializeColumns() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("durationDays"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        colStatus.setCellValueFactory(new PropertyValueFactory<>("active"));
        colStatus.setCellFactory(this::formatStatusCell);

        colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colCreatedAt.setCellFactory(this::formatDateTimeCell);
        colUpdatedAt.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        colUpdatedAt.setCellFactory(this::formatDateTimeCell);

        colActions.setCellFactory(col -> createActionCell());
    }

    // Hàm xử lý Lọc và Tìm kiếm
    @FXML
    private void handleFilter() {
        String searchTerm = searchField.getText();
        String status = statusFilter.getSelectionModel().getSelectedItem();

        packageData.clear();
        packageData.addAll(packageService.getFilteredPackages(searchTerm, status));

        packagesTable.getSelectionModel().clearSelection();
        showPackageDetails(null);
        detailPane.setDisable(true);
    }

    // Tải/Làm mới dữ liệu
    @FXML
    public void loadPackages() {
        handleFilter();
    }

    /**
     * Tải dữ liệu từ Model vào Form và thiết lập chế độ XEM (Read-only).
     */
    private void showPackageDetails(Package pkg) {
        if (pkg != null) {
            selectedPackage = pkg;
            formTitle.setText("Chi tiết Gói: " + pkg.getName());

            // Load data
            codeField.setText(pkg.getCode());
            nameField.setText(pkg.getName());
            priceField.setText(pkg.getPrice().toString());
            durationField.setText(String.valueOf(pkg.getDurationDays()));
            descriptionArea.setText(pkg.getDescription());
            isActiveCheckbox.setSelected(pkg.isActive());

            // Thiết lập chế độ XEM (Read-only) mặc định
            setFormEditable(false);
            deleteButton.setVisible(true);
            deleteButton.setDisable(true); // Vô hiệu hóa nút xóa trong chế độ xem
            saveButton.setText("💾 Lưu Thay đổi");

        } else {
            // Trường hợp không có gì được chọn (sau khi xóa, hủy)
            selectedPackage = null;
            formTitle.setText("Chi tiết Gói tập");
            setFormEditable(false);
            clearFormFields();
            deleteButton.setVisible(false);
        }
    }

    /**
     * Chuyển Form sang chế độ Chỉnh sửa. Được gọi bởi nút "✏️".
     */
    private void startEditMode(Package pkg) {
        if (pkg == null) return;

        packagesTable.getSelectionModel().select(pkg); // Đảm bảo hàng được chọn
        formTitle.setText("CHỈNH SỬA GÓI: " + pkg.getName());

        setFormEditable(true); // BẬT CHẾ ĐỘ CHỈNH SỬA
        deleteButton.setDisable(false); // Kích hoạt nút xóa
        saveButton.setText("💾 Lưu Thay đổi");
    }


    // --- CÁC HÀM THAO TÁC FORM ---

    @FXML
    private void handleNewPackage() {
        if (!authService.canManagePackages()) {
            authService.showAccessDeniedAlert();
            return;
        }
        
        packagesTable.getSelectionModel().clearSelection();
        selectedPackage = new Package();
        formTitle.setText("➕ Thêm Gói tập Mới");
        setFormEditable(true); // Chế độ Thêm mới phải là editable
        clearFormFields();
        detailPane.setDisable(false);
        deleteButton.setVisible(false);
        saveButton.setText("💾 Thêm Gói");
    }

    @FXML
    private void handleCancel() {
        // Quay về trạng thái không chọn/vô hiệu hóa
        packagesTable.getSelectionModel().clearSelection();
        showPackageDetails(null);
        detailPane.setDisable(true);
    }

    @FXML
    private void handleSave() {
        if (!authService.canManagePackages()) {
            authService.showAccessDeniedAlert();
            return;
        }
        
        if (!isInputValid()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng kiểm tra lại dữ liệu nhập. Tên, Code, Giá và Số ngày không được để trống/sai định dạng.").showAndWait();
            return;
        }

        // 1. Áp dụng dữ liệu từ Form vào Model
        selectedPackage.setCode(codeField.getText());
        selectedPackage.setName(nameField.getText());
        selectedPackage.setDescription(descriptionArea.getText());
        selectedPackage.setPrice(new BigDecimal(priceField.getText()));
        selectedPackage.setDurationDays(Integer.parseInt(durationField.getText()));
        selectedPackage.setIsActive(isActiveCheckbox.isSelected());

        // 2. Lưu vào Database (GỌI SERVICE)
        Optional<Package> savedPkg = packageService.savePackage(selectedPackage);

        if (savedPkg.isPresent()) {
            if (selectedPackage.getId() == 0) {
                packageData.add(savedPkg.get());
                packagesTable.getSelectionModel().select(savedPkg.get());
                new Alert(Alert.AlertType.INFORMATION, "Thêm gói tập thành công!").showAndWait();
            } else {
                packagesTable.refresh();
                new Alert(Alert.AlertType.INFORMATION, "Cập nhật thành công!").showAndWait();
            }
            // Sau khi lưu, chuyển về chế độ XEM
            setFormEditable(false);
            deleteButton.setDisable(true);
        } else {
            new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu/cập nhật gói tập vào Database.").showAndWait();
        }
    }

    @FXML
    private void handleDelete() {
        if (!authService.canManagePackages()) {
            authService.showAccessDeniedAlert();
            return;
        }
        
        if (selectedPackage == null || selectedPackage.getId() == 0) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa gói tập " + selectedPackage.getName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Xác nhận Xóa");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (packageService.deletePackage(selectedPackage.getId())) {
                packageData.remove(selectedPackage);
                packagesTable.getSelectionModel().clearSelection();
                showPackageDetails(null);
                new Alert(Alert.AlertType.INFORMATION, "Xóa gói tập thành công!").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Lỗi khi xóa gói tập. (Kiểm tra quy tắc nghiệp vụ/khóa ngoại)").showAndWait();
            }
        }
    }

    // --- HÀM TIỆN ÍCH CHO CELL FACTORY ---

    private TableCell<Package, Void> createActionCell() {
        return new TableCell<>() {
            private final Button editButton = new Button("Chỉnh sửa");
            private final HBox pane = new HBox(5, editButton);

            {
                editButton.getStyleClass().add("icon-small-button");
                // Khi nhấn nút SỬA, gọi hàm startEditMode
                editButton.setOnAction(event -> {
                    Package pkg = getTableView().getItems().get(getIndex());
                    startEditMode(pkg); // <--- KÍCH HOẠT CHẾ ĐỘ CHỈNH SỬA
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }

    private TableCell<Package, LocalDateTime> formatDateTimeCell(TableColumn<Package, LocalDateTime> column) {
        return new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : DATE_TIME_FORMATTER.format(item));
            }
        };
    }

    private TableCell<Package, Boolean> formatStatusCell(TableColumn<Package, Boolean> column) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("status-active", "status-inactive");

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "🟢 Hiển thị" : "🔴 Ẩn");
                    getStyleClass().add(item ? "status-active" : "status-inactive");
                }
            }
        };
    }

    // --- CÁC HÀM TIỆN ÍCH FORM ---

    private void clearFormFields() {
        codeField.clear();
        nameField.clear();
        priceField.clear();
        durationField.clear();
        descriptionArea.clear();
        isActiveCheckbox.setSelected(true);
    }

    /**
     * Điều khiển khả năng chỉnh sửa của các trường Form và nút Lưu.
     */
    private void setFormEditable(boolean editable) {
        // Cho phép chỉnh sửa Code chỉ khi Thêm mới (ID=0)
        codeField.setEditable(editable && (selectedPackage != null && selectedPackage.getId() == 0));
        nameField.setEditable(editable);
        priceField.setEditable(editable);
        durationField.setEditable(editable);
        descriptionArea.setEditable(editable);
        isActiveCheckbox.setDisable(!editable);
        saveButton.setDisable(!editable);
    }

    private boolean isInputValid() {
        if (nameField.getText() == null || nameField.getText().isEmpty() ||
                codeField.getText() == null || codeField.getText().isEmpty()) {
            return false;
        }

        try {
            new BigDecimal(priceField.getText());
            if (Integer.parseInt(durationField.getText()) <= 0) return false;
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    @FXML
    private void handleExportExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Xuất danh sách Gói tập");
            fileChooser.setInitialFileName("DanhSachGoiTap.xlsx");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

            File file = fileChooser.showSaveDialog(packagesTable.getScene().getWindow());
            if (file != null) {
                excelExportService.exportPackages(
                    packageData.stream().toList(),
                    file.getAbsolutePath()
                );
                showAlert(Alert.AlertType.INFORMATION, "Thành công", 
                    "✅ Xuất Excel thành công: " + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", 
                "❌ Lỗi khi xuất Excel: " + ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}