package com.example.gympro.domain.billing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private final long id;
    private final long invoiceId;
    private final long methodId;
    private final Long shiftId;
    private final BigDecimal paidAmount;
    private final LocalDateTime paidAt;
    private final String referenceCode;
    private final long createdBy;
    private final boolean refund;
    private final Long refundOfPaymentId;

    private Payment(Builder builder) {
        this.id = builder.id;
        this.invoiceId = builder.invoiceId;
        this.methodId = builder.methodId;
        this.shiftId = builder.shiftId;
        this.paidAmount = builder.paidAmount;
        this.paidAt = builder.paidAt;
        this.referenceCode = builder.referenceCode;
        this.createdBy = builder.createdBy;
        this.refund = builder.refund;
        this.refundOfPaymentId = builder.refundOfPaymentId;
    }

    public long getId() { return id; }
    public long getInvoiceId() { return invoiceId; }
    public long getMethodId() { return methodId; }
    public Long getShiftId() { return shiftId; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public String getReferenceCode() { return referenceCode; }
    public long getCreatedBy() { return createdBy; }
    public boolean isRefund() { return refund; }
    public Long getRefundOfPaymentId() { return refundOfPaymentId; }

    public static class Builder {
        private long id;
        private long invoiceId;
        private long methodId;
        private Long shiftId;
        private BigDecimal paidAmount = BigDecimal.ZERO;
        private LocalDateTime paidAt;
        private String referenceCode;
        private long createdBy;
        private boolean refund;
        private Long refundOfPaymentId;

        public Builder id(long id) { this.id = id; return this; }
        public Builder invoiceId(long invoiceId) { this.invoiceId = invoiceId; return this; }
        public Builder methodId(long methodId) { this.methodId = methodId; return this; }
        public Builder shiftId(Long shiftId) { this.shiftId = shiftId; return this; }
        public Builder paidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; return this; }
        public Builder paidAt(LocalDateTime paidAt) { this.paidAt = paidAt; return this; }
        public Builder referenceCode(String referenceCode) { this.referenceCode = referenceCode; return this; }
        public Builder createdBy(long createdBy) { this.createdBy = createdBy; return this; }
        public Builder refund(boolean refund) { this.refund = refund; return this; }
        public Builder refundOfPaymentId(Long refundOfPaymentId) { this.refundOfPaymentId = refundOfPaymentId; return this; }

        public Payment build() {
            return new Payment(this);
        }
    }
}

