package com.example.gympro.domain.member;

public enum MemberStatus {
    PENDING,  // Chờ thanh toán (khi đăng ký mới chưa thanh toán)
    ACTIVE,   // Đang hoạt động (có subscription đang active)
    EXPIRED,  // Hết hạn (subscription đã hết hạn)
    PAUSED;   // Tạm dừng (tạm thời không tập, nhưng vẫn là member)

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

