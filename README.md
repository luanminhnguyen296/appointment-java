# 🏥 Doctor Appointment Management System

Hệ thống quản lý lịch hẹn bác sĩ chuyên nghiệp được xây dựng trên nền tảng **Spring Boot** kết hợp với kiến trúc **Server-side Pagination** và giao diện hiện đại.

## 🚀 Tính năng nổi bật

- **Quản lý Bệnh nhân (Patients):**
  - Thêm, sửa, xóa thông tin khách hàng.
  - Phân trang dữ liệu tại Server (**Server-side Pagination**) giúp hệ thống xử lý mượt mà ngay cả khi có hàng triệu bản ghi.
  - Tìm kiếm nâng cao theo Tên, Số điện thoại, Email.
  - Bộ lọc giới tính thông minh.
- **Giao diện Chuyên nghiệp:**
  - Thiết kế hiện đại với **Tailwind CSS**.
  - Tương tác mượt mà bằng **Alpine.js**.
  - Bảng dữ liệu thông minh sử dụng **DataTables.net**.
  - Hệ thống icon sắc nét từ **Remix Icon**.
- **Hệ thống mạnh mẽ:**
  - Backend sử dụng **Spring Boot 3**.
  - Cơ sở dữ liệu **MySQL** chạy trên **Docker**.
  - Tự động đồng bộ cấu trúc Database qua **Hibernate DDL**.

## 🛠 Công nghệ sử dụng

- **Backend:** Java 17+, Spring Boot, Spring Data JPA, Hibernate.
- **Frontend:** Thymeleaf, Tailwind CSS, Alpine.js, DataTables, jQuery, Remix Icon.
- **Infrastructure:** Maven, Docker, MySQL.

## ⚙️ Hướng dẫn cài đặt & Chạy ứng dụng

### 1. Chuẩn bị

- Đã cài đặt **Java 17** hoặc mới hơn.
- Đã cài đặt **Maven**.
- Đã cài đặt **Docker** và **Docker Compose**.

### 2. Khởi động Cơ sở dữ liệu (MySQL)

Mở terminal tại thư mục gốc của project và chạy lệnh:

```bash
docker-compose -f docker/docker-compose.yml up -d
```

_Lưu ý: Database sẽ được tạo tự động với tên `appointment`._

### 3. Chạy ứng dụng Spring Boot

```bash
mvn clean spring-boot:run
```

### 4. Truy cập ứng dụng

Mở trình duyệt và truy cập:

- **Trang danh sách bệnh nhân:** [http://localhost:8080/patients](http://localhost:8080/patients)
- **API Datatable (Json):** [http://localhost:8080/api/v1/patients/list-datatable](http://localhost:8080/api/v1/patients/list-datatable)

## 📁 Cấu trúc thư mục chính

- `src/main/java/`: Chứa mã nguồn logic xử lý (Controller, Service, Repository, Model).
- `src/main/resources/templates/`: Chứa giao diện Thymeleaf (Layout, Patient List/Form).
- `docker/`: Chứa cấu hình môi trường Docker cho MySQL.
- `pom.xml`: Quản lý phụ thuộc (dependencies) của Maven.

---

Phát triển bởi **Antigravity** 🚀
