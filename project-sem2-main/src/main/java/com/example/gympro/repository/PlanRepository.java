package com.example.gympro.repository;

import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.viewModel.Plan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlanRepository implements PlanRepositoryInterface {

    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();
    private static final String SELECT_ACTIVE_PLANS_SQL =
            "SELECT id, name, price, duration_days FROM plans WHERE is_active = 1";

    @Override
    public List<Plan> findAllActive() {
        List<Plan> planList = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
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
        return new Plan(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getInt("duration_days")
        );
    }
}