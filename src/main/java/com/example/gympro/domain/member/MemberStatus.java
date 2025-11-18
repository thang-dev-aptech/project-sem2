package com.example.gympro.domain.member;

public enum MemberStatus {
    PENDING,
    ACTIVE,
    EXPIRED,
    PAUSED,
    RENEWED;

    public static MemberStatus fromDatabase(String value) {
        if (value == null || value.isBlank()) {
            return PENDING;
        }
        try {
            return MemberStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return PENDING;
        }
    }

    public String toDatabaseValue() {
        return name();
    }
}

