package com.example.gympro.controller.settings;

import com.example.gympro.controller.base.BaseController;
import com.example.gympro.service.settings.SettingsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Controller for "Business Configuration" tab in Settings
 */
public class BusinessSettingsTabController extends BaseController {
    
    @FXML private TextField txtMemberCodePrefix;
    @FXML private TextField txtInvoicePrefix;
    @FXML private Button btnSaveBusiness;
    
    private final SettingsService settingsService = new SettingsService();
    
    @FXML
    public void initialize() {
        loadBusinessSettings();
        btnSaveBusiness.setOnAction(e -> saveBusinessSettings());
    }
    
    private void loadBusinessSettings() {
        txtMemberCodePrefix.setText(settingsService.getMemberCodePrefix());
        txtInvoicePrefix.setText(settingsService.getInvoicePrefix());
    }
    
    private void saveBusinessSettings() {
        String memberPrefix = txtMemberCodePrefix.getText().trim();
        String invoicePrefix = txtInvoicePrefix.getText().trim();
        
        if (isEmpty(memberPrefix) || isEmpty(invoicePrefix)) {
            showWarning("⚠️ Please fill in Member Code Prefix and Invoice Prefix!");
            return;
        }
        
        // Save prefixes with detailed result (includes migration info)
        SettingsService.PrefixUpdateResult result = settingsService.setPrefixesWithResult(memberPrefix, invoicePrefix);
        
        if (result.isSuccess()) {
            showAlert(result.getMessage());
        } else {
            showError(result.getMessage());
        }
    }
}

