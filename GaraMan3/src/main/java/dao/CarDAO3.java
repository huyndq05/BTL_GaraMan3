package dao;

import model.Car3;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CarDAO3 extends DAO3 {
    public CarDAO3() {
        super();
    }

    private Car3 mapRow(ResultSet rs) throws Exception {
        return new Car3(
            rs.getInt("id"), rs.getString("plateNumber"), rs.getString("brand"),
            rs.getString("model"), rs.getInt("year"), rs.getString("description"), rs.getInt("tblCustomer3id")
        );
    }

    public Car3 getCar(String plateNumber) {
        if (plateNumber == null) return null;
        if (isUsingDatabase()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM tblCar3 WHERE UPPER(plateNumber) = UPPER(?) LIMIT 1")) {
                ps.setString(1, plateNumber.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapRow(rs);
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Car3 c : CAR_LIST) {
            if (plateNumber.trim().equalsIgnoreCase(c.getPlateNumber())) return c;
        }
        return null;
    }

    public Car3[] getCar(int customerId) {
        if (isUsingDatabase()) {
            List<Car3> list = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM tblCar3 WHERE tblCustomer3id = ? ORDER BY id")) {
                ps.setInt(1, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapRow(rs));
                    return list.toArray(new Car3[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<Car3> list = new ArrayList<>();
        for (Car3 c : CAR_LIST) {
            if (c.getCustomerId() == customerId) list.add(c);
        }
        return list.toArray(new Car3[0]);
    }

    public Car3 getCarById(int id) {
        if (isUsingDatabase()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM tblCar3 WHERE id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapRow(rs);
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Car3 c : CAR_LIST) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public boolean saveCar(Car3 car) {
        if (car == null) return false;
        Car3 existing = getCar(car.getPlateNumber());
        if (existing != null) {
            car.setId(existing.getId());
            if (!isUsingDatabase() && car.getCustomerId() > 0) existing.setCustomerId(car.getCustomerId());
            return true;
        }
        if (isUsingDatabase()) {
            String sql = "INSERT INTO tblCar3 (plateNumber, brand, model, year, description, tblCustomer3id) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, car.getPlateNumber());
                ps.setString(2, car.getBrand() != null ? car.getBrand() : "");
                ps.setString(3, car.getModel() != null ? car.getModel() : "");
                ps.setInt(4, car.getYear() > 0 ? car.getYear() : 2022);
                ps.setString(5, car.getDescription() != null ? car.getDescription() : "");
                ps.setInt(6, car.getCustomerId());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) car.setId(rs.getInt(1));
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        if (car.getId() <= 0) car.setId(CAR_LIST.size() + 1);
        CAR_LIST.add(car);
        return true;
    }
}
