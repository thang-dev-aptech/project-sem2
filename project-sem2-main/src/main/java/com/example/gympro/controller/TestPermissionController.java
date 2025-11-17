package com.example.gympro.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import com.example.gympro.authorization.Permission;
import com.example.gympro.authorization.PermissionManager;
import com.example.gympro.authorization.UIAccessControl;
import com.example.gympro.service.SessionManager;

/**
 * Controller mẫu để TEST PHÂN QUYỀN
 * 
 * File này dùng để DEMO - không ảnh hưởng code cũ
 * 
 * CÁCH SỬ DỤNG:
 * 1. Tạo file FXML tương ứng (hoặc dùng trong MainController)
 * 2. Login bằng Admin → thấy tất cả button
 * 3. Login bằng Staff → chỉ thấy button "Staff Can See"
 */
public class TestPermissionController {
    
    @FXML
    private Label roleLabel;
    
    @FXML
    private Label permissionCountLabel;
    
    @FXML
    private Button adminOnlyButton;
    
    @FXML
    private Button staffCanSeeButton;
    
    @FXML
    private Button deleteButton;
    
    @FXML
    private Button editButton;
    
    @FXML
    private Button viewReportsButton;
    
    @FXML
    private void initialize() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║     TEST PHÂN QUYỀN - GYMPRO             ║");
        System.out.println("╚════════════════════════════════════════════╝\n");
        
        // Hiển thị thông tin user hiện tại
        displayUserInfo();
        
        // Áp dụng phân quyền vào các button
        applyPermissions();
        
