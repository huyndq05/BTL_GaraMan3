package controller;

import dao.RepairOrderDAO3;
import model.Appointment3;
import model.Car3;
import model.Customer3;
import model.RepairOrder3;
import model.RepairServiceDetail3;
import model.RepairSparePartDetail3;
import model.SalesStaff3;

import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ConfirmRepairOrderController extends HttpServlet {

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

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        SalesStaff3 loggedStaff = (SalesStaff3) session.getAttribute("salesStaff");
        if (loggedStaff == null) {
            response.sendRedirect("login");
            return;
        }

        RepairOrderDAO3 repairOrderDAO = new RepairOrderDAO3();
        SimpleDateFormat dfIso = new SimpleDateFormat("yyyy-MM-dd");

        RepairOrder3 repairOrder = (RepairOrder3) session.getAttribute("rc_repairOrder");
        boolean savedOk = false;
        String errorMsg = null;

        if (request.getParameter("subBack") != null) {
            RepairServiceDetail3[] backServices = (repairOrder != null) ? repairOrder.getRepairServices() : null;
            int backStep = (backServices != null && backServices.length > 0) ? 4 : 3;
            response.sendRedirect("receive-car?step=" + backStep);
            return;
        }

        if ("POST".equalsIgnoreCase(request.getMethod())
                && request.getParameter("subConfirm") != null
                && repairOrder != null) {
            String expDateStr = request.getParameter("expectedDate");
            if (expDateStr != null && !expDateStr.trim().isEmpty()) {
                try {
                    repairOrder.setCompletedDate(dfIso.parse(expDateStr.trim()));
                } catch (Exception ignored) {}
            }
            String orderNote = request.getParameter("orderNote");
            if (orderNote != null) {
                repairOrder.setNote(orderNote.trim());
            }
            repairOrder.setStatus("Đang sửa chữa");

            Appointment3 appointment = (Appointment3) session.getAttribute("rc_appointment");
            boolean ok = repairOrderDAO.createRepairOrder(repairOrder, appointment);
            if (ok) {
                session.removeAttribute("rc_customer");
                session.removeAttribute("rc_car");
                session.removeAttribute("rc_appointment");
                session.removeAttribute("rc_services");
                session.removeAttribute("rc_parts");
                session.removeAttribute("rc_technician");
                session.removeAttribute("rc_repairOrder");
                session.removeAttribute("rc_step");
                savedOk = true;
            } else {
                errorMsg = "Không thể lưu phiếu nhận xe. Vui lòng kiểm tra tồn kho và trạng thái lịch hẹn.";
            }
        }

        if (!savedOk && repairOrder == null) {
            response.sendRedirect("receive-car");
            return;
        }

        Customer3 customer = (repairOrder != null) ? repairOrder.getCustomer() : null;
        Car3 car = (repairOrder != null) ? repairOrder.getCar() : null;
        SalesStaff3 salesStaff = (repairOrder != null && repairOrder.getSalesStaff() != null)
                ? repairOrder.getSalesStaff() : loggedStaff;
        RepairServiceDetail3[] serviceDetails = (repairOrder != null && repairOrder.getRepairServices() != null)
                ? repairOrder.getRepairServices() : new RepairServiceDetail3[0];
        RepairSparePartDetail3[] partDetails = (repairOrder != null && repairOrder.getRepairSpareParts() != null)
                ? repairOrder.getRepairSpareParts() : new RepairSparePartDetail3[0];

        int nextCode = repairOrderDAO.getNextOrderNumber();
        float totalAmount = (repairOrder != null) ? repairOrder.getTotalAmount() : 0;

        request.setAttribute("loggedStaff", loggedStaff);
        request.setAttribute("repairOrder", repairOrder);
        request.setAttribute("customer", customer);
        request.setAttribute("car", car);
        request.setAttribute("salesStaff", salesStaff);
        request.setAttribute("serviceDetails", serviceDetails);
        request.setAttribute("partDetails", partDetails);
        request.setAttribute("nextCode", Integer.valueOf(nextCode));
        request.setAttribute("totalAmount", Float.valueOf(totalAmount));
        request.setAttribute("savedOk", Boolean.valueOf(savedOk));
        request.setAttribute("errorMsg", errorMsg);
        request.setAttribute("fromController", Boolean.TRUE);
        request.getRequestDispatcher("/ConfirmRepairOrderScreen3.jsp").forward(request, response);
    }
}
