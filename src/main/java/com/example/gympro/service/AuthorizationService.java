package com.example.gympro.service;

/**
 * AuthorizationService - Kiểm tra quyền truy cập của user
 * 
 * Quy tắc phân quyền:
 * - OWNER: Toàn quyền (CRUD tất cả)
 * - STAFF: Chỉ xem và tạo mới, không được xóa/sửa một số module nhạy cảm
 */
public class AuthorizationService {
    
    private final SessionManager sessionManager;
    
    public AuthorizationService() {
        this.sessionManager = SessionManager.getInstance();
    }
    
    /**
     * Kiểm tra user có role cụ thể không
     */
    public boolean hasRole(String roleName) {
        return sessionManager.hasRole(roleName);
    }
    
    /**
     * Kiểm tra user có phải OWNER không
     */
    public boolean isOwner() {
        return hasRole("OWNER");
    }
    
    /**
     * Kiểm tra user có phải STAFF không
     */
    public boolean isStaff() {
        return hasRole("STAFF");
    }
    
    /**
     * Kiểm tra user có quyền xem (tất cả roles đều có)
     */
    public boolean canView() {
        return sessionManager.getCurrentUser() != null;
    }
    
    /**
     * Kiểm tra user có quyền tạo mới
     * OWNER: Có, STAFF: Có (trừ một số module đặc biệt)
     */
    public boolean canCreate() {
        return canView();
    }
    
    /**
     * Kiểm tra user có quyền chỉnh sửa
     * OWNER: Có, STAFF: Có (trừ một số module đặc biệt)
     */
    public boolean canEdit() {
        return canView();
    }
    
    /**
     * Kiểm tra user có quyền xóa
     * OWNER: Có, STAFF: Không
     */
    public boolean canDelete() {
        return isOwner();
    }
    
    /**
     * Kiểm tra user có quyền quản lý gói tập (Packages)
     * OWNER: Toàn quyền, STAFF: Chỉ xem
     */
    public boolean canManagePackages() {
        return isOwner();
    }
    
    /**
     * Kiểm tra user có quyền quản lý cấu hình hệ thống
     * Chỉ OWNER mới có quyền
     */
    public boolean canManageSettings() {
        return isOwner();
    }
    
    /**
     * Kiểm tra user có quyền xem báo cáo
     * OWNER: Có, STAFF: Có
     */
    public boolean canViewReports() {
        return canView();
    }
    
    /**
     * Kiểm tra user có quyền xuất báo cáo/Excel
     * OWNER: Có, STAFF: Có
     */
    public boolean canExportReports() {
        return canView();
    }
    
    /**
     * Kiểm tra user có quyền áp dụng chiết khấu
     * OWNER: Có, STAFF: Có (nhưng có thể giới hạn %)
     */
    public boolean canApplyDiscount() {
        return canView();
    }
    
    /**
     * Hiển thị thông báo lỗi khi không có quyền
     */
    public void showAccessDeniedAlert() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.WARNING
        );
        alert.setTitle("Không có quyền truy cập");
        alert.setHeaderText(null);
        alert.setContentText("Bạn không có quyền thực hiện thao tác này. Vui lòng liên hệ quản trị viên.");
        alert.showAndWait();
    }
}

