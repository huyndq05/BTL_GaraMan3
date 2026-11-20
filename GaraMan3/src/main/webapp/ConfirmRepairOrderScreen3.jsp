<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%
    if (request.getAttribute("fromController") == null) {
        response.sendRedirect("confirm-repair-order");
        return;
    }
    SimpleDateFormat dfShow = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dfIso = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat priceFormat = new DecimalFormat("#,###");

    SalesStaff3 loggedStaff = (SalesStaff3) request.getAttribute("loggedStaff");
    RepairOrder3 repairOrder = (RepairOrder3) request.getAttribute("repairOrder");
    Customer3 customer = (Customer3) request.getAttribute("customer");
    Car3 car = (Car3) request.getAttribute("car");
    SalesStaff3 salesStaff = (SalesStaff3) request.getAttribute("salesStaff");
    RepairServiceDetail3[] serviceDetails = (RepairServiceDetail3[]) request.getAttribute("serviceDetails");
    if (serviceDetails == null) serviceDetails = new RepairServiceDetail3[0];
    RepairSparePartDetail3[] partDetails = (RepairSparePartDetail3[]) request.getAttribute("partDetails");
    if (partDetails == null) partDetails = new RepairSparePartDetail3[0];
    int nextCode = ((Integer) request.getAttribute("nextCode")).intValue();
    float totalAmount = ((Float) request.getAttribute("totalAmount")).floatValue();
    boolean savedOk = Boolean.TRUE.equals(request.getAttribute("savedOk"));
    String errorMsg = (String) request.getAttribute("errorMsg");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Xác nhận phiếu nhận xe</title>
    <link rel="stylesheet" href="assets/style.css">
