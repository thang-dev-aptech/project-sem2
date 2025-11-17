package com.example.gympro.repository;

import java.sql.*;
import java.util.*;

import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.viewModel.PieStats;
import com.example.gympro.viewModel.RevenueData;

public class DashboardRepository {
    public int getTotalMembers() {
        String sql = "SELECT COUNT(*) AS total FROM members";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalPackages() {
        String sql = "SELECT COUNT(*) AS total FROM plans";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getTotalRevenueThisMonth() {
        String sql = """
                SELECT SUM(p.paid_amount) AS total_revenue
                FROM payments p
                WHERE p.is_refund = 0
                  AND YEAR(p.paid_at) = YEAR(CURDATE())
                  AND MONTH(p.paid_at) = MONTH(CURDATE());


                                                """;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total_revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getExpiringMembers(int days) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM members m
                JOIN subscriptions s
                    ON m.id = s.member_id
                JOIN (
                    SELECT member_id, MAX(end_date) AS max_end_date
                    FROM subscriptions
                    GROUP BY member_id
                ) latest
                    ON s.member_id = latest.member_id
                    AND s.end_date = latest.max_end_date
                WHERE DATEDIFF(s.end_date, CURDATE()) BETWEEN 0 AND ?
                  AND m.is_deleted = 0;
                   """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

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
                list.add(new RevenueData("Tháng " + i, total));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public PieStats getPieStats() {
        String sql = """
                    SELECT
                        SUM(CASE WHEN s.status = 'ACTIVE' AND DATEDIFF(s.end_date, CURDATE()) > 3 THEN 1 ELSE 0 END) AS active_members,
                        SUM(CASE WHEN s.status = 'ACTIVE' AND DATEDIFF(s.end_date, CURDATE()) BETWEEN 1 AND 3 THEN 1 ELSE 0 END) AS expiring_members,
                        SUM(CASE WHEN s.status != 'ACTIVE' OR DATEDIFF(s.end_date, CURDATE()) <= 0 THEN 1 ELSE 0 END) AS expired_members
                    FROM subscriptions s
                    JOIN members m ON s.member_id = m.id
                    WHERE m.is_deleted = 0
                      AND s.end_date = (
                          SELECT MAX(end_date)
                          FROM subscriptions
                          WHERE member_id = s.member_id
                      );
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
