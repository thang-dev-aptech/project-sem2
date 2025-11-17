package com.example.gympro.authorization;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

/**
 * FILE VÍ DỤ - DEMO CÁCH SỬ DỤNG PHÂN QUYỀN
 * 
 * Copy các đoạn code từ file này vào Controller thật của bạn
 * 
 * ⚠️ FILE NÀY CHỈ ĐỂ THAM KHẢO - KHÔNG CHẠY ĐƯỢC ⚠️
 */
public class ExampleUsageController {
    
    @FXML
    private Button addButton;
    
    @FXML
    private Button editButton;
    
    @FXML
    private Button deleteButton;
    
    @FXML
    private Button reportButton;
    
    @FXML
    private Label roleLabel;
    
    @FXML
    private TableView<?> dataTable;
    
    /**
     * VÍ DỤ 1: Ẩn/hiện button trong initialize()
     * 
     * Áp dụng trong: MembersController, PackagesController, UserManagementController
     */
    @FXML
    private void initialize() {
        // Ẩn button Edit nếu không có quyền
        // Staff sẽ KHÔNG THẤY button này
        UIAccessControl.hideIfNoPermission(editButton, Permission.EDIT_MEMBER);
        
        // Ẩn button Delete nếu không có quyền  
        // Staff sẽ KHÔNG THẤY button này
        UIAccessControl.hideIfNoPermission(deleteButton, Permission.DELETE_MEMBER);
        
        // Ẩn button Report nếu không có quyền
        // Staff sẽ KHÔNG THẤY button này
        UIAccessControl.hideIfNoPermission(reportButton, Permission.VIEW_REPORTS);
        
        // Button Add thì cả Admin và Staff đều thấy (không cần kiểm tra)
        
        // Hiển thị vai trò hiện tại
        if (PermissionManager.isAdmin()) {
            roleLabel.setText("👑 Admin - Toàn quyền");
        } else if (PermissionManager.isStaff()) {
            roleLabel.setText("👤 Staff - Quyền hạn chế");
        }
    }
    
    /**
     * VÍ DỤ 2: Kiểm tra quyền trong hàm xử lý sự kiện
     * 
     * Dùng khi click button để thực hiện hành động
     */
    @FXML
    private void handleAdd() {
        // Kiểm tra quyền trước khi thêm
        if (!UIAccessControl.checkPermissionWithAlert(Permission.ADD_MEMBER)) {
            return; // Dừng lại nếu không có quyền
        }
        
        // ✅ Có quyền - Tiếp tục thêm
        System.out.println("Đang thêm thành viên mới...");
        // Code thêm thành viên ở đây...
    }
    
    /**
     * VÍ DỤ 3: Kiểm tra quyền + xác nhận trước khi xóa
     * 
     * Dùng cho các hành động NGUY HIỂM (xóa, cập nhật quan trọng)
     */
    @FXML
    private void handleDelete() {
        // Lấy item đang chọn
        Object selected = dataTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("Chưa chọn item nào!");
            return;
        }
        
        // Kiểm tra quyền VÀ hỏi xác nhận
        if (UIAccessControl.checkPermissionAndConfirm(
            Permission.DELETE_MEMBER,
            "Bạn có chắc chắn muốn xóa item này không?\nHành động này không thể hoàn tác!")) {
            
            // ✅ Có quyền VÀ đã xác nhận - Tiếp tục xóa
            System.out.println("Đang xóa...");
            // Code xóa ở đây...
        }
    }
    
    /**
     * VÍ DỤ 4: Vô hiệu hóa button thay vì ẩn
     * 
     * Button vẫn hiển thị nhưng bị mờ đi và không click được
     */
    @FXML
    private void initializeWithDisable() {
        // Vô hiệu hóa thay vì ẩn
        UIAccessControl.disableIfNoPermission(editButton, Permission.EDIT_MEMBER);
        UIAccessControl.disableIfNoPermission(deleteButton, Permission.DELETE_MEMBER);
    }
    
    /**
     * VÍ DỤ 5: Kiểm tra trực tiếp bằng PermissionManager
     * 
     * Dùng khi cần logic phức tạp hơn
     */
    @FXML
    private void handleComplexLogic() {
        if (PermissionManager.isAdmin()) {
            // Logic riêng cho Admin
            System.out.println("Admin có thể làm tất cả");
            showAllData();
        } else if (PermissionManager.isStaff()) {
            // Logic riêng cho Staff
            System.out.println("Staff chỉ xem một phần");
            showLimitedData();
        } else {
            System.out.println("Không có quyền truy cập");
        }
    }
    
    /**
     * VÍ DỤ 6: Kiểm tra nhiều quyền cùng lúc
     */
    @FXML
    private void checkMultiplePermissions() {
        boolean canEdit = PermissionManager.hasPermission(Permission.EDIT_MEMBER);
        boolean canDelete = PermissionManager.hasPermission(Permission.DELETE_MEMBER);
        boolean canExport = PermissionManager.hasPermission(Permission.EXPORT_REPORTS);
        
        if (canEdit && canDelete && canExport) {
            System.out.println("Có đủ quyền để thực hiện tất cả");
        } else {
            System.out.println("Thiếu một số quyền");
        }
    }
    
    /**
     * VÍ DỤ 7: Ẩn nhiều button cùng lúc
     */
    @FXML
    private void hideMultipleButtons() {
        // Ẩn nhiều button có cùng quyền
        UIAccessControl.applyPermissionToMultiple(
            Permission.DELETE_MEMBER,
            deleteButton,
            editButton,
            reportButton
        );
    }
    
    /**
     * VÍ DỤ 8: Debug - In ra tất cả quyền hiện có
     */
    @FXML
    private void debugPermissions() {
        System.out.println("\n===== DEBUG QUYỀN =====");
        PermissionManager.printCurrentUserPermissions();
        System.out.println("========================\n");
    }
    
    /**
     * VÍ DỤ 9: Hiển thị popup thông tin vai trò
     */
    @FXML
    private void showRoleInfo() {
        UIAccessControl.showCurrentRoleInfo();
    }
    
    // ==================================================
    // CÁC HÀM GIẢ LẬP (KHÔNG DÙNG TRONG CODE THẬT)
    // ==================================================
    
    private void showAllData() {
        // Hiển thị tất cả dữ liệu cho Admin
    }
    
    private void showLimitedData() {
        // Hiển thị dữ liệu giới hạn cho Staff
    }
}
