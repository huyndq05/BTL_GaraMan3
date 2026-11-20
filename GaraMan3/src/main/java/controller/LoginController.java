package controller;

import dao.StaffDAO3;
import model.Staff3;

import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginController extends HttpServlet {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,30}$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        if ("logout".equals(request.getParameter("action"))) {
            session.removeAttribute("salesStaff");
            SalesStaffHomeController.clearReceiveCarSession(session);
        }

        request.setAttribute("valUser", "");
        request.setAttribute("errUser", "");
        request.setAttribute("fromController", Boolean.TRUE);
        request.getRequestDispatcher("/LoginScreen3.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        String u = request.getParameter("inUsername");
        String p = request.getParameter("inPassword");
        String valUser = (u != null) ? u.trim() : "";
        String error = null;
        String errUser = "";

        if (valUser.isEmpty() || p == null || p.trim().isEmpty()) {
            error = "Vui lòng nhập đầy đủ tài khoản và mật khẩu.";
        } else if (!USERNAME_PATTERN.matcher(valUser).matches()) {
            errUser = "Tài khoản không đúng định dạng (chỉ gồm chữ cái, chữ số, dấu gạch dưới).";
            error = "Không thể đăng nhập bằng tài khoản không đúng định dạng quy định.";
        } else {
            StaffDAO3 staffDAO = new StaffDAO3();
            Staff3 staff = staffDAO.checkLogin(valUser, p.trim());
            if (staff != null) {
                session.setAttribute("salesStaff", staff);
                response.sendRedirect("sales-home");
                return;
            } else {
                error = "Tên đăng nhập hoặc mật khẩu không chính xác.";
            }
        }

        request.setAttribute("error", error);
        request.setAttribute("errUser", errUser);
        request.setAttribute("valUser", valUser);
        request.setAttribute("fromController", Boolean.TRUE);
        request.getRequestDispatcher("/LoginScreen3.jsp").forward(request, response);
    }
}
