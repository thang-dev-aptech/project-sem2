-- =====================================================================
-- V2 - Seed Data: Đầy đủ dữ liệu mặc định cho GymPro
-- Tất cả INSERT đều dùng WHERE NOT EXISTS để idempotent (an toàn khi chạy lại)
-- =====================================================================

-- =====================================================================
-- 1. BRANCHES (Chi nhánh)
-- =====================================================================
INSERT INTO branches(code, name, address, phone)
SELECT 'BR-001','Chi nhánh 1','123 Đường ABC, Quận 1, TP.HCM','0123-456-789'
WHERE NOT EXISTS (SELECT 1 FROM branches WHERE code = 'BR-001');

-- =====================================================================
-- 2. ROLES (Vai trò người dùng)
-- =====================================================================
INSERT INTO roles(name, description)
SELECT 'OWNER','Chủ phòng gym'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name='OWNER');

INSERT INTO roles(name, description)
SELECT 'STAFF','Nhân viên quầy'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name='STAFF');

-- =====================================================================
-- 3. PAYMENT METHODS (Phương thức thanh toán)
-- =====================================================================
INSERT INTO payment_methods(code, display_name)
SELECT 'CASH','Tiền mặt' WHERE NOT EXISTS (SELECT 1 FROM payment_methods WHERE code='CASH');
INSERT INTO payment_methods(code, display_name)
SELECT 'BANK','Chuyển khoản' WHERE NOT EXISTS (SELECT 1 FROM payment_methods WHERE code='BANK');
INSERT INTO payment_methods(code, display_name)
SELECT 'QR','Quét QR' WHERE NOT EXISTS (SELECT 1 FROM payment_methods WHERE code='QR');

-- =====================================================================
-- 4. DISCOUNT POLICIES (Chính sách chiết khấu)
-- =====================================================================
INSERT INTO discount_policies(role_name, max_percent, max_amount)
SELECT 'STAFF', 10.00, 100000.00
WHERE NOT EXISTS (SELECT 1 FROM discount_policies WHERE role_name='STAFF');

INSERT INTO discount_policies(role_name, max_percent, max_amount)
SELECT 'OWNER', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM discount_policies WHERE role_name='OWNER');

-- =====================================================================
-- 5. SYSTEM CONFIGS (Cấu hình hệ thống)
-- =====================================================================
INSERT INTO system_configs(config_key, config_value, config_type, description)
SELECT 'GYM_NAME', 'GymPro Fitness Center', 'STRING', 'Tên phòng gym'
WHERE NOT EXISTS (SELECT 1 FROM system_configs WHERE config_key='GYM_NAME');

INSERT INTO system_configs(config_key, config_value, config_type, description)
SELECT 'GYM_ADDRESS', '123 Đường ABC, Quận 1, TP.HCM', 'STRING', 'Địa chỉ phòng gym'
WHERE NOT EXISTS (SELECT 1 FROM system_configs WHERE config_key='GYM_ADDRESS');

INSERT INTO system_configs(config_key, config_value, config_type, description)
SELECT 'GYM_PHONE', '0123-456-789', 'STRING', 'Số điện thoại liên hệ'
WHERE NOT EXISTS (SELECT 1 FROM system_configs WHERE config_key='GYM_PHONE');

INSERT INTO system_configs(config_key, config_value, config_type, description)
SELECT 'GYM_EMAIL', 'info@gympro.com', 'STRING', 'Email liên hệ'
WHERE NOT EXISTS (SELECT 1 FROM system_configs WHERE config_key='GYM_EMAIL');

INSERT INTO system_configs(config_key, config_value, config_type, description)
SELECT 'GRACE_DAYS', '5', 'NUMBER', 'Số ngày gia hạn trước khi hết hạn'
WHERE NOT EXISTS (SELECT 1 FROM system_configs WHERE config_key='GRACE_DAYS');

INSERT INTO system_configs(config_key, config_value, config_type, description)
SELECT 'MEMBER_CODE_PREFIX', 'GYM', 'STRING', 'Tiền tố mã hội viên'
WHERE NOT EXISTS (SELECT 1 FROM system_configs WHERE config_key='MEMBER_CODE_PREFIX');

