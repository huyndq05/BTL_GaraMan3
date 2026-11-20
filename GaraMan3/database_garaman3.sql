DROP DATABASE IF EXISTS garaman3;
CREATE DATABASE garaman3 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE garaman3;

-- 1. Bảng địa chỉ
CREATE TABLE tblFullAddress3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    district VARCHAR(255) NOT NULL,
    province VARCHAR(255) NOT NULL,
    detail VARCHAR(255) NOT NULL
);

-- 2. Bảng họ tên
CREATE TABLE tblFullName3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    lastName VARCHAR(255) NOT NULL,
    middleName VARCHAR(255),
    firstName VARCHAR(255) NOT NULL
);

-- 3. Bảng thông tin Gara
CREATE TABLE tblGara3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phoneNumber VARCHAR(50) NOT NULL,
    openTime VARCHAR(50) NOT NULL,
    closeTime VARCHAR(50) NOT NULL,
    note VARCHAR(255),
    tblFullAddress3id INT(10) NOT NULL,
    FOREIGN KEY (tblFullAddress3id) REFERENCES tblFullAddress3(id)
);

-- 4. Bảng khu vực kho
CREATE TABLE tblStorageZone3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    tblGara3id INT(10) NOT NULL,
    FOREIGN KEY (tblGara3id) REFERENCES tblGara3(id)
);

-- 5. Bảng phụ tùng
CREATE TABLE tblSparePart3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    quantity INT(10) NOT NULL DEFAULT 0,
    unit VARCHAR(50) NOT NULL,
    sellingPrice FLOAT(10) NOT NULL,
    description VARCHAR(255),
    tblStorageZone3id INT(10) NOT NULL,
    FOREIGN KEY (tblStorageZone3id) REFERENCES tblStorageZone3(id)
);

-- 6. Bảng nhân viên
CREATE TABLE tblStaff3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    birthDate DATE NOT NULL,
    email VARCHAR(255) NOT NULL,
    phoneNumber VARCHAR(50) NOT NULL,
    salary FLOAT(10) NOT NULL,
    role VARCHAR(50) NOT NULL,
    note VARCHAR(255),
    tblGara3id INT(10) NOT NULL,
    tblFullAddress3id INT(10) NOT NULL,
    tblFullName3id INT(10) NOT NULL,
    FOREIGN KEY (tblGara3id) REFERENCES tblGara3(id),
    FOREIGN KEY (tblFullAddress3id) REFERENCES tblFullAddress3(id),
    FOREIGN KEY (tblFullName3id) REFERENCES tblFullName3(id)
);

-- 7. Bảng nhân viên kho
CREATE TABLE tblWarehouseStaff3 (
    tblStaff3id INT(10) PRIMARY KEY,
    FOREIGN KEY (tblStaff3id) REFERENCES tblStaff3(id)
);

-- 8. Bảng nhân viên bán hàng
CREATE TABLE tblSalesStaff3 (
    tblStaff3id INT(10) PRIMARY KEY,
    FOREIGN KEY (tblStaff3id) REFERENCES tblStaff3(id)
);

-- 9. Bảng kỹ thuật viên
CREATE TABLE tblTechnician3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    phoneNumber VARCHAR(50) NOT NULL,
    specialization VARCHAR(255) NOT NULL,
    salary FLOAT(10) NOT NULL,
    status VARCHAR(50) NOT NULL,
    tblGara3id INT(10) NOT NULL,
    tblFullAddress3id INT(10) NOT NULL,
    tblFullName3id INT(10) NOT NULL,
    FOREIGN KEY (tblGara3id) REFERENCES tblGara3(id),
    FOREIGN KEY (tblFullAddress3id) REFERENCES tblFullAddress3(id),
    FOREIGN KEY (tblFullName3id) REFERENCES tblFullName3(id)
);

-- 10. Bảng nhà cung cấp
CREATE TABLE tblSupplier3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phoneNumber VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    taxCode VARCHAR(50) NOT NULL,
    note VARCHAR(255),
    tblFullAddress3id INT(10) NOT NULL,
    FOREIGN KEY (tblFullAddress3id) REFERENCES tblFullAddress3(id)
);

-- 11. Bảng khách hàng
CREATE TABLE tblCustomer3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    phoneNumber VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    note VARCHAR(255),
    tblFullAddress3id INT(10) NOT NULL,
    tblFullName3id INT(10) NOT NULL,
    FOREIGN KEY (tblFullAddress3id) REFERENCES tblFullAddress3(id),
    FOREIGN KEY (tblFullName3id) REFERENCES tblFullName3(id)
);

-- 12. Bảng xe
CREATE TABLE tblCar3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    plateNumber VARCHAR(50) UNIQUE NOT NULL,
    brand VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    year INT(10) NOT NULL,
    description VARCHAR(255),
    tblCustomer3id INT(10) NOT NULL,
    FOREIGN KEY (tblCustomer3id) REFERENCES tblCustomer3(id)
);

-- 13. Bảng dịch vụ
CREATE TABLE tblService3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price FLOAT(10) NOT NULL,
    duration INT(10) NOT NULL,
    description VARCHAR(255)
);

