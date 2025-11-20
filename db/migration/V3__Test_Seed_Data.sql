-- =====================================================================
-- V3__Test_Seed_Data.sql
-- Seed data đơn giản cho testing (4-5 dữ liệu mỗi loại)
-- =====================================================================

-- =====================================================================
-- 1. BRANCHES (Chi nhánh) - 1 chi nhánh
-- =====================================================================
INSERT INTO branches (code, name, address, phone)
SELECT 'BR-001', 'Chi nhánh Trung tâm', '123 Đường ABC, Quận 1, TP.HCM', '0123-456-789'
WHERE NOT EXISTS (SELECT 1 FROM branches WHERE code = 'BR-001');

-- =====================================================================
-- 2. ROLES (Vai trò) - 2 roles
-- =====================================================================
INSERT INTO roles (name, description)
SELECT 'OWNER', 'Chủ phòng gym'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'OWNER');

INSERT INTO roles (name, description)
SELECT 'STAFF', 'Nhân viên quầy'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'STAFF');

-- =====================================================================
-- 3. PAYMENT METHODS (Phương thức thanh toán) - 3 methods
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

-- =====================================================================
-- 4. USERS (Người dùng) - 3 users
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

-- Manager user (password: manager123)
INSERT INTO users (branch_id, username, full_name, email, phone, password_hash, is_active)
SELECT b.id, 'manager', 'Quản lý Test', 'manager@gympro.com', '0912345678', 
       '$2a$10$aczKTH1d5ImEYD3XmL2L8Of75MAhQGlk5QyvjHTgQ7t72qlUOBEgS', 1
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'manager');

-- =====================================================================
-- 5. USER ROLES (Phân quyền) - 3 assignments
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

-- Grant OWNER role to manager
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u JOIN roles r ON r.name = 'OWNER'
WHERE u.username = 'manager' 
  AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

-- =====================================================================
-- 6. PLANS (Gói tập) - 4 plans
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
-- 7. MEMBERS (Hội viên) - 5 members
-- =====================================================================
INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note)
SELECT b.id, 'GYM-2024-001', 'Nguyễn Văn An', '0901234567', 'nguyenvanan@email.com', 'MALE', '1990-01-15', '123 Đường A, Quận 1', 'ACTIVE', 'Hội viên VIP'
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-001');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note)
SELECT b.id, 'GYM-2024-002', 'Trần Thị Bình', '0912345678', 'tranthibinh@email.com', 'FEMALE', '1995-05-20', '456 Đường B, Quận 2', 'ACTIVE', NULL
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-002');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note)
SELECT b.id, 'GYM-2024-003', 'Lê Văn Cường', '0923456789', 'levancuong@email.com', 'MALE', '1988-08-10', '789 Đường C, Quận 3', 'ACTIVE', NULL
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-003');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note)
SELECT b.id, 'GYM-2024-004', 'Phạm Thị Dung', '0934567890', 'phamthidung@email.com', 'FEMALE', '1992-12-25', '321 Đường D, Quận 4', 'PENDING', 'Chờ thanh toán'
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-004');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note)
SELECT b.id, 'GYM-2024-005', 'Hoàng Văn Em', '0945678901', 'hoangvanem@email.com', 'MALE', '1993-03-30', '654 Đường E, Quận 5', 'ACTIVE', NULL
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-005');

-- =====================================================================
-- 8. SUBSCRIPTIONS (Đăng ký) - 4 subscriptions
-- =====================================================================
-- Member 1: Gói 3 tháng, còn hạn 20 ngày
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 70 DAY), DATE_ADD(CURDATE(), INTERVAL 20 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-001' AND p.code = 'PKG-002'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.status = 'ACTIVE');

-- Member 2: Gói 1 tháng, sắp hết hạn (2 ngày)
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 28 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-002' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.status = 'ACTIVE');

-- Member 3: Gói 6 tháng, còn hạn 100 ngày
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 80 DAY), DATE_ADD(CURDATE(), INTERVAL 100 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-003' AND p.code = 'PKG-003'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.status = 'ACTIVE');

-- Member 5: Gói 1 tháng, đã hết hạn 5 ngày
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 35 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-005' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_SUB(CURDATE(), INTERVAL 5 DAY));

-- =====================================================================
-- 9. INVOICES (Hóa đơn) - 4 invoices
-- =====================================================================
-- Invoice 1: Đã thanh toán (có payment)
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-001', DATE_SUB(CURDATE(), INTERVAL 70 DAY), 1300000, 'NONE', 0, 1300000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.status = 'ACTIVE'
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-001'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-001');

-- Invoice 2: Chưa thanh toán (sắp hết hạn) - UNPAID
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-002', DATE_SUB(CURDATE(), INTERVAL 28 DAY), 500000, 'NONE', 0, 500000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.status = 'ACTIVE'
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-002'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-002');

