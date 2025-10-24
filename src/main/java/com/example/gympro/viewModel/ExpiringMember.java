package com.example.gympro.viewModel;

public class ExpiringMember {
    private String id;
    private String name;
    private String packageName;
    private String expiry;
    private int daysLeft;
    private String phone;

    public ExpiringMember(String id, String name, String packageName, String expiry, int daysLeft, String phone) {
        this.id = id;
        this.name = name;
        this.packageName = packageName;
        this.expiry = expiry;
        this.daysLeft = daysLeft;
        this.phone = phone;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPackageName() { return packageName; }
    public String getExpiry() { return expiry; }
    public int getDaysLeft() { return daysLeft; }
    public String getPhone() { return phone; }
}
