package com.example.gympro.service.reports;

import com.example.gympro.repository.reports.ReportsRepository;
import com.example.gympro.viewModel.MemberReport;
import com.example.gympro.viewModel.PackageReport;
import com.example.gympro.viewModel.PaymentReport;
import com.example.gympro.viewModel.RevenueReport;

import java.time.LocalDate;
import java.util.List;

/**
 * Service cho báo cáo
 */
public class ReportsService {
    private final ReportsRepository repository;

    public ReportsService() {
        this.repository = new ReportsRepository();
    }

    /**
     * Lấy báo cáo doanh thu
     */
    public List<RevenueReport> getRevenueReport(LocalDate fromDate, LocalDate toDate) {
        return repository.getRevenueReport(fromDate, toDate);
    }

    /**
     * Lấy tổng hợp doanh thu
     */
    public ReportsRepository.RevenueSummary getRevenueSummary(LocalDate fromDate, LocalDate toDate) {
        return repository.getRevenueSummary(fromDate, toDate);
    }

    /**
     * Lấy báo cáo hội viên
     */
    public List<MemberReport> getMemberReport(LocalDate fromDate, LocalDate toDate) {
        return repository.getMemberReport(fromDate, toDate);
    }

    /**
     * Lấy tổng hợp hội viên
     */
    public ReportsRepository.MemberSummary getMemberSummary(LocalDate fromDate, LocalDate toDate) {
        return repository.getMemberSummary(fromDate, toDate);
    }

    /**
     * Lấy báo cáo gói tập
     */
    public List<PackageReport> getPackageReport(LocalDate fromDate, LocalDate toDate) {
        return repository.getPackageReport(fromDate, toDate);
    }

    /**
     * Lấy báo cáo thanh toán
     */
    public List<PaymentReport> getPaymentReport(LocalDate fromDate, LocalDate toDate) {
        return repository.getPaymentReport(fromDate, toDate);
    }

    /**
     * Lấy tổng hợp thanh toán
     */
    public ReportsRepository.PaymentSummary getPaymentSummary(LocalDate fromDate, LocalDate toDate) {
        return repository.getPaymentSummary(fromDate, toDate);
    }
}

