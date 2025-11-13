-- =====================================================================
-- GymPro Complete Database Schema Migration
-- Version: V1
-- Description: Complete database schema for GymPro application
-- =====================================================================

-- Charset & collation
SET NAMES utf8mb4;
SET collation_connection = 'utf8mb4_0900_ai_ci';
SET sql_mode = 'STRICT_ALL_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- Force engine/collation tại session
SET @ENGINE = 'InnoDB';
SET @COLLATE = 'utf8mb4_0900_ai_ci';

-- =====================================================================
-- MASTER / DANH MỤC & CẤU HÌNH
-- =====================================================================

-- branches: Danh mục chi nhánh
CREATE TABLE branches (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  code         VARCHAR(32)  NOT NULL UNIQUE,
  name         VARCHAR(255) NOT NULL,
  address      VARCHAR(500),
  phone        VARCHAR(30),
  created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- roles: Danh mục vai trò ứng dụng
CREATE TABLE roles (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  name         VARCHAR(50) NOT NULL UNIQUE,
  description  VARCHAR(255)
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- payment_methods: Danh mục phương thức thanh toán
CREATE TABLE payment_methods (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  code          VARCHAR(32)  NOT NULL UNIQUE,
  display_name  VARCHAR(100) NOT NULL
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- settings: Cấu hình hệ thống
CREATE TABLE settings (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  key_name     VARCHAR(100) NOT NULL UNIQUE,
  value_str    VARCHAR(255),
  value_num    DECIMAL(18,6),
  value_json   JSON,
  created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- counters: Bộ đếm sinh mã tự động
CREATE TABLE counters (
  counter_key   VARCHAR(64) PRIMARY KEY,
  current_value BIGINT NOT NULL
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- discount_policies: Chính sách giới hạn chiết khấu theo vai trò
CREATE TABLE discount_policies (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_name    VARCHAR(50) NOT NULL UNIQUE,
  max_percent  DECIMAL(5,2) NULL,
  max_amount   DECIMAL(12,2) NULL
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- NGƯỜI DÙNG & PHÂN QUYỀN
-- =====================================================================

-- users: Người dùng ứng dụng
CREATE TABLE users (
  id             BIGINT PRIMARY KEY AUTO_INCREMENT,
  branch_id      BIGINT NOT NULL,
  username       VARCHAR(50) NOT NULL UNIQUE,
  full_name      VARCHAR(255) NOT NULL,
  email          VARCHAR(255),
  phone          VARCHAR(30),
  password_hash  VARCHAR(255) NOT NULL,
  is_active      TINYINT(1) DEFAULT 1,
  last_login_at  TIMESTAMP NULL,
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_users_branch FOREIGN KEY (branch_id) REFERENCES branches(id)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- user_roles: Gán vai trò cho user
CREATE TABLE user_roles (
  user_id  BIGINT NOT NULL,
  role_id  BIGINT NOT NULL,
  PRIMARY KEY(user_id, role_id),
  CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- DANH MỤC GÓI, HỘI VIÊN
-- =====================================================================

-- plans: Danh mục gói tập
CREATE TABLE plans (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  branch_id     BIGINT NOT NULL,
  code          VARCHAR(32)  NOT NULL UNIQUE,
  name          VARCHAR(255) NOT NULL,
  description   VARCHAR(500),
  price         DECIMAL(12,2) NOT NULL CHECK (price >= 0),
  duration_days INT NOT NULL CHECK (duration_days > 0),
  is_active     TINYINT(1) DEFAULT 1,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_plans_branch FOREIGN KEY (branch_id) REFERENCES branches(id)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- members: Hồ sơ hội viên
CREATE TABLE members (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  branch_id     BIGINT NOT NULL,
  member_code   VARCHAR(32)  NOT NULL UNIQUE,
  full_name     VARCHAR(255) NOT NULL,
  phone         VARCHAR(30)  NOT NULL,
  email         VARCHAR(255),
  gender        ENUM('MALE','FEMALE','OTHER') DEFAULT 'OTHER',
  dob           DATE,
  address       VARCHAR(500),
  status        ENUM('PENDING','ACTIVE','EXPIRED','PAUSED','RENEWED') DEFAULT 'PENDING',
  note          VARCHAR(500),
  is_deleted    TINYINT(1) DEFAULT 0,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_members_branch FOREIGN KEY (branch_id) REFERENCES branches(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT uq_members_branch_phone UNIQUE (branch_id, phone)
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- NGHIỆP VỤ ĐĂNG KÝ/GIA HẠN, HÓA ĐƠN, THANH TOÁN
-- =====================================================================

-- subscriptions: Đăng ký/gia hạn gói cho hội viên
CREATE TABLE subscriptions (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  member_id     BIGINT NOT NULL,
  plan_id       BIGINT NOT NULL,
  start_date    DATE   NOT NULL,
  end_date      DATE   NOT NULL,
  status        ENUM('PENDING','ACTIVE','EXPIRED','CANCELLED') DEFAULT 'PENDING',
  note          VARCHAR(500),
  created_by    BIGINT,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_sub_member FOREIGN KEY (member_id) REFERENCES members(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_sub_plan FOREIGN KEY (plan_id) REFERENCES plans(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_sub_user FOREIGN KEY (created_by) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT chk_sub_dates CHECK (end_date >= start_date)
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- shifts: Ca làm việc tại quầy
CREATE TABLE shifts (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  branch_id     BIGINT NOT NULL,
  opened_by     BIGINT NOT NULL,
  closed_by     BIGINT NULL,
  opened_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  closed_at     TIMESTAMP NULL,
  total_amount  DECIMAL(12,2) DEFAULT 0,
  note          VARCHAR(255),
  CONSTRAINT fk_shifts_branch FOREIGN KEY (branch_id) REFERENCES branches(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_shifts_opened_by FOREIGN KEY (opened_by) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_shifts_closed_by FOREIGN KEY (closed_by) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- invoices: Hóa đơn bán gói/dịch vụ
CREATE TABLE invoices (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  member_id        BIGINT NOT NULL,
  subscription_id  BIGINT NULL,
  shift_id         BIGINT NULL,
  invoice_no       VARCHAR(50) NOT NULL UNIQUE,
  issue_date       DATE DEFAULT (CURRENT_DATE),
  subtotal_amount  DECIMAL(12,2) NOT NULL,
  discount_type    ENUM('NONE','PERCENT','AMOUNT') DEFAULT 'NONE',
  discount_value   DECIMAL(12,2) DEFAULT 0,
  total_amount     DECIMAL(12,2) NOT NULL,
  status           ENUM('ISSUED','VOIDED') DEFAULT 'ISSUED',
  void_reason      VARCHAR(255) NULL,
  void_by          BIGINT NULL,
  void_at          TIMESTAMP NULL,
  created_by       BIGINT,
  created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_inv_member FOREIGN KEY (member_id) REFERENCES members(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_inv_sub FOREIGN KEY (subscription_id) REFERENCES subscriptions(id)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT fk_inv_shift FOREIGN KEY (shift_id) REFERENCES shifts(id)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT fk_inv_void_by FOREIGN KEY (void_by) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT fk_inv_created_by FOREIGN KEY (created_by) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT chk_inv_amounts CHECK (subtotal_amount >= 0 AND total_amount >= 0 AND discount_value >= 0)
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- invoice_items: Dòng chi tiết hóa đơn
CREATE TABLE invoice_items (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  invoice_id   BIGINT NOT NULL,
  item_type    ENUM('PLAN','PRODUCT','SERVICE') DEFAULT 'PLAN',
  ref_id       BIGINT,
  description  VARCHAR(255) NOT NULL,
  qty          INT DEFAULT 1 CHECK (qty > 0),
  unit_price   DECIMAL(12,2) NOT NULL CHECK (unit_price >= 0),
  line_total   DECIMAL(12,2) NOT NULL CHECK (line_total >= 0),
  CONSTRAINT fk_item_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- payments: Giao dịch thanh toán cho hóa đơn
CREATE TABLE payments (
  id                   BIGINT PRIMARY KEY AUTO_INCREMENT,
  invoice_id           BIGINT NOT NULL,
  method_id            BIGINT NOT NULL,
  shift_id             BIGINT NULL,
  paid_amount          DECIMAL(12,2) NOT NULL CHECK (paid_amount >= 0),
  paid_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  reference_code       VARCHAR(100),
  created_by           BIGINT,
  is_refund            TINYINT(1) DEFAULT 0,
  refund_of_payment_id BIGINT NULL,
  CONSTRAINT fk_pay_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_pay_method FOREIGN KEY (method_id) REFERENCES payment_methods(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_pay_shift FOREIGN KEY (shift_id) REFERENCES shifts(id)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT fk_pay_created_by FOREIGN KEY (created_by) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT fk_pay_refund_of FOREIGN KEY (refund_of_payment_id) REFERENCES payments(id)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- NHẮC HẠN, SAO LƯU, AUDIT
-- =====================================================================

-- reminders: Lịch nhắc hết hạn
CREATE TABLE reminders (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  member_id        BIGINT NOT NULL,
  subscription_id  BIGINT NOT NULL,
  reminder_date    DATE NOT NULL,
  channel          ENUM('NONE','EMAIL','ZALO','SMS') DEFAULT 'NONE',
  status           ENUM('PENDING','SENT','FAILED') DEFAULT 'PENDING',
  payload          JSON,
  created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_rem_member FOREIGN KEY (member_id) REFERENCES members(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_rem_sub FOREIGN KEY (subscription_id) REFERENCES subscriptions(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- backup_logs: Nhật ký sao lưu/khôi phục DB
CREATE TABLE backup_logs (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  file_path    VARCHAR(500) NOT NULL,
  started_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  finished_at  TIMESTAMP NULL,
  status       ENUM('SUCCESS','FAILED') NOT NULL,
  message      VARCHAR(500)
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- audit_logs: Nhật ký thao tác
CREATE TABLE audit_logs (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  table_name    VARCHAR(64) NOT NULL,
  action_type   ENUM('INSERT','UPDATE','DELETE') NOT NULL,
  row_pk        BIGINT,
  before_data   JSON,
  after_data    JSON,
  actor_user_id BIGINT NULL,
  workstation   VARCHAR(100) NULL,
  ip_address    VARCHAR(45)  NULL,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_audit_user FOREIGN KEY (actor_user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- BỔ SUNG BẢNG HỖ TRỢ CHO UI & PERFORMANCE
-- =====================================================================

-- system_configs: Cấu hình hệ thống chi tiết
CREATE TABLE system_configs (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  config_key   VARCHAR(100) NOT NULL UNIQUE,
  config_value TEXT,
  config_type  ENUM('STRING','NUMBER','JSON','BOOLEAN') DEFAULT 'STRING',
  description  VARCHAR(255),
  created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- current_shifts: Quản lý ca hiện tại đang mở
CREATE TABLE current_shifts (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  branch_id    BIGINT NOT NULL,
  user_id      BIGINT NOT NULL,
  shift_id     BIGINT NOT NULL,
  started_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_current_shift_branch FOREIGN KEY (branch_id) REFERENCES branches(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_current_shift_user FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_current_shift_shift FOREIGN KEY (shift_id) REFERENCES shifts(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT uq_current_shift_branch UNIQUE (branch_id)
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- INDEXES CHO PERFORMANCE
-- =====================================================================

-- Indexes cơ bản
CREATE INDEX idx_members_branch_phone ON members(branch_id, phone);
CREATE INDEX idx_subscriptions_member ON subscriptions(member_id);
CREATE INDEX idx_subscriptions_status ON subscriptions(status);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_shift ON invoices(shift_id);
CREATE INDEX idx_payments_shift ON payments(shift_id);

-- Indexes cho performance dashboard
CREATE INDEX idx_payments_created_at ON payments(paid_at);
CREATE INDEX idx_invoices_created_at ON invoices(created_at);
CREATE INDEX idx_subscriptions_end_date ON subscriptions(end_date);
CREATE INDEX idx_members_phone ON members(phone);
CREATE INDEX idx_members_email ON members(email);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
CREATE INDEX idx_reminders_reminder_date ON reminders(reminder_date);

-- =====================================================================
-- VIEWS CHO DASHBOARD & BÁO CÁO
-- =====================================================================

-- dashboard_metrics: Metrics cho dashboard chính
CREATE VIEW dashboard_metrics AS
SELECT 
  DATE(p.paid_at) as metric_date,
  COUNT(DISTINCT p.invoice_id) as total_invoices,
  SUM(p.paid_amount) as total_revenue,
  COUNT(DISTINCT s.member_id) as active_members,
  AVG(p.paid_amount) as avg_invoice_amount
FROM payments p
JOIN invoices i ON p.invoice_id = i.id
LEFT JOIN subscriptions s ON i.subscription_id = s.id
WHERE p.is_refund = 0 AND p.paid_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY DATE(p.paid_at)
ORDER BY metric_date DESC;

-- expiring_members: Danh sách hội viên sắp hết hạn
CREATE VIEW expiring_members AS
SELECT 
  m.id, m.member_code, m.full_name, m.phone, m.email,
  p.name as plan_name, s.end_date,
  DATEDIFF(s.end_date, CURDATE()) as days_remaining,
  s.status as subscription_status
FROM members m
JOIN subscriptions s ON m.id = s.member_id
JOIN plans p ON s.plan_id = p.id
WHERE s.status = 'ACTIVE' 
  AND s.end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
  AND m.is_deleted = 0
ORDER BY s.end_date ASC;

-- top_selling_plans: Top gói bán chạy
CREATE VIEW top_selling_plans AS
SELECT 
  p.id, p.name as plan_name, p.price,
  COUNT(ii.id) as total_sales,
  SUM(ii.line_total) as total_revenue,
  AVG(ii.line_total) as avg_sale_price
FROM plans p
JOIN invoice_items ii ON p.id = ii.ref_id
JOIN invoices i ON ii.invoice_id = i.id
WHERE ii.item_type = 'PLAN' 
  AND i.status = 'ISSUED'
  AND i.issue_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY p.id, p.name, p.price
ORDER BY total_sales DESC, total_revenue DESC;

-- staff_performance: Hiệu suất nhân viên theo doanh thu
CREATE VIEW staff_performance AS
SELECT 
  u.id, u.full_name, u.username,
  COUNT(DISTINCT i.id) as total_invoices,
  SUM(p.paid_amount) as total_revenue,
  AVG(p.paid_amount) as avg_invoice_amount,
  COUNT(DISTINCT s.member_id) as members_served
FROM users u
JOIN invoices i ON u.id = i.created_by
JOIN payments p ON i.id = p.invoice_id
LEFT JOIN subscriptions s ON i.subscription_id = s.id
WHERE p.is_refund = 0 
  AND i.issue_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY u.id, u.full_name, u.username
ORDER BY total_revenue DESC;

-- daily_revenue_chart: Dữ liệu cho biểu đồ doanh thu theo ngày
CREATE VIEW daily_revenue_chart AS
SELECT 
  DATE(p.paid_at) as chart_date,
  SUM(p.paid_amount) as daily_revenue,
  COUNT(DISTINCT p.invoice_id) as daily_invoices,
  HOUR(p.paid_at) as peak_hour
FROM payments p
WHERE p.is_refund = 0 
  AND p.paid_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY DATE(p.paid_at), HOUR(p.paid_at)
ORDER BY chart_date DESC, peak_hour ASC;

-- =====================================================================
-- STORED PROCEDURES CHO BUSINESS LOGIC
-- =====================================================================

-- Procedure tính ngày gia hạn (R01/R02)
DELIMITER //
CREATE PROCEDURE CalculateRenewalDates(
  IN p_member_id BIGINT,
  IN p_plan_id BIGINT,
  OUT p_start_date DATE,
  OUT p_end_date DATE
)
BEGIN
  DECLARE v_current_end_date DATE DEFAULT NULL;
  DECLARE v_plan_duration INT DEFAULT 0;
  
  -- Lấy thời hạn gói
  SELECT duration_days INTO v_plan_duration 
  FROM plans WHERE id = p_plan_id;
  
  -- Lấy ngày kết thúc hiện tại của hội viên
  SELECT MAX(end_date) INTO v_current_end_date
  FROM subscriptions 
  WHERE member_id = p_member_id AND status = 'ACTIVE';
  
  -- Logic R01: Nếu còn hạn, gia hạn từ ngày kết thúc + 1
  -- Logic R02: Nếu hết hạn, gia hạn từ hôm nay
  IF v_current_end_date IS NOT NULL AND v_current_end_date >= CURDATE() THEN
    SET p_start_date = DATE_ADD(v_current_end_date, INTERVAL 1 DAY);
  ELSE
    SET p_start_date = CURDATE();
  END IF;
  
  SET p_end_date = DATE_ADD(p_start_date, INTERVAL v_plan_duration DAY);
END //
DELIMITER ;

-- Procedure sinh mã hội viên (R06)
DELIMITER //
CREATE PROCEDURE GenerateMemberCode(
  IN p_branch_id BIGINT,
  OUT p_member_code VARCHAR(32)
)
BEGIN
  DECLARE v_year VARCHAR(4);
  DECLARE v_counter_key VARCHAR(64);
  DECLARE v_next_number INT DEFAULT 1;
  
  SET v_year = YEAR(CURDATE());
  SET v_counter_key = CONCAT('MEMBER_CODE_', v_year);
  
  -- Lấy hoặc tạo counter
  INSERT INTO counters (counter_key, current_value) 
  VALUES (v_counter_key, 1)
  ON DUPLICATE KEY UPDATE current_value = current_value + 1;
  
  SELECT current_value INTO v_next_number 
  FROM counters WHERE counter_key = v_counter_key;
  
  SET p_member_code = CONCAT('GYM-', v_year, '-', LPAD(v_next_number, 4, '0'));
END //
DELIMITER ;

-- Procedure tạo đăng ký mới với validation
DELIMITER //
CREATE PROCEDURE CreateSubscription(
  IN p_member_id BIGINT,
  IN p_plan_id BIGINT,
  IN p_created_by BIGINT,
  IN p_note VARCHAR(500),
  OUT p_subscription_id BIGINT,
  OUT p_result_code INT,
  OUT p_result_message VARCHAR(255)
)
BEGIN
  DECLARE v_start_date DATE;
  DECLARE v_end_date DATE;
  DECLARE v_member_exists INT DEFAULT 0;
  DECLARE v_plan_exists INT DEFAULT 0;
  DECLARE v_plan_active INT DEFAULT 0;
  
  -- Validation
  SELECT COUNT(*) INTO v_member_exists FROM members WHERE id = p_member_id AND is_deleted = 0;
  SELECT COUNT(*) INTO v_plan_exists FROM plans WHERE id = p_plan_id;
  SELECT COUNT(*) INTO v_plan_active FROM plans WHERE id = p_plan_id AND is_active = 1;
  
  IF v_member_exists = 0 THEN
    SET p_result_code = 1;
    SET p_result_message = 'Hội viên không tồn tại hoặc đã bị xóa';
  ELSEIF v_plan_exists = 0 THEN
    SET p_result_code = 2;
    SET p_result_message = 'Gói tập không tồn tại';
  ELSEIF v_plan_active = 0 THEN
    SET p_result_code = 3;
    SET p_result_message = 'Gói tập đã bị vô hiệu hóa';
  ELSE
    -- Tính ngày bắt đầu và kết thúc
    CALL CalculateRenewalDates(p_member_id, p_plan_id, v_start_date, v_end_date);
    
    -- Tạo đăng ký
    INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, created_by, note, status)
    VALUES (p_member_id, p_plan_id, v_start_date, v_end_date, p_created_by, p_note, 'PENDING');
    
    SET p_subscription_id = LAST_INSERT_ID();
    SET p_result_code = 0;
    SET p_result_message = 'Tạo đăng ký thành công';
  END IF;
END //
DELIMITER ;

-- Procedure mở ca làm việc
DELIMITER //
CREATE PROCEDURE OpenShift(
  IN p_branch_id BIGINT,
  IN p_user_id BIGINT,
  OUT p_shift_id BIGINT,
  OUT p_result_code INT,
  OUT p_result_message VARCHAR(255)
)
BEGIN
  DECLARE v_existing_shift INT DEFAULT 0;
  
  -- Kiểm tra xem đã có ca nào đang mở chưa
  SELECT COUNT(*) INTO v_existing_shift 
  FROM current_shifts 
  WHERE branch_id = p_branch_id;
  
  IF v_existing_shift > 0 THEN
    SET p_result_code = 1;
    SET p_result_message = 'Đã có ca đang mở tại chi nhánh này';
  ELSE
    -- Tạo ca mới
    INSERT INTO shifts (branch_id, opened_by, opened_at)
    VALUES (p_branch_id, p_user_id, NOW());
    
    SET p_shift_id = LAST_INSERT_ID();
    
    -- Cập nhật ca hiện tại
    INSERT INTO current_shifts (branch_id, user_id, shift_id)
    VALUES (p_branch_id, p_user_id, p_shift_id);
    
    SET p_result_code = 0;
    SET p_result_message = 'Mở ca thành công';
  END IF;
END //
DELIMITER ;

-- =====================================================================
-- TRIGGERS CHO AUTO-AUDIT (R07)
-- =====================================================================

-- Trigger audit cho bảng members
DELIMITER //
CREATE TRIGGER tr_members_audit_insert
  AFTER INSERT ON members
  FOR EACH ROW
BEGIN
  INSERT INTO audit_logs (table_name, action_type, row_pk, after_data, actor_user_id, created_at)
  VALUES ('members', 'INSERT', NEW.id, JSON_OBJECT(
    'id', NEW.id,
    'member_code', NEW.member_code,
    'full_name', NEW.full_name,
    'phone', NEW.phone,
    'status', NEW.status
  ), @current_user_id, NOW());
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER tr_members_audit_update
  AFTER UPDATE ON members
  FOR EACH ROW
BEGIN
  INSERT INTO audit_logs (table_name, action_type, row_pk, before_data, after_data, actor_user_id, created_at)
  VALUES ('members', 'UPDATE', NEW.id, JSON_OBJECT(
    'id', OLD.id,
    'member_code', OLD.member_code,
    'full_name', OLD.full_name,
    'phone', OLD.phone,
    'status', OLD.status
  ), JSON_OBJECT(
    'id', NEW.id,
    'member_code', NEW.member_code,
    'full_name', NEW.full_name,
    'phone', NEW.phone,
    'status', NEW.status
  ), @current_user_id, NOW());
END //
DELIMITER ;

-- Trigger audit cho bảng subscriptions
DELIMITER //
CREATE TRIGGER tr_subscriptions_audit_insert
  AFTER INSERT ON subscriptions
  FOR EACH ROW
BEGIN
  INSERT INTO audit_logs (table_name, action_type, row_pk, after_data, actor_user_id, created_at)
  VALUES ('subscriptions', 'INSERT', NEW.id, JSON_OBJECT(
    'id', NEW.id,
    'member_id', NEW.member_id,
    'plan_id', NEW.plan_id,
    'start_date', NEW.start_date,
    'end_date', NEW.end_date,
    'status', NEW.status
  ), @current_user_id, NOW());
END //
DELIMITER ;

-- Trigger audit cho bảng invoices
DELIMITER //
CREATE TRIGGER tr_invoices_audit_insert
  AFTER INSERT ON invoices
  FOR EACH ROW
BEGIN
  INSERT INTO audit_logs (table_name, action_type, row_pk, after_data, actor_user_id, created_at)
  VALUES ('invoices', 'INSERT', NEW.id, JSON_OBJECT(
    'id', NEW.id,
    'invoice_no', NEW.invoice_no,
    'member_id', NEW.member_id,
    'total_amount', NEW.total_amount,
    'status', NEW.status
  ), @current_user_id, NOW());
END //
DELIMITER ;

-- Trigger audit cho bảng payments
DELIMITER //
CREATE TRIGGER tr_payments_audit_insert
  AFTER INSERT ON payments
  FOR EACH ROW
BEGIN
  INSERT INTO audit_logs (table_name, action_type, row_pk, after_data, actor_user_id, created_at)
  VALUES ('payments', 'INSERT', NEW.id, JSON_OBJECT(
    'id', NEW.id,
    'invoice_id', NEW.invoice_id,
    'paid_amount', NEW.paid_amount,
    'method_id', NEW.method_id
  ), @current_user_id, NOW());
END //
DELIMITER ;

-- =====================================================================
-- KẾT THÚC SCHEMA MIGRATION
-- Lưu ý: Seed data được đặt trong V2__Seed_Data.sql
-- =====================================================================
