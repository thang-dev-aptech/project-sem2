# 🔧 HƯỚNG DẪN ÁP DỤNG PHÂN QUYỀN VÀO DỰ ÁN

## ⚠️ VẤN ĐỀ

Bạn đã tạo các file phân quyền nhưng **CHƯA ÁP DỤNG** vào các Controller.  
Code phân quyền chỉ hoạt động khi bạn **gọi nó trong Controller**!

---

## 🎯 CÁCH ÁP DỤNG ĐƠN GIẢN NHẤT

### Phương án 1: Tạo Controller mới riêng (KHUYÊN DÙNG)

Tôi đã tạo sẵn file mẫu cho bạn:

**File:** `src/main/java/com/example/gympro/controller/TestPermissionController.java`

Đây là Controller ĐƠN GIẢN để test phân quyền:
- Có button Admin Only (chỉ Admin thấy)
- Có button Staff Can See (cả Admin và Staff thấy)
- Hiển thị thông tin vai trò hiện tại

**Cách test:**
1. Tạo file FXML tương ứng (hoặc dùng FXML có sẵn)
2. Login bằng Admin → thấy tất cả button
3. Login bằng Staff → chỉ thấy một số button

### Phương án 2: Thêm code vào Controller có sẵn

**⚠️ LƯU Ý:** Đây là bài tập nhóm, nên tôi KHÔNG KHUYÊN sửa trực tiếp vào Controller cũ!

Nhưng nếu bạn muốn, làm theo các bước sau:

---

## 📝 HƯỚNG DẪN CHI TIẾT

### Bước 1: Kiểm tra database có vai trò chưa

Mở MySQL và chạy:

```sql
-- Kiểm tra bảng roles
SELECT * FROM roles;

-- Kiểm tra user có vai trò chưa
SELECT u.username, r.name as role_name 
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id;
```

**Nếu chưa có dữ liệu**, chạy script này:

```sql
-- Thêm vai trò
INSERT INTO roles (name, description) VALUES 
('ADMIN', 'Quản trị viên - toàn quyền'),
('OWNER', 'Chủ sở hữu - toàn quyền'),
('STAFF', 'Nhân viên - quyền hạn chế');

-- Giả sử user có id=1 là Admin, id=2 là Staff
-- Gán vai trò Admin cho user id=1
INSERT INTO user_roles (user_id, role_id) 
SELECT 1, id FROM roles WHERE name = 'ADMIN';

-- Gán vai trò Staff cho user id=2
INSERT INTO user_roles (user_id, role_id) 
SELECT 2, id FROM roles WHERE name = 'STAFF';
```

### Bước 2: Test phân quyền trong Console

Thêm code này vào **bất kỳ Controller nào** trong hàm `initialize()`:

```java
@FXML
private void initialize() {
    // Code cũ của bạn...
    
    // THÊM ĐOẠN NÀY ĐỂ TEST
    System.out.println("\n===== TEST PHÂN QUYỀN =====");
    
    // Import các class cần thiết ở đầu file:
    // import com.example.gympro.authorization.*;
    
    if (PermissionManager.isAdmin()) {
        System.out.println("✅ User hiện tại là ADMIN");
    } else if (PermissionManager.isStaff()) {
        System.out.println("✅ User hiện tại là STAFF");
    } else {
        System.out.println("❌ Không xác định được vai trò");
    }
    
    System.out.println("\nDanh sách quyền:");
    PermissionManager.printCurrentUserPermissions();
    System.out.println("============================\n");
}
```

**Chạy ứng dụng** và xem Console output để biết phân quyền có hoạt động không.

### Bước 3: Áp dụng ẩn/hiện button (VÍ DỤ)

**⚠️ CHỈ LÀM NẾU BẠN MUỐN SỬA CONTROLLER CŨ**

Ví dụ trong `MembersController.java`:

```java
// 1. THÊM IMPORT Ở ĐẦU FILE
import com.example.gympro.authorization.Permission;
import com.example.gympro.authorization.PermissionManager;
import com.example.gympro.authorization.UIAccessControl;

// 2. TRONG HÀM initialize(), THÊM CODE SAU:
@FXML
private void initialize() {
    // ... code cũ ...
    
    // ÁP DỤNG PHÂN QUYỀN
    applyPermissions();
}

// 3. TẠO HÀM MỚI
private void applyPermissions() {
    // Tìm button Delete và Edit trong màn hình
    // Nếu bạn có @FXML private Button deleteBtn;
    // thì dùng như sau:
    
    // UIAccessControl.hideIfNoPermission(deleteBtn, Permission.DELETE_MEMBER);
    // UIAccessControl.hideIfNoPermission(editBtn, Permission.EDIT_MEMBER);
    
    // In ra console để test
    System.out.println("Đã áp dụng phân quyền cho Members");
}
```

### Bước 4: Áp dụng vào MainController (Ẩn menu)

