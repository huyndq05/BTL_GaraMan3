<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    if (request.getAttribute("fromController") == null) {
        response.sendRedirect("book-appointment");
        return;
    }

    SimpleDateFormat dfIso = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dfShow = new SimpleDateFormat("dd/MM/yyyy");

    Service3[] offeredServices = (Service3[]) request.getAttribute("offeredServices");
    @SuppressWarnings("unchecked")
    Set<Integer> selectedServiceIds = (Set<Integer>) request.getAttribute("selectedServiceIds");
    String inName = (String) request.getAttribute("inName");
    String inPhoneNum = (String) request.getAttribute("inPhoneNum");
    String inEmail = (String) request.getAttribute("inEmail");
    String inAddress = (String) request.getAttribute("inAddress");
    String inBrand = (String) request.getAttribute("inBrand");
    String inModel = (String) request.getAttribute("inModel");
    String inPlateNum = (String) request.getAttribute("inPlateNum");
    String outsubDate = (String) request.getAttribute("outsubDate");
    String outsubTime = (String) request.getAttribute("outsubTime");
    String inNote = (String) request.getAttribute("inNote");
    String selectedDetailsText = (String) request.getAttribute("selectedDetailsText");
    int calOffset = ((Integer) request.getAttribute("calOffset")).intValue();

    String errorMsg = (String) request.getAttribute("errorMsg");
    String errName = (String) request.getAttribute("errName");
    String errPhone = (String) request.getAttribute("errPhone");
    String errEmail = (String) request.getAttribute("errEmail");
    String errAddress = (String) request.getAttribute("errAddress");
    String errBrand = (String) request.getAttribute("errBrand");
    String errModel = (String) request.getAttribute("errModel");
    String errPlate = (String) request.getAttribute("errPlate");

    int displayMonth = ((Integer) request.getAttribute("displayMonth")).intValue();
    int displayYear = ((Integer) request.getAttribute("displayYear")).intValue();
    Date[] monthDates = (Date[]) request.getAttribute("monthDates");
    @SuppressWarnings("unchecked")
    Set<String> fullDates = (Set<String>) request.getAttribute("fullDates");
    Date todayStart = (Date) request.getAttribute("todayStart");
    int leadingEmptyCells = ((Integer) request.getAttribute("leadingEmptyCells")).intValue();
    @SuppressWarnings("unchecked")
    Map<String, Integer> slotCounts = (Map<String, Integer>) request.getAttribute("slotCounts");
    String selectedDateLabel = (String) request.getAttribute("selectedDateLabel");
    String selectedTimeLabel = (String) request.getAttribute("selectedTimeLabel");
    String initialPeriod = (String) request.getAttribute("initialPeriod");
    boolean showDatePicker = Boolean.TRUE.equals(request.getAttribute("showDatePicker"));
    int maxPerSlot = ((Integer) request.getAttribute("maxPerSlot")).intValue();
    String serviceCatalogJson = (String) request.getAttribute("serviceCatalogJson");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đặt lịch dịch vụ</title>
    <link rel="stylesheet" href="assets/style.css">
    <script src="assets/service-details.js"></script>
    <script>
        var MORNING_SLOTS = ["08:00", "09:00", "10:00", "11:00", "12:00"];
        var AFTERNOON_SLOTS = ["13:00", "14:00", "15:00", "16:00", "17:00"];
        var SERVICE_CATALOG = <%= serviceCatalogJson %>;
        var MAX_PER_SLOT = <%= maxPerSlot %>;
        var currentSlotCounts = {
            <% int ci = 0; for (Map.Entry<String, Integer> en : slotCounts.entrySet()) { if (ci++ > 0) out.print(","); out.print("\"" + en.getKey() + "\":" + en.getValue()); } %>
        };
        var activePeriod = "<%= initialPeriod %>";
        var touchedFields = {};

        function setFieldError(id, msg) {
            var el = document.getElementById(id);
            var errEl = document.getElementById("err_" + id);
            if (!el || !errEl) return;
            errEl.textContent = msg;
            if (msg) el.classList.add("invalid");
            else el.classList.remove("invalid");
        }

        function checkName(showEmpty) {
            var val = document.getElementById("inName").value.trim();
            if (!val) { setFieldError("inName", showEmpty ? "Vui lòng nhập họ và tên." : ""); return false; }
            if (!/^[\p{L}\s]+$/u.test(val)) { setFieldError("inName", "Họ và tên không được chứa chữ số hoặc ký tự đặc biệt."); return false; }
            setFieldError("inName", ""); return true;
        }

        function checkPhone(showEmpty) {
            var val = document.getElementById("inPhoneNum").value.trim();
            if (!val) { setFieldError("inPhoneNum", showEmpty ? "Vui lòng nhập số điện thoại." : ""); return false; }
            if (!/^\d+$/.test(val)) { setFieldError("inPhoneNum", "Số điện thoại chỉ được chứa chữ số."); return false; }
            if (!/^0\d{9,10}$/.test(val)) { setFieldError("inPhoneNum", "Số điện thoại phải gồm 10-11 chữ số và bắt đầu bằng số 0."); return false; }
            setFieldError("inPhoneNum", ""); return true;
        }

        function checkEmail(showEmpty) {
            var val = document.getElementById("inEmail").value.trim();
            if (!val) { setFieldError("inEmail", showEmpty ? "Vui lòng nhập email." : ""); return false; }
            if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val)) { setFieldError("inEmail", "Email không đúng định dạng (ví dụ: ten@gmail.com)."); return false; }
            setFieldError("inEmail", ""); return true;
        }

        function checkNonEmpty(id, label, showEmpty) {
            var val = document.getElementById(id).value.trim();
            if (!val) { setFieldError(id, showEmpty ? ("Vui lòng nhập " + label + ".") : ""); return false; }
            setFieldError(id, ""); return true;
        }

        function countCheckedServices() {
            var cbs = document.querySelectorAll("input[name='mainServiceId']:checked");
            return cbs ? cbs.length : 0;
        }

        function validateAll(forceShowEmpty) {
            var okName = checkName(forceShowEmpty || touchedFields["inName"]);
            var okPhone = checkPhone(forceShowEmpty || touchedFields["inPhoneNum"]);
            var okEmail = checkEmail(forceShowEmpty || touchedFields["inEmail"]);
            var okAddress = checkNonEmpty("inAddress", "địa chỉ", forceShowEmpty || touchedFields["inAddress"]);
            var okBrand = checkNonEmpty("inBrand", "hãng xe", forceShowEmpty || touchedFields["inBrand"]);
            var okModel = checkNonEmpty("inModel", "mẫu xe", forceShowEmpty || touchedFields["inModel"]);
            var okPlate = checkNonEmpty("inPlateNum", "biển số xe", forceShowEmpty || touchedFields["inPlateNum"]);
            var hasService = countCheckedServices() > 0;
            var hasDate = document.getElementById("outsubDate").value.trim() !== "";
            var hasTime = document.getElementById("outsubTime").value.trim() !== "";

            var allOk = okName && okPhone && okEmail && okAddress && okBrand && okModel && okPlate && hasService && hasDate && hasTime;
            document.getElementById("btnSubmitBook").disabled = !allOk;

            var missing = [];
            if (!okName || !okPhone || !okEmail || !okAddress) missing.push("thông tin khách hàng hợp lệ");
            if (!okBrand || !okModel || !okPlate) missing.push("thông tin xe");
            if (!hasService) missing.push("ít nhất 1 dịch vụ");
            if (!hasDate) missing.push("ngày hẹn");
            if (!hasTime) missing.push("giờ hẹn");
            var hintEl = document.getElementById("submitHint");
            hintEl.textContent = (missing.length === 0)
                ? "Đã đủ thông tin. Nhấn Tiếp tục để xem lại và xác nhận lịch hẹn."
                : ("Cần hoàn thiện: " + missing.join(", ") + ".");
        }

        function markTouched(id) {
            touchedFields[id] = true;
            validateAll(false);
        }

        // --- THOI GIAN: NGAY & GIO ---
        function toggleDatePicker() {
            var dp = document.getElementById("datePickerPanel");
            var tp = document.getElementById("timePickerPanel");
            var isShow = dp.style.display === "block";
            dp.style.display = isShow ? "none" : "block";
            tp.style.display = "none";
            updatePickerActiveState();
        }

        function toggleTimePicker() {
            var dp = document.getElementById("datePickerPanel");
            var tp = document.getElementById("timePickerPanel");
            var isShow = tp.style.display === "block";
            tp.style.display = isShow ? "none" : "block";
            dp.style.display = "none";
            renderTimeSection();
            updatePickerActiveState();
        }

        function updatePickerActiveState() {
            var dpOpen = document.getElementById("datePickerPanel").style.display === "block";
            var tpOpen = document.getElementById("timePickerPanel").style.display === "block";
            document.getElementById("btnOpenDate").className = "picker-box" + (dpOpen ? " active-picker" : "");
            document.getElementById("btnOpenTime").className = "picker-box" + (tpOpen ? " active-picker" : "");
        }

        function switchMonth(offset) {
            document.getElementById("calOffset").value = offset;
            document.getElementById("bookForm").submit();
        }

        function selectDate(isoDate, displayDate) {
            document.getElementById("outsubDate").value = isoDate;
            document.getElementById("outsubTime").value = "";
            document.getElementById("dateBoxText").textContent = displayDate;
            document.getElementById("timeBoxText").textContent = "Chọn giờ";

            var dayBtns = document.querySelectorAll(".cal-day-btn");
            for (var i = 0; i < dayBtns.length; i++) {
                if (dayBtns[i].getAttribute("data-date") === isoDate) dayBtns[i].classList.add("active");
                else dayBtns[i].classList.remove("active");
            }

            fetch("book-appointment?ajaxSlots=1&date=" + encodeURIComponent(isoDate))
                .then(function(res) { return res.json(); })
                .then(function(data) {
                    currentSlotCounts = data || {};
                    document.getElementById("datePickerPanel").style.display = "none";
                    document.getElementById("timePickerPanel").style.display = "block";
                    renderTimeSection();
                    updatePickerActiveState();
                    validateAll(false);
                });
        }

        function switchPeriod(period) {
            activePeriod = period;
            renderTimeSection();
        }

        function selectTimeSlot(slot) {
            if ((currentSlotCounts[slot] || 0) >= MAX_PER_SLOT) return;
            document.getElementById("outsubTime").value = slot;
            document.getElementById("timeBoxText").textContent = slot;
            renderTimeSection();
            validateAll(false);
        }

        function isPeriodFull(slots) {
            for (var i = 0; i < slots.length; i++) {
                if ((currentSlotCounts[slots[i]] || 0) < MAX_PER_SLOT) return false;
            }
            return true;
        }

        function renderTimeSection() {
            var dateVal = document.getElementById("outsubDate").value;
            var emptyNotice = document.getElementById("timeEmptyNotice");
            var timeInner = document.getElementById("timeInnerPanel");
            if (!dateVal) {
                emptyNotice.style.display = "block";
                timeInner.style.display = "none";
                return;
            }
            emptyNotice.style.display = "none";
            timeInner.style.display = "block";

            var morningFull = isPeriodFull(MORNING_SLOTS);
            var afternoonFull = isPeriodFull(AFTERNOON_SLOTS);
            var btnM = document.getElementById("btnPeriodMorning");
            var btnA = document.getElementById("btnPeriodAfternoon");
            btnM.textContent = morningFull ? "Sáng (Hết chỗ)" : "Sáng";
            btnA.textContent = afternoonFull ? "Chiều (Hết chỗ)" : "Chiều";
            btnM.className = "slot-btn" + (activePeriod === "morning" ? " active" : "");
            btnA.className = "slot-btn" + (activePeriod === "afternoon" ? " active" : "");

            var slots = (activePeriod === "morning") ? MORNING_SLOTS : AFTERNOON_SLOTS;
            var grid = document.getElementById("timeSlotGrid");
            var timeField = document.getElementById("outsubTime");
            var chosenTime = timeField.value;
            if (chosenTime && (currentSlotCounts[chosenTime] || 0) >= MAX_PER_SLOT) {
                timeField.value = "";
                chosenTime = "";
                document.getElementById("timeBoxText").textContent = "Chọn giờ";
                validateAll(false);
            }
            var html = "";
            for (var i = 0; i < slots.length; i++) {
                var s = slots[i];
                var cnt = currentSlotCounts[s] || 0;
                var full = cnt >= MAX_PER_SLOT;
                var isAct = (chosenTime === s && !full);
                var label = full ? (s + " ×") : s;
                html += "<button type='button' class='slot-btn" + (isAct ? " active" : "") + "'" +
                        (full ? " disabled aria-label='" + s + " đã đủ " + MAX_PER_SLOT + " xe'" : (" onclick=\"selectTimeSlot('" + s + "')\"")) +
                        ">" + escHtml(label) + "</button>";
            }
            grid.innerHTML = html;

            var statusEl = document.getElementById("periodStatusNote");
            if (morningFull && afternoonFull) {
                statusEl.textContent = "Tất cả khung giờ trong ngày này đã hết chỗ. Vui lòng chọn ngày khác.";
            } else if ((activePeriod === "morning" && morningFull) || (activePeriod === "afternoon" && afternoonFull)) {
                statusEl.textContent = "Buổi này đã hết chỗ. Vui lòng chuyển sang buổi còn lại.";
            } else {
                statusEl.textContent = "Tối đa " + MAX_PER_SLOT + " xe mỗi khung giờ. Dấu × chỉ khung giờ đã đủ xe.";
            }
        }

        window.addEventListener("DOMContentLoaded", function() {
            var existingDetail = document.getElementById("selectedDetailsText").value;
            if (existingDetail) {
                var box = document.getElementById("selectedDetailSummary");
                box.style.display = "block";
                box.textContent = "Hạng mục chi tiết đã chọn: " + existingDetail;
            }
            Object.keys(SERVICE_CATALOG).forEach(function(sid) {
                var name = SERVICE_CATALOG[sid].name + " (";
                var start = existingDetail.indexOf(name);
                if (start < 0) return;
                var end = existingDetail.indexOf(")", start + name.length);
                if (end >= 0) restoreServiceDetails(sid, existingDetail.slice(start + name.length, end));
            });
            renderServicePanels();
            validateAll(false);
        });
    </script>
