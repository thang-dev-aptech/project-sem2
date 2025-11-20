-- =====================================================================
-- V5__Expiring_Members_Seed_Data.sql
-- Seed data cho 10 members sắp hết hạn để test Expiring Members module
-- Phân bố từ -30 ngày (đã hết hạn) đến +14 ngày (sắp hết hạn)
-- =====================================================================

-- =====================================================================
-- 1. TẠO 10 MEMBERS MỚI
-- =====================================================================

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note)
SELECT b.id, 'GYM-2024-101', 'Nguyễn Văn Hết Hạn 1', '0911111111', 'expired1@test.com', 'MALE', '1990-01-01', '123 Đường Test 1', 'ACTIVE', 'Test: Đã hết hạn 25 ngày'
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-101');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note)
SELECT b.id, 'GYM-2024-102', 'Trần Thị Hết Hạn 2', '0922222222', 'expired2@test.com', 'FEMALE', '1992-02-02', '456 Đường Test 2', 'ACTIVE', 'Test: Đã hết hạn 20 ngày'
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-102');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note)
SELECT b.id, 'GYM-2024-103', 'Lê Văn Hết Hạn 3', '0933333333', 'expired3@test.com', 'MALE', '1988-03-03', '789 Đường Test 3', 'ACTIVE', 'Test: Đã hết hạn 15 ngày'
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-103');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note)
SELECT b.id, 'GYM-2024-104', 'Phạm Thị Hết Hạn 4', '0944444444', 'expired4@test.com', 'FEMALE', '1995-04-04', '321 Đường Test 4', 'ACTIVE', 'Test: Đã hết hạn 10 ngày'
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-104');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note)
SELECT b.id, 'GYM-2024-105', 'Hoàng Văn Hết Hạn 5', '0955555555', 'expired5@test.com', 'MALE', '1993-05-05', '654 Đường Test 5', 'ACTIVE', 'Test: Đã hết hạn 5 ngày'
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-105');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note)
SELECT b.id, 'GYM-2024-106', 'Vũ Thị Sắp Hết 1', '0966666666', 'expiring1@test.com', 'FEMALE', '1991-06-06', '987 Đường Test 6', 'ACTIVE', 'Test: Hết hạn hôm nay (0 ngày)'
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-106');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note)
SELECT b.id, 'GYM-2024-107', 'Đỗ Văn Sắp Hết 2', '0977777777', 'expiring2@test.com', 'MALE', '1989-07-07', '147 Đường Test 7', 'ACTIVE', 'Test: Còn 1 ngày'
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-107');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note)
SELECT b.id, 'GYM-2024-108', 'Bùi Thị Sắp Hết 3', '0988888888', 'expiring3@test.com', 'FEMALE', '1994-08-08', '258 Đường Test 8', 'ACTIVE', 'Test: Còn 3 ngày'
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-108');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note)
SELECT b.id, 'GYM-2024-109', 'Ngô Văn Sắp Hết 4', '0999999999', 'expiring4@test.com', 'MALE', '1992-09-09', '369 Đường Test 9', 'ACTIVE', 'Test: Còn 7 ngày'
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-109');

INSERT INTO members (branch_id, member_code, full_name, phone, email, gender, dob, address, status, note)
SELECT b.id, 'GYM-2024-110', 'Lý Thị Sắp Hết 5', '0900000000', 'expiring5@test.com', 'FEMALE', '1996-10-10', '741 Đường Test 10', 'ACTIVE', 'Test: Còn 14 ngày'
FROM branches b
WHERE b.code = 'BR-001' AND NOT EXISTS (SELECT 1 FROM members WHERE member_code = 'GYM-2024-110');

-- =====================================================================
-- 2. TẠO SUBSCRIPTIONS CHO 10 MEMBERS (với các ngày hết hạn khác nhau)
-- =====================================================================

-- Member 101: Đã hết hạn 25 ngày
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 55 DAY), DATE_SUB(CURDATE(), INTERVAL 25 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-101' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_SUB(CURDATE(), INTERVAL 25 DAY));

-- Member 102: Đã hết hạn 20 ngày
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 50 DAY), DATE_SUB(CURDATE(), INTERVAL 20 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-102' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_SUB(CURDATE(), INTERVAL 20 DAY));

-- Member 103: Đã hết hạn 15 ngày
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 45 DAY), DATE_SUB(CURDATE(), INTERVAL 15 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-103' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_SUB(CURDATE(), INTERVAL 15 DAY));

-- Member 104: Đã hết hạn 10 ngày
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 40 DAY), DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-104' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_SUB(CURDATE(), INTERVAL 10 DAY));

-- Member 105: Đã hết hạn 5 ngày
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 35 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'EXPIRED'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-105' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_SUB(CURDATE(), INTERVAL 5 DAY));

-- Member 106: Hết hạn hôm nay (0 ngày)
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 30 DAY), CURDATE(), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-106' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = CURDATE());

-- Member 107: Còn 1 ngày
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 29 DAY), DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-107' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY));

-- Member 108: Còn 3 ngày
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 27 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-108' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_ADD(CURDATE(), INTERVAL 3 DAY));

-- Member 109: Còn 7 ngày
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 23 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-109' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_ADD(CURDATE(), INTERVAL 7 DAY));

-- Member 110: Còn 14 ngày
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status)
SELECT m.id, p.id, DATE_SUB(CURDATE(), INTERVAL 16 DAY), DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'ACTIVE'
FROM members m, plans p
WHERE m.member_code = 'GYM-2024-110' AND p.code = 'PKG-001'
  AND NOT EXISTS (SELECT 1 FROM subscriptions s WHERE s.member_id = m.id AND s.end_date = DATE_ADD(CURDATE(), INTERVAL 14 DAY));

-- =====================================================================
-- GHI CHÚ TEST:
-- =====================================================================
-- 10 Members sắp hết hạn/đã hết hạn:
--   1. GYM-2024-101: Đã hết hạn 25 ngày (days_left = -25)
--   2. GYM-2024-102: Đã hết hạn 20 ngày (days_left = -20)
--   3. GYM-2024-103: Đã hết hạn 15 ngày (days_left = -15)
--   4. GYM-2024-104: Đã hết hạn 10 ngày (days_left = -10)
--   5. GYM-2024-105: Đã hết hạn 5 ngày (days_left = -5)
--   6. GYM-2024-106: Hết hạn hôm nay (days_left = 0)
--   7. GYM-2024-107: Còn 1 ngày (days_left = 1)
--   8. GYM-2024-108: Còn 3 ngày (days_left = 3)
--   9. GYM-2024-109: Còn 7 ngày (days_left = 7)
--  10. GYM-2024-110: Còn 14 ngày (days_left = 14)
--
-- Test với filter "Tất cả" (maxDayLeft = 14):
--   - Sẽ hiển thị: Tất cả 10 members (vì days_left từ -25 đến 14 nằm trong khoảng -30 đến 14)
--
-- Test với filter "≤ 3 ngày" (maxDayLeft = 3):
--   - Sẽ hiển thị: Members 105, 106, 107, 108 (days_left từ -5 đến 3)
--
-- Test với filter "≤ 7 ngày" (maxDayLeft = 7):
--   - Sẽ hiển thị: Members 104, 105, 106, 107, 108, 109 (days_left từ -10 đến 7)
-- =====================================================================

