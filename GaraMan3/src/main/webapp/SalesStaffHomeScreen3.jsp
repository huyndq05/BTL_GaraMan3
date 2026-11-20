<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.SalesStaff3" %>
<%
    if (request.getAttribute("fromController") == null) {
        response.sendRedirect("sales-home");
        return;
    }
    SalesStaff3 salesStaff = (SalesStaff3) request.getAttribute("salesStaff");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Trang chủ nhân viên bán hàng</title>
    <link rel="stylesheet" href="assets/style.css">
</head>
<body>
    <div class="top-nav">
        <span>Hệ thống GaraMan - NV: <%= salesStaff.getFullName() != null ? salesStaff.getFullName().toString() : salesStaff.getUsername() %></span>
        <div>
            <a href="home">Trang chủ</a>
            <a href="change-password">Đổi mật khẩu</a>
            <a href="login?action=logout">Đăng xuất</a>
        </div>
    </div>

    <div class="screen-box-sm" style="max-width: 520px;">
        <div class="screen-title">Trang chủ nhân viên bán hàng</div>

        <div class="btn-group-center">
            <a href="receive-car?init=1" class="btn" style="min-width: 170px;">Nhận xe vào sửa</a>
            <a href="change-password" class="btn" style="min-width: 170px;">Đổi mật khẩu</a>
        </div>
    </div>
</body>
</html>
