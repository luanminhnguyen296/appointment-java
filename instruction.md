# HƯỚNG DẪN TOÀN DIỆN: XÂY DỰNG HỆ THỐNG ĐẶT LỊCH KHÁM BỆNH
*(Dành cho Newbie - Spring Boot MVC + JPA + Thymeleaf + HTMX)*

Tài liệu này tổng hợp toàn bộ quy trình từ thiết lập đến triển khai các tính năng nâng cao, giúp mọi thành viên trong team có thể tự xây dựng dự án từ con số 0.

---

## 1. TỔNG QUAN VỀ KIẾN TRÚC & CÔNG NGHỆ
Dự án sử dụng cơ chế **Server-Side Rendering (SSR)** với Spring Boot, kết hợp với các thư viện hiện đại để tối ưu giao diện:
- **Backend:** Spring Boot 3.2+ (Java 17).
- **Database:** MySQL + Spring Data JPA (Hibernate) - tự động sinh bảng từ code.
- **Frontend:** Thymeleaf (hiển thị dữ liệu) + Tailwind CSS (style nhanh bằng class).
- **Tương tác động (SPA-like):** HTMX (giúp load trang không cần F5).
- **Security:** Spring Security (Xử lý đăng nhập, phân quyền ADMIN/DOCTOR).

---

## 2. THIẾT LẬP DỰ ÁN (SETUP & CONFIG)

### 2.1 Cấu trúc thư mục chuẩn
```text
src/main/java/com/example/appointment/
├── controller/     <- Xử lý Request, trả về View
├── model/          <- Các Class Entity (Ánh xạ bảng Database)
├── repository/     <- Các Interface JPA (Thao tác Database)
├── service/        <- Business Logic (Xử lý tính toán, gọi Repo)
└── config/         <- Cấu hình Security, Beans...

src/main/resources/
├── templates/      <- Giao diện HTML (Thymeleaf)
│   ├── layout/     <- File khung chung (Header, Footer, Sidebar)
│   └── [module]/   <- Giao diện riêng (patient, doctor, appointment)
├── static/         <- File tĩnh: css, js, images
└── application.properties <- File "trái tim" của cấu hình
```

### 2.2 Cấu hình `application.properties`
Copy và chỉnh sửa thông tin Database của bạn vào đây:

```properties
# 1. Kết nối MySQL (Đảm bảo database 'clinic_db' đã được tạo)
spring.datasource.url=jdbc:mysql://localhost:3306/clinic_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# 2. Cấu hình JPA/Hibernate
# 'update': Tự động tạo bảng/thêm cột từ Class Java vào Database
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

# 3. Cấu hình Server & Thymeleaf
server.port=8080
spring.thymeleaf.cache=false
```

---

## 3. LUỒNG MVC & CÁCH TRUYỀN DỮ LIỆU (DATA BINDING)

### 3.1 Luồng hoạt động (The Flow)
1. **User** tương tác trên Browser (nhấn nút, gửi form).
2. **Controller** nhận request. Nó sử dụng `@Autowired` để gọi **Service**.
3. **Service** thực hiện logic và gọi **Repository** để lấy dữ liệu từ MySQL.
4. **Controller** lấy kết quả đó, dùng `model.addAttribute("key", value)` để đẩy vào "giỏ" dữ liệu.
5. **Thymeleaf Engine** quét file `.html`, tìm các thẻ có `th:*`, thay thế bằng dữ liệu thực tế và gửi HTML hoàn chỉnh về Browser.

### 3.2 Ví dụ cụ thể: Đưa danh sách Bác sĩ ra màn hình
**Trong Controller:**
```java
@GetMapping("/doctors")
public String list(Model model) {
    List<Doctor> doctors = doctorService.getAll();
    model.addAttribute("listDoctors", doctors); // Đẩy dữ liệu vào "giỏ"
    return "doctor/list"; // Trình duyệt sẽ tìm file templates/doctor/list.html
}
```
**Trong file HTML (`list.html`):**
```html
<tr th:each="doc : ${listDoctors}">
    <td th:text="${doc.fullName}">Tên bác sĩ</td>
    <td th:text="${doc.specialty}">Chuyên khoa</td>
</tr>
```

