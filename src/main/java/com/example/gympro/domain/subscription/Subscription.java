package com.example.gympro.domain.subscription;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Subscription {
    private final long id;
    private final long memberId;
    private final long planId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final SubscriptionStatus status;
    private final String note;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Subscription(Builder builder) {
        this.id = builder.id;
        this.memberId = builder.memberId;
        this.planId = builder.planId;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.status = builder.status;
        this.note = builder.note;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public long getId() { return id; }
    public long getMemberId() { return memberId; }
    public long getPlanId() { return planId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public SubscriptionStatus getStatus() { return status; }
    public String getNote() { return note; }
    public Long getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public static class Builder {
        private long id;
        private long memberId;
        private long planId;
        private LocalDate startDate;
        private LocalDate endDate;
        private SubscriptionStatus status = SubscriptionStatus.PENDING;
        private String note;
        private Long createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(long id) { this.id = id; return this; }
        public Builder memberId(long memberId) { this.memberId = memberId; return this; }
        public Builder planId(long planId) { this.planId = planId; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder status(SubscriptionStatus status) { this.status = status; return this; }
        public Builder note(String note) { this.note = note; return this; }
        public Builder createdBy(Long createdBy) { this.createdBy = createdBy; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Subscription build() {
            return new Subscription(this);
        }
    }
}

