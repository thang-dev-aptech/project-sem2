package com.example.gympro.viewModel;

public class User {
    private String id;
    private String name;
    private String role;
    private String phone;
    private String status;

    public User(String id, String name, String role, String phone, String status) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.status = status;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
}
