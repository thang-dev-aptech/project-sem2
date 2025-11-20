package com.example.gympro.viewModel;

import java.math.BigDecimal;

/**
 * ViewModel cho báo cáo gói tập
 */
public class PackageReport {
    private String packageName;
    private BigDecimal price;
    private int soldCount;
    private BigDecimal revenue;
    private BigDecimal avgRevenue;
    private String status;

    public PackageReport() {}

    public PackageReport(String packageName, BigDecimal price, int soldCount,
                        BigDecimal revenue, BigDecimal avgRevenue, String status) {
        this.packageName = packageName;
        this.price = price;
        this.soldCount = soldCount;
        this.revenue = revenue;
        this.avgRevenue = avgRevenue;
        this.status = status;
    }

    // Getters
    public String getPackageName() { return packageName; }
    public BigDecimal getPrice() { return price; }
    public int getSoldCount() { return soldCount; }
    public BigDecimal getRevenue() { return revenue; }
    public BigDecimal getAvgRevenue() { return avgRevenue; }
    public String getStatus() { return status; }

    // Setters
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setSoldCount(int soldCount) { this.soldCount = soldCount; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
    public void setAvgRevenue(BigDecimal avgRevenue) { this.avgRevenue = avgRevenue; }
    public void setStatus(String status) { this.status = status; }
}

