package com.example.gympro.service.reports;

import com.example.gympro.repository.reports.*;
import com.example.gympro.viewModel.MemberReport;
import com.example.gympro.viewModel.PackageReport;
import com.example.gympro.viewModel.PaymentReport;
import com.example.gympro.viewModel.RevenueReport;

import java.time.LocalDate;
import java.util.List;

/**
 * Service cho báo cáo
 * Sử dụng các repository riêng cho từng loại báo cáo
 */
public class ReportsService {
    private final RevenueReportRepository revenueRepository;
    private final MemberReportRepository memberRepository;
    private final PackageReportRepository packageRepository;
    private final PaymentReportRepository paymentRepository;

    public ReportsService() {
        this.revenueRepository = new RevenueReportRepository();
        this.memberRepository = new MemberReportRepository();
        this.packageRepository = new PackageReportRepository();
        this.paymentRepository = new PaymentReportRepository();
    }

    /**
     * Lấy báo cáo doanh thu
     */
    public List<RevenueReport> getRevenueReport(LocalDate fromDate, LocalDate toDate) {
        return revenueRepository.getRevenueReport(fromDate, toDate);
    }

    /**
     * Lấy tổng hợp doanh thu
     */
    public RevenueReportRepository.RevenueSummary getRevenueSummary(LocalDate fromDate, LocalDate toDate) {
        return revenueRepository.getRevenueSummary(fromDate, toDate);
    }

    /**
     * Lấy báo cáo hội viên
     */
    public List<MemberReport> getMemberReport(LocalDate fromDate, LocalDate toDate) {
        return memberRepository.getMemberReport(fromDate, toDate);
    }

    /**
     * Lấy tổng hợp hội viên
     */
    public MemberReportRepository.MemberSummary getMemberSummary(LocalDate fromDate, LocalDate toDate) {
        return memberRepository.getMemberSummary(fromDate, toDate);
    }

    /**
     * Lấy báo cáo gói tập
     */
    public List<PackageReport> getPackageReport(LocalDate fromDate, LocalDate toDate) {
        return packageRepository.getPackageReport(fromDate, toDate);
    }

    /**
     * Lấy báo cáo thanh toán
     */
    public List<PaymentReport> getPaymentReport(LocalDate fromDate, LocalDate toDate) {
        return paymentRepository.getPaymentReport(fromDate, toDate);
    }

    /**
     * Lấy tổng hợp thanh toán
     */
    public PaymentReportRepository.PaymentSummary getPaymentSummary(LocalDate fromDate, LocalDate toDate) {
        return paymentRepository.getPaymentSummary(fromDate, toDate);
    }
}
