package dao;

import model.FullAddress3;
import model.FullName3;
import model.SalesStaff3;
import model.Staff3;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StaffDAO3 extends DAO3 {
    public StaffDAO3() {
        super();
    }

    public Staff3 checkLogin(String username, String password) {
        if (username == null || password == null) return null;
        if (isUsingDatabase()) {
            String sql = "SELECT s.*, fn.id AS fnId, fn.lastName, fn.middleName, fn.firstName, " +
                         "fa.id AS faId, fa.detail, fa.district, fa.province " +
                         "FROM tblStaff3 s " +
                         "JOIN tblFullName3 fn ON s.tblFullName3id = fn.id " +
                         "JOIN tblFullAddress3 fa ON s.tblFullAddress3id = fa.id " +
                         "WHERE s.username = ? AND s.password = ? LIMIT 1";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, username.trim());
                ps.setString(2, password.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        FullName3 fn = new FullName3(rs.getInt("fnId"), rs.getString("lastName"), rs.getString("middleName"), rs.getString("firstName"));
                        FullAddress3 fa = new FullAddress3(rs.getInt("faId"), rs.getString("detail"), rs.getString("district"), rs.getString("province"));
                        return new SalesStaff3(
                            rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                            rs.getDate("birthDate"), rs.getString("email"), rs.getString("phoneNumber"),
                            rs.getFloat("salary"), rs.getString("note"), fn, fa
                        );
                    }
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (SalesStaff3 s : STAFF_LIST) {
            if (username.trim().equalsIgnoreCase(s.getUsername()) && password.trim().equals(s.getPassword())) {
                return s;
            }
        }
        return null;
    }

    public boolean changePassword(int staffId, String oldPassword, String newPassword) {
        if (oldPassword == null || newPassword == null || newPassword.trim().isEmpty()) return false;
        if (isUsingDatabase()) {
            String sql = "UPDATE tblStaff3 SET password = ? WHERE id = ? AND password = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, newPassword.trim());
                ps.setInt(2, staffId);
                ps.setString(3, oldPassword.trim());
                return ps.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        for (SalesStaff3 s : STAFF_LIST) {
            if (s.getId() == staffId && oldPassword.trim().equals(s.getPassword())) {
                s.setPassword(newPassword.trim());
                return true;
            }
        }
        return false;
    }
}
