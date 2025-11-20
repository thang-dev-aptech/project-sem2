package com.example.gympro.controller;

import com.example.gympro.domain.Role;
import com.example.gympro.service.AuthorizationService;
import com.example.gympro.service.UserService;
import com.example.gympro.viewModel.UserViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserManagementController {

    // === FXML - TABLEVIEW ===
    @FXML private TableView<UserViewModel> usersTable;
    @FXML private TableColumn<UserViewModel, String> colUsername;
    @FXML private TableColumn<UserViewModel, String> colFullName;
    @FXML private TableColumn<UserViewModel, String> colEmail;
    @FXML private TableColumn<UserViewModel, String> colPhone;
    @FXML private TableColumn<UserViewModel, String> colRoles;
    @FXML private TableColumn<UserViewModel, Boolean> colStatus;
    @FXML private TableColumn<UserViewModel, LocalDateTime> colLastLogin;
    @FXML private TableColumn<UserViewModel, Void> colActions;

    // === FXML - TOOLBAR & FILTER ===
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    // === FXML - FORM CHI TIẾT ===
    @FXML private Label formTitle;
    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private CheckBox roleOwnerCheckBox;
    @FXML private CheckBox roleStaffCheckBox;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private VBox detailPane;

    // === SERVICE & DATA ===
    private final UserService userService = new UserService();
    private final AuthorizationService authService = new AuthorizationService();
    private final ObservableList<UserViewModel> userData = FXCollections.observableArrayList();
    private UserViewModel selectedUser = null;
    private List<Role> allRoles = new ArrayList<>();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Dữ liệu cho ComboBoxes
    private final ObservableList<String> statusList = FXCollections.observableArrayList(
            "Tất cả", "Hoạt động", "Vô hiệu hóa"
    );

    @FXML
    private void initialize() {
        // Kiểm tra quyền
        if (!authService.isOwner()) {
            authService.showAccessDeniedAlert();
            return;
        }

        initializeColumns();
        loadAllRoles();

        // Khởi tạo ComboBox filter
        statusFilter.setItems(statusList);
        statusFilter.getSelectionModel().selectFirst();

        // Khởi tạo ComboBox status trong Form
        ObservableList<String> formStatusList = FXCollections.observableArrayList("Hoạt động", "Vô hiệu hóa");
        statusComboBox.setItems(formStatusList);

        // Listener cho Tìm kiếm và Lọc
        searchField.textProperty().addListener((obs, oldV, newV) -> handleFilter());
        statusFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> handleFilter());

        loadUsers();
        usersTable.setItems(userData);

        // Listener chọn hàng
        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    showUserDetails(newValue);
                    detailPane.setDisable(newValue == null);
                }
        );

        // Thiết lập trạng thái ban đầu
        setFormEditable(false);
        detailPane.setDisable(true);
        deleteButton.setVisible(false);
    }

    private void loadAllRoles() {
        allRoles = userService.getAllRoles();
    }

    private void initializeColumns() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colRoles.setCellValueFactory(new PropertyValueFactory<>("rolesDisplay"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("isActive"));
        colStatus.setCellFactory(this::formatStatusCell);
        colLastLogin.setCellValueFactory(new PropertyValueFactory<>("lastLoginAt"));
        colLastLogin.setCellFactory(this::formatDateTimeCell);
        colActions.setCellFactory(col -> createActionCell());
    }

    private void handleFilter() {
        String searchTerm = searchField.getText();
        String status = statusFilter.getSelectionModel().getSelectedItem();

        userData.clear();
        List<UserViewModel> allUsers = userService.getAllUsers();

        // Lọc theo search term
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String lowerSearch = searchTerm.toLowerCase();
            allUsers = allUsers.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(lowerSearch)
                            || u.getFullName().toLowerCase().contains(lowerSearch)
                            || (u.getEmail() != null && u.getEmail().toLowerCase().contains(lowerSearch)))
                    .collect(Collectors.toList());
        }

        // Lọc theo status
        if (status != null && !"Tất cả".equals(status)) {
            boolean isActive = "Hoạt động".equals(status);
            allUsers = allUsers.stream()
                    .filter(u -> u.isActive() == isActive)
                    .collect(Collectors.toList());
        }

        userData.addAll(allUsers);
        usersTable.getSelectionModel().clearSelection();
        showUserDetails(null);
        detailPane.setDisable(true);
    }

    @FXML
    public void loadUsers() {
        handleFilter();
    }

    private void showUserDetails(UserViewModel user) {
        if (user != null) {
            selectedUser = user;
            formTitle.setText("Chi tiết User: " + user.getFullName());

            // Load data
            usernameField.setText(user.getUsername());
            fullNameField.setText(user.getFullName());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhone());
            passwordField.clear(); // Không hiển thị password
            statusComboBox.setValue(user.isActive() ? "Hoạt động" : "Vô hiệu hóa");

            // Load roles
            List<Role> userRoles = userService.getUserRoles(user.getId());
            roleOwnerCheckBox.setSelected(userRoles.stream().anyMatch(r -> "OWNER".equals(r.getName())));
            roleStaffCheckBox.setSelected(userRoles.stream().anyMatch(r -> "STAFF".equals(r.getName())));

            // Thiết lập chế độ XEM
            setFormEditable(false);
            deleteButton.setVisible(true);
            saveButton.setText("💾 Lưu Thay đổi");
        } else {
            selectedUser = null;
            formTitle.setText("Chi tiết Người dùng");
            setFormEditable(false);
            clearFormFields();
            deleteButton.setVisible(false);
        }
    }

    private void startEditMode(UserViewModel user) {
        if (user == null) return;

        usersTable.getSelectionModel().select(user);
        formTitle.setText("CHỈNH SỬA USER: " + user.getFullName());
        setFormEditable(true);
        deleteButton.setDisable(false);
        saveButton.setText("💾 Lưu Thay đổi");
    }

    @FXML
    private void handleNewUser() {
        usersTable.getSelectionModel().clearSelection();
        selectedUser = new UserViewModel();
        formTitle.setText("➕ Thêm User Mới");
        setFormEditable(true);
        clearFormFields();
        detailPane.setDisable(false);
        deleteButton.setVisible(false);
        saveButton.setText("💾 Thêm User");
        usernameField.setEditable(true);
    }

    @FXML
    private void handleCancel() {
        usersTable.getSelectionModel().clearSelection();
        showUserDetails(null);
        detailPane.setDisable(true);
    }

    @FXML
    private void handleSave() {
        if (!isInputValid()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng kiểm tra lại dữ liệu nhập. Username, Họ tên không được trống.").showAndWait();
            return;
        }

        // Áp dụng dữ liệu từ Form vào Model
        if (selectedUser == null) {
            selectedUser = new UserViewModel();
        }

        selectedUser.setUsername(usernameField.getText());
        selectedUser.setFullName(fullNameField.getText());
        selectedUser.setEmail(emailField.getText());
        selectedUser.setPhone(phoneField.getText());
        selectedUser.setIsActive("Hoạt động".equals(statusComboBox.getValue()));

        // Password chỉ set khi có giá trị
        String password = passwordField.getText();
        if (password != null && !password.trim().isEmpty()) {
            selectedUser.setPassword(password);
        }

        // Lấy role IDs
        List<Long> roleIds = new ArrayList<>();
        if (roleOwnerCheckBox.isSelected()) {
            allRoles.stream().filter(r -> "OWNER".equals(r.getName())).findFirst()
                    .ifPresent(r -> roleIds.add(r.getId()));
        }
        if (roleStaffCheckBox.isSelected()) {
            allRoles.stream().filter(r -> "STAFF".equals(r.getName())).findFirst()
                    .ifPresent(r -> roleIds.add(r.getId()));
        }

        // Lưu vào Database
        Optional<UserViewModel> savedUser = userService.saveUser(selectedUser, roleIds);

        if (savedUser.isPresent()) {
            if (selectedUser.getId() == 0) {
                userData.add(savedUser.get());
                usersTable.getSelectionModel().select(savedUser.get());
                new Alert(Alert.AlertType.INFORMATION, "Thêm user thành công!").showAndWait();
            } else {
                usersTable.refresh();
                new Alert(Alert.AlertType.INFORMATION, "Cập nhật thành công!").showAndWait();
            }
            setFormEditable(false);
            deleteButton.setDisable(true);
            usernameField.setEditable(false);
        } else {
            new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu/cập nhật user.").showAndWait();
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedUser == null || selectedUser.getId() == 0) return;

        // Không cho phép xóa chính mình
        com.example.gympro.domain.User currentUser = com.example.gympro.service.SessionManager.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getId().equals(selectedUser.getId())) {
            new Alert(Alert.AlertType.WARNING, "Bạn không thể xóa chính mình!").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc chắn muốn xóa user " + selectedUser.getFullName() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Xác nhận Xóa");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (userService.deleteUser(selectedUser.getId())) {
                userData.remove(selectedUser);
                usersTable.getSelectionModel().clearSelection();
                showUserDetails(null);
                new Alert(Alert.AlertType.INFORMATION, "Xóa user thành công!").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Lỗi khi xóa user.").showAndWait();
            }
        }
    }

    private boolean isInputValid() {
        return usernameField.getText() != null && !usernameField.getText().trim().isEmpty()
                && fullNameField.getText() != null && !fullNameField.getText().trim().isEmpty()
                && (selectedUser.getId() == 0 || passwordField.getText().trim().isEmpty() || passwordField.getText().length() >= 6);
    }

    private void setFormEditable(boolean editable) {
        usernameField.setEditable(editable && (selectedUser == null || selectedUser.getId() == 0));
        fullNameField.setEditable(editable);
        emailField.setEditable(editable);
        phoneField.setEditable(editable);
        passwordField.setEditable(editable);
        statusComboBox.setDisable(!editable);
        roleOwnerCheckBox.setDisable(!editable);
        roleStaffCheckBox.setDisable(!editable);
    }

    private void clearFormFields() {
        usernameField.clear();
        fullNameField.clear();
        emailField.clear();
        phoneField.clear();
        passwordField.clear();
        statusComboBox.getSelectionModel().selectFirst();
        roleOwnerCheckBox.setSelected(false);
        roleStaffCheckBox.setSelected(false);
    }

    private TableCell<UserViewModel, Void> createActionCell() {
        return new TableCell<>() {
            private final Button editButton = new Button("Chỉnh sửa");
            private final HBox pane = new HBox(5, editButton);
            {
                editButton.getStyleClass().add("icon-small-button");
                editButton.setOnAction(event -> {
                    UserViewModel user = getTableView().getItems().get(getIndex());
                    startEditMode(user);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }

    private TableCell<UserViewModel, Boolean> formatStatusCell(TableColumn<UserViewModel, Boolean> column) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("status-active", "status-inactive");
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "🟢 Hoạt động" : "🔴 Vô hiệu hóa");
                    getStyleClass().add(item ? "status-active" : "status-inactive");
                }
            }
        };
    }

    private TableCell<UserViewModel, LocalDateTime> formatDateTimeCell(TableColumn<UserViewModel, LocalDateTime> column) {
        return new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Chưa đăng nhập" : DATE_TIME_FORMATTER.format(item));
            }
        };
    }
}
