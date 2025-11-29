package com.example.gympro.repository.reports;

import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.viewModel.PackageReport;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository cho báo cáo gói tập
 */
public class PackageReportRepository {
    
    /**
     * Lấy báo cáo gói tập theo khoảng thời gian
     */
    public List<PackageReport> getPackageReport(LocalDate fromDate, LocalDate toDate) {
        List<PackageReport> reports = new ArrayList<>();
        String sql = """
            SELECT 
                p.name AS package_name,
                p.price,
                COUNT(DISTINCT CASE WHEN s.start_date BETWEEN ? AND ? THEN s.id END) AS sold_count,
                COALESCE(SUM(CASE WHEN s.start_date BETWEEN ? AND ? THEN i.total_amount ELSE 0 END), 0) AS revenue,
                CASE 
                    WHEN COUNT(DISTINCT CASE WHEN s.start_date BETWEEN ? AND ? THEN s.id END) > 0 
                    THEN COALESCE(SUM(CASE WHEN s.start_date BETWEEN ? AND ? THEN i.total_amount ELSE 0 END), 0) 
                         / COUNT(DISTINCT CASE WHEN s.start_date BETWEEN ? AND ? THEN s.id END)
                    ELSE 0 
                END AS avg_revenue,
                CASE WHEN p.is_active = 1 THEN 'Active' ELSE 'Inactive' END AS status
            FROM plans p
            LEFT JOIN subscriptions s ON p.id = s.plan_id
            LEFT JOIN invoices i ON s.id = i.subscription_id
            GROUP BY p.id, p.name, p.price, p.is_active
            ORDER BY revenue DESC, sold_count DESC, p.name ASC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            Date from = Date.valueOf(fromDate);
            Date to = Date.valueOf(toDate);
            // Set parameters for all 10 placeholders (5 pairs of fromDate, toDate)
            ps.setDate(1, from);
            ps.setDate(2, to);
            ps.setDate(3, from);
            ps.setDate(4, to);
            ps.setDate(5, from);
            ps.setDate(6, to);
            ps.setDate(7, from);
            ps.setDate(8, to);
            ps.setDate(9, from);
            ps.setDate(10, to);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PackageReport report = new PackageReport(
                        rs.getString("package_name"),
                        rs.getBigDecimal("price"),
                        rs.getInt("sold_count"),
                        rs.getBigDecimal("revenue"),
                        rs.getBigDecimal("avg_revenue"),
                        rs.getString("status")
                    );
                    reports.add(report);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }
}