---

## 4. HỆ THỐNG LAYOUT & FRAGMENTS
Để không phải viết lại Header/Footer nhiều lần, chúng ta dùng cơ chế "Mảnh ghép".

1. **Tạo Layout chung (`base.html`):** Định nghĩa vùng trống bằng `th:replace="~{::content}"`.
2. **Sử dụng tại trang con:**
```html
<div th:replace="~{layout/base :: layout(~{::content})}">
    <div th:fragment="content">
        <!-- Chỉ viết nội dung riêng của trang vào đây -->
        <h1>Trang quản lý bệnh nhân</h1>
    </div>
</div>
```

---

## 5. THỰC CHIẾN TẠO MODULE VÀ SỬ DỤNG HTMX

HTMX cho phép cập nhật **một phần** trang web mà không cần load lại trang. Ta sẽ tạo các "API giao diện" trả về mảnh HTML (Fragment).

### Bước 1: Tạo API xử lý trong Controller
```java
@Controller
@RequestMapping("/specialties")
public class SpecialtyController {
    @Autowired private SpecialtyService specialtyService;

    // 1. Trang chính (Load toàn bộ)
    @GetMapping
    public String index(Model model) {
        model.addAttribute("list", specialtyService.getAll());
        return "specialty/index";
    }

    // 2. API cho HTMX (Chỉ trả về cái bảng)
    @GetMapping("/search")
    public String search(@RequestParam String name, Model model) {
        model.addAttribute("list", specialtyService.findByName(name));
        // Chỉ render phần 'tableContainer' trong file index.html
        return "specialty/index :: tableContainer"; 
    }
}
```

### Bước 2: Dùng HTMX render giao diện
Trong file `specialty/index.html`:
```html
<div th:fragment="content">
    <!-- input gọi API qua HTMX -->
    <input type="text" name="name" 
           hx-get="/specialties/search" 
           hx-trigger="keyup changed delay:300ms" 
           hx-target="#result-area">

    <div id="result-area">
        <!-- Fragment dành cho HTMX cập nhật -->
        <table th:fragment="tableContainer">
            <tr th:each="item : ${list}">
                <td th:text="${item.name}"></td>
                <td>
                    <!-- Nút xóa nhanh -->
                    <button th:attr="hx-delete='/specialties/' + ${item.id}"
                            hx-target="closest tr" 
                            hx-swap="outerHTML">Xóa</button>
                </td>
            </tr>
        </table>
    </div>
</div>
```

---

## 6. BẢO MẬT VỚI SPRING SECURITY
Dự án sử dụng Spring Security để bảo vệ các đường dẫn nhạy cảm.

### Cấu hình chính (SecurityConfig.java):
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/css/**", "/js/**").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/").permitAll());
    return http.build();
}
```

---

## 7. CÁC LỖI THƯỜNG GẶP (COMMON ERRORS)
1. **Lỗi 404:** Sai đường dẫn URL hoặc tên file HTML trong `return "tên_file"`.
2. **Lỗi 500 (Thymeleaf parse error):** Thường do quên đóng thẻ HTML hoặc sai cú pháp `th:*`.
3. **Lỗi 403 (Access Denied):** Do Spring Security chặn.
4. **Lỗi WhiteLabel Error:** Kiểm tra log console Java, tìm dòng "Caused by" để biết lỗi gốc.

---
**Quy trình 6 bước cho mỗi tính năng mới:**
1. Tạo Entity -> 2. Tạo Repository -> 3. Tạo Service -> 4. Tạo Controller -> 5. Tạo HTML -> 6. Test & Fix.

*Chúc team triển khai dự án thành công!*
