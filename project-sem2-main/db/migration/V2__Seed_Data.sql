-- =====================================================================
-- V2 - Seed Data: Đầy đủ dữ liệu mặc định cho GymPro
-- Tất cả INSERT đều dùng WHERE NOT EXISTS để idempotent (an toàn khi chạy lại)
-- =====================================================================

-- =====================================================================
-- 1. BRANCHES (Chi nhánh)
-- =====================================================================
INSERT INTO
    branches (code, name, address, phone)
SELECT 'BR-001', 'Chi nhánh 1', '123 Đường ABC, Quận 1, TP.HCM', '0123-456-789'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM branches
        WHERE
            code = 'BR-001'
    );

-- =====================================================================
-- 2. ROLES (Vai trò người dùng)
-- =====================================================================
INSERT INTO
    roles (name, description)
SELECT 'OWNER', 'Chủ phòng gym'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM roles
        WHERE
            name = 'OWNER'
    );

INSERT INTO
    roles (name, description)
SELECT 'STAFF', 'Nhân viên quầy'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM roles
        WHERE
            name = 'STAFF'
    );

-- =====================================================================
-- 3. PAYMENT METHODS (Phương thức thanh toán)
-- =====================================================================
INSERT INTO
    payment_methods (code, display_name)
SELECT 'CASH', 'Tiền mặt'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM payment_methods
        WHERE
            code = 'CASH'
    );

INSERT INTO
    payment_methods (code, display_name)
SELECT 'BANK', 'Chuyển khoản'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM payment_methods
        WHERE
            code = 'BANK'
    );

INSERT INTO
    payment_methods (code, display_name)
SELECT 'QR', 'Quét QR'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM payment_methods
        WHERE
            code = 'QR'
    );

-- =====================================================================
-- 4. DISCOUNT POLICIES (Chính sách chiết khấu)
-- =====================================================================
INSERT INTO
    discount_policies (
        role_name,
        max_percent,
        max_amount
    )
SELECT 'STAFF', 10.00, 100000.00
WHERE
    NOT EXISTS (
        SELECT 1
        FROM discount_policies
        WHERE
            role_name = 'STAFF'
    );

INSERT INTO
    discount_policies (
        role_name,
        max_percent,
        max_amount
    )
SELECT 'OWNER', NULL, NULL
WHERE
    NOT EXISTS (
        SELECT 1
        FROM discount_policies
        WHERE
            role_name = 'OWNER'
    );

-- =====================================================================
-- 5. SYSTEM CONFIGS (Cấu hình hệ thống)
-- =====================================================================
INSERT INTO
    system_configs (
        config_key,
        config_value,
        config_type,
        description
    )
SELECT 'GYM_NAME', 'GymPro Fitness Center', 'STRING', 'Tên phòng gym'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM system_configs
        WHERE
            config_key = 'GYM_NAME'
    );

INSERT INTO
    system_configs (
        config_key,
        config_value,
        config_type,
        description
    )
SELECT 'GYM_ADDRESS', '123 Đường ABC, Quận 1, TP.HCM', 'STRING', 'Địa chỉ phòng gym'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM system_configs
        WHERE
            config_key = 'GYM_ADDRESS'
    );

INSERT INTO
    system_configs (
        config_key,
        config_value,
        config_type,
        description
    )
SELECT 'GYM_PHONE', '0123-456-789', 'STRING', 'Số điện thoại liên hệ'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM system_configs
        WHERE
            config_key = 'GYM_PHONE'
    );

INSERT INTO
    system_configs (
        config_key,
        config_value,
        config_type,
        description
    )
SELECT 'GYM_EMAIL', 'info@gympro.com', 'STRING', 'Email liên hệ'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM system_configs
        WHERE
            config_key = 'GYM_EMAIL'
    );

INSERT INTO
    system_configs (
        config_key,
        config_value,
        config_type,
        description
    )
SELECT 'GRACE_DAYS', '5', 'NUMBER', 'Số ngày gia hạn trước khi hết hạn'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM system_configs
        WHERE
            config_key = 'GRACE_DAYS'
    );

INSERT INTO
    system_configs (
        config_key,
        config_value,
        config_type,
        description
    )
SELECT 'MEMBER_CODE_PREFIX', 'GYM', 'STRING', 'Tiền tố mã hội viên'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM system_configs
        WHERE
            config_key = 'MEMBER_CODE_PREFIX'
    );