-- 14. Bảng hóa đơn nhập phụ tùng
CREATE TABLE tblImportOrder3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    importDate DATE NOT NULL,
    tblSupplier3id INT(10) NOT NULL,
    tblWarehouseStaff3id INT(10) NOT NULL,
    FOREIGN KEY (tblSupplier3id) REFERENCES tblSupplier3(id),
    FOREIGN KEY (tblWarehouseStaff3id) REFERENCES tblWarehouseStaff3(tblStaff3id)
);

-- 15. Bảng chi tiết nhập phụ tùng
CREATE TABLE tblImportOrderDetail3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    quantity INT(10) NOT NULL,
    price FLOAT(10) NOT NULL,
    tblImportOrder3id INT(10) NOT NULL,
    tblSparePart3id INT(10) NOT NULL,
    FOREIGN KEY (tblImportOrder3id) REFERENCES tblImportOrder3(id),
    FOREIGN KEY (tblSparePart3id) REFERENCES tblSparePart3(id)
);

-- 16. Bảng lịch hẹn
CREATE TABLE tblAppointment3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    appointmentDate DATE NOT NULL,
    appointmentTime VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    note VARCHAR(255),
    tblCustomer3id INT(10) NOT NULL,
    tblCar3id INT(10) NOT NULL,
    FOREIGN KEY (tblCustomer3id) REFERENCES tblCustomer3(id),
    FOREIGN KEY (tblCar3id) REFERENCES tblCar3(id)
);

-- 17. Bảng chi tiết dịch vụ lịch hẹn
CREATE TABLE tblAppointmentServiceDetail3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    tblAppointment3id INT(10) NOT NULL,
    tblService3id INT(10) NOT NULL,
    FOREIGN KEY (tblAppointment3id) REFERENCES tblAppointment3(id),
    FOREIGN KEY (tblService3id) REFERENCES tblService3(id)
);

-- 18. Bảng phiếu nhận xe sửa chữa
CREATE TABLE tblRepairOrder3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    createdDate DATE NOT NULL,
    completedDate DATE,
    status VARCHAR(50) NOT NULL,
    note VARCHAR(255),
    tblCar3id INT(10) NOT NULL,
    tblSalesStaff3id INT(10) NOT NULL,
    FOREIGN KEY (tblCar3id) REFERENCES tblCar3(id),
    FOREIGN KEY (tblSalesStaff3id) REFERENCES tblSalesStaff3(tblStaff3id)
);

-- 19. Bảng chi tiết dịch vụ sửa chữa
CREATE TABLE tblRepairServiceDetail3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    price FLOAT(10) NOT NULL,
    note VARCHAR(255),
    tblRepairOrder3id INT(10) NOT NULL,
    tblService3id INT(10) NOT NULL,
    tblTechnician3id INT(10) NOT NULL,
    FOREIGN KEY (tblRepairOrder3id) REFERENCES tblRepairOrder3(id),
    FOREIGN KEY (tblService3id) REFERENCES tblService3(id),
    FOREIGN KEY (tblTechnician3id) REFERENCES tblTechnician3(id)
);

-- 20. Bảng chi tiết phụ tùng sửa chữa
CREATE TABLE tblRepairSparePartDetail3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    quantity INT(10) NOT NULL,
    price FLOAT(10) NOT NULL,
    tblSparePart3id INT(10) NOT NULL,
    tblRepairOrder3id INT(10) NOT NULL,
    FOREIGN KEY (tblSparePart3id) REFERENCES tblSparePart3(id),
    FOREIGN KEY (tblRepairOrder3id) REFERENCES tblRepairOrder3(id)
);

-- 21. Bảng hóa đơn thanh toán
CREATE TABLE tblInvoice3 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    createdDate DATE NOT NULL,
    paymentMethod VARCHAR(50) NOT NULL,
    paymentStatus VARCHAR(50) NOT NULL,
    note VARCHAR(255),
    tblCustomer3id INT(10) NOT NULL,
    tblRepairOrder3id INT(10) NOT NULL,
    tblSalesStaff3id INT(10) NOT NULL,
    FOREIGN KEY (tblCustomer3id) REFERENCES tblCustomer3(id),
    FOREIGN KEY (tblRepairOrder3id) REFERENCES tblRepairOrder3(id),
    FOREIGN KEY (tblSalesStaff3id) REFERENCES tblSalesStaff3(tblStaff3id)
);

-- =========================================================================
-- DU LIEU MAU KHOI TAO HE THONG GARAMAN3
-- =========================================================================

