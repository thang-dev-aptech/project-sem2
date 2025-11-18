package com.example.gympro.domain.billing;

public enum InvoiceStatus {
    ISSUED,
    VOIDED;

    public static InvoiceStatus fromDatabase(String value) {
        if (value == null || value.isBlank()) {
            return ISSUED;
        }
        try {
            return InvoiceStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ISSUED;
        }
    }

    public String toDatabaseValue() {
        return name();
    }
}

