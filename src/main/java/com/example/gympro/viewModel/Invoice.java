package com.example.gympro.viewModel;

import java.time.LocalDate;

// POJO chứa dữ liệu JOIN cho màn hình Payment
public class Invoice {
    private long invoiceId;
    private String invoiceNo;
    private double totalAmount;
    private LocalDate issueDate;
    private long subscriptionId;
    private Member member; // Đối tượng Member lồng bên trong
    private Plan plan;     // Đối tượng Plan lồng bên trong

    public Invoice(long invoiceId, String invoiceNo, double totalAmount, LocalDate issueDate,
                   long subscriptionId, Member member, Plan plan) {
        this.invoiceId = invoiceId;
        this.invoiceNo = invoiceNo;
        this.totalAmount = totalAmount;
        this.issueDate = issueDate;
        this.subscriptionId = subscriptionId;
        this.member = member;
        this.plan = plan;
    }

    // Getters
    public long getInvoiceId() { return invoiceId; }
    public String getInvoiceNo() { return invoiceNo; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDate getIssueDate() { return issueDate; }
    public long getSubscriptionId() { return subscriptionId; }
    public Member getMember() { return member; }
    public Plan getPlan() { return plan; }

    @Override
    public String toString() {
        // Dùng .getFullName() vì Member POJO của bạn có hàm này
        return invoiceNo + " - " + member.getFullName();
    }
}