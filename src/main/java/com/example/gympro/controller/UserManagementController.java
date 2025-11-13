package com.example.gympro.controller;

import com.example.gympro.service.PermissionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller cho quản lý Users (Nhân viên)
 * CHỈ ADMIN mới được truy cập màn hình này
 */
public class UserManagementController {
    
    @FXML
    private VBox mainContainer;
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private void initialize() {
        // Kiểm tra quyền ngay khi khởi tạo
        if (!PermissionManager.canManageUsers()) {
            showAccessDenied();
            disableAllControls();
        }
    }
    
    private void showAccessDenied() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Không có quyền truy cập");
        alert.setHeaderText("Quyền truy cập bị từ chối");
        alert.setContentText("Bạn không có quyền quản lý Users.\n" +
                           "Chỉ ADMIN mới có quyền truy cập chức năng này.\n\n" +
                           "Role hiện tại: " + PermissionManager.getCurrentRoleName() + 
                           " (Quyền: " + PermissionManager.getAccessPercentage() + "%)");
        alert.showAndWait();
    }
    
    private void disableAllControls() {
        if (mainContainer != null) {
            mainContainer.setDisable(true);
        }
    }
}
