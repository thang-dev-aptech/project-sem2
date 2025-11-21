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
 * Controller cho màn hình Settings
 * Sử dụng BaseController cho common methods
 */
public class SettingsController extends BaseController {

    @FXML private TabPane tabPane;

    // Tab 1: Thông tin phòng gym
    @FXML private TextField txtGymName;
    @FXML private TextField txtGymAddress;
    @FXML private TextField txtGymPhone;
    @FXML private TextField txtGymEmail;
    @FXML private Button btnSaveBranch;

    // Tab 2: Cấu hình nghiệp vụ
    @FXML private TextField txtGraceDays;
    @FXML private TextField txtMemberCodePrefix;
    @FXML private TextField txtInvoicePrefix;
    @FXML private TextField txtCurrencySymbol;
    @FXML private Button btnSaveBusiness;

    // Tab 3: Bảo mật
    @FXML private TextField txtPasswordMinLength;
    @FXML private TextField txtSessionTimeout;
    @FXML private TextField txtMaxLoginAttempts;
    @FXML private TextField txtLockoutDuration;
    @FXML private Button btnSaveSecurity;

    // Tab 4: Hệ thống
    @FXML private Label lblAppVersion;
    @FXML private Label lblDbVersion;
    @FXML private Label lblDbInfo;

    private final SettingsService settingsService = new SettingsService();
    private final AuthorizationService authService = new AuthorizationService();

    @FXML
    private void initialize() {
        // Kiểm tra quyền truy cập
        if (!authService.canManageSettings()) {
            authService.showAccessDeniedAlert();
            tabPane.setDisable(true);
            return;
        }

        // Load dữ liệu cho tất cả tabs
        loadBranchInfo();
        loadBusinessSettings();
        loadSecuritySettings();
        loadSystemInfo();

        // Setup event handlers
        btnSaveBranch.setOnAction(e -> saveBranchInfo());
        btnSaveBusiness.setOnAction(e -> saveBusinessSettings());
        btnSaveSecurity.setOnAction(e -> saveSecuritySettings());
    }

    // ========== Tab 1: Thông tin phòng gym ==========
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
            showAlert("⚠️ Vui lòng nhập tên phòng gym!");
            return;
        }

        if (settingsService.updateBranchInfo(name, address, phone)) {
            showAlert("✅ Lưu thông tin phòng gym thành công!");
        } else {
            showAlert("❌ Lỗi khi lưu thông tin phòng gym!");
        }
    }

    // ========== Tab 2: Cấu hình nghiệp vụ ==========
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
                showAlert("⚠️ Số ngày phải >= 0!");
                return;
            }

            if (memberPrefix.isEmpty() || invoicePrefix.isEmpty() || currencySymbol.isEmpty()) {
                showAlert("⚠️ Vui lòng điền đầy đủ thông tin!");
                return;
            }

            boolean success = settingsService.setGraceDays(graceDays)
                    && settingsService.setMemberCodePrefix(memberPrefix)
                    && settingsService.setInvoicePrefix(invoicePrefix)
                    && settingsService.setCurrencySymbol(currencySymbol);

            if (success) {
                showAlert("✅ Lưu cấu hình nghiệp vụ thành công!");
            } else {
                showAlert("❌ Lỗi khi lưu cấu hình nghiệp vụ!");
            }
        } catch (NumberFormatException e) {
            showAlert("⚠️ Vui lòng nhập số hợp lệ!");
        }
    }

    // ========== Tab 3: Bảo mật ==========
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
                showAlert("⚠️ Độ dài mật khẩu phải từ 6-20 ký tự!");
                return;
            }

            if (sessionTimeout < 5 || sessionTimeout > 120) {
                showAlert("⚠️ Thời gian timeout phải từ 5-120 phút!");
                return;
            }

            boolean success = settingsService.setPasswordMinLength(passwordMinLength)
                    && settingsService.setSessionTimeout(sessionTimeout)
                    && settingsService.setMaxLoginAttempts(maxLoginAttempts)
                    && settingsService.setLockoutDuration(lockoutDuration);

            if (success) {
                showAlert("✅ Lưu cấu hình bảo mật thành công!");
            } else {
                showAlert("❌ Lỗi khi lưu cấu hình bảo mật!");
            }
        } catch (NumberFormatException e) {
            showAlert("⚠️ Vui lòng nhập số hợp lệ!");
        }
    }

    // ========== Tab 4: Hệ thống ==========
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

    // Helper methods đã được kế thừa từ BaseController
}
