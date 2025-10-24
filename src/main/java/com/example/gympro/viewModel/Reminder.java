package com.example.gympro.viewModel;

public class Reminder {
    private String id;
    private String name;
    private String packageType;
    private String expiryDate;
    private String daysLeft;

    public Reminder(String id, String name, String packageType, String expiryDate, String daysLeft) {
        this.id = id;
        this.name = name;
        this.packageType = packageType;
        this.expiryDate = expiryDate;
        this.daysLeft = daysLeft;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPackageType() { return packageType; }
    public String getExpiryDate() { return expiryDate; }
    public String getDaysLeft() { return daysLeft; }
}
