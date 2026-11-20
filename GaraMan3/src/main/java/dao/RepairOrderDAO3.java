package dao;

import model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class RepairOrderDAO3 extends DAO3 {
    public RepairOrderDAO3() {
        super();
    }

    public boolean createRepairOrder(RepairOrder3 order) {
        if (order == null || order.getCar() == null) return false;
        Appointment3 appointment = new AppointmentDAO3().getAppointment(order.getCar().getId());
        return createRepairOrder(order, appointment);
    }

    public boolean createRepairOrder(RepairOrder3 order, Appointment3 appointment) {
        if (order == null || order.getCar() == null) return false;
        if (order.getRepairServices() != null) {
            for (RepairServiceDetail3 detail : order.getRepairServices()) {
                if (detail != null && detail.getService() != null && detail.getPrice() <= 0) return false;
            }
        }
        if (isUsingDatabase()) {
            try (Connection connection = openDatabaseConnection()) {
                return saveInDatabase(order, appointment, connection);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        synchronized (DAO3.class) {
            return saveInMemory(order, appointment);
        }
    }

    private boolean saveInDatabase(RepairOrder3 order, Appointment3 appointment, Connection connection)
            throws Exception {
        connection.setAutoCommit(false);
        try {
            // 1. Kiem tra ton kho thuc te & tru ton kho
            if (order.getRepairSpareParts() != null) {
                for (RepairSparePartDetail3 pd : order.getRepairSpareParts()) {
                    if (pd != null && pd.getSparePart() != null) {
                        try (PreparedStatement psChk = connection.prepareStatement("SELECT quantity FROM tblSparePart3 WHERE id = ? FOR UPDATE")) {
                            psChk.setInt(1, pd.getSparePart().getId());
                            try (ResultSet rs = psChk.executeQuery()) {
                                if (!rs.next() || rs.getInt("quantity") < pd.getQuantity()) {
                                    connection.rollback();
                                    return false;
                                }
                            }
                        }
                    }
                }
            }

            // 2. Tao phieu nhan xe tblRepairOrder3
            String sqlOrder = "INSERT INTO tblRepairOrder3 (createdDate, completedDate, status, note, tblCar3id, tblSalesStaff3id) VALUES (?, ?, ?, ?, ?, ?)";
            int orderId = 0;
            try (PreparedStatement psOrder = connection.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setDate(1, new java.sql.Date(order.getCreatedDate().getTime()));
                if (order.getCompletedDate() != null) {
                    psOrder.setDate(2, new java.sql.Date(order.getCompletedDate().getTime()));
                } else {
                    psOrder.setNull(2, java.sql.Types.DATE);
                }
                psOrder.setString(3, order.getStatus() != null ? order.getStatus() : "Đang sửa chữa");
                psOrder.setString(4, order.getNote() != null ? order.getNote() : "");
                psOrder.setInt(5, order.getCar().getId());
                psOrder.setInt(6, order.getSalesStaff() != null ? order.getSalesStaff().getId() : 1);
                psOrder.executeUpdate();
                try (ResultSet rs = psOrder.getGeneratedKeys()) {
                    if (rs.next()) orderId = rs.getInt(1);
                }
            }

            // 3. Luu chi tiet dich vu tblRepairServiceDetail3
            if (order.getRepairServices() != null && order.getRepairServices().length > 0) {
                String sqlSvc = "INSERT INTO tblRepairServiceDetail3 (price, note, tblRepairOrder3id, tblService3id, tblTechnician3id) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement psSvc = connection.prepareStatement(sqlSvc)) {
                    for (RepairServiceDetail3 sd : order.getRepairServices()) {
                        if (sd != null && sd.getService() != null && sd.getTechnician() != null) {
                            psSvc.setFloat(1, sd.getPrice());
                            psSvc.setString(2, sd.getNote() != null ? sd.getNote() : "");
                            psSvc.setInt(3, orderId);
                            psSvc.setInt(4, sd.getService().getId());
                            psSvc.setInt(5, sd.getTechnician().getId());
                            psSvc.addBatch();
                        }
                    }
                    psSvc.executeBatch();
                }
            }

            // 4. Luu chi tiet phu tung & tru ton kho tblSparePart3
            if (order.getRepairSpareParts() != null && order.getRepairSpareParts().length > 0) {
                String sqlPart = "INSERT INTO tblRepairSparePartDetail3 (quantity, price, tblSparePart3id, tblRepairOrder3id) VALUES (?, ?, ?, ?)";
                String sqlUpdStock = "UPDATE tblSparePart3 SET quantity = quantity - ? WHERE id = ?";
                try (PreparedStatement psPart = connection.prepareStatement(sqlPart);
                     PreparedStatement psStock = connection.prepareStatement(sqlUpdStock)) {
                    for (RepairSparePartDetail3 pd : order.getRepairSpareParts()) {
                        if (pd != null && pd.getSparePart() != null) {
                            psPart.setInt(1, pd.getQuantity());
                            psPart.setFloat(2, pd.getPrice());
                            psPart.setInt(3, pd.getSparePart().getId());
                            psPart.setInt(4, orderId);
                            psPart.addBatch();

                            psStock.setInt(1, pd.getQuantity());
                            psStock.setInt(2, pd.getSparePart().getId());
                            psStock.addBatch();
                        }
                    }
                    psPart.executeBatch();
                    psStock.executeBatch();
                }
            }

            if (appointment != null) {
                String sql = "UPDATE tblAppointment3 SET status = 'Đã tiếp nhận' "
                        + "WHERE id = ? AND tblCar3id = ? AND status = 'Chờ tiếp nhận'";
                try (PreparedStatement psApp = connection.prepareStatement(sql)) {
                    psApp.setInt(1, appointment.getId());
                    psApp.setInt(2, order.getCar().getId());
                    if (psApp.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }
            }

            connection.commit();
            order.setId(orderId);
            return true;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception rollbackError) {
                e.addSuppressed(rollbackError);
            }
            throw e;
        }
    }

    private boolean saveInMemory(RepairOrder3 order, Appointment3 appointment) {
        if (appointment != null && !"Chờ tiếp nhận".equalsIgnoreCase(appointment.getStatus())) return false;

        // In-memory fallback
        if (order.getRepairSpareParts() != null) {
            for (RepairSparePartDetail3 pd : order.getRepairSpareParts()) {
                if (pd != null && pd.getSparePart() != null) {
                    if (pd.getSparePart().getQuantity() < pd.getQuantity()) return false;
                }
            }
            for (RepairSparePartDetail3 pd : order.getRepairSpareParts()) {
                if (pd != null && pd.getSparePart() != null) {
                    pd.getSparePart().setQuantity(pd.getSparePart().getQuantity() - pd.getQuantity());
                }
            }
        }
        if (order.getId() <= 0) order.setId(REPAIR_ORDER_LIST.size() + 1);
        if (appointment != null) appointment.setStatus("Đã tiếp nhận");
        REPAIR_ORDER_LIST.add(order);
        return true;
    }

    public int getNextOrderNumber() {
        if (isUsingDatabase()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT IFNULL(MAX(id), 0) + 1 AS nextId FROM tblRepairOrder3");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("nextId");
            } catch (Exception e) {}
        }
        return REPAIR_ORDER_LIST.size() + 1;
    }
}
