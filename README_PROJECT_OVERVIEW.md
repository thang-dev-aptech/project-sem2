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
| M08 | Người dùng & Vai trò | Phân quyền: Manager, Staff | Bảo mật |

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
- Đăng nhập người dùng (Manager, Staff).
- Mã hoá mật khẩu bằng **bcrypt**.
- Ghi lại mọi thao tác nhạy cảm vào **audit_logs**.
- Khóa màn hình khi idle X phút.
- Cấp quyền tối thiểu cho user `gympro_app` trong MySQL.

---

## 🚀 9. Kế hoạch triển khai (PM Plan)

### 📊 Hiện trạng dự án
**✅ Đã hoàn thành:**
- Database schema đầy đủ (V1__GymPro_Complete_Schema.sql)
- Database initialization (01-init-database.sql)
- Docker Compose setup
- FXML UI layouts cho tất cả screens
- Cấu hình application.properties
- ViewModels cơ bản cho data binding
- DatabaseConnection utility

**❌ Cần hoàn thiện:**
- Domain/Entity classes
- Repository/DAO layer
- Service layer (business logic)
- Controller implementations đầy đủ
- Authentication & Authorization
- Business rules validation
- PDF/Excel export
- Unit/Integration tests

### 🎯 Kế hoạch triển khai chi tiết

| Sprint | Thời gian | Nhiệm vụ chính | Công việc cụ thể | Trạng thái |
|--------|-----------|----------------|------------------|------------|
| **Sprint 0** | 1.5 tuần | Setup & Foundation | | ✅ Hoàn thành |
| | | ✓ Database Schema | Schema migration | ✅ |
| | | ✓ Docker Setup | Docker Compose | ✅ |
| | | ✓ FXML Layouts | UI screens | ✅ |
| | | ✓ Project Structure | Maven, dependencies | ✅ |
| **Sprint 1** | 2 tuần | Authentication & Core Domain | | 🔄 Cần làm |
| | | ✓ Domain Entities | Branch, User, Member, Plan | ⏳ |
| | | ✓ Login Service | Authentication với bcrypt | ⏳ |
| | | ✓ Session Management | User context | ⏳ |
| | | ✓ Repository Base | Base DAO pattern | ⏳ |
| **Sprint 2** | 2 tuần | Members & Plans Management | | ⏳ Chưa bắt đầu |
| | | ✓ Member Repository/Service | CRUD operations | ⏳ |
| | | ✓ Plan Repository/Service | CRUD operations | ⏳ |
| | | ✓ Member Controller | Business logic | ⏳ |
| | | ✓ Plan Controller | Business logic | ⏳ |
| | | ✓ Data Validation | Phone/Email checks | ⏳ |
| **Sprint 3** | 2 tuần | Subscription & Payment | | ⏳ Chưa bắt đầu |
| | | ✓ Subscription Service | Renewal logic (R01, R02) | ⏳ |
| | | ✓ Invoice Service | Invoice generation | ⏳ |
| | | ✓ Payment Service | Payment processing | ⏳ |
| | | ✓ Discount Logic (R04) | Apply discount rules | ⏳ |
| | | ✓ PDF Invoice Export | Invoice printing | ⏳ |
| **Sprint 4** | 1.5 tuần | Reminders & Reports | | ⏳ Chưa bắt đầu |
| | | ✓ Reminder Service | GRACE_DAYS logic (R05) | ⏳ |
| | | ✓ Dashboard Service | Metrics & stats | ⏳ |
| | | ✓ Report Service | Revenue reports | ⏳ |
| | | ✓ Excel Export | Export to Excel | ⏳ |
| **Sprint 5** | 1.5 tuần | Settings, Backup, Audit | | ⏳ Chưa bắt đầu |
| | | ✓ Settings Service | System configuration | ⏳ |
| | | ✓ Backup Service | DB backup/restore | ⏳ |
| | | ✓ Audit Log | Audit trail (R07) | ⏳ |
| | | ✓ User Management | User CRUD | ⏳ |
| **Sprint 6** | 1.5 tuần | Testing & Polish | | ⏳ Chưa bắt đầu |
| | | ✓ Unit Tests | Domain/Service layer | ⏳ |
| | | ✓ Integration Tests | Repository layer | ⏳ |
| | | ✓ UI Tests | Controller validation | ⏳ |
| | | ✓ Bug Fixes | Performance & bugs | ⏳ |
| | | ✓ Documentation | User manual | ⏳ |
| **UAT** | 1 tuần | User Acceptance Testing | | ⏳ Chưa bắt đầu |
| | | ✓ Demo session | Stakeholder demo | ⏳ |
| | | ✓ UAT Feedback | User feedback | ⏳ |
| | | ✓ Final fixes | Critical bugs | ⏳ |
| | | ✓ Packaging | jpackage/installer | ⏳ |
| **Total** | **11.5 tuần** | | | **~3 tháng** |

