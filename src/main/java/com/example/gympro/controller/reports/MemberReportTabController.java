package com.example.gympro.controller.reports;

import com.example.gympro.controller.base.BaseController;
import com.example.gympro.service.ExcelExportService;
import com.example.gympro.service.reports.ReportsService;
import com.example.gympro.viewModel.MemberReport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;

/**
 * Controller cho Tab "Hội viên" trong Reports
 */
public class MemberReportTabController extends BaseController {
    
    @FXML private Label lblTotalMembers;
    @FXML private Label lblNewMembers;
    @FXML private Label lblActiveMembers;
    @FXML private Label lblExpiredMembers;
    @FXML private TableView<MemberReport> tblMembers;
    @FXML private TableColumn<MemberReport, String> colMemberCode;
    @FXML private TableColumn<MemberReport, String> colMemberName;
    @FXML private TableColumn<MemberReport, String> colMemberPhone;
    @FXML private TableColumn<MemberReport, String> colMemberEmail;
    @FXML private TableColumn<MemberReport, String> colMemberStatus;
    @FXML private TableColumn<MemberReport, String> colMemberPackage;
    @FXML private TableColumn<MemberReport, LocalDate> colMemberStartDate;
    @FXML private TableColumn<MemberReport, LocalDate> colMemberEndDate;
    @FXML private TableColumn<MemberReport, LocalDate> colMemberCreated;
    @FXML private Button btnExportMembers;
    
    private final ReportsService service = new ReportsService();
    private final ExcelExportService excelExportService = new ExcelExportService();
    private final ObservableList<MemberReport> memberList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        setupTable();
        btnExportMembers.setOnAction(e -> exportToExcel());
    }
    
    private void setupTable() {
        colMemberCode.setCellValueFactory(new PropertyValueFactory<>("memberCode"));
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colMemberPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colMemberEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colMemberStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colMemberPackage.setCellValueFactory(new PropertyValueFactory<>("packageName"));
        colMemberStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colMemberEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colMemberCreated.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        
        tblMembers.setItems(memberList);
    }
    
    public void loadData(LocalDate fromDate, LocalDate toDate) {
        memberList.clear();
        memberList.addAll(service.getMemberReport(fromDate, toDate));
        
        var summary = service.getMemberSummary(fromDate, toDate);
        if (summary != null) {
            lblTotalMembers.setText(String.valueOf(summary.totalMembers));
            lblNewMembers.setText(String.valueOf(summary.newMembers));
            lblActiveMembers.setText(String.valueOf(summary.activeMembers));
            lblExpiredMembers.setText(String.valueOf(summary.expiredMembers));
        }
    }
    
    private void exportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Xuất báo cáo hội viên");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("BaoCaoHoiVien_" + LocalDate.now() + ".xlsx");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                excelExportService.exportMemberReport(memberList, file.getAbsolutePath());
                showAlert("✅ Xuất Excel thành công!");
            } catch (Exception e) {
                showError("❌ Lỗi khi xuất Excel: " + e.getMessage());
            }
        }
    }
}

