# CẬP NHẬT DATABASE CHO PHÂN QUYỀN

## 🎯 Vấn đề
Database hiện tại có role `OWNER` nhưng code phân quyền dùng `ADMIN`. Cần cập nhật để đồng bộ.

## ✅ Giải pháp đã làm
1. **Code nhận cả ADMIN và OWNER**: `PermissionManager.isAdmin()` đã được cập nhật để chấp nhận cả 2 role
2. **Database có đủ 3 roles**: ADMIN, STAFF, OWNER (OWNER = ADMIN)
3. **Migration file đã update**: V2__Seed_Data.sql đã thêm role ADMIN

## 🔄 Cách cập nhật Database

### Cách 1: Reset database (XÓA TOÀN BỘ DỮ LIỆU) - KHUYẾN NGHỊ
```powershell
# Kết nối MySQL
mysql -u root -p

# Trong MySQL:
DROP DATABASE IF EXISTS gympro;
CREATE DATABASE gympro;
exit

# Chạy lại migration
mvn flyway:migrate
```

### Cách 2: Cập nhật database hiện tại (GIỮ DỮ LIỆU)
```powershell
# Kết nối MySQL
mysql -u root -p gympro

# Chạy file SQL update
source c:/Users/MrBuuuuu/Downloads/project-sem2-main/project-sem2-main/UPDATE_ROLES.sql

# Hoặc copy/paste nội dung file UPDATE_ROLES.sql vào MySQL
```

### Cách 3: Chạy SQL thủ công
```sql
-- 1. Thêm role ADMIN
INSERT INTO roles(name, description)
SELECT 'ADMIN','Quản trị viên - Toàn quyền (100%)'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name='ADMIN');

-- 2. Gán role ADMIN cho user admin
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.username = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- 3. Gán role ADMIN cho user manager
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.username = 'manager'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- 4. Kiểm tra kết quả
SELECT 
    u.username,
    u.full_name,
    GROUP_CONCAT(r.name) AS roles
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
WHERE u.username IN ('admin', 'manager', 'staff')
GROUP BY u.id;
```

## ✅ Kiểm tra sau khi cập nhật

Chạy query này để xác nhận:
```sql
SELECT 
    u.username,
    GROUP_CONCAT(r.name) AS roles
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
GROUP BY u.username
ORDER BY u.username;
```

**Kết quả mong đợi**:
- `admin`: ADMIN, OWNER
- `manager`: ADMIN, OWNER  
- `staff`: STAFF

## 🎓 Lưu ý

1. **Code đã tương thích ngược**: `PermissionManager.isAdmin()` chấp nhận cả ADMIN và OWNER
2. **Không cần lo lắng**: Nếu database chỉ có OWNER, hệ thống vẫn hoạt động bình thường
3. **User hiện có**: 
   - `admin` / `admin123` → ADMIN (hoặc OWNER)
   - `manager` / `manager123` → ADMIN (hoặc OWNER)
   - `staff` / `staff123` → STAFF

## 🚀 Test ngay

Sau khi cập nhật database:
1. Chạy ứng dụng
2. Login với `staff` / `staff123` → Chỉ thấy 4 menu
3. Login với `admin` / `admin123` → Thấy đủ 9 menu
4. Vào Members với staff → Không có nút Sửa/Xóa
5. Vào Members với admin → Có nút Sửa/Xóa

## ❓ Nếu có lỗi

### Lỗi: "Cannot find role ADMIN"
→ Chạy lại migration hoặc thêm role ADMIN bằng SQL thủ công (xem Cách 3)

### Lỗi: "User không login được"
→ Kiểm tra password hash có đúng không bằng cách chạy:
```sql
SELECT username, password_hash FROM users WHERE username IN ('admin', 'staff', 'manager');
```

### Lỗi: "Menu không ẩn với STAFF"
→ Kiểm tra user có đúng role STAFF không:
```sql
SELECT u.username, r.name 
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE u.username = 'staff';
```
