# HƯỚNG DẪN THỰC CHIẾN: TẠO 1 MODULE CỤ THỂ VỚI HTMX
*(Ví dụ: Module Quản lý Chuyên khoa - Specialty)*

Tài liệu này hướng dẫn cách xây dựng một module hoàn chỉnh từ Backend tới Frontend, sử dụng **HTMX** để render giao diện một phần (Partial Rendering) thông qua các "API giao diện".

---

## 1. KHÁI NIỆM "HTMX API" TRONG SPRING BOOT
Khác với API truyền thống (trả về JSON), khi kết hợp với HTMX, chúng ta tạo ra các API trả về **Mảnh HTML (HTML Fragments)**.
- **Request:** Browser gửi yêu cầu qua HTMX (ví dụ `hx-get`).
- **Processing:** Controller xử lý dữ liệu.
- **Response:** Trả về một phần của file HTML (Fragment).
- **Update:** HTMX tự động "đắp" phần HTML đó vào vị trí chỉ định trên trang hiện tại.

---

## 2. TRIỂN KHAI MODULE CỤ THỂ (QUY TRÌNH 5 BƯỚC)

### Bước 1: Model & Repository
```java
// model/Specialty.java
@Entity
public class Specialty {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
}

// repository/SpecialtyRepository.java
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {}
```

### Bước 2: Xử lý tại Controller (API Giao diện)
Chúng ta sẽ viết 2 hàm: 1 hàm render trang chính và 1 hàm "API" chỉ render cái bảng.

```java
@Controller
@RequestMapping("/specialties")
public class SpecialtyController {
    
    @Autowired private SpecialtyRepository specialtyRepo;

    // 1. Render trang chủ của module (Toàn bộ trang)
    @GetMapping
    public String index(Model model) {
        model.addAttribute("list", specialtyRepo.findAll());
        return "specialty/index";
    }

    // 2. API cho HTMX: Chỉ trả về đoạn HTML của cái bảng
    @GetMapping("/search")
    public String search(@RequestParam String name, Model model) {
        // Giả sử có logic tìm kiếm
        model.addAttribute("list", specialtyRepo.findByNameContaining(name));
        
        // Trả về fragment 'tablePart' trong file 'index.html'
        return "specialty/index :: tablePart"; 
    }
}
```

### Bước 3: Thiết kế Giao diện với HTMX Fragments
Trong file `src/main/resources/templates/specialty/index.html`:

```html
<!-- Cấu trúc trang chính -->
<div th:replace="~{layout/base :: layout(~{::content})}">
    <div th:fragment="content">
        
        <!-- Thanh tìm kiếm dùng HTMX -->
        <input type="text" name="name" 
               placeholder="Tìm chuyên khoa..."
               hx-get="/specialties/search" 
               hx-trigger="keyup changed delay:300ms" 
               hx-target="#result-table">

        <!-- Vùng chứa bảng dữ liệu -->
        <div id="result-table">
            <!-- Đánh dấu fragment để Controller có thể gọi riêng lẻ -->
            <table th:fragment="tablePart" class="min-w-full bg-white">
                <thead> ... </thead>
                <tbody>
                    <tr th:each="item : ${list}">
                        <td th:text="${item.name}"></td>
                        <td>
                            <!-- Nút xóa nhanh bằng HTMX -->
                            <button th:attr="hx-delete='/specialties/delete/' + ${item.id}"
                                    hx-target="closest tr" 
                                    hx-swap="outerHTML">Xóa</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

    </div>
</div>
```

---

## 3. CÁCH HOẠT ĐỘNG CỦA CẶP ĐÔI "API & HTMX"

| Thuộc tính HTMX | Ý nghĩa |
|:---|:---|
| `hx-get="/url"` | Gửi request GET tới API. |
| `hx-trigger="..."` | Khi nào thì gửi (ví dụ: `keyup` - khi gõ phím, `click` - khi nhấn). |
| `hx-target="#id"` | Lấy kết quả HTML trả về và nhét vào vùng có ID này. |
| `hx-swap="..."` | Cách thay thế (ví dụ: `outerHTML` - thay thế cả thẻ, `innerHTML` - chỉ thay nội dung bên trong). |

---

## 4. ƯU ĐIỂM CỦA CÁCH LÀM NÀY
1.  **Tốc độ:** Server chỉ render một đoạn HTML nhỏ, tiết kiệm băng thông và tài nguyên.
2.  **Trải nghiệm:** Người dùng không thấy trang bị nháy (loading) khi tìm kiếm hoặc xóa.
3.  **Dễ code:** Không cần viết phức tạp bằng JavaScript/Ajax, chỉ cần gắn thuộc ngữ HTMX vào thẻ HTML.

---
**Lưu ý cho Newbie:** Nếu dùng `hx-delete` hoặc `hx-post`, đừng quên thêm mã bảo mật **CSRF** vào cấu hình hoặc thẻ meta để Spring Security không chặn yêu cầu (chi tiết xem phần Security trong file `instruction.md`).
