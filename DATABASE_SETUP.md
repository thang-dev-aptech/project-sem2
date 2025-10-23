# 🗄️ GymPro Database Setup Guide

## 📋 Tổng quan
Hướng dẫn setup database MySQL cho ứng dụng GymPro với Flyway migration.

## 🚀 Quick Start

### 1. Cài đặt MySQL 8.x
```bash
# macOS với Homebrew
brew install mysql
brew services start mysql

# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server-8.0

# Windows
# Tải MySQL Installer từ https://dev.mysql.com/downloads/installer/
```

### 2. Tạo Database và User
```sql
-- Đăng nhập MySQL với quyền root
mysql -u root -p

-- Tạo database
CREATE DATABASE gympro CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- Tạo user cho ứng dụng
CREATE USER 'gympro_user'@'localhost' IDENTIFIED BY 'gympro_password';
GRANT ALL PRIVILEGES ON gympro.* TO 'gympro_user'@'localhost';
FLUSH PRIVILEGES;

-- Kiểm tra
SHOW DATABASES;
SELECT User, Host FROM mysql.user WHERE User = 'gympro_user';
```

### 3. Chạy Migration với Flyway
```bash
# Từ thư mục project
mvn flyway:migrate

# Hoặc chạy trực tiếp file SQL
mysql -u gympro_user -p gympro < db/migration/V1__GymPro_Complete_Schema.sql
```

### 4. Kiểm tra Database
```sql
-- Kết nối với database
mysql -u gympro_user -p gympro

-- Kiểm tra các bảng đã tạo
SHOW TABLES;

-- Kiểm tra dữ liệu seed
SELECT * FROM branches;
SELECT * FROM roles;
SELECT * FROM payment_methods;
SELECT * FROM system_configs;

-- Kiểm tra views
SELECT * FROM dashboard_metrics LIMIT 5;
SELECT * FROM expiring_members LIMIT 5;
```

## 🔧 Cấu hình

### Database Properties
File: `src/main/resources/database.properties`
```properties
db.url=jdbc:mysql://localhost:3306/gympro?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=gympro_user
db.password=gympro_password
```

### Flyway Configuration
File: `src/main/resources/flyway.conf`
```properties
flyway.url=jdbc:mysql://localhost:3306/gympro?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
flyway.user=gympro_user
flyway.password=gympro_password
flyway.locations=filesystem:db/migration
```

## 📊 Database Schema Overview

### Core Tables
- **branches**: Chi nhánh phòng gym
- **users**: Người dùng hệ thống
- **roles**: Vai trò (OWNER/STAFF)
- **members**: Hội viên
- **plans**: Gói tập
- **subscriptions**: Đăng ký/gia hạn
- **invoices**: Hóa đơn
- **payments**: Thanh toán
- **shifts**: Ca làm việc

### Support Tables
- **system_configs**: Cấu hình hệ thống
- **current_shifts**: Ca hiện tại
- **reminders**: Nhắc hạn
- **audit_logs**: Audit trail
- **backup_logs**: Log sao lưu

### Views for Dashboard
- **dashboard_metrics**: Metrics chính
- **expiring_members**: Hội viên sắp hết hạn
- **top_selling_plans**: Top gói bán chạy
- **staff_performance**: Hiệu suất nhân viên
- **daily_revenue_chart**: Biểu đồ doanh thu

## 🛠️ Stored Procedures

### Business Logic
- **CalculateRenewalDates**: Tính ngày gia hạn (R01/R02)
- **GenerateMemberCode**: Sinh mã hội viên (R06)
- **CreateSubscription**: Tạo đăng ký với validation
- **OpenShift**: Mở ca làm việc

### Usage Examples
```sql
-- Tính ngày gia hạn
CALL CalculateRenewalDates(1, 1, @start_date, @end_date);
SELECT @start_date, @end_date;

-- Sinh mã hội viên
CALL GenerateMemberCode(1, @member_code);
SELECT @member_code;

-- Tạo đăng ký
CALL CreateSubscription(1, 1, 1, 'Gia hạn 3 tháng', @sub_id, @result_code, @result_msg);
SELECT @sub_id, @result_code, @result_msg;
```

## 🔍 Monitoring & Maintenance

### Performance Monitoring
```sql
-- Kiểm tra performance của queries
EXPLAIN SELECT * FROM dashboard_metrics WHERE metric_date >= CURDATE() - INTERVAL 7 DAY;

-- Kiểm tra indexes
SHOW INDEX FROM members;
SHOW INDEX FROM payments;
```

### Backup & Restore
```bash
# Backup
mysqldump -u gympro_user -p gympro > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore
mysql -u gympro_user -p gympro < backup_20241201_120000.sql
```

### Cleanup
```sql
-- Xóa dữ liệu test (cẩn thận!)
DELETE FROM payments WHERE created_at < DATE_SUB(NOW(), INTERVAL 30 DAY);
DELETE FROM audit_logs WHERE created_at < DATE_SUB(NOW(), INTERVAL 90 DAY);
```

## 🚨 Troubleshooting

### Common Issues

1. **Connection Refused**
   ```bash
   # Kiểm tra MySQL service
   brew services list | grep mysql
   sudo systemctl status mysql
   ```

2. **Access Denied**
   ```sql
   -- Kiểm tra user permissions
   SHOW GRANTS FOR 'gympro_user'@'localhost';
   ```

3. **Migration Failed**
   ```bash
   # Kiểm tra Flyway status
   mvn flyway:info
   
   # Repair nếu cần
   mvn flyway:repair
   ```

4. **Performance Issues**
   ```sql
   -- Kiểm tra slow queries
   SHOW VARIABLES LIKE 'slow_query_log';
   SHOW VARIABLES LIKE 'long_query_time';
   ```

## 📈 Next Steps

1. **Setup Application**: Cấu hình JavaFX app kết nối database
2. **Create Test Data**: Tạo dữ liệu test cho development
3. **Performance Tuning**: Tối ưu queries và indexes
4. **Monitoring Setup**: Cài đặt monitoring tools
5. **Backup Strategy**: Thiết lập backup tự động

## 📞 Support

Nếu gặp vấn đề, hãy kiểm tra:
1. MySQL service đang chạy
2. User permissions đúng
3. Database connection string
4. Flyway configuration
5. Log files trong `target/logs/`