-- 1. Địa chỉ (id 1..15)
INSERT INTO tblFullAddress3 (id, detail, district, province) VALUES
(1, 'Km10 Đường Nguyễn Trãi', 'Hà Đông', 'Hà Nội'),
(2, 'Số 10 Trần Phú', 'Hà Đông', 'Hà Nội'),
(3, 'Số 25 Lê Văn Lương', 'Thanh Xuân', 'Hà Nội'),
(4, 'Số 18 Giải Phóng', 'Hoàng Mai', 'Hà Nội'),
(5, 'Số 20 Nguyễn Trãi', 'Thanh Xuân', 'Hà Nội'),
(6, 'Số 15 Định Công', 'Hoàng Mai', 'Hà Nội'),
(7, 'Số 88 Cầu Giấy', 'Cầu Giấy', 'Hà Nội'),
(8, 'Số 42 Láng Hạ', 'Đống Đa', 'Hà Nội'),
(9, 'Số 12 Quang Trung', 'Hà Đông', 'Hà Nội'),
(10, 'Số 45 Lê Văn Lương', 'Thanh Xuân', 'Hà Nội'),
(11, 'Số 66 Nguyễn Văn Cừ', 'Long Biên', 'Hà Nội'),
(12, 'Số 102 Hoàng Quốc Việt', 'Cầu Giấy', 'Hà Nội'),
(13, 'Số 33 Phố Huế', 'Hai Bà Trưng', 'Hà Nội'),
(14, 'Lô 5 KCN Thăng Long', 'Đông Anh', 'Hà Nội'),
(15, 'Số 99 Phạm Hùng', 'Nam Từ Liêm', 'Hà Nội');

-- 2. Họ tên (id 1..12)
INSERT INTO tblFullName3 (id, lastName, middleName, firstName) VALUES
-- Nhân viên hệ thống (1..3)
(1, 'Nguyễn', 'Đình Quang', 'Huy'),
(2, 'Trần', 'Minh', 'Tuấn'),
(3, 'Vũ', 'Thị', 'Lan'),
-- Kỹ thuật viên (4..7)
(4, 'Lê', 'Văn', 'Cường'),
(5, 'Phạm', 'Văn', 'Dũng'),
(6, 'Hoàng', 'Minh', 'Đức'),
(7, 'Bùi', 'Quốc', 'Khánh'),
-- Khách hàng (8..12)
(8, 'Nguyễn', 'Văn', 'An'),
(9, 'Trần', 'Thị', 'Bích'),
(10, 'Lê', 'Hoàng', 'Minh'),
(11, 'Phạm', 'Thu', 'Hà'),
(12, 'Đỗ', 'Quốc', 'Bảo');

-- 3. Gara & Khu vực kho
INSERT INTO tblGara3 (id, name, phoneNumber, openTime, closeTime, note, tblFullAddress3id) VALUES
(1, 'Gara Ô tô GaraMan3', '02438889999', '07:30', '18:00', 'Trung tâm bảo dưỡng và sửa chữa ô tô GaraMan3', 1);

INSERT INTO tblStorageZone3 (id, name, tblGara3id) VALUES
(1, 'Khu A - Dầu nhớt & Vật tư tiêu hao', 1),
(2, 'Khu B - Phụ tùng Động cơ & Lọc', 1),
(3, 'Khu C - Phụ tùng Gầm, Phanh & Điện', 1);

-- 4. Nhân viên (Nguyễn Đình Quang Huy là Nhân viên bán hàng mặc định)
INSERT INTO tblStaff3 (id, username, password, birthDate, email, phoneNumber, salary, role, note, tblGara3id, tblFullAddress3id, tblFullName3id) VALUES
(1, 'nvbanhang', '123456', '2003-05-15', 'huy.ndq@garaman.vn', '0988111222', 12000000, 'SalesStaff', 'Nhân viên bán hàng - Tiếp nhận xe', 1, 2, 1),
(2, 'quanly', '123456', '1992-08-20', 'quanly@garaman.vn', '0988333444', 20000000, 'Manager', 'Quản lý gara', 1, 3, 2),
(3, 'nvkho', '123456', '1998-03-10', 'nvkho@garaman.vn', '0988555666', 11000000, 'WarehouseStaff', 'Nhân viên thủ kho', 1, 4, 3);

INSERT INTO tblSalesStaff3 (tblStaff3id) VALUES (1);
INSERT INTO tblWarehouseStaff3 (tblStaff3id) VALUES (3);

-- 5. Kỹ thuật viên (4 người)
INSERT INTO tblTechnician3 (id, phoneNumber, specialization, salary, status, tblGara3id, tblFullAddress3id, tblFullName3id) VALUES
(1, '0977111222', 'Máy - Gầm', 15000000, 'Rảnh', 1, 5, 4),
(2, '0977333444', 'Điện - Điều hòa ô tô', 14500000, 'Rảnh', 1, 6, 5),
(3, '0977555666', 'Đồng - Sơn', 14000000, 'Đang sửa', 1, 7, 6),
(4, '0977777888', 'Bảo dưỡng nhanh & Lốp', 13500000, 'Rảnh', 1, 8, 7);

-- 6. Nhà cung cấp (2 đơn vị)
INSERT INTO tblSupplier3 (id, name, phoneNumber, email, taxCode, note, tblFullAddress3id) VALUES
(1, 'Công ty Phụ tùng Ô tô Thành Công', '02437771111', 'sales@thanhcongparts.vn', '0101234567', 'Cung cấp dầu nhớt, lọc, bugi chính hãng', 14),
(2, 'Nhà phân phối Phụ tùng Bosch Việt Nam', '02437772222', 'contact@boschparts.vn', '0107654321', 'Cung cấp má phanh, ắc quy, gạt mưa', 15);

