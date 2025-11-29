package com.example.gympro.controller.settings;

import com.example.gympro.controller.base.BaseController;
import com.example.gympro.service.settings.SettingsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Controller for "Gym Information" tab in Settings
 */
public class BranchInfoTabController extends BaseController {
    
    @FXML private TextField txtGymName;
    @FXML private TextField txtGymAddress;
    @FXML private TextField txtGymPhone;
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
            showWarning("⚠️ Please enter gym name!");
            return;
        }
        
        if (settingsService.updateBranchInfo(name, address, phone)) {
            showAlert("✅ Gym information saved successfully!");
        } else {
            showError("❌ Error saving gym information!");
        }
    }
}

