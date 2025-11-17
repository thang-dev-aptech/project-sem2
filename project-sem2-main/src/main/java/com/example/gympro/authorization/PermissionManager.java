package com.example.gympro.authorization;

import com.example.gympro.service.SessionManager;
import com.example.gympro.domain.Role;
import java.util.List;
import java.util.ArrayList;

/**
 * Class quản lý phân quyền theo vai trò
 * 
 * ADMIN/OWNER: Có 100% quyền - tất cả các chức năng
 * STAFF: Có 40% quyền - chỉ các chức năng cơ bản
 * 
 * Cách sử dụng:
 * - Kiểm tra quyền: PermissionManager.hasPermission(Permission.VIEW_MEMBERS)
 * - Kiểm tra admin: PermissionManager.isAdmin()
 * - Kiểm tra staff: PermissionManager.isStaff()
 */
public class PermissionManager {
    
    // Tên vai trò trong database
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_OWNER = "OWNER";
    private static final String ROLE_STAFF = "STAFF";
    
    /**
     * Kiểm tra người dùng hiện tại có phải Admin/Owner không
     * Admin/Owner có 100% quyền
     */
    public static boolean isAdmin() {
        SessionManager session = SessionManager.getInstance();
        if (session == null || session.getCurrentUser() == null) {
            return false;
        }
        
        List<Role> roles = session.getCurrentRoles();
        if (roles == null) {
            return false;
        }
        
        for (Role role : roles) {
            String roleName = role.getName().toUpperCase();
            if (roleName.equals(ROLE_ADMIN) || roleName.equals(ROLE_OWNER)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Kiểm tra người dùng hiện tại có phải Staff không
     * Staff chỉ có 40% quyền
     */
    public static boolean isStaff() {
        SessionManager session = SessionManager.getInstance();
        if (session == null || session.getCurrentUser() == null) {
            return false;
        }
        
        List<Role> roles = session.getCurrentRoles();
        if (roles == null) {
            return false;
        }
        
        for (Role role : roles) {
            if (role.getName().toUpperCase().equals(ROLE_STAFF)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Lấy danh sách quyền của STAFF (40% quyền)
     * Bao gồm các quyền cơ bản để làm việc hàng ngày
     */
    private static List<Permission> getStaffPermissions() {
        List<Permission> permissions = new ArrayList<>();
        
        // Staff có thể xem Dashboard
        permissions.add(Permission.VIEW_DASHBOARD);
        
        // Staff có thể xem và thêm thành viên
        permissions.add(Permission.VIEW_MEMBERS);
        permissions.add(Permission.ADD_MEMBER);
        
        // Staff có thể xem gói tập
        permissions.add(Permission.VIEW_PACKAGES);
        
        // Staff có thể xem và tạo đăng ký
        permissions.add(Permission.VIEW_REGISTRATIONS);
        permissions.add(Permission.CREATE_REGISTRATION);
        
        // Staff có thể xem và xử lý thanh toán
        permissions.add(Permission.VIEW_PAYMENTS);
        permissions.add(Permission.PROCESS_PAYMENT);
        
        // Staff có thể xem thành viên sắp hết hạn
        permissions.add(Permission.VIEW_EXPIRING_MEMBERS);
        
        return permissions;
    }
    
    /**
     * Lấy danh sách quyền của ADMIN (100% quyền)
     * Admin có tất cả các quyền trong hệ thống
     */
    private static List<Permission> getAdminPermissions() {
        List<Permission> permissions = new ArrayList<>();
        
        // Thêm tất cả các quyền
        permissions.add(Permission.VIEW_DASHBOARD);
        
        permissions.add(Permission.VIEW_MEMBERS);
        permissions.add(Permission.ADD_MEMBER);
        permissions.add(Permission.EDIT_MEMBER);
        permissions.add(Permission.DELETE_MEMBER);
        
        permissions.add(Permission.VIEW_PACKAGES);
        permissions.add(Permission.ADD_PACKAGE);
        permissions.add(Permission.EDIT_PACKAGE);
        permissions.add(Permission.DELETE_PACKAGE);
        
        permissions.add(Permission.VIEW_REGISTRATIONS);
        permissions.add(Permission.CREATE_REGISTRATION);
        
        permissions.add(Permission.VIEW_PAYMENTS);
        permissions.add(Permission.PROCESS_PAYMENT);
        
        permissions.add(Permission.VIEW_EXPIRING_MEMBERS);
        
        permissions.add(Permission.VIEW_REPORTS);
        permissions.add(Permission.EXPORT_REPORTS);
        
        permissions.add(Permission.VIEW_SETTINGS);
        permissions.add(Permission.EDIT_SETTINGS);
        
        permissions.add(Permission.VIEW_USERS);
        permissions.add(Permission.ADD_USER);
        permissions.add(Permission.EDIT_USER);
        permissions.add(Permission.DELETE_USER);
        
        return permissions;
    }
    
    /**
     * Kiểm tra người dùng hiện tại có quyền cụ thể không
     * @param permission Quyền cần kiểm tra
     * @return true nếu có quyền, false nếu không
     */
    public static boolean hasPermission(Permission permission) {
        // Admin có tất cả quyền
        if (isAdmin()) {
            return true;
        }
        
        // Staff kiểm tra trong danh sách quyền của Staff
        if (isStaff()) {
            List<Permission> staffPermissions = getStaffPermissions();
            for (Permission p : staffPermissions) {
                if (p.getName().equals(permission.getName())) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Lấy tất cả quyền của người dùng hiện tại
     * @return Danh sách quyền
     */
    public static List<Permission> getCurrentUserPermissions() {
        if (isAdmin()) {
            return getAdminPermissions();
        } else if (isStaff()) {
            return getStaffPermissions();
        }
        return new ArrayList<>();
    }
    
    /**
     * In ra tất cả quyền của người dùng hiện tại (dùng để debug)
     */
    public static void printCurrentUserPermissions() {
        SessionManager session = SessionManager.getInstance();
        if (session.getCurrentUser() == null) {
            System.out.println("Chưa đăng nhập");
            return;
        }
        
        System.out.println("=== QUYỀN CỦA: " + session.getCurrentUser().getFullName() + " ===");
        
        if (isAdmin()) {
            System.out.println("Vai trò: ADMIN/OWNER (100% quyền)");
        } else if (isStaff()) {
            System.out.println("Vai trò: STAFF (40% quyền)");
        }
        
        List<Permission> permissions = getCurrentUserPermissions();
        System.out.println("Tổng số quyền: " + permissions.size());
        for (Permission p : permissions) {
            System.out.println("  - " + p);
        }
    }
}
