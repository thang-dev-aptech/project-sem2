package com.example.gympro.service;

import com.example.gympro.domain.Role;
import java.util.List;

/**
 * PermissionManager - Quản lý phân quyền đơn giản cho hệ thống
 * 
 * Chức năng chính:
 * - ADMIN: Có toàn quyền (100% chức năng)
 * - STAFF: Chỉ có quyền hạn chế (40% chức năng)
 * 
 * ADMIN được quyền:
 * - Xem Dashboard
 * - Quản lý Members (thêm, sửa, xóa)
 * - Quản lý Packages
 * - Đăng ký thành viên
 * - Quản lý Payment
 * - Xem Expiring Members
 * - Xem Reports
 * - Quản lý Settings
 * - Quản lý Users (thêm/xóa nhân viên)
 * 
 * STAFF được quyền:
 * - Xem Dashboard (chỉ đọc)
 * - Xem Members (chỉ đọc)
 * - Đăng ký thành viên mới
 * - Quản lý Payment
 * 
 * STAFF KHÔNG được quyền:
 * - Xóa/sửa Members
 * - Quản lý Packages
 * - Xem Reports
 * - Quản lý Settings
 * - Quản lý Users
 */
public class PermissionManager {
    
    /**
     * Kiểm tra xem user hiện tại có phải là ADMIN không
     * Chấp nhận cả ADMIN và OWNER (để tương thích với dữ liệu cũ)
     */
    public static boolean isAdmin() {
        List<Role> roles = SessionManager.getInstance().getCurrentRoles();
        if (roles == null) return false;
        return roles.stream().anyMatch(role -> 
            "ADMIN".equalsIgnoreCase(role.getName()) || 
            "OWNER".equalsIgnoreCase(role.getName())
        );
    }
    
    /**
     * Kiểm tra xem user hiện tại có phải là STAFF không
     */
    public static boolean isStaff() {
        List<Role> roles = SessionManager.getInstance().getCurrentRoles();
        if (roles == null) return false;
        return roles.stream().anyMatch(role -> "STAFF".equalsIgnoreCase(role.getName()));
    }
    
    /**
     * Kiểm tra quyền xem Dashboard
     * Cả ADMIN và STAFF đều được xem
     */
    public static boolean canViewDashboard() {
        return isAdmin() || isStaff();
    }
    
    /**
     * Kiểm tra quyền xem Members
     * Cả ADMIN và STAFF đều được xem
     */
    public static boolean canViewMembers() {
        return isAdmin() || isStaff();
    }
    
    /**
     * Kiểm tra quyền chỉnh sửa/xóa Members
     * CHỈ ADMIN mới được phép
     */
    public static boolean canEditMembers() {
        return isAdmin();
    }
    
    /**
     * Kiểm tra quyền quản lý Packages (thêm, sửa, xóa gói tập)
     * CHỈ ADMIN mới được phép
     */
    public static boolean canManagePackages() {
        return isAdmin();
    }
    
    /**
     * Kiểm tra quyền đăng ký thành viên mới
     * Cả ADMIN và STAFF đều được phép
     */
    public static boolean canRegisterMembers() {
        return isAdmin() || isStaff();
    }
    
    /**
     * Kiểm tra quyền quản lý thanh toán
     * Cả ADMIN và STAFF đều được phép
     */
    public static boolean canManagePayments() {
        return isAdmin() || isStaff();
    }
    
    /**
     * Kiểm tra quyền xem danh sách thành viên sắp hết hạn
     * CHỈ ADMIN mới được xem
     */
    public static boolean canViewExpiringMembers() {
        return isAdmin();
    }
    
    /**
     * Kiểm tra quyền xem báo cáo
     * CHỈ ADMIN mới được xem
     */
    public static boolean canViewReports() {
        return isAdmin();
    }
    
    /**
     * Kiểm tra quyền quản lý cài đặt hệ thống
     * CHỈ ADMIN mới được phép
     */
    public static boolean canManageSettings() {
        return isAdmin();
    }
    
    /**
     * Kiểm tra quyền quản lý users (thêm/xóa nhân viên)
     * CHỈ ADMIN mới được phép
     */
    public static boolean canManageUsers() {
        return isAdmin();
    }
    
    /**
     * Lấy tên role hiện tại để hiển thị
     */
    public static String getCurrentRoleName() {
        if (isAdmin()) return "ADMIN";
        if (isStaff()) return "STAFF";
        return "GUEST";
    }
    
    /**
     * Lấy phần trăm quyền truy cập
     * ADMIN: 100%
     * STAFF: 40%
     */
    public static int getAccessPercentage() {
        if (isAdmin()) return 100;
        if (isStaff()) return 40;
        return 0;
    }
}