-- 7. Danh sách Dịch vụ (3 nhóm dùng chung khi đặt lịch và nhận xe)
INSERT INTO tblService3 (id, name, price, duration, description) VALUES
(1, 'Bảo dưỡng', 500000, 60, 'Nhóm dịch vụ bảo dưỡng định kỳ (dầu máy, gạt mưa, đai dẫn động, lọc dầu, lọc gió, bugi, dầu phanh, dầu hộp số, nước làm mát)'),
(2, 'Sửa chữa chung', 700000, 90, 'Nhóm dịch vụ sửa chữa chung (hệ thống phanh, lái, điện/điều hòa, làm mát, treo/gầm, nội thất, chẩn đoán lỗi)'),
(3, 'Đồng sơn', 1500000, 180, 'Nhóm dịch vụ đồng sơn (thay thế chi tiết thân vỏ, đèn/gương/kính và sơn sửa phục hồi)');

-- 8. Danh sách Phụ tùng gara có sẵn (12 loại phụ tùng)
INSERT INTO tblSparePart3 (id, name, quantity, unit, sellingPrice, description, tblStorageZone3id) VALUES
(1, 'Dầu máy tổng hợp Castrol EDGE 5W-30 (4L)', 28, 'Can', 580000, 'Dầu nhớt động cơ tổng hợp toàn phần cao cấp', 1),
(2, 'Nước làm mát động cơ Prestone (3.78L)', 22, 'Bình', 220000, 'Dung dịch làm mát chống sôi và chống đóng cặn', 1),
(3, 'Dầu phanh DOT 4 Bosch (1L)', 18, 'Chai', 180000, 'Dầu thủy lực hệ thống phanh tiêu chuẩn DOT 4', 1),
(4, 'Lọc dầu động cơ Toyota/Honda chính hãng', 35, 'Cái', 160000, 'Lọc cặn dầu bôi trơn động cơ', 2),
(5, 'Lọc gió động cơ Denso', 24, 'Cái', 240000, 'Lọc bụi bẩn khí nạp vào buồng đốt', 2),
(6, 'Lọc gió điều hòa than hoạt tính', 26, 'Cái', 280000, 'Khử mùi và lọc bụi mịn PM2.5 cho khoang lái', 2),
(7, 'Bugi Iridium Denso IK20', 40, 'Cái', 260000, 'Bugi đánh lửa đầu Iridium tuổi thọ cao', 2),
(8, 'Dây curoa tổng Gates 6PK', 15, 'Sợi', 420000, 'Dây đai truyền động máy phát và lốc điều hòa', 2),
(9, 'Má phanh trước gốm Bosch Ceramic', 14, 'Bộ', 890000, 'Má phanh chống ồn, ít bụi phanh cho cầu trước', 3),
(10, 'Ắc quy khô GS 12V-60Ah', 10, 'Bình', 1650000, 'Bình ắc quy khởi động miễn bảo dưỡng', 3),
(11, 'Giảm xóc trước KYB Nhật Bản', 12, 'Cái', 1450000, 'Phuộc nhún giảm chấn cầu trước', 3),
(12, 'Gas lạnh điều hòa R134a Dupont', 30, 'Lon', 190000, 'Gas nạp hệ thống điều hòa ô tô tiêu chuẩn', 3);

-- 9. 5 Khách hàng đã có trong hệ thống
INSERT INTO tblCustomer3 (id, phoneNumber, email, note, tblFullAddress3id, tblFullName3id) VALUES
(1, '0912345678', 'nguyenvanan@gmail.com', 'Khách hàng thân thiết', 9, 8),
(2, '0987654321', 'tranthibich@gmail.com', 'Hay bảo dưỡng cuối tuần', 10, 9),
(3, '0904112233', 'lehoangminh@gmail.com', 'Khách doanh nghiệp', 11, 10),
(4, '0936554433', 'phamthuha@gmail.com', '', 12, 11),
(5, '0978998877', 'doquocbao@gmail.com', 'Thường đặt lịch buổi sáng', 13, 12);

-- 10. Mỗi khách có 1-2 xe ô tô (Tổng 8 xe)
INSERT INTO tblCar3 (id, plateNumber, brand, model, year, description, tblCustomer3id) VALUES
(1, '30A-123.45', 'Toyota', 'Vios', 2021, 'Sedan màu bạc, đi được 25.000 km', 1),
(2, '30G-678.90', 'Honda', 'City', 2022, 'Sedan màu trắng, bản RS', 1),
(3, '29A-888.99', 'Mazda', 'CX-5', 2023, 'SUV màu đỏ pha lê', 2),
(4, '30H-246.80', 'Hyundai', 'Accent', 2020, 'Sedan màu đen', 3),
(5, '29C-567.89', 'Ford', 'Ranger', 2022, 'Bán tải màu xanh, chạy công trình', 3),
(6, '30K-112.23', 'Kia', 'Seltos', 2023, 'SUV cỡ nhỏ màu vàng cam', 4),
(7, '30E-777.88', 'VinFast', 'VF8', 2024, 'SUV điện màu xám', 5),
(8, '29D-334.45', 'Mitsubishi', 'Xpander', 2021, 'MPV 7 chỗ màu trắng', 5);

