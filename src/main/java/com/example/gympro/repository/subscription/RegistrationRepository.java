package com.example.gympro.repository.subscription;

import com.example.gympro.domain.member.Member;
import com.example.gympro.domain.plan.Plan;
import com.example.gympro.service.settings.SettingsService;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class RegistrationRepository implements RegistrationRepositoryInterface {

    private static final String INSERT_SUBSCRIPTION_SQL = """
            INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status, created_by, note)
            VALUES (?, ?, ?, ?, 'PENDING', ?, NULL)
            """;

    private static final String INSERT_INVOICE_SQL = """
            INSERT INTO invoices (member_id, subscription_id, invoice_no, subtotal_amount, discount_type, discount_value,
                                  total_amount, status, created_by, issue_date)
            VALUES (?, ?, ?, ?, 'NONE', 0, ?, 'ISSUED', ?, CURRENT_DATE)
            """;

    private static final String INSERT_INVOICE_ITEM_SQL = """
            INSERT INTO invoice_items (invoice_id, item_type, ref_id, description, qty, unit_price, line_total)
            VALUES (?, 'PLAN', ?, ?, 1, ?, ?)
            """;

    @Override
    public long createSubscription(Connection conn, Member member, Plan plan, LocalDate startDate, LocalDate endDate,
                                   long createdByUserId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_SUBSCRIPTION_SQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, member.getId());
            pstmt.setLong(2, plan.getId());
            pstmt.setDate(3, Date.valueOf(startDate));
            pstmt.setDate(4, Date.valueOf(endDate));
            pstmt.setLong(5, createdByUserId);
            pstmt.executeUpdate();
            try (var keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("Tạo subscription thất bại, không lấy được ID.");
            }
        }
    }

    @Override
    public long createInvoice(Connection conn, Member member, Plan plan, long subscriptionId, long createdByUserId)
            throws SQLException {
        // Lấy invoice prefix từ settings
        SettingsService settingsService = new SettingsService();
        String invoicePrefix = settingsService.getInvoicePrefix();
        String invoiceNo = invoicePrefix + "-" + System.currentTimeMillis();
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_INVOICE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, member.getId());
            pstmt.setLong(2, subscriptionId);
            pstmt.setString(3, invoiceNo);
            pstmt.setBigDecimal(4, plan.getPrice());
            pstmt.setBigDecimal(5, plan.getPrice());
            pstmt.setLong(6, createdByUserId);
            pstmt.executeUpdate();
            try (var keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
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
            pstmt.setBigDecimal(4, plan.getPrice());
            pstmt.setBigDecimal(5, plan.getPrice());
            return pstmt.executeUpdate() > 0;
        }
    }
}

