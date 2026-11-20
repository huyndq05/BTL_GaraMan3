package dao;

import model.Appointment3;
import model.Car3;
import model.Customer3;
import model.FullAddress3;
import model.FullName3;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomerDAO3 extends DAO3 {
    public CustomerDAO3() {
        super();
    }

    private Customer3 mapRow(ResultSet rs) throws Exception {
        FullName3 fn = new FullName3(rs.getInt("fnId"), rs.getString("lastName"), rs.getString("middleName"), rs.getString("firstName"));
        FullAddress3 fa = new FullAddress3(rs.getInt("faId"), rs.getString("detail"), rs.getString("district"), rs.getString("province"));
        return new Customer3(rs.getInt("id"), fn, rs.getString("phoneNumber"), rs.getString("email"), fa, rs.getString("note"));
    }

    public Customer3[] getCustomer(String keyword) {
        String kw = (keyword == null) ? "" : keyword.trim();
        if (isUsingDatabase()) {
            String sql = "SELECT DISTINCT c.id, c.phoneNumber, c.email, c.note, " +
                         "fn.id AS fnId, fn.lastName, fn.middleName, fn.firstName, " +
                         "fa.id AS faId, fa.detail, fa.district, fa.province " +
                         "FROM tblCustomer3 c " +
                         "JOIN tblFullName3 fn ON c.tblFullName3id = fn.id " +
                         "JOIN tblFullAddress3 fa ON c.tblFullAddress3id = fa.id " +
                         "LEFT JOIN tblCar3 car ON car.tblCustomer3id = c.id " +
                         "WHERE LOWER(TRIM(CONCAT_WS(' ', fn.lastName, fn.middleName, fn.firstName))) LIKE ? " +
                         "OR c.phoneNumber LIKE ? OR LOWER(IFNULL(car.plateNumber, '')) LIKE ? " +
                         "ORDER BY c.id";
            List<Customer3> list = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                String pattern = "%" + kw.toLowerCase() + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
                ps.setString(3, pattern);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapRow(rs));
                    return list.toArray(new Customer3[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String kwLow = kw.toLowerCase();
        List<Customer3> result = new ArrayList<>();
        for (Customer3 c : CUSTOMER_LIST) {
            String name = c.getFullName() != null ? c.getFullName().toString().toLowerCase() : "";
            String phone = c.getPhoneNumber() != null ? c.getPhoneNumber().toLowerCase() : "";
            boolean matchPlate = false;
            for (Car3 car : CAR_LIST) {
                if (car.getCustomerId() == c.getId() && car.getPlateNumber() != null
                        && car.getPlateNumber().toLowerCase().contains(kwLow)) {
                    matchPlate = true;
                    break;
                }
            }
            if (kwLow.isEmpty() || name.contains(kwLow) || phone.contains(kwLow) || matchPlate) {
                result.add(c);
            }
        }
        return result.toArray(new Customer3[0]);
    }

    public Map<Customer3, Appointment3> getCustomersWithPendingAppointments() {
        Map<Customer3, Appointment3> queue = new LinkedHashMap<>();
        if (isUsingDatabase()) {
            String sql = "SELECT c.id, c.phoneNumber, c.email, c.note, "
                    + "n.id AS fnId, n.lastName, n.middleName, n.firstName, "
                    + "d.id AS faId, d.detail, d.district, d.province, "
                    + "a.id AS appointmentId, a.appointmentDate, a.appointmentTime "
                    + "FROM tblCustomer3 c JOIN tblFullName3 n ON n.id = c.tblFullName3id "
                    + "JOIN tblFullAddress3 d ON d.id = c.tblFullAddress3id "
                    + "JOIN tblAppointment3 a ON a.id = (SELECT upcoming.id FROM tblAppointment3 upcoming "
                    + "WHERE upcoming.tblCustomer3id = c.id AND upcoming.status = 'Chờ tiếp nhận' "
                    + "ORDER BY upcoming.appointmentDate, upcoming.appointmentTime, upcoming.id LIMIT 1) "
                    + "ORDER BY a.appointmentDate, a.appointmentTime, a.id";
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rows = ps.executeQuery()) {
                while (rows.next()) {
                    Appointment3 appointment = new Appointment3();
                    appointment.setId(rows.getInt("appointmentId"));
                    appointment.setAppointmentDate(rows.getDate("appointmentDate"));
                    appointment.setAppointmentTime(rows.getString("appointmentTime"));
                    queue.put(mapRow(rows), appointment);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return queue;
        }

        synchronized (DAO3.class) {
            APPOINTMENT_LIST.stream()
                    .filter(a -> a.getCustomer() != null && a.getAppointmentDate() != null
                            && a.getAppointmentTime() != null && "Chờ tiếp nhận".equalsIgnoreCase(a.getStatus()))
                    .sorted(AppointmentDAO3.RECEPTION_ORDER)
                    .forEach(a -> queue.putIfAbsent(getCustomerById(a.getCustomer().getId()), a));
        }
        queue.remove(null);
        return queue;
    }

    public Customer3 getCustomerByPhone(String phone) {
        if (phone == null) return null;
        if (isUsingDatabase()) {
            String sql = "SELECT c.id, c.phoneNumber, c.email, c.note, " +
                         "fn.id AS fnId, fn.lastName, fn.middleName, fn.firstName, " +
                         "fa.id AS faId, fa.detail, fa.district, fa.province " +
                         "FROM tblCustomer3 c " +
                         "JOIN tblFullName3 fn ON c.tblFullName3id = fn.id " +
                         "JOIN tblFullAddress3 fa ON c.tblFullAddress3id = fa.id " +
                         "WHERE c.phoneNumber = ? LIMIT 1";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, phone.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapRow(rs);
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Customer3 c : CUSTOMER_LIST) {
            if (phone.trim().equalsIgnoreCase(c.getPhoneNumber())) return c;
        }
        return null;
    }

    public Customer3 getCustomerById(int id) {
        if (isUsingDatabase()) {
            String sql = "SELECT c.id, c.phoneNumber, c.email, c.note, " +
                         "fn.id AS fnId, fn.lastName, fn.middleName, fn.firstName, " +
                         "fa.id AS faId, fa.detail, fa.district, fa.province " +
                         "FROM tblCustomer3 c " +
                         "JOIN tblFullName3 fn ON c.tblFullName3id = fn.id " +
                         "JOIN tblFullAddress3 fa ON c.tblFullAddress3id = fa.id " +
                         "WHERE c.id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapRow(rs);
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Customer3 c : CUSTOMER_LIST) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public boolean saveCustomer(Customer3 customer) {
        if (customer == null) return false;
        Customer3 existing = getCustomerByPhone(customer.getPhoneNumber());
        if (existing != null) {
            customer.setId(existing.getId());
            if (isUsingDatabase() && existing.getAddress() != null && existing.getAddress().toString().isEmpty()
                    && customer.getAddress() != null && !customer.getAddress().toString().isEmpty()) {
                String sql = "UPDATE tblFullAddress3 SET detail = ? WHERE id = ? "
                        + "AND detail = '' AND district = '' AND province = ''";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, customer.getAddress().toString());
                    ps.setInt(2, existing.getAddress().getId());
                    ps.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (!isUsingDatabase()) {
                if (existing.getAddress() == null || existing.getAddress().toString().isEmpty()) {
                    existing.setAddress(customer.getAddress());
                }
                if (customer.getFullName() != null && !customer.getFullName().toString().isEmpty()) existing.setFullName(customer.getFullName());
                if (customer.getEmail() != null && !customer.getEmail().isEmpty()) existing.setEmail(customer.getEmail());
            }
            return true;
        }
        return addCustomer(customer);
    }

    public boolean updateCustomer(Customer3 customer) {
        if (customer == null || customer.getId() <= 0 || customer.getFullName() == null
                || customer.getAddress() == null) return false;
        if (isUsingDatabase()) {
            String sql = "UPDATE tblCustomer3 c "
                    + "JOIN tblFullName3 n ON n.id = c.tblFullName3id "
                    + "JOIN tblFullAddress3 a ON a.id = c.tblFullAddress3id "
                    + "SET n.lastName = ?, n.middleName = ?, n.firstName = ?, c.phoneNumber = ?, c.email = ?, "
                    + "a.detail = ?, a.district = ?, a.province = ? WHERE c.id = ?";
            FullName3 name = customer.getFullName();
            FullAddress3 address = customer.getAddress();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, name.getLastName());
                ps.setString(2, name.getMiddleName());
                ps.setString(3, name.getFirstName());
                ps.setString(4, customer.getPhoneNumber());
                ps.setString(5, customer.getEmail());
                ps.setString(6, address.getDetail());
                ps.setString(7, address.getDistrict());
                ps.setString(8, address.getProvince());
                ps.setInt(9, customer.getId());
                return ps.executeUpdate() > 0 || getCustomerById(customer.getId()) != null;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        synchronized (DAO3.class) {
            Customer3 existing = getCustomerById(customer.getId());
            if (existing == null) return false;
            existing.setFullName(customer.getFullName());
            existing.setPhoneNumber(customer.getPhoneNumber());
            existing.setEmail(customer.getEmail());
            existing.setAddress(customer.getAddress());
            return true;
        }
    }

    public boolean addCustomer(Customer3 customer) {
        if (customer == null) return false;
        if (isUsingDatabase()) {
            try {
                con.setAutoCommit(false);
                FullName3 fn = customer.getFullName() != null ? customer.getFullName() : new FullName3(0, "", "", "");
                FullAddress3 fa = customer.getAddress() != null ? customer.getAddress() : new FullAddress3(0, "", "", "");

                int fnId = 0;
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO tblFullName3 (lastName, middleName, firstName) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, fn.getLastName() != null ? fn.getLastName() : "");
                    ps.setString(2, fn.getMiddleName() != null ? fn.getMiddleName() : "");
                    ps.setString(3, fn.getFirstName() != null ? fn.getFirstName() : "");
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) fnId = rs.getInt(1); }
                }

                int faId = 0;
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO tblFullAddress3 (district, province, detail) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, fa.getDistrict() != null ? fa.getDistrict() : "");
                    ps.setString(2, fa.getProvince() != null ? fa.getProvince() : "");
                    ps.setString(3, fa.getDetail() != null ? fa.getDetail() : "");
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) faId = rs.getInt(1); }
                }

                int custId = 0;
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO tblCustomer3 (phoneNumber, email, note, tblFullAddress3id, tblFullName3id) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "");
                    ps.setString(2, customer.getEmail() != null ? customer.getEmail() : "");
                    ps.setString(3, customer.getNote() != null ? customer.getNote() : "");
                    ps.setInt(4, faId);
                    ps.setInt(5, fnId);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) custId = rs.getInt(1); }
                }
                customer.setId(custId);
                con.commit();
                return true;
            } catch (Exception e) {
                try { con.rollback(); } catch (Exception ignored) {}
                e.printStackTrace();
                return false;
            } finally {
                try { con.setAutoCommit(true); } catch (Exception ignored) {}
            }
        }
        if (customer.getId() <= 0) customer.setId(CUSTOMER_LIST.size() + 1);
        CUSTOMER_LIST.add(customer);
        return true;
    }
}
