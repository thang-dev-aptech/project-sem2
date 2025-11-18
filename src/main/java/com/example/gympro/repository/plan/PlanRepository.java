package com.example.gympro.repository.plan;

import com.example.gympro.domain.plan.Plan;
import com.example.gympro.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PlanRepository implements PlanRepositoryInterface {

    private static final String SELECT_ACTIVE_PLANS_SQL =
            "SELECT id, branch_id, code, name, description, price, duration_days, is_active, created_at, updated_at FROM plans WHERE is_active = 1";

    @Override
    public List<Plan> findAllActive() {
        List<Plan> planList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ACTIVE_PLANS_SQL);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                planList.add(mapResultSetToPlan(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách gói tập: " + e.getMessage());
            e.printStackTrace();
        }
        return planList;
    }

    private Plan mapResultSetToPlan(ResultSet rs) throws SQLException {
        Timestamp createdTs = rs.getTimestamp("created_at");
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        return new Plan.Builder()
                .id(rs.getLong("id"))
                .branchId(rs.getLong("branch_id"))
                .code(rs.getString("code"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .price(rs.getBigDecimal("price"))
                .durationDays(rs.getInt("duration_days"))
                .active(rs.getBoolean("is_active"))
                .createdAt(createdTs != null ? createdTs.toLocalDateTime() : null)
                .updatedAt(updatedTs != null ? updatedTs.toLocalDateTime() : null)
                .build();
    }
}

