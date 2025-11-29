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
    @FXML private Button btnSaveSecurity;
    
    private final SettingsService settingsService = new SettingsService();
    
    @FXML
    public void initialize() {
        loadSecuritySettings();
        btnSaveSecurity.setOnAction(e -> saveSecuritySettings());
    }
    
    private void loadSecuritySettings() {
        txtPasswordMinLength.setText(String.valueOf(settingsService.getPasswordMinLength()));
    }
    
    private void saveSecuritySettings() {
        Optional<Integer> passwordMinLengthOpt = parseInteger(txtPasswordMinLength.getText());
        if (passwordMinLengthOpt.isEmpty() || passwordMinLengthOpt.get() < 6 || passwordMinLengthOpt.get() > 20) {
            showWarning("⚠️ Password length must be 6-20 characters!");
            return;
        }
        
        boolean success = settingsService.setPasswordMinLength(passwordMinLengthOpt.get());
        
        if (success) {
            showAlert("✅ Security configuration saved successfully!");
        } else {
            showError("❌ Error saving security configuration!");
        }
    }
}