INSERT INTO system_configs(config_key, config_value, config_type, description)
SELECT 'INVOICE_PREFIX', 'INV', 'STRING', 'Tiền tố số hóa đơn'
WHERE NOT EXISTS (SELECT 1 FROM system_configs WHERE config_key='INVOICE_PREFIX');

INSERT INTO system_configs(config_key, config_value, config_type, description)
SELECT 'CURRENCY_SYMBOL', '₫', 'STRING', 'Ký hiệu tiền tệ'
WHERE NOT EXISTS (SELECT 1 FROM system_configs WHERE config_key='CURRENCY_SYMBOL');

INSERT INTO system_configs(config_key, config_value, config_type, description)
SELECT 'CURRENCY_FORMAT', 'vi-VN', 'STRING', 'Định dạng tiền tệ'
WHERE NOT EXISTS (SELECT 1 FROM system_configs WHERE config_key='CURRENCY_FORMAT');

INSERT INTO system_configs(config_key, config_value, config_type, description)
SELECT 'INVOICE_TEMPLATE', 'default', 'STRING', 'Mẫu hóa đơn mặc định'
WHERE NOT EXISTS (SELECT 1 FROM system_configs WHERE config_key='INVOICE_TEMPLATE');

INSERT INTO system_configs(config_key, config_value, config_type, description)
SELECT 'BACKUP_RETENTION_DAYS', '30', 'NUMBER', 'Số ngày lưu trữ backup'
WHERE NOT EXISTS (SELECT 1 FROM system_configs WHERE config_key='BACKUP_RETENTION_DAYS');

INSERT INTO system_configs(config_key, config_value, config_type, description)
SELECT 'AUTO_REMINDER_DAYS', '7', 'NUMBER', 'Số ngày nhắc hạn tự động'
WHERE NOT EXISTS (SELECT 1 FROM system_configs WHERE config_key='AUTO_REMINDER_DAYS');

-- =====================================================================
-- 6. USERS (Người dùng - Test Accounts)
-- Passwords đã được hash bằng BCrypt:
--   admin: admin123
--   staff: staff123  
--   manager: manager123
-- =====================================================================

-- Admin user (password: admin123)
INSERT INTO users (branch_id, username, full_name, email, phone, password_hash, is_active)
SELECT b.id, 'admin', 'Administrator', 'admin@gympro.com', '0123456789',
       '$2a$10$aczKTH1d5ImEYD3XmL2L8Of75MAhQGlk5QyvjHTgQ7t72qlUOBEgS', 1
FROM branches b
WHERE b.code = 'BR-001'
  AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- Staff user (password: staff123)
INSERT INTO users (branch_id, username, full_name, email, phone, password_hash, is_active)
SELECT b.id, 'staff', 'Nhân viên Test', 'staff@gympro.com', '0987654321',
       '$2a$10$4thd6VL22evbKAYTGZQHDuIvlgpy3PaiL5dNQdXan1jfj2RgjAsjy', 1
FROM branches b
WHERE b.code = 'BR-001'
  AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'staff');

-- Manager user (password: manager123)
INSERT INTO users (branch_id, username, full_name, email, phone, password_hash, is_active)
SELECT b.id, 'manager', 'Quản lý Test', 'manager@gympro.com', '0912345678',
       '$2a$10$rORI/A9ufqDyL3QLN.XcLOT9n/SQzQPbT3r91RdyaeSWmwtULjquy', 1
FROM branches b
WHERE b.code = 'BR-001'
  AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'manager');

-- =====================================================================
-- 7. USER ROLES (Phân quyền cho users)
-- =====================================================================

-- Grant OWNER role to admin
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'OWNER'
WHERE u.username = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- Grant STAFF role to staff
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'STAFF'
WHERE u.username = 'staff'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- Grant OWNER role to manager
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'OWNER'
WHERE u.username = 'manager'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- =====================================================================
-- KẾT THÚC SEED DATA
-- =====================================================================
