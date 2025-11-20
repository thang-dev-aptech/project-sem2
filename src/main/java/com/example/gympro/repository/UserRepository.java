package com.example.gympro.repository;

import com.example.gympro.domain.Branch;
import com.example.gympro.domain.User;
import com.example.gympro.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {

    private static final String SQL_FIND_BY_USERNAME =
            "SELECT u.id, u.branch_id, u.username, u.full_name, u.email, u.phone, u.password_hash, u.is_active, u.last_login_at, u.created_at, u.updated_at, " +
            "       b.code as branch_code, b.name as branch_name, b.address as branch_address, b.phone as branch_phone, b.created_at as b_created_at, b.updated_at as b_updated_at " +
            "FROM users u JOIN branches b ON u.branch_id = b.id WHERE u.username = ?";

    private static final String SQL_FIND_ALL =
            "SELECT u.id, u.branch_id, u.username, u.full_name, u.email, u.phone, u.password_hash, u.is_active, u.last_login_at, u.created_at, u.updated_at, " +
            "       b.code as branch_code, b.name as branch_name " +
            "FROM users u JOIN branches b ON u.branch_id = b.id ORDER BY u.created_at DESC";

    private static final String SQL_FIND_BY_ID =
            "SELECT u.id, u.branch_id, u.username, u.full_name, u.email, u.phone, u.password_hash, u.is_active, u.last_login_at, u.created_at, u.updated_at, " +
            "       b.code as branch_code, b.name as branch_name " +
            "FROM users u JOIN branches b ON u.branch_id = b.id WHERE u.id = ?";

    private static final String SQL_INSERT =
            "INSERT INTO users (branch_id, username, full_name, email, phone, password_hash, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE users SET full_name = ?, email = ?, phone = ?, is_active = ? WHERE id = ?";

    private static final String SQL_UPDATE_PASSWORD =
            "UPDATE users SET password_hash = ? WHERE id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM users WHERE id = ?";

    public User findByUsername(String username) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query user by username", e);
        }
        return null;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query all users", e);
        }
        return users;
    }

    public Optional<User> findById(Long id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query user by id", e);
        }
        return Optional.empty();
    }

    public Optional<User> insert(User user) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, user.getBranchId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getPasswordHash());
            ps.setBoolean(7, user.getIsActive() != null ? user.getIsActive() : true);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return Optional.empty();
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user", e);
        }
        return Optional.empty();
    }

    public boolean update(User user) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setBoolean(4, user.getIsActive() != null ? user.getIsActive() : true);
            ps.setLong(5, user.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    public boolean updatePassword(Long userId, String passwordHash) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_PASSWORD)) {
            ps.setString(1, passwordHash);
            ps.setLong(2, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update password", e);
        }
    }

    public boolean delete(Long id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setBranchId(rs.getLong("branch_id"));
        user.setUsername(rs.getString("username"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setIsActive(rs.getBoolean("is_active"));

        Timestamp lastLogin = rs.getTimestamp("last_login_at");
        if (lastLogin != null) {
            user.setLastLoginAt(lastLogin.toLocalDateTime());
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        // Map branch nếu có
        try {
            Branch branch = new Branch();
            branch.setId(rs.getLong("branch_id"));
            if (rs.getString("branch_code") != null) {
                branch.setCode(rs.getString("branch_code"));
                branch.setName(rs.getString("branch_name"));
            }
            user.setBranch(branch);
        } catch (SQLException e) {
            // Branch không bắt buộc
        }

        return user;
    }
}


