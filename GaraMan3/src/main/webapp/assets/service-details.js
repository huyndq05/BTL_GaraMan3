var openMainDetail = null;
var openSubDetail = { 2: null, 3: null };
var selectedFlat = {};       // "1|item" -> true
var selectedL2 = {};         // "2|key" -> true
var selectedL3 = {};         // "2|key|item" -> true

function escHtml(s) {
    return String(s).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}

// --- DICH VU & THEM CHI TIET ---
function servicePriceLabel(price) {
    if (typeof price !== "number") return "";
    return "<span class='service-row-price'>" + price.toLocaleString("vi-VN") + " đ</span>";
}

function onMainServiceChange(sid) {
    var cb = document.getElementById("mainSvc_" + sid);
    if (!cb.checked) {
        // Bo chon nhom chinh -> xoa cac chi tiet con cua nhom do
        for (var k in selectedFlat) { if (k.indexOf(sid + "|") === 0) delete selectedFlat[k]; }
        for (var k2 in selectedL2) { if (k2.indexOf(sid + "|") === 0) delete selectedL2[k2]; }
        for (var k3 in selectedL3) { if (k3.indexOf(sid + "|") === 0) delete selectedL3[k3]; }
        if (openMainDetail === sid) openMainDetail = null;
    }
    renderServicePanels();
    syncSelectedDetailsText();
    if (typeof validateAll === 'function') validateAll(false);
}

function toggleMainDetail(sid) {
    if (openMainDetail !== null) {
        var previous = document.getElementById("btnDetail_" + openMainDetail);
        if (previous.dataset.saveDetails && saveRepairDetails(openMainDetail)) return;
    }
    openMainDetail = (openMainDetail === sid) ? null : sid;
    renderServicePanels();
}

function toggleFlatItem(sid, item) {
    var key = sid + "|" + item;
    if (selectedFlat[key]) delete selectedFlat[key];
    else {
        selectedFlat[key] = true;
        document.getElementById("mainSvc_" + sid).checked = true;
    }
    syncSelectedDetailsText();
    if (typeof validateAll === 'function') validateAll(false);
}

function toggleL2Item(sid, gKey) {
    var key = sid + "|" + gKey;
    if (selectedL2[key]) {
        delete selectedL2[key];
        for (var k3 in selectedL3) {
            if (k3.indexOf(sid + "|" + gKey + "|") === 0) delete selectedL3[k3];
        }
    } else {
        selectedL2[key] = true;
        document.getElementById("mainSvc_" + sid).checked = true;
    }
    renderServicePanels();
    syncSelectedDetailsText();
    if (typeof validateAll === 'function') validateAll(false);
}

function toggleSubDetail(sid, gKey) {
    openSubDetail[sid] = (openSubDetail[sid] === gKey) ? null : gKey;
    renderServicePanels();
}

function toggleL3Item(sid, gKey, item) {
    var key = sid + "|" + gKey + "|" + item;
    if (selectedL3[key]) delete selectedL3[key];
    else {
        selectedL3[key] = true;
        selectedL2[sid + "|" + gKey] = true;
        document.getElementById("mainSvc_" + sid).checked = true;
    }
    renderServicePanels();
    syncSelectedDetailsText();
    if (typeof validateAll === 'function') validateAll(false);
}