INSERT INTO
    system_configs (
        config_key,
        config_value,
        config_type,
        description
    )
SELECT 'INVOICE_PREFIX', 'INV', 'STRING', 'Tiền tố số hóa đơn'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM system_configs
        WHERE
            config_key = 'INVOICE_PREFIX'
    );

INSERT INTO
    system_configs (
        config_key,
        config_value,
        config_type,
        description
    )
SELECT 'CURRENCY_SYMBOL', '₫', 'STRING', 'Ký hiệu tiền tệ'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM system_configs
        WHERE
            config_key = 'CURRENCY_SYMBOL'
    );

INSERT INTO
    system_configs (
        config_key,
        config_value,
        config_type,
        description
    )
SELECT 'CURRENCY_FORMAT', 'vi-VN', 'STRING', 'Định dạng tiền tệ'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM system_configs
        WHERE
            config_key = 'CURRENCY_FORMAT'
    );

INSERT INTO
    system_configs (
        config_key,
        config_value,
        config_type,
        description
    )
SELECT 'INVOICE_TEMPLATE', 'default', 'STRING', 'Mẫu hóa đơn mặc định'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM system_configs
        WHERE
            config_key = 'INVOICE_TEMPLATE'
    );

INSERT INTO
    system_configs (
        config_key,
        config_value,
        config_type,
        description
    )
SELECT 'BACKUP_RETENTION_DAYS', '30', 'NUMBER', 'Số ngày lưu trữ backup'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM system_configs
        WHERE
            config_key = 'BACKUP_RETENTION_DAYS'
    );

INSERT INTO
    system_configs (
        config_key,
        config_value,
        config_type,
        description
    )
SELECT 'AUTO_REMINDER_DAYS', '7', 'NUMBER', 'Số ngày nhắc hạn tự động'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM system_configs
        WHERE
            config_key = 'AUTO_REMINDER_DAYS'
    );

-- =====================================================================
-- 6. USERS (Người dùng - Test Accounts)
-- Passwords đã được hash bằng BCrypt:
--   admin: admin123
--   staff: staff123
--   manager: manager123
-- =====================================================================

-- Admin user (password: admin123)
INSERT INTO
    users (
        branch_id,
        username,
        full_name,
        email,
        phone,
        password_hash,
        is_active
    )
SELECT b.id, 'admin', 'Administrator', 'admin@gympro.com', '0123456789', '$2a$10$aczKTH1d5ImEYD3XmL2L8Of75MAhQGlk5QyvjHTgQ7t72qlUOBEgS', 1
FROM branches b
WHERE
    b.code = 'BR-001'
    AND NOT EXISTS (
        SELECT 1
        FROM users
        WHERE
            username = 'admin'
    );

-- Staff user (password: staff123)
INSERT INTO
    users (
        branch_id,
        username,
        full_name,
        email,
        phone,
        password_hash,
        is_active
    )
SELECT b.id, 'staff', 'Nhân viên Test', 'staff@gympro.com', '0987654321', '$2a$10$4thd6VL22evbKAYTGZQHDuIvlgpy3PaiL5dNQdXan1jfj2RgjAsjy', 1
FROM branches b
WHERE
    b.code = 'BR-001'
    AND NOT EXISTS (
        SELECT 1
        FROM users
        WHERE
            username = 'staff'
    );

-- Manager user (password: manager123)
INSERT INTO
    users (
        branch_id,
        username,
        full_name,
        email,
        phone,
        password_hash,
        is_active
    )
SELECT b.id, 'manager', 'Quản lý Test', 'manager@gympro.com', '0912345678', '$2a$10$rORI/A9ufqDyL3QLN.XcLOT9n/SQzQPbT3r91RdyaeSWmwtULjquy', 1
FROM branches b
WHERE
    b.code = 'BR-001'
    AND NOT EXISTS (
        SELECT 1
        FROM users
        WHERE
            username = 'manager'
    );

-- =====================================================================
-- 7. USER ROLES (Phân quyền cho users)
-- =====================================================================

-- Grant OWNER role to admin
INSERT INTO
    user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
    JOIN roles r ON r.name = 'OWNER'
WHERE
    u.username = 'admin'
    AND NOT EXISTS (
        SELECT 1
        FROM user_roles ur
        WHERE
            ur.user_id = u.id
            AND ur.role_id = r.id
    );

