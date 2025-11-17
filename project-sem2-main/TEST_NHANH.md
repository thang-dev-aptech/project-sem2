# ⚡ TEST PHÂN QUYỀN NHANH - 5 BƯỚC

## 🎯 Vấn đề: "Tôi chạy code khi đăng nhập bằng Staff vẫn chưa được"

**Nguyên nhân:** Bạn đã tạo code phân quyền nhưng CHƯA ÁP DỤNG vào Controller!

---

## ✅ GIẢI PHÁP NHANH NHẤT

### Bước 1: Kiểm tra Database (2 phút)

Mở MySQL Workbench và chạy:

```sql
-- Kiểm tra có vai trò chưa
SELECT * FROM roles;

-- Kiểm tra user có vai trò chưa
SELECT u.username, r.name as role_name 
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id;
```

**Nếu trống hoặc NULL:**
```sql
-- Chạy file SQL này:
-- db/migration/V3__Add_Permission_Data.sql
```

Hoặc chạy nhanh:
```sql
-- Thêm vai trò
INSERT INTO roles (name, description) VALUES 
('ADMIN', 'Admin - 100%'),
('STAFF', 'Staff - 40%');

-- Gán ADMIN cho user đầu tiên
INSERT INTO user_roles (user_id, role_id)
SELECT 
    (SELECT id FROM users ORDER BY id LIMIT 1), 
    (SELECT id FROM roles WHERE name = 'ADMIN');

-- Gán STAFF cho user thứ 2
INSERT INTO user_roles (user_id, role_id)
SELECT 
    (SELECT id FROM users ORDER BY id LIMIT 1 OFFSET 1), 
    (SELECT id FROM roles WHERE name = 'STAFF');
```

### Bước 2: Thêm menu Test vào MainController (5 phút)

**File:** `src/main/java/com/example/gympro/controller/MainController.java`

Tìm dòng này:
```java
String[] menuItems = {
    "📊 Dashboard", "🧍 Members", "💪 Packages", "📅 Registration",
    "💳 Payment", "⏰ Expiring Members", "📈 Reports", "⚙️ Settings", "👤 Users"
};
```

**THAY BẰNG:**
```java
String[] menuItems = {
    "📊 Dashboard", "🧍 Members", "💪 Packages", "📅 Registration",
    "💳 Payment", "⏰ Expiring Members", "📈 Reports", "⚙️ Settings", "👤 Users", "🔐 Test Permission"
};
```

Và thêm:
```java
String[] screenIds = {
    "dashboard", "members", "packages", "registration",
    "payment", "expiry", "reports", "settings", "users", "test-permission"
};
```

Trong hàm `loadScreen()`, thêm:
```java
case "test-permission" -> "/com/example/gympro/fxml/test-permission.fxml";
```

### Bước 3: Thêm import vào MainController

**Thêm ở đầu file MainController.java:**
```java
import com.example.gympro.authorization.*;
```

**Trong hàm `createNavButtons()`, sau dòng `btn.setOnAction(...)`, thêm:**
```java
// Ẩn menu theo quyền
if (screenIds[i].equals("reports")) {
    UIAccessControl.hideIfNoPermission(btn, Permission.VIEW_REPORTS);
}
if (screenIds[i].equals("settings")) {
    UIAccessControl.hideIfNoPermission(btn, Permission.VIEW_SETTINGS);
}
if (screenIds[i].equals("users")) {
    UIAccessControl.hideIfNoPermission(btn, Permission.VIEW_USERS);
}
```

### Bước 4: Compile lại project

```powershell
# Trong VS Code Terminal
./mvnw clean compile
```

Hoặc nếu đang chạy, **Stop và chạy lại**

### Bước 5: Test thử

**Test 1: Login Admin**
- Phải thấy menu "Test Permission"
- Click vào, thấy TẤT CẢ button (cả màu đỏ)
- Console in ra "ADMIN - 23/23 quyền"

**Test 2: Logout và Login Staff**
- Vẫn thấy menu "Test Permission"
- Click vào, CHỈ thấy button màu xanh lá
- Button màu đỏ/cam/tím BIẾN MẤT
- Console in ra "STAFF - 9/23 quyền"

**Test 3: Check menu chính**
- Admin: Thấy tất cả menu
- Staff: KHÔNG THẤY menu Reports, Settings, Users

---

## 🐛 TROUBLESHOOTING

### ❌ Lỗi: Cannot resolve symbol 'Permission'

