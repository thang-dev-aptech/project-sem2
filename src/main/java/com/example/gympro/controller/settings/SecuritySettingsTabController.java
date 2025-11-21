package com.example.gympro.controller.settings;

import com.example.gympro.controller.base.BaseController;
import com.example.gympro.service.settings.SettingsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.Optional;

/**
 * Controller cho Tab "Bảo mật" trong Settings
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
            showWarning("⚠️ Độ dài mật khẩu phải từ 6-20 ký tự!");
            return;
        }
        
        Optional<Integer> sessionTimeoutOpt = parseInteger(txtSessionTimeout.getText());
        if (sessionTimeoutOpt.isEmpty() || sessionTimeoutOpt.get() < 5 || sessionTimeoutOpt.get() > 120) {
            showWarning("⚠️ Thời gian timeout phải từ 5-120 phút!");
            return;
        }
        
        Optional<Integer> maxLoginAttemptsOpt = parseInteger(txtMaxLoginAttempts.getText());
        Optional<Integer> lockoutDurationOpt = parseInteger(txtLockoutDuration.getText());
        
        if (maxLoginAttemptsOpt.isEmpty() || lockoutDurationOpt.isEmpty()) {
            showWarning("⚠️ Vui lòng nhập số hợp lệ!");
            return;
        }
        
        boolean success = settingsService.setPasswordMinLength(passwordMinLengthOpt.get())
                && settingsService.setSessionTimeout(sessionTimeoutOpt.get())
                && settingsService.setMaxLoginAttempts(maxLoginAttemptsOpt.get())
                && settingsService.setLockoutDuration(lockoutDurationOpt.get());
        
        if (success) {
            showAlert("✅ Lưu cấu hình bảo mật thành công!");
        } else {
            showError("❌ Lỗi khi lưu cấu hình bảo mật!");
        }
    }
}

