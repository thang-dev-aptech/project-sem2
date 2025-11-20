package com.example.gympro.viewModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ViewModel cho báo cáo thanh toán
 */
public class PaymentReport {
    private long paymentId;
    private LocalDateTime paymentDate;
    private String invoiceNo;
    private String memberName;
    private BigDecimal amount;
    private String methodName;
    private String note;

    public PaymentReport() {}

    public PaymentReport(long paymentId, LocalDateTime paymentDate, String invoiceNo,
                        String memberName, BigDecimal amount, String methodName, String note) {
        this.paymentId = paymentId;
        this.paymentDate = paymentDate;
        this.invoiceNo = invoiceNo;
        this.memberName = memberName;
        this.amount = amount;
        this.methodName = methodName;
        this.note = note;
    }

    // Getters
    public long getPaymentId() { return paymentId; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public String getInvoiceNo() { return invoiceNo; }
    public String getMemberName() { return memberName; }
    public BigDecimal getAmount() { return amount; }
    public String getMethodName() { return methodName; }
    public String getNote() { return note; }

    // Setters
    public void setPaymentId(long paymentId) { this.paymentId = paymentId; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
    public void setNote(String note) { this.note = note; }
}

