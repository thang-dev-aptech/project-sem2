package com.example.gympro.authorization;

/**
 * Class định nghĩa các quyền hạn trong hệ thống GymPro
 * Mỗi quyền có tên và mô tả rõ ràng
 * 
 * Admin (Owner) có 100% quyền
 * Staff chỉ có 40% quyền (các chức năng cơ bản)
 */
public class Permission {
    
    // Tên quyền
    private String name;
    
    // Mô tả quyền
    private String description;
    
    // Constructor
    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    // Định nghĩa các quyền cụ thể trong hệ thống
    // Admin có TẤT CẢ các quyền này
    // Staff chỉ có 1 số quyền được đánh dấu
    
    // === QUYỀN VỀ DASHBOARD ===
    public static final Permission VIEW_DASHBOARD = 
        new Permission("VIEW_DASHBOARD", "Xem trang Dashboard");
    
    // === QUYỀN VỀ THÀNH VIÊN (MEMBERS) ===
    public static final Permission VIEW_MEMBERS = 
        new Permission("VIEW_MEMBERS", "Xem danh sách thành viên");
    
    public static final Permission ADD_MEMBER = 
        new Permission("ADD_MEMBER", "Thêm thành viên mới");
    
    public static final Permission EDIT_MEMBER = 
        new Permission("EDIT_MEMBER", "Sửa thông tin thành viên");
    
    public static final Permission DELETE_MEMBER = 
        new Permission("DELETE_MEMBER", "Xóa thành viên");
    
    // === QUYỀN VỀ GÓI TẬP (PACKAGES) ===
    public static final Permission VIEW_PACKAGES = 
        new Permission("VIEW_PACKAGES", "Xem danh sách gói tập");
    
    public static final Permission ADD_PACKAGE = 
        new Permission("ADD_PACKAGE", "Thêm gói tập mới");
    
    public static final Permission EDIT_PACKAGE = 
        new Permission("EDIT_PACKAGE", "Sửa thông tin gói tập");
    
    public static final Permission DELETE_PACKAGE = 
        new Permission("DELETE_PACKAGE", "Xóa gói tập");
    
    // === QUYỀN VỀ ĐĂNG KÝ (REGISTRATION) ===
    public static final Permission VIEW_REGISTRATIONS = 
        new Permission("VIEW_REGISTRATIONS", "Xem danh sách đăng ký");
    
    public static final Permission CREATE_REGISTRATION = 
        new Permission("CREATE_REGISTRATION", "Tạo đăng ký mới");
    
    // === QUYỀN VỀ THANH TOÁN (PAYMENT) ===
    public static final Permission VIEW_PAYMENTS = 
        new Permission("VIEW_PAYMENTS", "Xem lịch sử thanh toán");
    
    public static final Permission PROCESS_PAYMENT = 
        new Permission("PROCESS_PAYMENT", "Xử lý thanh toán");
    
    // === QUYỀN VỀ HẾT HẠN (EXPIRING) ===
    public static final Permission VIEW_EXPIRING_MEMBERS = 
        new Permission("VIEW_EXPIRING_MEMBERS", "Xem thành viên sắp hết hạn");
    
    // === QUYỀN VỀ BÁO CÁO (REPORTS) ===
    public static final Permission VIEW_REPORTS = 
        new Permission("VIEW_REPORTS", "Xem báo cáo");
    
    public static final Permission EXPORT_REPORTS = 
        new Permission("EXPORT_REPORTS", "Xuất báo cáo");
    
    // === QUYỀN VỀ CÀI ĐẶT (SETTINGS) ===
    public static final Permission VIEW_SETTINGS = 
        new Permission("VIEW_SETTINGS", "Xem cài đặt");
    
    public static final Permission EDIT_SETTINGS = 
        new Permission("EDIT_SETTINGS", "Chỉnh sửa cài đặt");
    
    // === QUYỀN VỀ QUẢN LÝ NGƯỜI DÙNG (USERS) ===
    public static final Permission VIEW_USERS = 
        new Permission("VIEW_USERS", "Xem danh sách người dùng");
    
    public static final Permission ADD_USER = 
        new Permission("ADD_USER", "Thêm người dùng mới");
    
    public static final Permission EDIT_USER = 
        new Permission("EDIT_USER", "Sửa thông tin người dùng");
    
    public static final Permission DELETE_USER = 
        new Permission("DELETE_USER", "Xóa người dùng");
    
    @Override
    public String toString() {
        return name + " - " + description;
    }
}
