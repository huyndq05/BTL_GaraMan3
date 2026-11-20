package dao;

import model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppointmentDAO3 extends DAO3 {
    public static final int MAX_BOOKINGS_PER_SLOT = 3;
    public static final String[] MORNING_SLOTS = {"08:00", "09:00", "10:00", "11:00", "12:00"};
    public static final String[] AFTERNOON_SLOTS = {"13:00", "14:00", "15:00", "16:00", "17:00"};
    public static final String[] ALL_SLOTS = {
        "08:00", "09:00", "10:00", "11:00", "12:00",
        "13:00", "14:00", "15:00", "16:00", "17:00"
    };

    public AppointmentDAO3() {
        super();
    }

    private Date stripTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public Date[] getDatesOfMonth(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        List<Date> dates = new ArrayList<>();
        for (int day = 1; day <= maxDay; day++) {
            cal.set(Calendar.DAY_OF_MONTH, day);
            dates.add(cal.getTime());
        }
        return dates.toArray(new Date[0]);
    }

    public Date[] getDatesOfCurrentMonth() {
        Calendar now = Calendar.getInstance();
        return getDatesOfMonth(now.get(Calendar.MONTH) + 1, now.get(Calendar.YEAR));
    }

    public Set<String> getFullDatesInMonth(int month, int year) {
        Date[] dates = getDatesOfMonth(month, year);
        Set<String> fullDates = new HashSet<>();
        for (Map.Entry<String, Map<String, Integer>> day : countSlots(dates[0], dates[dates.length - 1]).entrySet()) {
            if (isFull(day.getValue())) fullDates.add(day.getKey());
        }
        return fullDates;
    }

    public boolean isDateFull(Date date) {
        return date != null && isFull(getSlotCountsForDate(date));
    }

    private boolean isFull(Map<String, Integer> counts) {
        for (String slot : ALL_SLOTS) {
            if (counts.getOrDefault(slot, 0) < MAX_BOOKINGS_PER_SLOT) return false;
        }
        return true;
    }

    public Map<String, Integer> getSlotCountsForDate(Date date) {
        Map<String, Integer> result = new LinkedHashMap<>();
        Map<String, Integer> booked = date == null ? Collections.emptyMap()
                : countSlots(date, date).getOrDefault(new SimpleDateFormat("yyyy-MM-dd").format(date),
                        Collections.emptyMap());
        for (String slot : ALL_SLOTS) result.put(slot, booked.getOrDefault(slot, 0));
        return result;
    }

    private Map<String, Map<String, Integer>> countSlots(Date firstDay, Date lastDay) {
        Map<String, Map<String, Integer>> counts = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (isUsingDatabase()) {
            String sql = "SELECT appointmentDate, TRIM(appointmentTime) AS slot, COUNT(*) AS bookings "
                    + "FROM tblAppointment3 WHERE appointmentDate BETWEEN ? AND ? AND status <> 'Đã hủy' "
                    + "GROUP BY appointmentDate, TRIM(appointmentTime)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setDate(1, new java.sql.Date(firstDay.getTime()));
                ps.setDate(2, new java.sql.Date(lastDay.getTime()));
                try (ResultSet rows = ps.executeQuery()) {
                    while (rows.next()) {
                        String slot = rows.getString("slot");
                        if (Arrays.asList(ALL_SLOTS).contains(slot)) {
                            counts.computeIfAbsent(dateFormat.format(rows.getDate("appointmentDate")), key -> new HashMap<>())
                                    .put(slot, rows.getInt("bookings"));
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return counts;
        }

        String first = dateFormat.format(firstDay);
        String last = dateFormat.format(lastDay);
        synchronized (DAO3.class) {
            for (Appointment3 appointment : APPOINTMENT_LIST) {
                if (appointment.getAppointmentDate() == null || appointment.getAppointmentTime() == null
                        || "Đã hủy".equalsIgnoreCase(appointment.getStatus())) continue;
                String day = dateFormat.format(appointment.getAppointmentDate());
                String slot = appointment.getAppointmentTime().trim();
                if (day.compareTo(first) >= 0 && day.compareTo(last) <= 0 && Arrays.asList(ALL_SLOTS).contains(slot)) {
                    counts.computeIfAbsent(day, key -> new HashMap<>()).merge(slot, 1, Integer::sum);
                }
            }
        }
        return counts;
    }

    public int getSlotCount(Date date, String slot) {
        if (date == null || slot == null) return 0;
        return getSlotCountsForDate(date).getOrDefault(slot.trim(), 0);
    }

    public Date[] getAvailableDate(int month, int year) {
        Date today = stripTime(new Date());
        Date[] allDates = getDatesOfMonth(month, year);
        Set<String> fullDates = getFullDatesInMonth(month, year);
        SimpleDateFormat dfIso = new SimpleDateFormat("yyyy-MM-dd");
        List<Date> available = new ArrayList<>();
        for (Date d : allDates) {
            if (!stripTime(d).before(today) && !fullDates.contains(dfIso.format(d))) {
                available.add(d);
            }
        }
        return available.toArray(new Date[0]);
    }

    public String[] getAvailableTime(Date date) {
        Map<String, Integer> counts = getSlotCountsForDate(date);
        List<String> available = new ArrayList<>();
        for (String s : ALL_SLOTS) {
            if (counts.getOrDefault(s, 0) < MAX_BOOKINGS_PER_SLOT) {
                available.add(s);
            }
        }
        return available.toArray(new String[0]);
    }

    static final Comparator<Appointment3> RECEPTION_ORDER = Comparator
            .comparing(Appointment3::getAppointmentDate)
            .thenComparing(Appointment3::getAppointmentTime)
            .thenComparingInt(Appointment3::getId);

    public boolean saveAppointment(Appointment3 appointment) {
        if (appointment == null || appointment.getCustomer() == null || appointment.getCar() == null
                || appointment.getAppointmentDate() == null
                || stripTime(appointment.getAppointmentDate()).before(stripTime(new Date()))
                || !Arrays.asList(ALL_SLOTS).contains(appointment.getAppointmentTime())) return false;
        if (appointment.getStatus() == null || appointment.getStatus().isEmpty()) {
            appointment.setStatus("Chờ tiếp nhận");
        }
        if (isUsingDatabase()) {
            try (Connection connection = openDatabaseConnection()) {
                return saveWithCapacityCheck(connection, appointment);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        synchronized (DAO3.class) {
            if (getSlotCount(appointment.getAppointmentDate(), appointment.getAppointmentTime()) >= MAX_BOOKINGS_PER_SLOT) {
                return false;
            }
            if (appointment.getId() <= 0) appointment.setId(APPOINTMENT_LIST.size() + 1);
            APPOINTMENT_LIST.add(appointment);
            return true;
        }
    }

    private boolean saveWithCapacityCheck(Connection connection, Appointment3 appointment) throws SQLException {
        java.sql.Date day = new java.sql.Date(appointment.getAppointmentDate().getTime());
        String time = appointment.getAppointmentTime();
        // Hold one lock per database and slot until this connection commits and closes.
        String lock = "SELECT GET_LOCK(SHA2(CONCAT(DATABASE(), '|garaman|', ?, '|', ?), 256), 5)";
        try (PreparedStatement ps = connection.prepareStatement(lock)) {
            ps.setDate(1, day);
            ps.setString(2, time);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || rs.getInt(1) != 1) return false;
            }
        }

        connection.setAutoCommit(false);
        try {
            String count = "SELECT COUNT(*) FROM tblAppointment3 WHERE appointmentDate = ? "
                    + "AND TRIM(appointmentTime) = ? AND status <> 'Đã hủy'";
            try (PreparedStatement ps = connection.prepareStatement(count)) {
                ps.setDate(1, day);
                ps.setString(2, time);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) >= MAX_BOOKINGS_PER_SLOT) {
                        connection.rollback();
                        return false;
                    }
                }
            }

            String insert = "INSERT INTO tblAppointment3 "
                    + "(appointmentDate, appointmentTime, status, note, tblCustomer3id, tblCar3id) VALUES (?, ?, ?, ?, ?, ?)";
            int id;
            try (PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDate(1, day);
                ps.setString(2, time);
                ps.setString(3, appointment.getStatus());
                ps.setString(4, appointment.getNote());
                ps.setInt(5, appointment.getCustomer().getId());
                ps.setInt(6, appointment.getCar().getId());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("Appointment ID was not generated");
                    id = rs.getInt(1);
                }
            }
            if (appointment.getAppointmentServices() != null && appointment.getAppointmentServices().length > 0) {
                String details = "INSERT INTO tblAppointmentServiceDetail3 (tblAppointment3id, tblService3id) VALUES (?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(details)) {
                    for (AppointmentServiceDetail3 detail : appointment.getAppointmentServices()) {
                        if (detail == null || detail.getService() == null) continue;
                        ps.setInt(1, id);
                        ps.setInt(2, detail.getService().getId());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            connection.commit();
            appointment.setId(id);
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackError) {
                e.addSuppressed(rollbackError);
            }
            throw e;
        }
    }

    public Appointment3 getAppointment(int carId) {
        if (isUsingDatabase()) {
            String sql = "SELECT id, appointmentDate, appointmentTime, status, note, tblCustomer3id, tblCar3id " +
                         "FROM tblAppointment3 WHERE tblCar3id = ? AND status = 'Chờ tiếp nhận' ORDER BY appointmentDate, appointmentTime, id LIMIT 1";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, carId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Appointment3 a = new Appointment3();
                        a.setId(rs.getInt("id"));
                        a.setAppointmentDate(rs.getDate("appointmentDate"));
                        a.setAppointmentTime(rs.getString("appointmentTime"));
                        a.setStatus(rs.getString("status"));
                        a.setNote(rs.getString("note"));
                        Service3[] svcs = new ServiceDAO3().getServiceOfAppointment(a.getId());
                        AppointmentServiceDetail3[] dets = new AppointmentServiceDetail3[svcs.length];
                        for (int i = 0; i < svcs.length; i++) dets[i] = new AppointmentServiceDetail3(0, svcs[i]);
                        a.setAppointmentServices(dets);
                        return a;
                    }
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        synchronized (DAO3.class) {
            return APPOINTMENT_LIST.stream()
                    .filter(a -> a.getCar() != null && a.getCar().getId() == carId
                            && a.getAppointmentDate() != null && a.getAppointmentTime() != null
                            && "Chờ tiếp nhận".equalsIgnoreCase(a.getStatus()))
                    .min(RECEPTION_ORDER).orElse(null);
        }
    }
}
