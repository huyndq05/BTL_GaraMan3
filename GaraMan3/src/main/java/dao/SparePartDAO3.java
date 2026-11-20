package dao;

import model.SparePart3;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SparePartDAO3 extends DAO3 {
    public SparePartDAO3() {
        super();
    }

    public SparePart3[] getPart(String keyword) {
        String kw = (keyword == null) ? "" : keyword.trim();
        if (isUsingDatabase()) {
            String sql = "SELECT id, name, quantity, unit, sellingPrice, description FROM tblSparePart3 " +
                         "WHERE LOWER(name) LIKE ? OR LOWER(description) LIKE ? OR CONCAT('PT', LPAD(id, 2, '0')) LIKE ? " +
                         "ORDER BY id";
            List<SparePart3> list = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                String pattern = "%" + kw.toLowerCase() + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
                ps.setString(3, pattern.toUpperCase());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new SparePart3(
                            rs.getInt("id"), rs.getString("name"), rs.getString("unit"),
                            rs.getFloat("sellingPrice"), rs.getInt("quantity"), rs.getString("description")
                        ));
                    }
                    return list.toArray(new SparePart3[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String kwLow = kw.toLowerCase();
        List<SparePart3> list = new ArrayList<>();
        for (SparePart3 p : SPARE_PART_LIST) {
            String code = String.format("PT%02d", p.getId()).toLowerCase();
            if (kwLow.isEmpty() || p.getName().toLowerCase().contains(kwLow) || code.contains(kwLow)
                    || (p.getDescription() != null && p.getDescription().toLowerCase().contains(kwLow))) {
                list.add(p);
            }
        }
        return list.toArray(new SparePart3[0]);
    }

    public SparePart3 getPartById(int id) {
        if (isUsingDatabase()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT id, name, quantity, unit, sellingPrice, description FROM tblSparePart3 WHERE id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new SparePart3(
                            rs.getInt("id"), rs.getString("name"), rs.getString("unit"),
                            rs.getFloat("sellingPrice"), rs.getInt("quantity"), rs.getString("description")
                        );
                    }
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (SparePart3 p : SPARE_PART_LIST) {
            if (p.getId() == id) return p;
        }
        return null;
    }
}
