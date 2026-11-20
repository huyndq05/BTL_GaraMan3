package dao;

import model.FullAddress3;
import model.FullName3;
import model.Technician3;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TechnicianDAO3 extends DAO3 {
    public TechnicianDAO3() {
        super();
    }

    private Technician3 mapRow(ResultSet rs) throws Exception {
        FullName3 fn = new FullName3(rs.getInt("fnId"), rs.getString("lastName"), rs.getString("middleName"), rs.getString("firstName"));
        FullAddress3 fa = new FullAddress3(rs.getInt("faId"), rs.getString("detail"), rs.getString("district"), rs.getString("province"));
        return new Technician3(rs.getInt("id"), fn, rs.getString("phoneNumber"), rs.getString("specialization"), rs.getString("status"), rs.getFloat("salary"), fa);
    }

    public Technician3[] getTechnician(String keyword) {
        String kw = (keyword == null) ? "" : keyword.trim();
        if (isUsingDatabase()) {
            String sql = "SELECT t.id, t.phoneNumber, t.specialization, t.salary, t.status, " +
                         "fn.id AS fnId, fn.lastName, fn.middleName, fn.firstName, " +
                         "fa.id AS faId, fa.detail, fa.district, fa.province " +
                         "FROM tblTechnician3 t " +
                         "JOIN tblFullName3 fn ON t.tblFullName3id = fn.id " +
                         "JOIN tblFullAddress3 fa ON t.tblFullAddress3id = fa.id " +
                         "WHERE LOWER(TRIM(CONCAT_WS(' ', fn.lastName, fn.middleName, fn.firstName))) LIKE ? " +
                         "OR LOWER(t.specialization) LIKE ? ORDER BY t.id";
            List<Technician3> list = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                String pattern = "%" + kw.toLowerCase() + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapRow(rs));
                    return list.toArray(new Technician3[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String kwLow = kw.toLowerCase();
        List<Technician3> list = new ArrayList<>();
        for (Technician3 t : TECHNICIAN_LIST) {
            String name = t.getFullName() != null ? t.getFullName().toString().toLowerCase() : "";
            String spec = t.getSpecialization() != null ? t.getSpecialization().toLowerCase() : "";
            if (kwLow.isEmpty() || name.contains(kwLow) || spec.contains(kwLow)) list.add(t);
        }
        return list.toArray(new Technician3[0]);
    }

    public Technician3 getTechnicianById(int id) {
        if (isUsingDatabase()) {
            String sql = "SELECT t.id, t.phoneNumber, t.specialization, t.salary, t.status, " +
                         "fn.id AS fnId, fn.lastName, fn.middleName, fn.firstName, " +
                         "fa.id AS faId, fa.detail, fa.district, fa.province " +
                         "FROM tblTechnician3 t " +
                         "JOIN tblFullName3 fn ON t.tblFullName3id = fn.id " +
                         "JOIN tblFullAddress3 fa ON t.tblFullAddress3id = fa.id " +
                         "WHERE t.id = ?";
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
        for (Technician3 t : TECHNICIAN_LIST) {
            if (t.getId() == id) return t;
        }
        return null;
    }

    public boolean isAllTechniciansBusy() {
        Technician3[] all = getTechnician("");
        if (all == null || all.length == 0) return false;
        for (Technician3 t : all) {
            if (t != null && "Rảnh".equalsIgnoreCase(t.getStatus())) {
                return false;
            }
        }
        return true;
    }
}
