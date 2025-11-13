package com.example.gympro.repository;

import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.viewModel.Invoice;
import com.example.gympro.viewModel.Member;
import com.example.gympro.viewModel.Plan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository implements PaymentRepositoryInterface {

    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    // Hằng số SQL
    private static final String SELECT_UNPAID_INVOICES_SQL =
            "SELECT " +
                    "i.id AS invoice_id, i.invoice_no, i.total_amount, i.issue_date, s.id AS sub_id, " +
                    "m.id AS mem_id, m.branch_id, m.member_code, m.full_name, m.phone, m.email, m.gender, m.dob, m.address, m.status AS mem_status, m.note, m.created_at, m.updated_at, " +
                    "pl.id AS plan_id, pl.name AS plan_name, pl.price, pl.duration_days " +
                    "FROM invoices i " +
                    "JOIN subscriptions s ON i.subscription_id = s.id " +
                    "JOIN members m ON s.member_id = m.id " +
                    "JOIN plans pl ON s.plan_id = pl.id " +
                    "LEFT JOIN payments p ON i.id = p.invoice_id " +
                    "WHERE i.status = 'ISSUED' AND p.id IS NULL";

    private static final String INSERT_PAYMENT_SQL =
            "INSERT INTO payments (invoice_id, method_id, shift_id, paid_amount, created_by, paid_at) VALUES (?, ?, ?, ?, ?, NOW())";

    private static final String UPDATE_SUBSCRIPTION_STATUS_SQL =
            "UPDATE subscriptions SET status = 'ACTIVE' WHERE id = ?";

    @Override
    public List<Invoice> findUnpaidInvoices() {
        List<Invoice> unpaidList = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_UNPAID_INVOICES_SQL);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                unpaidList.add(mapResultSetToInvoice(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return unpaidList;
    }

    @Override
    public boolean createPayment(Connection conn, Invoice invoice, long methodId, long shiftId, long userId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_PAYMENT_SQL)) {
            pstmt.setLong(1, invoice.getInvoiceId());
            pstmt.setLong(2, methodId);
            if (shiftId > 0) {
                pstmt.setLong(3, shiftId); // Nếu có ID thực (ví dụ 1, 2...), lưu bình thường
            } else {
                pstmt.setObject(3, null);  // Nếu là 0, lưu là NULL trong Database
            }
            pstmt.setDouble(4, invoice.getTotalAmount());
            pstmt.setLong(5, userId);
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

    // Hàm helper để tạo đối tượng Invoice từ JOIN
    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        // 1. Tạo Member (dùng hàm map của MemberRepository)
        // (Để đơn giản, chúng ta map tại đây, nhưng lý tưởng nhất là gọi `MemberRepository.mapResultSetToMember(rs)`)
        Member member = new Member();
        member.setId(rs.getLong("mem_id"));
        member.setBranchId(rs.getLong("branch_id"));
        member.setMemberCode(rs.getString("member_code"));
        member.setFullName(rs.getString("full_name"));
        member.setPhone(rs.getString("phone"));
        member.setEmail(rs.getString("email"));
        // (Bạn có thể set thêm các trường khác của Member nếu cần)

        // 2. Tạo Plan
        Plan plan = new Plan(
                rs.getLong("plan_id"),
                rs.getString("plan_name"),
                rs.getDouble("price"),
                rs.getInt("duration_days")
        );

        // 3. Tạo Invoice
        return new Invoice(
                rs.getLong("invoice_id"),
                rs.getString("invoice_no"),
                rs.getDouble("total_amount"),
                rs.getDate("issue_date").toLocalDate(),
                rs.getLong("sub_id"),
                member,
                plan
        );
    }
}