package com.example.gympro.repository;

import com.example.gympro.domain.Role;
import com.example.gympro.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleRepository {

    private static final String SQL_FIND_ROLES_BY_USER_ID =
            "SELECT r.id, r.name, r.description " +
            "FROM user_roles ur JOIN roles r ON ur.role_id = r.id WHERE ur.user_id = ?";

    public List<Role> findRolesByUserId(Long userId) {
        List<Role> roles = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ROLES_BY_USER_ID)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Role role = new Role();
                    role.setId(rs.getLong("id"));
                    role.setName(rs.getString("name"));
                    role.setDescription(rs.getString("description"));
                    roles.add(role);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query roles by user id", e);
        }
        return roles;
    }
}