function renderServicePanels() {
    [1, 2, 3].forEach(function(sid) {
        var btn = document.getElementById("btnDetail_" + sid);
        var panel = document.getElementById("panelDetail_" + sid);
        if (!btn || !panel) return;

        if (openMainDetail !== sid) {
            btn.textContent = "Thêm chi tiết";
            btn.setAttribute("aria-expanded", "false");
            panel.style.display = "none";
            panel.innerHTML = "";
            return;
        }

        btn.textContent = "Xong";
        btn.setAttribute("aria-expanded", "true");
        panel.style.display = "block";
        var cat = SERVICE_CATALOG[sid];

        if (cat.type === "flat") {
            var html = "";
            for (var i = 0; i < cat.items.length; i++) {
                var it = cat.items[i];
                var chk = selectedFlat[sid + "|" + it] ? " checked" : "";
                html += "<div class='svc-sub-item'><label>" +
                        "<input type='checkbox'" + chk + " onchange=\"toggleFlatItem(" + sid + ", '" + it.replace(/'/g, "\\'") + "')\">" +
                        "<span>" + escHtml(it) + "</span></label>" + servicePriceLabel(cat.prices && cat.prices[it]) + "</div>";
            }
            panel.innerHTML = html;
        } else {
            var l2Html = "";
            var activeGroup = null;
            for (var j = 0; j < cat.groups.length; j++) {
                var g = cat.groups[j];
                var chk2 = selectedL2[sid + "|" + g.key] ? " checked" : "";
                var isSubOpen = (openSubDetail[sid] === g.key);
                if (isSubOpen) activeGroup = g;
                l2Html += "<div class='svc-sub-item'><label>" +
                          "<input type='checkbox'" + chk2 + " onchange=\"toggleL2Item(" + sid + ", '" + g.key + "')\">" +
                          "<span>" + escHtml(g.name) + "</span></label>";
                if (g.items && g.items.length > 0) {
                    l2Html += "<button type='button' class='svc-detail-link' onclick=\"toggleSubDetail(" + sid + ", '" + g.key + "')\">" +
                              (isSubOpen ? "Xong" : "Thêm chi tiết") + "</button>";
                }
                l2Html += servicePriceLabel(g.price) + "</div>";
            }

            if (activeGroup) {
                var l3Html = "<div style='font-weight:bold; font-size:13px; margin-bottom:6px;'>" + escHtml(activeGroup.name) + "</div>";
                for (var m = 0; m < activeGroup.items.length; m++) {
                    var subIt = activeGroup.items[m];
                    var chk3 = selectedL3[sid + "|" + activeGroup.key + "|" + subIt] ? " checked" : "";
                    l3Html += "<div class='svc-sub-item'><label>" +
                              "<input type='checkbox'" + chk3 + " onchange=\"toggleL3Item(" + sid + ", '" + activeGroup.key + "', '" + subIt.replace(/'/g, "\\'") + "')\">" +
                              "<span>" + escHtml(subIt) + "</span></label>" + servicePriceLabel(activeGroup.prices && activeGroup.prices[subIt]) + "</div>";
                }
                panel.innerHTML = "<div class='svc-nested-grid'><div class='svc-l2-col'>" + l2Html + "</div><div class='svc-l3-col'>" + l3Html + "</div></div>";
            } else {
                panel.innerHTML = l2Html;
            }
        }
    });
}

function serviceDetailText(sid) {
    var service = SERVICE_CATALOG[sid];
    if (service.type === "flat") {
        return service.items.filter(function(item) {
            return selectedFlat[sid + "|" + item];
        }).join(", ");
    }
    return service.groups.map(function(group) {
        var items = group.items.filter(function(item) {
            return selectedL3[sid + "|" + group.key + "|" + item];
        });
        if (items.length) return group.name + ": " + items.join(", ");
        if (selectedL2[sid + "|" + group.key]) return group.name;
        return "";
    }).filter(Boolean).join("; ");
}

function restoreServiceDetails(sid, text) {
    var service = SERVICE_CATALOG[sid];
    if (!text) return;
    if (service.type === "flat") {
        service.items.forEach(function(item) {
            if ((", " + text + ", ").includes(", " + item + ", ")) {
                selectedFlat[sid + "|" + item] = true;
            }
        });
        return;
    }
    var groups = text.split("; ");
    service.groups.forEach(function(group) {
        var prefix = group.name + ": ";
        var selected = groups.find(function(value) {
            return value === group.name || value.startsWith(prefix);
        });
        if (!selected) return;
        selectedL2[sid + "|" + group.key] = true;
        if (selected === group.name) return;
        var items = ", " + selected.slice(prefix.length) + ", ";
        group.items.forEach(function(item) {
            if (items.includes(", " + item + ", ")) {
                selectedL3[sid + "|" + group.key + "|" + item] = true;
            }
        });
    });
}

function syncSelectedDetailsText() {
    var field = document.getElementById("selectedDetailsText");
    if (!field) return;
    var summary = Object.keys(SERVICE_CATALOG).map(function(sid) {
        var text = serviceDetailText(sid);
        return text ? SERVICE_CATALOG[sid].name + " (" + text + ")" : "";
    }).filter(Boolean).join("; ");
    field.value = summary;
    var box = document.getElementById("selectedDetailSummary");
    box.style.display = summary ? "block" : "none";
    box.textContent = summary ? "Hạng mục chi tiết đã chọn: " + summary : "";
}
