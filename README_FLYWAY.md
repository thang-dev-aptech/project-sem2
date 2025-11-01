# 🚀 Flyway Quick Guide (GymPro)

Hướng dẫn chạy database migrations cho GymPro bằng **Flyway Maven Plugin**.

## ⚡ Quick Start

### Bước 1: Khởi động MySQL Database

```bash
docker compose up -d mysql
```

**Lưu ý:** MySQL config `log_bin_trust_function_creators` đã được tự động set trong `docker-compose.yml`, không cần chạy thủ công nữa!

### Bước 2: Chạy Migrations

```bash
# Apply tất cả migrations mới (giữ nguyên data hiện tại)
mvn flyway:migrate

# Hoặc reset database hoàn toàn (⚠️ CHỈ dùng DEV/TEST - xóa hết data)
mvn flyway:clean flyway:migrate
```

## 📋 Prerequisites

- **Docker** (cho MySQL container)
- **Maven 3.6+** (đã có sẵn trong project)

## 🔧 Tại sao cần setup MySQL config?

Schema của GymPro có **Stored Procedures** và **Triggers** (xem `V1__GymPro_Complete_Schema.sql`):
- Stored Procedures: `CalculateRenewalDates`, `GenerateMemberCode`, `CreateSubscription`, `OpenShift`
- Triggers: Auto-audit cho `members`, `subscriptions`, `invoices`, `payments`

Khi MySQL có **binary logging** enabled (mặc định trong MySQL 8.0), tạo stored procedures/triggers yêu cầu **SUPER privilege** - điều này không an toàn trong production.

**Giải pháp:** Set `log_bin_trust_function_creators = 1` để cho phép tạo stored procedures/triggers mà không cần SUPER privilege.

**✅ Đã tự động:** Config này đã được thêm vào `docker-compose.yml`, MySQL tự động set khi khởi động container.

## 🔧 Database Connection

- **Host:** localhost:3306
- **Database:** gympro
- **User:** gympro_user
- **Password:** gympro_password

## 📁 Migration Files

- `db/migration/V1__GymPro_Complete_Schema.sql` - Schema
- `db/migration/V2__Seed_Data.sql` - Seed data (admin, roles, branch)

## 🎯 Maven Commands

| Command | Mô tả |
|---------|-------|
| `mvn flyway:migrate` | Apply tất cả migrations mới (giữ nguyên data và bảng cũ) |
| `mvn flyway:clean` | ⚠️ **XÓA TOÀN BỘ SCHEMA**: Bảng + Dữ liệu + Stored Procedures + Triggers + Views (CHỈ DEV/TEST) |
| `mvn flyway:clean flyway:migrate` | Reset hoàn toàn: Xóa hết rồi tạo lại từ đầu |
| `mvn flyway:info` | Xem trạng thái migrations (đã chạy migration nào, chưa chạy migration nào) |
| `mvn flyway:validate` | Validate migrations (kiểm tra tính nhất quán) |
| `mvn flyway:baseline` | Baseline database đã có (nếu DB đã có schema nhưng chưa dùng Flyway) |
| `mvn flyway:repair` | Sửa Flyway metadata nếu bị lỗi |

## ✅ Verify Data After Migration

```bash
docker exec -i gympro_mysql mysql -ugympro_user -pgympro_password -e "
  USE gympro;
  SELECT id, username FROM users;
  SELECT u.username, r.name FROM user_roles ur
    JOIN users u ON ur.user_id=u.id
    JOIN roles r ON ur.role_id=r.id;
"
```


## 👤 Test Users (sau khi migrate)

Sau khi chạy `mvn flyway:clean flyway:migrate`, các user test sau sẽ được tạo tự động:

| Username | Password | Role | Mô tả |
|----------|----------|------|-------|
| `admin` | `admin123` | OWNER | Administrator |
| `staff` | `staff123` | STAFF | Nhân viên quầy |
| `manager` | `manager123` | OWNER | Quản lý |

**Xem chi tiết:** Xem file `TEST_USERS.md`

## ⚙️ Configuration

Flyway được cấu hình trong `pom.xml` (Maven plugin):

- **URL:** `jdbc:mysql://localhost:3306/gympro?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`
- **User:** `gympro_user`
- **Password:** `gympro_password`
- **Locations:** `filesystem:db/migration`
- **Baseline on Migrate:** `true` (tự động baseline nếu DB chưa có Flyway)
- **Validate on Migrate:** `true` (validate trước khi migrate)
- **Encoding:** `UTF-8`
- **SQL Migration Pattern:** `V{version}__{description}.sql`

## ⚠️ Notes

- **Application Flyway đã TẮT** (`flyway.enabled=false` trong `application.properties`) để tránh chạy 2 lần khi mở app. Chỉ dùng Maven để migrate!
- **`flyway:clean` XÓA TOÀN BỘ SCHEMA** (bảng, dữ liệu, stored procedures, triggers, views) - **CHỈ dùng DEV/TEST!**
- **`flyway:migrate`** chỉ thêm migrations mới, **KHÔNG xóa** data/bảng cũ
- **MySQL config tự động:** `log_bin_trust_function_creators=1` đã được set trong `docker-compose.yml` để cho phép tạo stored procedures/triggers
- Nếu gặp lỗi "SUPER privilege", đảm bảo MySQL container đang chạy với config đúng (restart: `docker compose restart mysql`)
- File `flyway.conf` chỉ để tham khảo, không cần thiết khi dùng Maven

## 🔍 So sánh các lệnh

| Lệnh | Xóa bảng? | Xóa dữ liệu? | Xóa SP/Triggers? | Khi nào dùng? |
|------|-----------|--------------|------------------|---------------|
| `migrate` | ❌ | ❌ | ❌ | Thêm migration mới, giữ nguyên mọi thứ |
| `clean` | ✅ | ✅ | ✅ | Reset hoàn toàn DB (chỉ DEV/TEST) |
| `clean + migrate` | ✅ → ✅ | ✅ → ✅ | ✅ → ✅ | Reset rồi tạo lại từ đầu (chỉ DEV/TEST) |
