<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.SalesStaff3" %>
<%
    if (request.getAttribute("fromController") == null) {
        response.sendRedirect("change-password");
        return;
    }
    SalesStaff3 salesStaff = (SalesStaff3) request.getAttribute("salesStaff");
    String errorMsg = (String) request.getAttribute("errorMsg");
    boolean changedOk = Boolean.TRUE.equals(request.getAttribute("changedOk"));
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đổi mật khẩu</title>
    <link rel="stylesheet" href="assets/style.css">
    <script>
        function validateChangePass() {
            var o = document.getElementById("inOldPassword").value.trim();
            var n = document.getElementById("inNewPassword").value.trim();
            var c = document.getElementById("inConfirmPassword").value.trim();
            var errEl = document.getElementById("err_confirm");
            if (c.length > 0 && n !== c) {
                errEl.textContent = "Mật khẩu xác nhận không khớp.";
            } else {
                errEl.textContent = "";
            }
            document.getElementById("btnSavePass").disabled = !(o.length > 0 && n.length > 0 && c.length > 0 && n === c);
        }
        window.addEventListener("DOMContentLoaded", function() {
            if (document.getElementById("inOldPassword")) validateChangePass();
        });
    </script>
</head>
<body>
    <div class="top-nav">
        <span>Hệ thống GaraMan - NV: <%= salesStaff.getFullName() != null ? salesStaff.getFullName().toString() : salesStaff.getUsername() %></span>
        <div>
            <a href="sales-home">Trang chủ NV</a>
            <a href="login?action=logout">Đăng xuất</a>
        </div>
    </div>

    <div class="screen-box-sm" style="max-width: 500px;">
        <div class="screen-title">Đổi mật khẩu</div>

        <% if (changedOk) { %>
            <div class="notice-box" style="text-align: center;">
                <div style="font-weight: bold; margin-bottom: 14px;">Đổi mật khẩu thành công!</div>
                <a href="sales-home" class="btn" style="min-width: 110px;">OK</a>
            </div>
        <% } else { %>
            <% if (errorMsg != null) { %>
                <div class="notice-box"><%= errorMsg %></div>
            <% } %>

            <form action="change-password" method="post">
                <div class="field-row">
                    <label for="inOldPassword" style="width: 155px;">Mật khẩu cũ *:</label>
                    <input type="password" id="inOldPassword" name="inOldPassword" class="input-box" value="" oninput="validateChangePass()">
                </div>
                <div class="field-row">
                    <label for="inNewPassword" style="width: 155px;">Mật khẩu mới *:</label>
                    <input type="password" id="inNewPassword" name="inNewPassword" class="input-box" value="" oninput="validateChangePass()">
                </div>
                <div class="field-group">
                    <div class="field-row">
                        <label for="inConfirmPassword" style="width: 155px;">Xác nhận mật khẩu *:</label>
                        <input type="password" id="inConfirmPassword" name="inConfirmPassword" class="input-box" value="" oninput="validateChangePass()">
                    </div>
                    <div id="err_confirm" class="field-error" style="margin-left: 155px;"></div>
                </div>

                <div class="btn-group-center">
                    <a href="sales-home" class="btn">Quay lại</a>
                    <button type="submit" id="btnSavePass" name="subSavePassword" value="1" class="btn" disabled>Lưu</button>
                </div>
            </form>
        <% } %>
    </div>
</body>
</html>
