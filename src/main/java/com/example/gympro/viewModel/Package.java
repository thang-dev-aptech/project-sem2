package com.example.gympro.viewModel;

import javafx.beans.property.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Package {

    // Thuộc tính cơ sở (Dùng Property cho JavaFX Binding)
    private final LongProperty id = new SimpleLongProperty(0);
    private final LongProperty branchId = new SimpleLongProperty(1);
    private final StringProperty code = new SimpleStringProperty("");
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final IntegerProperty durationDays = new SimpleIntegerProperty(30);
    private final BooleanProperty isActive = new SimpleBooleanProperty(true);

    // Thuộc tính DateTime
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();

    // Constructor mặc định (Dùng để tạo gói mới)
    public Package() {}

    // --- Getters cho Repository (Data Access) ---
    public long getId() { return id.get(); }
    public long getBranchId() { return branchId.get(); }
    public String getCode() { return code.get(); }
    public String getName() { return name.get(); }
    public String getDescription() { return description.get(); }
    public BigDecimal getPrice() { return price.get(); }
    public int getDurationDays() { return durationDays.get(); }
    public boolean isActive() { return isActive.get(); }
    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public LocalDateTime getUpdatedAt() { return updatedAt.get(); }

    // --- Setters cho Repository và Controller ---
    public void setId(long id) { this.id.set(id); }
    public void setBranchId(long branchId) { this.branchId.set(branchId); }
    public void setCode(String code) { this.code.set(code); }
    public void setName(String name) { this.name.set(name); }
    public void setDescription(String description) { this.description.set(description); }
    public void setPrice(BigDecimal price) { this.price.set(price); }
    public void setDurationDays(int durationDays) { this.durationDays.set(durationDays); }
    public void setIsActive(boolean isActive) { this.isActive.set(isActive); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt.set(updatedAt); }

    // --- Property Getters (cho JavaFX Binding) ---
    public LongProperty idProperty() { return id; }
    public LongProperty branchIdProperty() { return branchId; }
    public StringProperty codeProperty() { return code; }
    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public ObjectProperty<BigDecimal> priceProperty() { return price; }
    public IntegerProperty durationDaysProperty() { return durationDays; }
    public BooleanProperty isActiveProperty() { return isActive; }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }
    public ObjectProperty<LocalDateTime> updatedAtProperty() { return updatedAt; }
}