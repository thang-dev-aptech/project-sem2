package com.example.gympro.repository;

import com.example.gympro.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * UserRoleRepository - Manages user_roles table (many-to-many)
 */
public class UserRoleRepository {

    private static final String SQL_DELETE_BY_USER_ID =
            "DELETE FROM user_roles WHERE user_id = ?";

    private static final String SQL_INSERT =
            "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";

    /**
     * Delete all roles of a user
     */
    public boolean deleteByUserId(Long userId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_BY_USER_ID)) {
            ps.setLong(1, userId);
            return ps.executeUpdate() >= 0; // May have no roles
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user roles", e);
        }
    }

    /**
     * Assign roles to user (delete old and add new)
     */
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete old roles
                deleteByUserId(userId);

                // Add new roles
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