-- Grant STAFF role to staff
INSERT INTO
    user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
    JOIN roles r ON r.name = 'STAFF'
WHERE
    u.username = 'staff'
    AND NOT EXISTS (
        SELECT 1
        FROM user_roles ur
        WHERE
            ur.user_id = u.id
            AND ur.role_id = r.id
    );

-- Grant OWNER role to manager
INSERT INTO
    user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
    JOIN roles r ON r.name = 'OWNER'
WHERE
    u.username = 'manager'
    AND NOT EXISTS (
        SELECT 1
        FROM user_roles ur
        WHERE
            ur.user_id = u.id
            AND ur.role_id = r.id
    );
-- =====================================================================
-- 8. PLANS (Gói tập)
-- =====================================================================
INSERT INTO
    plans (
        branch_id,
        code,
        name,
        description,
        price,
        duration_days,
        is_active
    )
SELECT b.id, 'PL-001', 'Gói Cơ bản', 'Gói tập cơ bản 1 tháng', 500000, 30, 1
FROM branches b
WHERE
    b.code = 'BR-001'
    AND NOT EXISTS (
        SELECT 1
        FROM plans
        WHERE
            code = 'PL-001'
    );

INSERT INTO
    plans (
        branch_id,
        code,
        name,
        description,
        price,
        duration_days,
        is_active
    )
SELECT b.id, 'PL-002', 'Gói Nâng cao', 'Gói tập nâng cao 3 tháng', 1200000, 90, 1
FROM branches b
WHERE
    b.code = 'BR-001'
    AND NOT EXISTS (
        SELECT 1
        FROM plans
        WHERE
            code = 'PL-002'
    );

-- =====================================================================
-- 9. MEMBERS (Hội viên)
-- =====================================================================
INSERT INTO
    members (
        branch_id,
        member_code,
        full_name,
        phone,
        email,
        gender,
        dob,
        address,
        status,
        is_deleted
    )
SELECT b.id, 'MEM-0001', 'Nguyễn Văn A', '0901111111', 'a@gmail.com', 'MALE', '1990-01-01', '123 Đường ABC', 'ACTIVE', 0
FROM branches b
WHERE
    b.code = 'BR-001'
    AND NOT EXISTS (
        SELECT 1
        FROM members
        WHERE
            member_code = 'MEM-0001'
    );

INSERT INTO
    members (
        branch_id,
        member_code,
        full_name,
        phone,
        email,
        gender,
        dob,
        address,
        status,
        is_deleted
    )
SELECT b.id, 'MEM-0002', 'Trần Thị B', '0902222222', 'b@gmail.com', 'FEMALE', '1992-05-12', '456 Đường DEF', 'ACTIVE', 0
FROM branches b
WHERE
    b.code = 'BR-001'
    AND NOT EXISTS (
        SELECT 1
        FROM members
        WHERE
            member_code = 'MEM-0002'
    );

-- =====================================================================
-- 10. SUBSCRIPTIONS (Đăng ký gói)
-- =====================================================================
INSERT INTO
    subscriptions (
        member_id,
        plan_id,
        start_date,
        end_date,
        status,
        created_by,
        note
    )
SELECT m.id, p.id, '2025-11-11', DATE_ADD(
        '2025-11-11', INTERVAL p.duration_days DAY
    ), 'ACTIVE', u.id, 'Đăng ký thử'
FROM members m
    JOIN plans p ON p.code = 'PL-001'
    JOIN users u ON u.username = 'admin'
WHERE
    m.member_code = 'MEM-0001'
    AND NOT EXISTS (
        SELECT 1
        FROM subscriptions
        WHERE
            member_id = m.id
            AND plan_id = p.id
    );

INSERT INTO
    subscriptions (
        member_id,
        plan_id,
        start_date,
        end_date,
        status,
        created_by,
        note
    )
SELECT m.id, p.id, CURDATE(), DATE_ADD(
        CURDATE(), INTERVAL p.duration_days DAY
    ), 'ACTIVE', u.id, 'Đăng ký thử'
FROM members m
    JOIN plans p ON p.code = 'PL-002'
    JOIN users u ON u.username = 'admin'
WHERE
    m.member_code = 'MEM-0002'
    AND NOT EXISTS (
        SELECT 1
        FROM subscriptions
        WHERE
            member_id = m.id
            AND plan_id = p.id
    );

