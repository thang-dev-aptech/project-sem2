package com.example.gympro.service;

import java.util.ArrayList;
import java.util.List;
import com.example.gympro.repository.DashboardRepository;
import com.example.gympro.service.settings.SettingsService;
import com.example.gympro.viewModel.DashboardStat;
import com.example.gympro.viewModel.PieStats;
import com.example.gympro.viewModel.RevenueData;

public class DashboardService {
    private final DashboardRepository repository;

    public DashboardService() {
        this.repository = new DashboardRepository();
    }

    public List<RevenueData> getMonthlyRevenue() {
        return repository.getMonthlyRevenue();
    }

    public PieStats getPieStats() {
        return repository.getPieStats();
    }

    public List<DashboardStat> getDashboardStats() {
        List<DashboardStat> stats = new ArrayList<>();

        int totalMembers = repository.getTotalMembers();
        int totalPackages = repository.getTotalPackages();
        double revenueThisMonth = repository.getTotalRevenueThisMonth();
        
        // Use default Grace Days (5 days)
        int graceDays = SettingsService.DEFAULT_GRACE_DAYS;
        int expiringMembers = repository.getExpiringMembers(graceDays);

        stats.add(new DashboardStat("Total Members", String.valueOf(totalMembers), "👥", "#4CAF50"));
        stats.add(new DashboardStat("Total Packages", String.valueOf(totalPackages), "📦", "#2196F3"));
        stats.add(new DashboardStat("Monthly Revenue", String.format("%,.0f VND", revenueThisMonth), "💰",
                "#FFC107"));
        stats.add(new DashboardStat("Expiring Soon", String.valueOf(expiringMembers), "⏰", "#F44336"));

        return stats;
    }
}
