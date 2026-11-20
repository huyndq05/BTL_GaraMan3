<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (request.getAttribute("fromController") == null) {
        response.sendRedirect("login");
        return;
    }
    String error = (String) request.getAttribute("error");
    String errUser = (String) request.getAttribute("errUser");
    if (errUser == null) errUser = "";
    String valUser = (String) request.getAttribute("valUser");
    if (valUser == null) valUser = "";
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập tài khoản</title>
    <link rel="stylesheet" href="assets/style.css">
    <script>
        function validateLogin() {
            var u = document.getElementById("inUsername").value.trim();
            var p = document.getElementById("inPassword").value.trim();
            var errEl = document.getElementById("err_inUsername");
            var okUser = true;
            if (u.length > 0 && !/^[a-zA-Z0-9_]{3,30}$/.test(u)) {
                errEl.textContent = "Tài khoản chỉ gồm chữ cái, chữ số, dấu gạch dưới (3-30 ký tự).";
                okUser = false;
            } else {
                errEl.textContent = "";
            }
            document.getElementById("btnLogin").disabled = !(okUser && u.length >= 3 && p.length > 0);
        }
        window.addEventListener("DOMContentLoaded", validateLogin);
    </script>
</head>
<body>
    <div class="top-nav">
        <span>Hệ thống GaraMan</span>
        <div>
            <a href="home">Trang chủ</a>
        </div>
    </div>

    <div class="screen-box-sm">
        <div class="screen-title">Đăng nhập</div>

        <% if (error != null) { %>
            <div class="notice-box"><%= error %></div>
        <% } %>

        <form action="login" method="post">
            <div class="field-group">
                <div class="field-row">
                    <label for="inUsername">Tài khoản *:</label>
                    <input type="text" id="inUsername" name="inUsername" class="input-box" value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(valUser) %>" oninput="validateLogin()">
                </div>
                <div id="err_inUsername" class="field-error"><%= errUser %></div>
            </div>

            <div class="field-row">
                <label for="inPassword">Mật khẩu *:</label>
                <input type="password" id="inPassword" name="inPassword" class="input-box" value="" oninput="validateLogin()">
            </div>

            <div class="btn-group-center">
                <a href="home" class="btn">Về trang chủ</a>
                <button type="submit" id="btnLogin" name="subLogin" value="1" class="btn" disabled>Đăng nhập</button>
            </div>
        </form>
    </div>
</body>
</html>
