package com.example.gympro.repository.settings;

import com.example.gympro.utils.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository for Settings - manages system configuration
 */
public class SettingsRepository {

    /**
     * Get setting value by key
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
     * Get numeric setting value
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
     * Save or update setting
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
     * Get all settings as Map
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
     * Get branch information
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
     * Update branch information
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
     * Update all member codes when prefix changes
     * Example: GYM-0001 → TMN-0001
     * @param oldPrefix The old prefix (e.g., "GYM")
     * @param newPrefix The new prefix (e.g., "TMN")
     * @return Number of members updated, or -1 if error
     */
    public int updateAllMemberCodes(String oldPrefix, String newPrefix) {
        if (oldPrefix == null || newPrefix == null || oldPrefix.equals(newPrefix)) {
            return 0; // No change needed
        }

        // First, count how many members will be affected
        String countSql = "SELECT COUNT(*) FROM members WHERE member_code LIKE ? AND is_deleted = 0";
        String likePattern = oldPrefix + "-%";
        int totalAffected = 0;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement countPs = conn.prepareStatement(countSql)) {
            countPs.setString(1, likePattern);
            try (ResultSet rs = countPs.executeQuery()) {
                if (rs.next()) {
                    totalAffected = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting members to update: " + e.getMessage());
            return -1;
        }

        if (totalAffected == 0) {
            return 0; // No members to update
        }

        // Use JOIN instead of subquery to avoid MySQL restriction
        String sql = """
            UPDATE members m1
            LEFT JOIN members m2 ON m2.member_code = CONCAT(?, SUBSTRING(m1.member_code, ?))
                AND m2.id != m1.id
                AND m2.is_deleted = 0
            SET m1.member_code = CONCAT(?, SUBSTRING(m1.member_code, ?))
            WHERE m1.member_code LIKE ?
              AND m1.is_deleted = 0
              AND m2.id IS NULL
        """;
        
        int prefixLength = oldPrefix.length() + 1; // +1 for the dash
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            
            ps.setString(1, newPrefix + "-");
            ps.setInt(2, prefixLength);
            ps.setString(3, newPrefix + "-");
            ps.setInt(4, prefixLength);
            ps.setString(5, likePattern);
            
            int updated = ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            
            if (updated < totalAffected) {
                int skipped = totalAffected - updated;
                System.out.println("Updated " + updated + " member codes from prefix '" + oldPrefix + "' to '" + newPrefix + 
                    "'. " + skipped + " skipped due to conflicts.");
            } else {
                System.out.println("Updated " + updated + " member codes from prefix '" + oldPrefix + "' to '" + newPrefix + "'");
            }
            return updated;
        } catch (SQLException e) {
            System.err.println("Error updating member codes: " + e.getMessage());
            e.printStackTrace();
            // Note: Connection is already closed by try-with-resources, no need to rollback
            return -1;
        }
    }

    /**
     * Update all invoice codes when prefix changes
     * Example: INV-0001 → BILL-0001
     * @param oldPrefix The old prefix (e.g., "INV")
     * @param newPrefix The new prefix (e.g., "BILL")
     * @return Number of invoices updated, or -1 if error
     */
    public int updateAllInvoiceCodes(String oldPrefix, String newPrefix) {
        if (oldPrefix == null || newPrefix == null || oldPrefix.equals(newPrefix)) {
            return 0; // No change needed
        }

        // First, count how many invoices will be affected
        String countSql = "SELECT COUNT(*) FROM invoices WHERE invoice_no LIKE ?";
        String likePattern = oldPrefix + "-%";
        int totalAffected = 0;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement countPs = conn.prepareStatement(countSql)) {
            countPs.setString(1, likePattern);
            try (ResultSet rs = countPs.executeQuery()) {
                if (rs.next()) {
                    totalAffected = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting invoices to update: " + e.getMessage());
            return -1;
        }

        if (totalAffected == 0) {
            return 0; // No invoices to update
        }

        // Use JOIN instead of subquery to avoid MySQL restriction
        String sql = """
            UPDATE invoices i1
            LEFT JOIN invoices i2 ON i2.invoice_no = CONCAT(?, SUBSTRING(i1.invoice_no, ?))
                AND i2.id != i1.id
            SET i1.invoice_no = CONCAT(?, SUBSTRING(i1.invoice_no, ?))
            WHERE i1.invoice_no LIKE ?
              AND i2.id IS NULL
        """;
        
        int prefixLength = oldPrefix.length() + 1; // +1 for the dash
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            
            ps.setString(1, newPrefix + "-");
            ps.setInt(2, prefixLength);
            ps.setString(3, newPrefix + "-");
            ps.setInt(4, prefixLength);
            ps.setString(5, likePattern);
            
            int updated = ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            
            if (updated < totalAffected) {
                int skipped = totalAffected - updated;
                System.out.println("Updated " + updated + " invoice codes from prefix '" + oldPrefix + "' to '" + newPrefix + 
                    "'. " + skipped + " skipped due to conflicts.");
            } else {
                System.out.println("Updated " + updated + " invoice codes from prefix '" + oldPrefix + "' to '" + newPrefix + "'");
            }
            return updated;
        } catch (SQLException e) {
            System.err.println("Error updating invoice codes: " + e.getMessage());
            e.printStackTrace();
            // Note: Connection is already closed by try-with-resources, no need to rollback
            return -1;
        }
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
}

