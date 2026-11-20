package dao;

import model.Appointment3;
import model.AppointmentServiceDetail3;
import model.Service3;
import model.ServiceCatalog3;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO3 extends DAO3 {
    public ServiceDAO3() {
        super();
    }

    public static boolean isOfferedService(int id) {
        return ServiceCatalog3.contains(id);
    }

    public Service3[] getOfferedServices(String keyword) {
        List<Service3> offered = new ArrayList<>();
        for (Service3 service : getService(keyword)) {
            if (isOfferedService(service.getId())) offered.add(service);
        }
        return offered.toArray(new Service3[0]);
    }

    public Service3[] getService(String keyword) {
        String kw = (keyword == null) ? "" : keyword.trim();
        if (isUsingDatabase()) {
            String sql = "SELECT id, name, price, duration, description FROM tblService3 " +
                         "WHERE LOWER(name) LIKE ? OR LOWER(description) LIKE ? OR CONCAT('DV', LPAD(id, 2, '0')) LIKE ? " +
                         "ORDER BY id";
            List<Service3> list = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                String pattern = "%" + kw.toLowerCase() + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
                ps.setString(3, pattern.toUpperCase());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new Service3(
                            rs.getInt("id"), rs.getString("name"),
                            rs.getFloat("price"), rs.getInt("duration"), rs.getString("description")
                        ));
                    }
                    return list.toArray(new Service3[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String kwLow = kw.toLowerCase();
        List<Service3> list = new ArrayList<>();
        for (Service3 s : SERVICE_LIST) {
            String code = String.format("DV%02d", s.getId()).toLowerCase();
            if (kwLow.isEmpty() || s.getName().toLowerCase().contains(kwLow) || code.contains(kwLow)
                    || (s.getDescription() != null && s.getDescription().toLowerCase().contains(kwLow))) {
                list.add(s);
            }
        }
        return list.toArray(new Service3[0]);
    }

    public Service3[] getServiceOfAppointment(int appointmentId) {
        if (isUsingDatabase()) {
            String sql = "SELECT s.id, s.name, s.price, s.duration, s.description " +
                         "FROM tblAppointmentServiceDetail3 d JOIN tblService3 s ON d.tblService3id = s.id " +
                         "WHERE d.tblAppointment3id = ?";
            List<Service3> list = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, appointmentId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new Service3(
                            rs.getInt("id"), rs.getString("name"),
                            rs.getFloat("price"), rs.getInt("duration"), rs.getString("description")
                        ));
                    }
                    return list.toArray(new Service3[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<Service3> list = new ArrayList<>();
        for (Appointment3 a : APPOINTMENT_LIST) {
            if (a.getId() == appointmentId && a.getAppointmentServices() != null) {
                for (AppointmentServiceDetail3 d : a.getAppointmentServices()) {
                    if (d != null && d.getService() != null) list.add(d.getService());
                }
            }
        }
        return list.toArray(new Service3[0]);
    }

    public Service3 getServiceById(int id) {
        if (isUsingDatabase()) {
            String sql = "SELECT id, name, price, duration, description FROM tblService3 WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Service3(
                            rs.getInt("id"), rs.getString("name"),
                            rs.getFloat("price"), rs.getInt("duration"), rs.getString("description")
                        );
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Service3 s : SERVICE_LIST) {
            if (s.getId() == id) return s;
        }
        return null;
    }
}
