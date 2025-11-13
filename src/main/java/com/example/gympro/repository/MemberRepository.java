package com.example.gympro.repository;

import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.viewModel.Member;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberRepository implements MemberRepositoryInterface {

    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    private static final String BASE_SELECT_SQL =
            "SELECT id, branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, created_at, updated_at FROM members WHERE is_deleted = 0";

    private static final String INSERT_SQL =
            "INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    // 9 trường SET + 1 trường WHERE
    private static final String UPDATE_SQL =
            "UPDATE members SET member_code = ?, full_name = ?, phone = ?, email = ?, gender = ?, dob = ?, address = ?, status = ?, note = ? WHERE id = ?";

    private static final String DELETE_SQL =
            "UPDATE members SET is_deleted = 1, status = 'EXPIRED' WHERE id = ?";

    @Override
    public List<Member> findAll() {
        return findAll(null, null);
    }

    @Override
    public List<Member> findAll(String searchTerm, String status) {
        // ... (Hàm này giữ nguyên, không thay đổi) ...
        List<Member> members = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder(BASE_SELECT_SQL);
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isEmpty() && !"Tất cả".equals(status)) {
            sqlBuilder.append(" AND status = ?");
            params.add(status);
        }

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sqlBuilder.append(" AND (member_code LIKE ? OR full_name LIKE ? OR phone LIKE ? OR email LIKE ?)");
            String likeParam = "%" + searchTerm.trim() + "%";
            params.add(likeParam);
            params.add(likeParam);
            params.add(likeParam);
            params.add(likeParam);
        }

        sqlBuilder.append(" ORDER BY id DESC");

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    members.add(mapResultSetToMember(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải hội viên: " + e.getMessage());
            e.printStackTrace();
        }
        return members;
    }

    @Override
    public Optional<Member> insert(Member member) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            // Hàm setMemberParams đúng cho INSERT (10 tham số)
            setMemberParams(pstmt, member);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        member.setId(generatedKeys.getLong(1));
                        return Optional.of(member);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm hội viên: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // === SỬA LỖI TRONG HÀM UPDATE ===
    @Override
    public boolean update(Member member) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {

            // KHÔNG GỌI setMemberParams(pstmt, member) vì nó bao gồm cả branch_id

            // Gán 9 tham số cho 9 trường SET
            pstmt.setString(1, member.getMemberCode());
            pstmt.setString(2, member.getFullName());
            pstmt.setString(3, member.getPhone());
            pstmt.setString(4, member.getEmail());
            pstmt.setString(5, member.getGender());
            pstmt.setDate(6, member.getDob() != null ? Date.valueOf(member.getDob()) : null);
            pstmt.setString(7, member.getAddress());
            pstmt.setString(8, member.getStatus());
            pstmt.setString(9, member.getNote());

            // Gán tham số thứ 10 cho WHERE id = ?
            pstmt.setLong(10, member.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            // In ra lỗi để debug
            System.err.println("Lỗi khi cập nhật hội viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(long id) {
        // ... (Hàm này giữ nguyên, không thay đổi) ...
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa hội viên: " + e.getMessage());
            return false;
        }
    }

    // Hàm tiện ích map ResultSet
    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getLong("id"));
        member.setBranchId(rs.getLong("branch_id"));
        member.setMemberCode(rs.getString("member_code"));
        member.setFullName(rs.getString("full_name"));
        member.setPhone(rs.getString("phone"));
        member.setEmail(rs.getString("email"));
        member.setGender(rs.getString("gender"));
        Date dob = rs.getDate("dob");
        if (dob != null) {
            member.setDob(dob.toLocalDate());
        }
        member.setAddress(rs.getString("address"));
        member.setStatus(rs.getString("status"));
        member.setNote(rs.getString("note"));

        Timestamp createdTs = rs.getTimestamp("created_at");
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (createdTs != null) { member.setCreatedAt(createdTs.toLocalDateTime()); }
        if (updatedTs != null) { member.setUpdatedAt(updatedTs.toLocalDateTime()); }

        return member;
    }

    // Gán 10 tham số (Dùng cho INSERT)
    private void setMemberParams(PreparedStatement pstmt, Member member) throws SQLException {
        pstmt.setLong(1, member.getBranchId());
        pstmt.setString(2, member.getMemberCode());
        pstmt.setString(3, member.getFullName());
        pstmt.setString(4, member.getPhone());
        pstmt.setString(5, member.getEmail());
        pstmt.setString(6, member.getGender());
        pstmt.setDate(7, member.getDob() != null ? Date.valueOf(member.getDob()) : null);
        pstmt.setString(8, member.getAddress());
        pstmt.setString(9, member.getStatus());
        pstmt.setString(10, member.getNote());
    }

    @Override
    public Optional<Member> findById(long id) { return Optional.empty(); } // Chưa implement
}