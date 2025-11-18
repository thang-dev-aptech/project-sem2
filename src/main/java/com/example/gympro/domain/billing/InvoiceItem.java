package com.example.gympro.domain.billing;

import java.math.BigDecimal;

public class InvoiceItem {
    private final long id;
    private final long invoiceId;
    private final InvoiceItemType itemType;
    private final Long refId;
    private final String description;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal lineTotal;

    private InvoiceItem(Builder builder) {
        this.id = builder.id;
        this.invoiceId = builder.invoiceId;
        this.itemType = builder.itemType;
        this.refId = builder.refId;
        this.description = builder.description;
        this.quantity = builder.quantity;
        this.unitPrice = builder.unitPrice;
        this.lineTotal = builder.lineTotal;
    }

    public long getId() { return id; }
    public long getInvoiceId() { return invoiceId; }
    public InvoiceItemType getItemType() { return itemType; }
    public Long getRefId() { return refId; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getLineTotal() { return lineTotal; }

    public static class Builder {
        private long id;
        private long invoiceId;
        private InvoiceItemType itemType = InvoiceItemType.PLAN;
        private Long refId;
        private String description;
        private int quantity = 1;
        private BigDecimal unitPrice = BigDecimal.ZERO;
        private BigDecimal lineTotal = BigDecimal.ZERO;

        public Builder id(long id) { this.id = id; return this; }
        public Builder invoiceId(long invoiceId) { this.invoiceId = invoiceId; return this; }
        public Builder itemType(InvoiceItemType itemType) { this.itemType = itemType; return this; }
        public Builder refId(Long refId) { this.refId = refId; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder quantity(int quantity) { this.quantity = quantity; return this; }
        public Builder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
        public Builder lineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; return this; }

        public InvoiceItem build() {
            return new InvoiceItem(this);
        }
    }
}

