package com.example.gympro.viewModel;

public class RevenueData {
    private String label;
    private double totalRevenue;

    public RevenueData(String label, double totalRevenue) {
        this.label = label;

        this.totalRevenue = totalRevenue;

    }

    public String getLabel() {
        return label;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

}
