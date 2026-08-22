<%-- Document : homestay-list Created on : Aug 18, 2026, 4:32:10 PM Author : Admin --%>

    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
        <%@ taglib prefix="c" uri="jakarta.tags.core" %>
            <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Tìm homestay</title>
                    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
                    <script src="${pageContext.request.contextPath}/assets/js/address-selector.js"></script>
                </head>

                <body>
                    <jsp:include page="/views/fragments/customer-header.jsp" />

                    <c:if test="${not empty banners}">
                        <section class="public-banners">
                            <c:forEach items="${banners}" var="banner">
                                <c:choose>
                                    <c:when test="${not empty banner.targetUrl}">
                                        <c:url value="${banner.targetUrl}" var="bannerLink" />
                                        <a href="<c:out value='${bannerLink}'/>"><img
                                                src="<c:out value='${banner.imageUrl}'/>"
                                                alt="<c:out value='${banner.title}'/>">
                                            <c:if test="${not empty banner.title}"><span>
                                                    <c:out value="${banner.title}" />
                                                </span></c:if>
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <div><img src="<c:out value='${banner.imageUrl}'/>"
                                                alt="<c:out value='${banner.title}'/>">
                                            <c:if test="${not empty banner.title}"><span>
                                                    <c:out value="${banner.title}" />
                                                </span></c:if>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </section>
                    </c:if>

                    <section class="search-hero">
                        <p class="eyebrow">TÌM NƠI DỪNG CHÂN</p>
                        <h1>Ở đâu cũng có thể là nhà.</h1>
                        <form id="searchForm" method="get" action="${pageContext.request.contextPath}/homestays"
                            class="search-form">
                            <label class="wide">Từ khóa
                                <input type="text" name="keyword" placeholder="Tên hoặc địa chỉ homestay"
                                    value="<c:out value='${param.keyword}'/>">
                            </label>
                            <label>Địa điểm
                                <select id="provinceSelect" name="city">
                                    <option value="">-- Đang tải... --</option>
                                </select>
                            </label>
                            <label>Nhận phòng
                                <input type="date" id="checkIn" name="checkIn"
                                    value="<c:out value='${criteria.checkInDate}'/>" min="">
                            </label>
                            <label>Trả phòng
                                <input type="date" id="checkOut" name="checkOut"
                                    value="<c:out value='${criteria.checkOutDate}'/>" min="">
                            </label>
                            <label>Số khách
                                <div class="guests-stepper">
                                    <span id="guestsDecBtn" class="stepper-btn" role="button"
                                        aria-label="Giảm số khách">−</span>
                                    <input type="number" id="guestsInput" name="guests" min="1" max="30"
                                        value="<c:out value='${criteria.guests != null ? criteria.guests : 2}'/>"
                                        readonly>
                                    <span id="guestsIncBtn" class="stepper-btn" role="button"
                                        aria-label="Tăng số khách">+</span>
                                </div>
                            </label>
                            <label>Giá từ
                                <input type="number" name="minPrice" min="0" step="1000"
                                    value="<c:out value='${param.minPrice}'/>">
                            </label>
                            <label>Giá đến
                                <input type="number" name="maxPrice" min="0" step="1000"
                                    value="<c:out value='${param.maxPrice}'/>">
                            </label>
                            <div class="search-dropdown-field">
                                <span class="field-label">Tiện ích</span>
                                <div class="search-dropdown-trigger" tabindex="0">
                                    <c:choose>
                                        <c:when test="${not empty criteria.amenityIds}">
                                            <span class="trigger-text">${criteria.amenityIds.size()} tiện ích đã
                                                chọn</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="trigger-text">Tất cả tiện ích</span>
                                        </c:otherwise>
                                    </c:choose>
                                    <span class="dropdown-arrow">▾</span>
                                </div>
                                <div class="search-dropdown-panel">
                                    <div class="dropdown-amenities-list">
                                        <c:forEach items="${amenities}" var="amenity">
                                            <label class="dropdown-check-option">
                                                <input type="checkbox" name="amenityIds" value="${amenity.amenityId}"
                                                    ${criteria.amenityIds !=null &&
                                                    criteria.amenityIds.contains(amenity.amenityId) ? 'checked' : '' }>
                                                <span>
                                                    <c:out value="${amenity.amenityName}" />
                                                </span>
                                            </label>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                            <label>Đánh giá
                                <select name="minRating">
                                    <option value="">Tất cả</option>
                                    <option value="3" ${param.minRating=='3' ? 'selected' : '' }>Từ 3 sao</option>
                                    <option value="4" ${param.minRating=='4' ? 'selected' : '' }>Từ 4 sao</option>
                                    <option value="5" ${param.minRating=='5' ? 'selected' : '' }>5 sao</option>
                                </select>
                            </label>
                            <button type="submit">Tìm kiếm</button>
                        </form>
                    </section>

                    <main class="catalog-wrap">
                        <div class="result-heading">
                            <div>
                                <p class="eyebrow">KẾT QUẢ</p>
                                <h2>${homestays.size()} homestay phù hợp</h2>
                            </div>
                            <a class="clear-link" href="${pageContext.request.contextPath}/homestays">Xóa bộ lọc</a>
                        </div>

                        <c:if test="${not empty requestScope.error}">
                            <div class="alert">
                                <c:out value="${requestScope.error}" />
                            </div>
                        </c:if>

                        <c:choose>
                            <c:when test="${empty homestays}">
                                <section class="empty-state">
                                    <h3>Chưa tìm thấy nơi phù hợp</h3>
                                    <p>Hãy thử thay đổi ngày, mức giá hoặc tiện ích.</p>
                                </section>
                            </c:when>
                            <c:otherwise>
                                <section class="property-grid">
                                    <c:forEach items="${homestays}" var="item">
                                        <article class="property-card">
                                            <a class="image-wrap"
                                                href="${pageContext.request.contextPath}/homestay-detail?id=${item.homestayId}&checkIn=${param.checkIn}&checkOut=${param.checkOut}&guests=${param.guests}">
                                                <c:choose>
                                                    <c:when test="${not empty item.primaryImageUrl}">
                                                        <img src="<c:out value='${item.primaryImageUrl}'/>"
                                                            alt="<c:out value='${item.title}'/>">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="image-placeholder">HOMESTAY</div>
                                                    </c:otherwise>
                                                </c:choose>
                                                <span class="guest-badge">Tối đa ${item.maxGuests} khách</span>
                                            </a>
                                            <div class="property-content">
                                                <div class="location">
                                                    <c:out value="${item.district}" />,
                                                    <c:out value="${item.city}" />
                                                </div>
                                                <h3><a
                                                        href="${pageContext.request.contextPath}/homestay-detail?id=${item.homestayId}&checkIn=${param.checkIn}&checkOut=${param.checkOut}&guests=${param.guests}">
                                                        <c:out value="${item.title}" />
                                                    </a></h3>
                                                <div class="card-bottom">
                                                    <p><strong>
                                                            <fmt:formatNumber value="${item.pricePerNight}"
                                                                pattern="#,##0" /> ₫
                                                        </strong> / đêm</p>
                                                    <p class="rating">★
                                                        <fmt:formatNumber value="${item.averageRating}"
                                                            maxFractionDigits="1" /> <span>(${item.reviewCount})</span>
                                                    </p>
                                                </div>
                                            </div>
                                        </article>
                                    </c:forEach>
                                </section>
                            </c:otherwise>
                        </c:choose>
                    </main>
                    <script>
                        // ── Date + Guests logic ──────────────────────────────
                        (function () {
                            var checkInEl = document.getElementById('checkIn');
                            var checkOutEl = document.getElementById('checkOut');
                            var guestsEl = document.getElementById('guestsInput');
                            var decBtn = document.getElementById('guestsDecBtn');
                            var incBtn = document.getElementById('guestsIncBtn');

                            // ── Compute today & tomorrow from CLIENT browser clock ──
                            // This ensures the dates are always accurate regardless of
                            // server timezone. Format: YYYY-MM-DD
                            var now = new Date();
                            var todayStr = now.getFullYear() + '-'
                                + String(now.getMonth() + 1).padStart(2, '0') + '-'
                                + String(now.getDate()).padStart(2, '0');

                            var tmrRow = new Date(now);
                            tmrRow.setDate(tmrRow.getDate() + 1);
                            var tomorrowStr = tmrRow.getFullYear() + '-'
                                + String(tmrRow.getMonth() + 1).padStart(2, '0') + '-'
                                + String(tmrRow.getDate()).padStart(2, '0');

                            // Set min constraints so date-picker blocks past dates
                            checkInEl.min = todayStr;
                            checkOutEl.min = tomorrowStr;

                            // ── Override server defaults with real client-side dates ──
                            // Only applies when loading without explicit URL params
                            // (server may have a different timezone offset)
                            if (!checkInEl.value || checkInEl.value < todayStr) checkInEl.value = todayStr;
                            if (!checkOutEl.value || checkOutEl.value <= checkInEl.value) checkOutEl.value = tomorrowStr;

                            // ── Helper: get next day string from a YYYY-MM-DD string ──
                            function nextDay(dateStr) {
                                var d = new Date(dateStr);
                                d.setDate(d.getDate() + 1);
                                return d.getFullYear() + '-'
                                    + String(d.getMonth() + 1).padStart(2, '0') + '-'
                                    + String(d.getDate()).padStart(2, '0');
                            }

                            // ── When checkIn changes: update checkOut min & auto-advance ──
                            checkInEl.addEventListener('change', function () {
                                var ciVal = checkInEl.value;
                                if (ciVal) {
                                    var coMin = nextDay(ciVal);
                                    checkOutEl.min = coMin;
                                    if (checkOutEl.value && checkOutEl.value <= ciVal) {
                                        checkOutEl.value = coMin;
                                    }
                                } else {
                                    checkOutEl.min = tomorrowStr;
                                }
                            });

                            // ── When checkOut changes: constrain checkIn max ──
                            checkOutEl.addEventListener('change', function () {
                                var coVal = checkOutEl.value;
                                if (coVal) {
                                    var d = new Date(coVal);
                                    d.setDate(d.getDate() - 1);
                                    checkInEl.max = d.getFullYear() + '-'
                                        + String(d.getMonth() + 1).padStart(2, '0') + '-'
                                        + String(d.getDate()).padStart(2, '0');
                                } else {
                                    checkInEl.removeAttribute('max');
                                }
                            });

                            // Trigger once on load to sync checkOut min with current checkIn
                            checkInEl.dispatchEvent(new Event('change'));

                            // ── Guests stepper ───────────────────────────────
                            var MAX_GUESTS = 30;

                            function updateStepperState() {
                                var val = parseInt(guestsEl.value, 10) || 1;
                                if (val < 1) { guestsEl.value = 1; val = 1; }
                                if (val > MAX_GUESTS) { guestsEl.value = MAX_GUESTS; val = MAX_GUESTS; }
                                decBtn.classList.toggle('stepper-disabled', val <= 1);
                                incBtn.classList.toggle('stepper-disabled', val >= MAX_GUESTS);
                            }

                            decBtn.addEventListener('click', function () {
                                if (decBtn.classList.contains('stepper-disabled')) return;
                                var val = parseInt(guestsEl.value, 10) || 1;
                                if (val > 1) { guestsEl.value = val - 1; }
                                updateStepperState();
                            });

                            incBtn.addEventListener('click', function () {
                                if (incBtn.classList.contains('stepper-disabled')) return;
                                var val = parseInt(guestsEl.value, 10) || 1;
                                if (val < MAX_GUESTS) { guestsEl.value = val + 1; }
                                updateStepperState();
                            });

                            updateStepperState();


                            // ── Form submit validation ────────────────────────
                            document.getElementById('searchForm').addEventListener('submit', function (e) {
                                var ciVal = checkInEl.value;
                                var coVal = checkOutEl.value;
                                var errors = [];

                                if (ciVal && ciVal < todayStr) {
                                    errors.push('Ngày nhận phòng không được là ngày trong quá khứ.');
                                }
                                if (coVal && coVal < todayStr) {
                                    errors.push('Ngày trả phòng không được là ngày trong quá khứ.');
                                }
                                if (ciVal && coVal && coVal <= ciVal) {
                                    errors.push('Ngày trả phòng phải sau ngày nhận phòng.');
                                }

                                if (errors.length > 0) {
                                    e.preventDefault();
                                    alert(errors.join('\n'));
                                }
                            });
                        })();

                        // ── Amenity dropdown counter ─────────────────────────
                        document.querySelectorAll('.dropdown-amenities-list input[type="checkbox"]').forEach(function (cb) {
                            cb.addEventListener('change', function () {
                                var checkedCount = document.querySelectorAll('.dropdown-amenities-list input[type="checkbox"]:checked').length;
                                var textElem = document.querySelector('.search-dropdown-trigger .trigger-text');
                                if (textElem) {
                                    textElem.textContent = checkedCount > 0 ? (checkedCount + ' tiện ích đã chọn') : 'Tất cả tiện ích';
                                }
                            });
                        });

                        // ── Province (tỉnh/thành) dropdown ───────────────────
                        (function () {
                            var CONTEXT = '${pageContext.request.contextPath}';
                            var currentCity = '${not empty param.city ? param.city : ""}'; // Giá trị đang được lọc

                            var provinceSelect = document.getElementById('provinceSelect');
                            if (!provinceSelect) return;

                            // Chèn option "Tất cả" vào đầu
                            provinceSelect.innerHTML = '<option value="">-- Đang tải... --</option>';

                            // Helper fetch tỉnh thành
                            async function loadProvinces() {
                                try {
                                    var res = await fetch(CONTEXT + '/api/provinces');
                                    if (!res.ok) throw new Error('status ' + res.status);
                                    return await res.json();
                                } catch (e) {
                                    // fallback
                                    try {
                                        var res2 = await fetch('https://provinces.open-api.vn/api/v2/p/');
                                        if (res2.ok) return await res2.json();
                                    } catch (e2) {}
                                    return [];
                                }
                            }

                            loadProvinces().then(function (provinces) {
                                provinceSelect.innerHTML = '';

                                // Option đầu: Tất cả
                                var allOpt = document.createElement('option');
                                allOpt.value = '';
                                allOpt.textContent = 'Tất cả địa điểm';
                                if (!currentCity) allOpt.selected = true;
                                provinceSelect.appendChild(allOpt);

                                var matched = false;
                                if (Array.isArray(provinces)) {
                                    provinces.forEach(function (p) {
                                        var opt = document.createElement('option');
                                        opt.value = p.name;
                                        opt.textContent = p.name;
                                        opt.setAttribute('data-code', p.code);
                                        if (currentCity && p.name.trim().toLowerCase() === currentCity.toLowerCase()) {
                                            opt.selected = true;
                                            matched = true;
                                        }
                                        provinceSelect.appendChild(opt);
                                    });
                                }

                                // Nếu giá trị hiện tại không khớp với danh sách (dữ liệu cũ), vẫn hiển thị
                                if (currentCity && !matched) {
                                    var customOpt = document.createElement('option');
                                    customOpt.value = currentCity;
                                    customOpt.textContent = currentCity;
                                    customOpt.selected = true;
                                    provinceSelect.appendChild(customOpt);
                                }
                            });
                        })();
                    </script>
                </body>

                </html>