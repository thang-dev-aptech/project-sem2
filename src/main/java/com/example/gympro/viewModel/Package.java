package com.example.gympro.viewModel;

import java.util.List;

public class Package {
    private String id;
    private String name;
    private double price;
    private String duration;
    private List<String> features;
    private int members;

    public Package(String id, String name, double price, String duration, List<String> features, int members) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.duration = duration;
        this.features = features;
        this.members = members;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDuration() { return duration; }
    public List<String> getFeatures() { return features; }
    public int getMembers() { return members; }
}
