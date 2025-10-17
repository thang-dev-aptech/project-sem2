# 🏋️‍♂️ GymPro – JavaFX + MySQL Desktop Application

## 📌 1. Tổng quan
**GymPro** là ứng dụng quản lý phòng gym nội bộ, phát triển bằng **JavaFX (Desktop)** và **MySQL**.  
Ứng dụng giúp số hoá quy trình vận hành tại phòng gym nhỏ/lẻ hoặc chi nhánh đơn, bao gồm:
- Quản lý hội viên, gói tập, đăng ký/gia hạn.
- Quản lý huấn luyện viên (PT) và nhân viên.
- Thanh toán, xuất hoá đơn, báo cáo doanh thu.
- Nhắc hạn và cảnh báo hội viên sắp hết hạn.

> 💬 Mục tiêu của nhóm: Hoàn thiện phần mềm có thể **chạy cục bộ hoặc LAN**, giao diện thân thiện, đầy đủ quy trình nghiệp vụ cơ bản.

---

## 🧩 2. Phạm vi & mục tiêu

### 🎯 Mục tiêu
- Quản lý **hội viên – gói tập – đăng ký/gia hạn – thanh toán – báo cáo**.
- Tự động tính ngày bắt đầu/kết thúc khi gia hạn.
- Hỗ trợ chiết khấu linh hoạt (% hoặc số tiền).
- Xuất **Excel/PDF** hóa đơn, báo cáo.
- Giao diện trực quan bằng **JavaFX + SceneBuilder**.
- CSDL đồng bộ qua **MySQL + Flyway** (DDL versioning).

### 🔒 Phạm vi kỳ 2
| Hạng mục | Trạng thái |
|-----------|------------|
| 1 chi nhánh | ✅ Có |
| Giao diện desktop (JavaFX) | ✅ Có |
| CSDL MySQL (local/LAN) | ✅ Có |
| Check-in bằng QR | ❌ Kỳ sau |
| Gửi Zalo/Email tự động | ❌ Kỳ sau |
| Quản lý đa chi nhánh | ❌ Kỳ sau |

---

## 🧱 3. Kiến trúc ứng dụng

### ⚙️ Mô hình MVVM + DAO
```
View (FXML) ↔ ViewModel (Controller)
      ↕
   Service Layer
      ↕
 Repository (DAO)
      ↕
     MySQL
```

### 📂 Cấu trúc thư mục
```
app/
 ├── src/main/java/com/gympro/
 │    ├── domain/        # Entity classes (Member, Plan, Payment, ...)
 │    ├── repository/    # DAO layer
 │    ├── service/       # Business logic
 │    ├── viewmodel/     # Controller/ViewModel
 │
 ├── src/main/resources/
 │    ├── fxml/          # SceneBuilder layouts
 │    └── application.css
 │
 ├── db/migration/       # Flyway SQL files (V1__init.sql)
 ├── backups/            # Database backups
 └── README_PROJECT_OVERVIEW.md
```

---

## 🧾 4. Mô tả module nghiệp vụ

| Mã module | Tên module | Chức năng chính | Ghi chú |
|------------|-------------|------------------|---------|
| M01 | Quản lý Hội viên | CRUD hội viên, kiểm tra SĐT trùng, sinh mã GYM-YYYY-NNNN | Core |
| M02 | Quản lý Gói tập | Tạo/sửa/xóa gói; xác định thời hạn (tháng, tuần) | Core |
| M03 | Đăng ký & Gia hạn | Tính ngày start/end theo logic còn hạn – hết hạn | Core |
| M04 | Thanh toán | Ghi nhận hóa đơn, in PDF, tính chiết khấu | Core |
| M05 | Nhắc hạn | Gửi cảnh báo khi còn ≤5 ngày hết hạn | Cấu hình được |
| M06 | Báo cáo | Doanh thu tháng, số hội viên, xuất Excel | Core |
| M07 | Cấu hình & Backup | GRACE_DAYS, RENEW_RULE, sao lưu DB | Optional |
| M08 | Người dùng & Vai trò | Phân quyền: Owner, Manager, Staff, PT | Bảo mật |

---

## 📋 5. Quy tắc nghiệp vụ tiêu biểu

