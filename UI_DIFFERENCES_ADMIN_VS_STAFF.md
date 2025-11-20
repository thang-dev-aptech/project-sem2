# 🎨 So sánh Giao diện: ADMIN (OWNER) vs STAFF

## 📋 Tổng quan
Tài liệu này mô tả sự khác biệt về giao diện và chức năng giữa **ADMIN (OWNER)** và **STAFF** trong ứng dụng GymPro.

---

## 🗂️ 1. MENU NAVIGATION (Thanh điều hướng bên trái)

| Menu Item | ADMIN (OWNER) | STAFF | Ghi chú |
|-----------|---------------|-------|---------|
| 📊 Dashboard | ✅ Hiển thị | ✅ Hiển thị | Cả 2 đều xem được |
| 🧍 Members | ✅ Hiển thị | ✅ Hiển thị | Cả 2 đều quản lý được |
| 💪 Packages | ✅ Hiển thị | ❌ **ẨN HOÀN TOÀN** | Chỉ ADMIN quản lý gói tập |
| 📅 Registration | ✅ Hiển thị | ✅ Hiển thị | Cả 2 đều đăng ký/gia hạn được |
| 💳 Payment | ✅ Hiển thị | ✅ Hiển thị | Cả 2 đều thanh toán được |
| ⏰ Expiring Members | ✅ Hiển thị | ✅ Hiển thị | Cả 2 đều xem được |
| 📈 Reports | ✅ Hiển thị | ✅ Hiển thị | Cả 2 đều xem được |
| ⚙️ Settings | ✅ Hiển thị | ❌ **ẨN HOÀN TOÀN** | Chỉ ADMIN cấu hình hệ thống |
| 👤 Users | ✅ Hiển thị | ❌ **ẨN HOÀN TOÀN** | Chỉ ADMIN quản lý user |

**Kết quả:**
- **ADMIN**: Thấy **9 menu items**
- **STAFF**: Chỉ thấy **6 menu items** (thiếu Packages, Settings, Users)

---

## 👥 2. TRANG QUẢN LÝ HỘI VIÊN (Members)

| Chức năng | ADMIN (OWNER) | STAFF | Mô tả |
|-----------|---------------|-------|-------|
| **Xem danh sách** | ✅ | ✅ | Cả 2 đều xem được bảng hội viên |
| **Tìm kiếm/Lọc** | ✅ | ✅ | Cả 2 đều tìm kiếm được |
| **Xem chi tiết** | ✅ | ✅ | Click vào hàng → hiện form chi tiết |
| **Thêm mới** | ✅ | ✅ | Nút "➕ Thêm Hội viên Mới" |
| **Chỉnh sửa** | ✅ | ✅ | Nút "✏️ Chỉnh sửa" trong cột Actions |
| **Xóa** | ✅ | ❌ **NÚT ẨN** | Chỉ ADMIN có nút "🗑️ Xóa" |

**Giao diện STAFF:**
```
┌─────────────────────────────────────┐
│  [Tìm kiếm] [Lọc trạng thái ▼]     │
├─────────────────────────────────────┤
│  Bảng danh sách hội viên            │
│  [Mã] [Tên] [SĐT] ... [Actions]     │
│  ─────────────────────────────────  │
│  GYM-001 | Nguyễn Văn A | [✏️ Sửa] │
│  GYM-002 | Trần Thị B   | [✏️ Sửa] │
└─────────────────────────────────────┘
┌─────────────────────────────────────┐
│  Chi tiết Hội viên                  │
│  [Mã] [Tên] [SĐT] ...               │
│  [💾 Lưu Thay đổi] [❌ Hủy]        │
│  (KHÔNG CÓ nút Xóa)                 │
└─────────────────────────────────────┘
```

**Giao diện ADMIN:**
```
┌─────────────────────────────────────┐
│  [Tìm kiếm] [Lọc trạng thái ▼]     │
├─────────────────────────────────────┤
│  Bảng danh sách hội viên            │
│  [Mã] [Tên] [SĐT] ... [Actions]     │
│  ─────────────────────────────────  │
│  GYM-001 | Nguyễn Văn A | [✏️ Sửa] │
│  GYM-002 | Trần Thị B   | [✏️ Sửa] │
└─────────────────────────────────────┘
┌─────────────────────────────────────┐
│  Chi tiết Hội viên                  │
│  [Mã] [Tên] [SĐT] ...               │
│  [💾 Lưu] [🗑️ Xóa] [❌ Hủy]        │
│  (CÓ nút Xóa)                        │
└─────────────────────────────────────┘
```

---

