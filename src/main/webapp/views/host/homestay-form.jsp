<%-- Document : homestay-form Created on : Aug 18, 2026, 6:07:20 PM Author : Admin --%>

    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
        <%@ taglib prefix="c" uri="jakarta.tags.core" %>
            <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>${form.homestayId > 0 ? 'Chỉnh sửa' : 'Thêm'} homestay</title>
                    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
                    <script src="${pageContext.request.contextPath}/assets/js/address-selector.js"></script>
                </head>

                <body class="host-body">
                    <jsp:include page="/views/host/header.jsp" />

                    <main class="property-form-wrap">
                        <div class="property-form-heading">
                            <p class="eyebrow">${form.homestayId > 0 ? 'CẬP NHẬT THÔNG TIN' : 'NƠI LƯU TRÚ MỚI'}</p>
                            <h1>${form.homestayId > 0 ? 'Chỉnh sửa homestay' : 'Thêm homestay'}</h1>
                        </div>
                        <c:if test="${not empty requestScope.error}">
                            <div class="notice error">
                                <c:out value="${requestScope.error}" />
                            </div>
                        </c:if>

                        <form method="post" action="${pageContext.request.contextPath}/host/homestay-form"
                            class="property-form">
                            <input type="hidden" name="homestayId" value="${form.homestayId}">
                            <section class="form-section">
                                <h2>Thông tin cơ bản</h2>
                                <label class="full">Tên homestay<input type="text" name="title" maxlength="200" required
                                        value="<c:out value='${form.title}'/>"></label>
                                <label class="full">Mô tả<textarea name="description"
                                        rows="6"><c:out value="${form.description}"/></textarea></label>
                                <label>Giá mỗi đêm<input type="number" name="pricePerNight" min="0" step="1000" required
                                        value="${form.pricePerNight}"></label>
                                <label>Số khách tối đa<input type="number" name="maxGuests" min="1" required
                                        value="${form.maxGuests > 0 ? form.maxGuests : 1}"></label>
                            </section>

                            <section class="form-section">
                                <h2>Địa điểm</h2>
                                <label class="full">Địa chỉ<input type="text" name="address" maxlength="255" required
                                        value="<c:out value='${form.address}'/>"></label>
                                <label>Tỉnh / Thành phố
                                    <select id="provinceSelect" name="city" required>
                                        <option value="">-- Đang tải... --</option>
                                    </select>
                                </label>
                                <label>Phường / Xã
                                    <select id="wardSelect" name="district">
                                        <option value="">-- Chọn Tỉnh/Thành trước --</option>
                                    </select>
                                </label>
                                <label>Vĩ độ<input type="number" name="latitude" min="-90" max="90" step="0.00000001"
                                        value="${form.latitude}"></label>
                                <label>Kinh độ<input type="number" name="longitude" min="-180" max="180"
                                        step="0.00000001" value="${form.longitude}"></label>
                            </section>

                            <section class="form-section">
                                <h2>Chính sách hủy</h2>
                                <p class="form-hint">Chính sách bạn chọn sẽ tự áp dụng khi khách đặt phòng homestay này.
                                </p>
                                <div class="policy-picker-grid">
                                    <c:forEach items="${policies}" var="policy">
                                        <label class="policy-picker-card">
                                            <input type="radio" name="cancellationPolicyId" value="${policy.policyId}"
                                                required ${form.cancellationPolicyId==policy.policyId ? 'checked' : ''
                                                }>
                                            <div class="policy-card-head">
                                                <div><small>POLICY #${policy.policyId}</small>
                                                    <h2>
                                                        <c:out value="${policy.policyName}" />
                                                    </h2>
                                                </div>
                                                <span class="booking-status status-Active">ACTIVE</span>
                                            </div>
                                            <p>
                                                <c:out value="${policy.description}" />
                                            </p>
                                            <div class="policy-rules">
                                                <span><b>100%</b> trước ${policy.fullRefundDays} ngày</span>
                                                <span><b>
                                                        <fmt:formatNumber value="${policy.partialRefundPercent}"
                                                            maxFractionDigits="0" />%
                                                    </b> trước ${policy.partialRefundDays} ngày</span>
                                                <span><b>0%</b> sau thời hạn</span>
                                            </div>
                                        </label>
                                    </c:forEach>
                                </div>
                            </section>

                            <section class="form-section">
                                <h2>Tiện ích</h2>
                                <p class="form-hint"><a href="${pageContext.request.contextPath}/host/amenities">Quản lý
                                        danh mục tiện ích</a></p>
                                <div class="option-grid full">
                                    <c:forEach items="${amenities}" var="amenity"><label class="check-option"><input
                                                type="checkbox" name="amenityIds" value="${amenity.amenityId}"
                                                ${form.amenityIds.contains(amenity.amenityId) ? 'checked' : '' }><span>
                                                <c:out value="${amenity.amenityName}" />
                                            </span></label></c:forEach>
                                </div>
                            </section>

                            <section class="form-section">
                                <h2>Thanh toán chấp nhận</h2>
                                <div class="option-grid full">
                                    <c:forEach items="${paymentMethods}" var="method"><label class="check-option"><input
                                                type="checkbox" name="paymentMethodIds"
                                                value="${method.paymentMethodId}"
                                                ${form.paymentMethodIds.contains(method.paymentMethodId) ? 'checked'
                                                : '' }><span>
                                                <c:out value="${method.methodName}" /> <small>${method.online ? 'Online'
                                                    : 'Tại homestay'}</small>
                                            </span></label></c:forEach>
                                </div>
                            </section>

                            <section class="form-section">
                                <h2>Hình ảnh</h2>
                                <label class="full">URL hình ảnh – mỗi URL một dòng
                                    <textarea id="imageUrlsInput" name="imageUrls" rows="7" required
                                        placeholder="https://example.com/image-1.jpg&#10;https://example.com/image-2.jpg"><c:out value="${form.imageUrlsText}"/></textarea>
                                    <small>Dòng đầu tiên được dùng làm ảnh đại diện. Chỉ chấp nhận đuôi: .jpg, .jpeg, .png, .gif, .webp, .avif</small>
                                    <span id="imageUrlsError" style="color:#c0392b; font-size:0.85rem; display:none;"></span>
                                </label>
                            </section>

                            <div class="property-form-actions"><a
                                    href="${pageContext.request.contextPath}/host/homestays">Hủy</a><button
                                    type="submit">${form.homestayId > 0 ? 'Lưu thay đổi' : 'Tạo homestay'}</button>
                            </div>
                        </form>
                    </main>

                    <script>
                        initAddressSelector({
                            provinceSelectId: 'provinceSelect',
                            wardSelectId: 'wardSelect',
                            contextPath: '${pageContext.request.contextPath}',
                            initialCity: '<c:out value="${form.city}"/>',
                            initialDistrict: '<c:out value="${form.district}"/>'
                        });

                        // ── Validate URL ảnh khi submit form ──────────────────
                        // Chỉ cho phép các URL kết thúc bằng đuôi ảnh hợp lệ
                        var ALLOWED_EXTENSIONS = ['.jpg', '.jpeg', '.png', '.gif', '.webp', '.avif'];

                        function isValidImageUrl(url) {
                            // Loại bỏ query string và fragment (đoạn sau ? hoặc #) trước khi kiểm tra đuôi
                            var cleanUrl = url.split('?')[0].split('#')[0].toLowerCase();
                            // Kiểm tra xem URL có kết thúc bằng một trong các đuôi hợp lệ không
                            return ALLOWED_EXTENSIONS.some(function (ext) {
                                return cleanUrl.endsWith(ext);
                            });
                        }

                        document.querySelector('.property-form').addEventListener('submit', function (e) {
                            var textarea = document.getElementById('imageUrlsInput');
                            var errorSpan = document.getElementById('imageUrlsError');

                            // Lấy tất cả các dòng, bỏ qua dòng trống
                            var lines = textarea.value.split('\n').map(function (l) { return l.trim(); });
                            var nonEmptyLines = lines.filter(function (l) { return l.length > 0; });

                            // Tìm những URL không hợp lệ
                            var invalidUrls = nonEmptyLines.filter(function (url) {
                                return !isValidImageUrl(url);
                            });

                            if (invalidUrls.length > 0) {
                                // Dừng submit và hiển thị lỗi
                                e.preventDefault();
                                errorSpan.style.display = 'block';
                                errorSpan.textContent = 'URL không hợp lệ (phải kết thúc bằng .jpg/.jpeg/.png/.gif/.webp/.avif):\n'
                                    + invalidUrls.join('\n');
                                textarea.focus();
                            } else {
                                // OK — ẩn thông báo lỗi nếu có
                                errorSpan.style.display = 'none';
                            }
                        });
                    </script>
                </body>

                </html>