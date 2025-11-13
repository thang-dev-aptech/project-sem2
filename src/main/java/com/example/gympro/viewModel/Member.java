package com.example.gympro.viewModel;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Member {

    private final LongProperty id = new SimpleLongProperty(0);
    private final LongProperty branchId = new SimpleLongProperty(1);
    private final StringProperty memberCode = new SimpleStringProperty("");
    private final StringProperty fullName = new SimpleStringProperty("");
    private final StringProperty phone = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty gender = new SimpleStringProperty("OTHER");
    private final ObjectProperty<LocalDate> dob = new SimpleObjectProperty<>();
    private final StringProperty address = new SimpleStringProperty("");
    private final StringProperty status = new SimpleStringProperty("PENDING");
    private final StringProperty note = new SimpleStringProperty("");
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();

    public Member() {}

    public Member(String memId, String fullName, String email, String phone, String joinDate, String memStatus, String packageType) {
    }

    // --- Getters cho Repository ---
    public long getId() { return id.get(); }
    public long getBranchId() { return branchId.get(); }
    public String getMemberCode() { return memberCode.get(); }
    public String getFullName() { return fullName.get(); }
    public String getPhone() { return phone.get(); }
    public String getEmail() { return email.get(); }
    public String getGender() { return gender.get(); }
    public LocalDate getDob() { return dob.get(); }
    public String getAddress() { return address.get(); }
    public String getStatus() { return status.get(); }
    public String getNote() { return note.get(); }
    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public LocalDateTime getUpdatedAt() { return updatedAt.get(); }

    // --- Setters cho Repository & Controller ---
    public void setId(long id) { this.id.set(id); }
    public void setBranchId(long branchId) { this.branchId.set(branchId); }
    public void setMemberCode(String code) { this.memberCode.set(code); }
    public void setFullName(String name) { this.fullName.set(name); }
    public void setPhone(String phone) { this.phone.set(phone); }
    public void setEmail(String email) { this.email.set(email); }
    public void setGender(String gender) { this.gender.set(gender); }
    public void setDob(LocalDate dob) { this.dob.set(dob); }
    public void setAddress(String address) { this.address.set(address); }
    public void setStatus(String status) { this.status.set(status); }
    public void setNote(String note) { this.note.set(note); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt.set(updatedAt); }

    // --- Property Getters (cho JavaFX Binding) ---
    public LongProperty idProperty() { return id; }
    public StringProperty memberCodeProperty() { return memberCode; }
    public StringProperty fullNameProperty() { return fullName; }
    public StringProperty phoneProperty() { return phone; }
    public StringProperty emailProperty() { return email; }
    public StringProperty genderProperty() { return gender; }
    public ObjectProperty<LocalDate> dobProperty() { return dob; }
    public StringProperty statusProperty() { return status; }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }
    public ObjectProperty<LocalDateTime> updatedAtProperty() { return updatedAt; }
}