        // In ra console để debug
        debugPermissions();
    }
    
    /**
     * Hiển thị thông tin vai trò của user hiện tại
     */
    private void displayUserInfo() {
        SessionManager session = SessionManager.getInstance();
        
        if (session.getCurrentUser() == null) {
            roleLabel.setText("⚠️ Chưa đăng nhập");
            permissionCountLabel.setText("0 quyền");
            return;
        }
        
        String userName = session.getCurrentUser().getFullName();
        int permissionCount = PermissionManager.getCurrentUserPermissions().size();
        
        if (PermissionManager.isAdmin()) {
            roleLabel.setText("👑 " + userName + " - ADMIN/OWNER");
            permissionCountLabel.setText(permissionCount + "/23 quyền (100%)");
        } else if (PermissionManager.isStaff()) {
            roleLabel.setText("👤 " + userName + " - STAFF");
            permissionCountLabel.setText(permissionCount + "/23 quyền (40%)");
        } else {
            roleLabel.setText("❓ " + userName + " - Không xác định");
            permissionCountLabel.setText(permissionCount + " quyền");
        }
    }
    
    /**
     * Áp dụng phân quyền vào các button
     * Button sẽ tự động ẩn nếu user không có quyền
     */
    private void applyPermissions() {
        // Button chỉ Admin mới thấy
        if (adminOnlyButton != null) {
            UIAccessControl.hideIfNoPermission(adminOnlyButton, Permission.DELETE_USER);
        }
        
        // Button Delete - chỉ Admin
        if (deleteButton != null) {
            UIAccessControl.hideIfNoPermission(deleteButton, Permission.DELETE_MEMBER);
        }
        
        // Button Edit - chỉ Admin
        if (editButton != null) {
            UIAccessControl.hideIfNoPermission(editButton, Permission.EDIT_MEMBER);
        }
        
        // Button View Reports - chỉ Admin
        if (viewReportsButton != null) {
            UIAccessControl.hideIfNoPermission(viewReportsButton, Permission.VIEW_REPORTS);
        }
        
        // staffCanSeeButton - cả Admin và Staff đều thấy (không cần kiểm tra)
        
        System.out.println("✅ Đã áp dụng phân quyền vào các button");
    }
    
    /**
     * In thông tin phân quyền ra console để debug
     */
    private void debugPermissions() {
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.println("│ THÔNG TIN PHÂN QUYỀN                    │");
        System.out.println("└─────────────────────────────────────────┘");
        
        SessionManager session = SessionManager.getInstance();
        if (session.getCurrentUser() != null) {
            System.out.println("User: " + session.getCurrentUser().getFullName());
            System.out.println("Username: " + session.getCurrentUser().getUsername());
        }
        
        System.out.println("\nVai trò:");
        if (PermissionManager.isAdmin()) {
            System.out.println("  ✅ ADMIN/OWNER");
        } else if (PermissionManager.isStaff()) {
            System.out.println("  ✅ STAFF");
        } else {
            System.out.println("  ❌ Không xác định");
        }
        
        System.out.println("\n" + "=".repeat(45));
        PermissionManager.printCurrentUserPermissions();
        System.out.println("=".repeat(45) + "\n");
    }
    
    // ==========================================
    // CÁC HÀM XỬ LÝ SỰ KIỆN BUTTON
    // ==========================================
    
    @FXML
    private void handleAdminOnly() {
        showAlert(AlertType.INFORMATION, 
            "Admin Only", 
            "Chức năng này chỉ Admin mới thấy!\n\nNếu bạn là Staff, bạn sẽ không thấy button này.");
    }
    
    @FXML
    private void handleStaffCanSee() {
        showAlert(AlertType.INFORMATION, 
            "Staff Can See", 
            "Chức năng này cả Admin và Staff đều thấy!\n\nĐây là các chức năng cơ bản hàng ngày.");
    }
    
    @FXML
    private void handleDelete() {
        if (UIAccessControl.checkPermissionAndConfirm(
            Permission.DELETE_MEMBER,
            "Xóa thành viên là hành động nguy hiểm!\nBạn có chắc chắn muốn xóa?")) {
            
            showAlert(AlertType.INFORMATION, 
                "Xóa thành công", 
                "Đã xóa thành viên (đây chỉ là demo)");
        }
    }
    
    @FXML
    private void handleEdit() {
        if (!UIAccessControl.checkPermissionWithAlert(Permission.EDIT_MEMBER)) {
            return;
        }
        
        showAlert(AlertType.INFORMATION, 
            "Sửa thành viên", 
            "Bạn có quyền sửa thành viên!");
    }
    
    @FXML
    private void handleViewReports() {
        if (!UIAccessControl.checkPermissionWithAlert(Permission.VIEW_REPORTS)) {
            return;
        }
        
        showAlert(AlertType.INFORMATION, 
            "Xem báo cáo", 
            "Bạn có quyền xem báo cáo!");
    }
    
    @FXML
    private void handleShowRoleInfo() {
        UIAccessControl.showCurrentRoleInfo();
    }
    
    @FXML
    private void handleTestPermission() {
        StringBuilder message = new StringBuilder();
        message.append("TEST CÁC QUYỀN THƯỜNG DÙNG:\n\n");
        
        // Test một số quyền
        message.append("✓ Xem thành viên: ")
               .append(PermissionManager.hasPermission(Permission.VIEW_MEMBERS) ? "✅" : "❌")
               .append("\n");
        
        message.append("✓ Thêm thành viên: ")
               .append(PermissionManager.hasPermission(Permission.ADD_MEMBER) ? "✅" : "❌")
               .append("\n");
        
        message.append("✓ Sửa thành viên: ")
               .append(PermissionManager.hasPermission(Permission.EDIT_MEMBER) ? "✅" : "❌")
               .append("\n");
        
        message.append("✓ Xóa thành viên: ")
               .append(PermissionManager.hasPermission(Permission.DELETE_MEMBER) ? "✅" : "❌")
               .append("\n");
        
        message.append("✓ Xem báo cáo: ")
               .append(PermissionManager.hasPermission(Permission.VIEW_REPORTS) ? "✅" : "❌")
               .append("\n");
        
        message.append("✓ Quản lý người dùng: ")
               .append(PermissionManager.hasPermission(Permission.VIEW_USERS) ? "✅" : "❌")
               .append("\n");
        
        showAlert(AlertType.INFORMATION, "Kết quả kiểm tra quyền", message.toString());
    }
    
    /**
     * Helper method để hiển thị Alert
     */
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
