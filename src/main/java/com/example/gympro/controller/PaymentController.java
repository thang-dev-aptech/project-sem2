package com.example.gympro.controller;

import com.example.gympro.service.PaymentMethodService;
import com.example.gympro.service.PaymentService;
import com.example.gympro.service.PaymentServiceInterface;
import com.example.gympro.service.SessionManager;
import com.example.gympro.service.VietQRService;
import com.example.gympro.viewModel.Invoice;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import java.text.DecimalFormat;
import java.util.List;

public class PaymentController {

    // --- FXML Injections (100% match with your FXML) ---
    @FXML private ComboBox<Invoice> invoiceComboBox;
    @FXML private TextField memberNameField;
    @FXML private TextField emailField;
    @FXML private TextField packageField;
    @FXML private TextField amountField;
    @FXML private ToggleGroup paymentMethodGroup;
    @FXML private Button processPaymentButton;
    @FXML private Label invoiceNumberLabel;
    @FXML private Label invoiceMemberLabel;
    @FXML private Label invoiceEmailLabel;
    @FXML private Label invoicePackageLabel;
    @FXML private Label invoiceAmountLabel;
    @FXML private VBox invoicePlaceholder;
    @FXML private VBox invoicePreviewContainer;

    // --- Service ---
    private final PaymentServiceInterface paymentService = new PaymentService();
    private final PaymentMethodService paymentMethodService = new PaymentMethodService();
    private PaymentMethodService.PaymentMethodIds paymentMethodIds;


    // --- Helpers ---
    private final DecimalFormat moneyFormatter = new DecimalFormat("₫#,###");
    
    private long getCurrentUserId() {
        var currentUser = SessionManager.getInstance().getCurrentUser();
        return currentUser != null ? currentUser.getId() : 0;
    }

    // BANK ACCOUNT CONFIGURATION FOR RECEIVING PAYMENTS
    private final VietQRService vietQRService = new VietQRService();
    private final String MY_BANK_BIN = "970407"; // Change to your bank code
    private final String MY_ACCOUNT_NO = "8930102003"; // Your account number
    private final String MY_ACCOUNT_NAME = "TRUONG DUC THANH";

    @FXML
    private void initialize() {
        // Load payment method IDs from database
        paymentMethodIds = paymentMethodService.getCommonPaymentMethodIds();
        
        setupInvoiceComboBox();
        loadUnpaidInvoices();

        // Listener to populate information
        invoiceComboBox.valueProperty().addListener((obs, oldInv, newInv) -> {
            if (newInv != null) {
                populateDetails(newInv);
                showDetails(true); // Show details
            } else {
                clearDetails();
                showDetails(false); // Hide details, show placeholder
            }
        });

        // Assign action to button
        processPaymentButton.setOnAction(event -> handleProcessPayment());

        // Set initial state
        clearDetails();
        showDetails(false);
    }

    private void loadUnpaidInvoices() {
        try {
            // 1. Get List from Service
            List<Invoice> unpaid = paymentService.getUnpaidInvoices();
            // 2. Convert to ObservableList for JavaFX
            invoiceComboBox.setItems(FXCollections.observableArrayList(unpaid));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Cannot load invoices: " + e.getMessage());
        }
    }

