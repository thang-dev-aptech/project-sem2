package com.example.gympro.repository;

import com.example.gympro.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository để lấy thông tin payment methods từ database
 */
public class PaymentMethodRepository {

    private static final String SELECT_ALL_SQL = """
        SELECT id, code, display_name
        FROM payment_methods
        ORDER BY id
        """;

    private static final String SELECT_BY_CODE_SQL = """
        SELECT id, code, display_name
        FROM payment_methods
        WHERE code = ?
        """;

    private static final String SELECT_BY_ID_SQL = """
        SELECT id, code, display_name
        FROM payment_methods
        WHERE id = ?
        """;

    /**
     * Lấy tất cả payment methods
     */
    public List<PaymentMethod> findAll() {
        List<PaymentMethod> methods = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                methods.add(new PaymentMethod(
                    rs.getLong("id"),
                    rs.getString("code"),
                    rs.getString("display_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return methods;
    }

    /**
     * Lấy payment method theo code (CASH, BANK, QR, CARD)
     */
    public PaymentMethod findByCode(String code) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_CODE_SQL)) {

            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new PaymentMethod(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("display_name")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy payment method theo ID
     */
    public PaymentMethod findById(long id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new PaymentMethod(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("display_name")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy map code -> ID để cache
     */
    public Map<String, Long> getCodeToIdMap() {
        Map<String, Long> map = new HashMap<>();
        List<PaymentMethod> methods = findAll();
        for (PaymentMethod method : methods) {
            map.put(method.getCode(), method.getId());
        }
        return map;
    }

    /**
     * Inner class để đại diện cho Payment Method
     */
    public static class PaymentMethod {
        private final long id;
        private final String code;
        private final String displayName;

        public PaymentMethod(long id, String code, String displayName) {
            this.id = id;
            this.code = code;
            this.displayName = displayName;
        }

        public long getId() { return id; }
        public String getCode() { return code; }
        public String getDisplayName() { return displayName; }

        @Override
        public String toString() {
            return displayName;
        }
    }
}