-- 11. 5 Đơn đặt lịch hẹn trước (ở trạng thái Chờ tiếp nhận để test Module 2)
INSERT INTO tblAppointment3 (id, appointmentDate, appointmentTime, status, note, tblCustomer3id, tblCar3id) VALUES
(1, CURDATE(), '08:00', 'Chờ tiếp nhận', 'Chi tiết: Bảo dưỡng (Dầu máy, Lọc dầu, Lọc gió động cơ)', 1, 1),
(2, CURDATE(), '09:00', 'Chờ tiếp nhận', 'Chi tiết: Bảo dưỡng (Lọc gió điều hòa); Sửa chữa chung (Hệ thống điện, điều hòa: Giàn lạnh / Nạp gas điều hòa)', 2, 3),
(3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:00', 'Chờ tiếp nhận', 'Chi tiết: Sửa chữa chung (Hệ thống phanh: Má phanh trước/sau; Hệ thống lái: Thước lái, Trợ lực lái)', 3, 5),
(4, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '14:00', 'Chờ tiếp nhận', 'Chi tiết: Bảo dưỡng (Dầu máy, Bugi, Nước làm mát); Đồng sơn (Sơn sửa phục hồi: Cản trước / cản sau)', 4, 6),
(5, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '15:00', 'Chờ tiếp nhận', 'Chi tiết: Sửa chữa chung (Chẩn đoán đèn báo lỗi và cập nhật phần mềm); Đồng sơn (Thay thế chi tiết thân vỏ: Gương chiếu hậu)', 5, 8);

-- Chi tiết dịch vụ cho 5 đơn đặt trước (gắn với 3 nhóm dịch vụ đặt lịch)
INSERT INTO tblAppointmentServiceDetail3 (tblAppointment3id, tblService3id) VALUES
(1, 1),
(2, 1), (2, 2),
(3, 2),
(4, 1), (4, 3),
(5, 2), (5, 3);

-- BEGIN GARAMAN3 EXTENDED DEMO DATA
-- Dữ liệu minh họa, có thể chạy riêng đoạn này trên CSDL GaraMan3 đang dùng.
-- Không ghi đè hồ sơ, tồn kho hoặc lịch hẹn đã tồn tại.
START TRANSACTION;

CREATE TEMPORARY TABLE demoCustomer3 (
    seq INT PRIMARY KEY, lastName VARCHAR(255), middleName VARCHAR(255), firstName VARCHAR(255),
    detail VARCHAR(255), district VARCHAR(255), phoneNumber VARCHAR(50), email VARCHAR(255),
    plateNumber VARCHAR(50), brand VARCHAR(50), model VARCHAR(50), year INT
);
INSERT INTO demoCustomer3 VALUES
(1, 'Nguyễn', 'Minh', 'Anh', '141 Trần Thái Tông', 'Cầu Giấy', '0907500001', 'garaman3-demo-01@example.test', '30Q-73.01', 'Toyota', 'Vios', 2019),
(2, 'Trần', 'Thị', 'Bình', '142 Tố Hữu', 'Hà Đông', '0907500002', 'garaman3-demo-02@example.test', '30Q-73.02', 'Honda', 'City', 2020),
(3, 'Lê', 'Quang', 'Cường', '143 Nguyễn Văn Cừ', 'Long Biên', '0907500003', 'garaman3-demo-03@example.test', '30Q-73.03', 'Mazda', 'CX-5', 2021),
(4, 'Phạm', 'Thu', 'Dung', '144 Hàm Nghi', 'Nam Từ Liêm', '0907500004', 'garaman3-demo-04@example.test', '30Q-73.04', 'Hyundai', 'Accent', 2022),
(5, 'Hoàng', 'Đức', 'Dũng', '145 Lạc Long Quân', 'Tây Hồ', '0907500005', 'garaman3-demo-05@example.test', '30Q-73.05', 'Ford', 'Ranger', 2023),
(6, 'Vũ', 'Ngọc', 'Hà', '146 Trần Thái Tông', 'Cầu Giấy', '0907500006', 'garaman3-demo-06@example.test', '30Q-73.06', 'Kia', 'Seltos', 2024),
(7, 'Đặng', 'Khánh', 'Hân', '147 Tố Hữu', 'Hà Đông', '0907500007', 'garaman3-demo-07@example.test', '30Q-73.07', 'VinFast', 'VF 5', 2018),
(8, 'Bùi', 'Văn', 'Hiếu', '148 Nguyễn Văn Cừ', 'Long Biên', '0907500008', 'garaman3-demo-08@example.test', '30Q-73.08', 'Mitsubishi', 'Xpander', 2019),
(9, 'Đỗ', 'Thị', 'Hoa', '149 Hàm Nghi', 'Nam Từ Liêm', '0907500009', 'garaman3-demo-09@example.test', '30Q-73.09', 'Suzuki', 'XL7', 2020),
(10, 'Hồ', 'Quang', 'Huy', '150 Lạc Long Quân', 'Tây Hồ', '0907500010', 'garaman3-demo-10@example.test', '30Q-73.10', 'Nissan', 'Almera', 2021),
(11, 'Ngô', 'Minh', 'Khang', '151 Trần Thái Tông', 'Cầu Giấy', '0907500011', 'garaman3-demo-11@example.test', '30Q-73.11', 'Toyota', 'Vios', 2022),
(12, 'Dương', 'Thùy', 'Linh', '152 Tố Hữu', 'Hà Đông', '0907500012', 'garaman3-demo-12@example.test', '30Q-73.12', 'Honda', 'City', 2023),
(13, 'Lý', 'Gia', 'Long', '153 Nguyễn Văn Cừ', 'Long Biên', '0907500013', 'garaman3-demo-13@example.test', '30Q-73.13', 'Mazda', 'CX-5', 2024),
(14, 'Võ', 'Hoàng', 'Mai', '154 Hàm Nghi', 'Nam Từ Liêm', '0907500014', 'garaman3-demo-14@example.test', '30Q-73.14', 'Hyundai', 'Accent', 2018),
(15, 'Phan', 'Tuấn', 'Minh', '155 Lạc Long Quân', 'Tây Hồ', '0907500015', 'garaman3-demo-15@example.test', '30Q-73.15', 'Ford', 'Ranger', 2019),
(16, 'Trương', 'Hải', 'Nam', '156 Trần Thái Tông', 'Cầu Giấy', '0907500016', 'garaman3-demo-16@example.test', '30Q-73.16', 'Kia', 'Seltos', 2020),
(17, 'Đinh', 'Thị', 'Nga', '157 Tố Hữu', 'Hà Đông', '0907500017', 'garaman3-demo-17@example.test', '30Q-73.17', 'VinFast', 'VF 5', 2021),
(18, 'Cao', 'Anh', 'Ngọc', '158 Nguyễn Văn Cừ', 'Long Biên', '0907500018', 'garaman3-demo-18@example.test', '30Q-73.18', 'Mitsubishi', 'Xpander', 2022),
(19, 'Mai', 'Quốc', 'Phong', '159 Hàm Nghi', 'Nam Từ Liêm', '0907500019', 'garaman3-demo-19@example.test', '30Q-73.19', 'Suzuki', 'XL7', 2023),
(20, 'Lương', 'Minh', 'Phúc', '160 Lạc Long Quân', 'Tây Hồ', '0907500020', 'garaman3-demo-20@example.test', '30Q-73.20', 'Nissan', 'Almera', 2024),
(21, 'Tạ', 'Thu', 'Quỳnh', '161 Trần Thái Tông', 'Cầu Giấy', '0907500021', 'garaman3-demo-21@example.test', '30Q-73.21', 'Toyota', 'Vios', 2018),
(22, 'Tô', 'Đức', 'Sơn', '162 Tố Hữu', 'Hà Đông', '0907500022', 'garaman3-demo-22@example.test', '30Q-73.22', 'Honda', 'City', 2019),
(23, 'Châu', 'Ngọc', 'Thảo', '163 Nguyễn Văn Cừ', 'Long Biên', '0907500023', 'garaman3-demo-23@example.test', '30Q-73.23', 'Mazda', 'CX-5', 2020),
(24, 'Hà', 'Minh', 'Thư', '164 Hàm Nghi', 'Nam Từ Liêm', '0907500024', 'garaman3-demo-24@example.test', '30Q-73.24', 'Hyundai', 'Accent', 2021),
(25, 'Thái', 'Quang', 'Tiến', '165 Lạc Long Quân', 'Tây Hồ', '0907500025', 'garaman3-demo-25@example.test', '30Q-73.25', 'Ford', 'Ranger', 2022),
(26, 'Huỳnh', 'Thanh', 'Trúc', '166 Trần Thái Tông', 'Cầu Giấy', '0907500026', 'garaman3-demo-26@example.test', '30Q-73.26', 'Kia', 'Seltos', 2023),
(27, 'Chu', 'Văn', 'Tú', '167 Tố Hữu', 'Hà Đông', '0907500027', 'garaman3-demo-27@example.test', '30Q-73.27', 'VinFast', 'VF 5', 2024),
(28, 'Lưu', 'Quang', 'Tùng', '168 Nguyễn Văn Cừ', 'Long Biên', '0907500028', 'garaman3-demo-28@example.test', '30Q-73.28', 'Mitsubishi', 'Xpander', 2018),
(29, 'Ninh', 'Hải', 'Vân', '169 Hàm Nghi', 'Nam Từ Liêm', '0907500029', 'garaman3-demo-29@example.test', '30Q-73.29', 'Suzuki', 'XL7', 2019),
(30, 'Kiều', 'Gia', 'Việt', '170 Lạc Long Quân', 'Tây Hồ', '0907500030', 'garaman3-demo-30@example.test', '30Q-73.30', 'Nissan', 'Almera', 2020);

INSERT INTO tblFullName3 (lastName, middleName, firstName)
SELECT d.lastName, d.middleName, d.firstName FROM demoCustomer3 d
WHERE NOT EXISTS (
    SELECT 1 FROM tblFullName3 n
    WHERE n.lastName = d.lastName AND n.middleName = d.middleName AND n.firstName = d.firstName
)
AND NOT EXISTS (
    SELECT 1 FROM tblCustomer3 c WHERE c.email = d.email OR c.phoneNumber = d.phoneNumber
        OR c.note = CONCAT('[GM3 DEMO] Khách ', d.seq)
);
INSERT INTO tblFullAddress3 (detail, district, province)
SELECT d.detail, d.district, 'Hà Nội' FROM demoCustomer3 d
WHERE NOT EXISTS (
    SELECT 1 FROM tblFullAddress3 a
    WHERE a.detail = d.detail AND a.district = d.district AND a.province = 'Hà Nội'
)
AND NOT EXISTS (
    SELECT 1 FROM tblCustomer3 c WHERE c.email = d.email OR c.phoneNumber = d.phoneNumber
        OR c.note = CONCAT('[GM3 DEMO] Khách ', d.seq)
);
INSERT INTO tblCustomer3 (phoneNumber, email, note, tblFullAddress3id, tblFullName3id)
SELECT d.phoneNumber, d.email, CONCAT('[GM3 DEMO] Khách ', d.seq),
       (SELECT MIN(a.id) FROM tblFullAddress3 a WHERE a.detail = d.detail AND a.district = d.district AND a.province = 'Hà Nội'),
       (SELECT MIN(n.id) FROM tblFullName3 n WHERE n.lastName = d.lastName AND n.middleName = d.middleName AND n.firstName = d.firstName)
FROM demoCustomer3 d
WHERE NOT EXISTS (
    SELECT 1 FROM tblCustomer3 c WHERE c.email = d.email OR c.phoneNumber = d.phoneNumber
        OR c.note = CONCAT('[GM3 DEMO] Khách ', d.seq)
);
INSERT INTO tblCar3 (plateNumber, brand, model, year, description, tblCustomer3id)
SELECT d.plateNumber, d.brand, d.model, d.year, 'Xe mẫu GaraMan3', c.id
FROM demoCustomer3 d JOIN tblCustomer3 c ON c.note = CONCAT('[GM3 DEMO] Khách ', d.seq)
WHERE NOT EXISTS (SELECT 1 FROM tblCar3 car WHERE car.plateNumber = d.plateNumber);

-- Chọn một ngày chưa có lịch trong khoảng từ 2 đến 31 ngày tới.
-- Ba lịch ở từng khung giờ từ 08:00 đến 17:00 khiến lịch hiển thị X.
SET @gm3DemoDate = COALESCE(
    (SELECT MIN(appointmentDate) FROM tblAppointment3 WHERE note LIKE '[GM3 DEMO] Ngày kín %'),
    (SELECT MIN(DATE_ADD(CURRENT_DATE, INTERVAL (d.seq + 1) DAY))
     FROM demoCustomer3 d
     WHERE NOT EXISTS (
         SELECT 1 FROM tblAppointment3 a WHERE a.appointmentDate = DATE_ADD(CURRENT_DATE, INTERVAL (d.seq + 1) DAY)
             AND a.status <> 'Đã hủy'
     ))
);
INSERT INTO tblAppointment3 (appointmentDate, appointmentTime, status, note, tblCustomer3id, tblCar3id)
SELECT @gm3DemoDate, CONCAT(LPAD(8 + FLOOR((d.seq - 1) / 3), 2, '0'), ':00'),
       'Chờ tiếp nhận', CONCAT('[GM3 DEMO] Ngày kín ', d.seq), c.id, car.id
FROM demoCustomer3 d
JOIN tblCustomer3 c ON c.note = CONCAT('[GM3 DEMO] Khách ', d.seq)
JOIN tblCar3 car ON car.plateNumber = d.plateNumber AND car.tblCustomer3id = c.id
WHERE @gm3DemoDate IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM tblAppointment3 a WHERE a.note = CONCAT('[GM3 DEMO] Ngày kín ', d.seq));

