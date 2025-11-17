package com.example.gympro.repository;

import com.example.gympro.domain.Branch;
import com.example.gympro.domain.User;
import com.example.gympro.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    private static final String SQL_FIND_BY_USERNAME =
            "SELECT u.id, u.branch_id, u.username, u.full_name, u.email, u.phone, u.password_hash, u.is_active, u.last_login_at, u.created_at, u.updated_at, " +
            "       b.code as branch_code, b.name as branch_name, b.address as branch_address, b.phone as branch_phone, b.created_at as b_created_at, b.updated_at as b_updated_at " +
            "FROM users u JOIN branches b ON u.branch_id = b.id WHERE u.username = ?";

    public User findByUsername(String username) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setBranchId(rs.getLong("branch_id"));
                    user.setUsername(rs.getString("username"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setIsActive(rs.getBoolean("is_active"));

                    Branch branch = new Branch();
                    branch.setId(rs.getLong("branch_id"));
                    branch.setCode(rs.getString("branch_code"));
                    branch.setName(rs.getString("branch_name"));
                    branch.setAddress(rs.getString("branch_address"));
                    branch.setPhone(rs.getString("branch_phone"));
                    user.setBranch(branch);
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query user by username", e);
        }
        return null;
    }
}


