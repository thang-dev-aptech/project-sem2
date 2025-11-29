package com.example.gympro.controller;

import com.example.gympro.controller.base.BaseController;
import com.example.gympro.service.AuthorizationService;
import com.example.gympro.service.settings.SettingsService;
import com.example.gympro.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Properties;

/**
 * Controller for Settings screen
 * Uses BaseController for common methods
 */
public class SettingsController extends BaseController {

    @FXML private TabPane tabPane;

    // Tab 1: Gym information
    @FXML private TextField txtGymName;
    @FXML private TextField txtGymAddress;
    @FXML private TextField txtGymPhone;
    @FXML private Button btnSaveBranch;

    // Tab 2: Business configuration
    @FXML private TextField txtMemberCodePrefix;
    @FXML private TextField txtInvoicePrefix;
    @FXML private Button btnSaveBusiness;

    // Tab 3: Security
    @FXML private TextField txtPasswordMinLength;
    @FXML private Button btnSaveSecurity;

    // Tab 4: System
    @FXML private Label lblAppVersion;
    @FXML private Label lblDbVersion;
    @FXML private Label lblDbInfo;

    private final SettingsService settingsService = new SettingsService();
    private final AuthorizationService authService = new AuthorizationService();

    @FXML
    private void initialize() {
        // Check access permission
        if (!authService.canManageSettings()) {
            authService.showAccessDeniedAlert();
            tabPane.setDisable(true);
            return;
        }

        // Load data for all tabs
        loadBranchInfo();
        loadBusinessSettings();
        loadSecuritySettings();
        loadSystemInfo();

        // Setup event handlers
        btnSaveBranch.setOnAction(e -> saveBranchInfo());
        btnSaveBusiness.setOnAction(e -> saveBusinessSettings());
        btnSaveSecurity.setOnAction(e -> saveSecuritySettings());
    }

    // ========== Tab 1: Gym information ==========
    private void loadBranchInfo() {
        com.example.gympro.repository.settings.SettingsRepository.BranchInfo branch = settingsService.getBranchInfo();
        if (branch != null) {
            txtGymName.setText(branch.getName());
            txtGymAddress.setText(branch.getAddress());
            txtGymPhone.setText(branch.getPhone());
        }
    }

    private void saveBranchInfo() {
        String name = txtGymName.getText().trim();
        String address = txtGymAddress.getText().trim();
        String phone = txtGymPhone.getText().trim();

        if (name.isEmpty()) {
            showAlert("⚠️ Please enter gym name!");
            return;
        }

        if (settingsService.updateBranchInfo(name, address, phone)) {
            showAlert("✅ Gym information saved successfully!");
        } else {
            showAlert("❌ Error saving gym information!");
        }
    }

    // ========== Tab 2: Business configuration ==========
    private void loadBusinessSettings() {
        txtMemberCodePrefix.setText(settingsService.getMemberCodePrefix());
        txtInvoicePrefix.setText(settingsService.getInvoicePrefix());
    }

    private void saveBusinessSettings() {
        String memberPrefix = txtMemberCodePrefix.getText().trim();
        String invoicePrefix = txtInvoicePrefix.getText().trim();

        // Validate Member Code Prefix
        if (memberPrefix.isEmpty()) {
            showAlert("⚠️ Member Code Prefix cannot be empty!");
            return;
        }
        if (!ValidationUtils.isValidCode(memberPrefix)) {
            showAlert("⚠️ Member Code Prefix must be 2-50 alphanumeric characters (may include dash/underscore).");
            return;
        }
        
        // Validate Invoice Prefix
        if (invoicePrefix.isEmpty()) {
            showAlert("⚠️ Invoice Prefix cannot be empty!");
            return;
        }
        if (!ValidationUtils.isValidCode(invoicePrefix)) {
            showAlert("⚠️ Invoice Prefix must be 2-50 alphanumeric characters (may include dash/underscore).");
            return;
        }

        // Save prefixes with detailed result (includes migration info)
        SettingsService.PrefixUpdateResult result = settingsService.setPrefixesWithResult(memberPrefix, invoicePrefix);

        if (result.isSuccess()) {
            showAlert(result.getMessage());
        } else {
            showAlert(result.getMessage());
        }
    }

    // ========== Tab 3: Security ==========
    private void loadSecuritySettings() {
        txtPasswordMinLength.setText(String.valueOf(settingsService.getPasswordMinLength()));
    }

    private void saveSecuritySettings() {
        try {
            int passwordMinLength = Integer.parseInt(txtPasswordMinLength.getText().trim());

            if (passwordMinLength < 6 || passwordMinLength > 20) {
                showAlert("⚠️ Password length must be 6-20 characters!");
                return;
            }

            boolean success = settingsService.setPasswordMinLength(passwordMinLength);

            if (success) {
                showAlert("✅ Security configuration saved successfully!");
            } else {
                showAlert("❌ Error saving security configuration!");
            }
        } catch (NumberFormatException e) {
            showAlert("⚠️ Please enter a valid number!");
        }
    }

    // ========== Tab 4: System ==========
    private void loadSystemInfo() {
        // Load app version from pom.xml
        try {
            Properties prop = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties");
            if (input != null) {
                prop.load(input);
                String version = prop.getProperty("app.version", "1.0.0");
                lblAppVersion.setText(version);
            } else {
                lblAppVersion.setText("1.0.0");
            }
        } catch (Exception e) {
            lblAppVersion.setText("1.0.0");
        }
        
        // Load database info
        try {
            Connection conn = com.example.gympro.utils.DatabaseConnection.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            String dbVersion = metaData.getDatabaseProductVersion();
            String dbUrl = metaData.getURL();
            
            lblDbVersion.setText("MySQL " + dbVersion);
            lblDbInfo.setText(dbUrl);
            conn.close();
        } catch (Exception e) {
            lblDbVersion.setText("MySQL 8.0");
            lblDbInfo.setText("gympro@localhost:3306");
        }
    }

    // Helper methods inherited from BaseController
}
