package com.example.gympro.controller;

import com.example.gympro.controller.base.BaseController;
import com.example.gympro.service.ExcelExportService;
import com.example.gympro.service.reports.ReportsService;
import com.example.gympro.viewModel.MemberReport;
import com.example.gympro.viewModel.PackageReport;
import com.example.gympro.viewModel.PaymentReport;
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
import java.time.format.DateTimeFormatter;

/**
 * Controller for Reports screen
 * Uses BaseController for common methods
 */
public class ReportsController extends BaseController {

    @FXML private DatePicker dateFrom;
    @FXML private DatePicker dateTo;
    @FXML private Button btnFilter;
    @FXML private Button btnReset;
    @FXML private Label lblSummary;
    @FXML private TabPane tabPane;

    // Revenue Tab
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

    // Member Tab
    @FXML private Label lblTotalMembers;
    @FXML private Label lblNewMembers;
    @FXML private Label lblActiveMembers;
    @FXML private Label lblExpiredMembers;
    @FXML private TableView<MemberReport> tblMembers;
    @FXML private TableColumn<MemberReport, String> colMemberCode2;
    @FXML private TableColumn<MemberReport, String> colMemberName2;
    @FXML private TableColumn<MemberReport, String> colMemberPhone2;
    @FXML private TableColumn<MemberReport, String> colMemberEmail2;
    @FXML private TableColumn<MemberReport, String> colMemberStatus2;
    @FXML private TableColumn<MemberReport, String> colMemberPackage2;
    @FXML private TableColumn<MemberReport, LocalDate> colMemberStartDate;
    @FXML private TableColumn<MemberReport, LocalDate> colMemberEndDate;
    @FXML private TableColumn<MemberReport, LocalDate> colMemberCreated;
    @FXML private Button btnExportMembers;

    // Package Tab
    @FXML private TableView<PackageReport> tblPackages;
    @FXML private TableColumn<PackageReport, String> colPackageName2;
    @FXML private TableColumn<PackageReport, BigDecimal> colPackagePrice;
    @FXML private TableColumn<PackageReport, Integer> colPackageSold;
    @FXML private TableColumn<PackageReport, BigDecimal> colPackageRevenue;
    @FXML private TableColumn<PackageReport, BigDecimal> colPackageAvg;
    @FXML private TableColumn<PackageReport, String> colPackageStatus;
    @FXML private Button btnExportPackages;

    // Payment Tab
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
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        // Set default date range (current month)
        LocalDate today = LocalDate.now();
        dateFrom.setValue(today.withDayOfMonth(1));
        dateTo.setValue(today);

        // Setup table columns
        setupRevenueColumns();
        setupMemberColumns();
        setupPackageColumns();
        setupPaymentColumns();

        // Setup event handlers
        btnFilter.setOnAction(e -> applyFilter());
        btnReset.setOnAction(e -> resetFilter());
        btnExportRevenue.setOnAction(e -> exportRevenueToExcel());
        btnExportMembers.setOnAction(e -> exportMembersToExcel());
        btnExportPackages.setOnAction(e -> exportPackagesToExcel());
        btnExportPayments.setOnAction(e -> exportPaymentsToExcel());

        // Load initial data
        applyFilter();

