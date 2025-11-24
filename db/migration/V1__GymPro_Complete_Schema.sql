-- =====================================================================
-- GymPro Complete Database Schema Migration
-- Version: V1
-- Description: Complete database schema for GymPro application (Tinh gọn)
-- =====================================================================

-- Charset & collation
SET NAMES utf8mb4;
SET collation_connection = 'utf8mb4_0900_ai_ci';
SET sql_mode = 'STRICT_ALL_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

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

-- invoices: Hóa đơn bán gói/dịch vụ
CREATE TABLE invoices (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  member_id        BIGINT NOT NULL,
  subscription_id  BIGINT NULL,
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
  CONSTRAINT fk_pay_created_by FOREIGN KEY (created_by) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT fk_pay_refund_of FOREIGN KEY (refund_of_payment_id) REFERENCES payments(id)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB COLLATE=utf8mb4_0900_ai_ci;


-- =====================================================================
-- INDEXES CHO PERFORMANCE (Tinh gọn - chỉ giữ indexes cần thiết)
-- =====================================================================

CREATE INDEX idx_members_branch_phone ON members(branch_id, phone);
CREATE INDEX idx_subscriptions_member ON subscriptions(member_id);
CREATE INDEX idx_subscriptions_status ON subscriptions(status);
CREATE INDEX idx_subscriptions_end_date ON subscriptions(end_date);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_created_at ON invoices(created_at);
CREATE INDEX idx_payments_created_at ON payments(paid_at);
CREATE INDEX idx_members_phone ON members(phone);
CREATE INDEX idx_members_email ON members(email);
