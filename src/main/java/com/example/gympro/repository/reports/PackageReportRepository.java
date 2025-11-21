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
                COUNT(DISTINCT s.id) AS sold_count,
                COALESCE(SUM(i.total_amount), 0) AS revenue,
                CASE 
                    WHEN COUNT(DISTINCT s.id) > 0 
                    THEN COALESCE(SUM(i.total_amount), 0) / COUNT(DISTINCT s.id)
                    ELSE 0 
                END AS avg_revenue,
                CASE WHEN p.is_active = 1 THEN 'Active' ELSE 'Inactive' END AS status
            FROM plans p
            LEFT JOIN subscriptions s ON p.id = s.plan_id 
                AND s.start_date BETWEEN ? AND ?
            LEFT JOIN invoices i ON s.id = i.subscription_id 
                AND i.status = 'ISSUED'
            GROUP BY p.id, p.name, p.price, p.is_active
            ORDER BY revenue DESC, sold_count DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));

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

