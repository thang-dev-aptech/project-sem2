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

    // --- FXML Injections (Khớp 100% với FXML của bạn) ---
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

    // CAU HINH TAI KHOAN NHAN TIEN
    private final VietQRService vietQRService = new VietQRService();
    private final String MY_BANK_BIN = "970407"; // Đổi thành mã ngân hàng của bạn
    private final String MY_ACCOUNT_NO = "8930102003"; // Số TK của bạn
    private final String MY_ACCOUNT_NAME = "TRUONG DUC THANH";

    @FXML
    private void initialize() {
        // Load payment method IDs từ database
        paymentMethodIds = paymentMethodService.getCommonPaymentMethodIds();
        
        setupInvoiceComboBox();
        loadUnpaidInvoices();

        // Listener để điền thông tin
        invoiceComboBox.valueProperty().addListener((obs, oldInv, newInv) -> {
            if (newInv != null) {
                populateDetails(newInv);
                showDetails(true); // Hiển thị chi tiết
            } else {
                clearDetails();
                showDetails(false); // Ẩn chi tiết, hiện placeholder
            }
        });

        // Gán hành động cho nút
        processPaymentButton.setOnAction(event -> handleProcessPayment());

        // Đặt trạng thái ban đầu
        clearDetails();
        showDetails(false);
    }

    private void loadUnpaidInvoices() {
        try {
            // 1. Lấy List từ Service
            List<Invoice> unpaid = paymentService.getUnpaidInvoices();
            // 2. Chuyển sang ObservableList cho JavaFX
            invoiceComboBox.setItems(FXCollections.observableArrayList(unpaid));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi CSDL", "Không thể tải hóa đơn: " + e.getMessage());
        }
    }

    @FXML
    private void handleProcessPayment() {
        Invoice selectedInvoice = invoiceComboBox.getValue();
        if (selectedInvoice == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn", "Vui lòng chọn một hóa đơn.");
            return;
        }

        RadioButton selectedRadio = (RadioButton) paymentMethodGroup.getSelectedToggle();
        if (selectedRadio == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn", "Vui lòng chọn phương thức thanh toán.");
            return;
        }

        String methodText = selectedRadio.getText();

        // === LOGIC QR CODE ===
        if (methodText.equals("Bank Transfer")) {
            // Gọi hàm hiển thị QR
            showQRCodeDialog(selectedInvoice);
        } else {
            // Thanh toán thường (Tiền mặt / Thẻ)
            long methodId = 0;
            if (methodText.equals("Credit Card")) {
                methodId = paymentMethodIds != null ? paymentMethodIds.getCardId() : 0;
            } else if (methodText.equals("Cash")) {
                methodId = paymentMethodIds != null ? paymentMethodIds.getCashId() : 0;
            }
            
            if (methodId == 0) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy phương thức thanh toán. Vui lòng thử lại.");
                return;
            }
            
            executePaymentTransaction(selectedInvoice, methodId);
        }
    }
    private void showQRCodeDialog(Invoice invoice) {
        // Tạo một luồng riêng (Background Thread) để gọi API
        // Việc này giúp ứng dụng KHÔNG bị đơ khi mạng chậm
        new Thread(() -> {
            try {
                String content = "TT " + invoice.getInvoiceNo(); // Nội dung CK ngắn gọn

                // 1. Gọi Service lấy link ảnh
                String qrDataUrl = vietQRService.generateQRCodeBase64(
                        MY_BANK_BIN,
                        MY_ACCOUNT_NO,
                        MY_ACCOUNT_NAME,
                        invoice.getTotalAmount(),
                        content
                );

                // 2. Quay lại luồng giao diện (JavaFX Thread) để vẽ ảnh
                Platform.runLater(() -> {
                    if (qrDataUrl != null) {
                        showPopupWithImage(qrDataUrl, invoice);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tạo mã QR. Vui lòng thử lại.");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Hàm phụ để vẽ Popup
    private void showPopupWithImage(String qrUrl, Invoice invoice) {
        // Load ảnh từ chuỗi Base64 mà API trả về
        Image qrImage = new Image(qrUrl);
        ImageView imageView = new ImageView(qrImage);
        imageView.setFitWidth(400);
        imageView.setPreserveRatio(true);

        // Visualizing what happens here

        Alert qrDialog = new Alert(Alert.AlertType.CONFIRMATION);
        qrDialog.setTitle("Quét mã thanh toán");
        qrDialog.setHeaderText("Số tiền: " + moneyFormatter.format(invoice.getTotalAmount()));

        VBox content = new VBox(10, imageView);
        content.setStyle("-fx-alignment: center; -fx-padding: 10;");
        qrDialog.getDialogPane().setContent(content);

        // Tùy chỉnh nút bấm
        ButtonType btnPaid = new ButtonType("Đã nhận được tiền", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Hủy bỏ", ButtonBar.ButtonData.CANCEL_CLOSE);
        qrDialog.getButtonTypes().setAll(btnPaid, btnCancel);

        // Chờ người dùng xác nhận
        qrDialog.showAndWait().ifPresent(type -> {
            if (type == btnPaid) {
                // Nếu nhân viên bấm Đã nhận -> Lưu vào Database (BANK hoặc QR)
                long methodId = paymentMethodIds != null ? paymentMethodIds.getBankId() : 0;
                if (methodId == 0) {
                    // Fallback: thử QR nếu BANK không có
                    methodId = paymentMethodIds != null ? paymentMethodIds.getQrId() : 0;
                }
                if (methodId == 0) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy phương thức thanh toán chuyển khoản.");
                    return;
                }
                executePaymentTransaction(invoice, methodId);
            }
        });
    }
    private void executePaymentTransaction(Invoice invoice, long methodId) {
        long userId = getCurrentUserId();
        if (userId == 0) {
            showAlert(Alert.AlertType.ERROR, "Lỗi phiên đăng nhập", "Vui lòng đăng nhập lại.");
            return;
        }

        boolean success = paymentService.processPayment(
                invoice,
                methodId,
                userId
        );

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công",
                    "Giao dịch hoàn tất cho hóa đơn: " + invoice.getInvoiceNo());
            loadUnpaidInvoices(); // Tải lại danh sách
            clearDetails();       // Xóa trắng form
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi CSDL", "Không thể lưu giao dịch.");
        }
    }

    // --- CÁC HÀM TIỆN ÍCH (HELPER METHODS) ---

    private void populateDetails(Invoice invoice) {
        String formattedAmount = moneyFormatter.format(invoice.getTotalAmount());
        // Thẻ trái
        memberNameField.setText(invoice.getMember().getFullName());
        emailField.setText(invoice.getMember().getEmail());
        packageField.setText(invoice.getPlan().getName());
        amountField.setText(formattedAmount);
        // Thẻ phải
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
        invoiceAmountLabel.setText("đ 0");
    }

    // Hàm này ẩn/hiện các chi tiết
    private void showDetails(boolean show) {
        // Ẩn/hiện placeholder
        invoicePlaceholder.setVisible(!show);
        invoicePlaceholder.setManaged(!show);
        // Ẩn/hiện container chi tiết
        invoicePreviewContainer.setVisible(show);
        invoicePreviewContainer.setManaged(show);
    }

    private void setupInvoiceComboBox() {
        invoiceComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Invoice inv) {
                return (inv == null) ? "Chọn hóa đơn chờ..." : inv.getInvoiceNo() + " - " + inv.getMember().getFullName();
            }
            @Override public Invoice fromString(String s) { return null; }
        });
        invoiceComboBox.setPromptText("Chọn hóa đơn chờ...");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}