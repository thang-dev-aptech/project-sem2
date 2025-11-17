-- ========================================
-- V3 - SCRIPT THÊM DỮ LIỆU PHÂN QUYỀN
-- ========================================
-- Migration để thêm vai trò ADMIN và gán quyền cho users
-- OWNER và STAFF đã có sẵn từ V2, chỉ cần thêm ADMIN

-- Bước 1: Thêm vai trò ADMIN (OWNER và STAFF đã có từ V2)
INSERT INTO roles (name, description)
SELECT 'ADMIN', 'Quản trị viên - toàn quyền quản lý hệ thống (100% quyền)'
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'ADMIN'
);

-- Bước 2: Gán vai trò cho users đã có từ V2
-- V2 đã tạo 3 users: admin (OWNER), staff (STAFF), manager (OWNER)
-- Giờ ta update mô tả cho rõ ràng hơn

-- Update mô tả cho vai trò OWNER (đã có từ V2)
UPDATE roles 
SET description = 'Chủ sở hữu - toàn quyền quản lý hệ thống (100% quyền)'
WHERE name = 'OWNER' AND description = 'Chủ phòng gym';

-- Update mô tả cho vai trò STAFF (đã có từ V2)  
UPDATE roles
SET description = 'Nhân viên - quyền hạn chế, chỉ xử lý công việc hàng ngày (40% quyền)'
WHERE name = 'STAFF' AND description = 'Nhân viên quầy';

-- ========================================
-- Bước 3: Thêm vai trò ADMIN cho user 'admin' (nếu chưa có)
-- ========================================
-- User 'admin' hiện có vai trò OWNER (từ V2)
-- Thêm thêm vai trò ADMIN cho user này

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
);

-- ========================================
-- Kết quả sau khi chạy V3:
-- ========================================
-- ✅ Đã thêm vai trò ADMIN
-- ✅ Đã update mô tả cho OWNER và STAFF
-- ✅ User 'admin' có 2 vai trò: OWNER và ADMIN (100% quyền)
-- ✅ User 'staff' có vai trò: STAFF (40% quyền)
-- ✅ User 'manager' có vai trò: OWNER (100% quyền)

-- ========================================
-- Kiểm tra kết quả (chạy sau khi migrate):
-- ========================================
-- SELECT u.username, u.full_name, GROUP_CONCAT(r.name) as roles
-- FROM users u
-- LEFT JOIN user_roles ur ON u.id = ur.user_id
-- LEFT JOIN roles r ON ur.role_id = r.id
-- GROUP BY u.id, u.username, u.full_name
-- ORDER BY u.id;

-- OUTPUT mong muốn:
-- username | full_name        | roles
-- admin    | System Admin     | OWNER,ADMIN
-- staff    | Staff User       | STAFF
-- manager  | Manager User     | OWNER
