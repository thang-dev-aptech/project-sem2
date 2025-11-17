package com.example.gympro.service;

import java.util.ArrayList;
import java.util.List;
import com.example.gympro.repository.DashboardStaffRepository;
import com.example.gympro.viewModel.RevenueData;
import com.example.gympro.viewModel.DashboardStat;

public class DashboardStaffService {
    private final DashboardStaffRepository repository;

    public DashboardStaffService() {
        this.repository = new DashboardStaffRepository();
    }

    public List<RevenueData> getDailyRevenue() {
        return repository.getDailyRevenue();
    }

    public List<DashboardStat> getDashboardStats() {
        List<DashboardStat> stats = new ArrayList<>();

        int totalInvoices = repository.getTotalInvoices();
        int totalPackages = repository.getTotalPackages();
        double revenueToday = repository.getTotalRevenueThisDay();
        int expiringMembers = repository.getExpiringMembers(3);
        stats.add(new DashboardStat("Số hóa đơn hôm nay", String.valueOf(totalInvoices), "📄", "#4CAF50"));
        stats.add(new DashboardStat("Tổng số gói", String.valueOf(totalPackages), "📦", "#2196F3"));
        stats.add(new DashboardStat("Doanh thu hôm nay", String.format("%,.0f VND", revenueToday), "💰", "#FFC107"));
        stats.add(new DashboardStat("Sắp hết hạn", String.valueOf(expiringMembers), "⏰", "#F44336"));

        return stats;
    }
}
