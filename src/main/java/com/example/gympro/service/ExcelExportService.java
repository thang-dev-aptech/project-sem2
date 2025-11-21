package com.example.gympro.service;

import com.example.gympro.service.export.*;
import com.example.gympro.viewModel.*;

import java.io.IOException;
import java.util.List;

/**
 * Facade service để xuất dữ liệu ra file Excel
 * Sử dụng các exporter riêng cho từng loại dữ liệu
 */
public class ExcelExportService {
    
    /**
     * Xuất danh sách hội viên ra Excel
     */
    public void exportMembers(List<Member> members, String filePath) throws IOException {
        MemberExporter exporter = new MemberExporter();
        exporter.export(members, filePath);
    }
    
    /**
     * Xuất danh sách gói tập ra Excel
     */
    public void exportPackages(List<com.example.gympro.viewModel.Package> packages, String filePath) throws IOException {
        PackageExporter exporter = new PackageExporter();
        exporter.export(packages, filePath);
    }
    
    /**
     * Xuất danh sách hội viên sắp hết hạn ra Excel
     */
    public void exportExpiringMembers(List<ExpiringMember> members, String filePath) throws IOException {
        ExpiringMemberExporter exporter = new ExpiringMemberExporter();
        exporter.export(members, filePath);
    }
    
    /**
     * Xuất báo cáo doanh thu ra Excel
     */
    public void exportRevenueReport(List<RevenueReport> reports, String filePath) throws IOException {
        RevenueReportExporter exporter = new RevenueReportExporter();
        exporter.export(reports, filePath);
    }
    
    /**
     * Xuất báo cáo hội viên ra Excel
     */
    public void exportMemberReport(List<MemberReport> reports, String filePath) throws IOException {
        MemberReportExporter exporter = new MemberReportExporter();
        exporter.export(reports, filePath);
    }
    
    /**
     * Xuất báo cáo gói tập ra Excel
     */
    public void exportPackageReport(List<PackageReport> reports, String filePath) throws IOException {
        PackageReportExporter exporter = new PackageReportExporter();
        exporter.export(reports, filePath);
    }
    
    /**
     * Xuất báo cáo thanh toán ra Excel
     */
    public void exportPaymentReport(List<PaymentReport> reports, String filePath) throws IOException {
        PaymentReportExporter exporter = new PaymentReportExporter();
        exporter.export(reports, filePath);
    }
}
