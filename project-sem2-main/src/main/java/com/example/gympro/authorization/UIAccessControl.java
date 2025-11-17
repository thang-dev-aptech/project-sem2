package com.example.gympro.authorization;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Class điều khiển giao diện theo quyền hạn
 * Giúp ẩn/hiện các button, menu, chức năng theo vai trò
 * 
 * Cách sử dụng trong Controller:
 * 
 * 1. Ẩn button nếu không có quyền:
 *    UIAccessControl.hideIfNoPermission(deleteButton, Permission.DELETE_MEMBER);
 * 
 * 2. Vô hiệu hóa button nếu không có quyền:
 *    UIAccessControl.disableIfNoPermission(editButton, Permission.EDIT_MEMBER);
 * 
 * 3. Kiểm tra quyền trước khi thực hiện action:
 *    if (UIAccessControl.checkPermissionWithAlert(Permission.DELETE_MEMBER)) {
 *        // Thực hiện xóa
 *    }
 */
public class UIAccessControl {
    
    /**
     * Ẩn một Node (Button, MenuItem, v.v.) nếu không có quyền
     * Node sẽ hoàn toàn biến mất khỏi giao diện
     * 
     * @param node Node cần ẩn
     * @param permission Quyền cần thiết
     */
    public static void hideIfNoPermission(Node node, Permission permission) {
        if (node == null) return;
        
        boolean hasPermission = PermissionManager.hasPermission(permission);
        node.setVisible(hasPermission);
        node.setManaged(hasPermission);  // Không chiếm chỗ trong layout
    }
    
    /**
     * Vô hiệu hóa một Node nếu không có quyền
     * Node vẫn hiển thị nhưng bị mờ đi và không click được
     * 
     * @param node Node cần vô hiệu hóa
     * @param permission Quyền cần thiết
     */
    public static void disableIfNoPermission(Node node, Permission permission) {
        if (node == null) return;
        
        boolean hasPermission = PermissionManager.hasPermission(permission);
        node.setDisable(!hasPermission);
    }
    
    /**
     * Ẩn MenuItem nếu không có quyền
     * 
     * @param menuItem MenuItem cần ẩn
     * @param permission Quyền cần thiết
     */
    public static void hideMenuItemIfNoPermission(MenuItem menuItem, Permission permission) {
        if (menuItem == null) return;
        
        boolean hasPermission = PermissionManager.hasPermission(permission);
        menuItem.setVisible(hasPermission);
    }
    
    /**
     * Kiểm tra quyền và hiển thị cảnh báo nếu không có quyền
     * Dùng trước khi thực hiện một hành động
     * 
     * @param permission Quyền cần kiểm tra
     * @return true nếu có quyền, false nếu không (đã hiển thị cảnh báo)
     */
    public static boolean checkPermissionWithAlert(Permission permission) {
        if (PermissionManager.hasPermission(permission)) {
            return true;
        }
        
        // Hiển thị cảnh báo
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Không có quyền");
        alert.setHeaderText("Bạn không có quyền thực hiện chức năng này");
        alert.setContentText("Quyền cần thiết: " + permission.getDescription() + 
                            "\n\nVui lòng liên hệ quản trị viên để được cấp quyền.");
        alert.showAndWait();
        
        return false;
    }
    
    /**
     * Kiểm tra quyền và xác nhận hành động nguy hiểm
     * Dùng cho các hành động như xóa, sửa quan trọng
     * 
     * @param permission Quyền cần kiểm tra
     * @param confirmMessage Thông điệp xác nhận
     * @return true nếu có quyền VÀ người dùng xác nhận, false nếu không
     */
    public static boolean checkPermissionAndConfirm(Permission permission, String confirmMessage) {
        // Kiểm tra quyền trước
        if (!PermissionManager.hasPermission(permission)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Không có quyền");
            alert.setHeaderText("Bạn không có quyền thực hiện chức năng này");
            alert.setContentText("Quyền cần thiết: " + permission.getDescription());
            alert.showAndWait();
            return false;
        }
        
        // Có quyền, hỏi xác nhận
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Bạn có chắc chắn muốn thực hiện?");
        alert.setContentText(confirmMessage);
        
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
    
    /**
     * Hiển thị thông báo vai trò hiện tại
     * Dùng để debug hoặc thông báo cho user biết họ đang ở vai trò nào
     */
    public static void showCurrentRoleInfo() {
        String roleInfo;
        if (PermissionManager.isAdmin()) {
            roleInfo = "Vai trò: ADMIN/OWNER\nQuyền hạn: 100% - Toàn quyền quản lý hệ thống";
        } else if (PermissionManager.isStaff()) {
            roleInfo = "Vai trò: STAFF\nQuyền hạn: 40% - Các chức năng cơ bản";
        } else {
            roleInfo = "Vai trò: Không xác định\nVui lòng đăng nhập lại";
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông tin vai trò");
        alert.setHeaderText("Thông tin phân quyền của bạn");
        alert.setContentText(roleInfo);
        alert.showAndWait();
    }
    
    /**
     * Áp dụng phân quyền cho nhiều button cùng lúc
     * Tiện dụng khi có nhiều button cùng quyền
     * 
     * @param permission Quyền chung
     * @param nodes Các node cần áp dụng phân quyền
     */
    public static void applyPermissionToMultiple(Permission permission, Node... nodes) {
        for (Node node : nodes) {
            hideIfNoPermission(node, permission);
        }
    }
}
