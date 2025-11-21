-- =====================================================================
-- V2__Seed_Data.sql
-- Seed data tổng hợp cho GymPro (gộp từ V2, V3, V4, V5)
-- Tất cả INSERT đều dùng WHERE NOT EXISTS để idempotent (an toàn khi chạy lại)
-- Đã loại bỏ shift_id và shifts
-- =====================================================================

-- =====================================================================
-- 1. BRANCHES (Chi nhánh)
-- =====================================================================
INSERT INTO branches (code, name, address, phone)
SELECT 'BR-001', 'Chi nhánh Trung tâm', '123 Đường ABC, Quận 1, TP.HCM', '0123-456-789'
WHERE NOT EXISTS (SELECT 1 FROM branches WHERE code = 'BR-001');

-- =====================================================================
-- 2. ROLES (Vai trò người dùng)
-- =====================================================================
INSERT INTO roles (name, description)
SELECT 'OWNER', 'Chủ phòng gym'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'OWNER');

INSERT INTO roles (name, description)
SELECT 'STAFF', 'Nhân viên quầy'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'STAFF');

-- =====================================================================
-- 3. PAYMENT METHODS (Phương thức thanh toán)
-- =====================================================================
INSERT INTO payment_methods (code, display_name)
SELECT 'CASH', 'Tiền mặt'
WHERE NOT EXISTS (SELECT 1 FROM payment_methods WHERE code = 'CASH');

INSERT INTO payment_methods (code, display_name)
SELECT 'BANK', 'Chuyển khoản'
WHERE NOT EXISTS (SELECT 1 FROM payment_methods WHERE code = 'BANK');

INSERT INTO payment_methods (code, display_name)
SELECT 'QR', 'Quét QR'
WHERE NOT EXISTS (SELECT 1 FROM payment_methods WHERE code = 'QR');

INSERT INTO payment_methods (code, display_name)
SELECT 'CARD', 'Thẻ tín dụng'
WHERE NOT EXISTS (SELECT 1 FROM payment_methods WHERE code = 'CARD');

-- =====================================================================
-- 4. DISCOUNT POLICIES (Chính sách chiết khấu)
-- =====================================================================
INSERT INTO discount_policies (role_name, max_percent, max_amount)
SELECT 'STAFF', 10.00, 100000.00
WHERE NOT EXISTS (SELECT 1 FROM discount_policies WHERE role_name = 'STAFF');

INSERT INTO discount_policies (role_name, max_percent, max_amount)
SELECT 'OWNER', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM discount_policies WHERE role_name = 'OWNER');

-- =====================================================================
-- 5. SETTINGS (Cấu hình hệ thống)
-- =====================================================================
INSERT INTO settings (key_name, value_str, value_num)
SELECT 'GRACE_DAYS', NULL, 5
WHERE NOT EXISTS (SELECT 1 FROM settings WHERE key_name = 'GRACE_DAYS');


INSERT INTO settings (key_name, value_str, value_num)
SELECT 'MEMBER_CODE_PREFIX', 'GYM', NULL
WHERE NOT EXISTS (SELECT 1 FROM settings WHERE key_name = 'MEMBER_CODE_PREFIX');

INSERT INTO settings (key_name, value_str, value_num)
SELECT 'INVOICE_PREFIX', 'INV', NULL
WHERE NOT EXISTS (SELECT 1 FROM settings WHERE key_name = 'INVOICE_PREFIX');

INSERT INTO settings (key_name, value_str, value_num)
SELECT 'CURRENCY_SYMBOL', '₫', NULL
WHERE NOT EXISTS (SELECT 1 FROM settings WHERE key_name = 'CURRENCY_SYMBOL');

-- =====================================================================
-- 6. USERS (Người dùng - Test Accounts)
-- Passwords đã được hash bằng BCrypt:
--   admin: admin123  (OWNER)
--   staff: staff123  (STAFF)
-- =====================================================================

-- Admin user (password: admin123)
INSERT INTO users (branch_id, username, full_name, email, phone, password_hash, is_active)
SELECT b.id, 'admin', 'Administrator', 'admin@gympro.com', '0123456789', 
       '$2a$10$aczKTH1d5ImEYD3XmL2L8Of75MAhQGlk5QyvjHTgQ7t72qlUOBEgS', 1
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- Staff user (password: staff123)
INSERT INTO users (branch_id, username, full_name, email, phone, password_hash, is_active)
SELECT b.id, 'staff', 'Nhân viên Test', 'staff@gympro.com', '0987654321', 
       '$2a$10$4thd6VL22evbKAYTGZQHDuIvlgpy3PaiL5dNQdXan1jfj2RgjAsjy', 1
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'staff');

