<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    if (request.getAttribute("fromController") == null) {
        response.sendRedirect("confirm-appointment");
        return;
    }
    SimpleDateFormat dfShow = new SimpleDateFormat("dd/MM/yyyy");
    Customer3 customer = (Customer3) request.getAttribute("customer");
    Car3 car = (Car3) request.getAttribute("car");
    Appointment3 appointment = (Appointment3) request.getAttribute("appointment");
    AppointmentServiceDetail3[] details = (AppointmentServiceDetail3[]) request.getAttribute("details");
    if (details == null) details = new AppointmentServiceDetail3[0];
    SalesStaff3 loggedStaff = (SalesStaff3) request.getAttribute("loggedStaff");
    boolean bookedOk = Boolean.TRUE.equals(request.getAttribute("bookedOk"));
    String bookingError = (String) request.getAttribute("bookingError");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Xác nhận đặt lịch hẹn</title>
    <link rel="stylesheet" href="assets/style.css">
</head>
<body>
    <div class="top-nav">
        <span>Hệ thống GaraMan</span>
        <div>
            <a href="home">Trang chủ</a>
            <% if (loggedStaff != null) { %>
                <a href="sales-home">Tài khoản</a>
            <% } else { %>
                <a href="login">Tài khoản</a>
            <% } %>
        </div>
    </div>

    <div class="screen-box" style="max-width: 820px;">
        <div class="screen-title">Xác nhận đặt lịch hẹn</div>

        <% if (bookedOk) { %>
            <div class="notice-box" style="text-align: center; padding: 24px 16px;">
                <div style="font-size: 16px; font-weight: bold; margin-bottom: 16px;">Đặt lịch hẹn thành công!</div>
                <a href="home?action=reset" class="btn" style="min-width: 120px;">OK</a>
            </div>
        <% } else { %>
            <div style="padding: 0 12px;">
                <% if (bookingError != null) { %>
                    <div class="notice-box" role="alert"><%= bookingError %></div>
                <% } %>
                <div class="info-line">
                    <span>Khách hàng: <%= customer.getFullName() != null ? customer.getFullName().toString() : "" %></span>
                    <span>SĐT: <%= customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "" %></span>
                    <span>Email: <%= customer.getEmail() != null ? customer.getEmail() : "" %></span>
                </div>
                <div class="info-line">
                    <span>Địa chỉ: <%= org.apache.taglibs.standard.functions.Functions.escapeXml(
                            customer.getAddress() == null ? "" : customer.getAddress().toString()) %></span>
                </div>
                <div class="info-line" style="margin-bottom: 12px;">
                    <span>Xe: <%= car.getBrand() %> <%= car.getModel() %> (<%= car.getPlateNumber() %>)</span>
                    <span>Ngày hẹn: <%= appointment.getAppointmentDate() != null ? dfShow.format(appointment.getAppointmentDate()) : "" %></span>
                    <span>Giờ hẹn: <%= appointment.getAppointmentTime() != null ? appointment.getAppointmentTime() : "" %></span>
                </div>
                <% if (appointment.getNote() != null && !appointment.getNote().trim().isEmpty()) { %>
                <div class="info-line" style="margin-bottom: 12px;">
                    <span><%= org.apache.taglibs.standard.functions.Functions.escapeXml(appointment.getNote()) %></span>
                </div>
                <% } %>

                <table id="outAppointmentDetail" class="table">
                    <thead>
                        <tr>
                            <th style="width: 50px;">TT</th>
                            <th style="width: 100px;">Mã DV</th>
                            <th>Tên dịch vụ đã chọn</th>
                            <th style="width: 130px;">Thời lượng</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            for (int i = 0; i < details.length; i++) {
                                Service3 s = details[i].getService();
                                if (s == null) continue;
                        %>
                        <tr>
                            <td><%= i + 1 %></td>
                            <td><%= String.format("DV%02d", s.getId()) %></td>
                            <td><%= s.getName() %></td>
                            <td><%= s.getDuration() %> phút</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>

                <form action="confirm-appointment" method="post" class="btn-group-center">
                    <button type="submit" name="subBack" value="1" class="btn">Quay lại</button>
                    <button type="submit" name="subConfirm" value="1" class="btn">Xác nhận</button>
                </form>
            </div>
        <% } %>
    </div>
</body>
</html>
