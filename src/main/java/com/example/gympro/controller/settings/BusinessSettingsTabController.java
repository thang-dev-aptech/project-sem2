package com.example.gympro.controller.settings;

import com.example.gympro.controller.base.BaseController;
import com.example.gympro.service.settings.SettingsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.Optional;

/**
 * Controller for "Business Configuration" tab in Settings
 */
public class BusinessSettingsTabController extends BaseController {
    
    @FXML private TextField txtGraceDays;
    @FXML private TextField txtMemberCodePrefix;
    @FXML private TextField txtInvoicePrefix;
    @FXML private TextField txtCurrencySymbol;
    @FXML private Button btnSaveBusiness;
    
    private final SettingsService settingsService = new SettingsService();
    
    @FXML
    public void initialize() {
        loadBusinessSettings();
        btnSaveBusiness.setOnAction(e -> saveBusinessSettings());
    }
    
    private void loadBusinessSettings() {
        txtGraceDays.setText(String.valueOf(settingsService.getGraceDays()));
        txtMemberCodePrefix.setText(settingsService.getMemberCodePrefix());
        txtInvoicePrefix.setText(settingsService.getInvoicePrefix());
        txtCurrencySymbol.setText(settingsService.getCurrencySymbol());
    }
    
    private void saveBusinessSettings() {
        Optional<Integer> graceDaysOpt = parseInteger(txtGraceDays.getText());
        if (graceDaysOpt.isEmpty() || graceDaysOpt.get() < 0) {
            showWarning("⚠️ Days must be >= 0!");
            return;
        }
        
        String memberPrefix = txtMemberCodePrefix.getText().trim();
        String invoicePrefix = txtInvoicePrefix.getText().trim();
        String currencySymbol = txtCurrencySymbol.getText().trim();
        
        if (isEmpty(memberPrefix) || isEmpty(invoicePrefix) || isEmpty(currencySymbol)) {
            showWarning("⚠️ Please fill in all information!");
            return;
        }
        
        boolean success = settingsService.setGraceDays(graceDaysOpt.get())
                && settingsService.setMemberCodePrefix(memberPrefix)
                && settingsService.setInvoicePrefix(invoicePrefix)
                && settingsService.setCurrencySymbol(currencySymbol);
        
        if (success) {
            showAlert("✅ Business configuration saved successfully!");
        } else {
            showError("❌ Error saving business configuration!");
        }
    }
}

