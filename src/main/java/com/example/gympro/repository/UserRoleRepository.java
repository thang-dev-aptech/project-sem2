package com.example.gympro.repository;

import com.example.gympro.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * UserRoleRepository - Quản lý bảng user_roles (many-to-many)
 */
public class UserRoleRepository {

    private static final String SQL_DELETE_BY_USER_ID =
            "DELETE FROM user_roles WHERE user_id = ?";

    private static final String SQL_INSERT =
            "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";

    /**
     * Xóa tất cả roles của user
     */
    public boolean deleteByUserId(Long userId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_BY_USER_ID)) {
            ps.setLong(1, userId);
            return ps.executeUpdate() >= 0; // Có thể không có role nào
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user roles", e);
        }
    }

    /**
     * Gán roles cho user (xóa cũ và thêm mới)
     */
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Xóa roles cũ
                deleteByUserId(userId);

                // Thêm roles mới
                if (roleIds != null && !roleIds.isEmpty()) {
                    try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
                        for (Long roleId : roleIds) {
                            ps.setLong(1, userId);
                            ps.setLong(2, roleId);
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to assign roles to user", e);
        }
    }
}

