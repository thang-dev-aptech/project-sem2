package com.example.gympro.controller;

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
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controller cho màn hình Báo cáo
 */
public class ReportsController {

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
        btnExportRevenue.setOnAction(e -> exportRevenueToCSV());
        btnExportMembers.setOnAction(e -> exportMembersToCSV());
        btnExportPackages.setOnAction(e -> exportPackagesToCSV());
        btnExportPayments.setOnAction(e -> exportPaymentsToCSV());

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
            showAlert("⚠️ Vui lòng chọn khoảng thời gian!");
            return;
        }

        if (dateFrom.getValue().isAfter(dateTo.getValue())) {
            showAlert("⚠️ Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc!");
            return;
        }

        LocalDate from = dateFrom.getValue();
        LocalDate to = dateTo.getValue();

        // Update summary label
        lblSummary.setText(String.format("📊 Báo cáo từ %s đến %s", 
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
            case "💰 Doanh Thu":
                loadRevenueData(from, to);
                break;
            case "👥 Hội Viên":
                loadMemberData(from, to);
                break;
            case "📦 Gói Tập":
                loadPackageData(from, to);
                break;
            case "💳 Thanh Toán":
                loadPaymentData(from, to);
                break;
        }
    }

    private void loadRevenueData(LocalDate from, LocalDate to) {
        var summary = service.getRevenueSummary(from, to);
        var reports = service.getRevenueReport(from, to);

        lblTotalRevenue.setText(String.format("%,.0f VNĐ", summary.getTotalRevenue().doubleValue()));
        lblTotalInvoices.setText(String.valueOf(summary.getTotalInvoices()));
        lblTotalPayments.setText(String.valueOf(summary.getTotalPayments()));
        lblAvgInvoice.setText(String.format("%,.0f VNĐ", summary.getAvgInvoice().doubleValue()));

        ObservableList<RevenueReport> data = FXCollections.observableArrayList(reports);
        tblRevenue.setItems(data);
    }

    private void loadMemberData(LocalDate from, LocalDate to) {
        var summary = service.getMemberSummary(from, to);
        var reports = service.getMemberReport(from, to);

        lblTotalMembers.setText(String.valueOf(summary.getTotalMembers()));
        lblNewMembers.setText(String.valueOf(summary.getNewMembers()));
        lblActiveMembers.setText(String.valueOf(summary.getActiveMembers()));
        lblExpiredMembers.setText(String.valueOf(summary.getExpiredMembers()));

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

        lblCashTotal.setText(String.format("%,.0f VNĐ", summary.getCashTotal().doubleValue()));
        lblBankTotal.setText(String.format("%,.0f VNĐ", summary.getBankTotal().doubleValue()));
        lblQRTotal.setText(String.format("%,.0f VNĐ", summary.getQrTotal().doubleValue()));

        ObservableList<PaymentReport> data = FXCollections.observableArrayList(reports);
        tblPayments.setItems(data);
    }

    private void exportRevenueToCSV() {
        exportToCSV("BaoCaoDoanhThu", tblRevenue.getItems(), 
            "Số HĐ,Ngày,Hội viên,Mã HV,Gói tập,Tạm tính,Giảm giá,Tổng cộng,Trạng thái",
            report -> String.format("%s,%s,%s,%s,%s,%.0f,%.0f,%.0f,%s",
                report.getInvoiceNo(),
                report.getInvoiceDate() != null ? report.getInvoiceDate().format(dateFormatter) : "",
                report.getMemberName() != null ? report.getMemberName() : "",
                report.getMemberCode() != null ? report.getMemberCode() : "",
                report.getPackageName() != null ? report.getPackageName() : "",
                report.getSubtotal() != null ? report.getSubtotal().doubleValue() : 0,
                report.getDiscount() != null ? report.getDiscount().doubleValue() : 0,
                report.getTotal() != null ? report.getTotal().doubleValue() : 0,
                report.getStatus() != null ? report.getStatus() : ""
            ));
    }

    private void exportMembersToCSV() {
        exportToCSV("BaoCaoHoiVien", tblMembers.getItems(),
            "Mã HV,Họ tên,SĐT,Email,Trạng thái,Gói tập,Ngày bắt đầu,Ngày kết thúc,Ngày tạo",
            report -> String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                report.getMemberCode() != null ? report.getMemberCode() : "",
                report.getMemberName() != null ? report.getMemberName() : "",
                report.getPhone() != null ? report.getPhone() : "",
                report.getEmail() != null ? report.getEmail() : "",
                report.getStatus() != null ? report.getStatus() : "",
                report.getPackageName() != null ? report.getPackageName() : "",
                report.getStartDate() != null ? report.getStartDate().format(dateFormatter) : "",
                report.getEndDate() != null ? report.getEndDate().format(dateFormatter) : "",
                report.getCreatedAt() != null ? report.getCreatedAt().format(dateFormatter) : ""
            ));
    }

    private void exportPackagesToCSV() {
        exportToCSV("BaoCaoGoiTap", tblPackages.getItems(),
            "Tên gói,Giá,Số lượng bán,Doanh thu,Trung bình/HĐ,Trạng thái",
            report -> String.format("%s,%.0f,%d,%.0f,%.0f,%s",
                report.getPackageName() != null ? report.getPackageName() : "",
                report.getPrice() != null ? report.getPrice().doubleValue() : 0,
                report.getSoldCount(),
                report.getRevenue() != null ? report.getRevenue().doubleValue() : 0,
                report.getAvgRevenue() != null ? report.getAvgRevenue().doubleValue() : 0,
                report.getStatus() != null ? report.getStatus() : ""
            ));
    }

    private void exportPaymentsToCSV() {
        exportToCSV("BaoCaoThanhToan", tblPayments.getItems(),
            "Mã TT,Ngày TT,Số HĐ,Hội viên,Số tiền,Phương thức,Ghi chú",
            report -> String.format("%d,%s,%s,%s,%.0f,%s,%s",
                report.getPaymentId(),
                report.getPaymentDate() != null ? report.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "",
                report.getInvoiceNo() != null ? report.getInvoiceNo() : "",
                report.getMemberName() != null ? report.getMemberName() : "",
                report.getAmount() != null ? report.getAmount().doubleValue() : 0,
                report.getMethodName() != null ? report.getMethodName() : "",
                report.getNote() != null ? report.getNote() : ""
            ));
    }

    private <T> void exportToCSV(String fileName, ObservableList<T> items, String header, java.util.function.Function<T, String> rowMapper) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Lưu báo cáo");
            fileChooser.setInitialFileName(fileName + ".csv");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

            File file = fileChooser.showSaveDialog(btnExportRevenue.getScene().getWindow());
            if (file != null) {
                try (PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {

                    writer.write('\uFEFF'); // BOM for Excel
                    writer.println(header);

                    for (T item : items) {
                        writer.println(rowMapper.apply(item));
                    }
                }

                showAlert("✅ Xuất thành công: " + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("❌ Lỗi khi xuất file: " + ex.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
