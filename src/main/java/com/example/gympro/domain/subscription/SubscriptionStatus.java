package com.example.gympro.domain.subscription;

public enum SubscriptionStatus {
    PENDING,
    ACTIVE,
    EXPIRED,
    CANCELLED;

    public static SubscriptionStatus fromDatabase(String value) {
        if (value == null || value.isBlank()) {
            return PENDING;
        }
        try {
            return SubscriptionStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return PENDING;
        }
    }

    public String toDatabaseValue() {
        return name();
    }
}

