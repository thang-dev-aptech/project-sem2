package com.example.gympro.domain.plan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Plan {
    private final long id;
    private final long branchId;
    private final String code;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final int durationDays;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Plan(Builder builder) {
        this.id = builder.id;
        this.branchId = builder.branchId;
        this.code = builder.code;
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.durationDays = builder.durationDays;
        this.active = builder.active;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public long getId() { return id; }
    public long getBranchId() { return branchId; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getDurationDays() { return durationDays; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Builder toBuilder() {
        return new Builder()
                .id(id)
                .branchId(branchId)
                .code(code)
                .name(name)
                .description(description)
                .price(price)
                .durationDays(durationDays)
                .active(active)
                .createdAt(createdAt)
                .updatedAt(updatedAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plan plan = (Plan) o;
        return id == plan.id && Objects.equals(code, plan.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }

    @Override
    public String toString() {
        return "Plan{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }

    public static class Builder {
        private long id;
        private long branchId = 1;
        private String code;
        private String name;
        private String description;
        private BigDecimal price = BigDecimal.ZERO;
        private int durationDays = 30;
        private boolean active = true;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(long id) { this.id = id; return this; }
        public Builder branchId(long branchId) { this.branchId = branchId; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder price(BigDecimal price) { this.price = price; return this; }
        public Builder durationDays(int durationDays) { this.durationDays = durationDays; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Plan build() {
            return new Plan(this);
        }
    }
}

