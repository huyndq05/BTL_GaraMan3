package dao;

import model.*;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DAO3 {
    public static Connection con;
    private static boolean dbAttempted = false;
    private static boolean initialized = false;

    public static final List<SalesStaff3> STAFF_LIST = new ArrayList<>();
    public static final List<Technician3> TECHNICIAN_LIST = new ArrayList<>();
    public static final List<Customer3> CUSTOMER_LIST = new ArrayList<>();
    public static final List<Car3> CAR_LIST = new ArrayList<>();
    public static final List<Service3> SERVICE_LIST = new ArrayList<>();
    public static final List<SparePart3> SPARE_PART_LIST = new ArrayList<>();
    public static final List<Appointment3> APPOINTMENT_LIST = new ArrayList<>();
    public static final List<RepairOrder3> REPAIR_ORDER_LIST = new ArrayList<>();

    public DAO3() {
        initSampleData();
        connectDatabase();
    }

    public static synchronized void connectDatabase() {
        try {
            if (con != null && !con.isClosed()) return;
        } catch (Exception ignored) {}
        if (dbAttempted && con == null) return;
        dbAttempted = true;

        try {
            con = openDatabaseConnection();
        } catch (Throwable e) {
            con = null;
        }
    }

    protected static Connection openDatabaseConnection() throws ClassNotFoundException, SQLException {
        String dbUrl = "jdbc:mysql://localhost:3306/garaman3?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh&connectTimeout=500&socketTimeout=3000";
        String dbUser = "root";
        String dbPass = "";

        try (InputStream is = DAO3.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                if (props.getProperty("db.url") != null && !props.getProperty("db.url").trim().isEmpty()) {
                    dbUrl = props.getProperty("db.url").trim();
                }
                if (props.getProperty("db.username") != null) {
                    dbUser = props.getProperty("db.username").trim();
                }
                if (props.getProperty("db.password") != null) {
                    dbPass = props.getProperty("db.password").trim();
                }
            }
        } catch (Exception ignored) {}

        Class.forName("com.mysql.cj.jdbc.Driver");
        DriverManager.setLoginTimeout(1);
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }

    public static boolean isUsingDatabase() {
        try {
            return con != null && !con.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public static synchronized void initSampleData() {
        if (initialized) return;
        initialized = true;

        // 1. Nhân viên bán hàng mặc định: Nguyễn Đình Quang Huy
        SalesStaff3 nv1 = new SalesStaff3(
            1, "nvbanhang", "123456", new Date(),
            "huy.ndq@garaman.vn", "0988111222", 12000000, "Nhân viên bán hàng - Tiếp nhận xe",
            new FullName3(1, "Nguyễn", "Đình Quang", "Huy"),
            new FullAddress3(2, "Số 10 Trần Phú", "Hà Đông", "Hà Nội")
        );
        STAFF_LIST.add(nv1);

        // 2. Kỹ thuật viên (4 người)
        TECHNICIAN_LIST.add(new Technician3(
            1, new FullName3(4, "Lê", "Văn", "Cường"), "0977111222", "Máy - Gầm", "Rảnh", 15000000,
            new FullAddress3(5, "Số 20 Nguyễn Trãi", "Thanh Xuân", "Hà Nội")
        ));
        TECHNICIAN_LIST.add(new Technician3(
            2, new FullName3(5, "Phạm", "Văn", "Dũng"), "0977333444", "Điện - Điều hòa ô tô", "Rảnh", 14500000,
            new FullAddress3(6, "Số 15 Định Công", "Hoàng Mai", "Hà Nội")
        ));
        TECHNICIAN_LIST.add(new Technician3(
            3, new FullName3(6, "Hoàng", "Minh", "Đức"), "0977555666", "Đồng - Sơn", "Đang sửa", 14000000,
            new FullAddress3(7, "Số 88 Cầu Giấy", "Cầu Giấy", "Hà Nội")
        ));
        TECHNICIAN_LIST.add(new Technician3(
            4, new FullName3(7, "Bùi", "Quốc", "Khánh"), "0977777888", "Bảo dưỡng nhanh & Lốp", "Rảnh", 13500000,
            new FullAddress3(8, "Số 42 Láng Hạ", "Đống Đa", "Hà Nội")
        ));

        // 3. Danh sách 10 dịch vụ (3 nhóm chính cho đặt lịch + 7 dịch vụ chi tiết tại xưởng)
        SERVICE_LIST.add(new Service3(1, "Bảo dưỡng", 500000, 60, "Nhóm dịch vụ bảo dưỡng định kỳ (dầu máy, gạt mưa, đai dẫn động, lọc dầu, lọc gió, bugi, dầu phanh, dầu hộp số, nước làm mát)"));
        SERVICE_LIST.add(new Service3(2, "Sửa chữa chung", 700000, 90, "Nhóm dịch vụ sửa chữa chung (hệ thống phanh, lái, điện/điều hòa, làm mát, treo/gầm, nội thất, chẩn đoán lỗi)"));
        SERVICE_LIST.add(new Service3(3, "Đồng sơn", 1500000, 180, "Nhóm dịch vụ đồng sơn (thay thế chi tiết thân vỏ, đèn/gương/kính và sơn sửa phục hồi)"));

        // 4. Danh sách 12 phụ tùng có sẵn
        SPARE_PART_LIST.add(new SparePart3(1, "Dầu máy tổng hợp Castrol EDGE 5W-30 (4L)", "Can", 580000, 28, "Dầu nhớt động cơ tổng hợp toàn phần cao cấp"));
        SPARE_PART_LIST.add(new SparePart3(2, "Nước làm mát động cơ Prestone (3.78L)", "Bình", 220000, 22, "Dung dịch làm mát chống sôi và chống đóng cặn"));
        SPARE_PART_LIST.add(new SparePart3(3, "Dầu phanh DOT 4 Bosch (1L)", "Chai", 180000, 18, "Dầu thủy lực hệ thống phanh tiêu chuẩn DOT 4"));
        SPARE_PART_LIST.add(new SparePart3(4, "Lọc dầu động cơ Toyota/Honda chính hãng", "Cái", 160000, 35, "Lọc cặn dầu bôi trơn động cơ"));
        SPARE_PART_LIST.add(new SparePart3(5, "Lọc gió động cơ Denso", "Cái", 240000, 24, "Lọc bụi bẩn khí nạp vào buồng đốt"));
        SPARE_PART_LIST.add(new SparePart3(6, "Lọc gió điều hòa than hoạt tính", "Cái", 280000, 26, "Khử mùi và lọc bụi mịn PM2.5 cho khoang lái"));
        SPARE_PART_LIST.add(new SparePart3(7, "Bugi Iridium Denso IK20", "Cái", 260000, 40, "Bugi đánh lửa đầu Iridium tuổi thọ cao"));
        SPARE_PART_LIST.add(new SparePart3(8, "Dây curoa tổng Gates 6PK", "Sợi", 420000, 15, "Dây đai truyền động máy phát và lốc điều hòa"));
        SPARE_PART_LIST.add(new SparePart3(9, "Má phanh trước gốm Bosch Ceramic", "Bộ", 890000, 14, "Má phanh chống ồn, ít bụi phanh cho cầu trước"));
        SPARE_PART_LIST.add(new SparePart3(10, "Ắc quy khô GS 12V-60Ah", "Bình", 1650000, 10, "Bình ắc quy khởi động miễn bảo dưỡng"));
        SPARE_PART_LIST.add(new SparePart3(11, "Giảm xóc trước KYB Nhật Bản", "Cái", 1450000, 12, "Phuộc nhún giảm chấn cầu trước"));
        SPARE_PART_LIST.add(new SparePart3(12, "Gas lạnh điều hòa R134a Dupont", "Lon", 190000, 30, "Gas nạp hệ thống điều hòa ô tô tiêu chuẩn"));

        // 5. 5 Khách hàng & 8 Xe ô tô (mỗi khách 1-2 xe)
        Customer3 kh1 = new Customer3(1, new FullName3(8, "Nguyễn", "Văn", "An"), "0912345678", "nguyenvanan@gmail.com", new FullAddress3(9, "Số 12 Quang Trung", "Hà Đông", "Hà Nội"), "Khách hàng thân thiết");
        Customer3 kh2 = new Customer3(2, new FullName3(9, "Trần", "Thị", "Bích"), "0987654321", "tranthibich@gmail.com", new FullAddress3(10, "Số 45 Lê Văn Lương", "Thanh Xuân", "Hà Nội"), "Hay bảo dưỡng cuối tuần");
        Customer3 kh3 = new Customer3(3, new FullName3(10, "Lê", "Hoàng", "Minh"), "0904112233", "lehoangminh@gmail.com", new FullAddress3(11, "Số 66 Nguyễn Văn Cừ", "Long Biên", "Hà Nội"), "Khách doanh nghiệp");
        Customer3 kh4 = new Customer3(4, new FullName3(11, "Phạm", "Thu", "Hà"), "0936554433", "phamthuha@gmail.com", new FullAddress3(12, "Số 102 Hoàng Quốc Việt", "Cầu Giấy", "Hà Nội"), "");
        Customer3 kh5 = new Customer3(5, new FullName3(12, "Đỗ", "Quốc", "Bảo"), "0978998877", "doquocbao@gmail.com", new FullAddress3(13, "Số 33 Phố Huế", "Hai Bà Trưng", "Hà Nội"), "Thường đặt lịch buổi sáng");
        Collections.addAll(CUSTOMER_LIST, kh1, kh2, kh3, kh4, kh5);

        Car3 c1 = new Car3(1, "30A-123.45", "Toyota", "Vios", 2021, "Sedan màu bạc", 1);
        Car3 c2 = new Car3(2, "30G-678.90", "Honda", "City", 2022, "Sedan màu trắng", 1);
        Car3 c3 = new Car3(3, "29A-888.99", "Mazda", "CX-5", 2023, "SUV màu đỏ pha lê", 2);
        Car3 c4 = new Car3(4, "30H-246.80", "Hyundai", "Accent", 2020, "Sedan màu đen", 3);
        Car3 c5 = new Car3(5, "29C-567.89", "Ford", "Ranger", 2022, "Bán tải màu xanh", 3);
        Car3 c6 = new Car3(6, "30K-112.23", "Kia", "Seltos", 2023, "SUV màu vàng cam", 4);
        Car3 c7 = new Car3(7, "30E-777.88", "VinFast", "VF8", 2024, "SUV điện màu xám", 5);
        Car3 c8 = new Car3(8, "29D-334.45", "Mitsubishi", "Xpander", 2021, "MPV 7 chỗ màu trắng", 5);
        Collections.addAll(CAR_LIST, c1, c2, c3, c4, c5, c6, c7, c8);

        kh1.setCars(new Car3[]{c1, c2});
        kh2.setCars(new Car3[]{c3});
        kh3.setCars(new Car3[]{c4, c5});
        kh4.setCars(new Car3[]{c6});
        kh5.setCars(new Car3[]{c7, c8});

        // 6. 5 Đơn đặt lịch hẹn trước
        Calendar cal = Calendar.getInstance();
        Date d0 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1); Date d1 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1); Date d2 = cal.getTime();

        Appointment3 a1 = new Appointment3(1, d0, "08:00", "Chờ tiếp nhận", kh1, c1, new AppointmentServiceDetail3[]{
            new AppointmentServiceDetail3(1, SERVICE_LIST.get(0))
        });
        a1.setNote("Chi tiết: Bảo dưỡng (Dầu máy, Lọc dầu, Lọc gió động cơ)");

        Appointment3 a2 = new Appointment3(2, d0, "09:00", "Chờ tiếp nhận", kh2, c3, new AppointmentServiceDetail3[]{
            new AppointmentServiceDetail3(2, SERVICE_LIST.get(0)), new AppointmentServiceDetail3(3, SERVICE_LIST.get(1))
        });
        a2.setNote("Chi tiết: Bảo dưỡng (Lọc gió điều hòa); Sửa chữa chung (Hệ thống điện, điều hòa: Giàn lạnh / Nạp gas điều hòa)");

        Appointment3 a3 = new Appointment3(3, d1, "10:00", "Chờ tiếp nhận", kh3, c5, new AppointmentServiceDetail3[]{
            new AppointmentServiceDetail3(4, SERVICE_LIST.get(1))
        });
        a3.setNote("Chi tiết: Sửa chữa chung (Hệ thống phanh: Má phanh trước/sau; Hệ thống lái: Thước lái, Trợ lực lái)");

        Appointment3 a4 = new Appointment3(4, d1, "14:00", "Chờ tiếp nhận", kh4, c6, new AppointmentServiceDetail3[]{
            new AppointmentServiceDetail3(5, SERVICE_LIST.get(0)), new AppointmentServiceDetail3(6, SERVICE_LIST.get(2))
        });
        a4.setNote("Chi tiết: Bảo dưỡng (Dầu máy, Bugi, Nước làm mát); Đồng sơn (Sơn sửa phục hồi: Cản trước / cản sau)");

        Appointment3 a5 = new Appointment3(5, d2, "15:00", "Chờ tiếp nhận", kh5, c8, new AppointmentServiceDetail3[]{
            new AppointmentServiceDetail3(7, SERVICE_LIST.get(1)), new AppointmentServiceDetail3(8, SERVICE_LIST.get(2))
        });
        a5.setNote("Chi tiết: Sửa chữa chung (Chẩn đoán đèn báo lỗi và cập nhật phần mềm); Đồng sơn (Thay thế chi tiết thân vỏ: Gương chiếu hậu)");

        Collections.addAll(APPOINTMENT_LIST, a1, a2, a3, a4, a5);
    }

    public static String formatDate(Date d) {
        if (d == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy").format(d);
    }
}