**Giải pháp:** Thêm import
```java
import com.example.gympro.authorization.*;
```

### ❌ Lỗi: NullPointerException khi gọi PermissionManager

**Nguyên nhân:** Chưa login hoặc database không có vai trò

**Giải pháp:** 
1. Đảm bảo đã login thành công
2. Check database (Bước 1)

### ❌ Staff vẫn thấy tất cả button

**Nguyên nhân:** 
1. Database user không có vai trò STAFF
2. Chưa áp dụng `UIAccessControl.hideIfNoPermission()`

**Giải pháp:**
1. Check query ở Bước 1
2. Xem lại code ở Bước 3

### ❌ Không thấy menu "Test Permission"

**Nguyên nhân:** Chưa compile lại hoặc chưa restart app

**Giải pháp:** 
1. Stop app
2. `./mvnw clean compile`
3. Chạy lại app

---

## 📊 KẾT QUẢ MONG ĐỢI

### Login Admin:
```
Console output:
╔════════════════════════════════════════════╗
║     TEST PHÂN QUYỀN - GYMPRO             ║
╚════════════════════════════════════════════╝

User: Admin User
Username: admin

Vai trò:
  ✅ ADMIN/OWNER

===== QUYỀN CỦA: Admin User =====
Vai trò: ADMIN/OWNER (100% quyền)
Tổng số quyền: 23
  - VIEW_DASHBOARD - Xem trang Dashboard
  - VIEW_MEMBERS - Xem danh sách thành viên
  - ADD_MEMBER - Thêm thành viên mới
  - EDIT_MEMBER - Sửa thông tin thành viên
  - DELETE_MEMBER - Xóa thành viên
  ... (còn 18 quyền nữa)
```

### Login Staff:
```
Console output:
╔════════════════════════════════════════════╗
║     TEST PHÂN QUYỀN - GYMPRO             ║
╚════════════════════════════════════════════╝

User: Staff User
Username: staff

Vai trò:
  ✅ STAFF

===== QUYỀN CỦA: Staff User =====
Vai trò: STAFF (40% quyền)
Tổng số quyền: 9
  - VIEW_DASHBOARD - Xem trang Dashboard
  - VIEW_MEMBERS - Xem danh sách thành viên
  - ADD_MEMBER - Thêm thành viên mới
  - VIEW_PACKAGES - Xem danh sách gói tập
  - VIEW_REGISTRATIONS - Xem danh sách đăng ký
  - CREATE_REGISTRATION - Tạo đăng ký mới
  - VIEW_PAYMENTS - Xem lịch sử thanh toán
  - PROCESS_PAYMENT - Xử lý thanh toán
  - VIEW_EXPIRING_MEMBERS - Xem thành viên sắp hết hạn
```

---

## 💡 NẾU VẪN KHÔNG ĐƯỢC

### Phương án dự phòng: Test trực tiếp trong code

Thêm vào **BẤT KỲ Controller nào** trong hàm `initialize()`:

```java
@FXML
private void initialize() {
    // Code cũ...
    
    // THÊM ĐOẠN NÀY
    testPermission();
}

private void testPermission() {
    System.out.println("\n===== TEST PHÂN QUYỀN =====");
    
    try {
        // Import: import com.example.gympro.authorization.*;
        
        if (PermissionManager.isAdmin()) {
            System.out.println("✅ Là ADMIN");
        } else if (PermissionManager.isStaff()) {
            System.out.println("✅ Là STAFF");
        } else {
            System.out.println("❌ Không xác định vai trò");
        }
        
        System.out.println("Quyền: " + PermissionManager.getCurrentUserPermissions().size());
        
    } catch (Exception e) {
        System.out.println("❌ LỖI: " + e.getMessage());
        e.printStackTrace();
    }
    
    System.out.println("============================\n");
}
```

**Chạy app và xem Console** để biết phân quyền có hoạt động không.

---

## 📞 TÓM TẮT

**Code phân quyền ĐÃ TẠO xong, nhưng cần:**

1. ✅ Database có dữ liệu vai trò
2. ✅ Gọi hàm phân quyền trong Controller
3. ✅ Compile và restart app

**File quan trọng:**
- `TestPermissionController.java` - Controller test
- `test-permission.fxml` - Giao diện test
- `V3__Add_Permission_Data.sql` - Script database
- `CACH_AP_DUNG.md` - Hướng dẫn chi tiết

**Làm theo 5 bước trên là chạy được ngay!** 🚀