</head>
<body>
    <div class="top-nav">
        <span>Hệ thống GaraMan - NV: <%= loggedStaff.getFullName() != null ? loggedStaff.getFullName().toString() : loggedStaff.getUsername() %></span>
        <div>
            <a href="sales-home">Trang chủ NV</a>
            <a href="change-password">Đổi mật khẩu</a>
            <a href="login?action=logout">Đăng xuất</a>
        </div>
    </div>

    <div class="screen-box" style="max-width: 920px;">
        <div class="screen-title">Xác nhận phiếu nhận xe</div>

        <% if (savedOk) { %>
            <div class="notice-box" style="text-align: center; padding: 24px 16px;">
                <div style="font-size: 16px; font-weight: bold; margin-bottom: 16px;">Nhận xe vào sửa thành công!</div>
                <a href="sales-home?action=reset" class="btn" style="min-width: 120px;">OK</a>
            </div>
        <% } else { %>
            <div class="step-bar">
                <div class="step-item done"><a href="receive-car?step=1">1. Khách hàng</a></div>
                <div class="step-item done"><a href="receive-car?step=2">2. Chọn xe</a></div>
                <div class="step-item done"><a href="receive-car?step=3">3. Dịch vụ &amp; Phụ tùng</a></div>
                <div class="step-item done"><a href="receive-car?step=4">4. Phân công KTV</a></div>
                <div class="step-item active"><span>5. Xác nhận</span></div>
            </div>

            <% if (errorMsg != null) { %>
                <div class="notice-box"><%= errorMsg %></div>
            <% } %>

            <form action="confirm-repair-order" method="post" style="padding: 0 8px;">
                <div class="info-line">
                    <span>Mã phiếu: <strong><%= String.format("RO%03d", nextCode) %></strong></span>
                    <span>Ngày nhận: <%= repairOrder.getCreatedDate() != null ? dfShow.format(repairOrder.getCreatedDate()) : "" %></span>
                    <span>Trạng thái: <strong><%= repairOrder.getStatus() != null ? repairOrder.getStatus() : "Đang sửa chữa" %></strong></span>
                    <span>NV bán hàng: <%= salesStaff != null && salesStaff.getFullName() != null ? salesStaff.getFullName().toString() : "" %></span>
                </div>
                <div class="info-line" style="margin-bottom: 12px;">
                    <span>Khách hàng: <%= customer != null && customer.getFullName() != null ? customer.getFullName().toString() : "" %> (<%= customer != null ? customer.getPhoneNumber() : "" %>)</span>
                    <span>Xe: <%= car != null ? car.getBrand() + " " + car.getModel() + " (" + car.getPlateNumber() + ")" : "" %></span>
                </div>

                <div class="grid-2" style="margin-bottom: 14px;">
                    <div class="col-half">
                        <div class="field-row" style="margin-bottom: 0;">
                            <label for="expectedDate" style="width: 145px;">Ngày giao dự kiến:</label>
                            <input type="date" id="expectedDate" name="expectedDate" class="input-box"
                                   value="<%= repairOrder.getCompletedDate() != null ? dfIso.format(repairOrder.getCompletedDate()) : "" %>"
                                   min="<%= dfIso.format(new Date()) %>">
                        </div>
                    </div>
                    <div class="col-half">
                        <div class="field-row" style="margin-bottom: 0;">
                            <label for="orderNote" style="width: 80px;">Ghi chú:</label>
                            <input type="text" id="orderNote" name="orderNote" class="input-box"
                                   value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(repairOrder.getNote() != null ? repairOrder.getNote() : "") %>">
                        </div>
                    </div>
                </div>

                <table id="outRepairOrder" class="table">
                    <thead>
                        <tr>
                            <th style="width: 45px;">TT</th>
                            <th>Tên dịch vụ / phụ tùng</th>
                            <th style="width: 90px;">Loại</th>
                            <th style="width: 50px;">SL</th>
                            <th style="width: 115px;">Đơn giá</th>
                            <th style="width: 115px;">Thành tiền</th>
                            <th style="width: 140px;">KTV phụ trách</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            int tt = 1;
                            for (RepairServiceDetail3 sd : serviceDetails) {
                                if (sd == null || sd.getService() == null) continue;
                                String techName = (sd.getTechnician() != null && sd.getTechnician().getFullName() != null)
                                        ? sd.getTechnician().getFullName().toString() : "-";
                        %>
                        <tr>
                            <td><%= tt++ %></td>
                            <td>
                                <%= org.apache.taglibs.standard.functions.Functions.escapeXml(sd.getService().getName()) %>
                                <% if (sd.getNote() != null && !sd.getNote().isEmpty()) { %>
                                    <div class="service-detail-note"><%= org.apache.taglibs.standard.functions.Functions.escapeXml(sd.getNote()) %></div>
                                <% } %>
                            </td>
                            <td>Dịch vụ</td>
                            <td>1</td>
                            <td><%= priceFormat.format(sd.getPrice()).replace(',', '.') %></td>
                            <td><%= priceFormat.format(sd.getPrice()).replace(',', '.') %></td>
                            <td><%= techName %></td>
                        </tr>
                        <%
                            }
                            for (RepairSparePartDetail3 pd : partDetails) {
                                if (pd == null || pd.getSparePart() == null) continue;
                                float subTotal = pd.getPrice() * pd.getQuantity();
                        %>
                        <tr>
                            <td><%= tt++ %></td>
                            <td><%= pd.getSparePart().getName() %></td>
                            <td>Phụ tùng</td>
                            <td><%= pd.getQuantity() %></td>
                            <td><%= priceFormat.format(pd.getPrice()).replace(',', '.') %></td>
                            <td><%= priceFormat.format(subTotal).replace(',', '.') %></td>
                            <td>-</td>
                        </tr>
                        <% } %>
                        <tr>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td>Tổng tạm tính:</td>
                            <td><%= priceFormat.format(totalAmount).replace(',', '.') %></td>
                            <td></td>
                        </tr>
                    </tbody>
                </table>

                <div class="btn-group-center">
                    <button type="submit" name="subBack" value="1" class="btn">Quay lại</button>
                    <button type="submit" name="subConfirm" value="1" class="btn">Xác nhận</button>
                </div>
            </form>
        <% } %>
    </div>
</body>
</html>