-- =====================================================================
-- 11. SHIFTS (Ca làm việc)
-- =====================================================================
INSERT INTO
    shifts (
        branch_id,
        opened_by,
        opened_at,
        note
    )
SELECT b.id, u.id, NOW(), 'Ca sáng'
FROM branches b
    JOIN users u ON u.username = 'staff'
WHERE
    b.code = 'BR-001'
    AND NOT EXISTS (
        SELECT 1
        FROM shifts
        WHERE
            branch_id = b.id
            AND opened_by = u.id
    );

-- =====================================================================
-- 12. INVOICES (Hóa đơn)
-- =====================================================================
INSERT INTO
    invoices (
        member_id,
        subscription_id,
        shift_id,
        invoice_no,
        issue_date,
        subtotal_amount,
        discount_type,
        discount_value,
        total_amount,
        status,
        created_by
    )
SELECT m.id, s.id, sh.id, 'INV-0001', CURDATE(), 500000, 'NONE', 0, 500000, 'ISSUED', u.id
FROM
    members m
    JOIN subscriptions s ON s.member_id = m.id
    JOIN shifts sh ON sh.branch_id = m.branch_id
    JOIN users u ON u.username = 'staff'
WHERE
    m.member_code = 'MEM-0001'
    AND NOT EXISTS (
        SELECT 1
        FROM invoices
        WHERE
            invoice_no = 'INV-0001'
    );

-- Hóa đơn thứ 2 cho MEM-0002
INSERT INTO
    invoices (
        member_id,
        subscription_id,
        shift_id,
        invoice_no,
        issue_date,
        subtotal_amount,
        discount_type,
        discount_value,
        total_amount,
        status,
        created_by
    )
SELECT m.id, s.id, sh.id, 'INV-0002', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 750000, 'PERCENT', 5, 712500, 'ISSUED', u.id
FROM
    members m
    JOIN subscriptions s ON s.member_id = m.id
    JOIN shifts sh ON sh.branch_id = m.branch_id
    JOIN users u ON u.username = 'staff'
WHERE
    m.member_code = 'MEM-0002'
    AND NOT EXISTS (
        SELECT 1
        FROM invoices
        WHERE
            invoice_no = 'INV-0002'
    );

-- =====================================================================
-- 13. INVOICE ITEMS (Chi tiết hóa đơn)
-- =====================================================================
INSERT INTO
    invoice_items (
        invoice_id,
        item_type,
        ref_id,
        description,
        qty,
        unit_price,
        line_total
    )
SELECT i.id, 'PLAN', p.id, p.name, 1, p.price, p.price
FROM
    invoices i
    JOIN subscriptions s ON i.subscription_id = s.id
    JOIN plans p ON p.id = s.plan_id
WHERE
    i.invoice_no IN ('INV-0001', 'INV-0002')
    AND NOT EXISTS (
        SELECT 1
        FROM invoice_items ii
        WHERE
            ii.invoice_id = i.id
    );

-- =====================================================================
-- 14. PAYMENTS (Thanh toán)
-- =====================================================================

INSERT INTO
    payments (
        invoice_id,
        method_id,
        shift_id,
        paid_amount,
        paid_at,
        created_by
    )
SELECT i.id, pm.id, i.shift_id, i.total_amount, '2025-11-08', u.id
FROM
    invoices i
    JOIN payment_methods pm ON pm.code = 'CASH'
    JOIN users u ON u.username = 'staff'
WHERE
    i.invoice_no = 'INV-0001'
    AND NOT EXISTS (
        SELECT 1
        FROM payments
        WHERE
            invoice_id = i.id
            AND method_id = pm.id
    );

INSERT INTO
    payments (
        invoice_id,
        method_id,
        shift_id,
        paid_amount,
        paid_at,
        created_by
    )
SELECT i.id, pm.id, i.shift_id, i.total_amount / 2, '2025-11-09', u.id
FROM
    invoices i
    JOIN payment_methods pm ON pm.code = 'BANK'
    JOIN users u ON u.username = 'staff'
WHERE
    i.invoice_no = 'INV-0002'
    AND NOT EXISTS (
        SELECT 1
        FROM payments
        WHERE
            invoice_id = i.id
            AND method_id = pm.id
    );

