package controller;

import dao.StaffDAO3;
import model.SalesStaff3;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ChangePasswordController extends HttpServlet {

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

        request.setAttribute("salesStaff", salesStaff);
        request.setAttribute("changedOk", Boolean.FALSE);
        request.setAttribute("fromController", Boolean.TRUE);
        request.getRequestDispatcher("/ChangePasswordScreen3.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        SalesStaff3 salesStaff = (SalesStaff3) session.getAttribute("salesStaff");
        if (salesStaff == null) {
            response.sendRedirect("login");
            return;
        }

        String errorMsg = null;
        boolean changedOk = false;

        if (request.getParameter("subSavePassword") != null) {
            String oldPass = request.getParameter("inOldPassword") != null
                    ? request.getParameter("inOldPassword").trim() : "";
            String newPass = request.getParameter("inNewPassword") != null
                    ? request.getParameter("inNewPassword").trim() : "";
            String confirmPass = request.getParameter("inConfirmPassword") != null
                    ? request.getParameter("inConfirmPassword").trim() : "";

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                errorMsg = "Vui lòng nhập đầy đủ các trường mật khẩu.";
            } else if (!newPass.equals(confirmPass)) {
                errorMsg = "Xác nhận mật khẩu mới không trùng khớp.";
            } else if (newPass.equals(oldPass)) {
                errorMsg = "Mật khẩu mới phải khác mật khẩu cũ.";
            } else {
                StaffDAO3 staffDAO = new StaffDAO3();
                if (staffDAO.changePassword(salesStaff.getId(), oldPass, newPass)) {
                    salesStaff.setPassword(newPass);
                    changedOk = true;
                } else {
                    errorMsg = "Mật khẩu cũ không chính xác.";
                }
            }
        }

        request.setAttribute("salesStaff", salesStaff);
        request.setAttribute("errorMsg", errorMsg);
        request.setAttribute("changedOk", Boolean.valueOf(changedOk));
        request.setAttribute("fromController", Boolean.TRUE);
        request.getRequestDispatcher("/ChangePasswordScreen3.jsp").forward(request, response);
    }
}
