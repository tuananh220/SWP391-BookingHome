(function () {
    function pad(value) {
        return String(value).padStart(2, "0");
    }

    function todayStr() {
        var now = new Date();
        return now.getFullYear() + "-" + pad(now.getMonth() + 1) + "-" + pad(now.getDate());
    }

    function addDays(iso, days) {
        var parts = iso.split("-");
        var date = new Date(Number(parts[0]), Number(parts[1]) - 1, Number(parts[2]));
        date.setDate(date.getDate() + days);
        return date.getFullYear() + "-" + pad(date.getMonth() + 1) + "-" + pad(date.getDate());
    }

    function nowLocal() {
        var now = new Date();
        return todayStr() + "T" + pad(now.getHours()) + ":" + pad(now.getMinutes());
    }

    function clampMin(input, min) {
        if (min && input.value && input.value < min) {
            input.value = "";
        }
    }

    function clampMax(input, max) {
        if (max && input.value && input.value > max) {
            input.value = "";
        }
    }

    var today = todayStr();
    var checkIn = document.querySelector('input[type="date"][name="checkIn"]');
    var checkOut = document.querySelector('input[type="date"][name="checkOut"]');

    function syncCheckoutMin() {
        if (!checkOut) {
            return;
        }
        var minOut = checkIn && checkIn.value ? addDays(checkIn.value, 1) : addDays(today, 1);
        checkOut.min = minOut;
        clampMin(checkOut, minOut);
    }

    if (checkIn) {
        checkIn.min = today;
        clampMin(checkIn, today);
        checkIn.addEventListener("change", syncCheckoutMin);
    }
    syncCheckoutMin();

    var requested = document.querySelector('input[type="date"][name="requestedCheckOutDate"]');
    var typeSelect = document.querySelector('select[name="requestType"]');
    if (requested) {
        var originalCheckout = requested.getAttribute("data-original-checkout");
        var checkInDate = requested.getAttribute("data-check-in");

        function syncStayChange() {
            var type = typeSelect ? typeSelect.value : "Extension";
            var min;
            var max = "";
            if (type === "EarlyCheckout") {
                var afterCheckIn = checkInDate ? addDays(checkInDate, 1) : today;
                min = afterCheckIn > today ? afterCheckIn : today;
                max = originalCheckout ? addDays(originalCheckout, -1) : "";
            } else {
                min = originalCheckout ? addDays(originalCheckout, 1) : addDays(today, 1);
            }
            requested.min = min;
            if (max) {
                requested.max = max;
            } else {
                requested.removeAttribute("max");
            }
            clampMin(requested, min);
            clampMax(requested, max);
        }

        if (typeSelect) {
            typeSelect.addEventListener("change", syncStayChange);
        }
        syncStayChange();
    }

    var start = document.querySelector('input[type="datetime-local"][name="startDate"]');
    var end = document.querySelector('input[type="datetime-local"][name="endDate"]');
    if (start && end) {
        var isNew = !start.value;
        function syncVoucherEnd() {
            end.min = start.value || nowLocal();
            clampMin(end, end.min);
        }
        if (isNew) {
            start.min = nowLocal();
        }
        start.addEventListener("change", syncVoucherEnd);
        syncVoucherEnd();
    }
})();
