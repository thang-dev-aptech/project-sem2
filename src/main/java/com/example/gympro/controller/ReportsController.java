package com.example.gympro.controller;

import com.example.gympro.service.PermissionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

/**
 * Controller cho Reports (Báo cáo)
 * CHỈ ADMIN mới được truy cập màn hình này
 */
public class ReportsController {
    
    @FXML
    private VBox mainContainer;
    
    @FXML
    private void initialize() {
        // Kiểm tra quyền ngay khi khởi tạo
        if (!PermissionManager.canViewReports()) {
            showAccessDenied();
            disableAllControls();
        }
    }
    
    private void showAccessDenied() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Không có quyền truy cập");
        alert.setHeaderText("Quyền truy cập bị từ chối");
        alert.setContentText("Bạn không có quyền xem Reports.\n" +
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
