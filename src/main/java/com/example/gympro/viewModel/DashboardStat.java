package com.example.gympro.viewModel;

public class DashboardStat {
    private String label;
    private String value;
    private String icon;
    private String bgColor;

    public DashboardStat(String label, String value, String icon, String bgColor) {
        this.label = label;
        this.value = value;
        this.icon = icon;
        this.bgColor = bgColor;
    }

    public String getLabel() { return label; }
    public String getValue() { return value; }
    public String getIcon() { return icon; }
    public String getBgColor() { return bgColor; }
}
