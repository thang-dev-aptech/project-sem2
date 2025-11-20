package com.example.gympro.viewModel;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ViewModel cho chiết khấu sự kiện
 */
public class EventDiscountViewModel {
    private long id;
    private String eventName;
    private String description;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;

    public EventDiscountViewModel() {}

    public EventDiscountViewModel(long id, String eventName, String description,
                                 BigDecimal discountPercent, BigDecimal discountAmount,
                                 LocalDate startDate, LocalDate endDate, boolean isActive) {
        this.id = id;
        this.eventName = eventName;
        this.description = description;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }

    // Getters
    public long getId() { return id; }
    public String getEventName() { return eventName; }
    public String getDescription() { return description; }
    public BigDecimal getDiscountPercent() { return discountPercent; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean isActive() { return isActive; }

    // Setters
    public void setId(long id) { this.id = id; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public void setDescription(String description) { this.description = description; }
    public void setDiscountPercent(BigDecimal discountPercent) { this.discountPercent = discountPercent; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setActive(boolean active) { isActive = active; }
}

