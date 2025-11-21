package com.example.gympro.controller.reports;

import com.example.gympro.controller.base.BaseController;
import com.example.gympro.service.ExcelExportService;
import com.example.gympro.service.reports.ReportsService;
import com.example.gympro.viewModel.PackageReport;
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
 * Controller cho Tab "Gói tập" trong Reports
 */
public class PackageReportTabController extends BaseController {
    
    @FXML private TableView<PackageReport> tblPackages;
    @FXML private TableColumn<PackageReport, String> colPackageName;
    @FXML private TableColumn<PackageReport, BigDecimal> colPackagePrice;
    @FXML private TableColumn<PackageReport, Integer> colPackageSold;
    @FXML private TableColumn<PackageReport, BigDecimal> colPackageRevenue;
    @FXML private TableColumn<PackageReport, BigDecimal> colPackageAvg;
    @FXML private TableColumn<PackageReport, String> colPackageStatus;
    @FXML private Button btnExportPackages;
    
    private final ReportsService service = new ReportsService();
    private final ExcelExportService excelExportService = new ExcelExportService();
    private final ObservableList<PackageReport> packageList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        setupTable();
        btnExportPackages.setOnAction(e -> exportToExcel());
    }
    
    private void setupTable() {
        colPackageName.setCellValueFactory(new PropertyValueFactory<>("packageName"));
        colPackagePrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPackageSold.setCellValueFactory(new PropertyValueFactory<>("soldCount"));
        colPackageRevenue.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        colPackageAvg.setCellValueFactory(new PropertyValueFactory<>("avgRevenue"));
        colPackageStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Format currency columns
        formatCurrencyColumn(colPackagePrice);
        formatCurrencyColumn(colPackageRevenue);
        formatCurrencyColumn(colPackageAvg);
        
        tblPackages.setItems(packageList);
    }
    
    private void formatCurrencyColumn(TableColumn<PackageReport, BigDecimal> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item.doubleValue()));
            }
        });
    }
    
    public void loadData(LocalDate fromDate, LocalDate toDate) {
        packageList.clear();
        packageList.addAll(service.getPackageReport(fromDate, toDate));
    }
    
    private void exportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Xuất báo cáo gói tập");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("BaoCaoGoiTap_" + LocalDate.now() + ".xlsx");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                excelExportService.exportPackageReport(packageList, file.getAbsolutePath());
                showAlert("✅ Xuất Excel thành công!");
            } catch (Exception e) {
                showError("❌ Lỗi khi xuất Excel: " + e.getMessage());
            }
        }
    }
}

