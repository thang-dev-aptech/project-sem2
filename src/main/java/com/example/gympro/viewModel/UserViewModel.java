package com.example.gympro.viewModel;

import javafx.beans.property.*;
import java.time.LocalDateTime;

/**
 * UserViewModel - JavaFX ViewModel cho User Management
 * Dùng cho binding với UI
 */
public class UserViewModel {
    private final LongProperty id = new SimpleLongProperty(0);
    private final LongProperty branchId = new SimpleLongProperty(1);
    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty fullName = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty phone = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty(""); // Chỉ dùng khi tạo mới/đổi mật khẩu
    private final BooleanProperty isActive = new SimpleBooleanProperty(true);
    private final StringProperty rolesDisplay = new SimpleStringProperty(""); // Hiển thị roles dạng "OWNER, STAFF"
    private final ObjectProperty<LocalDateTime> lastLoginAt = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();

    public UserViewModel() {}

    // Getters
    public long getId() { return id.get(); }
    public long getBranchId() { return branchId.get(); }
    public String getUsername() { return username.get(); }
    public String getFullName() { return fullName.get(); }
    public String getEmail() { return email.get(); }
    public String getPhone() { return phone.get(); }
    public String getPassword() { return password.get(); }
    public boolean isActive() { return isActive.get(); }
    public String getRolesDisplay() { return rolesDisplay.get(); }
    public LocalDateTime getLastLoginAt() { return lastLoginAt.get(); }
    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public LocalDateTime getUpdatedAt() { return updatedAt.get(); }

    // Setters
    public void setId(long id) { this.id.set(id); }
    public void setBranchId(long branchId) { this.branchId.set(branchId); }
    public void setUsername(String username) { this.username.set(username); }
    public void setFullName(String fullName) { this.fullName.set(fullName); }
    public void setEmail(String email) { this.email.set(email); }
    public void setPhone(String phone) { this.phone.set(phone); }
    public void setPassword(String password) { this.password.set(password); }
    public void setIsActive(boolean isActive) { this.isActive.set(isActive); }
    public void setRolesDisplay(String rolesDisplay) { this.rolesDisplay.set(rolesDisplay); }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt.set(lastLoginAt); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt.set(updatedAt); }

    // Property Getters (cho JavaFX Binding)
    public LongProperty idProperty() { return id; }
    public LongProperty branchIdProperty() { return branchId; }
    public StringProperty usernameProperty() { return username; }
    public StringProperty fullNameProperty() { return fullName; }
    public StringProperty emailProperty() { return email; }
    public StringProperty phoneProperty() { return phone; }
    public StringProperty passwordProperty() { return password; }
    public BooleanProperty isActiveProperty() { return isActive; }
    public StringProperty rolesDisplayProperty() { return rolesDisplay; }
    public ObjectProperty<LocalDateTime> lastLoginAtProperty() { return lastLoginAt; }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }
    public ObjectProperty<LocalDateTime> updatedAtProperty() { return updatedAt; }
}

