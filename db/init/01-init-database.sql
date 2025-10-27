-- =====================================================================
-- GymPro Database Initialization Script
-- This script runs after the main schema migration
-- =====================================================================

-- Create application user with proper permissions
CREATE USER IF NOT EXISTS 'gympro_user'@'%' IDENTIFIED BY 'gympro_password';
GRANT ALL PRIVILEGES ON gympro.* TO 'gympro_user'@'%';
FLUSH PRIVILEGES;

-- Set default database
USE gympro;

-- Insert default data if tables are empty
INSERT IGNORE INTO branches(code, name, address, phone) VALUES 
('BR-001','Chi nhánh 1','123 Đường ABC, Quận 1, TP.HCM','0123-456-789');

INSERT IGNORE INTO roles(name, description) VALUES 
('OWNER','Chủ phòng gym'),
('STAFF','Nhân viên quầy');

INSERT IGNORE INTO payment_methods(code, display_name) VALUES 
('CASH','Tiền mặt'),
('BANK','Chuyển khoản'),
('QR','Quét QR');

-- Chính sách chiết khấu
INSERT IGNORE INTO discount_policies(role_name, max_percent, max_amount)
VALUES ('STAFF', 10.00, 100000.00), ('OWNER', NULL, NULL);

-- Cấu hình hệ thống mặc định
INSERT IGNORE INTO system_configs(config_key, config_value, config_type, description) VALUES
('GYM_NAME', 'GymPro Fitness Center', 'STRING', 'Tên phòng gym'),
('GYM_ADDRESS', '123 Đường ABC, Quận 1, TP.HCM', 'STRING', 'Địa chỉ phòng gym'),
('GYM_PHONE', '0123-456-789', 'STRING', 'Số điện thoại liên hệ'),
('GYM_EMAIL', 'info@gympro.com', 'STRING', 'Email liên hệ'),
('GRACE_DAYS', '5', 'NUMBER', 'Số ngày gia hạn trước khi hết hạn'),
('MEMBER_CODE_PREFIX', 'GYM', 'STRING', 'Tiền tố mã hội viên'),
('INVOICE_PREFIX', 'INV', 'STRING', 'Tiền tố số hóa đơn'),
('CURRENCY_SYMBOL', '₫', 'STRING', 'Ký hiệu tiền tệ'),
('CURRENCY_FORMAT', 'vi-VN', 'STRING', 'Định dạng tiền tệ'),
('INVOICE_TEMPLATE', 'default', 'STRING', 'Mẫu hóa đơn mặc định'),
('BACKUP_RETENTION_DAYS', '30', 'NUMBER', 'Số ngày lưu trữ backup'),
('AUTO_REMINDER_DAYS', '7', 'NUMBER', 'Số ngày nhắc hạn tự động');

-- Create default admin user (password: admin123)
INSERT IGNORE INTO users (branch_id, username, full_name, email, phone, password_hash, is_active) VALUES
(1, 'admin', 'Administrator', 'admin@gympro.com', '0123456789', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 1);

-- Assign admin role
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
(1, 1); -- admin is OWNER

-- Create sample plans
INSERT IGNORE INTO plans (branch_id, code, name, description, price, duration_days, is_active) VALUES
(1, 'PLAN-001', 'Gói 1 tháng', 'Gói tập 1 tháng cho hội viên mới', 500000, 30, 1),
(1, 'PLAN-002', 'Gói 3 tháng', 'Gói tập 3 tháng tiết kiệm', 1200000, 90, 1),
(1, 'PLAN-003', 'Gói 6 tháng', 'Gói tập 6 tháng ưu đãi', 2000000, 180, 1),
(1, 'PLAN-004', 'Gói 12 tháng', 'Gói tập 12 tháng VIP', 3500000, 365, 1);

-- Initialize counters
INSERT IGNORE INTO counters (counter_key, current_value) VALUES
('MEMBER_CODE_2024', 0),
('INVOICE_NO_2024', 0);

COMMIT;
