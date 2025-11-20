-- =====================================================================
-- V4__Payment_Seed_Data.sql
-- Seed data cho phần thanh toán để test Reports module
-- Tạo nhiều payments với các phương thức khác nhau và khoảng thời gian khác nhau
-- =====================================================================

-- =====================================================================
-- 1. TẠO THÊM SUBSCRIPTIONS nếu cần (cho các invoices mới)
-- =====================================================================
-- Tạo subscription mới cho member 1 (nếu chưa có subscription cho gói 1 tháng)
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 0 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-001' AND p.code = 'PKG-001'
  AND NOT EXISTS (
    SELECT 1 FROM subscriptions s 
    WHERE s.member_id = m.id 
      AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 30 DAY)
  );

-- Tạo subscription mới cho member 2 (nếu chưa có subscription cho gói 3 tháng)
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_ADD(CURDATE(), INTERVAL 75 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-002' AND p.code = 'PKG-002'
  AND NOT EXISTS (
    SELECT 1 FROM subscriptions s 
    WHERE s.member_id = m.id 
      AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 15 DAY)
  );

-- Tạo subscription mới cho member 3 (nếu chưa có subscription cho gói 6 tháng)
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 170 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-003' AND p.code = 'PKG-003'
  AND NOT EXISTS (
    SELECT 1 FROM subscriptions s 
    WHERE s.member_id = m.id 
      AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 10 DAY)
  );

-- Tạo subscription mới cho member 5 (nếu chưa có subscription cho gói 1 tháng)
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 25 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-005' AND p.code = 'PKG-001'
  AND NOT EXISTS (
    SELECT 1 FROM subscriptions s 
    WHERE s.member_id = m.id 
      AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 5 DAY)
  );

-- =====================================================================
-- 2. TẠO THÊM INVOICES (Hóa đơn) - 10 invoices trong các tháng khác nhau
-- =====================================================================

-- Invoice 5: Tháng trước (30 ngày trước) - Đã thanh toán bằng CASH
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-005', DATE_SUB(CURDATE(), INTERVAL 30 DAY), 500000, 'NONE', 0, 500000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 30 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-001'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-005')
LIMIT 1;

-- Invoice 6: 15 ngày trước - Đã thanh toán bằng BANK
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-006', DATE_SUB(CURDATE(), INTERVAL 15 DAY), 1300000, 'PERCENT', 5, 1235000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 15 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-002'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-006')
LIMIT 1;

-- Invoice 7: 10 ngày trước - Đã thanh toán bằng QR
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-007', DATE_SUB(CURDATE(), INTERVAL 10 DAY), 2400000, 'AMOUNT', 200000, 2200000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 10 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-003'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-007')
LIMIT 1;

-- Invoice 8: 5 ngày trước - Đã thanh toán bằng CASH
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-008', DATE_SUB(CURDATE(), INTERVAL 5 DAY), 500000, 'NONE', 0, 500000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 5 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-005'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-008')
LIMIT 1;

-- Tạo subscription mới cho member 1 (gói 3 tháng, 3 ngày trước)
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 87 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-001' AND p.code = 'PKG-002'
  AND NOT EXISTS (
    SELECT 1 FROM subscriptions s 
    WHERE s.member_id = m.id 
      AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 3 DAY)
  );

-- Invoice 9: 3 ngày trước - Đã thanh toán bằng BANK
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-009', DATE_SUB(CURDATE(), INTERVAL 3 DAY), 1300000, 'NONE', 0, 1300000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 3 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-001'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-009')
LIMIT 1;

-- Tạo subscription mới cho member 3 (gói 12 tháng, hôm qua)
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 364 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-003' AND p.code = 'PKG-004'
  AND NOT EXISTS (
    SELECT 1 FROM subscriptions s 
    WHERE s.member_id = m.id 
      AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
  );

-- Invoice 10: Hôm qua - Đã thanh toán bằng QR
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-010', DATE_SUB(CURDATE(), INTERVAL 1 DAY), 4500000, 'PERCENT', 10, 4050000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-003'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-010')
LIMIT 1;

