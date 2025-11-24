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

    // === FXML - DETAIL FORM ===
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

        // Initialize ComboBox filter
        statusFilter.setItems(FXCollections.observableArrayList(
                "All",
                "Active",
                "Inactive"
        ));
        statusFilter.getSelectionModel().selectFirst();

        // Listener for Search and Filter
        searchField.textProperty().addListener((obs, oldV, newV) -> handleFilter());
        statusFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> handleFilter());

        loadPackages();
        packagesTable.setItems(packageData);

        // Setup export button
        if (exportButton != null) {
            exportButton.setOnAction(e -> handleExportExcel());
            exportButton.setDisable(false);
        }

        // Row selection listener: ONLY LOAD DATA INTO FORM, DO NOT ACTIVATE EDIT MODE
        packagesTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    showPackageDetails(newValue);
                    detailPane.setDisable(newValue == null);
                }
        );

        // Set initial state: Disable form
        setFormEditable(false);
        detailPane.setDisable(true);
        deleteButton.setVisible(false);
        
        // Only OWNER can manage packages
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

    // Filter and Search handler
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

    // Load/Refresh data
    @FXML
    public void loadPackages() {
        handleFilter();
    }

    /**
     * Load data from Model into Form and set VIEW mode (Read-only).
     */
    private void showPackageDetails(Package pkg) {
        if (pkg != null) {
            selectedPackage = pkg;
            formTitle.setText("Package Details: " + pkg.getName());

            // Load data
            codeField.setText(pkg.getCode());
            nameField.setText(pkg.getName());
            priceField.setText(pkg.getPrice().toString());
            durationField.setText(String.valueOf(pkg.getDurationDays()));
            descriptionArea.setText(pkg.getDescription());
            isActiveCheckbox.setSelected(pkg.isActive());

            // Set VIEW mode (Read-only) by default
            setFormEditable(false);
            deleteButton.setVisible(true);
            deleteButton.setDisable(true); // Disable delete button in view mode
            saveButton.setText("💾 Save Changes");

        } else {
            // Case when nothing is selected (after delete, cancel)
            selectedPackage = null;
            formTitle.setText("Package Details");
            setFormEditable(false);
            clearFormFields();
            deleteButton.setVisible(false);
        }
    }

    /**
     * Switch Form to Edit mode. Called by "✏️" button.
     */
    private void startEditMode(Package pkg) {
        if (pkg == null) return;

        packagesTable.getSelectionModel().select(pkg); // Ensure row is selected
        formTitle.setText("EDIT PACKAGE: " + pkg.getName());

        setFormEditable(true); // ENABLE EDIT MODE
        deleteButton.setDisable(false); // Enable delete button
        saveButton.setText("💾 Save Changes");
    }


    // --- FORM OPERATION METHODS ---

    @FXML
    private void handleNewPackage() {
        if (!authService.canManagePackages()) {
            authService.showAccessDeniedAlert();
            return;
        }
        
        packagesTable.getSelectionModel().clearSelection();
        selectedPackage = new Package();
        formTitle.setText("➕ Add New Package");
        setFormEditable(true); // Add mode must be editable
        clearFormFields();
        detailPane.setDisable(false);
        deleteButton.setVisible(false);
        saveButton.setText("💾 Add Package");
    }

    @FXML
    private void handleCancel() {
        // Return to unselected/disabled state
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
            new Alert(Alert.AlertType.WARNING, "Please check your input. Name, Code, Price and Duration cannot be empty/invalid format.").showAndWait();
            return;
        }

        // 1. Apply data from Form to Model
        selectedPackage.setCode(codeField.getText());
        selectedPackage.setName(nameField.getText());
        selectedPackage.setDescription(descriptionArea.getText());
        selectedPackage.setPrice(new BigDecimal(priceField.getText()));
        selectedPackage.setDurationDays(Integer.parseInt(durationField.getText()));
        selectedPackage.setIsActive(isActiveCheckbox.isSelected());

        // 2. Save to Database (CALL SERVICE)
        Optional<Package> savedPkg = packageService.savePackage(selectedPackage);

        if (savedPkg.isPresent()) {
            if (selectedPackage.getId() == 0) {
                packageData.add(savedPkg.get());
                packagesTable.getSelectionModel().select(savedPkg.get());
                new Alert(Alert.AlertType.INFORMATION, "Package added successfully!").showAndWait();
            } else {
                packagesTable.refresh();
                new Alert(Alert.AlertType.INFORMATION, "Update successful!").showAndWait();
            }
            // After saving, switch back to VIEW mode
            setFormEditable(false);
            deleteButton.setDisable(true);
        } else {
            new Alert(Alert.AlertType.ERROR, "Error saving/updating package to Database.").showAndWait();
        }
    }

    @FXML
    private void handleDelete() {
        if (!authService.canManagePackages()) {
            authService.showAccessDeniedAlert();
            return;
        }
        
        if (selectedPackage == null || selectedPackage.getId() == 0) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete package " + selectedPackage.getName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (packageService.deletePackage(selectedPackage.getId())) {
                packageData.remove(selectedPackage);
                packagesTable.getSelectionModel().clearSelection();
                showPackageDetails(null);
                new Alert(Alert.AlertType.INFORMATION, "Package deleted successfully!").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Error deleting package. (Check business rules/foreign keys)").showAndWait();
            }
        }
    }

    // --- UTILITY METHODS FOR CELL FACTORY ---

    private TableCell<Package, Void> createActionCell() {
        return new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final HBox pane = new HBox(5, editButton);

            {
                editButton.getStyleClass().add("icon-small-button");
                // When clicking EDIT button, call startEditMode
                editButton.setOnAction(event -> {
                    Package pkg = getTableView().getItems().get(getIndex());
                    startEditMode(pkg); // <--- ACTIVATE EDIT MODE
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
                    setText(item ? "🟢 Active" : "🔴 Inactive");
                    getStyleClass().add(item ? "status-active" : "status-inactive");
                }
            }
        };
    }

    // --- FORM UTILITY METHODS ---

    private void clearFormFields() {
        codeField.clear();
        nameField.clear();
        priceField.clear();
        durationField.clear();
        descriptionArea.clear();
        isActiveCheckbox.setSelected(true);
    }

    /**
     * Control the editability of Form fields and Save button.
     */
    private void setFormEditable(boolean editable) {
        // Allow editing Code only when Adding new (ID=0)
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
            fileChooser.setTitle("Export Package List");
            fileChooser.setInitialFileName("PackageList.xlsx");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

            File file = fileChooser.showSaveDialog(packagesTable.getScene().getWindow());
            if (file != null) {
                excelExportService.exportPackages(
                    packageData.stream().toList(),
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