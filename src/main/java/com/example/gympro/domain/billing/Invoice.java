package com.example.gympro.domain.billing;

import com.example.gympro.domain.member.Member;
import com.example.gympro.domain.plan.Plan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Invoice {
    private final long id;
    private final String invoiceNo;
    private final Long subscriptionId;
    private final Long shiftId;
    private final LocalDate issueDate;
    private final BigDecimal subtotalAmount;
    private final DiscountType discountType;
    private final BigDecimal discountValue;
    private final BigDecimal totalAmount;
    private final InvoiceStatus status;
    private final String voidReason;
    private final Long voidBy;
    private final LocalDateTime voidAt;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Member member;
    private final Plan plan;
    private final List<InvoiceItem> items;

    private Invoice(Builder builder) {
        this.id = builder.id;
        this.invoiceNo = builder.invoiceNo;
        this.subscriptionId = builder.subscriptionId;
        this.shiftId = builder.shiftId;
        this.issueDate = builder.issueDate;
        this.subtotalAmount = builder.subtotalAmount;
        this.discountType = builder.discountType;
        this.discountValue = builder.discountValue;
        this.totalAmount = builder.totalAmount;
        this.status = builder.status;
        this.voidReason = builder.voidReason;
        this.voidBy = builder.voidBy;
        this.voidAt = builder.voidAt;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.member = builder.member;
        this.plan = builder.plan;
        this.items = builder.items;
    }

    public long getId() { return id; }
    public String getInvoiceNo() { return invoiceNo; }
    public Long getSubscriptionId() { return subscriptionId; }
    public Long getShiftId() { return shiftId; }
    public LocalDate getIssueDate() { return issueDate; }
    public BigDecimal getSubtotalAmount() { return subtotalAmount; }
    public DiscountType getDiscountType() { return discountType; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public InvoiceStatus getStatus() { return status; }
    public String getVoidReason() { return voidReason; }
    public Long getVoidBy() { return voidBy; }
    public LocalDateTime getVoidAt() { return voidAt; }
    public Long getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Member getMember() { return member; }
    public Plan getPlan() { return plan; }
    public List<InvoiceItem> getItems() { return items; }

    public static class Builder {
        private long id;
        private String invoiceNo;
        private Long subscriptionId;
        private Long shiftId;
        private LocalDate issueDate;
        private BigDecimal subtotalAmount = BigDecimal.ZERO;
        private DiscountType discountType = DiscountType.NONE;
        private BigDecimal discountValue = BigDecimal.ZERO;
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private InvoiceStatus status = InvoiceStatus.ISSUED;
        private String voidReason;
        private Long voidBy;
        private LocalDateTime voidAt;
        private Long createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Member member;
        private Plan plan;
        private List<InvoiceItem> items = List.of();

        public Builder id(long id) { this.id = id; return this; }
        public Builder invoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; return this; }
        public Builder subscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; return this; }
        public Builder shiftId(Long shiftId) { this.shiftId = shiftId; return this; }
        public Builder issueDate(LocalDate issueDate) { this.issueDate = issueDate; return this; }
        public Builder subtotalAmount(BigDecimal subtotalAmount) { this.subtotalAmount = subtotalAmount; return this; }
        public Builder discountType(DiscountType discountType) { this.discountType = discountType; return this; }
        public Builder discountValue(BigDecimal discountValue) { this.discountValue = discountValue; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder status(InvoiceStatus status) { this.status = status; return this; }
        public Builder voidReason(String voidReason) { this.voidReason = voidReason; return this; }
        public Builder voidBy(Long voidBy) { this.voidBy = voidBy; return this; }
        public Builder voidAt(LocalDateTime voidAt) { this.voidAt = voidAt; return this; }
        public Builder createdBy(Long createdBy) { this.createdBy = createdBy; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder member(Member member) { this.member = member; return this; }
        public Builder plan(Plan plan) { this.plan = plan; return this; }
        public Builder items(List<InvoiceItem> items) { this.items = items; return this; }

        public Invoice build() {
            return new Invoice(this);
        }
    }
}

