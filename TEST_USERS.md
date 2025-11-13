# 🧪 Test Users - GymPro

File này chứa thông tin các user test để đăng nhập vào hệ thống.

## 📋 Danh sách User Test

| Username | Password | Role | Quyền truy cập | Ghi chú |
|----------|----------|------|----------------|---------|
| `admin` | `admin123` | ADMIN | 100% | Quản trị viên - Toàn quyền |
| `manager` | `manager123` | ADMIN | 100% | Quản lý - Toàn quyền |
| `staff` | `staff123` | STAFF | 40% | Nhân viên - Quyền hạn chế |

## 🔑 Chi tiết phân quyền

### ADMIN (100% - admin, manager)
✅ **Được phép**:
- 📊 Dashboard (xem đầy đủ)
- 🧍 Members (xem, thêm, sửa, xóa)
- 💪 Packages (quản lý gói tập)
- 📅 Registration (đăng ký thành viên)
- 💳 Payment (quản lý thanh toán)
- ⏰ Expiring Members (xem hội viên sắp hết hạn)
- 📈 Reports (xem báo cáo, doanh thu)
- ⚙️ Settings (cài đặt hệ thống)
- 👤 Users (quản lý nhân viên)

### STAFF (40% - staff)
✅ **Được phép**:
- 📊 Dashboard (chỉ xem)
- 🧍 Members (chỉ xem, KHÔNG sửa/xóa)
- 📅 Registration (đăng ký thành viên mới)
- 💳 Payment (quản lý thanh toán)

❌ **KHÔNG được phép**:
- 💪 Packages (không thấy menu)
- ⏰ Expiring Members (không thấy menu)
- 📈 Reports (không thấy menu)
- ⚙️ Settings (không thấy menu)
- 👤 Users (không thấy menu)

## 🧪 Cách test

### Test 1: Login với ADMIN
```
Username: admin
Password: admin123
Kết quả: Thấy đủ 9 menu, có nút "Chỉnh sửa" và "Xóa" trong Members
```

### Test 2: Login với STAFF
```
Username: staff
Password: staff123
Kết quả: Chỉ thấy 4 menu (Dashboard, Members, Registration, Payment)
         Trong Members KHÔNG có nút "Chỉnh sửa" và "Xóa"
```

## 🔑 Cách tạo hash password mới

Nếu bạn muốn tạo user mới với password khác:

```bash
# Windows PowerShell
cd c:\Users\MrBuuuuu\Downloads\project-sem2-main\project-sem2-main
mvn compile
java -cp "target/classes;%USERPROFILE%\.m2\repository\org\mindrot\jbcrypt\0.4\jbcrypt-0.4.jar" com.example.gympro.utils.PasswordHasher <password>

# Ví dụ:
java -cp "target/classes;%USERPROFILE%\.m2\repository\org\mindrot\jbcrypt\0.4\jbcrypt-0.4.jar" com.example.gympro.utils.PasswordHasher mypassword123
```

Sau đó copy hash vào SQL INSERT:

```sql
-- Tạo user mới với role ADMIN
INSERT INTO users (branch_id, username, full_name, email, phone, password_hash, is_active)
SELECT b.id, 'newadmin', 'New Admin', 'newadmin@gympro.com', '0123456789',
       '<hash_from_output>', 1
FROM branches b
WHERE b.code = 'BR-001'
  AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'newadmin');

-- Gán role ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.username = 'newadmin';
```

## 📊 Password Hash mẫu

```
admin123  → $2a$10$aczKTH1d5ImEYD3XmL2L8Of75MAhQGlk5QyvjHTgQ7t72qlUOBEgS
staff123  → $2a$10$4thd6VL22evbKAYTGZQHDuIvlgpy3PaiL5dNQdXan1jfj2RgjAsjy
manager123 → $2a$10$rORI/A9ufqDyL3QLN.XcLOT9n/SQzQPbT3r91RdyaeSWmwtULjquy
```

## ⚠️ Lưu ý

- Các password này chỉ dùng cho **môi trường DEV/TEST**
- **KHÔNG** sử dụng trong production
- Sau khi migrate (flyway migrate), các user này sẽ tự động được tạo
- Role `OWNER` và `ADMIN` là tương đương (để tương thích với dữ liệu cũ)
- Database có cả 3 roles: ADMIN, STAFF, OWNER (OWNER = ADMIN)

## 🔄 Cập nhật Database

Nếu database đã chạy rồi, bạn cần chạy lại migration:

```bash
# Cách 1: Reset database (XÓA TOÀN BỘ DỮ LIỆU)
DROP DATABASE gympro;
CREATE DATABASE gympro;
mvn flyway:migrate

# Cách 2: Chạy SQL thủ công (GIỮ DỮ LIỆU)
# Vào MySQL và chạy các câu lệnh INSERT role ADMIN từ file V2__Seed_Data.sql
```