## 💪 3. TRANG QUẢN LÝ GÓI TẬP (Packages)

| Chức năng | ADMIN (OWNER) | STAFF | Mô tả |
|-----------|---------------|-------|-------|
| **Xem menu** | ✅ | ❌ **MENU ẨN** | STAFF không thấy menu "💪 Packages" |
| **Xem danh sách** | ✅ | ❌ | Nếu vào được, chỉ thấy bảng (read-only) |
| **Form quản lý** | ✅ | ❌ **FORM ẨN** | STAFF không thấy form thêm/sửa/xóa |
| **Thêm mới** | ✅ | ❌ | Chỉ ADMIN |
| **Chỉnh sửa** | ✅ | ❌ | Chỉ ADMIN |
| **Xóa** | ✅ | ❌ | Chỉ ADMIN |

**Giao diện STAFF:**
- **KHÔNG THẤY** menu "💪 Packages" trong navigation
- Nếu cố truy cập trực tiếp → Form bị ẩn, chỉ thấy bảng (nếu có)

**Giao diện ADMIN:**
```
┌─────────────────────────────────────┐
│  [Tìm kiếm] [Lọc trạng thái ▼]     │
├─────────────────────────────────────┤
│  Bảng danh sách gói tập            │
│  [Mã] [Tên] [Giá] ... [Actions]     │
│  ─────────────────────────────────  │
│  PKG-001 | Gói 1 tháng | [✏️ Sửa] │
└─────────────────────────────────────┘
┌─────────────────────────────────────┐
│  Chi tiết Gói tập                   │
│  [Mã] [Tên] [Giá] [Số ngày] ...    │
│  [💾 Lưu] [🗑️ Xóa] [❌ Hủy]        │
└─────────────────────────────────────┘
```

---

## 📅 4. TRANG ĐĂNG KÝ/GIA HẠN (Registration)

| Chức năng | ADMIN (OWNER) | STAFF | Ghi chú |
|-----------|---------------|-------|---------|
| **Xem menu** | ✅ | ✅ | Cả 2 đều thấy |
| **Đăng ký mới** | ✅ | ✅ | Cả 2 đều tạo được |
| **Gia hạn** | ✅ | ✅ | Cả 2 đều gia hạn được |
| **Tính toán tự động** | ✅ | ✅ | Logic giống nhau |

**→ KHÔNG CÓ KHÁC BIỆT** (Cả 2 đều có quyền đầy đủ)

---

## 💳 5. TRANG THANH TOÁN (Payment)

| Chức năng | ADMIN (OWNER) | STAFF | Ghi chú |
|-----------|---------------|-------|---------|
| **Xem menu** | ✅ | ✅ | Cả 2 đều thấy |
| **Xem hóa đơn chưa thanh toán** | ✅ | ✅ | Cả 2 đều xem được |
| **Xử lý thanh toán** | ✅ | ✅ | Cả 2 đều thanh toán được |
| **Tạo QR code** | ✅ | ✅ | Cả 2 đều tạo được |
| **In hóa đơn** | ✅ | ✅ | Cả 2 đều in được |

**→ KHÔNG CÓ KHÁC BIỆT** (Cả 2 đều có quyền đầy đủ)

**Lưu ý:** Có thể cần phân quyền chiết khấu (% tối đa) trong tương lai:
- ADMIN: Chiết khấu không giới hạn
- STAFF: Chiết khấu tối đa 10% (ví dụ)

---

## ⏰ 6. TRANG HỘI VIÊN SẮP HẾT HẠN (Expiring Members)

| Chức năng | ADMIN (OWNER) | STAFF | Ghi chú |
|-----------|---------------|-------|---------|
| **Xem danh sách** | ✅ | ✅ | Cả 2 đều xem được |
| **Gia hạn từ danh sách** | ✅ | ✅ | Cả 2 đều gia hạn được |
| **Gửi nhắc nhở** | ✅ | ✅ | Cả 2 đều gửi được |

**→ KHÔNG CÓ KHÁC BIỆT** (Cả 2 đều có quyền đầy đủ)

---

## 📈 7. TRANG BÁO CÁO (Reports)

| Chức năng | ADMIN (OWNER) | STAFF | Ghi chú |
|-----------|---------------|-------|---------|
| **Xem menu** | ✅ | ✅ | Cả 2 đều thấy |
| **Xem báo cáo** | ✅ | ✅ | Cả 2 đều xem được |
| **Xuất Excel/PDF** | ✅ | ✅ | Cả 2 đều xuất được |

**→ KHÔNG CÓ KHÁC BIỆT** (Cả 2 đều có quyền đầy đủ)