Trong `MainController.java`, sửa hàm `createNavButtons()`:

```java
private void createNavButtons() {
    String[] menuItems = {
        "📊 Dashboard", "🧍 Members", "💪 Packages", "📅 Registration",
        "💳 Payment", "⏰ Expiring Members", "📈 Reports", "⚙️ Settings", "👤 Users"
    };
    String[] screenIds = {
        "dashboard", "members", "packages", "registration",
        "payment", "expiry", "reports", "settings", "users"
    };

    for (int i = 0; i < menuItems.length; i++) {
        Button btn = new Button(menuItems[i]);
        btn.getStyleClass().add("nav-button");
        final String screenId = screenIds[i];
        
        btn.setOnAction(e -> {
            currentScreen = screenId;
            loadScreen(screenId);
            updateNavButtons(btn);
        });
        
        // ===== THÊM ĐOẠN NÀY ĐỂ ẨN MENU =====
        // Import: import com.example.gympro.authorization.*;
        
        // Ẩn Reports nếu không có quyền
        if (screenIds[i].equals("reports")) {
            UIAccessControl.hideIfNoPermission(btn, Permission.VIEW_REPORTS);
        }
        
        // Ẩn Settings nếu không có quyền
        if (screenIds[i].equals("settings")) {
            UIAccessControl.hideIfNoPermission(btn, Permission.VIEW_SETTINGS);
        }
        
        // Ẩn Users nếu không có quyền
        if (screenIds[i].equals("users")) {
            UIAccessControl.hideIfNoPermission(btn, Permission.VIEW_USERS);
        }
        // ===== HẾT ĐOẠN THÊM =====
        
        navMenu.getChildren().add(btn);
    }
}
```

---

## 🧪 CÁCH TEST

### Test 1: Console Log
Sau khi login, check Console output xem có in ra:
```
===== TEST PHÂN QUYỀN =====
✅ User hiện tại là STAFF
Danh sách quyền:
...
```

### Test 2: Login với 2 tài khoản

**Login Admin:**
- Phải thấy TẤT CẢ menu: Dashboard, Members, Packages, Registration, Payment, Expiring Members, Reports, Settings, Users
- Tất cả button đều hiện

**Login Staff:**
- Menu Reports, Settings, Users phải **BIẾN MẤT**
- Chỉ còn: Dashboard, Members, Packages, Registration, Payment, Expiring Members

---

## 🐛 TROUBLESHOOTING

### Lỗi 1: NullPointerException
**Nguyên nhân:** Chưa login hoặc SessionManager chưa có user

**Giải pháp:** Đảm bảo đã login thành công

### Lỗi 2: Vẫn thấy tất cả menu khi login Staff
**Nguyên nhân:** Chưa thêm code ẩn menu vào MainController

**Giải pháp:** Xem lại Bước 4 ở trên

### Lỗi 3: Cannot resolve symbol 'Permission'
**Nguyên nhân:** Chưa import

**Giải pháp:** Thêm dòng này ở đầu file:
```java
import com.example.gympro.authorization.*;
```

### Lỗi 4: Database không có vai trò
**Nguyên nhân:** Bảng roles hoặc user_roles trống

**Giải pháp:** Chạy SQL script ở Bước 1

---

## 💡 KHUYÊN DÙNG CHO BÀI TẬP NHÓM

**CÁCH AN TOÀN NHẤT:**

1. **KHÔNG sửa Controller cũ** (để tránh conflict với thành viên khác)

2. **Tạo Controller mới** để demo phân quyền:
   - `TestPermissionController.java` (đã tạo sẵn)
   - Tạo FXML đơn giản với 2-3 button
   - Demo cho thầy xem phân quyền hoạt động

3. **Chỉ sửa MainController** để ẩn menu (ít conflict nhất)

4. **Giải thích cho thầy:**
   > "Em đã tạo hệ thống phân quyền hoàn chỉnh.  
   > Do đây là bài tập nhóm, em tạo file mới để demo chức năng,  
   > chưa tích hợp vào toàn bộ Controller để tránh ảnh hưởng code của bạn khác.  
   > Về mặt kỹ thuật, chỉ cần thêm 2-3 dòng code vào Controller là hoạt động."

---

## 📞 TÓM TẮT

**Để phân quyền hoạt động:**

✅ Đã tạo 3 class: Permission, PermissionManager, UIAccessControl  
✅ Database phải có dữ liệu trong bảng `roles` và `user_roles`  
✅ Phải **GỌI** các hàm phân quyền trong Controller  

**Nếu chưa hoạt động, làm theo thứ tự:**
1. Check database (Bước 1)
2. Test console log (Bước 2)
3. Áp dụng vào MainController (Bước 4)
4. Demo với TestPermissionController

**File tham khảo:**
- `TestPermissionController.java` - Controller mẫu
- `phanquyen.md` - Hướng dẫn đầy đủ
- `HUONG_DAN_NHANH.md` - Cheat sheet
