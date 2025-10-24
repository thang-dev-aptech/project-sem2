package com.example.gympro.viewModel;

public class RevenueData {
    private String month;
    private int revenue;

    public RevenueData(String month, int revenue) {
        this.month = month;
        this.revenue = revenue;
    }

    public String getMonth() { return month; }
    public int getRevenue() { return revenue; }
}
