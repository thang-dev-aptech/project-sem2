// File: src/main/java/com/gympro.model/Member.java
package com.example.gympro.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Member {

    // Khai báo các trường sử dụng final StringProperty
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty phone;
    private final StringProperty joinDate;
    private final StringProperty status;
    private final StringProperty packageType;

    // Constructor: Khởi tạo SimpleStringProperty
    public Member(String id, String name, String email, String phone, String joinDate, String status, String packageType) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.joinDate = new SimpleStringProperty(joinDate);
        this.status = new SimpleStringProperty(status);
        this.packageType = new SimpleStringProperty(packageType);
    }

    // ===============================================
    // GETTERS CHO PROPERTY (Dùng cho TableView)
    // ===============================================
    public StringProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }
    public StringProperty phoneProperty() { return phone; }
    public StringProperty joinDateProperty() { return joinDate; }
    public StringProperty statusProperty() { return status; }
    public StringProperty packageTypeProperty() { return packageType; }

    // ===============================================
    // GETTERS CHO GIÁ TRỊ (Dùng cho logic)
    // ===============================================
    public String getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getEmail() { return email.get(); }
    public String getPhone() { return phone.get(); }
    public String getJoinDate() { return joinDate.get(); }
    public String getStatus() { return status.get(); }
    public String getPackageType() { return packageType.get(); }

    // ===============================================
    // SETTERS (Dùng để cập nhật dữ liệu)
    // ===============================================
    // Setter phải set lên Property để TableView tự động cập nhật
    public void setId(String id) { this.id.set(id); }
    public void setJoinDate(String joinDate) { this.joinDate.set(joinDate); }
    public void setName(String name) { this.name.set(name); }
    public void setEmail(String email) { this.email.set(email); }
    public void setPhone(String phone) { this.phone.set(phone); }
    public void setStatus(String status) { this.status.set(status); }
    public void setPackageType(String packageType) { this.packageType.set(packageType); }
}