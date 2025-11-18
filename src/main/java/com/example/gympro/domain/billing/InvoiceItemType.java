package com.example.gympro.domain.billing;

public enum InvoiceItemType {
    PLAN,
    PRODUCT,
    SERVICE;

    public static InvoiceItemType fromDatabase(String value) {
        if (value == null || value.isBlank()) {
            return PLAN;
        }
        try {
            return InvoiceItemType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return PLAN;
        }
    }

    public String toDatabaseValue() {
        return name();
    }
}

