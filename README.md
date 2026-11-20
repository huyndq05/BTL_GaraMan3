# GaraMan3

GaraMan3 là hệ thống quản lý gara ô tô cho bài tập lớn môn Phân tích và Thiết kế Hệ thống Thông tin, đề số 27. Bản triển khai gồm hai luồng chính. Khách hàng đặt lịch sửa xe trực tuyến. Nhân viên bán hàng tiếp nhận xe, chọn hạng mục và phụ tùng, phân công kỹ thuật viên rồi xác nhận phiếu.

[Báo cáo 27B23DCCN393.docx](27B23DCCN393.docx)

## Yêu cầu cài đặt

- Git để tải mã nguồn.
- JDK tương thích với Java 8 và Maven. Chạy `java -version` và `mvn -version` để kiểm tra JDK mà Maven đang dùng.
- MySQL Server 8.0 đang chạy. Cấu hình mẫu dùng `localhost:3306`.
- Cổng `8080` còn trống. Maven sẽ khởi động Tomcat theo cấu hình dự án.

## 1. Tải mã nguồn

```bash
git clone https://github.com/huyndq05/BTL_GaraMan3.git
cd BTL_GaraMan3/GaraMan3
```

## 2. Khởi tạo cơ sở dữ liệu

Từ thư mục `GaraMan3`, chạy lệnh sau và nhập mật khẩu MySQL khi được hỏi.

```bash
mysql --default-character-set=utf8mb4 -u root -p --execute="source database_garaman3.sql"
```

Nếu dùng MySQL Workbench, mở và chạy tệp [database_garaman3.sql](GaraMan3/database_garaman3.sql). Script tạo schema `garaman3` và nạp dữ liệu mẫu. **Script xóa schema `garaman3` nếu đã tồn tại.** Hãy sao lưu trước nếu schema đang chứa dữ liệu cần giữ. Bỏ qua bước này khi dùng lại CSDL GaraMan3 đã được khởi tạo.

## 3. Cấu hình kết nối

Tạo `src/main/resources/db.properties` từ tệp mẫu.

**PowerShell**

```powershell
Copy-Item src/main/resources/db.properties.example src/main/resources/db.properties
```

**Bash**

```bash
cp src/main/resources/db.properties.example src/main/resources/db.properties
```

Mở tệp vừa tạo. Điền tài khoản và mật khẩu MySQL vào `db.username` và `db.password`. Sửa máy chủ, cổng hoặc tên schema trong `db.url` nếu dùng cấu hình khác. Git bỏ qua `db.properties` để thông tin đăng nhập MySQL chỉ nằm trên máy của bạn.

## 4. Chạy hệ thống

Trong thư mục `GaraMan3`, chạy lệnh sau.

```bash
mvn tomcat7:run
```

Trên Windows, bạn cũng có thể chạy `run.bat`. Khi Tomcat khởi động xong, mở [http://localhost:8080/GaraMan3/](http://localhost:8080/GaraMan3/). Nhấn `Ctrl+C` trong cửa sổ chạy để dừng máy chủ.

## 5. Thử các luồng chính

- **Khách hàng đặt lịch.** Từ trang chủ, bấm **Đặt lịch hẹn**, điền thông tin khách và xe, chọn dịch vụ, hạng mục, ngày và giờ. Xem lại thông tin rồi xác nhận. Ngày kín lịch hiển thị dấu **X**. Khung giờ đủ ba xe bị khóa.
- **Nhân viên nhận xe.** Mở [trang đăng nhập nhân viên](http://localhost:8080/GaraMan3/LoginScreen3.jsp). Tài khoản mẫu là `nvbanhang`, mật khẩu mẫu `123456`. Chọn **Nhận xe vào sửa**, chọn khách và xe, kiểm tra các hạng mục, thêm phụ tùng nếu cần, phân công kỹ thuật viên và xác nhận phiếu.

Sau khi nạp SQL, lịch mẫu có một ngày kín và một ngày chỉ kín khung **11:00**. Ngày cụ thể được tính theo thời điểm nạp dữ liệu.

## Xử lý lỗi thường gặp

- **Không tìm thấy `mvn`.** Kiểm tra Maven đã được thêm vào `PATH` và `JAVA_HOME` trỏ đến thư mục JDK.
- **Trang mở nhưng thiếu dữ liệu mẫu.** Kiểm tra dịch vụ MySQL, schema `garaman3` và thông tin trong `db.properties`, rồi khởi động lại Tomcat.
- **Cổng `8080` đang được dùng.** Dừng ứng dụng đang sử dụng cổng hoặc đổi `<port>` trong [pom.xml](GaraMan3/pom.xml) và truy cập URL theo cổng mới.

Có thể kiểm tra bước biên dịch bằng `mvn test` trong thư mục `GaraMan3`.
