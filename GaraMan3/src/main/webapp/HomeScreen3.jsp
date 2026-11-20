<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.SalesStaff3" %>
<%
    if (request.getAttribute("fromController") == null) {
        response.sendRedirect("home");
        return;
    }
    SalesStaff3 loggedStaff = (SalesStaff3) session.getAttribute("salesStaff");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Trang chủ</title>
    <link rel="stylesheet" href="assets/style.css">
</head>
<body>
    <div class="top-nav">
        <span>Hệ thống GaraMan</span>
        <div>
            <% if (loggedStaff != null) { %>
                <a href="sales-home">Tài khoản (<%= loggedStaff.getFullName() != null ? loggedStaff.getFullName().toString() : loggedStaff.getUsername() %>)</a>
            <% } else { %>
                <a href="login">Tài khoản</a>
            <% } %>
        </div>
    </div>

    <div class="screen-box-sm">
        <div class="screen-title">Trang chủ</div>

        <form action="book-appointment" method="get" class="btn-group-center">
            <input type="hidden" name="init" value="1">
            <button type="submit" name="subBookAppointment" class="btn" style="min-width: 170px;">
                Đặt lịch hẹn
            </button>
        </form>
    </div>
</body>
</html>
