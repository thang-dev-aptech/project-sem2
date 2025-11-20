package com.example.gympro.repository.settings;

import com.example.gympro.utils.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository cho Settings - quản lý cấu hình hệ thống
 */
public class SettingsRepository {

    /**
     * Lấy giá trị setting theo key
     */
    public String getStringSetting(String key) {
        String sql = "SELECT value_str FROM settings WHERE key_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("value_str");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy giá trị số từ setting
     */
    public BigDecimal getNumericSetting(String key) {
        String sql = "SELECT value_num FROM settings WHERE key_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("value_num");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lưu hoặc cập nhật setting
     */
    public boolean saveSetting(String key, String valueStr, BigDecimal valueNum) {
        String sql = """
            INSERT INTO settings (key_name, value_str, value_num)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
                value_str = VALUES(value_str),
                value_num = VALUES(value_num),
                updated_at = CURRENT_TIMESTAMP
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, valueStr);
            if (valueNum != null) {
                ps.setBigDecimal(3, valueNum);
            } else {
                ps.setNull(3, Types.DECIMAL);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy tất cả settings dưới dạng Map
     */
    public Map<String, String> getAllSettings() {
        Map<String, String> settings = new HashMap<>();
        String sql = "SELECT key_name, value_str, value_num FROM settings";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String key = rs.getString("key_name");
                String valueStr = rs.getString("value_str");
                BigDecimal valueNum = rs.getBigDecimal("value_num");
                
                if (valueStr != null) {
                    settings.put(key, valueStr);
                } else if (valueNum != null) {
                    settings.put(key, valueNum.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return settings;
    }

    /**
     * Lấy thông tin chi nhánh (branch)
     */
    public BranchInfo getBranchInfo() {
        String sql = "SELECT code, name, address, phone FROM branches LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new BranchInfo(
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getString("phone")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cập nhật thông tin chi nhánh
     */
    public boolean updateBranchInfo(String name, String address, String phone) {
        String sql = "UPDATE branches SET name = ?, address = ?, phone = ? WHERE code = (SELECT code FROM branches LIMIT 1)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, phone);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy danh sách chiết khấu sự kiện từ JSON trong settings
     */
    public List<EventDiscount> getEventDiscounts() {
        List<EventDiscount> discounts = new ArrayList<>();
        String sql = "SELECT value_json FROM settings WHERE key_name = 'EVENT_DISCOUNTS'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String json = rs.getString("value_json");
                // TODO: Parse JSON và convert sang List<EventDiscount>
                // Tạm thời return empty list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return discounts;
    }

    /**
     * Lưu danh sách chiết khấu sự kiện vào JSON
     */
    public boolean saveEventDiscounts(List<EventDiscount> discounts) {
        // TODO: Convert List<EventDiscount> sang JSON và lưu vào settings
        String sql = "INSERT INTO settings (key_name, value_json) VALUES ('EVENT_DISCOUNTS', ?) " +
                     "ON DUPLICATE KEY UPDATE value_json = VALUES(value_json)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Tạm thời: lưu empty JSON
            ps.setString(1, "[]");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Inner classes
    public static class BranchInfo {
        private final String code;
        private final String name;
        private final String address;
        private final String phone;

        public BranchInfo(String code, String name, String address, String phone) {
            this.code = code;
            this.name = name;
            this.address = address;
            this.phone = phone;
        }

        public String getCode() { return code; }
        public String getName() { return name; }
        public String getAddress() { return address; }
        public String getPhone() { return phone; }
    }

    public static class EventDiscount {
        private long id;
        private String eventName;
        private String description;
        private BigDecimal discountPercent;
        private BigDecimal discountAmount;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean isActive;

        // Getters and setters
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public String getEventName() { return eventName; }
        public void setEventName(String eventName) { this.eventName = eventName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getDiscountPercent() { return discountPercent; }
        public void setDiscountPercent(BigDecimal discountPercent) { this.discountPercent = discountPercent; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
    }
}

