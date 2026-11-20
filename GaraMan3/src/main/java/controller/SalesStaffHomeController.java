package controller;

import model.SalesStaff3;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SalesStaffHomeController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        SalesStaff3 salesStaff = (SalesStaff3) session.getAttribute("salesStaff");
        if (salesStaff == null) {
            response.sendRedirect("login");
            return;
        }

        if ("reset".equals(request.getParameter("action"))) {
            clearReceiveCarSession(session);
        }

        request.setAttribute("salesStaff", salesStaff);
        request.setAttribute("fromController", Boolean.TRUE);
        request.getRequestDispatcher("/SalesStaffHomeScreen3.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public static void clearReceiveCarSession(HttpSession session) {
        session.removeAttribute("rc_customer");
        session.removeAttribute("rc_car");
        session.removeAttribute("rc_appointment");
        session.removeAttribute("rc_services");
        session.removeAttribute("rc_parts");
        session.removeAttribute("rc_technician");
        session.removeAttribute("rc_repairOrder");
        session.removeAttribute("rc_step");
        session.removeAttribute("rc_kwCustomer");
        session.removeAttribute("rc_kwService");
        session.removeAttribute("rc_kwPart");
        session.removeAttribute("rc_kwTech");
    }
}