-- =====================================================================
-- 7. USER ROLES (Phân quyền cho users)
-- =====================================================================

-- Grant OWNER role to admin
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u JOIN roles r ON r.name = 'OWNER'
WHERE u.username = 'admin' 
  AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

-- Grant STAFF role to staff
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u JOIN roles r ON r.name = 'STAFF'
WHERE u.username = 'staff' 
  AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

-- =====================================================================
-- 8. PLANS (Gói tập)
-- =====================================================================
INSERT INTO plans (branch_id, code, name, description, price, duration_days, is_active)
SELECT b.id, 'PKG-001', 'Gói 1 tháng', 'Gói tập 1 tháng', 500000, 30, 1
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM plans WHERE code = 'PKG-001');

INSERT INTO plans (branch_id, code, name, description, price, duration_days, is_active)
SELECT b.id, 'PKG-002', 'Gói 3 tháng', 'Gói tập 3 tháng (ưu đãi)', 1300000, 90, 1
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM plans WHERE code = 'PKG-002');

INSERT INTO plans (branch_id, code, name, description, price, duration_days, is_active)
SELECT b.id, 'PKG-003', 'Gói 6 tháng', 'Gói tập 6 tháng (ưu đãi lớn)', 2400000, 180, 1
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM plans WHERE code = 'PKG-003');

INSERT INTO plans (branch_id, code, name, description, price, duration_days, is_active)
SELECT b.id, 'PKG-004', 'Gói 12 tháng', 'Gói tập 12 tháng (ưu đãi tốt nhất)', 4500000, 365, 1
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM plans WHERE code = 'PKG-004');

-- =====================================================================
-- 9. MEMBERS (Hội viên) - Gộp từ V2, V3, V5
-- =====================================================================

-- Members từ V2
INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, is_deleted)
SELECT b.id, 'MEM-0001', 'Nguyễn Văn A', '0901111111', 'a@gmail.com', 'MALE', '1990-01-01', '123 Đường ABC', 'ACTIVE', 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'MEM-0001');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, is_deleted)
SELECT b.id, 'MEM-0002', 'Trần Thị B', '0902222222', 'b@gmail.com', 'FEMALE', '1992-05-12', '456 Đường DEF', 'ACTIVE', 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'MEM-0002');

-- Members từ V3
INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, is_deleted)
SELECT b.id, 'GYM-2024-001', 'Nguyễn Văn An', '0901234567', 'nguyenvanan@email.com', 'MALE', '1990-01-15', '123 Đường A, Quận 1', 'ACTIVE', 'Hội viên VIP', 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-001');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, is_deleted)
SELECT b.id, 'GYM-2024-002', 'Trần Thị Bình', '0912345678', 'tranthibinh@email.com', 'FEMALE', '1995-05-20', '456 Đường B, Quận 2', 'ACTIVE', NULL, 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-002');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, is_deleted)
SELECT b.id, 'GYM-2024-003', 'Lê Văn Cường', '0923456789', 'levancuong@email.com', 'MALE', '1988-08-10', '789 Đường C, Quận 3', 'ACTIVE', NULL, 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-003');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, is_deleted)
SELECT b.id, 'GYM-2024-004', 'Phạm Thị Dung', '0934567890', 'phamthidung@email.com', 'FEMALE', '1992-12-25', '321 Đường D, Quận 4', 'PENDING', 'Chờ thanh toán', 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-004');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, is_deleted)
SELECT b.id, 'GYM-2024-005', 'Hoàng Văn Em', '0945678901', 'hoangvanem@email.com', 'MALE', '1993-03-30', '654 Đường E, Quận 5', 'ACTIVE', NULL, 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-005');

