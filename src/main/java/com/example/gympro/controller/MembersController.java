package com.example.gympro.controller;

import com.example.gympro.service.ExcelExportService;
import com.example.gympro.service.MemberService;
import com.example.gympro.service.AuthorizationService;
import com.example.gympro.utils.ValidationUtils;
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

    // === FXML - DETAIL FORM ===
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

    // Data for ComboBoxes
    private final ObservableList<String> statusList = FXCollections.observableArrayList(
            "All", "Pending (PENDING)", "Active (ACTIVE)", "Expired (EXPIRED)", "Paused (PAUSED)"
    );
    private final ObservableList<String> genderList = FXCollections.observableArrayList(
            "Male (MALE)", "Female (FEMALE)", "Other (OTHER)"
    );

    @FXML
    private void initialize() {
        initializeColumns();

        // Initialize ComboBox filter
        statusFilter.setItems(statusList);
        statusFilter.getSelectionModel().selectFirst();

        // Initialize ComboBoxes in Form
        genderComboBox.setItems(genderList);
        // Get status list for Form (remove "All")
        ObservableList<String> formStatusList = FXCollections.observableArrayList(statusList);
        formStatusList.remove(0); // Remove "All"
        statusComboBox.setItems(formStatusList);


        // Listener for Search and Filter
        searchField.textProperty().addListener((obs, oldV, newV) -> handleFilter());
        statusFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> handleFilter());

        // Sync member status with subscription expiration before loading
        memberService.syncMemberStatus();
        
        loadMembers();
        membersTable.setItems(memberData);

        // Setup export button
        if (exportButton != null) {
            exportButton.setOnAction(e -> handleExportExcel());
            exportButton.setDisable(false);
        }

        // Row selection listener: ONLY LOAD DATA INTO FORM (VIEW mode)
        membersTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    showMemberDetails(newValue);
                    detailPane.setDisable(newValue == null);
                }
        );

        // Set initial state: Disable form
        setFormEditable(false);
        detailPane.setDisable(true);
        deleteButton.setVisible(false);
        
        // Hide delete button if user doesn't have permission
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

        // ADDED: Map Updated column
        colUpdatedAt.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        colUpdatedAt.setCellFactory(this::formatDateTimeCell);

        colActions.setCellFactory(col -> createActionCell());
    }

    // Filter and Search handler
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

    // Load/Refresh data
    @FXML
    public void loadMembers() {
        handleFilter();
    }

    /**
     * Load data from Model into Form and set VIEW mode (Read-only).
     */
    private void showMemberDetails(Member member) {
        if (member != null) {
            selectedMember = member;
            formTitle.setText("Member Details: " + member.getFullName());

            // Load data
            codeField.setText(member.getMemberCode());
            fullNameField.setText(member.getFullName());
            phoneField.setText(member.getPhone());
            emailField.setText(member.getEmail());
            dobPicker.setValue(member.getDob());
            addressArea.setText(member.getAddress());
            noteArea.setText(member.getNote());

            // Handle ComboBox (find correct value based on DB)
            genderComboBox.setValue(findComboBoxValue(genderList, member.getGender()));
            statusComboBox.setValue(findComboBoxValue(statusList, member.getStatus()));

            // Set VIEW mode (Read-only) by default
            setFormEditable(false);
            deleteButton.setVisible(true);
            deleteButton.setDisable(true);
            saveButton.setText("💾 Save Changes");

        } else {
            // Case when nothing is selected
            selectedMember = null;
            formTitle.setText("Member Details");
            setFormEditable(false);
            clearFormFields();
            deleteButton.setVisible(false);
        }
    }

    /**
     * Switch Form to Edit mode. Called by "✏️" button.
     */
    private void startEditMode(Member member) {
        if (member == null) return;

        membersTable.getSelectionModel().select(member);
        formTitle.setText("EDIT MEMBER: " + member.getFullName());

        setFormEditable(true);
        deleteButton.setDisable(false);
        saveButton.setText("💾 Save Changes");

        detailPane.requestFocus();
    }

    // --- FORM OPERATION METHODS ---

    @FXML
    private void handleNewMember() {
        membersTable.getSelectionModel().clearSelection();
        selectedMember = new Member();
        formTitle.setText("➕ Add New Member");
        setFormEditable(true);
        clearFormFields();
        detailPane.setDisable(false);
        deleteButton.setVisible(false);
        saveButton.setText("💾 Add Member");
        
        // Auto-generate member code preview (will be generated on save)
        codeField.setText("(Auto-generated)");
        codeField.setEditable(false);

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
            new Alert(Alert.AlertType.WARNING, "Please check your input. Name and Phone cannot be empty.").showAndWait();
            return;
        }

        // 1. Apply data from Form to Model
        // For new members, member code will be auto-generated by service
        // For existing members, keep the existing code
        if (selectedMember.getId() != 0 && !codeField.getText().isEmpty() && !codeField.getText().equals("(Auto-generated)")) {
            selectedMember.setMemberCode(codeField.getText());
        } else {
            // Clear member code for new members - service will generate it
            selectedMember.setMemberCode(null);
        }
        selectedMember.setFullName(fullNameField.getText());
        selectedMember.setPhone(phoneField.getText());
        selectedMember.setEmail(emailField.getText());
        selectedMember.setDob(dobPicker.getValue());
        selectedMember.setAddress(addressArea.getText());
        selectedMember.setNote(noteArea.getText());

        // Get ENUM value from ComboBox
        selectedMember.setGender(extractValueFromComboBox(genderComboBox.getValue(), "OTHER"));
        selectedMember.setStatus(extractValueFromComboBox(statusComboBox.getValue(), "PENDING"));

        // 2. Save to Database (CALL SERVICE)
        Optional<Member> savedMember = memberService.saveMember(selectedMember);

        if (savedMember.isPresent()) {
            if (selectedMember.getId() == 0) {
                // Add new - reload to get generated member code
                loadMembers(); // Reload to get new ID, member code, and timestamp
                // Find and select the newly created member
                savedMember.ifPresent(m -> {
                    membersTable.getItems().stream()
                        .filter(mem -> mem.getFullName().equals(m.getFullName()) && 
                                      mem.getPhone().equals(m.getPhone()))
                        .findFirst()
                        .ifPresent(membersTable.getSelectionModel()::select);
                });
                new Alert(Alert.AlertType.INFORMATION, "Member added successfully with code: " + savedMember.get().getMemberCode()).showAndWait();
            } else {
                // Update
                // FIXED: Reload entire list to get new "updated_at" timestamp
                loadMembers();
                new Alert(Alert.AlertType.INFORMATION, "Update successful!").showAndWait();
            }
            // After saving, switch back to VIEW mode
            setFormEditable(false);
            deleteButton.setDisable(true);
        } else {
            new Alert(Alert.AlertType.ERROR, "Error saving/updating member.").showAndWait();
        }
    }

    @FXML
    private void handleDelete() {
        if (!authService.canDelete()) {
            authService.showAccessDeniedAlert();
            return;
        }
        
        if (selectedMember == null || selectedMember.getId() == 0) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete member " + selectedMember.getFullName() + "? (This action will change status to EXPIRED and hide)", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (memberService.deleteMember(selectedMember.getId())) {
                memberData.remove(selectedMember);
                membersTable.getSelectionModel().clearSelection();
                showMemberDetails(null);
                new Alert(Alert.AlertType.INFORMATION, "Member deleted successfully!").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Error deleting member.").showAndWait();
            }
        }
    }

    // --- UTILITY METHODS FOR CELL FACTORY ---

    private TableCell<Member, Void> createActionCell() {
        return new TableCell<>() {
            private final Button editButton = new Button("Edit");
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
                    // Add CSS style based on status
                    switch (item.toUpperCase()) {
                        case "ACTIVE":
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

    // --- FORM UTILITY METHODS ---

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
        // Member code is always read-only (auto-generated for new, display-only for existing)
        codeField.setEditable(false);
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
        // Validate Full Name
        String fullName = fullNameField.getText();
        if (!ValidationUtils.isValidName(fullName)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                "Full Name is required and must be 2-100 characters.");
            return false;
        }
        
        // Validate Phone
        String phone = phoneField.getText();
        if (!ValidationUtils.isValidPhone(phone)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                "Phone number is required and must be in valid format (e.g., 0123456789 or +84123456789).");
            return false;
        }
        
        // Validate Email (optional but must be valid if provided)
        String email = emailField.getText();
        if (email != null && !email.trim().isEmpty()) {
            if (!ValidationUtils.isValidEmail(email)) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", 
                    "Email format is invalid. Please enter a valid email address.");
                return false;
            }
        }
        
        // Validate Date of Birth (optional but must be valid if provided)
        LocalDate dob = dobPicker.getValue();
        if (dob != null) {
            if (!ValidationUtils.isValidDateOfBirth(dob)) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", 
                    "Date of Birth cannot be in the future and must be reasonable.");
                return false;
            }
        }
        
        return true;
    }

    // Find display value ("Male (MALE)") based on DB value ("MALE")
    private String findComboBoxValue(ObservableList<String> list, String dbValue) {
        if (dbValue == null) return null;
        return list.stream()
                .filter(s -> s.toUpperCase().contains("(" + dbValue.toUpperCase() + ")"))
                .findFirst()
                .orElse(null);
    }

    // Extract DB value ("MALE") from display value ("Male (MALE)")
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
            fileChooser.setTitle("Export Member List");
            fileChooser.setInitialFileName("MemberList.xlsx");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

            File file = fileChooser.showSaveDialog(membersTable.getScene().getWindow());
            if (file != null) {
                excelExportService.exportMembers(
                    memberData.stream().toList(),
                    file.getAbsolutePath()
                );
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                    "✅ Excel export successful: " + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", 
                "❌ Error exporting Excel: " + ex.getMessage());
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