package com.example.gympro.viewModel;

import java.time.LocalDate;

/**
 * ViewModel cho báo cáo hội viên
 */
public class MemberReport {
    private String memberCode;
    private String memberName;
    private String phone;
    private String email;
    private String status;
    private String packageName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate createdAt;

    public MemberReport() {}

    public MemberReport(String memberCode, String memberName, String phone, String email,
                       String status, String packageName, LocalDate startDate,
                       LocalDate endDate, LocalDate createdAt) {
        this.memberCode = memberCode;
        this.memberName = memberName;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.packageName = packageName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    // Getters
    public String getMemberCode() { return memberCode; }
    public String getMemberName() { return memberName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public String getPackageName() { return packageName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LocalDate getCreatedAt() { return createdAt; }

    // Setters
    public void setMemberCode(String memberCode) { this.memberCode = memberCode; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setStatus(String status) { this.status = status; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}

