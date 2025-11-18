package com.example.gympro.domain.billing;

public enum DiscountType {
    NONE,
    PERCENT,
    AMOUNT;

    public static DiscountType fromDatabase(String value) {
        if (value == null || value.isBlank()) {
            return NONE;
        }
        try {
            return DiscountType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return NONE;
        }
    }

    public String toDatabaseValue() {
        return name();
    }
}

