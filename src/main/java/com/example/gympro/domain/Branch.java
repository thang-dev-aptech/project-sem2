package com.example.gympro.domain;

import java.time.LocalDateTime;

/**
 * Branch entity - Chi nhánh phòng gym
 * Mỗi hệ thống có thể có nhiều chi nhánh
 */
public class Branch {
    private Long id;
    private String code;
    private String name;
    private String address;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Branch() {
    }

    public Branch(String code, String name, String address, String phone) {
        this.code = code;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public Branch(Long id, String code, String name, String address, String phone) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Branch{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
