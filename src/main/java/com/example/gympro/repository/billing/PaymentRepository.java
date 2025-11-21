package com.example.gympro.repository.billing;

import com.example.gympro.domain.billing.Invoice;
import com.example.gympro.domain.billing.InvoiceStatus;
import com.example.gympro.domain.billing.Payment;
import com.example.gympro.domain.member.Member;
import com.example.gympro.domain.member.MemberStatus;
import com.example.gympro.domain.plan.Plan;
import com.example.gympro.utils.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository implements PaymentRepositoryInterface {

    private static final String SELECT_UNPAID_INVOICES_SQL = """
            SELECT i.id AS invoice_id,
                   i.invoice_no,
                   i.total_amount,
                   i.issue_date,
                   i.subscription_id,
                   m.id AS mem_id,
                   m.branch_id,
                   m.member_code,
                   m.full_name,
                   m.phone,
                   m.email,
                   m.gender,
                   m.dob,
                   m.address,
                   m.status AS member_status,
                   p.id AS plan_id,
                   p.code AS plan_code,
                   p.name AS plan_name,
                   p.description AS plan_description,
                   p.price AS plan_price,
                   p.duration_days
            FROM invoices i
            JOIN members m ON i.member_id = m.id
            JOIN subscriptions s ON i.subscription_id = s.id
            JOIN plans p ON s.plan_id = p.id
            WHERE i.status = 'ISSUED'
              AND NOT EXISTS (
                    SELECT 1 FROM payments pay
                    WHERE pay.invoice_id = i.id
                      AND pay.is_refund = 0
              )
            ORDER BY i.issue_date DESC
            """;

    private static final String INSERT_PAYMENT_SQL = """
            INSERT INTO payments (invoice_id, method_id, paid_amount, created_by, reference_code, is_refund,
                                   refund_of_payment_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_SUBSCRIPTION_STATUS_SQL = """
            UPDATE subscriptions SET status = 'ACTIVE' WHERE id = ?
            """;

    @Override
    public List<Invoice> findUnpaidInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_UNPAID_INVOICES_SQL);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    @Override
    public boolean createPayment(Connection conn, Payment payment) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_PAYMENT_SQL)) {
            pstmt.setLong(1, payment.getInvoiceId());
            pstmt.setLong(2, payment.getMethodId());
            pstmt.setBigDecimal(3, payment.getPaidAmount());
            pstmt.setLong(4, payment.getCreatedBy());
            if (payment.getReferenceCode() != null) {
                pstmt.setString(5, payment.getReferenceCode());
            } else {
                pstmt.setNull(5, Types.VARCHAR);
            }
            pstmt.setBoolean(6, payment.isRefund());
            if (payment.getRefundOfPaymentId() != null) {
                pstmt.setLong(7, payment.getRefundOfPaymentId());
            } else {
                pstmt.setNull(7, Types.BIGINT);
            }
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateSubscriptionStatus(Connection conn, long subscriptionId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_SUBSCRIPTION_STATUS_SQL)) {
            pstmt.setLong(1, subscriptionId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        java.sql.Date memberDobDate = rs.getDate("dob");
        Member member = new Member.Builder()
                .id(rs.getLong("mem_id"))
                .branchId(rs.getLong("branch_id"))
                .memberCode(rs.getString("member_code"))
                .fullName(rs.getString("full_name"))
                .phone(rs.getString("phone"))
                .email(rs.getString("email"))
                .gender(rs.getString("gender"))
                .dob(memberDobDate != null ? memberDobDate.toLocalDate() : null)
                .address(rs.getString("address"))
                .status(MemberStatus.fromDatabase(rs.getString("member_status")))
                .build();

        Plan plan = new Plan.Builder()
                .id(rs.getLong("plan_id"))
                .code(rs.getString("plan_code"))
                .name(rs.getString("plan_name"))
                .description(rs.getString("plan_description"))
                .price(rs.getBigDecimal("plan_price"))
                .durationDays(rs.getInt("duration_days"))
                .build();

        long subscriptionId = rs.getLong("subscription_id");
        if (rs.wasNull()) {
            subscriptionId = 0;
        }

        return new Invoice.Builder()
                .id(rs.getLong("invoice_id"))
                .invoiceNo(rs.getString("invoice_no"))
                .subscriptionId(subscriptionId == 0 ? null : subscriptionId)
                .issueDate(rs.getDate("issue_date").toLocalDate())
                .totalAmount(rs.getBigDecimal("total_amount"))
                .status(InvoiceStatus.ISSUED)
                .member(member)
                .plan(plan)
                .build();
    }
}

