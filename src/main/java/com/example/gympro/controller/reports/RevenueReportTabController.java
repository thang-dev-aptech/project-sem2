package com.example.gympro.controller.reports;

import com.example.gympro.controller.base.BaseController;
import com.example.gympro.service.ExcelExportService;
import com.example.gympro.service.reports.ReportsService;
import com.example.gympro.viewModel.RevenueReport;
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
 * Controller cho Tab "Doanh thu" trong Reports
 */
public class RevenueReportTabController extends BaseController {
    
    @FXML private Label lblTotalRevenue;
    @FXML private Label lblTotalInvoices;
    @FXML private Label lblTotalPayments;
    @FXML private Label lblAvgInvoice;
    @FXML private TableView<RevenueReport> tblRevenue;
    @FXML private TableColumn<RevenueReport, String> colInvoiceNo;
    @FXML private TableColumn<RevenueReport, LocalDate> colInvoiceDate;
    @FXML private TableColumn<RevenueReport, String> colMemberName;
    @FXML private TableColumn<RevenueReport, String> colMemberCode;
    @FXML private TableColumn<RevenueReport, String> colPackageName;
    @FXML private TableColumn<RevenueReport, BigDecimal> colSubtotal;
    @FXML private TableColumn<RevenueReport, BigDecimal> colDiscount;
    @FXML private TableColumn<RevenueReport, BigDecimal> colTotal;
    @FXML private TableColumn<RevenueReport, String> colStatus;
    @FXML private Button btnExportRevenue;
    
    private final ReportsService service = new ReportsService();
    private final ExcelExportService excelExportService = new ExcelExportService();
    private final ObservableList<RevenueReport> revenueList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        setupTable();
        btnExportRevenue.setOnAction(e -> exportToExcel());
    }
    
    private void setupTable() {
        colInvoiceNo.setCellValueFactory(new PropertyValueFactory<>("invoiceNo"));
        colInvoiceDate.setCellValueFactory(new PropertyValueFactory<>("invoiceDate"));
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colMemberCode.setCellValueFactory(new PropertyValueFactory<>("memberCode"));
        colPackageName.setCellValueFactory(new PropertyValueFactory<>("packageName"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Format currency columns
        formatCurrencyColumn(colSubtotal);
        formatCurrencyColumn(colDiscount);
        formatCurrencyColumn(colTotal);
        
        tblRevenue.setItems(revenueList);
    }
    
    private void formatCurrencyColumn(TableColumn<RevenueReport, BigDecimal> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item.doubleValue()));
            }
        });
    }
    
    public void loadData(LocalDate fromDate, LocalDate toDate) {
        revenueList.clear();
        revenueList.addAll(service.getRevenueReport(fromDate, toDate));
        
        var summary = service.getRevenueSummary(fromDate, toDate);
        if (summary != null) {
            lblTotalRevenue.setText(String.format("%,.0f VNĐ", summary.totalRevenue.doubleValue()));
            lblTotalInvoices.setText(String.valueOf(summary.totalInvoices));
            lblTotalPayments.setText(String.valueOf(summary.totalPayments));
            lblAvgInvoice.setText(String.format("%,.0f VNĐ", summary.avgInvoice.doubleValue()));
        }
    }
    
    private void exportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Xuất báo cáo doanh thu");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("BaoCaoDoanhThu_" + LocalDate.now() + ".xlsx");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                excelExportService.exportRevenueReport(revenueList, file.getAbsolutePath());
                showAlert("✅ Xuất Excel thành công!");
            } catch (Exception e) {
                showError("❌ Lỗi khi xuất Excel: " + e.getMessage());
            }
        }
    }
}