-- Members từ V5 (Expiring Members)
INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, is_deleted)
SELECT b.id, 'GYM-2024-101', 'Nguyễn Văn Hết Hạn 1', '0911111111', 'expired1@test.com', 'MALE', '1990-01-01', '123 Đường Test 1', 'ACTIVE', 'Test: Đã hết hạn 25 ngày', 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-101');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, is_deleted)
SELECT b.id, 'GYM-2024-102', 'Trần Thị Hết Hạn 2', '0922222222', 'expired2@test.com', 'FEMALE', '1992-02-02', '456 Đường Test 2', 'ACTIVE', 'Test: Đã hết hạn 20 ngày', 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-102');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, is_deleted)
SELECT b.id, 'GYM-2024-103', 'Lê Văn Hết Hạn 3', '0933333333', 'expired3@test.com', 'MALE', '1988-03-03', '789 Đường Test 3', 'ACTIVE', 'Test: Đã hết hạn 15 ngày', 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-103');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, is_deleted)
SELECT b.id, 'GYM-2024-104', 'Phạm Thị Hết Hạn 4', '0944444444', 'expired4@test.com', 'FEMALE', '1995-04-04', '321 Đường Test 4', 'ACTIVE', 'Test: Đã hết hạn 10 ngày', 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-104');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, is_deleted)
SELECT b.id, 'GYM-2024-105', 'Hoàng Văn Hết Hạn 5', '0955555555', 'expired5@test.com', 'MALE', '1993-05-05', '654 Đường Test 5', 'ACTIVE', 'Test: Đã hết hạn 5 ngày', 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-105');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, is_deleted)
SELECT b.id, 'GYM-2024-106', 'Vũ Thị Sắp Hết 1', '0966666666', 'expiring1@test.com', 'FEMALE', '1991-06-06', '987 Đường Test 6', 'ACTIVE', 'Test: Hết hạn hôm nay (0 ngày)', 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-106');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, is_deleted)
SELECT b.id, 'GYM-2024-107', 'Đỗ Văn Sắp Hết 2', '0977777777', 'expiring2@test.com', 'MALE', '1989-07-07', '147 Đường Test 7', 'ACTIVE', 'Test: Còn 1 ngày', 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-107');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, is_deleted)
SELECT b.id, 'GYM-2024-108', 'Bùi Thị Sắp Hết 3', '0988888888', 'expiring3@test.com', 'FEMALE', '1994-08-08', '258 Đường Test 8', 'ACTIVE', 'Test: Còn 3 ngày', 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-108');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, is_deleted)
SELECT b.id, 'GYM-2024-109', 'Ngô Văn Sắp Hết 4', '0999999999', 'expiring4@test.com', 'MALE', '1992-09-09', '369 Đường Test 9', 'ACTIVE', 'Test: Còn 7 ngày', 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-109');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note, is_deleted)
SELECT b.id, 'GYM-2024-110', 'Lý Thị Sắp Hết 5', '0900000000', 'expiring5@test.com', 'FEMALE', '1996-10-10', '741 Đường Test 10', 'ACTIVE', 'Test: Còn 14 ngày', 0
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-110');

-- =====================================================================
-- 10. SUBSCRIPTIONS (Đăng ký gói) - Gộp từ V2, V3, V4, V5
-- =====================================================================

-- Subscriptions từ V2
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status, created_by, note)
SELECT m.id, p.id, '2025-11-11', DATE_ADD('2025-11-11', INTERVAL p.duration_days DAY), 'ACTIVE', u.id, 'Đăng ký thử'
FROM members m
JOIN plans p ON p.code = 'PKG-001'
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'MEM-0001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.start_date = '2025-11-11');

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status, created_by, note)
SELECT m.id, p.id, CURDATE(), DATE_ADD(CURDATE(), INTERVAL p.duration_days DAY), 'ACTIVE', u.id, 'Đăng ký thử'
FROM members m
JOIN plans p ON p.code = 'PKG-002'
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'MEM-0002'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.start_date = CURDATE());

-- Subscriptions từ V3
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 70 DAY), DATE_ADD(CURDATE(), INTERVAL 20 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-001' AND p.code = 'PKG-002'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.status = 'ACTIVE' AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 70 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 28 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-002' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.status = 'ACTIVE' AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 28 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 80 DAY), DATE_ADD(CURDATE(), INTERVAL 100 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-003' AND p.code = 'PKG-003'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.status = 'ACTIVE' AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 80 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 35 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-005' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_SUB(CURDATE(), INTERVAL 5 DAY));

-- Subscriptions từ V4
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 0 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-001' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 30 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_ADD(CURDATE(), INTERVAL 75 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-002' AND p.code = 'PKG-002'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 15 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 170 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-003' AND p.code = 'PKG-003'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 10 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 25 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-005' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 5 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 87 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-001' AND p.code = 'PKG-002'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 3 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 364 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-003' AND p.code = 'PKG-004'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-002' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.start_date = CURDATE());

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-001' AND p.code = 'PKG-002'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 60 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 45 DAY), DATE_ADD(CURDATE(), INTERVAL 135 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-003' AND p.code = 'PKG-003'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 45 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-005' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 20 DAY));

