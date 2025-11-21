package com.example.gympro.controller.reports;

import com.example.gympro.controller.base.BaseController;
import com.example.gympro.service.ExcelExportService;
import com.example.gympro.service.reports.ReportsService;
import com.example.gympro.viewModel.PaymentReport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Controller cho Tab "Thanh toán" trong Reports
 */
public class PaymentReportTabController extends BaseController {
    
    @FXML private Label lblCashTotal;
    @FXML private Label lblBankTotal;
    @FXML private Label lblQRTotal;
    @FXML private TableView<PaymentReport> tblPayments;
    @FXML private TableColumn<PaymentReport, Long> colPaymentId;
    @FXML private TableColumn<PaymentReport, LocalDate> colPaymentDate;
    @FXML private TableColumn<PaymentReport, String> colPaymentInvoice;
    @FXML private TableColumn<PaymentReport, String> colPaymentMember;
    @FXML private TableColumn<PaymentReport, BigDecimal> colPaymentAmount;
    @FXML private TableColumn<PaymentReport, String> colPaymentMethod;
    @FXML private TableColumn<PaymentReport, String> colPaymentNote;
    @FXML private Button btnExportPayments;
    
    private final ReportsService service = new ReportsService();
    private final ExcelExportService excelExportService = new ExcelExportService();
    private final ObservableList<PaymentReport> paymentList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        setupTable();
        btnExportPayments.setOnAction(e -> exportToExcel());
    }
    
    private void setupTable() {
        colPaymentId.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        colPaymentDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        colPaymentInvoice.setCellValueFactory(new PropertyValueFactory<>("invoiceNo"));
        colPaymentMember.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colPaymentAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colPaymentMethod.setCellValueFactory(new PropertyValueFactory<>("methodName"));
        colPaymentNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        
        // Format currency column
        colPaymentAmount.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item.doubleValue()));
            }
        });
        
        tblPayments.setItems(paymentList);
    }
    
    public void loadData(LocalDate fromDate, LocalDate toDate) {
        paymentList.clear();
        paymentList.addAll(service.getPaymentReport(fromDate, toDate));
        
        var summary = service.getPaymentSummary(fromDate, toDate);
        if (summary != null) {
            lblCashTotal.setText(String.format("%,.0f VNĐ", summary.cashTotal.doubleValue()));
            lblBankTotal.setText(String.format("%,.0f VNĐ", summary.bankTotal.doubleValue()));
            lblQRTotal.setText(String.format("%,.0f VNĐ", summary.qrTotal.doubleValue()));
        }
    }
    
    private void exportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Xuất báo cáo thanh toán");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("BaoCaoThanhToan_" + LocalDate.now() + ".xlsx");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                excelExportService.exportPaymentReport(paymentList, file.getAbsolutePath());
                showAlert("✅ Xuất Excel thành công!");
            } catch (Exception e) {
                showError("❌ Lỗi khi xuất Excel: " + e.getMessage());
            }
        }
    }
}

