# 🧪 Test Users - GymPro

File này chứa thông tin các user test để đăng nhập vào hệ thống.

## 📋 Danh sách User Test

| Username | Password | Role | Ghi chú |
|----------|----------|------|---------|
| `admin` | `admin123` | OWNER | Administrator - Full access |
| `staff` | `staff123` | STAFF | Nhân viên quầy - Limited access |

## 🔑 Cách tạo hash password mới

Nếu bạn muốn tạo user mới với password khác:

```bash
# Compile và chạy PasswordHasher utility
cd /Users/metacom/Documents/Project/gympro/GymPro
mvn compile
java -cp "target/classes:$(find ~/.m2/repository -name 'jbcrypt-*.jar' | head -1)" \
  com.example.gympro.utils.PasswordHasher <password>

# Ví dụ:
java -cp "target/classes:$(find ~/.m2/repository -name 'jbcrypt-*.jar' | head -1)" \
  com.example.gympro.utils.PasswordHasher mypassword123
```

Sau đó copy hash vào SQL INSERT:

```sql
INSERT INTO users (branch_id, username, full_name, email, phone, password_hash, is_active)
SELECT b.id, 'newuser', 'Full Name', 'email@example.com', '0123456789',
       '<hash_from_output>', 1
FROM branches b
WHERE b.code = 'BR-001'
  AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'newuser');
```

## ⚠️ Lưu ý

- Các password này chỉ dùng cho **môi trường DEV/TEST**
- **KHÔNG** sử dụng trong production
- Sau khi migrate (flyway migrate), các user này sẽ tự động được tạo