-- Subscriptions từ V5 (Expiring Members)
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 55 DAY), DATE_SUB(CURDATE(), INTERVAL 25 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-101' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_SUB(CURDATE(), INTERVAL 25 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 50 DAY), DATE_SUB(CURDATE(), INTERVAL 20 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-102' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_SUB(CURDATE(), INTERVAL 20 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 45 DAY), DATE_SUB(CURDATE(), INTERVAL 15 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-103' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_SUB(CURDATE(), INTERVAL 15 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 40 DAY), DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-104' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_SUB(CURDATE(), INTERVAL 10 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 35 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-105' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_SUB(CURDATE(), INTERVAL 5 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 30 DAY), CURDATE(), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-106' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = CURDATE());

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 29 DAY), DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-107' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 27 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-108' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_ADD(CURDATE(), INTERVAL 3 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 23 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-109' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_ADD(CURDATE(), INTERVAL 7 DAY));

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 16 DAY), DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-110' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_ADD(CURDATE(), INTERVAL 14 DAY));

-- =====================================================================
-- 11. INVOICES (Hóa đơn) - Gộp từ V2, V3, V4 (đã loại bỏ shift_id)
-- =====================================================================

-- Invoices từ V2
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-0001', CURDATE(), 500000, 'NONE', 0, 500000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id
JOIN users u ON u.username = 'staff'
WHERE m.member_code = 'MEM-0001'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-0001');

INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-0002', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 750000, 'PERCENT', 5, 712500, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id
JOIN users u ON u.username = 'staff'
WHERE m.member_code = 'MEM-0002'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-0002');

-- Invoices từ V3
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-001', DATE_SUB(CURDATE(), INTERVAL 70 DAY), 1300000, 'NONE', 0, 1300000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.status = 'ACTIVE' AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 70 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-001'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-001')
LIMIT 1;

INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-002', DATE_SUB(CURDATE(), INTERVAL 28 DAY), 500000, 'NONE', 0, 500000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.status = 'ACTIVE' AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 28 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-002'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-002')
LIMIT 1;

INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-003', DATE_SUB(CURDATE(), INTERVAL 80 DAY), 2400000, 'AMOUNT', 100000, 2300000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.status = 'ACTIVE' AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 80 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-003'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-003')
LIMIT 1;

INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, NULL, 'INV-2024-004', CURDATE(), 500000, 'NONE', 0, 500000, 'ISSUED', u.id
FROM members m
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-004'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-004')
LIMIT 1;

-- Invoices từ V4
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-005', DATE_SUB(CURDATE(), INTERVAL 30 DAY), 500000, 'NONE', 0, 500000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 30 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-001'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-005')
LIMIT 1;

INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-006', DATE_SUB(CURDATE(), INTERVAL 15 DAY), 1300000, 'PERCENT', 5, 1235000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 15 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-002'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-006')
LIMIT 1;

INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-007', DATE_SUB(CURDATE(), INTERVAL 10 DAY), 2400000, 'AMOUNT', 200000, 2200000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 10 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-003'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-007')
LIMIT 1;

INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-008', DATE_SUB(CURDATE(), INTERVAL 5 DAY), 500000, 'NONE', 0, 500000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 5 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-005'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-008')
LIMIT 1;

INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-009', DATE_SUB(CURDATE(), INTERVAL 3 DAY), 1300000, 'NONE', 0, 1300000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 3 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-001'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-009')
LIMIT 1;

INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-010', DATE_SUB(CURDATE(), INTERVAL 1 DAY), 4500000, 'PERCENT', 10, 4050000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-003'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-010')
LIMIT 1;

INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-011', CURDATE(), 500000, 'NONE', 0, 500000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = CURDATE()
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-002'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-011')
LIMIT 1;

INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-012', DATE_SUB(CURDATE(), INTERVAL 60 DAY), 1300000, 'NONE', 0, 1300000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 60 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-001'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-012')
LIMIT 1;

INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-013', DATE_SUB(CURDATE(), INTERVAL 45 DAY), 2400000, 'AMOUNT', 100000, 2300000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 45 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-003'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-013')
LIMIT 1;

INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-014', DATE_SUB(CURDATE(), INTERVAL 20 DAY), 500000, 'NONE', 0, 500000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 20 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-005'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-014')
LIMIT 1;

-- =====================================================================
-- 12. INVOICE ITEMS (Chi tiết hóa đơn)
-- =====================================================================

-- Items cho tất cả invoices
INSERT INTO invoice_items (invoice_id, item_type, ref_id, description, qty, unit_price, line_total)
SELECT i.id, 'PLAN', p.id, p.name, 1, i.subtotal_amount, i.subtotal_amount
FROM invoices i
JOIN subscriptions s ON i.subscription_id = s.id
JOIN plans p ON s.plan_id = p.id
WHERE i.invoice_no IN ('INV-0001', 'INV-0002', 'INV-2024-001', 'INV-2024-002', 'INV-2024-003', 
                       'INV-2024-005', 'INV-2024-006', 'INV-2024-007', 'INV-2024-008', 'INV-2024-009', 
                       'INV-2024-010', 'INV-2024-011', 'INV-2024-012', 'INV-2024-013', 'INV-2024-014')
  AND NOT EXISTS (SELECT 1 FROM invoice_items WHERE invoice_id = i.id);

-- Item cho Invoice 4 (không có subscription, dùng plan trực tiếp)
INSERT INTO invoice_items (invoice_id, item_type, ref_id, description, qty, unit_price, line_total)
SELECT i.id, 'PLAN', p.id, p.name, 1, 500000, 500000
FROM invoices i
JOIN plans p ON p.code = 'PKG-001'
WHERE i.invoice_no = 'INV-2024-004'
  AND NOT EXISTS (SELECT 1 FROM invoice_items WHERE invoice_id = i.id);

-- =====================================================================
-- 13. PAYMENTS (Thanh toán) - Gộp từ V2, V3, V4 (đã loại bỏ shift_id)
-- =====================================================================

-- Payments từ V2
INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, created_by)
SELECT i.id, pm.id, i.total_amount, '2025-11-08', u.id
FROM invoices i
JOIN payment_methods pm ON pm.code = 'CASH'
JOIN users u ON u.username = 'staff'
WHERE i.invoice_no = 'INV-0001'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND method_id = pm.id AND is_refund = 0);

INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, created_by)
SELECT i.id, pm.id, i.total_amount / 2, '2025-11-09', u.id
FROM invoices i
JOIN payment_methods pm ON pm.code = 'BANK'
JOIN users u ON u.username = 'staff'
WHERE i.invoice_no = 'INV-0002'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND method_id = pm.id AND is_refund = 0);

INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, created_by)
SELECT i.id, pm.id, i.total_amount / 2, '2025-11-10', u.id
FROM invoices i
JOIN payment_methods pm ON pm.code = 'QR'
JOIN users u ON u.username = 'staff'
WHERE i.invoice_no = 'INV-0002'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND method_id = pm.id AND is_refund = 0);

-- Payments từ V3
INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, created_by)
SELECT i.id, pm.id, 1300000, DATE_SUB(CURDATE(), INTERVAL 70 DAY), u.id
FROM invoices i
JOIN payment_methods pm ON pm.code = 'CASH'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-001'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, created_by)
SELECT i.id, pm.id, 2300000, DATE_SUB(CURDATE(), INTERVAL 80 DAY), u.id
FROM invoices i
JOIN payment_methods pm ON pm.code = 'BANK'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-003'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

-- Payments từ V4
INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 500000, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'CASH-001', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'CASH'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-005'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 1235000, DATE_SUB(CURDATE(), INTERVAL 15 DAY), 'BANK-TRANSFER-001', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'BANK'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-006'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 2200000, DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'QR-001', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'QR'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-007'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 500000, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'CASH-002', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'CASH'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-008'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 1300000, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'BANK-TRANSFER-002', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'BANK'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-009'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 4050000, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'QR-002', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'QR'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-010'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 500000, CURDATE(), 'CASH-003', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'CASH'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-011'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 1300000, DATE_SUB(CURDATE(), INTERVAL 60 DAY), 'BANK-TRANSFER-003', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'BANK'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-012'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 2300000, DATE_SUB(CURDATE(), INTERVAL 45 DAY), 'CASH-004', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'CASH'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-013'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 500000, DATE_SUB(CURDATE(), INTERVAL 20 DAY), 'QR-003', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'QR'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-014'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);


-- =====================================================================
-- KẾT THÚC SEED DATA
-- =====================================================================
