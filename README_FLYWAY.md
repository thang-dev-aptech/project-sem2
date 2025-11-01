# 🚀 Flyway Quick Guide (GymPro)

Hướng dẫn chạy database migrations cho GymPro bằng Flyway.

## ⚡ Quick Start (Khuyến nghị)

Đã có sẵn 3 scripts để chạy nhanh:

```bash
# 1. Khởi động MySQL
docker compose up -d mysql

# 2. Reset database (clean + migrate)
./flyway-reset.sh

# Hoặc chạy riêng:
./flyway-clean.sh    # Xóa schema (chỉ DEV)
./flyway-migrate.sh  # Apply migrations
```

## 📋 Prerequisites

- Docker (cho MySQL container)
- Không cần cài Flyway CLI, scripts tự dùng Docker

## 🔧 Database Connection

- **Host:** localhost:3306
- **Database:** gympro
- **User:** gympro_user
- **Password:** gympro_password

## 📁 Migration Files

- `db/migration/V1__GymPro_Complete_Schema.sql` - Schema
- `db/migration/V2__Seed_Data.sql` - Seed data (admin, roles, branch)

## 🎯 Scripts Available

| Script | Mô tả |
|--------|-------|
| `./flyway-clean.sh` | Xóa toàn bộ schema (⚠️ CHỈ dùng DEV/TEST) |
| `./flyway-migrate.sh` | Apply tất cả migrations |
| `./flyway-reset.sh` | Clean + Migrate (quick reset) |

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

## 🔄 Alternative: Flyway CLI (nếu đã cài)

Nếu bạn đã cài Flyway CLI, có thể dùng file `flyway.conf`:

```bash
flyway -configFiles=flyway.conf clean
flyway -configFiles=flyway.conf migrate
```

## ⚠️ Notes

- Application Flyway đã **TẮT** (`flyway.enabled=false`) để tránh chạy 2 lần khi mở app
- Chỉ chạy `clean` ở môi trường DEV/TEST (sẽ xóa toàn bộ DB!)
- Login mặc định sau khi seed:
  - Username: `admin`
  - Password: `admin123`
  - Role: `OWNER`
