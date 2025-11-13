package com.example.gympro.viewModel;

public class RevenueData {
    private int month;
    private double totalRevenue;

    public RevenueData(int month, double totalRevenue) {
        this.month = month;

        this.totalRevenue = totalRevenue;

    }

    public int getMonth() {
        return month;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

}