-- Invoice 3: Đã thanh toán (có payment, có chiết khấu)
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-003', DATE_SUB(CURDATE(), INTERVAL 80 DAY), 2400000, 'AMOUNT', 100000, 2300000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.status = 'ACTIVE'
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-003'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-003');

-- Invoice 4: Chưa thanh toán (cho member 4 - PENDING, chưa có subscription)
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, NULL, 'INV-2024-004', CURDATE(), 500000, 'NONE', 0, 500000, 'ISSUED', u.id
FROM members m
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-004'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-004');

-- =====================================================================
-- 10. INVOICE ITEMS (Chi tiết hóa đơn) - 4 items
-- =====================================================================
-- Item cho Invoice 1
INSERT INTO invoice_items (invoice_id, item_type, ref_id, description, qty, unit_price, line_total)
SELECT i.id, 'PLAN', p.id, p.name, 1, 1300000, 1300000
FROM invoices i
JOIN subscriptions s ON i.subscription_id = s.id
JOIN plans p ON s.plan_id = p.id
WHERE i.invoice_no = 'INV-2024-001'
  AND NOT EXISTS (SELECT 1 FROM invoice_items WHERE invoice_id = i.id);

-- Item cho Invoice 2
INSERT INTO invoice_items (invoice_id, item_type, ref_id, description, qty, unit_price, line_total)
SELECT i.id, 'PLAN', p.id, p.name, 1, 500000, 500000
FROM invoices i
JOIN subscriptions s ON i.subscription_id = s.id
JOIN plans p ON s.plan_id = p.id
WHERE i.invoice_no = 'INV-2024-002'
  AND NOT EXISTS (SELECT 1 FROM invoice_items WHERE invoice_id = i.id);

-- Item cho Invoice 3
INSERT INTO invoice_items (invoice_id, item_type, ref_id, description, qty, unit_price, line_total)
SELECT i.id, 'PLAN', p.id, p.name, 1, 2400000, 2400000
FROM invoices i
JOIN subscriptions s ON i.subscription_id = s.id
JOIN plans p ON s.plan_id = p.id
WHERE i.invoice_no = 'INV-2024-003'
  AND NOT EXISTS (SELECT 1 FROM invoice_items WHERE invoice_id = i.id);

-- Item cho Invoice 4 (không có subscription, dùng plan trực tiếp)
INSERT INTO invoice_items (invoice_id, item_type, ref_id, description, qty, unit_price, line_total)
SELECT i.id, 'PLAN', p.id, p.name, 1, 500000, 500000
FROM invoices i
JOIN plans p ON p.code = 'PKG-001'
WHERE i.invoice_no = 'INV-2024-004'
  AND NOT EXISTS (SELECT 1 FROM invoice_items WHERE invoice_id = i.id);

-- =====================================================================
-- 11. PAYMENTS (Thanh toán) - 2 payments
-- =====================================================================
-- Payment 1: Đã thanh toán Invoice 1
INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, shift_id, created_by)
SELECT i.id, pm.id, 1300000, DATE_SUB(CURDATE(), INTERVAL 70 DAY), NULL, u.id
FROM invoices i
JOIN payment_methods pm ON pm.code = 'CASH'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-001'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

-- Payment 2: Đã thanh toán Invoice 3
INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, shift_id, created_by)
SELECT i.id, pm.id, 2300000, DATE_SUB(CURDATE(), INTERVAL 80 DAY), NULL, u.id
FROM invoices i
JOIN payment_methods pm ON pm.code = 'BANK'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-003'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

-- =====================================================================
-- 12. LINK INVOICES TO SUBSCRIPTIONS (Đã được link trong INSERT trên)
-- =====================================================================
-- Không cần UPDATE vì đã link trong INSERT

-- =====================================================================
-- GHI CHÚ TEST:
-- =====================================================================
-- Users để test:
--   - admin / admin123 (OWNER) - Toàn quyền
--   - staff / staff123 (STAFF) - Quyền hạn chế
--   - manager / manager123 (OWNER) - Toàn quyền
--
-- Members để test:
--   - GYM-2024-001: Còn hạn 20 ngày (Gói 3 tháng)
--   - GYM-2024-002: Sắp hết hạn 2 ngày (Gói 1 tháng) - Hiện trong Expiring
--   - GYM-2024-003: Còn hạn 100 ngày (Gói 6 tháng)
--   - GYM-2024-004: PENDING - Chưa có subscription
--   - GYM-2024-005: Đã hết hạn 5 ngày - Hiện trong Expiring
--
-- Invoices để test:
--   - INV-2024-001: Đã thanh toán (có payment) - Status: ISSUED
--   - INV-2024-002: Chưa thanh toán (UNPAID) - Có thể test Payment - Status: ISSUED
--   - INV-2024-003: Đã thanh toán (có payment, có chiết khấu) - Status: ISSUED
--   - INV-2024-004: Chưa thanh toán (UNPAID) - Có thể test Payment - Status: ISSUED
-- =====================================================================