| Rule ID | Mô tả | Logic | Ghi chú |
|----------|--------|--------|---------|
| R01 | Gia hạn khi còn hạn | start = end_current + 1 | end = start + plan.duration_months |
| R02 | Gia hạn khi đã hết hạn | start = today | end = start + plan.duration_months |
| R03 | Trạng thái hội viên | PENDING → ACTIVE → EXPIRED | Có thể RENEWED hoặc PAUSED |
| R04 | Chiết khấu | Áp dụng theo % hoặc số tiền | Kiểm tra quyền user |
| R05 | Gần hết hạn | end_date - today ≤ GRACE_DAYS | GRACE_DAYS mặc định = 5 |
| R06 | Mã hội viên | Format “GYM-YYYY-NNNN” | Tự sinh tự động |
| R07 | Audit log | Ghi lại before/after mỗi thay đổi | Lưu vào bảng `audit_logs` |

---

## 🧠 6. User Stories (tóm tắt)

| ID | Mô tả | Acceptance Criteria |
|----|--------|----------------------|
| US-M-01 | Thêm hội viên mới | Cảnh báo khi SĐT trùng, sinh mã tự động |
| US-P-01 | Tạo gói tập mới | Validate số tháng > 0 |
| US-S-02 | Gia hạn gói tập | Tính ngày bắt đầu/kết thúc đúng logic |
| US-Pay-01 | Thanh toán | Sau khi thanh toán → ACTIVE + sinh hóa đơn PDF |
| US-Rep-01 | Báo cáo doanh thu | Lọc theo tháng/năm, export Excel |
| US-Back-01 | Sao lưu DB | Tạo file backup timestamped |
| US-Auth-01 | Đăng nhập | Xác thực bcrypt, phân quyền giao diện |

---

## 🧪 7. Kiểm thử & xác nhận
- **Unit Test:** tính toán ngày, chiết khấu, quyền hạn.
- **Integration Test:** quy trình hội viên → đăng ký → thanh toán → in hóa đơn.
- **UI Test:** validate form nhập liệu.
- **Ảnh chụp màn hình:** 8–10 form chính từ SceneBuilder.
- **Hiệu năng:** tìm kiếm 10k hội viên ≤ 200ms.

---

## 🔐 8. Bảo mật
- Đăng nhập người dùng (Owner, Manager, Staff).
- Mã hoá mật khẩu bằng **bcrypt**.
- Ghi lại mọi thao tác nhạy cảm vào **audit_logs**.
- Khóa màn hình khi idle X phút.
- Cấp quyền tối thiểu cho user `gympro_app` trong MySQL.

---

## 🚀 9. Kế hoạch triển khai (PM Plan)

| Sprint | Thời gian | Module chính | Ghi chú |
|---------|------------|---------------|---------|
| Sprint 0 | 1 tuần | Setup Maven, Flyway, Auth cơ bản | |
| Sprint 1 | 2 tuần | Members + Plans | |
| Sprint 2 | 2 tuần | Subscription + Payment + PDF | |
| Sprint 3 | 1 tuần | Reminder + Report | |
| Sprint 4 | 1 tuần | Settings + Backup + Audit | |
| UAT | 1 tuần | Fix lỗi, demo, đóng gói jpackage | |

---

## 🌱 10. Hướng phát triển
- Quản lý đa chi nhánh (Cloud + API)
- Gửi Zalo/Email thông báo hết hạn
- Check-in bằng QR/CCCD
- Đồng bộ đám mây với server trung tâm
- Phiên bản Web hoặc Mobile App

---

## 👥 11. Thông tin nhóm
**Lớp:** C2406G  
**Nhóm:** 2  
**Giảng viên hướng dẫn:** Lục Văn Tiến  
**Thành viên:**  
| Họ tên | Vai trò | Phụ trách |
|--------|-----------|------------|
| Nguyễn Văn A | Leader/Dev | Kiến trúc, DB |
| Trần Thị B | Dev UI | FXML, SceneBuilder |
| Phạm Văn C | Dev Backend | Logic, DAO, Service |
| Lê Văn D | Tester | Testcase, báo cáo |
| Nguyễn Thị E | PM/BA | Tài liệu, tiến độ |

---

## 🧾 12. Thông tin kỹ thuật
| Thành phần | Phiên bản |
|-------------|------------|
| Java | 21+ |
| JavaFX | 21 |
| MySQL | 8.x |
| Maven | 3.9+ |
| Flyway | 10+ |
| Apache POI / PDFBox | Latest |
| Logback / SLF4J | Latest |
| SceneBuilder | 22 |
