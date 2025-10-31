package com.example.gympro.service;

import java.util.List;
import com.example.gympro.repository.DashboardRepository;
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
}