-- Tạo subscription mới cho member 2 (gói 1 tháng, hôm nay)
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-002' AND p.code = 'PKG-001'
  AND NOT EXISTS (
    SELECT 1 FROM subscriptions s 
    WHERE s.member_id = m.id 
      AND s.start_date = CURDATE()
  );

-- Invoice 11: Hôm nay - Đã thanh toán bằng CASH
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-011', CURDATE(), 500000, 'NONE', 0, 500000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = CURDATE()
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-002'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-011')
LIMIT 1;

-- Tạo subscription mới cho member 1 (gói 3 tháng, 60 ngày trước)
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-001' AND p.code = 'PKG-002'
  AND NOT EXISTS (
    SELECT 1 FROM subscriptions s 
    WHERE s.member_id = m.id 
      AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 60 DAY)
  );

-- Invoice 12: 60 ngày trước (2 tháng trước) - Đã thanh toán bằng BANK
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-012', DATE_SUB(CURDATE(), INTERVAL 60 DAY), 1300000, 'NONE', 0, 1300000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 60 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-001'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-012')
LIMIT 1;

-- Tạo subscription mới cho member 3 (gói 6 tháng, 45 ngày trước)
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 45 DAY), DATE_ADD(CURDATE(), INTERVAL 135 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-003' AND p.code = 'PKG-003'
  AND NOT EXISTS (
    SELECT 1 FROM subscriptions s 
    WHERE s.member_id = m.id 
      AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 45 DAY)
  );

-- Invoice 13: 45 ngày trước - Đã thanh toán bằng CASH
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-013', DATE_SUB(CURDATE(), INTERVAL 45 DAY), 2400000, 'AMOUNT', 100000, 2300000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 45 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-003'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-013')
LIMIT 1;

-- Tạo subscription mới cho member 5 (gói 1 tháng, 20 ngày trước)
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-005' AND p.code = 'PKG-001'
  AND NOT EXISTS (
    SELECT 1 FROM subscriptions s 
    WHERE s.member_id = m.id 
      AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 20 DAY)
  );

-- Invoice 14: 20 ngày trước - Đã thanh toán bằng QR
INSERT INTO invoices (member_id, subscription_id, invoice_no, issue_date, subtotal_amount, discount_type, discount_value, total_amount, status, created_by)
SELECT m.id, s.id, 'INV-2024-014', DATE_SUB(CURDATE(), INTERVAL 20 DAY), 500000, 'NONE', 0, 500000, 'ISSUED', u.id
FROM members m
JOIN subscriptions s ON s.member_id = m.id AND s.start_date = DATE_SUB(CURDATE(), INTERVAL 20 DAY)
JOIN users u ON u.username = 'admin'
WHERE m.member_code = 'GYM-2024-005'
  AND NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_no = 'INV-2024-014')
LIMIT 1;

-- =====================================================================
-- 3. TẠO INVOICE ITEMS cho các invoices mới
-- =====================================================================

-- Items cho Invoice 5-14
INSERT INTO invoice_items (invoice_id, item_type, ref_id, description, qty, unit_price, line_total)
SELECT i.id, 'PLAN', p.id, p.name, 1, i.subtotal_amount, i.subtotal_amount
FROM invoices i
JOIN subscriptions s ON i.subscription_id = s.id
JOIN plans p ON s.plan_id = p.id
WHERE i.invoice_no IN ('INV-2024-005', 'INV-2024-006', 'INV-2024-007', 'INV-2024-008', 'INV-2024-009', 
                       'INV-2024-010', 'INV-2024-011', 'INV-2024-012', 'INV-2024-013', 'INV-2024-014')
  AND NOT EXISTS (SELECT 1 FROM invoice_items WHERE invoice_id = i.id);

-- =====================================================================
-- 4. TẠO PAYMENTS (Thanh toán) - 10 payments với các phương thức khác nhau
-- =====================================================================

