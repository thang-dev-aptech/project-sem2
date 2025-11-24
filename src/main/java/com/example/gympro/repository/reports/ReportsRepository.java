package com.example.gympro.repository.reports;

import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.viewModel.MemberReport;
import com.example.gympro.viewModel.PackageReport;
import com.example.gympro.viewModel.PaymentReport;
import com.example.gympro.viewModel.RevenueReport;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository cho báo cáo - query dữ liệu theo date range
 */
public class ReportsRepository {

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
                i.discount_value,
                i.total_amount,
                i.status
            FROM invoices i
            JOIN members m ON i.member_id = m.id
            LEFT JOIN subscriptions s ON i.subscription_id = s.id
            LEFT JOIN plans p ON s.plan_id = p.id
            WHERE i.issue_date BETWEEN ? AND ?
              AND i.status = 'ISSUED'
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
                        rs.getBigDecimal("discount_value"),
                        rs.getBigDecimal("total_amount"),
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

    /**
     * Lấy tổng doanh thu, số hóa đơn, số thanh toán trong khoảng thời gian
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
              AND i.status = 'ISSUED'
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
     * Lấy báo cáo hội viên theo khoảng thời gian
     */
    public List<MemberReport> getMemberReport(LocalDate fromDate, LocalDate toDate) {
        List<MemberReport> reports = new ArrayList<>();
        String sql = """
            SELECT 
                m.member_code,
                m.full_name AS member_name,
                m.phone,
                m.email,
                m.status,
                p.name AS package_name,
                s.start_date,
                s.end_date,
                m.created_at
            FROM members m
            LEFT JOIN (
                SELECT member_id, MAX(end_date) AS max_end_date
                FROM subscriptions
                GROUP BY member_id
            ) latest ON m.id = latest.member_id
            LEFT JOIN subscriptions s 
                ON s.member_id = latest.member_id 
                AND s.end_date = latest.max_end_date
            LEFT JOIN plans p ON s.plan_id = p.id
            WHERE m.created_at BETWEEN ? AND ?
              AND m.is_deleted = 0
            ORDER BY m.created_at DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(fromDate.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(toDate.atTime(23, 59, 59)));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MemberReport report = new MemberReport(
                        rs.getString("member_code"),
                        rs.getString("member_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("status"),
                        rs.getString("package_name"),
                        rs.getDate("start_date") != null ? rs.getDate("start_date").toLocalDate() : null,
                        rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null,
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime().toLocalDate() : null
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
     * Lấy thống kê hội viên trong khoảng thời gian
     */
    public MemberSummary getMemberSummary(LocalDate fromDate, LocalDate toDate) {
        String sql = """
            SELECT 
                COUNT(*) AS total_members,
                SUM(CASE WHEN m.created_at BETWEEN ? AND ? THEN 1 ELSE 0 END) AS new_members,
                SUM(CASE WHEN s.status = 'ACTIVE' AND s.end_date > CURDATE() THEN 1 ELSE 0 END) AS active_members,
                SUM(CASE WHEN s.status != 'ACTIVE' OR s.end_date <= CURDATE() THEN 1 ELSE 0 END) AS expired_members
            FROM members m
            LEFT JOIN (
                SELECT member_id, MAX(end_date) AS max_end_date, MAX(status) AS status
                FROM subscriptions
                GROUP BY member_id
            ) latest ON m.id = latest.member_id
            LEFT JOIN subscriptions s 
                ON s.member_id = latest.member_id 
                AND s.end_date = latest.max_end_date
            WHERE m.created_at BETWEEN ? AND ?
              AND m.is_deleted = 0
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            Timestamp from = Timestamp.valueOf(fromDate.atStartOfDay());
            Timestamp to = Timestamp.valueOf(toDate.atTime(23, 59, 59));
            ps.setTimestamp(1, from);
            ps.setTimestamp(2, to);
            ps.setTimestamp(3, from);
            ps.setTimestamp(4, to);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MemberSummary(
                        rs.getInt("total_members"),
                        rs.getInt("new_members"),
                        rs.getInt("active_members"),
                        rs.getInt("expired_members")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new MemberSummary(0, 0, 0, 0);
    }

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
            LEFT JOIN invoices i ON s.id = i.subscription_id AND i.status = 'ISSUED'
            WHERE (s.start_date BETWEEN ? AND ? OR s.start_date IS NULL)
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
                pm.display_name AS method_name,
                p.reference_code AS note
            FROM payments p
            JOIN invoices i ON p.invoice_id = i.id
            JOIN members m ON i.member_id = m.id
            JOIN payment_methods pm ON p.method_id = pm.id
            WHERE DATE(p.paid_at) BETWEEN ? AND ?
              AND p.is_refund = 0
            ORDER BY p.paid_at DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PaymentReport report = new PaymentReport(
                        rs.getLong("payment_id"),
                        rs.getTimestamp("paid_at").toLocalDateTime(),
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
     * Lấy tổng thanh toán theo phương thức trong khoảng thời gian
     */
    public PaymentSummary getPaymentSummary(LocalDate fromDate, LocalDate toDate) {
        String sql = """
            SELECT 
                COALESCE(SUM(CASE WHEN pm.code = 'CASH' THEN p.paid_amount ELSE 0 END), 0) AS cash_total,
                COALESCE(SUM(CASE WHEN pm.code = 'BANK' THEN p.paid_amount ELSE 0 END), 0) AS bank_total,
                COALESCE(SUM(CASE WHEN pm.code = 'QR' THEN p.paid_amount ELSE 0 END), 0) AS qr_total
            FROM payments p
            JOIN payment_methods pm ON p.method_id = pm.id
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

    // Inner classes for summary data
    public static class RevenueSummary {
        private final BigDecimal totalRevenue;
        private final int totalInvoices;
        private final int totalPayments;
        private final BigDecimal avgInvoice;

        public RevenueSummary(BigDecimal totalRevenue, int totalInvoices, int totalPayments, BigDecimal avgInvoice) {
            this.totalRevenue = totalRevenue;
            this.totalInvoices = totalInvoices;
            this.totalPayments = totalPayments;
            this.avgInvoice = avgInvoice;
        }

        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public int getTotalInvoices() { return totalInvoices; }
        public int getTotalPayments() { return totalPayments; }
        public BigDecimal getAvgInvoice() { return avgInvoice; }
    }

    public static class MemberSummary {
        private final int totalMembers;
        private final int newMembers;
        private final int activeMembers;
        private final int expiredMembers;

        public MemberSummary(int totalMembers, int newMembers, int activeMembers, int expiredMembers) {
            this.totalMembers = totalMembers;
            this.newMembers = newMembers;
            this.activeMembers = activeMembers;
            this.expiredMembers = expiredMembers;
        }

        public int getTotalMembers() { return totalMembers; }
        public int getNewMembers() { return newMembers; }
        public int getActiveMembers() { return activeMembers; }
        public int getExpiredMembers() { return expiredMembers; }
    }

    public static class PaymentSummary {
        private final BigDecimal cashTotal;
        private final BigDecimal bankTotal;
        private final BigDecimal qrTotal;

        public PaymentSummary(BigDecimal cashTotal, BigDecimal bankTotal, BigDecimal qrTotal) {
            this.cashTotal = cashTotal;
            this.bankTotal = bankTotal;
            this.qrTotal = qrTotal;
        }

        public BigDecimal getCashTotal() { return cashTotal; }
        public BigDecimal getBankTotal() { return bankTotal; }
        public BigDecimal getQrTotal() { return qrTotal; }
    }
}

