package com.example.gympro.controller;

import com.example.gympro.controller.base.BaseController;
import com.example.gympro.service.AuthorizationService;
import com.example.gympro.service.settings.SettingsService;
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
    @FXML private TextField txtGymEmail;
    @FXML private Button btnSaveBranch;

    // Tab 2: Business configuration
    @FXML private TextField txtGraceDays;
    @FXML private TextField txtMemberCodePrefix;
    @FXML private TextField txtInvoicePrefix;
    @FXML private TextField txtCurrencySymbol;
    @FXML private Button btnSaveBusiness;

    // Tab 3: Security
    @FXML private TextField txtPasswordMinLength;
    @FXML private TextField txtSessionTimeout;
    @FXML private TextField txtMaxLoginAttempts;
    @FXML private TextField txtLockoutDuration;
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
        txtGraceDays.setText(String.valueOf(settingsService.getGraceDays()));
        txtMemberCodePrefix.setText(settingsService.getMemberCodePrefix());
        txtInvoicePrefix.setText(settingsService.getInvoicePrefix());
        txtCurrencySymbol.setText(settingsService.getCurrencySymbol());
    }

    private void saveBusinessSettings() {
        try {
            int graceDays = Integer.parseInt(txtGraceDays.getText().trim());
            String memberPrefix = txtMemberCodePrefix.getText().trim();
            String invoicePrefix = txtInvoicePrefix.getText().trim();
            String currencySymbol = txtCurrencySymbol.getText().trim();

            if (graceDays < 0) {
                showAlert("⚠️ Days must be >= 0!");
                return;
            }

            if (memberPrefix.isEmpty() || invoicePrefix.isEmpty() || currencySymbol.isEmpty()) {
                showAlert("⚠️ Please fill in all information!");
                return;
            }

            boolean success = settingsService.setGraceDays(graceDays)
                    && settingsService.setMemberCodePrefix(memberPrefix)
                    && settingsService.setInvoicePrefix(invoicePrefix)
                    && settingsService.setCurrencySymbol(currencySymbol);

            if (success) {
                showAlert("✅ Business configuration saved successfully!");
            } else {
                showAlert("❌ Error saving business configuration!");
            }
        } catch (NumberFormatException e) {
            showAlert("⚠️ Please enter a valid number!");
        }
    }

    // ========== Tab 3: Security ==========
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
                showAlert("⚠️ Password length must be 6-20 characters!");
                return;
            }

            if (sessionTimeout < 5 || sessionTimeout > 120) {
                showAlert("⚠️ Timeout must be 5-120 minutes!");
                return;
            }

            boolean success = settingsService.setPasswordMinLength(passwordMinLength)
                    && settingsService.setSessionTimeout(sessionTimeout)
                    && settingsService.setMaxLoginAttempts(maxLoginAttempts)
                    && settingsService.setLockoutDuration(lockoutDuration);

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