    @FXML
    private void handleProcessPayment() {
        Invoice selectedInvoice = invoiceComboBox.getValue();
        if (selectedInvoice == null) {
            showAlert(Alert.AlertType.WARNING, "Not Selected", "Please select an invoice.");
            return;
        }

        RadioButton selectedRadio = (RadioButton) paymentMethodGroup.getSelectedToggle();
        if (selectedRadio == null) {
            showAlert(Alert.AlertType.WARNING, "Not Selected", "Please select a payment method.");
            return;
        }

        String methodText = selectedRadio.getText();

        // === QR CODE LOGIC ===
        if (methodText.equals("Bank Transfer")) {
            // Call function to show QR
            showQRCodeDialog(selectedInvoice);
        } else {
            // Regular payment (Cash / Card)
            long methodId = 0;
            if (methodText.equals("Credit Card")) {
                methodId = paymentMethodIds != null ? paymentMethodIds.getCardId() : 0;
            } else if (methodText.equals("Cash")) {
                methodId = paymentMethodIds != null ? paymentMethodIds.getCashId() : 0;
            }
            
            if (methodId == 0) {
                showAlert(Alert.AlertType.ERROR, "Error", "Payment method not found. Please try again.");
                return;
            }
            
            executePaymentTransaction(selectedInvoice, methodId);
        }
    }
    private void showQRCodeDialog(Invoice invoice) {
        // Create a separate thread (Background Thread) to call API
        // This prevents the application from freezing when network is slow
        new Thread(() -> {
            try {
                String content = "TT " + invoice.getInvoiceNo(); // Short transfer content

                // 1. Call Service to get image link
                String qrDataUrl = vietQRService.generateQRCodeBase64(
                        MY_BANK_BIN,
                        MY_ACCOUNT_NO,
                        MY_ACCOUNT_NAME,
                        invoice.getTotalAmount(),
                        content
                );

                // 2. Return to UI thread (JavaFX Thread) to render image
                Platform.runLater(() -> {
                    if (qrDataUrl != null) {
                        showPopupWithImage(qrDataUrl, invoice);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Cannot generate QR code. Please try again.");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Helper function to render Popup
    private void showPopupWithImage(String qrUrl, Invoice invoice) {
        // Load image from Base64 string returned by API
        Image qrImage = new Image(qrUrl);
        ImageView imageView = new ImageView(qrImage);
        imageView.setFitWidth(400);
        imageView.setPreserveRatio(true);

        // Visualizing what happens here

        Alert qrDialog = new Alert(Alert.AlertType.CONFIRMATION);
        qrDialog.setTitle("Scan Payment Code");
        qrDialog.setHeaderText("Amount: " + moneyFormatter.format(invoice.getTotalAmount()));

        VBox content = new VBox(10, imageView);
        content.setStyle("-fx-alignment: center; -fx-padding: 10;");
        qrDialog.getDialogPane().setContent(content);

        // Customize buttons
        ButtonType btnPaid = new ButtonType("Payment Received", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        qrDialog.getButtonTypes().setAll(btnPaid, btnCancel);

        // Wait for user confirmation
        qrDialog.showAndWait().ifPresent(type -> {
            if (type == btnPaid) {
                // If staff clicks Payment Received -> Save to Database (BANK or QR)
                long methodId = paymentMethodIds != null ? paymentMethodIds.getBankId() : 0;
                if (methodId == 0) {
                    // Fallback: try QR if BANK is not available
                    methodId = paymentMethodIds != null ? paymentMethodIds.getQrId() : 0;
                }
                if (methodId == 0) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Bank transfer payment method not found.");
                    return;
                }
                executePaymentTransaction(invoice, methodId);
            }
        });
    }
    private void executePaymentTransaction(Invoice invoice, long methodId) {
        long userId = getCurrentUserId();
        if (userId == 0) {
            showAlert(Alert.AlertType.ERROR, "Session Error", "Please login again.");
            return;
        }

        boolean success = paymentService.processPayment(
                invoice,
                methodId,
                userId
        );

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Transaction completed for invoice: " + invoice.getInvoiceNo());
            loadUnpaidInvoices(); // Reload list
            clearDetails();       // Clear form
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Cannot save transaction.");
        }
    }

    // --- HELPER METHODS ---

    private void populateDetails(Invoice invoice) {
        String formattedAmount = moneyFormatter.format(invoice.getTotalAmount());
        // Left card
        memberNameField.setText(invoice.getMember().getFullName());
        emailField.setText(invoice.getMember().getEmail());
        packageField.setText(invoice.getPlan().getName());
        amountField.setText(formattedAmount);
        // Right card
        invoiceNumberLabel.setText(invoice.getInvoiceNo());
        invoiceMemberLabel.setText(invoice.getMember().getFullName());
        invoiceEmailLabel.setText(invoice.getMember().getEmail());
        invoicePackageLabel.setText(invoice.getPlan().getName());
        invoiceAmountLabel.setText(formattedAmount);
    }

    private void clearDetails() {
        invoiceComboBox.setValue(null);
        memberNameField.clear();
        emailField.clear();
        packageField.clear();
        amountField.clear();
        invoiceNumberLabel.setText("N/A");
        invoiceMemberLabel.setText("N/A");
        invoiceEmailLabel.setText("N/A");
        invoicePackageLabel.setText("N/A");
        invoiceAmountLabel.setText("₫ 0");
    }

    // This function hides/shows details
    private void showDetails(boolean show) {
        // Hide/show placeholder
        invoicePlaceholder.setVisible(!show);
        invoicePlaceholder.setManaged(!show);
        // Hide/show details container
        invoicePreviewContainer.setVisible(show);
        invoicePreviewContainer.setManaged(show);
    }

    private void setupInvoiceComboBox() {
        invoiceComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Invoice inv) {
                return (inv == null) ? "Select pending invoice..." : inv.getInvoiceNo() + " - " + inv.getMember().getFullName();
            }
            @Override public Invoice fromString(String s) { return null; }
        });
        invoiceComboBox.setPromptText("Select pending invoice...");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}