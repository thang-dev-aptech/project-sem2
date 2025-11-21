package com.example.gympro.controller.settings;

import com.example.gympro.controller.base.BaseController;
import com.example.gympro.service.settings.SettingsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Controller cho Tab "Thông tin phòng gym" trong Settings
 */
public class BranchInfoTabController extends BaseController {
    
    @FXML private TextField txtGymName;
    @FXML private TextField txtGymAddress;
    @FXML private TextField txtGymPhone;
    @FXML private TextField txtGymEmail;
    @FXML private Button btnSaveBranch;
    
    private final SettingsService settingsService = new SettingsService();
    
    @FXML
    public void initialize() {
        loadBranchInfo();
        btnSaveBranch.setOnAction(e -> saveBranchInfo());
    }
    
    private void loadBranchInfo() {
        var branch = settingsService.getBranchInfo();
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
        
        if (isEmpty(name)) {
            showWarning("⚠️ Vui lòng nhập tên phòng gym!");
            return;
        }
        
        if (settingsService.updateBranchInfo(name, address, phone)) {
            showAlert("✅ Lưu thông tin phòng gym thành công!");
        } else {
            showError("❌ Lỗi khi lưu thông tin phòng gym!");
        }
    }
}

