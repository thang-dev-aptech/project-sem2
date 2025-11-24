package com.example.gympro.repository.member;

import com.example.gympro.domain.member.Member;
import com.example.gympro.domain.member.MemberStatus;
import com.example.gympro.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberRepository implements MemberRepositoryInterface {

    private static final String BASE_SELECT_SQL =
            "SELECT id, branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, is_deleted, created_at, updated_at" +
                    " FROM members WHERE is_deleted = 0";

    private static final String INSERT_SQL =
            "INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
        List<Member> members = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder(BASE_SELECT_SQL);
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isBlank() && !"All".equalsIgnoreCase(status) && !"Tất cả".equalsIgnoreCase(status)) {
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

        try (Connection conn = DatabaseConnection.getConnection();
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
            System.err.println("Error loading members: " + e.getMessage());
            e.printStackTrace();
        }
        return members;
    }

    @Override
    public Optional<Member> insert(Member member) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setMemberParams(pstmt, member);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long newId = generatedKeys.getLong(1);
                        return Optional.of(member.toBuilder().id(newId).build());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding member: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Member member) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {

            pstmt.setString(1, member.getMemberCode());
            pstmt.setString(2, member.getFullName());
            pstmt.setString(3, member.getPhone());
            pstmt.setString(4, member.getEmail());
            pstmt.setString(5, member.getGender());
            pstmt.setDate(6, member.getDob() != null ? Date.valueOf(member.getDob()) : null);
            pstmt.setString(7, member.getAddress());
            pstmt.setString(8, member.getStatus().toDatabaseValue());
            pstmt.setString(9, member.getNote());
            pstmt.setLong(10, member.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(long id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting member: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Member> findById(long id) {
        String sql = BASE_SELECT_SQL + " AND id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding member: " + e.getMessage());
        }
        return Optional.empty();
    }

    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Date dob = rs.getDate("dob");
        Timestamp createdTs = rs.getTimestamp("created_at");
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        return new Member.Builder()
                .id(rs.getLong("id"))
                .branchId(rs.getLong("branch_id"))
                .memberCode(rs.getString("member_code"))
                .fullName(rs.getString("full_name"))
                .phone(rs.getString("phone"))
                .email(rs.getString("email"))
                .gender(rs.getString("gender"))
                .dob(dob != null ? dob.toLocalDate() : null)
                .address(rs.getString("address"))
                .status(MemberStatus.fromDatabase(rs.getString("status")))
                .note(rs.getString("note"))
                .deleted(rs.getBoolean("is_deleted"))
                .createdAt(createdTs != null ? createdTs.toLocalDateTime() : null)
                .updatedAt(updatedTs != null ? updatedTs.toLocalDateTime() : null)
                .build();
    }

    private void setMemberParams(PreparedStatement pstmt, Member member) throws SQLException {
        pstmt.setLong(1, member.getBranchId());
        pstmt.setString(2, member.getMemberCode());
        pstmt.setString(3, member.getFullName());
        pstmt.setString(4, member.getPhone());
        pstmt.setString(5, member.getEmail());
        pstmt.setString(6, member.getGender());
        pstmt.setDate(7, member.getDob() != null ? Date.valueOf(member.getDob()) : null);
        pstmt.setString(8, member.getAddress());
        pstmt.setString(9, member.getStatus().toDatabaseValue());
        pstmt.setString(10, member.getNote());
    }
}

