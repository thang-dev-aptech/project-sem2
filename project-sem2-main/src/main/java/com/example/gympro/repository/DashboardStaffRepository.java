package com.example.gympro.repository;

import java.sql.*;
import java.util.*;

import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.viewModel.RevenueData;

public class DashboardStaffRepository {

    public List<RevenueData> getDailyRevenue() {
        List<RevenueData> list = new ArrayList<>();
        String sql = """
                    SELECT DATE(p.paid_at) as chart_date, SUM(p.paid_amount) as daily_revenue
                    FROM payments p
                    WHERE p.is_refund = 0
                      AND p.paid_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
                    GROUP BY DATE(p.paid_at)
                    ORDER BY chart_date ASC;
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String date = rs.getDate("chart_date").toString();
                double total = rs.getDouble("daily_revenue");
                list.add(new RevenueData(date, total));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int getTotalInvoices() {
        String sql = "SELECT COUNT(*) AS total FROM invoices";
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

    public double getTotalRevenueThisDay() {
        String sql = """
                      SELECT SUM(p.paid_amount) AS total_revenue
                      FROM payments p
                      WHERE p.is_refund = 0
                        AND DATE(p.paid_at) = DATE(CURDATE())
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
                    FROM subscriptions
                   WHERE DATEDIFF(end_date, CURDATE()) BETWEEN 0 AND ?;
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

}