INSERT INTO
    payments (
        invoice_id,
        method_id,
        shift_id,
        paid_amount,
        paid_at,
        created_by
    )
SELECT i.id, pm.id, i.shift_id, i.total_amount / 2, '2025-11-10', u.id
FROM
    invoices i
    JOIN payment_methods pm ON pm.code = 'QR'
    JOIN users u ON u.username = 'staff'
WHERE
    i.invoice_no = 'INV-0002'
    AND NOT EXISTS (
        SELECT 1
        FROM payments
        WHERE
            invoice_id = i.id
            AND method_id = pm.id
    );

INSERT INTO
    payments (
        invoice_id,
        method_id,
        shift_id,
        paid_amount,
        paid_at,
        created_by
    )
SELECT i.id, pm.id, i.shift_id, 0, NULL, u.id
FROM
    invoices i
    JOIN payment_methods pm ON pm.code = 'BANK'
    JOIN users u ON u.username = 'staff'
WHERE
    i.invoice_no = 'INV-0003'
    AND NOT EXISTS (
        SELECT 1
        FROM payments
        WHERE
            invoice_id = i.id
            AND method_id = pm.id
    );

INSERT INTO
    payments (
        invoice_id,
        method_id,
        shift_id,
        paid_amount,
        paid_at,
        created_by
    )
SELECT i.id, pm.id, i.shift_id, i.total_amount * 0.3, '2025-11-11', u.id
FROM
    invoices i
    JOIN payment_methods pm ON pm.code = 'CASH'
    JOIN users u ON u.username = 'staff'
WHERE
    i.invoice_no = 'INV-0004'
    AND NOT EXISTS (
        SELECT 1
        FROM payments
        WHERE
            invoice_id = i.id
            AND method_id = pm.id
    );

INSERT INTO
    payments (
        invoice_id,
        method_id,
        shift_id,
        paid_amount,
        paid_at,
        created_by
    )
SELECT i.id, pm.id, i.shift_id, i.total_amount * 0.7, '2025-11-12', u.id
FROM
    invoices i
    JOIN payment_methods pm ON pm.code = 'QR'
    JOIN users u ON u.username = 'staff'
WHERE
    i.invoice_no = 'INV-0004'
    AND NOT EXISTS (
        SELECT 1
        FROM payments
        WHERE
            invoice_id = i.id
            AND method_id = pm.id
    );

INSERT INTO
    invoices (
        member_id,
        subscription_id,
        shift_id,
        invoice_no,
        issue_date,
        subtotal_amount,
        discount_type,
        discount_value,
        total_amount,
        status,
        created_by
    )
SELECT m.id, p.id, sh.id, 'INV-0005', '2025-11-13', 600000, 'NONE', 0, 600000, 'ISSUED', u.id
FROM
    members m
    JOIN subscriptions s ON s.member_id = m.id
    JOIN plans p ON p.id = s.plan_id
    JOIN shifts sh ON sh.branch_id = m.branch_id
    JOIN users u ON u.username = 'staff'
WHERE
    m.member_code = 'MEM-0001'
    AND NOT EXISTS (
        SELECT 1
        FROM invoices
        WHERE
            invoice_no = 'INV-0005'
    );

INSERT INTO
    payments (
        invoice_id,
        method_id,
        shift_id,
        paid_amount,
        paid_at,
        created_by
    )
SELECT i.id, pm.id, i.shift_id, i.total_amount, '2025-11-13', u.id
FROM
    invoices i
    JOIN payment_methods pm ON pm.code = 'CASH'
    JOIN users u ON u.username = 'staff'
WHERE
    i.invoice_no = 'INV-0005'
    AND NOT EXISTS (
        SELECT 1
        FROM payments
        WHERE
            invoice_id = i.id
            AND method_id = pm.id
    );

-- =====================================================================
-- 15. REMINDERS (Nhắc hạn)
-- =====================================================================
INSERT INTO
    reminders (
        member_id,
        subscription_id,
        reminder_date,
        channel,
        status
    )
SELECT s.member_id, s.id, DATE_ADD(s.end_date, INTERVAL -3 DAY), 'EMAIL', 'PENDING'
FROM subscriptions s
WHERE
    NOT EXISTS (
        SELECT 1
        FROM reminders r
        WHERE
            r.subscription_id = s.id
    );

-- =====================================================================
-- KẾT THÚC SEED DATA
-- =====================================================================