        // Listen to tab changes
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && dateFrom.getValue() != null && dateTo.getValue() != null) {
                loadTabData(newTab.getText());
            }
        });
    }

    private void setupRevenueColumns() {
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
        colSubtotal.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item.doubleValue()));
            }
        });
        colDiscount.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item.doubleValue()));
            }
        });
        colTotal.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item.doubleValue()));
            }
        });
    }

    private void setupMemberColumns() {
        colMemberCode2.setCellValueFactory(new PropertyValueFactory<>("memberCode"));
        colMemberName2.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colMemberPhone2.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colMemberEmail2.setCellValueFactory(new PropertyValueFactory<>("email"));
        colMemberStatus2.setCellValueFactory(new PropertyValueFactory<>("status"));
        colMemberPackage2.setCellValueFactory(new PropertyValueFactory<>("packageName"));
        colMemberStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colMemberEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colMemberCreated.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
    }

    private void setupPackageColumns() {
        colPackageName2.setCellValueFactory(new PropertyValueFactory<>("packageName"));
        colPackagePrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPackageSold.setCellValueFactory(new PropertyValueFactory<>("soldCount"));
        colPackageRevenue.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        colPackageAvg.setCellValueFactory(new PropertyValueFactory<>("avgRevenue"));
        colPackageStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Format currency columns
        colPackagePrice.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item.doubleValue()));
            }
        });
        colPackageRevenue.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item.doubleValue()));
            }
        });
        colPackageAvg.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item.doubleValue()));
            }
        });
    }

    private void setupPaymentColumns() {
        colPaymentId.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        colPaymentDate.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getPaymentDate() != null 
                ? cellData.getValue().getPaymentDate().toLocalDate() 
                : null;
            return new javafx.beans.property.SimpleObjectProperty<>(date);
        });
        colPaymentInvoice.setCellValueFactory(new PropertyValueFactory<>("invoiceNo"));
        colPaymentMember.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colPaymentAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colPaymentMethod.setCellValueFactory(new PropertyValueFactory<>("methodName"));
        colPaymentNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        // Format currency column
        colPaymentAmount.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item.doubleValue()));
            }
        });
    }

    private void applyFilter() {
        if (dateFrom.getValue() == null || dateTo.getValue() == null) {
            showWarning("⚠️ Please select a date range!");
            return;
        }

        if (dateFrom.getValue().isAfter(dateTo.getValue())) {
            showWarning("⚠️ Start date must be less than or equal to end date!");
            return;
        }

        LocalDate from = dateFrom.getValue();
        LocalDate to = dateTo.getValue();

        // Update summary label
        lblSummary.setText(String.format("📊 Report from %s to %s", 
            from.format(dateFormatter), to.format(dateFormatter)));

        // Load data for current tab
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            loadTabData(selectedTab.getText());
        }
    }

    private void resetFilter() {
        LocalDate today = LocalDate.now();
        dateFrom.setValue(today.withDayOfMonth(1));
        dateTo.setValue(today);
        applyFilter();
    }

    private void loadTabData(String tabText) {
        LocalDate from = dateFrom.getValue();
        LocalDate to = dateTo.getValue();

        if (from == null || to == null) return;

        switch (tabText) {
            case "💰 Revenue":
                loadRevenueData(from, to);
                break;
            case "👥 Members":
                loadMemberData(from, to);
                break;
            case "📦 Packages":
                loadPackageData(from, to);
                break;
            case "💳 Payments":
                loadPaymentData(from, to);
                break;
        }
    }

    private void loadRevenueData(LocalDate from, LocalDate to) {
        var summary = service.getRevenueSummary(from, to);
        var reports = service.getRevenueReport(from, to);

        lblTotalRevenue.setText(String.format("%,.0f VNĐ", summary.totalRevenue.doubleValue()));
        lblTotalInvoices.setText(String.valueOf(summary.totalInvoices));
        lblTotalPayments.setText(String.valueOf(summary.totalPayments));
        lblAvgInvoice.setText(String.format("%,.0f VNĐ", summary.avgInvoice.doubleValue()));

        ObservableList<RevenueReport> data = FXCollections.observableArrayList(reports);
        tblRevenue.setItems(data);
    }

    private void loadMemberData(LocalDate from, LocalDate to) {
        var summary = service.getMemberSummary(from, to);
        var reports = service.getMemberReport(from, to);

        lblTotalMembers.setText(String.valueOf(summary.totalMembers));
        lblNewMembers.setText(String.valueOf(summary.newMembers));
        lblActiveMembers.setText(String.valueOf(summary.activeMembers));
        lblExpiredMembers.setText(String.valueOf(summary.expiredMembers));

        ObservableList<MemberReport> data = FXCollections.observableArrayList(reports);
        tblMembers.setItems(data);
    }

    private void loadPackageData(LocalDate from, LocalDate to) {
        var reports = service.getPackageReport(from, to);
        ObservableList<PackageReport> data = FXCollections.observableArrayList(reports);
        tblPackages.setItems(data);
    }

    private void loadPaymentData(LocalDate from, LocalDate to) {
        var summary = service.getPaymentSummary(from, to);
        var reports = service.getPaymentReport(from, to);

        lblCashTotal.setText(String.format("%,.0f VNĐ", summary.cashTotal.doubleValue()));
        lblBankTotal.setText(String.format("%,.0f VNĐ", summary.bankTotal.doubleValue()));
        lblQRTotal.setText(String.format("%,.0f VNĐ", summary.qrTotal.doubleValue()));

        ObservableList<PaymentReport> data = FXCollections.observableArrayList(reports);
        tblPayments.setItems(data);
    }

    private void exportRevenueToExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Revenue Report");
            fileChooser.setInitialFileName("RevenueReport.xlsx");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

            File file = fileChooser.showSaveDialog(btnExportRevenue.getScene().getWindow());
            if (file != null) {
                excelExportService.exportRevenueReport(
                    tblRevenue.getItems().stream().toList(),
                    file.getAbsolutePath()
                );
                showAlert("✅ Excel export successful: " + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("❌ Error exporting Excel: " + ex.getMessage());
        }
    }

    private void exportMembersToExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Member Report");
            fileChooser.setInitialFileName("MemberReport.xlsx");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

            File file = fileChooser.showSaveDialog(btnExportMembers.getScene().getWindow());
            if (file != null) {
                excelExportService.exportMemberReport(
                    tblMembers.getItems().stream().toList(),
                    file.getAbsolutePath()
                );
                showAlert("✅ Excel export successful: " + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("❌ Error exporting Excel: " + ex.getMessage());
        }
    }

    private void exportPackagesToExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Package Report");
            fileChooser.setInitialFileName("PackageReport.xlsx");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

            File file = fileChooser.showSaveDialog(btnExportPackages.getScene().getWindow());
            if (file != null) {
                excelExportService.exportPackageReport(
                    tblPackages.getItems().stream().toList(),
                    file.getAbsolutePath()
                );
                showAlert("✅ Excel export successful: " + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("❌ Error exporting Excel: " + ex.getMessage());
        }
    }

    private void exportPaymentsToExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Payment Report");
            fileChooser.setInitialFileName("PaymentReport.xlsx");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

            File file = fileChooser.showSaveDialog(btnExportPayments.getScene().getWindow());
            if (file != null) {
                excelExportService.exportPaymentReport(
                    tblPayments.getItems().stream().toList(),
                    file.getAbsolutePath()
                );
                showAlert("✅ Excel export successful: " + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("❌ Error exporting Excel: " + ex.getMessage());
        }
    }

    // Helper methods inherited from BaseController
}
