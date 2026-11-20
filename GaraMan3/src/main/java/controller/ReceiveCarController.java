package controller;

import dao.AppointmentDAO3;
import dao.CarDAO3;
import dao.CustomerDAO3;
import dao.ServiceDAO3;
import dao.SparePartDAO3;
import dao.TechnicianDAO3;
import model.Appointment3;
import model.AppointmentServiceDetail3;
import model.Car3;
import model.Customer3;
import model.FullAddress3;
import model.FullName3;
import model.RepairOrder3;
import model.RepairServiceDetail3;
import model.RepairSparePartDetail3;
import model.SalesStaff3;
import model.Service3;
import model.ServiceCatalog3;
import model.SparePart3;
import model.Technician3;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ReceiveCarController extends HttpServlet {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\s]+$");
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

    private String getBookedServiceDetails(String note, String serviceName) {
        if (note == null || !note.startsWith("Chi tiết: ")) return "";
        String details = note.substring("Chi tiết: ".length()).split(" \\| Ghi chú: ", 2)[0];
        Matcher match = Pattern.compile("(?:^|; )" + Pattern.quote(serviceName)
                + " \\(([^)]*)\\)").matcher(details);
        return match.find() ? match.group(1) : "";
    }

    private float getBookedServicePrice(Service3 service, String note) {
        try {
            return ServiceCatalog3.price(service, note);
        } catch (IllegalArgumentException ex) {
            return 0;
        }
    }

    private boolean hasUnpricedServices(List<RepairServiceDetail3> services) {
        for (RepairServiceDetail3 detail : services) {
            if (detail.getPrice() <= 0) return true;
        }
        return false;
    }

    private String getCustomerNote(Appointment3 appointment) {
        if (appointment == null || appointment.getNote() == null) return "";
        String note = appointment.getNote();
        if (note.startsWith("Chi tiết: ")) {
            String separator = " | Ghi chú: ";
            int start = note.indexOf(separator);
            return start < 0 ? "" : note.substring(start + separator.length());
        }
        String prefix = "Ghi chú: ";
        return note.startsWith(prefix) ? note.substring(prefix.length()) : note;
    }

    private RepairOrder3 buildRepairOrder(SalesStaff3 staff, Customer3 customer, Car3 car,
                                          Appointment3 appointment, List<RepairServiceDetail3> services,
                                          List<RepairSparePartDetail3> parts, int deliveryDays) {
        RepairOrder3 order = new RepairOrder3();
        Calendar deliveryDate = Calendar.getInstance();
        order.setCreatedDate(deliveryDate.getTime());
        deliveryDate.add(Calendar.DAY_OF_MONTH, deliveryDays);
        order.setCompletedDate(deliveryDate.getTime());
        order.setStatus("Đang sửa chữa");
        order.setNote(getCustomerNote(appointment));
        order.setSalesStaff(staff);
        order.setCustomer(customer);
        order.setCar(car);
        order.setRepairServices(services.toArray(new RepairServiceDetail3[0]));
        order.setRepairSpareParts(parts.toArray(new RepairSparePartDetail3[0]));
        return order;
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        SalesStaff3 salesStaff = (SalesStaff3) session.getAttribute("salesStaff");
        if (salesStaff == null) {
            response.sendRedirect("login");
            return;
        }

        if ("1".equals(request.getParameter("init"))) {
            SalesStaffHomeController.clearReceiveCarSession(session);
        }

        CustomerDAO3 customerDAO = new CustomerDAO3();
        CarDAO3 carDAO = new CarDAO3();
        AppointmentDAO3 appointmentDAO = new AppointmentDAO3();
        ServiceDAO3 serviceDAO = new ServiceDAO3();
        SparePartDAO3 sparePartDAO = new SparePartDAO3();
        TechnicianDAO3 technicianDAO = new TechnicianDAO3();

        SimpleDateFormat dfShow = new SimpleDateFormat("dd/MM/yyyy");

        Customer3 selectedCustomer = (Customer3) session.getAttribute("rc_customer");
        Car3 selectedCar = (Car3) session.getAttribute("rc_car");
        Appointment3 selectedAppointment = (Appointment3) session.getAttribute("rc_appointment");
        Technician3 selectedTech = (Technician3) session.getAttribute("rc_technician");

        @SuppressWarnings("unchecked")
        List<RepairServiceDetail3> selectedServices = (List<RepairServiceDetail3>) session.getAttribute("rc_services");
        if (selectedServices == null) {
            selectedServices = new ArrayList<>();
            session.setAttribute("rc_services", selectedServices);
        }

        @SuppressWarnings("unchecked")
        List<RepairSparePartDetail3> selectedParts = (List<RepairSparePartDetail3>) session.getAttribute("rc_parts");
        if (selectedParts == null) {
            selectedParts = new ArrayList<>();
            session.setAttribute("rc_parts", selectedParts);
        }

        Integer stepObj = (Integer) session.getAttribute("rc_step");
        int currentStep = (stepObj != null) ? stepObj : 1;

        String stepParam = request.getParameter("step");
        if (stepParam != null && !stepParam.trim().isEmpty()) {
            try {
                int reqStep = Integer.parseInt(stepParam.trim());
                if (reqStep == 1) {
                    currentStep = 1;
                } else if (reqStep == 2 && selectedCustomer != null) {
                    currentStep = 2;
                } else if (reqStep == 3 && selectedCustomer != null && selectedCar != null) {
                    currentStep = 3;
                } else if (reqStep == 4 && selectedCustomer != null && selectedCar != null && !selectedServices.isEmpty()) {
                    currentStep = 4;
                }
            } catch (NumberFormatException ignored) {}
        }

        if (request.getParameter("inSearchCustomer") != null) session.setAttribute("rc_kwCustomer", request.getParameter("inSearchCustomer"));
        if (request.getParameter("inSearchService") != null) session.setAttribute("rc_kwService", request.getParameter("inSearchService"));
        if (request.getParameter("inSearchPart") != null) session.setAttribute("rc_kwPart", request.getParameter("inSearchPart"));
        if (request.getParameter("inSearchTechnician") != null) session.setAttribute("rc_kwTech", request.getParameter("inSearchTechnician"));

        String kwCustomer = getStrAttr(session, "rc_kwCustomer");
        String kwService = getStrAttr(session, "rc_kwService");
        String kwPart = getStrAttr(session, "rc_kwPart");
        String kwTech = getStrAttr(session, "rc_kwTech");

        // Cap nhat KTV duoc chon theo tung dich vu (neu dang o Buoc 4)
        for (int i = 0; i < selectedServices.size(); i++) {
            String tidStr = request.getParameter("techForService_" + i);
            if (tidStr != null && !tidStr.trim().isEmpty()) {
                try {
                    int tid = Integer.parseInt(tidStr.trim());
                    if (tid > 0) {
                        Technician3 t = technicianDAO.getTechnicianById(tid);
                        if (t != null) selectedServices.get(i).setTechnician(t);
                    } else {
                        selectedServices.get(i).setTechnician(null);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        String errorMsg = null;
        String infoMsg = null;
        boolean showNewCustomerBox = false;
        Customer3 editingCustomer = null;
        String nName = "", nPhone = "", nEmail = "", nAddr = "";
        boolean showNewCarBox = false;

        String act = request.getParameter("act");
        String targetIdStr = request.getParameter("targetId");
        int targetId = -1;
        if (targetIdStr != null && !targetIdStr.trim().isEmpty()) {
            try { targetId = Integer.parseInt(targetIdStr.trim()); } catch (NumberFormatException ignored) {}
        }

        // --- BUOC 1: THEM HOAC CHINH SUA KHACH HANG ---
        if ("editCustomer".equals(act) && targetId > 0) {
            editingCustomer = customerDAO.getCustomerById(targetId);
            if (editingCustomer != null) {
                nName = editingCustomer.getFullName() == null ? "" : editingCustomer.getFullName().toString();
                nPhone = editingCustomer.getPhoneNumber();
                nEmail = editingCustomer.getEmail();
                nAddr = editingCustomer.getAddress() == null ? "" : editingCustomer.getAddress().toString();
                showNewCustomerBox = true;
            } else {
                errorMsg = "Không tìm thấy khách hàng cần sửa.";
            }
            currentStep = 1;
        }

        if ("POST".equalsIgnoreCase(request.getMethod()) && request.getParameter("saveNewCustomer") != null) {
            currentStep = 1;
            showNewCustomerBox = true;
            nName = request.getParameter("newCustName") != null ? request.getParameter("newCustName").trim() : "";
            nPhone = request.getParameter("newCustPhone") != null ? request.getParameter("newCustPhone").trim() : "";
            nEmail = request.getParameter("newCustEmail") != null ? request.getParameter("newCustEmail").trim() : "";
            nAddr = request.getParameter("newCustAddr") != null ? request.getParameter("newCustAddr").trim() : "";
            String editId = request.getParameter("editCustomerId");
            if (editId != null && !editId.isEmpty()) {
                try { editingCustomer = customerDAO.getCustomerById(Integer.parseInt(editId)); }
                catch (NumberFormatException ignored) {}
            }

            Customer3 samePhone = nPhone.isEmpty() ? null : customerDAO.getCustomerByPhone(nPhone);
            if (editId != null && !editId.isEmpty() && editingCustomer == null) {
                errorMsg = "Không tìm thấy khách hàng cần sửa.";
            } else if (nName.isEmpty() || nPhone.isEmpty() || nAddr.isEmpty()) {
                errorMsg = "Vui lòng nhập họ tên, số điện thoại và địa chỉ.";
            } else if (nName.length() > 255 || nEmail.length() > 255 || nAddr.length() > 255) {
                errorMsg = "Họ tên, email và địa chỉ tối đa 255 ký tự mỗi trường.";
            } else if (!NAME_PATTERN.matcher(nName).matches()) {
                errorMsg = "Họ và tên khách hàng không được chứa chữ số hoặc ký tự đặc biệt.";
            } else if (!PHONE_PATTERN.matcher(nPhone).matches()) {
                errorMsg = "Số điện thoại phải gồm 10-11 chữ số và bắt đầu bằng số 0.";
            } else if (!nEmail.isEmpty() && !EMAIL_PATTERN.matcher(nEmail).matches()) {
                errorMsg = "Email khách hàng không đúng định dạng.";
            } else if (samePhone != null && (editingCustomer == null || samePhone.getId() != editingCustomer.getId())) {
                errorMsg = "Số điện thoại " + nPhone + " đã được dùng cho khách hàng khác.";
            } else {
                String[] names = nName.split("\\s+");
                String last = names.length >= 2 ? names[0] : "";
                String first = names[names.length - 1];
                String middle = names.length > 2 ? String.join(" ", Arrays.copyOfRange(names, 1, names.length - 1)) : "";
                FullName3 name = new FullName3(0, last, middle, first);
                FullAddress3 address = new FullAddress3(0, nAddr, "", "");
                if (editingCustomer != null) {
                    int id = editingCustomer.getId();
                    if (editingCustomer.getAddress() != null && nAddr.equals(editingCustomer.getAddress().toString())) {
                        address = editingCustomer.getAddress();
                    }
                    Customer3 updated = new Customer3(id, name, nPhone, nEmail, address, editingCustomer.getNote());
                    if (customerDAO.updateCustomer(updated)) {
                        if (selectedCustomer != null && selectedCustomer.getId() == id) {
                            selectedCustomer = updated;
                            session.setAttribute("rc_customer", updated);
                            session.removeAttribute("rc_repairOrder");
                        }
                        if (!kwCustomer.trim().isEmpty()) {
                            kwCustomer = nPhone;
                            session.setAttribute("rc_kwCustomer", kwCustomer);
                        }
                        editingCustomer = null;
                        showNewCustomerBox = false;
                        nName = nPhone = nEmail = nAddr = "";
                        infoMsg = "Đã cập nhật thông tin khách hàng.";
                    } else {
                        errorMsg = "Không thể cập nhật thông tin khách hàng.";
                    }
                } else {
                    Customer3 newCustomer = new Customer3(0, name, nPhone, nEmail, address, "");
                    if (customerDAO.addCustomer(newCustomer)) {
                        selectedCustomer = newCustomer;
                        selectedCar = null;
                        selectedAppointment = null;
                        selectedServices.clear();
                        selectedParts.clear();
                        selectedTech = null;
                        session.setAttribute("rc_customer", selectedCustomer);
                        session.removeAttribute("rc_car");
                        session.removeAttribute("rc_appointment");
                        session.removeAttribute("rc_technician");
                        currentStep = 2;
                        showNewCarBox = true;
                        infoMsg = "Đã thêm khách hàng: " + nName;
                    } else {
                        errorMsg = "Không thể lưu khách hàng mới.";
                    }
                }
            }
        }

        // --- BUOC 2: THEM XE MOI CHO KHACH HANG DA CHON ---
        if (request.getParameter("saveNewCar") != null && selectedCustomer != null) {
            String cPlate = request.getParameter("newCarPlate") != null ? request.getParameter("newCarPlate").trim().toUpperCase() : "";
            String cBrand = request.getParameter("newCarBrand") != null ? request.getParameter("newCarBrand").trim() : "";
            String cModel = request.getParameter("newCarModel") != null ? request.getParameter("newCarModel").trim() : "";
            String cYearStr = request.getParameter("newCarYear") != null ? request.getParameter("newCarYear").trim() : "";
            String cDesc = request.getParameter("newCarDesc") != null ? request.getParameter("newCarDesc").trim() : "";

            if (cPlate.isEmpty() || cBrand.isEmpty() || cModel.isEmpty()) {
                errorMsg = "Vui lòng nhập đầy đủ Biển số xe, Hãng xe và Mẫu xe.";
                showNewCarBox = true;
                currentStep = 2;
            } else if (carDAO.getCar(cPlate) != null) {
                errorMsg = "Biển số xe " + cPlate + " đã tồn tại trong hệ thống.";
                showNewCarBox = true;
                currentStep = 2;
            } else {
                int cYear = 2022;
                if (!cYearStr.isEmpty()) {
                    try { cYear = Integer.parseInt(cYearStr); } catch (NumberFormatException ignored) {}
                }
                Car3 newCar = new Car3(0, cPlate, cBrand, cModel, cYear, cDesc, selectedCustomer.getId());
                if (carDAO.saveCar(newCar)) {
                    selectedCar = newCar;
                    selectedAppointment = null;
                    selectedServices.clear();
                    selectedParts.clear();
                    selectedTech = null;
                    session.setAttribute("rc_car", selectedCar);
                    session.removeAttribute("rc_appointment");
                    session.removeAttribute("rc_technician");
                    currentStep = 3;
                    infoMsg = "Đã thêm xe: " + cBrand + " " + cModel + " (" + cPlate + ")";
                } else {
                    errorMsg = "Không thể lưu thông tin xe mới.";
                    showNewCarBox = true;
                }
            }
        }

        // --- XU LY CAC HANH DONG CHINH ---
        if ("selectCustomer".equals(act) && targetId > 0) {
            Customer3 c = customerDAO.getCustomerById(targetId);
            if (c != null) {
                selectedCustomer = c;
                selectedCar = null;
                selectedAppointment = null;
                selectedServices.clear();
                selectedParts.clear();
                selectedTech = null;
                session.setAttribute("rc_customer", selectedCustomer);
                session.removeAttribute("rc_car");
                session.removeAttribute("rc_appointment");
                session.removeAttribute("rc_technician");
                currentStep = 2;
            }
        } else if ("selectCar".equals(act) && targetId > 0) {
            Car3 car = carDAO.getCarById(targetId);
            if (car != null && selectedCustomer != null && car.getCustomerId() == selectedCustomer.getId()) {
                selectedCar = car;
                session.setAttribute("rc_car", selectedCar);
                selectedServices.clear();
                selectedParts.clear();
                selectedTech = null;
                session.removeAttribute("rc_technician");

                Appointment3 app = appointmentDAO.getAppointment(car.getId());
                session.setAttribute("rc_appointment", app);
                selectedAppointment = app;
                if (app != null && app.getAppointmentServices() != null) {
                    for (AppointmentServiceDetail3 d : app.getAppointmentServices()) {
                        if (d != null && d.getService() != null && ServiceDAO3.isOfferedService(d.getService().getId())) {
                            Service3 service = d.getService();
                            String note = getBookedServiceDetails(app.getNote(), service.getName());
                            selectedServices.add(new RepairServiceDetail3(0, getBookedServicePrice(service, note), note, service, null));
                        }
                    }
                    infoMsg = "Lịch hẹn trước: " +
                            (app.getAppointmentDate() != null ? dfShow.format(app.getAppointmentDate()) : "") +
                            " - " + (app.getAppointmentTime() != null ? app.getAppointmentTime() : "") +
                            " (đã nạp " + selectedServices.size() + " dịch vụ)";
                }
                currentStep = 3;
            }
        } else if (("addService".equals(act) || "saveServiceDetails".equals(act)) && ServiceDAO3.isOfferedService(targetId)
                && selectedCustomer != null && selectedCar != null) {
            Service3 service = serviceDAO.getServiceById(targetId);
            if (service != null) {
                RepairServiceDetail3 selected = null;
                for (RepairServiceDetail3 detail : selectedServices) {
                    if (detail.getService().getId() == targetId) {
                        selected = detail;
                        break;
                    }
                }
                boolean editing = "saveServiceDetails".equals(act);
                String note = editing && request.getParameter("serviceDetailNote") != null
                        ? request.getParameter("serviceDetailNote").trim() : "";
                if (note.codePointCount(0, note.length()) > 255) {
                    errorMsg = "Chi tiết dịch vụ dài quá 255 ký tự. Vui lòng rút gọn.";
                } else if (selected != null && !editing) {
                    errorMsg = "Dịch vụ '" + service.getName() + "' đã có trong danh sách đã chọn.";
                } else {
                    try {
                        float price = ServiceCatalog3.price(service, note);
                        if (selected == null) {
                            selectedServices.add(new RepairServiceDetail3(0, price, note, service, selectedTech));
                            infoMsg = "Đã thêm dịch vụ: " + service.getName();
                        } else {
                            selected.setNote(note);
                            selected.setPrice(price);
                            infoMsg = "Đã cập nhật chi tiết dịch vụ: " + service.getName();
                        }
                    } catch (IllegalArgumentException ex) {
                        errorMsg = "Hạng mục dịch vụ không hợp lệ. Vui lòng chọn lại.";
                    }
                }
            }
            currentStep = 3;
        } else if ("removeService".equals(act) && targetId >= 0 && targetId < selectedServices.size()) {
            selectedServices.remove(targetId);
            currentStep = 3;
        } else if ("addPart".equals(act) && targetId > 0) {
            String qtyParam = request.getParameter("inQuantity");
            SparePart3 p = sparePartDAO.getPartById(targetId);
            if (p != null) {
                int qty = -1;
                try {
                    qty = Integer.parseInt(qtyParam != null ? qtyParam.trim() : "");
                } catch (NumberFormatException ignored) {}

                if (qty <= 0) {
                    errorMsg = "Số lượng phụ tùng phải là số nguyên dương lớn hơn 0.";
                } else {
                    int alreadyQty = 0;
                    RepairSparePartDetail3 existingDet = null;
                    for (RepairSparePartDetail3 pd : selectedParts) {
                        if (pd.getSparePart().getId() == p.getId()) {
                            alreadyQty = pd.getQuantity();
                            existingDet = pd;
                            break;
                        }
                    }
                    if ((long) qty + alreadyQty > p.getQuantity()) {
                        errorMsg = "Số lượng phụ tùng '" + p.getName() + "' yêu cầu (" + (qty + alreadyQty)
                                + ") vượt quá tồn kho (" + p.getQuantity() + ").";
                    } else {
                        if (existingDet != null) {
                            existingDet.setQuantity(existingDet.getQuantity() + qty);
                        } else {
                            selectedParts.add(new RepairSparePartDetail3(0, qty, p.getSellingPrice(), p));
                        }
                        infoMsg = "Đã thêm phụ tùng: " + p.getName() + " (SL: " + qty + ")";
                    }
                }
            }
            currentStep = 3;
        } else if ("removePart".equals(act) && targetId >= 0 && targetId < selectedParts.size()) {
            selectedParts.remove(targetId);
            currentStep = 3;
        } else if ("assignAllTech".equals(act) && targetId > 0) {
            Technician3 tech = technicianDAO.getTechnicianById(targetId);
            if (tech != null) {
                selectedTech = tech;
                session.setAttribute("rc_technician", selectedTech);
                for (RepairServiceDetail3 rsd : selectedServices) {
                    rsd.setTechnician(tech);
                }
                infoMsg = "Đã phân công: " + (tech.getFullName() != null ? tech.getFullName().toString() : "");
            }
            currentStep = 4;
        }

        // --- CHUYEN TU BUOC 3 SANG BUOC 4 ---
        if (request.getParameter("subCompleteServicePart") != null) {
            if (selectedCustomer == null) {
                errorMsg = "Vui lòng chọn khách hàng.";
                currentStep = 1;
            } else if (selectedCar == null) {
                errorMsg = "Vui lòng chọn xe của khách hàng.";
                currentStep = 2;
            } else if (selectedServices.isEmpty() && selectedParts.isEmpty()) {
                errorMsg = "Vui lòng chọn ít nhất một dịch vụ hoặc phụ tùng.";
                currentStep = 3;
            } else if (hasUnpricedServices(selectedServices)) {
                errorMsg = "Vui lòng thêm ít nhất một hạng mục cho mỗi nhóm dịch vụ đã chọn.";
                currentStep = 3;
            } else if (selectedServices.isEmpty()) {
                RepairOrder3 order = buildRepairOrder(salesStaff, selectedCustomer, selectedCar,
                        selectedAppointment, selectedServices, selectedParts, 1);
                session.setAttribute("rc_repairOrder", order);
                session.setAttribute("rc_step", currentStep);
                response.sendRedirect("confirm-repair-order");
                return;
            } else {
                currentStep = 4;
            }
        }

        // --- BUOC 4 -> BUOC 5 XAC NHAN PHIEU ---
        if (request.getParameter("subComplete") != null) {
            if (selectedCustomer == null) {
                errorMsg = "Vui lòng chọn khách hàng.";
                currentStep = 1;
            } else if (selectedCar == null) {
                errorMsg = "Vui lòng chọn xe của khách hàng.";
                currentStep = 2;
            } else if (selectedServices.isEmpty() && selectedParts.isEmpty()) {
                errorMsg = "Vui lòng chọn ít nhất một dịch vụ hoặc phụ tùng.";
                currentStep = 3;
            } else if (hasUnpricedServices(selectedServices)) {
                errorMsg = "Vui lòng thêm ít nhất một hạng mục cho mỗi nhóm dịch vụ đã chọn.";
                currentStep = 3;
            } else {
                boolean missingTech = false;
                for (RepairServiceDetail3 rsd : selectedServices) {
                    if (rsd.getTechnician() == null) {
                        missingTech = true;
                        break;
                    }
                }
                if (missingTech) {
                    errorMsg = "Vui lòng phân công kỹ thuật viên cho tất cả các dịch vụ đã chọn.";
                    currentStep = 4;
                } else {
                    int deliveryDays = technicianDAO.isAllTechniciansBusy() ? 2 : 1;
                    RepairOrder3 order = buildRepairOrder(salesStaff, selectedCustomer, selectedCar,
                            selectedAppointment, selectedServices, selectedParts, deliveryDays);
                    session.setAttribute("rc_repairOrder", order);
                    session.setAttribute("rc_step", currentStep);
                    response.sendRedirect("confirm-repair-order");
                    return;
                }
            }
        }

        session.setAttribute("rc_step", currentStep);

        boolean showAppointmentQueue = currentStep == 1 && kwCustomer.trim().isEmpty();
        Map<Customer3, Appointment3> appointmentQueue = showAppointmentQueue
                ? customerDAO.getCustomersWithPendingAppointments()
                : Collections.<Customer3, Appointment3>emptyMap();
        Customer3[] customerList = showAppointmentQueue
                ? appointmentQueue.keySet().toArray(new Customer3[0])
                : (currentStep == 1 ? customerDAO.getCustomer(kwCustomer) : new Customer3[0]);
        int queuePageSize = 10;
        int queuePages = Math.max(1, (customerList.length + queuePageSize - 1) / queuePageSize);
        int queuePage = 1;
        if (showAppointmentQueue && request.getParameter("queuePage") != null) {
            try {
                queuePage = Math.max(1, Math.min(queuePages, Integer.parseInt(request.getParameter("queuePage"))));
            } catch (NumberFormatException ignored) {}
        }
        int firstCustomer = showAppointmentQueue ? (queuePage - 1) * queuePageSize : 0;
        int lastCustomer = showAppointmentQueue ? Math.min(customerList.length, firstCustomer + queuePageSize) : customerList.length;

        Car3[] carList = (currentStep == 2 && selectedCustomer != null) ? carDAO.getCar(selectedCustomer.getId()) : new Car3[0];
        if (currentStep == 2 && carList.length == 0) showNewCarBox = true;

        Service3[] serviceList = (currentStep == 3) ? serviceDAO.getOfferedServices(kwService) : new Service3[0];
        SparePart3[] partList = (currentStep == 3) ? sparePartDAO.getPart(kwPart) : new SparePart3[0];
        int visibleParts = kwPart.trim().isEmpty() ? Math.min(5, partList.length) : partList.length;

        Technician3[] techList = (currentStep == 4) ? technicianDAO.getTechnician(kwTech) : new Technician3[0];
        Technician3[] allTechs = (currentStep == 4) ? technicianDAO.getTechnician("") : new Technician3[0];
        boolean allTechniciansBusy = (currentStep == 4) && technicianDAO.isAllTechniciansBusy();

        float subtotal = 0;
        for (RepairServiceDetail3 rsd : selectedServices) subtotal += rsd.getPrice();
        for (RepairSparePartDetail3 rpd : selectedParts) subtotal += rpd.getPrice() * rpd.getQuantity();

        request.setAttribute("salesStaff", salesStaff);
        request.setAttribute("selectedCustomer", selectedCustomer);
        request.setAttribute("selectedCar", selectedCar);
        request.setAttribute("selectedAppointment", selectedAppointment);
        request.setAttribute("selectedTech", selectedTech);
        request.setAttribute("selectedServices", selectedServices);
        request.setAttribute("selectedParts", selectedParts);
        request.setAttribute("currentStep", Integer.valueOf(currentStep));
        request.setAttribute("kwCustomer", kwCustomer);
        request.setAttribute("kwService", kwService);
        request.setAttribute("kwPart", kwPart);
        request.setAttribute("kwTech", kwTech);
        request.setAttribute("errorMsg", errorMsg);
        request.setAttribute("infoMsg", infoMsg);
        request.setAttribute("showNewCustomerBox", Boolean.valueOf(showNewCustomerBox));
        request.setAttribute("editingCustomer", editingCustomer);
        request.setAttribute("nName", nName);
        request.setAttribute("nPhone", nPhone);
        request.setAttribute("nEmail", nEmail);
        request.setAttribute("nAddr", nAddr);
        request.setAttribute("showNewCarBox", Boolean.valueOf(showNewCarBox));
        request.setAttribute("showAppointmentQueue", Boolean.valueOf(showAppointmentQueue));
        request.setAttribute("appointmentQueue", appointmentQueue);
        request.setAttribute("customerList", customerList);
        request.setAttribute("queuePage", Integer.valueOf(queuePage));
        request.setAttribute("queuePages", Integer.valueOf(queuePages));
        request.setAttribute("firstCustomer", Integer.valueOf(firstCustomer));
        request.setAttribute("lastCustomer", Integer.valueOf(lastCustomer));
        request.setAttribute("carList", carList);
        request.setAttribute("serviceList", serviceList);
        request.setAttribute("partList", partList);
        request.setAttribute("visibleParts", Integer.valueOf(visibleParts));
        request.setAttribute("techList", techList);
        request.setAttribute("allTechs", allTechs);
        request.setAttribute("allTechniciansBusy", Boolean.valueOf(allTechniciansBusy));
        request.setAttribute("subtotal", Float.valueOf(subtotal));
        request.setAttribute("customerNote", getCustomerNote(selectedAppointment));
        request.setAttribute("serviceCatalogJson", ServiceCatalog3.forScreen(true));
        request.setAttribute("fromController", Boolean.TRUE);
        request.getRequestDispatcher("/ReceiveCarScreen3.jsp").forward(request, response);
    }

    private String getStrAttr(HttpSession session, String key) {
        String val = (String) session.getAttribute(key);
        return val != null ? val : "";
    }
}