**Lưu ý:** Có thể cần phân quyền trong tương lai:
- ADMIN: Xem tất cả báo cáo (doanh thu, lợi nhuận, chi phí)
- STAFF: Chỉ xem báo cáo cơ bản (số hội viên, đăng ký mới)

---

## ⚙️ 8. TRANG CẤU HÌNH (Settings)

| Chức năng | ADMIN (OWNER) | STAFF | Mô tả |
|-----------|---------------|-------|-------|
| **Xem menu** | ✅ | ❌ **MENU ẨN** | STAFF không thấy menu "⚙️ Settings" |
| **Cấu hình hệ thống** | ✅ | ❌ | Chỉ ADMIN |
| **Backup/Restore** | ✅ | ❌ | Chỉ ADMIN |
| **Cấu hình thông báo** | ✅ | ❌ | Chỉ ADMIN |

**→ STAFF KHÔNG THẤY MENU NÀY**

---

## 👤 9. TRANG QUẢN LÝ NGƯỜI DÙNG (Users)

| Chức năng | ADMIN (OWNER) | STAFF | Mô tả |
|-----------|---------------|-------|-------|
| **Xem menu** | ✅ | ❌ **MENU ẨN** | STAFF không thấy menu "👤 Users" |
| **Xem danh sách user** | ✅ | ❌ | Chỉ ADMIN |
| **Thêm/Sửa/Xóa user** | ✅ | ❌ | Chỉ ADMIN |
| **Phân quyền** | ✅ | ❌ | Chỉ ADMIN |

**→ STAFF KHÔNG THẤY MENU NÀY**

---

## 📊 TÓM TẮT TRỰC QUAN

### ADMIN (OWNER) - Giao diện đầy đủ
```
┌─────────────────────────────────────┐
│  📊 Dashboard                       │
│  🧍 Members                         │
│  💪 Packages          ← CÓ          │
│  📅 Registration                    │
│  💳 Payment                         │
│  ⏰ Expiring Members                │
│  📈 Reports                         │
│  ⚙️ Settings          ← CÓ          │
│  👤 Users             ← CÓ          │
└─────────────────────────────────────┘
```

### STAFF - Giao diện hạn chế
```
┌─────────────────────────────────────┐
│  📊 Dashboard                       │
│  🧍 Members (không có nút Xóa)      │
│  💪 Packages          ← ẨN         │
│  📅 Registration                    │
│  💳 Payment                         │
│  ⏰ Expiring Members                │
│  📈 Reports                         │
│  ⚙️ Settings          ← ẨN         │
│  👤 Users             ← ẨN         │
└─────────────────────────────────────┘
```

---

## 🎯 ĐIỂM KHÁC BIỆT CHÍNH

1. **Menu Navigation:**
   - ADMIN: 9 menu items
   - STAFF: 6 menu items (thiếu Packages, Settings, Users)

2. **Members Page:**
   - ADMIN: Có nút "🗑️ Xóa"
   - STAFF: Không có nút Xóa

3. **Packages Page:**
   - ADMIN: Quản lý đầy đủ (CRUD)
   - STAFF: Menu ẩn, form ẩn

4. **Settings & Users:**
   - ADMIN: Có quyền truy cập
   - STAFF: Menu ẩn hoàn toàn

5. **Các trang khác (Registration, Payment, Reports, Expiring):**
   - **KHÔNG CÓ KHÁC BIỆT** - Cả 2 đều có quyền đầy đủ

---

## 💡 ĐỀ XUẤT PHÂN QUYỀN BỔ SUNG (Tùy chọn)

### 1. Chiết khấu trong Payment:
- ADMIN: Không giới hạn %
- STAFF: Tối đa 10% (cần thêm validation)

### 2. Báo cáo chi tiết:
- ADMIN: Xem tất cả (doanh thu, lợi nhuận, chi phí)
- STAFF: Chỉ xem báo cáo cơ bản (số hội viên, đăng ký mới)

### 3. Export dữ liệu:
- ADMIN: Export tất cả dữ liệu
- STAFF: Chỉ export dữ liệu trong phạm vi quyền

---

## ✅ KẾT LUẬN

**ADMIN (OWNER)** có quyền **TOÀN QUYỀN**:
- Quản lý tất cả module
- Xóa dữ liệu
- Cấu hình hệ thống
- Quản lý người dùng

**STAFF** có quyền **HẠN CHẾ**:
- Không quản lý gói tập
- Không xóa hội viên
- Không cấu hình hệ thống
- Không quản lý người dùng
- Nhưng vẫn có thể: Đăng ký, Gia hạn, Thanh toán, Xem báo cáo

