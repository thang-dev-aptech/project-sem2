package com.example.gympro.repository;

import java.sql.*;
import java.util.*;

import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.viewModel.PieStats;
import com.example.gympro.viewModel.RevenueData;

public class DashboardRepository {

    public List<RevenueData> getMonthlyRevenue() {
        List<RevenueData> list = new ArrayList<>();
        String sql = """
                    SELECT MONTH(p.paid_at) AS metric_month, SUM(p.paid_amount) AS total_revenue
                    FROM payments p
                    WHERE p.is_refund = 0 AND YEAR(p.paid_at) = YEAR(CURDATE())
                    GROUP BY MONTH(p.paid_at)
                    ORDER BY metric_month
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            Map<Integer, Double> monthMap = new HashMap<>();
            while (rs.next()) {
                int month = rs.getInt("metric_month");
                double total = rs.getDouble("total_revenue");
                monthMap.put(month, total);
            }

            for (int i = 1; i <= 12; i++) {
                double total = monthMap.getOrDefault(i, 0.0);
                list.add(new RevenueData(i, total));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public PieStats getPieStats() {
        String sql = """
                    SELECT
                        SUM(CASE WHEN s.end_date > CURDATE() THEN 1 ELSE 0 END) AS active_members,
                        SUM(CASE WHEN s.end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY) THEN 1 ELSE 0 END) AS expiring_members,
                        SUM(CASE WHEN s.end_date < CURDATE() THEN 1 ELSE 0 END) AS expired_members
                    FROM subscriptions s;
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int active = rs.getInt("active_members");
                int expiring = rs.getInt("expiring_members");
                int expired = rs.getInt("expired_members");

                return new PieStats(active, expiring, expired);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PieStats(0, 0, 0);
    }
}
