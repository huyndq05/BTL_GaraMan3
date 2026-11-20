package controller;

import dao.AppointmentDAO3;
import dao.ServiceDAO3;
import model.Appointment3;
import model.AppointmentServiceDetail3;
import model.Car3;
import model.Customer3;
import model.FullAddress3;
import model.FullName3;
import model.Service3;
import model.ServiceCatalog3;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BookAppointmentController extends HttpServlet {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\s]+$");
    private static final Pattern DIGITS_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9,10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private int noteLength(String note, String details) {
        String text = (details.isEmpty() ? "" : "Chi tiết: " + details)
                + (note.trim().isEmpty() ? "" : (details.isEmpty() ? "" : " | ") + "Ghi chú: " + note.trim());
        return text.codePointCount(0, text.length());
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        AppointmentDAO3 appointmentDAO = new AppointmentDAO3();
        ServiceDAO3 serviceDAO = new ServiceDAO3();
        SimpleDateFormat dfIso = new SimpleDateFormat("yyyy-MM-dd");
        dfIso.setLenient(false);
        SimpleDateFormat dfShow = new SimpleDateFormat("dd/MM/yyyy");

        // AJAX endpoint tra ve so luong dat cho tung khung gio theo ngay
        if ("1".equals(request.getParameter("ajaxSlots"))) {
            response.setContentType("application/json;charset=UTF-8");
            String dateParam = request.getParameter("date");
            Map<String, Integer> counts = new LinkedHashMap<>();
            try {
                if (dateParam != null && !dateParam.trim().isEmpty()) {
                    Date d = dfIso.parse(dateParam.trim());
                    counts = appointmentDAO.getSlotCountsForDate(d);
                }
            } catch (Exception ignored) {}
            StringBuilder sb = new StringBuilder("{");
            int idx = 0;
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                if (idx++ > 0) sb.append(",");
                sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
            }
            sb.append("}");
            response.getWriter().print(sb.toString());
            return;
        }

        if ("1".equals(request.getParameter("init"))) {
            session.removeAttribute("bk_inName");
            session.removeAttribute("bk_inPhoneNum");
            session.removeAttribute("bk_inEmail");
            session.removeAttribute("bk_inAddress");
            session.removeAttribute("bk_inBrand");
            session.removeAttribute("bk_inModel");
            session.removeAttribute("bk_inPlateNum");
            session.removeAttribute("bk_outsubDate");
            session.removeAttribute("bk_outsubTime");
            session.removeAttribute("bk_inNote");
            session.removeAttribute("bk_selectedServices");
            session.removeAttribute("bk_selectedDetails");
            session.removeAttribute("bk_calOffset");
        }

        if (request.getParameter("inName") != null) session.setAttribute("bk_inName", request.getParameter("inName"));
        if (request.getParameter("inPhoneNum") != null) session.setAttribute("bk_inPhoneNum", request.getParameter("inPhoneNum"));
        if (request.getParameter("inEmail") != null) session.setAttribute("bk_inEmail", request.getParameter("inEmail"));
        if (request.getParameter("inAddress") != null) session.setAttribute("bk_inAddress", request.getParameter("inAddress"));
        if (request.getParameter("inBrand") != null) session.setAttribute("bk_inBrand", request.getParameter("inBrand"));
        if (request.getParameter("inModel") != null) session.setAttribute("bk_inModel", request.getParameter("inModel"));
        if (request.getParameter("inPlateNum") != null) session.setAttribute("bk_inPlateNum", request.getParameter("inPlateNum"));
        if (request.getParameter("outsubDate") != null) session.setAttribute("bk_outsubDate", request.getParameter("outsubDate"));
        if (request.getParameter("outsubTime") != null) session.setAttribute("bk_outsubTime", request.getParameter("outsubTime"));
        if (request.getParameter("inNote") != null) session.setAttribute("bk_inNote", request.getParameter("inNote"));
        if (request.getParameter("calOffset") != null) {
            try {
                int off = Integer.parseInt(request.getParameter("calOffset"));
                session.setAttribute("bk_calOffset", Math.max(0, Math.min(2, off)));
            } catch (Exception ignored) {}
        }

        Service3[] offeredServices = serviceDAO.getOfferedServices("");
        Map<Integer, Service3> servicesById = new LinkedHashMap<>();
        for (Service3 service : offeredServices) {
            servicesById.put(service.getId(), service);
        }

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            Set<Integer> postedServices = new LinkedHashSet<>();
            String[] svcParams = request.getParameterValues("mainServiceId");
            if (svcParams != null) {
                for (String sp : svcParams) {
                    try {
                        int id = Integer.parseInt(sp.trim());
                        if (servicesById.containsKey(id)) postedServices.add(id);
                    } catch (NumberFormatException ignored) {}
                }
            }
            session.setAttribute("bk_selectedServices", postedServices);

            String detailsStr = request.getParameter("selectedDetailsText");
            if (detailsStr != null) {
                session.setAttribute("bk_selectedDetails", detailsStr.trim());
            }
        }

        String inName = getStrAttr(session, "bk_inName");
        String inPhoneNum = getStrAttr(session, "bk_inPhoneNum");
        String inEmail = getStrAttr(session, "bk_inEmail");
        String inAddress = getStrAttr(session, "bk_inAddress");
        String inBrand = getStrAttr(session, "bk_inBrand");
        String inModel = getStrAttr(session, "bk_inModel");
        String inPlateNum = getStrAttr(session, "bk_inPlateNum");
        String outsubDate = getStrAttr(session, "bk_outsubDate");
        String outsubTime = getStrAttr(session, "bk_outsubTime");
        String inNote = getStrAttr(session, "bk_inNote");
        String selectedDetailsText = getStrAttr(session, "bk_selectedDetails");
        Integer calOffsetObj = (Integer) session.getAttribute("bk_calOffset");
        int calOffset = (calOffsetObj != null) ? calOffsetObj : 0;

        @SuppressWarnings("unchecked")
        Set<Integer> selectedServiceIds = (Set<Integer>) session.getAttribute("bk_selectedServices");
        if (selectedServiceIds == null) {
            selectedServiceIds = new LinkedHashSet<>();
            session.setAttribute("bk_selectedServices", selectedServiceIds);
        }
        selectedServiceIds.retainAll(servicesById.keySet());

        String errorMsg = null;
        String errName = "";
        String errPhone = "";
        String errEmail = "";
        String errAddress = "";
        String errBrand = "";
        String errModel = "";
        String errPlate = "";

        if ("POST".equalsIgnoreCase(request.getMethod()) && request.getParameter("subComplete") != null) {
            String tName = inName.trim();
            String tPhone = inPhoneNum.trim();
            String tEmail = inEmail.trim();
            String tAddress = inAddress.trim();
            String tBrand = inBrand.trim();
            String tModel = inModel.trim();
            String tPlate = inPlateNum.trim();

            boolean valid = true;
            if (tName.isEmpty()) {
                errName = "Vui lòng nhập họ và tên.";
                valid = false;
            } else if (!NAME_PATTERN.matcher(tName).matches()) {
                errName = "Họ và tên không được chứa chữ số hoặc ký tự đặc biệt.";
                valid = false;
            }

            if (tPhone.isEmpty()) {
                errPhone = "Vui lòng nhập số điện thoại.";
                valid = false;
            } else if (!DIGITS_PATTERN.matcher(tPhone).matches()) {
                errPhone = "Số điện thoại chỉ được chứa chữ số.";
                valid = false;
            } else if (!PHONE_PATTERN.matcher(tPhone).matches()) {
                errPhone = "Số điện thoại phải gồm 10-11 chữ số và bắt đầu bằng số 0.";
                valid = false;
            }

            if (tEmail.isEmpty()) {
                errEmail = "Vui lòng nhập email.";
                valid = false;
            } else if (!EMAIL_PATTERN.matcher(tEmail).matches()) {
                errEmail = "Email không đúng định dạng (ví dụ: ten@gmail.com).";
                valid = false;
            }

            if (tAddress.isEmpty() || tAddress.codePointCount(0, tAddress.length()) > 255) {
                errAddress = "Vui lòng nhập địa chỉ từ 1 đến 255 ký tự.";
                valid = false;
            }
            if (tBrand.isEmpty()) { errBrand = "Vui lòng nhập hãng xe."; valid = false; }
            if (tModel.isEmpty()) { errModel = "Vui lòng nhập mẫu xe."; valid = false; }
            if (tPlate.isEmpty()) { errPlate = "Vui lòng nhập biển số xe."; valid = false; }

            if (selectedServiceIds.isEmpty()) {
                errorMsg = "Vui lòng chọn ít nhất một dịch vụ.";
                valid = false;
            } else if (noteLength(inNote, selectedDetailsText) > 255) {
                errorMsg = "Chi tiết dịch vụ và ghi chú dài quá 255 ký tự. Vui lòng rút gọn.";
                valid = false;
            } else if (outsubDate.isEmpty()) {
                errorMsg = "Vui lòng chọn ngày hẹn.";
                valid = false;
            } else if (outsubTime.isEmpty()) {
                errorMsg = "Vui lòng chọn giờ hẹn.";
                valid = false;
            }

            if (valid) {
                try {
                    Date appDate = dfIso.parse(outsubDate);
                    if (!dfIso.format(appDate).equals(outsubDate) || outsubDate.compareTo(dfIso.format(new Date())) < 0
                            || !Arrays.asList(AppointmentDAO3.ALL_SLOTS).contains(outsubTime)) {
                        errorMsg = "Vui lòng chọn ngày và giờ còn trống.";
                    } else if (appointmentDAO.getSlotCount(appDate, outsubTime) >= AppointmentDAO3.MAX_BOOKINGS_PER_SLOT) {
                        errorMsg = "Khung giờ " + outsubTime + " ngày " + dfShow.format(appDate)
                                + " đã hết chỗ. Vui lòng chọn khung giờ khác.";
                    } else {
                        String[] nameParts = tName.split("\\s+");
                        String last = nameParts.length >= 2 ? nameParts[0] : "";
                        String first = nameParts[nameParts.length - 1];
                        StringBuilder mid = new StringBuilder();
                        for (int i = 1; i < nameParts.length - 1; i++) {
                            if (mid.length() > 0) mid.append(" ");
                            mid.append(nameParts[i]);
                        }
                        FullName3 fullName = new FullName3(0, last, mid.toString(), first);
                        FullAddress3 address = new FullAddress3(0, tAddress, "", "");
                        Customer3 customer = new Customer3(0, fullName, tPhone, tEmail, address, "");
                        Car3 car = new Car3(0, tPlate, tBrand, tModel, 2022, "", 0);

                        List<AppointmentServiceDetail3> detList = new ArrayList<>();
                        for (Integer sid : selectedServiceIds) {
                            Service3 s = servicesById.get(sid);
                            if (s != null) {
                                detList.add(new AppointmentServiceDetail3(0, s));
                            }
                        }

                        StringBuilder finalNote = new StringBuilder();
                        if (!selectedDetailsText.isEmpty()) {
                            finalNote.append("Chi tiết: ").append(selectedDetailsText);
                        }
                        if (!inNote.trim().isEmpty()) {
                            if (finalNote.length() > 0) finalNote.append(" | ");
                            finalNote.append("Ghi chú: ").append(inNote.trim());
                        }

                        Appointment3 appointment = new Appointment3();
                        appointment.setAppointmentDate(appDate);
                        appointment.setAppointmentTime(outsubTime);
                        appointment.setStatus("Chờ tiếp nhận");
                        appointment.setNote(finalNote.toString());
                        appointment.setCustomer(customer);
                        appointment.setCar(car);
                        appointment.setAppointmentServices(detList.toArray(new AppointmentServiceDetail3[0]));

                        session.setAttribute("customer", customer);
                        session.setAttribute("car", car);
                        session.setAttribute("appointment", appointment);
                        response.sendRedirect("confirm-appointment");
                        return;
                    }
                } catch (Exception e) {
                    errorMsg = "Ngày hẹn không hợp lệ.";
                }
            } else if (errorMsg == null) {
                errorMsg = "Vui lòng kiểm tra lại các thông tin bắt buộc còn thiếu hoặc chưa đúng định dạng.";
            }
        }

        // Chuan bi du lieu lich thang cho View
        Calendar calTarget = Calendar.getInstance();
        calTarget.add(Calendar.MONTH, calOffset);
        int displayMonth = calTarget.get(Calendar.MONTH) + 1;
        int displayYear = calTarget.get(Calendar.YEAR);
        Date[] monthDates = appointmentDAO.getDatesOfMonth(displayMonth, displayYear);
        Set<String> fullDates = appointmentDAO.getFullDatesInMonth(displayMonth, displayYear);

        Calendar calToday = Calendar.getInstance();
        calToday.set(Calendar.HOUR_OF_DAY, 0);
        calToday.set(Calendar.MINUTE, 0);
        calToday.set(Calendar.SECOND, 0);
        calToday.set(Calendar.MILLISECOND, 0);
        Date todayStart = calToday.getTime();

        Calendar calFirst = Calendar.getInstance();
        calFirst.setTime(monthDates[0]);
        int leadingEmptyCells = calFirst.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;

        Map<String, Integer> slotCounts = new LinkedHashMap<>();
        if (!outsubDate.isEmpty()) {
            try {
                slotCounts = appointmentDAO.getSlotCountsForDate(dfIso.parse(outsubDate));
            } catch (Exception ignored) {}
        }

        String selectedDateLabel = "Chọn ngày";
        if (!outsubDate.isEmpty()) {
            try { selectedDateLabel = dfShow.format(dfIso.parse(outsubDate)); } catch (Exception ignored) {}
        }
        String selectedTimeLabel = outsubTime.isEmpty() ? "Chọn giờ" : outsubTime;
        String initialPeriod = (!outsubTime.isEmpty() && outsubTime.compareTo("13:00") >= 0) ? "afternoon" : "morning";

        request.setAttribute("offeredServices", offeredServices);
        request.setAttribute("selectedServiceIds", selectedServiceIds);
        request.setAttribute("inName", inName);
        request.setAttribute("inPhoneNum", inPhoneNum);
        request.setAttribute("inEmail", inEmail);
        request.setAttribute("inAddress", inAddress);
        request.setAttribute("inBrand", inBrand);
        request.setAttribute("inModel", inModel);
        request.setAttribute("inPlateNum", inPlateNum);
        request.setAttribute("outsubDate", outsubDate);
        request.setAttribute("outsubTime", outsubTime);
        request.setAttribute("inNote", inNote);
        request.setAttribute("selectedDetailsText", selectedDetailsText);
        request.setAttribute("calOffset", Integer.valueOf(calOffset));
        request.setAttribute("errorMsg", errorMsg);
        request.setAttribute("errName", errName);
        request.setAttribute("errPhone", errPhone);
        request.setAttribute("errEmail", errEmail);
        request.setAttribute("errAddress", errAddress);
        request.setAttribute("errBrand", errBrand);
        request.setAttribute("errModel", errModel);
        request.setAttribute("errPlate", errPlate);
        request.setAttribute("displayMonth", Integer.valueOf(displayMonth));
        request.setAttribute("displayYear", Integer.valueOf(displayYear));
        request.setAttribute("monthDates", monthDates);
        request.setAttribute("fullDates", fullDates);
        request.setAttribute("todayStart", todayStart);
        request.setAttribute("leadingEmptyCells", Integer.valueOf(leadingEmptyCells));
        request.setAttribute("slotCounts", slotCounts);
        request.setAttribute("selectedDateLabel", selectedDateLabel);
        request.setAttribute("selectedTimeLabel", selectedTimeLabel);
        request.setAttribute("initialPeriod", initialPeriod);
        request.setAttribute("showDatePicker", Boolean.valueOf(request.getParameter("calOffset") != null));
        request.setAttribute("maxPerSlot", Integer.valueOf(AppointmentDAO3.MAX_BOOKINGS_PER_SLOT));
        request.setAttribute("serviceCatalogJson", ServiceCatalog3.forScreen(false));
        request.setAttribute("fromController", Boolean.TRUE);
        request.getRequestDispatcher("/BookAppointmentScreen3.jsp").forward(request, response);
    }

    private String getStrAttr(HttpSession session, String key) {
        String val = (String) session.getAttribute(key);
        return val != null ? val : "";
    }
}
