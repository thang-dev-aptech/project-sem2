package com.example.gympro.controller;

import com.example.gympro.service.ExcelExportService;
import com.example.gympro.service.MemberService;
import com.example.gympro.service.AuthorizationService;
import com.example.gympro.viewModel.Member;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MembersController {

    // === FXML - TABLEVIEW ===
    @FXML private TableView<Member> membersTable;
    @FXML private TableColumn<Member, String> colMemberCode;
    @FXML private TableColumn<Member, String> colFullName;
    @FXML private TableColumn<Member, String> colPhone;
    @FXML private TableColumn<Member, String> colEmail;
    @FXML private TableColumn<Member, String> colStatus;
    @FXML private TableColumn<Member, String> colGender;
    @FXML private TableColumn<Member, LocalDate> colDOB;
    @FXML private TableColumn<Member, LocalDateTime> colCreatedAt;
    @FXML private TableColumn<Member, LocalDateTime> colUpdatedAt;
    @FXML private TableColumn<Member, Void> colActions;

    // === FXML - TOOLBAR & FILTER ===
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    // === FXML - FORM CHI TIẾT ===
    @FXML private Label formTitle;
    @FXML private TextField codeField;
    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea addressArea;
    @FXML private TextArea noteArea;

    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private VBox detailPane;

    // === SERVICE & DATA ===
    private final MemberService memberService = new MemberService();
    private final AuthorizationService authService = new AuthorizationService();
    private final ExcelExportService excelExportService = new ExcelExportService();
    private final ObservableList<Member> memberData = FXCollections.observableArrayList();
    private Member selectedMember = null;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // === FXML - EXPORT BUTTON ===
    @FXML private Button exportButton;

    // Dữ liệu cho ComboBoxes
    private final ObservableList<String> statusList = FXCollections.observableArrayList(
            "Tất cả", "Chờ (PENDING)", "Kích hoạt (ACTIVE)", "Hết hạn (EXPIRED)", "Tạm dừng (PAUSED)", "Gia hạn (RENEWED)"
    );
    private final ObservableList<String> genderList = FXCollections.observableArrayList(
            "Nam (MALE)", "Nữ (FEMALE)", "Khác (OTHER)"
    );

    @FXML
    private void initialize() {
        initializeColumns();

        // Khởi tạo ComboBox filter
        statusFilter.setItems(statusList);
        statusFilter.getSelectionModel().selectFirst();

        // Khởi tạo ComboBoxes trong Form
        genderComboBox.setItems(genderList);
        // Lấy danh sách trạng thái cho Form (bỏ "Tất cả")
        ObservableList<String> formStatusList = FXCollections.observableArrayList(statusList);
        formStatusList.remove(0); // Xóa "Tất cả"
        statusComboBox.setItems(formStatusList);


        // Listener cho Tìm kiếm và Lọc
        searchField.textProperty().addListener((obs, oldV, newV) -> handleFilter());
        statusFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> handleFilter());

        loadMembers();
        membersTable.setItems(memberData);

        // Setup export button
        if (exportButton != null) {
            exportButton.setOnAction(e -> handleExportExcel());
            exportButton.setDisable(false);
        }

        // Listener chọn hàng: CHỈ TẢI DATA VÀO FORM (chế độ XEM)
        membersTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    showMemberDetails(newValue);
                    detailPane.setDisable(newValue == null);
                }
        );

        // Thiết lập trạng thái ban đầu: Vô hiệu hóa form
        setFormEditable(false);
        detailPane.setDisable(true);
        deleteButton.setVisible(false);
        
        // Ẩn nút xóa nếu user không có quyền
        if (!authService.canDelete()) {
            deleteButton.setVisible(false);
        }
    }

    private void initializeColumns() {
        colMemberCode.setCellValueFactory(new PropertyValueFactory<>("memberCode"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));

        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(this::formatStatusCell);

        colDOB.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colDOB.setCellFactory(this::formatDateCell);

        colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colCreatedAt.setCellFactory(this::formatDateTimeCell);

        // ĐÃ THÊM: Ánh xạ cột Cập nhật
        colUpdatedAt.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        colUpdatedAt.setCellFactory(this::formatDateTimeCell);

        colActions.setCellFactory(col -> createActionCell());
    }

    // Hàm xử lý Lọc và Tìm kiếm
    @FXML
    private void handleFilter() {
        String searchTerm = searchField.getText();
        String status = statusFilter.getSelectionModel().getSelectedItem();

        memberData.clear();
        memberData.addAll(memberService.getFilteredMembers(searchTerm, status));

        membersTable.getSelectionModel().clearSelection();

        showMemberDetails(null);
        detailPane.setDisable(true);
    }

    // Tải/Làm mới dữ liệu
    @FXML
    public void loadMembers() {
        handleFilter();
    }

    /**
     * Tải dữ liệu từ Model vào Form và thiết lập chế độ XEM (Read-only).
     */
    private void showMemberDetails(Member member) {
        if (member != null) {
            selectedMember = member;
            formTitle.setText("Chi tiết Hội viên: " + member.getFullName());

            // Load data
            codeField.setText(member.getMemberCode());
            fullNameField.setText(member.getFullName());
            phoneField.setText(member.getPhone());
            emailField.setText(member.getEmail());
            dobPicker.setValue(member.getDob());
            addressArea.setText(member.getAddress());
            noteArea.setText(member.getNote());

            // Xử lý ComboBox (tìm đúng giá trị dựa trên DB)
            genderComboBox.setValue(findComboBoxValue(genderList, member.getGender()));
            statusComboBox.setValue(findComboBoxValue(statusList, member.getStatus()));

            // Thiết lập chế độ XEM (Read-only) mặc định
            setFormEditable(false);
            deleteButton.setVisible(true);
            deleteButton.setDisable(true);
            saveButton.setText("💾 Lưu Thay đổi");

        } else {
            // Trường hợp không có gì được chọn
            selectedMember = null;
            formTitle.setText("Chi tiết Hội viên");
            setFormEditable(false);
            clearFormFields();
            deleteButton.setVisible(false);
        }
    }

    /**
     * Chuyển Form sang chế độ Chỉnh sửa. Được gọi bởi nút "✏️".
     */
    private void startEditMode(Member member) {
        if (member == null) return;

        membersTable.getSelectionModel().select(member);
        formTitle.setText("CHỈNH SỬA HỘI VIÊN: " + member.getFullName());

        setFormEditable(true);
        deleteButton.setDisable(false);
        saveButton.setText("💾 Lưu Thay đổi");

        detailPane.requestFocus();
    }

    // --- CÁC HÀM THAO TÁC FORM ---

    @FXML
    private void handleNewMember() {
        membersTable.getSelectionModel().clearSelection();
        selectedMember = new Member();
        formTitle.setText("➕ Thêm Hội viên Mới");
        setFormEditable(true);
        clearFormFields();
        detailPane.setDisable(false);
        deleteButton.setVisible(false);
        saveButton.setText("💾 Thêm Hội viên");

        detailPane.requestFocus();
    }

    @FXML
    private void handleCancel() {
        membersTable.getSelectionModel().clearSelection();
        showMemberDetails(null);
        detailPane.setDisable(true);
    }

    @FXML
    private void handleSave() {
        if (!isInputValid()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng kiểm tra lại dữ liệu nhập. Mã HV, Tên, SĐT không được trống.").showAndWait();
            return;
        }

        // 1. Áp dụng dữ liệu từ Form vào Model
        selectedMember.setMemberCode(codeField.getText());
        selectedMember.setFullName(fullNameField.getText());
        selectedMember.setPhone(phoneField.getText());
        selectedMember.setEmail(emailField.getText());
        selectedMember.setDob(dobPicker.getValue());
        selectedMember.setAddress(addressArea.getText());
        selectedMember.setNote(noteArea.getText());

        // Lấy giá trị ENUM từ ComboBox
        selectedMember.setGender(extractValueFromComboBox(genderComboBox.getValue(), "OTHER"));
        selectedMember.setStatus(extractValueFromComboBox(statusComboBox.getValue(), "PENDING"));

        // 2. Lưu vào Database (GỌI SERVICE)
        Optional<Member> savedMember = memberService.saveMember(selectedMember);

        if (savedMember.isPresent()) {
            if (selectedMember.getId() == 0) {
                // Thêm mới
                loadMembers(); // Tải lại để lấy ID và timestamp mới
                new Alert(Alert.AlertType.INFORMATION, "Thêm hội viên thành công!").showAndWait();
            } else {
                // Cập nhật
                // ĐÃ SỬA: Tải lại toàn bộ danh sách để lấy timestamp "updated_at" mới
                loadMembers();
                new Alert(Alert.AlertType.INFORMATION, "Cập nhật thành công!").showAndWait();
            }
            // Sau khi lưu, chuyển về chế độ XEM
            setFormEditable(false);
            deleteButton.setDisable(true);
        } else {
            new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu/cập nhật hội viên.").showAndWait();
        }
    }

    @FXML
    private void handleDelete() {
        if (!authService.canDelete()) {
            authService.showAccessDeniedAlert();
            return;
        }
        
        if (selectedMember == null || selectedMember.getId() == 0) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa hội viên " + selectedMember.getFullName() + "? (Hành động này sẽ chuyển trạng thái sang EXPIRED và ẩn đi)", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Xác nhận Xóa");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (memberService.deleteMember(selectedMember.getId())) {
                memberData.remove(selectedMember);
                membersTable.getSelectionModel().clearSelection();
                showMemberDetails(null);
                new Alert(Alert.AlertType.INFORMATION, "Xóa hội viên thành công!").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Lỗi khi xóa hội viên.").showAndWait();
            }
        }
    }

    // --- HÀM TIỆN ÍCH CHO CELL FACTORY ---

    private TableCell<Member, Void> createActionCell() {
        return new TableCell<>() {
            private final Button editButton = new Button("Chỉnh sửa");
            private final HBox pane = new HBox(5, editButton);
            {
                editButton.getStyleClass().add("icon-small-button");
                editButton.setOnAction(event -> {
                    Member member = getTableView().getItems().get(getIndex());
                    startEditMode(member);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }

    private TableCell<Member, LocalDateTime> formatDateTimeCell(TableColumn<Member, LocalDateTime> column) {
        return new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : DATE_TIME_FORMATTER.format(item));
            }
        };
    }

    private TableCell<Member, LocalDate> formatDateCell(TableColumn<Member, LocalDate> column) {
        return new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : DATE_FORMATTER.format(item));
            }
        };
    }

    private TableCell<Member, String> formatStatusCell(TableColumn<Member, String> column) {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("status-active", "status-pending", "status-inactive");

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    // Thêm style CSS dựa trên trạng thái
                    switch (item.toUpperCase()) {
                        case "ACTIVE":
                        case "RENEWED":
                            getStyleClass().add("status-active");
                            break;
                        case "PENDING":
                            getStyleClass().add("status-pending");
                            break;
                        case "EXPIRED":
                        case "PAUSED":
                            getStyleClass().add("status-inactive");
                            break;
                    }
                }
            }
        };
    }

    // --- CÁC HÀM TIỆN ÍCH FORM ---

    private void clearFormFields() {
        codeField.clear();
        fullNameField.clear();
        phoneField.clear();
        emailField.clear();
        dobPicker.setValue(null);
        addressArea.clear();
        noteArea.clear();
        genderComboBox.getSelectionModel().selectFirst();
        statusComboBox.getSelectionModel().selectFirst();
    }

    private void setFormEditable(boolean editable) {
        codeField.setEditable(editable && (selectedMember != null && selectedMember.getId() == 0));
        fullNameField.setEditable(editable);
        phoneField.setEditable(editable);
        emailField.setEditable(editable);
        dobPicker.setEditable(editable);
        addressArea.setEditable(editable);
        noteArea.setEditable(editable);
        genderComboBox.setDisable(!editable);
        statusComboBox.setDisable(!editable);
        saveButton.setDisable(!editable);
    }

    private boolean isInputValid() {
        return !(fullNameField.getText() == null || fullNameField.getText().isEmpty() ||
                codeField.getText() == null || codeField.getText().isEmpty() ||
                phoneField.getText() == null || phoneField.getText().isEmpty());
    }

    // Tìm giá trị hiển thị ("Nam (MALE)") dựa trên giá trị DB ("MALE")
    private String findComboBoxValue(ObservableList<String> list, String dbValue) {
        if (dbValue == null) return null;
        return list.stream()
                .filter(s -> s.toUpperCase().contains("(" + dbValue.toUpperCase() + ")"))
                .findFirst()
                .orElse(null);
    }

    // Trích xuất giá trị DB ("MALE") từ giá trị hiển thị ("Nam (MALE)")
    private String extractValueFromComboBox(String comboBoxValue, String defaultValue) {
        if (comboBoxValue == null) return defaultValue;
        if (comboBoxValue.contains("(")) {
            return comboBoxValue.substring(comboBoxValue.indexOf("(") + 1, comboBoxValue.indexOf(")")).toUpperCase();
        }
        return defaultValue;
    }

    @FXML
    private void handleExportExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Xuất danh sách Hội viên");
            fileChooser.setInitialFileName("DanhSachHoiVien.xlsx");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

            File file = fileChooser.showSaveDialog(membersTable.getScene().getWindow());
            if (file != null) {
                excelExportService.exportMembers(
                    memberData.stream().toList(),
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