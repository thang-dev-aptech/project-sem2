package com.example.gympro.repository;

import com.example.gympro.viewModel.Member;
import com.example.gympro.viewModel.Plan;
import java.sql.*;
import java.time.LocalDate;

public class RegistrationRepository implements RegistrationRepositoryInterface {

    // Hằng số SQL cho 3 bước của nghiệp vụ
    private static final String INSERT_SUBSCRIPTION_SQL =
            "INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status, created_by) VALUES (?, ?, ?, ?, 'PENDING', ?)";

    private static final String INSERT_INVOICE_SQL =
            "INSERT INTO invoices (member_id, subscription_id, invoice_no, subtotal_amount, total_amount, status, created_by, issue_date) VALUES (?, ?, ?, ?, ?, 'ISSUED', ?, ?)";

    private static final String INSERT_INVOICE_ITEM_SQL =
            "INSERT INTO invoice_items (invoice_id, item_type, ref_id, description, qty, unit_price, line_total) VALUES (?, 'PLAN', ?, ?, 1, ?, ?)";

    @Override
    public long createSubscription(Connection conn, Member member, Plan plan, LocalDate startDate, LocalDate endDate, long userId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_SUBSCRIPTION_SQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, member.getId()); // Dùng POJO getter
            pstmt.setLong(2, plan.getId());
            pstmt.setDate(3, Date.valueOf(startDate));
            pstmt.setDate(4, Date.valueOf(endDate));
            pstmt.setLong(5, userId);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                throw new SQLException("Tạo subscription thất bại, không lấy được ID.");
            }
        }
    }

    @Override
    public long createInvoice(Connection conn, Member member, Plan plan, long subscriptionId, long userId) throws SQLException {
        String invoiceNo = "INV-" + System.currentTimeMillis(); // TODO: Dùng Stored Procedure
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_INVOICE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, member.getId());
            pstmt.setLong(2, subscriptionId);
            pstmt.setString(3, invoiceNo);
            pstmt.setDouble(4, plan.getPrice());
            pstmt.setDouble(5, plan.getPrice());
            pstmt.setLong(6, userId);
            pstmt.setDate(7, Date.valueOf(LocalDate.now()));
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                throw new SQLException("Tạo invoice thất bại, không lấy được ID.");
            }
        }
    }

    @Override
    public boolean createInvoiceItem(Connection conn, Plan plan, long invoiceId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_INVOICE_ITEM_SQL)) {
            pstmt.setLong(1, invoiceId);
            pstmt.setLong(2, plan.getId());
            pstmt.setString(3, plan.getName());
            pstmt.setDouble(4, plan.getPrice());
            pstmt.setDouble(5, plan.getPrice());
            return pstmt.executeUpdate() > 0;
        }
    }
}