-- Chọn một ngày khác có khung 11:00 trống để minh họa chỉ một giờ bị khóa.
-- Các khung giờ còn lại của ngày vẫn chọn được.
SET @gm3PartialDate = (
    SELECT MIN(DATE_ADD(@gm3DemoDate, INTERVAL d.seq DAY))
    FROM demoCustomer3 d
    WHERE NOT EXISTS (
        SELECT 1 FROM tblAppointment3 a
        WHERE a.appointmentDate = DATE_ADD(@gm3DemoDate, INTERVAL d.seq DAY)
            AND a.appointmentTime = '11:00' AND a.status <> 'Đã hủy'
            AND (a.note IS NULL OR a.note NOT IN (
                '[GM3 DEMO] Ngày còn chỗ 1', '[GM3 DEMO] Ngày còn chỗ 2', '[GM3 DEMO] Ngày còn chỗ 3'))
    )
);
INSERT INTO tblAppointment3 (appointmentDate, appointmentTime, status, note, tblCustomer3id, tblCar3id)
SELECT @gm3PartialDate, '11:00',
       'Chờ tiếp nhận', CONCAT('[GM3 DEMO] Ngày còn chỗ ', d.seq), c.id, car.id
FROM demoCustomer3 d
JOIN tblCustomer3 c ON c.note = CONCAT('[GM3 DEMO] Khách ', d.seq)
JOIN tblCar3 car ON car.plateNumber = d.plateNumber AND car.tblCustomer3id = c.id
WHERE d.seq BETWEEN 1 AND 3 AND @gm3PartialDate IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM tblAppointment3 a WHERE a.note = CONCAT('[GM3 DEMO] Ngày còn chỗ ', d.seq));

