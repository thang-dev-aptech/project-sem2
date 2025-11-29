package com.example.gympro.repository.reports;

import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.viewModel.RevenueReport;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository cho báo cáo doanh thu
 */
public class RevenueReportRepository {
    
    /**
     * Lấy báo cáo doanh thu theo khoảng thời gian
     */
    public List<RevenueReport> getRevenueReport(LocalDate fromDate, LocalDate toDate) {
        List<RevenueReport> reports = new ArrayList<>();
        String sql = """
            SELECT 
                i.id AS invoice_id,
                i.invoice_no,
                i.issue_date,
                m.full_name AS member_name,
                m.member_code,
                p.name AS package_name,
                i.subtotal_amount,
                i.total_amount
            FROM invoices i
            JOIN members m ON i.member_id = m.id
            LEFT JOIN subscriptions s ON i.subscription_id = s.id
            LEFT JOIN plans p ON s.plan_id = p.id
            WHERE i.issue_date BETWEEN ? AND ?
            ORDER BY i.issue_date DESC, i.invoice_no DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RevenueReport report = new RevenueReport(
                        rs.getLong("invoice_id"),
                        rs.getString("invoice_no"),
                        rs.getDate("issue_date").toLocalDate(),
                        rs.getString("member_name"),
                        rs.getString("member_code"),
                        rs.getString("package_name"),
                        rs.getBigDecimal("subtotal_amount"),
                        rs.getBigDecimal("total_amount"),
                        "ISSUED"
                    );
                    reports.add(report);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }
    
    /**
     * Lấy tổng hợp doanh thu
     */
    public RevenueSummary getRevenueSummary(LocalDate fromDate, LocalDate toDate) {
        String sql = """
            SELECT 
                COALESCE(SUM(i.total_amount), 0) AS total_revenue,
                COUNT(DISTINCT i.id) AS total_invoices,
                COUNT(DISTINCT p.id) AS total_payments,
                CASE 
                    WHEN COUNT(DISTINCT i.id) > 0 
                    THEN COALESCE(SUM(i.total_amount), 0) / COUNT(DISTINCT i.id)
                    ELSE 0 
                END AS avg_invoice
            FROM invoices i
            LEFT JOIN payments p ON i.id = p.invoice_id AND p.is_refund = 0
            WHERE i.issue_date BETWEEN ? AND ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new RevenueSummary(
                        rs.getBigDecimal("total_revenue"),
                        rs.getInt("total_invoices"),
                        rs.getInt("total_payments"),
                        rs.getBigDecimal("avg_invoice")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new RevenueSummary(BigDecimal.ZERO, 0, 0, BigDecimal.ZERO);
    }
    
    /**
     * Summary class cho doanh thu
     */
    public static class RevenueSummary {
        public final BigDecimal totalRevenue;
        public final int totalInvoices;
        public final int totalPayments;
        public final BigDecimal avgInvoice;
        
        public RevenueSummary(BigDecimal totalRevenue, int totalInvoices, int totalPayments, BigDecimal avgInvoice) {
            this.totalRevenue = totalRevenue;
            this.totalInvoices = totalInvoices;
            this.totalPayments = totalPayments;
            this.avgInvoice = avgInvoice;
        }
    }
}

