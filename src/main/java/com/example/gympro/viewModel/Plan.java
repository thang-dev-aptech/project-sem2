package com.example.gympro.viewModel;

import java.text.DecimalFormat;

// POJO chứa dữ liệu từ bảng 'plans'
public class Plan {
    private long id;
    private String name;
    private double price;
    private int durationDays;

    // Constructor rỗng (nếu cần)
    public Plan() {}

    // Constructor đầy đủ (dùng bởi Repository)
    public Plan(long id, String name, double price, int durationDays) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.durationDays = durationDays;
    }

    // Getters
    public long getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getDurationDays() { return durationDays; }

    // Setters (nếu cần)
    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("₫#,###");
        return name + " - " + durationDays + " ngày (" + df.format(price) + ")";
    }
}