<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%!
    private String escape(String value) {
        return org.apache.taglibs.standard.functions.Functions.escapeXml(value == null ? "" : value);
    }
%>
<%
    if (request.getAttribute("fromController") == null) {
        response.sendRedirect("receive-car");
        return;
    }

    SimpleDateFormat dfShow = new SimpleDateFormat("dd/MM/yyyy");
    DecimalFormat priceFormat = new DecimalFormat("#,###");

    SalesStaff3 salesStaff = (SalesStaff3) request.getAttribute("salesStaff");
    Customer3 selectedCustomer = (Customer3) request.getAttribute("selectedCustomer");
    Car3 selectedCar = (Car3) request.getAttribute("selectedCar");
    Appointment3 selectedAppointment = (Appointment3) request.getAttribute("selectedAppointment");
    Technician3 selectedTech = (Technician3) request.getAttribute("selectedTech");
    @SuppressWarnings("unchecked")
    List<RepairServiceDetail3> selectedServices = (List<RepairServiceDetail3>) request.getAttribute("selectedServices");
    @SuppressWarnings("unchecked")
    List<RepairSparePartDetail3> selectedParts = (List<RepairSparePartDetail3>) request.getAttribute("selectedParts");
    int currentStep = ((Integer) request.getAttribute("currentStep")).intValue();

    String kwCustomer = (String) request.getAttribute("kwCustomer");
    String kwService = (String) request.getAttribute("kwService");
    String kwPart = (String) request.getAttribute("kwPart");
    String kwTech = (String) request.getAttribute("kwTech");

    String errorMsg = (String) request.getAttribute("errorMsg");
    String infoMsg = (String) request.getAttribute("infoMsg");
    boolean showNewCustomerBox = Boolean.TRUE.equals(request.getAttribute("showNewCustomerBox"));
    Customer3 editingCustomer = (Customer3) request.getAttribute("editingCustomer");
    String nName = (String) request.getAttribute("nName");
    String nPhone = (String) request.getAttribute("nPhone");
    String nEmail = (String) request.getAttribute("nEmail");
    String nAddr = (String) request.getAttribute("nAddr");
    boolean showNewCarBox = Boolean.TRUE.equals(request.getAttribute("showNewCarBox"));

    boolean showAppointmentQueue = Boolean.TRUE.equals(request.getAttribute("showAppointmentQueue"));
    @SuppressWarnings("unchecked")
    Map<Customer3, Appointment3> appointmentQueue = (Map<Customer3, Appointment3>) request.getAttribute("appointmentQueue");
    Customer3[] customerList = (Customer3[]) request.getAttribute("customerList");
    int queuePage = ((Integer) request.getAttribute("queuePage")).intValue();
    int queuePages = ((Integer) request.getAttribute("queuePages")).intValue();
    int firstCustomer = ((Integer) request.getAttribute("firstCustomer")).intValue();
    int lastCustomer = ((Integer) request.getAttribute("lastCustomer")).intValue();

    Car3[] carList = (Car3[]) request.getAttribute("carList");
    Service3[] serviceList = (Service3[]) request.getAttribute("serviceList");
    SparePart3[] partList = (SparePart3[]) request.getAttribute("partList");
    int visibleParts = ((Integer) request.getAttribute("visibleParts")).intValue();
    Technician3[] techList = (Technician3[]) request.getAttribute("techList");
    Technician3[] allTechs = (Technician3[]) request.getAttribute("allTechs");
    boolean allTechniciansBusy = Boolean.TRUE.equals(request.getAttribute("allTechniciansBusy"));
    float subtotal = ((Float) request.getAttribute("subtotal")).floatValue();
    String customerNote = (String) request.getAttribute("customerNote");
    if (customerNote == null) customerNote = "";
    String serviceCatalogJson = (String) request.getAttribute("serviceCatalogJson");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Nhận xe vào sửa</title>
    <link rel="stylesheet" href="assets/style.css">
    <script src="assets/service-details.js"></script>
    <script>
        var SERVICE_CATALOG = <%= serviceCatalogJson %>;

        function saveRepairDetails(sid) {
            var control = document.getElementById("mainSvc_" + sid);
            var note = serviceDetailText(sid);
            if (note === control.dataset.savedNote) return false;

            var error = document.getElementById("serviceDetailError");
            if (Array.from(note).length > 255) {
                error.textContent = "Chi tiết dịch vụ dài quá 255 ký tự. Vui lòng rút gọn.";
                return true;
            }
            document.getElementById("serviceDetailNote").value = note;
            doAction("saveServiceDetails", sid);
            return true;
        }
        function toggleRepairService(control) {
            var index = control.dataset.selectedIndex;
            doAction(control.checked ? "addService" : "removeService",
                    control.checked ? control.value : index);
        }

        function doAction(actName, idVal) {
            if (actName === "editCustomer") sessionStorage.removeItem("rc_scrollY");
            else sessionStorage.setItem("rc_scrollY", window.scrollY);
            document.getElementById("act").value = actName;
            document.getElementById("targetId").value = idVal;
            document.getElementById("receiveForm").submit();
        }
        function addPartWithQty(partId) {
            var qtyEl = document.getElementById("qty_part_" + partId);
            document.getElementById("inQuantity").value = qtyEl ? qtyEl.value : "1";
            doAction("addPart", partId);
        }
        function toggleBox(boxId) {
            var box = document.getElementById(boxId);
            if (!box) return;
            box.style.display = (box.style.display === "none" || box.style.display === "") ? "block" : "none";
        }
        function clearCustomerForm() {
            var box = document.getElementById("newCustomerBox");
            box.querySelectorAll("input").forEach(function(input) { input.value = ""; });
            box.querySelector(".sub-heading").textContent = "Thêm khách hàng mới";
            box.querySelector("[name=saveNewCustomer]").textContent = "Lưu khách hàng";
            box.style.display = "none";
        }

        function goStep(stepNum) {
            window.location.href = "receive-car?step=" + stepNum;
        }
        window.addEventListener("DOMContentLoaded", function() {
            document.querySelectorAll("[data-saved-note]").forEach(function(control) {
                restoreServiceDetails(control.value, control.dataset.savedNote);
            });
            var y = sessionStorage.getItem("rc_scrollY");
            if (y !== null) {
                window.scrollTo(0, parseInt(y, 10) || 0);
                sessionStorage.removeItem("rc_scrollY");
            }
        });
    </script>
