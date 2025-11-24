package com.example.gympro.controller.settings;

import com.example.gympro.controller.base.BaseController;
import com.example.gympro.service.settings.SettingsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.Optional;

/**
 * Controller for "Security" tab in Settings
 */
public class SecuritySettingsTabController extends BaseController {
    
    @FXML private TextField txtPasswordMinLength;
    @FXML private TextField txtSessionTimeout;
    @FXML private TextField txtMaxLoginAttempts;
    @FXML private TextField txtLockoutDuration;
    @FXML private Button btnSaveSecurity;
    
    private final SettingsService settingsService = new SettingsService();
    
    @FXML
    public void initialize() {
        loadSecuritySettings();
        btnSaveSecurity.setOnAction(e -> saveSecuritySettings());
    }
    
    private void loadSecuritySettings() {
        txtPasswordMinLength.setText(String.valueOf(settingsService.getPasswordMinLength()));
        txtSessionTimeout.setText(String.valueOf(settingsService.getSessionTimeout()));
        txtMaxLoginAttempts.setText(String.valueOf(settingsService.getMaxLoginAttempts()));
        txtLockoutDuration.setText(String.valueOf(settingsService.getLockoutDuration()));
    }
    
    private void saveSecuritySettings() {
        Optional<Integer> passwordMinLengthOpt = parseInteger(txtPasswordMinLength.getText());
        if (passwordMinLengthOpt.isEmpty() || passwordMinLengthOpt.get() < 6 || passwordMinLengthOpt.get() > 20) {
            showWarning("⚠️ Password length must be 6-20 characters!");
            return;
        }
        
        Optional<Integer> sessionTimeoutOpt = parseInteger(txtSessionTimeout.getText());
        if (sessionTimeoutOpt.isEmpty() || sessionTimeoutOpt.get() < 5 || sessionTimeoutOpt.get() > 120) {
            showWarning("⚠️ Timeout must be 5-120 minutes!");
            return;
        }
        
        Optional<Integer> maxLoginAttemptsOpt = parseInteger(txtMaxLoginAttempts.getText());
        Optional<Integer> lockoutDurationOpt = parseInteger(txtLockoutDuration.getText());
        
        if (maxLoginAttemptsOpt.isEmpty() || lockoutDurationOpt.isEmpty()) {
            showWarning("⚠️ Please enter a valid number!");
            return;
        }
        
        boolean success = settingsService.setPasswordMinLength(passwordMinLengthOpt.get())
                && settingsService.setSessionTimeout(sessionTimeoutOpt.get())
                && settingsService.setMaxLoginAttempts(maxLoginAttemptsOpt.get())
                && settingsService.setLockoutDuration(lockoutDurationOpt.get());
        
        if (success) {
            showAlert("✅ Security configuration saved successfully!");
        } else {
            showError("❌ Error saving security configuration!");
        }
    }
}

