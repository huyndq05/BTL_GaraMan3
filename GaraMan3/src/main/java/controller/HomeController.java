package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HomeController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        if ("reset".equals(request.getParameter("action"))) {
            clearBookingSession(session);
        }

        request.setAttribute("fromController", Boolean.TRUE);
        request.getRequestDispatcher("/HomeScreen3.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public static void clearBookingSession(HttpSession session) {
        session.removeAttribute("appointment");
        session.removeAttribute("customer");
        session.removeAttribute("car");
        session.removeAttribute("selectedServiceIds");
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
        session.removeAttribute("bk_serviceDetails");
        session.removeAttribute("bk_selectedDetails");
        session.removeAttribute("bk_calOffset");
    }
}