### 📋 Hướng dẫn triển khai Sprint theo Sprint

#### 🔐 Sprint 1: Authentication & Core Domain (2 tuần)

**Tuần 1: Domain Entities**
```
1. Tạo Domain Entities:
   - domain/Branch.java
   - domain/User.java
   - domain/Role.java
   - domain/Member.java
   - domain/Plan.java
   - domain/Subscription.java
   - domain/Invoice.java
   - domain/Payment.java
   - domain/PaymentMethod.java
   - domain/SystemConfig.java
   - domain/AuditLog.java

2. Thêm getters/setters
3. Thêm constructors
4. Thêm equals/hashCode/toString
```

**Tuần 2: Authentication**
```
1. Repository Layer:
   - repository/BaseRepository.java (template pattern)
   - repository/UserRepository.java
   - repository/MemberRepository.java

2. Service Layer:
   - service/AuthService.java
   - service/SessionManager.java

3. Controller:
   - Update LoginController.java với logic authentication
   - Add bcrypt password verification
   - Add session management

4. Testing:
   - Test login flow
   - Test session timeout
```

#### 👥 Sprint 2: Members & Plans Management (2 tuần)

**Tuần 1: Members**
```
1. Repository:
   - repository/MemberRepository.java (CRUD)
   - Add findByPhone(), findByCode()

2. Service:
   - service/MemberService.java
   - Member code generation (R06)
   - Phone duplicate check
   - Status management

3. Controller:
   - Update MembersController.java
   - Add CRUD operations
   - Add search/filter
   - Add member dialog

4. Testing:
   - Test CRUD operations
   - Test duplicate phone check
```

**Tuần 2: Plans**
```
1. Repository:
   - repository/PlanRepository.java (CRUD)

2. Service:
   - service/PlanService.java

3. Controller:
   - Update PackagesController.java
   - Add plan management

4. Testing:
   - Test plan CRUD
   - Test active/inactive toggle
```

#### 💳 Sprint 3: Subscription & Payment (2 tuần)

**Tuần 1: Subscription**
```
1. Repository:
   - repository/SubscriptionRepository.java

2. Service:
   - service/SubscriptionService.java
   - Implement CalculateRenewalDates (R01, R02)
   - Status transitions (R03)

3. Controller:
   - Update RegistrationController.java

4. Testing:
   - Test R01 (còn hạn)
   - Test R02 (hết hạn)
```

**Tuần 2: Payment & Invoice**
```
1. Repository:
   - repository/InvoiceRepository.java
   - repository/PaymentRepository.java

2. Service:
   - service/InvoiceService.java
   - service/PaymentService.java
   - service/PDFExportService.java
   - Implement R04 (discount logic)

3. Controller:
   - Update PaymentController.java

4. Testing:
   - Test invoice generation
   - Test payment processing
   - Test PDF export
```

#### 📊 Sprint 4: Reminders & Reports (1.5 tuần)