</head>
<body>
    <div class="top-nav">
        <span>Hệ thống GaraMan</span>
        <div>
            <a href="home">Trang chủ</a>
            <a href="<%= session.getAttribute("salesStaff") != null ? "sales-home" : "login" %>">Tài khoản</a>
        </div>
    </div>

    <div class="screen-box">
        <div class="screen-title">ĐẶT LỊCH DỊCH VỤ</div>

        <% if (errorMsg != null) { %>
            <div class="notice-box"><%= errorMsg %></div>
        <% } %>

        <form id="bookForm" action="book-appointment" method="post">
            <input type="hidden" id="outsubDate" name="outsubDate" value="<%= outsubDate %>">
            <input type="hidden" id="outsubTime" name="outsubTime" value="<%= outsubTime %>">
            <input type="hidden" id="calOffset" name="calOffset" value="<%= calOffset %>">
            <input type="hidden" id="selectedDetailsText" name="selectedDetailsText" value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(selectedDetailsText) %>">

            <!-- HANG 1: 1. THONG TIN KHACH HANG | 2. THONG TIN XE -->
            <div class="grid-2" style="margin-bottom: 16px;">
                <div class="col-half section-box" style="margin-bottom: 0;">
                    <div class="sub-heading">1. Thông tin khách hàng</div>

                    <div class="field-group">
                        <div class="field-row">
                            <label for="inName">Họ và tên <span class="required-mark">*</span>:</label>
                            <input type="text" id="inName" name="inName" class="input-box<%= errName.isEmpty() ? "" : " invalid" %>"
                                   value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(inName) %>" oninput="validateAll(false)" onblur="markTouched('inName')">
                        </div>
                        <div id="err_inName" class="field-error"><%= errName %></div>
                    </div>

                    <div class="field-group">
                        <div class="field-row">
                            <label for="inPhoneNum">Số điện thoại <span class="required-mark">*</span>:</label>
                            <input type="text" id="inPhoneNum" name="inPhoneNum" class="input-box<%= errPhone.isEmpty() ? "" : " invalid" %>"
                                   value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(inPhoneNum) %>" oninput="validateAll(false)" onblur="markTouched('inPhoneNum')">
                        </div>
                        <div id="err_inPhoneNum" class="field-error"><%= errPhone %></div>
                    </div>

                    <div class="field-group" style="margin-bottom: 0;">
                        <div class="field-row">
                            <label for="inEmail">Email <span class="required-mark">*</span>:</label>
                            <input type="text" id="inEmail" name="inEmail" class="input-box<%= errEmail.isEmpty() ? "" : " invalid" %>"
                                   value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(inEmail) %>" oninput="validateAll(false)" onblur="markTouched('inEmail')">
                        </div>
                        <div id="err_inEmail" class="field-error"><%= errEmail %></div>
                    </div>
                    <div class="field-group">
                        <div class="field-row">
                            <label for="inAddress">Địa chỉ <span class="required-mark">*</span>:</label>
                            <input type="text" id="inAddress" name="inAddress" autocomplete="street-address" maxlength="255"
                                   class="input-box<%= errAddress.isEmpty() ? "" : " invalid" %>"
                                   value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(inAddress) %>"
                                   oninput="validateAll(false)" onblur="markTouched('inAddress')">
                        </div>
                        <div id="err_inAddress" class="field-error"><%= errAddress %></div>
                    </div>
                </div>

                <div class="col-half section-box" style="margin-bottom: 0;">
                    <div class="sub-heading">2. Thông tin xe</div>

                    <div class="field-group">
                        <div class="field-row">
                            <label for="inBrand">Hãng xe <span class="required-mark">*</span>:</label>
                            <input type="text" id="inBrand" name="inBrand" class="input-box<%= errBrand.isEmpty() ? "" : " invalid" %>"
                                   value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(inBrand) %>" oninput="validateAll(false)" onblur="markTouched('inBrand')">
                        </div>
                        <div id="err_inBrand" class="field-error"><%= errBrand %></div>
                    </div>

                    <div class="field-group">
                        <div class="field-row">
                            <label for="inModel">Mẫu xe <span class="required-mark">*</span>:</label>
                            <input type="text" id="inModel" name="inModel" class="input-box<%= errModel.isEmpty() ? "" : " invalid" %>"
                                   value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(inModel) %>" oninput="validateAll(false)" onblur="markTouched('inModel')">
                        </div>
                        <div id="err_inModel" class="field-error"><%= errModel %></div>
                    </div>

                    <div class="field-group" style="margin-bottom: 0;">
                        <div class="field-row">
                            <label for="inPlateNum">Biển số xe <span class="required-mark">*</span>:</label>
                            <input type="text" id="inPlateNum" name="inPlateNum" class="input-box<%= errPlate.isEmpty() ? "" : " invalid" %>"
                                   value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(inPlateNum) %>" oninput="validateAll(false)" onblur="markTouched('inPlateNum')">
                        </div>
                        <div id="err_inPlateNum" class="field-error"><%= errPlate %></div>
                    </div>
                </div>
            </div>

            <!-- HANG 2: 3. DICH VU & GHI CHU | 4. THOI GIAN -->
            <div class="grid-2" style="margin-bottom: 12px; align-items: flex-start;">
                <!-- 3. DICH VU -->
                <div class="col-half section-box" style="margin-bottom: 0;">
                    <div class="sub-heading">3. Dịch vụ <span class="required-mark">*</span></div>

                    <% for (Service3 service : offeredServices) {
                        int id = service.getId();
                    %>
                    <div class="svc-row">
                        <label>
                            <input type="checkbox" id="mainSvc_<%= id %>" name="mainServiceId" value="<%= id %>"
                                   <%= selectedServiceIds.contains(id) ? "checked" : "" %> onchange="onMainServiceChange(<%= id %>)">
                            <span><%= org.apache.taglibs.standard.functions.Functions.escapeXml(service.getName()) %></span>
                        </label>
                        <button type="button" id="btnDetail_<%= id %>" aria-controls="panelDetail_<%= id %>" aria-expanded="false"
                                class="svc-detail-link" onclick="toggleMainDetail(<%= id %>)">Thêm chi tiết</button>
                    </div>
                    <div id="panelDetail_<%= id %>" class="svc-detail-panel" style="display: none;"></div>
                    <% } %>

                    <div id="selectedDetailSummary" class="selected-detail-summary" style="display: none;"></div>

                    <div style="margin-top: 10px;">
                        <div class="sub-heading" style="margin-bottom: 6px;">Ghi chú</div>
                        <textarea id="inNote" name="inNote" class="note-area"><%= org.apache.taglibs.standard.functions.Functions.escapeXml(inNote) %></textarea>
                    </div>
                </div>

                <!-- 4. THOI GIAN -->
                <div class="col-half section-box" style="margin-bottom: 0;">
                    <div class="sub-heading">4. Thời gian <span class="required-mark">*</span></div>

                    <div class="picker-trigger-row">
                        <button type="button" id="btnOpenDate" class="picker-box" onclick="toggleDatePicker()">
                            <span id="dateBoxText"><%= selectedDateLabel %></span>
                            <span>[Ngày]</span>
                        </button>
                        <button type="button" id="btnOpenTime" class="picker-box" onclick="toggleTimePicker()">
                            <span id="timeBoxText"><%= selectedTimeLabel %></span>
                            <span>[Giờ]</span>
                        </button>
                    </div>

                    <!-- Khung Lich Thang (CN -> T7) -->
                    <div id="datePickerPanel" class="picker-panel" style="display: <%= showDatePicker ? "block" : "none" %>;">
                        <div class="calendar-header">
                            <button type="button" class="btn-sm" style="min-width: 36px;" <%= calOffset <= 0 ? "disabled" : "" %> onclick="switchMonth(<%= calOffset - 1 %>)">&lt;</button>
                            <strong>Tháng <%= String.format("%02d/%d", displayMonth, displayYear) %></strong>
                            <button type="button" class="btn-sm" style="min-width: 36px;" <%= calOffset >= 2 ? "disabled" : "" %> onclick="switchMonth(<%= calOffset + 1 %>)">&gt;</button>
                        </div>

                        <div class="calendar-grid">
                            <div class="cal-dow">CN</div>
                            <div class="cal-dow">T2</div>
                            <div class="cal-dow">T3</div>
                            <div class="cal-dow">T4</div>
                            <div class="cal-dow">T5</div>
                            <div class="cal-dow">T6</div>
                            <div class="cal-dow">T7</div>

                            <% for (int e = 0; e < leadingEmptyCells; e++) { %>
                                <div></div>
                            <% } %>

                            <%
                                for (Date d : monthDates) {
                                    String iso = dfIso.format(d);
                                    String disp = dfShow.format(d);
                                    Calendar cDay = Calendar.getInstance();
                                    cDay.setTime(d);
                                    cDay.set(Calendar.HOUR_OF_DAY, 0);
                                    cDay.set(Calendar.MINUTE, 0);
                                    cDay.set(Calendar.SECOND, 0);
                                    cDay.set(Calendar.MILLISECOND, 0);
                                    boolean isPast = cDay.getTime().before(todayStart);
                                    boolean isFull = fullDates.contains(iso);
                                    boolean isSel = iso.equals(outsubDate);
                                    int dayNum = cDay.get(Calendar.DAY_OF_MONTH);
                            %>
                            <button type="button"
                                    class="slot-btn cal-day-btn<%= isPast ? " past-day" : "" %><%= isSel ? " active" : "" %>"
                                    data-date="<%= iso %>"
                                    <%= (isPast || isFull) ? "disabled" : ("onclick=\"selectDate('" + iso + "', '" + disp + "')\"") %>>
                                <%= (!isPast && isFull) ? "X" : String.valueOf(dayNum) %>
                            </button>
                            <% } %>
                        </div>
                        <div class="hint-text">[X] Đã hết chỗ</div>
                    </div>

                    <!-- Khung Chon Gio (Sang / Chieu) -->
                    <div id="timePickerPanel" class="picker-panel" style="display: none;">
                        <div id="timeEmptyNotice" class="hint-text" style="display: none;">
                            Chưa chọn ngày hẹn.
                        </div>
                        <div id="timeInnerPanel">
                            <div class="period-tabs">
                                <button type="button" id="btnPeriodMorning" class="slot-btn" style="flex: 1;" onclick="switchPeriod('morning')">Sáng</button>
                                <button type="button" id="btnPeriodAfternoon" class="slot-btn" style="flex: 1;" onclick="switchPeriod('afternoon')">Chiều</button>
                            </div>
                            <div id="timeSlotGrid" class="slot-grid-4"></div>
                            <div id="periodStatusNote" class="hint-text"></div>
                        </div>
                    </div>
                </div>
            </div>

            <div id="submitHint" class="hint-text" style="display: none;"></div>

            <!-- Thanh nut dieu huong -->
            <div class="btn-group-center" style="margin-top: 14px;">
                <a href="home?action=reset" class="btn">Về trang chủ</a>
                <button type="submit" id="btnSubmitBook" name="subComplete" value="1" class="btn" disabled>Tiếp tục</button>
            </div>
        </form>
    </div>
</body>
</html>