-- Đồng bộ ba lịch mẫu từ lần nạp trước. Giữ lịch đã xử lý và dữ liệu khách ngoài bộ mẫu.
UPDATE tblAppointment3 a
JOIN demoCustomer3 d ON d.seq BETWEEN 1 AND 3 AND a.note = CONCAT('[GM3 DEMO] Ngày còn chỗ ', d.seq)
JOIN tblCar3 car ON car.id = a.tblCar3id AND car.plateNumber = d.plateNumber
SET a.appointmentDate = @gm3PartialDate, a.appointmentTime = '11:00'
WHERE a.status = 'Chờ tiếp nhận' AND @gm3PartialDate IS NOT NULL
  AND (a.appointmentDate <> @gm3PartialDate OR a.appointmentTime <> '11:00');

INSERT INTO tblAppointmentServiceDetail3 (tblAppointment3id, tblService3id)
SELECT a.id, 1 + MOD(d.seq - 1, 3)
FROM tblAppointment3 a JOIN demoCustomer3 d
ON a.note IN (CONCAT('[GM3 DEMO] Ngày kín ', d.seq), CONCAT('[GM3 DEMO] Ngày còn chỗ ', d.seq))
WHERE NOT EXISTS (SELECT 1 FROM tblAppointmentServiceDetail3 x WHERE x.tblAppointment3id = a.id);