**Tuần 1: Reminders & Dashboard**
```
1. Repository:
   - repository/ReminderRepository.java

2. Service:
   - service/ReminderService.java
   - Implement R05 (GRACE_DAYS)
   - service/DashboardService.java

3. Controller:
   - Update ExpiryController.java
   - Update DashboardController.java

4. Testing:
   - Test reminder generation
   - Test dashboard metrics
```

**Tuần 2: Reports & Export**
```
1. Service:
   - service/ReportService.java
   - service/ExcelExportService.java

2. Controller:
   - Update ReportsController.java

3. Testing:
   - Test revenue reports
   - Test Excel export
```

#### ⚙️ Sprint 5: Settings, Backup, Audit (1.5 tuần)

**Tuần 1: Settings & User Management**
```
1. Repository:
   - repository/SystemConfigRepository.java

2. Service:
   - service/SettingsService.java
   - service/UserManagementService.java

3. Controller:
   - Update SettingsController.java
   - Update UserManagementController.java

4. Testing:
   - Test settings CRUD
   - Test user management
```

**Tuần 2: Backup & Audit**
```
1. Service:
   - service/BackupService.java
   - Audit logs (trigger-based - R07)

2. Testing:
   - Test backup/restore
   - Test audit logs
```

#### 🧪 Sprint 6: Testing & Polish (1.5 tuần)

**Tuần 1: Testing**
```
1. Unit Tests:
   - domain/*Test.java
   - service/*Test.java

2. Integration Tests:
   - repository/*Test.java
   - Test với test DB

3. UI Tests:
   - Controller tests
   - Form validation tests
```

**Tuần 2: Polish & Docs**
```
1. Bug fixes
2. Performance optimization
3. Code review
4. Documentation:
   - USER_MANUAL.md
   - DEVELOPER_GUIDE.md
   - API_DOCS.md
```

#### 🎯 UAT (1 tuần)

**Demo & Launch**
```
1. Demo với stakeholders
2. Collect feedback
3. Fix critical bugs
4. Package application (jpackage)
5. Create installer
6. Deploy documentation
```

### 📌 Công cụ & Best Practices

**Development**
- **IDE**: IntelliJ IDEA / VS Code
- **SCM**: Git với branching strategy (main, develop, feature/*, bugfix/*)
- **CI/CD**: GitHub Actions (optional)

**Testing**
- **JUnit 5**: Unit & Integration tests
- **TestContainers**: Database testing
- **Mockito**: Mock dependencies

**Code Quality**
- **Checkstyle**: Code style
- **SonarQube**: Code analysis (optional)
- **Code Review**: Peer review

**Documentation**
- **Markdown**: README, guides
- **JavaDoc**: API documentation
- **Mockups**: UI designs

### ⚠️ Quản lý rủi ro & Mitigation

| Rủi ro | Tác động | Xác suất | Mitigation |
|--------|----------|----------|-----------|
| Thành viên bỏ học giữa chừng | Cao | Trung bình | Phân công đa mảng, documentation đầy đủ, code review |
| Database connection issues | Cao | Thấp | Sử dụng connection pool, retry mechanism, logging đầy đủ |
| Performance với >10k records | Trung bình | Trung bình | Pagination, lazy loading, indexing, caching |
| PDF/Excel export bugs | Trung bình | Trung bình | Unit test, integration test, test với nhiều scenarios |
| Scope creep | Cao | Cao | Strict sprint planning, backlog management, prioritize MVP |
| Mobile/Responsive issues | Thấp | Thấp | Test trên nhiều screen sizes |

### 🎓 Learning Resources

**Java/JavaFX**
- [JavaFX Tutorial](https://openjfx.io/)
- [JavaFX Documentation](https://docs.oracle.com/javase/8/javafx/api/)

**MySQL**
- [MySQL 8.0 Reference](https://dev.mysql.com/doc/)
- [Flyway Documentation](https://flywaydb.org/documentation/)

**Testing**
- [JUnit 5 Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://site.mockito.org/)

**PDF/Excel**
- [Apache POI](https://poi.apache.org/)
- [PDFBox](https://pdfbox.apache.org/)

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
