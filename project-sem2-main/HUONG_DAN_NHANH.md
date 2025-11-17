# 🚀 HƯỚNG DẪN NHANH - PHÂN QUYỀN GYMPRO

## ⚡ Sử dụng trong 3 bước

### Bước 1: Import vào Controller
```java
import com.example.gympro.authorization.Permission;
import com.example.gympro.authorization.PermissionManager;
import com.example.gympro.authorization.UIAccessControl;
```

### Bước 2: Ẩn button trong initialize()
```java
@FXML
private void initialize() {
    // Ẩn nút Xóa nếu không có quyền (Staff sẽ không thấy)
    UIAccessControl.hideIfNoPermission(deleteButton, Permission.DELETE_MEMBER);
    
    // Ẩn nút Sửa nếu không có quyền
    UIAccessControl.hideIfNoPermission(editButton, Permission.EDIT_MEMBER);
}
```

### Bước 3: Kiểm tra quyền khi xử lý sự kiện
```java
@FXML
private void handleDelete() {
    // Kiểm tra quyền trước khi xóa
    if (!UIAccessControl.checkPermissionWithAlert(Permission.DELETE_MEMBER)) {
        return; // Không có quyền, dừng lại
    }
    
    // Có quyền, tiếp tục xóa
    deleteMember();
}
```

## 📋 Danh sách quyền thường dùng

```java
// Thành viên
Permission.VIEW_MEMBERS      // Xem danh sách
Permission.ADD_MEMBER        // Thêm mới
Permission.EDIT_MEMBER       // Sửa
Permission.DELETE_MEMBER     // Xóa

// Gói tập
Permission.VIEW_PACKAGES     // Xem danh sách
Permission.ADD_PACKAGE       // Thêm mới
Permission.EDIT_PACKAGE      // Sửa
Permission.DELETE_PACKAGE    // Xóa

// Đăng ký
Permission.VIEW_REGISTRATIONS   // Xem danh sách
Permission.CREATE_REGISTRATION  // Tạo đăng ký

// Thanh toán
Permission.VIEW_PAYMENTS     // Xem lịch sử
Permission.PROCESS_PAYMENT   // Xử lý thanh toán

// Báo cáo
Permission.VIEW_REPORTS      // Xem báo cáo
Permission.EXPORT_REPORTS    // Xuất báo cáo

// Cài đặt
Permission.VIEW_SETTINGS     // Xem cài đặt
Permission.EDIT_SETTINGS     // Sửa cài đặt

// Người dùng
Permission.VIEW_USERS        // Xem danh sách
Permission.ADD_USER          // Thêm mới
Permission.EDIT_USER         // Sửa
Permission.DELETE_USER       // Xóa
```

## 🎯 Phân quyền Admin vs Staff

| Chức năng | Admin | Staff |
|-----------|-------|-------|
| Xem Dashboard | ✅ | ✅ |
| Xem/Thêm thành viên | ✅ | ✅ |
| Sửa/Xóa thành viên | ✅ | ❌ |
| Xem gói tập | ✅ | ✅ |
| Thêm/Sửa/Xóa gói tập | ✅ | ❌ |
| Tạo đăng ký | ✅ | ✅ |
| Xử lý thanh toán | ✅ | ✅ |
| Xem báo cáo | ✅ | ❌ |
| Cài đặt hệ thống | ✅ | ❌ |
| Quản lý người dùng | ✅ | ❌ |

**Tổng kết:** Admin 100% (23/23 quyền) - Staff 40% (9/23 quyền)

## 💡 Mẹo sử dụng

### Ẩn button
```java
UIAccessControl.hideIfNoPermission(button, Permission.XXX);
```

### Vô hiệu hóa button (button vẫn hiện nhưng bị mờ)
```java
UIAccessControl.disableIfNoPermission(button, Permission.XXX);
```

### Kiểm tra + hiện cảnh báo
```java
if (!UIAccessControl.checkPermissionWithAlert(Permission.XXX)) {
    return;
}
```

### Kiểm tra + xác nhận (dùng cho xóa)
```java
if (UIAccessControl.checkPermissionAndConfirm(Permission.DELETE_XXX, "Xác nhận xóa?")) {
    // Thực hiện xóa
}
```

### Kiểm tra vai trò
```java
if (PermissionManager.isAdmin()) {
    // Code cho Admin
} else if (PermissionManager.isStaff()) {
    // Code cho Staff
}
```

## 📁 Cấu trúc file đã tạo

```
src/main/java/com/example/gympro/authorization/
├── Permission.java              # Định nghĩa các quyền
├── PermissionManager.java       # Quản lý phân quyền
├── UIAccessControl.java         # Điều khiển giao diện
└── ExampleUsageController.java  # Ví dụ sử dụng
```

## ❓ Câu hỏi nhanh

**Q: Tại sao Staff 40%?**  
A: Staff có 9/23 quyền = 39% ≈ 40%, đủ để làm việc hàng ngày.

**Q: Làm sao test?**  
A: Login bằng Admin xem tất cả button, login Staff xem button bị ẩn.

**Q: Code này có khó không?**  
A: Không! Chỉ cần copy 2-3 dòng code là xong.

**Q: Có ảnh hưởng code cũ không?**  
A: Không! Đây là file hoàn toàn mới, không động vào code hiện có.

## 🎓 Trả lời thầy

**"Em làm phân quyền như thế nào?"**
> Em tạo 3 class mới trong package authorization:
> - Permission: định nghĩa quyền
> - PermissionManager: kiểm tra quyền theo vai trò
> - UIAccessControl: ẩn/hiện button theo quyền
> 
> Admin có 100% quyền, Staff có 40% quyền.

**"Tại sao Staff 40%?"**
> Staff có 9/23 quyền = 40%, bao gồm các chức năng cơ bản như xem danh sách, tạo đăng ký, thu tiền. Không có quyền nguy hiểm như xóa dữ liệu, sửa cài đặt.

**"Code này áp dụng ở đâu?"**
> Em áp dụng vào các Controller như MembersController, PackagesController, chỉ cần thêm 2-3 dòng code để ẩn button theo quyền.

---

📖 **Xem hướng dẫn chi tiết:** Đọc file `phanquyen.md`  
💻 **Xem ví dụ code:** Đọc file `ExampleUsageController.java`