-- Payment 3: Invoice 5 - CASH, 30 ngày trước
INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 500000, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'CASH-001', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'CASH'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-005'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

-- Payment 4: Invoice 6 - BANK, 15 ngày trước
INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 1235000, DATE_SUB(CURDATE(), INTERVAL 15 DAY), 'BANK-TRANSFER-001', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'BANK'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-006'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

-- Payment 5: Invoice 7 - QR, 10 ngày trước
INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 2200000, DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'QR-001', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'QR'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-007'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

-- Payment 6: Invoice 8 - CASH, 5 ngày trước
INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 500000, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'CASH-002', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'CASH'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-008'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

-- Payment 7: Invoice 9 - BANK, 3 ngày trước
INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 1300000, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'BANK-TRANSFER-002', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'BANK'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-009'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

-- Payment 8: Invoice 10 - QR, hôm qua
INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 4050000, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'QR-002', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'QR'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-010'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

-- Payment 9: Invoice 11 - CASH, hôm nay
INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 500000, CURDATE(), 'CASH-003', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'CASH'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-011'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

-- Payment 10: Invoice 12 - BANK, 60 ngày trước
INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 1300000, DATE_SUB(CURDATE(), INTERVAL 60 DAY), 'BANK-TRANSFER-003', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'BANK'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-012'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

-- Payment 11: Invoice 13 - CASH, 45 ngày trước
INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 2300000, DATE_SUB(CURDATE(), INTERVAL 45 DAY), 'CASH-004', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'CASH'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-013'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

-- Payment 12: Invoice 14 - QR, 20 ngày trước
INSERT INTO payments (invoice_id, method_id, paid_amount, paid_at, reference_code, created_by, is_refund)
SELECT i.id, pm.id, 500000, DATE_SUB(CURDATE(), INTERVAL 20 DAY), 'QR-003', u.id, 0
FROM invoices i
JOIN payment_methods pm ON pm.code = 'QR'
JOIN users u ON u.username = 'admin'
WHERE i.invoice_no = 'INV-2024-014'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id AND is_refund = 0);

-- =====================================================================
-- GHI CHÚ TEST PAYMENTS:
-- =====================================================================
-- Tổng cộng: 12 payments (2 từ V3 + 10 từ V4)
--
-- Phân bố theo phương thức:
--   - CASH: 5 payments (INV-001, INV-005, INV-008, INV-011, INV-013)
--   - BANK: 4 payments (INV-003, INV-006, INV-009, INV-012)
--   - QR: 3 payments (INV-007, INV-010, INV-014)
--
-- Phân bố theo thời gian:
--   - 60 ngày trước: 1 payment (INV-012 - BANK)
--   - 45 ngày trước: 1 payment (INV-013 - CASH)
--   - 30 ngày trước: 1 payment (INV-001 - CASH, INV-005 - CASH)
--   - 20 ngày trước: 1 payment (INV-014 - QR)
--   - 15 ngày trước: 1 payment (INV-006 - BANK)
--   - 10 ngày trước: 1 payment (INV-007 - QR)
--   - 5 ngày trước: 1 payment (INV-008 - CASH)
--   - 3 ngày trước: 1 payment (INV-009 - BANK)
--   - Hôm qua: 1 payment (INV-010 - QR)
--   - Hôm nay: 1 payment (INV-011 - CASH)
--
-- Test Reports Module:
--   1. Filter theo tháng hiện tại (từ đầu tháng đến hôm nay):
--      - Sẽ thấy: INV-005, INV-006, INV-007, INV-008, INV-009, INV-010, INV-011
--   2. Filter theo 30 ngày gần nhất:
--      - Sẽ thấy: Tất cả payments từ 30 ngày trước đến hôm nay
--   3. Filter theo 7 ngày gần nhất:
--      - Sẽ thấy: INV-008, INV-009, INV-010, INV-011
--   4. Tab Thanh Toán:
--      - Tổng CASH: 5 payments
--      - Tổng BANK: 4 payments
--      - Tổng QR: 3 payments
-- =====================================================================

