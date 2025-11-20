package com.example.gympro.viewModel;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ViewModel cho báo cáo doanh thu
 */
public class RevenueReport {
    private long invoiceId;
    private String invoiceNo;
    private LocalDate invoiceDate;
    private String memberName;
    private String memberCode;
    private String packageName;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    private String status;

    public RevenueReport() {}

    public RevenueReport(long invoiceId, String invoiceNo, LocalDate invoiceDate,
                        String memberName, String memberCode, String packageName,
                        BigDecimal subtotal, BigDecimal discount, BigDecimal total, String status) {
        this.invoiceId = invoiceId;
        this.invoiceNo = invoiceNo;
        this.invoiceDate = invoiceDate;
        this.memberName = memberName;
        this.memberCode = memberCode;
        this.packageName = packageName;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
        this.status = status;
    }

    // Getters
    public long getInvoiceId() { return invoiceId; }
    public String getInvoiceNo() { return invoiceNo; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public String getMemberName() { return memberName; }
    public String getMemberCode() { return memberCode; }
    public String getPackageName() { return packageName; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getTotal() { return total; }
    public String getStatus() { return status; }

    // Setters
    public void setInvoiceId(long invoiceId) { this.invoiceId = invoiceId; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public void setMemberCode(String memberCode) { this.memberCode = memberCode; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public void setStatus(String status) { this.status = status; }
}

