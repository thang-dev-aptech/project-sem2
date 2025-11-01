-- =====================================================================
-- V2 - Seed base data for login (branches, roles, admin user)
-- =====================================================================

-- Branch
INSERT INTO branches(code, name, address, phone)
SELECT 'BR-001','Chi nhánh 1','123 Đường ABC, Quận 1, TP.HCM','0123-456-789'
WHERE NOT EXISTS (SELECT 1 FROM branches WHERE code = 'BR-001');

-- Roles
INSERT INTO roles(name, description)
SELECT 'OWNER','Chủ phòng gym'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name='OWNER');

INSERT INTO roles(name, description)
SELECT 'STAFF','Nhân viên quầy'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name='STAFF');

-- =====================================================================
-- TEST USERS - PASSWORDS ARE PLAIN TEXT (before hashing)
-- admin: admin123
-- staff: staff123  
-- manager: manager123
-- =====================================================================

-- Admin user (password: admin123)
INSERT INTO users (branch_id, username, full_name, email, phone, password_hash, is_active)
SELECT b.id, 'admin', 'Administrator', 'admin@gympro.com', '0123456789',
       '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 1
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

-- Optional: default payment methods for later modules (safe inserts)
INSERT INTO payment_methods(code, display_name)
SELECT 'CASH','Tiền mặt' WHERE NOT EXISTS (SELECT 1 FROM payment_methods WHERE code='CASH');
INSERT INTO payment_methods(code, display_name)
SELECT 'BANK','Chuyển khoản' WHERE NOT EXISTS (SELECT 1 FROM payment_methods WHERE code='BANK');
INSERT INTO payment_methods(code, display_name)
SELECT 'QR','Quét QR' WHERE NOT EXISTS (SELECT 1 FROM payment_methods WHERE code='QR');