CREATE TEMPORARY TABLE demoPart3 (
    name VARCHAR(255) PRIMARY KEY, quantity INT, unit VARCHAR(50),
    sellingPrice FLOAT, description VARCHAR(255), zoneId INT
);
INSERT INTO demoPart3 VALUES
('Ốc vít tự ren M4 × 16 mm', 160, 'Gói', 18000, 'Bắt ốp nhựa và nội thất', 1),
('Ốc vít tự ren M5 × 20 mm', 130, 'Gói', 22000, 'Bắt chắn bùn và ốp cản', 2),
('Bu lông lục giác M6 × 25 mm', 80, 'Cái', 8000, 'Thép mạ kẽm', 3),
('Bu lông lục giác M8 × 35 mm', 75, 'Cái', 12000, 'Thép cấp bền 8.8', 1),
('Đai ốc tự hãm M8', 90, 'Cái', 6500, 'Đai ốc có vòng nylon', 2),
('Long đen phẳng M8', 140, 'Cái', 3000, 'Vòng đệm cho bu lông M8', 3),
('Kẹp nhựa giữ tấm ốp 8 mm', 100, 'Gói', 35000, 'Chốt gài ốp cửa và cản xe', 1),
('Dây rút nhựa chịu nhiệt 200 mm', 50, 'Gói', 27000, 'Cố định bó dây điện', 2),
('Lốp ô tô 175/65 R14', 12, 'Cái', 980000, 'Lốp cho xe đô thị cỡ nhỏ', 3),
('Lốp ô tô 185/65 R15', 14, 'Cái', 1190000, 'Lốp phổ thông cho sedan', 1),
('Lốp ô tô 195/55 R16', 10, 'Cái', 1490000, 'Lốp cho sedan và hatchback', 2),
('Lốp ô tô 205/55 R16', 16, 'Cái', 1590000, 'Lốp đi phố và đường trường', 3),
('Lốp ô tô 215/60 R17', 9, 'Cái', 1940000, 'Lốp cho crossover', 1),
('Lốp ô tô 225/60 R18', 8, 'Cái', 2550000, 'Lốp cho SUV', 2),
('Van lốp cao su TR413', 40, 'Cái', 25000, 'Van cho mâm lốp không săm', 3),
('Bộ vá lốp không săm', 18, 'Bộ', 125000, 'Dụng cụ và dây vá lốp', 1),
('Cầu chì lá ô tô 10 A', 40, 'Gói', 32000, 'Cầu chì lá cỡ tiêu chuẩn', 2),
('Cầu chì lá ô tô 15 A', 35, 'Gói', 32000, 'Cầu chì lá cỡ tiêu chuẩn', 3),
('Bóng đèn pha H7 12 V', 25, 'Cái', 170000, 'Bóng halogen cho đèn pha', 1),
('Bóng đèn hậu P21/5W', 30, 'Cái', 45000, 'Bóng hai tim cho đèn hậu', 2),
('Thảm sàn cao su ô tô', 9, 'Bộ', 480000, 'Bộ thảm sàn dễ vệ sinh', 3),
('Nắp chụp van lốp', 25, 'Bộ', 28000, 'Bộ bốn nắp chụp van', 1),
('Chổi gạt mưa 16 inch', 20, 'Cái', 125000, 'Lưỡi cao su cho kính trước', 2),
('Dung dịch rửa kính 2 lít', 25, 'Chai', 65000, 'Dung dịch làm sạch kính ô tô', 3);
INSERT INTO tblSparePart3 (name, quantity, unit, sellingPrice, description, tblStorageZone3id)
SELECT d.name, d.quantity, d.unit, d.sellingPrice, d.description, d.zoneId FROM demoPart3 d
WHERE NOT EXISTS (SELECT 1 FROM tblSparePart3 p WHERE p.name = d.name);

DROP TEMPORARY TABLE demoPart3;
DROP TEMPORARY TABLE demoCustomer3;
COMMIT;
-- END GARAMAN3 EXTENDED DEMO DATA