</head>
<body>
    <div class="top-nav">
        <span>Hệ thống GaraMan - NV: <%= salesStaff.getFullName() != null ? salesStaff.getFullName().toString() : salesStaff.getUsername() %></span>
        <div>
            <a href="sales-home">Trang chủ NV</a>
            <a href="change-password">Đổi mật khẩu</a>
            <a href="login?action=logout">Đăng xuất</a>
        </div>
    </div>

    <div class="screen-box">
        <div class="screen-title">Nhận xe vào sửa</div>

        <div class="step-bar">
            <div class="step-item <%= currentStep == 1 ? "active" : "done" %>">
                <a href="receive-car?step=1">1. Khách hàng</a>
            </div>
            <div class="step-item <%= currentStep == 2 ? "active" : (selectedCustomer != null ? "done" : "") %>">
                <% if (selectedCustomer != null) { %>
                    <a href="receive-car?step=2">2. Chọn xe</a>
                <% } else { %>
                    <span>2. Chọn xe</span>
                <% } %>
            </div>
            <div class="step-item <%= currentStep == 3 ? "active" : (selectedCar != null ? "done" : "") %>">
                <% if (selectedCar != null) { %>
                    <a href="receive-car?step=3">3. Dịch vụ &amp; Phụ tùng</a>
                <% } else { %>
                    <span>3. Dịch vụ &amp; Phụ tùng</span>
                <% } %>
            </div>
            <div class="step-item <%= currentStep == 4 ? "active" : ((selectedCar != null && !selectedServices.isEmpty()) ? "done" : "") %>">
                <% if (selectedCar != null && !selectedServices.isEmpty()) { %>
                    <a href="receive-car?step=4">4. Phân công KTV</a>
                <% } else { %>
                    <span>4. Phân công KTV</span>
                <% } %>
            </div>
            <div class="step-item">
                <span>5. Xác nhận</span>
            </div>
        </div>

        <% if (errorMsg != null) { %>
            <div class="notice-box"><%= errorMsg %></div>
        <% } %>
        <% if (infoMsg != null) { %>
            <div class="notice-box"><%= infoMsg %></div>
        <% } %>

        <!-- Tom tat thong tin dang chon -->
        <% if (selectedCustomer != null) { %>
        <div class="summary-bar">
            <div>
                <strong>Khách hàng:</strong> <%= selectedCustomer.getFullName() != null ? selectedCustomer.getFullName().toString() : "" %>
                (<%= selectedCustomer.getPhoneNumber() %>)
                <% if (selectedCar != null) { %>
                    &nbsp;|&nbsp; <strong>Xe:</strong> <%= selectedCar.getBrand() %> <%= selectedCar.getModel() %> (<%= selectedCar.getPlateNumber() %>)
                <% } %>
                <% if (currentStep >= 3) { %>
                    &nbsp;|&nbsp; <strong>Hạng mục:</strong> <%= selectedServices.size() %> DV, <%= selectedParts.size() %> PT
                    &nbsp;|&nbsp; <strong>Tạm tính:</strong> <%= priceFormat.format(subtotal).replace(',', '.') %> VNĐ
                <% } %>
            </div>
            <div>
                <% if (currentStep > 1) { %>
                    <button type="button" class="btn-sm" onclick="goStep(1)">Đổi khách</button>
                <% } %>
                <% if (currentStep > 2 && selectedCustomer != null) { %>
                    <button type="button" class="btn-sm" onclick="goStep(2)">Đổi xe</button>
                <% } %>
            </div>
        </div>
        <% } %>

        <form id="receiveForm" action="receive-car" method="post">
            <input type="hidden" name="queuePage" value="<%= queuePage %>">
            <input type="hidden" id="act" name="act" value="">
            <input type="hidden" id="targetId" name="targetId" value="">
            <input type="hidden" id="inQuantity" name="inQuantity" value="1">

            <!-- ================= BUOC 1: KHACH HANG ================= -->
            <% if (currentStep == 1) { %>
            <div class="section-box">
                <div class="sub-heading"><%= showAppointmentQueue ? "Khách có lịch chờ tiếp nhận" : "Kết quả tìm khách hàng" %></div>

                <div class="search-bar">
                    <label for="inSearchCustomer">Tìm kiếm:</label>
                    <input type="text" id="inSearchCustomer" name="inSearchCustomer" class="input-box" style="width: 300px;"
                           value="<%= escape(kwCustomer) %>">
                    <button type="submit" name="btnSearchCustomer" value="1" class="btn-sm">Tìm</button>
                    <button type="button" name="subAddCustomer" class="btn-sm" style="min-width: 130px;" onclick="clearCustomerForm(); toggleBox('newCustomerBox')">
                        Thêm khách mới
                    </button>
                </div>

                <!-- Form them khach hang moi -->
                <div id="newCustomerBox" style="display: <%= showNewCustomerBox ? "block" : "none" %>; border: 1px solid #000; padding: 12px 16px; margin-bottom: 14px; background: #fafafa;">
                    <div class="sub-heading"><%= editingCustomer == null ? "Thêm khách hàng mới" : "Chỉnh sửa thông tin khách hàng" %></div>
                    <input type="hidden" name="editCustomerId" value="<%= editingCustomer == null ? "" : editingCustomer.getId() %>">
                    <div class="grid-2" style="margin-bottom: 8px;">
                        <div class="col-half">
                            <div class="field-row">
                                <label for="newCustName">Họ và tên (*):</label>
                                <input type="text" id="newCustName" name="newCustName" class="input-box" maxlength="255" value="<%= escape(nName) %>">
                            </div>
                            <div class="field-row">
                                <label for="newCustPhone">Số điện thoại (*):</label>
                                <input type="text" id="newCustPhone" name="newCustPhone" class="input-box" maxlength="11" value="<%= escape(nPhone) %>">
                            </div>
                        </div>
                        <div class="col-half">
                            <div class="field-row">
                                <label for="newCustEmail">Email:</label>
                                <input type="text" id="newCustEmail" name="newCustEmail" class="input-box" maxlength="255" value="<%= escape(nEmail) %>">
                            </div>
                            <div class="field-row">
                                <label for="newCustAddr">Địa chỉ (*):</label>
                                <input type="text" id="newCustAddr" name="newCustAddr" class="input-box" maxlength="255" value="<%= escape(nAddr) %>">
                            </div>
                        </div>
                    </div>
                    <div style="text-align: right;">
                        <button type="button" class="btn-sm" onclick="clearCustomerForm()">Hủy</button>
                        <button type="submit" name="saveNewCustomer" value="1" class="btn-sm" style="min-width: 120px;"><%= editingCustomer == null ? "Lưu khách hàng" : "Lưu thay đổi" %></button>
                    </div>
                </div>

                <table id="outsubCustomer" class="table">
                    <thead>
                        <tr>
                            <th style="width: 45px;">TT</th>
                            <th>Họ tên</th>
                            <th style="width: 125px;">Số điện thoại</th>
                            <th>Email</th>
                            <th>Địa chỉ</th>
                            <% if (showAppointmentQueue) { %>
                                <th>Ngày giờ hẹn</th>
                            <% } %>
                            <th style="width: 100px;">Chỉnh sửa</th>
                            <th style="width: 75px;">Chọn</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (customerList.length == 0) { %>
                        <tr>
                            <td colspan="<%= showAppointmentQueue ? 8 : 7 %>" style="text-align: center;">
                                <%= showAppointmentQueue ? "Chưa có lịch hẹn chờ tiếp nhận. Hãy tìm khách theo tên, số điện thoại hoặc biển số."
                                        : "Không tìm thấy khách hàng." %>
                            </td>
                        </tr>
                        <% } else {
                            for (int i = firstCustomer; i < lastCustomer; i++) {
                                Customer3 c = customerList[i];
                                boolean isSel = (selectedCustomer != null && selectedCustomer.getId() == c.getId());
                                String addrStr = (c.getAddress() != null && !c.getAddress().toString().isEmpty())
                                        ? c.getAddress().toString()
                                        : (c.getAddress() != null && c.getAddress().getProvince() != null ? c.getAddress().getProvince() : "");
                        %>
                        <tr class="<%= isSel ? "selected-row" : "" %>">
                            <td><%= i + 1 %></td>
                            <td><%= escape(c.getFullName() == null ? "" : c.getFullName().toString()) %></td>
                            <td><%= escape(c.getPhoneNumber()) %></td>
                            <td><%= escape(c.getEmail()) %></td>
                            <td><%= escape(addrStr) %></td>
                            <% if (showAppointmentQueue) {
                                Appointment3 next = appointmentQueue.get(c);
                            %>
                                <td><%= dfShow.format(next.getAppointmentDate()) %> <%= escape(next.getAppointmentTime()) %></td>
                            <% } %>
                            <td><button type="button" class="btn-sm customer-edit-button" onclick="doAction('editCustomer', <%= c.getId() %>)">Chỉnh sửa</button></td>
                            <td>
                                <a href="javascript:void(0)" class="action-link" onclick="doAction('selectCustomer', <%= c.getId() %>)">
                                    (chọn)
                                </a>
                            </td>
                        </tr>
                        <% } } %>
                    </tbody>
                </table>
                <% if (showAppointmentQueue && queuePages > 1) { %>
                    <div class="btn-group-center appointment-queue-pages">
                        <% if (queuePage > 1) { %>
                            <a class="btn-sm" href="receive-car?step=1&amp;queuePage=<%= queuePage - 1 %>">Trang trước</a>
                        <% } %>
                        <span>Trang <%= queuePage %> / <%= queuePages %></span>
                        <% if (queuePage < queuePages) { %>
                            <a class="btn-sm" href="receive-car?step=1&amp;queuePage=<%= queuePage + 1 %>">Trang sau</a>
                        <% } %>
                    </div>
                <% } %>
            </div>
            <% } %>

            <!-- ================= BUOC 2: CHON / THEM XE CUA KHACH HANG ================= -->
            <% if (currentStep == 2 && selectedCustomer != null) { %>
            <div class="section-box">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                    <div class="sub-heading" style="margin-bottom: 0;">Xe của khách hàng</div>
                    <button type="button" class="btn-sm" style="min-width: 120px;" onclick="toggleBox('newCarBox')">
                        Thêm xe mới
                    </button>
                </div>

                <!-- Form them xe moi -->
                <div id="newCarBox" style="display: <%= showNewCarBox ? "block" : "none" %>; border: 1px solid #000; padding: 12px 16px; margin-bottom: 14px; background: #fafafa;">
                    <div class="sub-heading">Thêm xe mới cho khách hàng <%= selectedCustomer.getFullName() != null ? selectedCustomer.getFullName().toString() : "" %></div>
                    <div class="grid-2" style="margin-bottom: 8px;">
                        <div class="col-half">
                            <div class="field-row">
                                <label>Biển số xe (*):</label>
                                <input type="text" name="newCarPlate" class="input-box" value="">
                            </div>
                            <div class="field-row">
                                <label>Hãng xe (*):</label>
                                <input type="text" name="newCarBrand" class="input-box" value="">
                            </div>
                            <div class="field-row">
                                <label>Mẫu xe (*):</label>
                                <input type="text" name="newCarModel" class="input-box" value="">
                            </div>
                        </div>
                        <div class="col-half">
                            <div class="field-row">
                                <label>Năm sản xuất:</label>
                                <input type="text" name="newCarYear" class="input-box" value="">
                            </div>
                            <div class="field-row">
                                <label>Mô tả xe:</label>
                                <input type="text" name="newCarDesc" class="input-box" value="">
                            </div>
                        </div>
                    </div>
                    <div style="text-align: right;">
                        <button type="button" class="btn-sm" onclick="toggleBox('newCarBox')">Hủy</button>
                        <button type="submit" name="saveNewCar" value="1" class="btn-sm" style="min-width: 110px;">Lưu xe</button>
                    </div>
                </div>

                <table id="outsubCarList" class="table">
                    <thead>
                        <tr>
                            <th style="width: 45px;">TT</th>
                            <th style="width: 125px;">Biển số xe</th>
                            <th style="width: 130px;">Hãng xe</th>
                            <th style="width: 130px;">Mẫu xe</th>
                            <th style="width: 80px;">Năm SX</th>
                            <th>Mô tả</th>
                            <th style="width: 90px;">Chọn</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (carList.length == 0) { %>
                        <tr>
                            <td colspan="7" style="text-align: center;">Chưa có xe.</td>
                        </tr>
                        <% } else {
                            for (int i = 0; i < carList.length; i++) {
                                Car3 car = carList[i];
                                boolean isSel = (selectedCar != null && selectedCar.getId() == car.getId());
                        %>
                        <tr class="<%= isSel ? "selected-row" : "" %>">
                            <td><%= i + 1 %></td>
                            <td><%= car.getPlateNumber() %></td>
                            <td><%= car.getBrand() %></td>
                            <td><%= car.getModel() %></td>
                            <td><%= car.getYear() > 0 ? String.valueOf(car.getYear()) : "-" %></td>
                            <td><%= car.getDescription() != null ? car.getDescription() : "" %></td>
                            <td>
                                <a href="javascript:void(0)" class="action-link" onclick="doAction('selectCar', <%= car.getId() %>)">
                                    (chọn)
                                </a>
                            </td>
                        </tr>
                        <% } } %>
                    </tbody>
                </table>
            </div>
            <% } %>

            <!-- ================= BUOC 3: CHON DICH VU VA PHU TUNG ================= -->
            <% if (currentStep == 3 && selectedCustomer != null && selectedCar != null) { %>
            <% if (selectedAppointment != null) { %>
            <div class="notice-box">
                <strong>Lịch hẹn đặt trước:</strong>
                Ngày <%= selectedAppointment.getAppointmentDate() != null ? dfShow.format(selectedAppointment.getAppointmentDate()) : "" %>
                - Giờ <%= selectedAppointment.getAppointmentTime() != null ? selectedAppointment.getAppointmentTime() : "" %>
                <% if (!customerNote.isEmpty()) { %>
                    | Ghi chú khách hàng: <%= escape(customerNote) %>
                <% } %>
            </div>
            <% } %>

            <div class="grid-2 repair-selection-grid">
                <!-- Tra cuu Dich vu -->
                <div class="col-half section-box" style="margin-bottom: 0;">
                    <div class="sub-heading">Dịch vụ</div>
                    <div class="search-bar">
                        <input type="text" id="inSearchService" name="inSearchService" class="input-box" style="flex: 1;"
                               value="<%= escape(kwService) %>">
                        <button type="submit" name="btnSearchService" value="1" class="btn-sm">Tìm</button>
                    </div>

                    <input type="hidden" id="serviceDetailNote" name="serviceDetailNote">
                    <div id="serviceDetailError" class="field-error" role="alert"></div>
                    <div id="outsubServiceList">
                        <% if (serviceList.length == 0) { %>
                            <div class="hint-text">Không tìm thấy dịch vụ phù hợp.</div>
                        <% } %>
                        <% for (Service3 service : serviceList) {
                            int id = service.getId();
                            int selectedIndex = -1;
                            for (int i = 0; i < selectedServices.size(); i++) {
                                if (selectedServices.get(i).getService().getId() == id) {
                                    selectedIndex = i;
                                    break;
                                }
                            }
                            boolean hasDetailOptions = id >= 1 && id <= 3;
                            String savedNote = selectedIndex < 0 ? "" : selectedServices.get(selectedIndex).getNote();
                        %>
                            <div class="svc-row">
                                <label>
                                    <input type="checkbox" id="mainSvc_<%= id %>" value="<%= id %>"
                                           data-selected-index="<%= selectedIndex %>"
                                           <% if (hasDetailOptions) { %>data-saved-note="<%= escape(savedNote == null ? "" : savedNote) %>"<% } %>
                                           <%= selectedIndex >= 0 ? "checked" : "" %> onchange="toggleRepairService(this)">
                                    <span><%= escape(service.getName()) %></span>
                                </label>
                                <% if (hasDetailOptions) { %>
                                    <button type="button" id="btnDetail_<%= id %>" class="svc-detail-link"
                                            data-save-details="true" aria-controls="panelDetail_<%= id %>"
                                            aria-expanded="false" onclick="toggleMainDetail(<%= id %>)">Thêm chi tiết</button>
                                <% } %>
                            </div>
                            <% if (hasDetailOptions) { %>
                                <div id="panelDetail_<%= id %>" class="svc-detail-panel" style="display: none;"></div>
                            <% } %>
                        <% } %>
                    </div>
                </div>

                <!-- Tra cuu Phu tung -->
                <div class="col-half section-box" style="margin-bottom: 0;">
                    <div class="sub-heading">Phụ tùng</div>
                    <div class="search-bar">
                        <input type="text" id="inSearchPart" name="inSearchPart" class="input-box" style="flex: 1;"
                               value="<%= escape(kwPart) %>">
                        <button type="submit" name="btnSearchPart" value="1" class="btn-sm">Tìm</button>
                    </div>

                    <table id="outsubPartList" class="table">
                        <thead>
                            <tr>
                                <th style="width: 35px;">TT</th>
                                <th>Tên phụ tùng</th>
                                <th style="width: 45px;">Tồn</th>
                                <th style="width: 85px;">Đơn giá</th>
                                <th style="width: 50px;">SL</th>
                                <th style="width: 60px;">Chọn</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (partList.length == 0) { %>
                            <tr>
                                <td colspan="6" style="text-align: center;">Không tìm thấy phụ tùng phù hợp.</td>
                            </tr>
                            <% } else {
                                for (int i = 0; i < visibleParts; i++) {
                                    SparePart3 p = partList[i];
                            %>
                            <tr>
                                <td><%= i + 1 %></td>
                                <td><%= p.getName() %></td>
                                <td><%= p.getQuantity() %></td>
                                <td><%= priceFormat.format(p.getSellingPrice()).replace(',', '.') %></td>
                                <td>
                                    <input type="text" id="qty_part_<%= p.getId() %>" class="input-box"
                                           style="width: 42px; height: 24px; text-align: center; padding: 2px;" value="1">
                                </td>
                                <td>
                                    <% if (p.getQuantity() > 0) { %>
                                        <a href="javascript:void(0)" class="action-link" onclick="addPartWithQty(<%= p.getId() %>)">(thêm)</a>
                                    <% } else { %>
                                        <span>(hết)</span>
                                    <% } %>
                                </td>
                            </tr>
                            <% } } %>
                        </tbody>
                    </table>
                    <% if (visibleParts < partList.length) { %>
                        <div class="hint-text" aria-label="Còn phụ tùng khác. Nhập từ khóa để tìm.">...</div>
                    <% } %>
                </div>
            </div>

            <!-- Bang hang muc da chon -->
            <div class="section-box" style="margin-top: 16px;">
                <div class="sub-heading">Danh sách dịch vụ và phụ tùng đã chọn</div>
                <table id="outSelectedList" class="table">
                    <thead>
                        <tr>
                            <th style="width: 45px;">TT</th>
                            <th>Tên dịch vụ / phụ tùng</th>
                            <th style="width: 90px;">Loại</th>
                            <th style="width: 55px;">SL</th>
                            <th style="width: 115px;">Đơn giá</th>
                            <th style="width: 120px;">Thành tiền</th>
                            <th style="width: 75px;">Xóa</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            int rowNum = 1;
                            if (selectedServices.isEmpty() && selectedParts.isEmpty()) {
                        %>
                        <tr>
                            <td colspan="7" style="text-align: center;">Chưa có dịch vụ hoặc phụ tùng nào được chọn.</td>
                        </tr>
                        <% } else {
                            for (int i = 0; i < selectedServices.size(); i++) {
                                RepairServiceDetail3 rsd = selectedServices.get(i);
                        %>
                        <tr>
                            <td><%= rowNum++ %></td>
                            <td>
                                <%= escape(rsd.getService().getName()) %>
                                <% if (rsd.getNote() != null && !rsd.getNote().isEmpty()) { %>
                                    <div class="service-detail-note"><%= escape(rsd.getNote()) %></div>
                                <% } %>
                            </td>
                            <td>Dịch vụ</td>
                            <td>1</td>
                            <td><%= rsd.getPrice() > 0 ? priceFormat.format(rsd.getPrice()).replace(',', '.') : "Chưa chọn hạng mục" %></td>
                            <td><%= rsd.getPrice() > 0 ? priceFormat.format(rsd.getPrice()).replace(',', '.') : "—" %></td>
                            <td>
                                <a href="javascript:void(0)" class="action-link" onclick="doAction('removeService', <%= i %>)">(xóa)</a>
                            </td>
                        </tr>
                        <% }
                            for (int i = 0; i < selectedParts.size(); i++) {
                                RepairSparePartDetail3 rpd = selectedParts.get(i);
                                float sub = rpd.getPrice() * rpd.getQuantity();
                        %>
                        <tr>
                            <td><%= rowNum++ %></td>
                            <td><%= rpd.getSparePart().getName() %></td>
                            <td>Phụ tùng</td>
                            <td><%= rpd.getQuantity() %></td>
                            <td><%= priceFormat.format(rpd.getPrice()).replace(',', '.') %></td>
                            <td><%= priceFormat.format(sub).replace(',', '.') %></td>
                            <td>
                                <a href="javascript:void(0)" class="action-link" onclick="doAction('removePart', <%= i %>)">(xóa)</a>
                            </td>
                        </tr>
                        <% } %>
                        <tr>
                            <td colspan="5" style="text-align: right; font-weight: bold;">Tổng tiền tạm tính:</td>
                            <td colspan="2" style="font-weight: bold;"><%= priceFormat.format(subtotal).replace(',', '.') %> VNĐ</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% } %>

            <!-- ================= BUOC 4: PHAN CONG KY THUAT VIEN ================= -->
            <% if (currentStep == 4 && selectedCustomer != null && selectedCar != null) { %>
            <% if (allTechniciansBusy) { %>
            <div class="notice-box">Tất cả nhân viên kỹ thuật đều đang bận.</div>
            <% } %>

            <div class="section-box">
                <div class="sub-heading">Kỹ thuật viên</div>
                <div class="search-bar">
                    <label for="inSearchTechnician">Tìm kiếm:</label>
                    <input type="text" id="inSearchTechnician" name="inSearchTechnician" class="input-box" style="width: 260px;"
                           value="<%= escape(kwTech) %>">
                    <button type="submit" name="btnSearchTech" value="1" class="btn-sm">Tìm</button>
                </div>

                <table id="outsubTechnicianList" class="table">
                    <thead>
                        <tr>
                            <th style="width: 45px;">TT</th>
                            <th>Họ tên</th>
                            <th style="width: 120px;">Số điện thoại</th>
                            <th>Chuyên môn</th>
                            <th style="width: 105px;">Trạng thái</th>
                            <th style="width: 150px;">Phân công</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (techList.length == 0) { %>
                        <tr>
                            <td colspan="6" style="text-align: center;">Không tìm thấy nhân viên kỹ thuật phù hợp.</td>
                        </tr>
                        <% } else {
                            for (int i = 0; i < techList.length; i++) {
                                Technician3 t = techList[i];
                                boolean isSel = (selectedTech != null && selectedTech.getId() == t.getId());
                        %>
                        <tr class="<%= isSel ? "selected-row" : "" %>">
                            <td><%= i + 1 %></td>
                            <td><%= t.getFullName() != null ? t.getFullName().toString() : "" %></td>
                            <td><%= t.getPhoneNumber() != null ? t.getPhoneNumber() : "" %></td>
                            <td><%= t.getSpecialization() %></td>
                            <td><%= t.getStatus() %></td>
                            <td>
                                <a href="javascript:void(0)" class="action-link" onclick="doAction('assignAllTech', <%= t.getId() %>)">
                                    (gán tất cả dịch vụ)
                                </a>
                            </td>
                        </tr>
                        <% } } %>
                    </tbody>
                </table>
            </div>

            <div class="section-box">
                <div class="sub-heading">Phân công theo dịch vụ</div>
                <table class="table">
                    <thead>
                        <tr>
                            <th style="width: 45px;">TT</th>
                            <th>Tên dịch vụ đã chọn</th>
                            <th style="width: 120px;">Đơn giá</th>
                            <th style="width: 280px;">Kỹ thuật viên phụ trách (*)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            for (int i = 0; i < selectedServices.size(); i++) {
                                RepairServiceDetail3 rsd = selectedServices.get(i);
                                int curTid = (rsd.getTechnician() != null) ? rsd.getTechnician().getId() : 0;
                        %>
                        <tr>
                            <td><%= i + 1 %></td>
                            <td><%= rsd.getService().getName() %></td>
                            <td><%= priceFormat.format(rsd.getPrice()).replace(',', '.') %></td>
                            <td>
                                <select name="techForService_<%= i %>" class="input-box" style="width: 100%; height: 28px;">
                                    <option value="0">-- chọn kỹ thuật viên --</option>
                                    <% for (Technician3 t : allTechs) { %>
                                    <option value="<%= t.getId() %>" <%= (curTid == t.getId()) ? "selected" : "" %>>
                                        <%= t.getFullName() != null ? t.getFullName().toString() : "" %> - <%= t.getSpecialization() %> (<%= t.getStatus() %>)
                                    </option>
                                    <% } %>
                                </select>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% } %>

            <!-- ================= THANH NUT DIEU HUONG ================= -->
            <div class="btn-group-center">
                <a href="sales-home?action=reset" class="btn">Về trang chủ</a>

                <% if (currentStep == 2) { %>
                    <button type="button" class="btn" onclick="goStep(1)">Quay lại Bước 1</button>
                <% } else if (currentStep == 3) { %>
                    <button type="button" class="btn" onclick="goStep(2)">Quay lại Bước 2</button>
                    <button type="submit" name="subCompleteServicePart" value="1" class="btn" style="min-width: 170px;">Hoàn tất chọn DV/PT</button>
                <% } else if (currentStep == 4) { %>
                    <button type="button" class="btn" onclick="goStep(3)">Quay lại Bước 3</button>
                    <button type="submit" name="subComplete" value="1" class="btn">Hoàn tất</button>
                <% } %>
            </div>
        </form>
    </div>
</body>
</html>
