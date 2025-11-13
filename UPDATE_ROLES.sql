-- =====================================================================
-- CẬP NHẬT PHÂN QUYỀN - Thêm role ADMIN vào hệ thống
-- File này dùng để cập nhật database ĐÃ CHẠY MIGRATION trước đó
-- =====================================================================

-- Thêm role ADMIN (nếu chưa có)
INSERT INTO roles(name, description)
SELECT 'ADMIN','Quản trị viên - Toàn quyền (100%)'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name='ADMIN');

-- Cập nhật mô tả cho role STAFF
UPDATE roles 
SET description = 'Nhân viên quầy - Quyền hạn chế (40%)'
WHERE name = 'STAFF';

-- Cập nhật mô tả cho role OWNER
UPDATE roles 
SET description = 'Chủ phòng gym (tương đương ADMIN)'
WHERE name = 'OWNER';

-- Gán role ADMIN cho user 'admin' (nếu chưa có)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.username = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- Gán role ADMIN cho user 'manager' (nếu chưa có)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.username = 'manager'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- Xem kết quả
SELECT 
    u.username,
    u.full_name,
    GROUP_CONCAT(r.name) AS roles,
    GROUP_CONCAT(r.description) AS descriptions
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
WHERE u.username IN ('admin', 'manager', 'staff')
GROUP BY u.id, u.username, u.full_name;
