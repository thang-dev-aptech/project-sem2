package com.example.gympro.domain;

import java.time.LocalDateTime;

/**
 * User entity - Người dùng hệ thống
 * Bao gồm nhân viên, quản lý
 */
public class User {
    private Long id;
    private Long branchId;
    private Branch branch;  // Association
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;
    private Boolean isActive;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(Long branchId, String username, String fullName, String email, 
                String phone, String passwordHash) {
        this.branchId = branchId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.isActive = true;
    }

    public User(Long id, Long branchId, String username, String fullName, String email,
                String phone, String passwordHash, Boolean isActive) {
        this.id = id;
        this.branchId = branchId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.isActive = isActive;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getBranchId() {
        return branchId;
    }

    public Branch getBranch() {
        return branch;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
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

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
