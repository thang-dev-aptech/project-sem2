package com.example.gympro.repository.reports;

import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.viewModel.PaymentReport;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository cho báo cáo thanh toán
 */
public class PaymentReportRepository {
    
    /**
     * Lấy báo cáo thanh toán theo khoảng thời gian
     */
    public List<PaymentReport> getPaymentReport(LocalDate fromDate, LocalDate toDate) {
        List<PaymentReport> reports = new ArrayList<>();
        String sql = """
            SELECT 
                p.id AS payment_id,
                p.paid_at,
                i.invoice_no,
                m.full_name AS member_name,
                p.paid_amount,
                pm.name AS method_name,
                p.reference_code AS note
            FROM payments p
            JOIN invoices i ON p.invoice_id = i.id
            JOIN members m ON i.member_id = m.id
            LEFT JOIN payment_methods pm ON p.method_id = pm.id
            WHERE DATE(p.paid_at) BETWEEN ? AND ?
              AND p.is_refund = 0
            ORDER BY p.paid_at DESC, p.id DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PaymentReport report = new PaymentReport(
                        rs.getLong("payment_id"),
                        rs.getTimestamp("paid_at") != null ? rs.getTimestamp("paid_at").toLocalDateTime() : null,
                        rs.getString("invoice_no"),
                        rs.getString("member_name"),
                        rs.getBigDecimal("paid_amount"),
                        rs.getString("method_name"),
                        rs.getString("note")
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
     * Lấy tổng hợp thanh toán theo phương thức
     */
    public PaymentSummary getPaymentSummary(LocalDate fromDate, LocalDate toDate) {
        String sql = """
            SELECT 
                COALESCE(SUM(CASE WHEN pm.code = 'CASH' THEN p.paid_amount ELSE 0 END), 0) AS cash_total,
                COALESCE(SUM(CASE WHEN pm.code = 'BANK' THEN p.paid_amount ELSE 0 END), 0) AS bank_total,
                COALESCE(SUM(CASE WHEN pm.code = 'QR' THEN p.paid_amount ELSE 0 END), 0) AS qr_total
            FROM payments p
            LEFT JOIN payment_methods pm ON p.method_id = pm.id
            WHERE DATE(p.paid_at) BETWEEN ? AND ?
              AND p.is_refund = 0
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PaymentSummary(
                        rs.getBigDecimal("cash_total"),
                        rs.getBigDecimal("bank_total"),
                        rs.getBigDecimal("qr_total")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PaymentSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
    
    /**
     * Summary class cho thanh toán
     */
    public static class PaymentSummary {
        public final BigDecimal cashTotal;
        public final BigDecimal bankTotal;
        public final BigDecimal qrTotal;
        
        public PaymentSummary(BigDecimal cashTotal, BigDecimal bankTotal, BigDecimal qrTotal) {
            this.cashTotal = cashTotal;
            this.bankTotal = bankTotal;
            this.qrTotal = qrTotal;
        }
    }
}

