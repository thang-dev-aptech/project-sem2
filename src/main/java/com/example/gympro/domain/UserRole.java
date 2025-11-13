package com.example.gympro.domain;

/**
 * UserRole entity - Bảng trung gian mapping User và Role
 * Một user có thể có nhiều roles (OWNER, STAFF, etc.)
 * Cần thiết để kiểm tra quyền user khi login
 */
public class UserRole {
    private Long userId;
    private User user;  // Association
    private Long roleId;
    private Role role;  // Association

    public UserRole() {
    }

    public UserRole(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
        if (user != null) {
            this.userId = user.getId();
        }
        if (role != null) {
            this.roleId = role.getId();
        }
    }

    // Getters
    public Long getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public Long getRoleId() {
        return roleId;
    }

    public Role getRole() {
        return role;
    }

    // Setters
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
        }
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public void setRole(Role role) {
        this.role = role;
        if (role != null) {
            this.roleId = role.getId();
        }
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "userId=" + userId +
                ", roleId=" + roleId +
                '}';
    }
}
