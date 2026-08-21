<%-- 
    Document   : booking-create
    Created on : Aug 18, 2026, 4:48:53 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Xác nhận đặt phòng</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body>
        <jsp:include page="/views/fragments/customer-header.jsp"/>

        <main class="checkout-wrap">
            <section class="checkout-main">
                <p class="eyebrow">BƯỚC XÁC NHẬN</p>
                <h1>Xác nhận chuyến đi</h1>

                <article class="trip-property">
                    <div>
                        <p class="eyebrow">
                            <c:out value="${quote.homestay.city}"/>
                        </p>
                        <h2>
                            <c:out value="${quote.homestay.title}"/>
                        </h2>
                        <p>
                            <c:out value="${quote.homestay.address}"/>
                        </p>
                    </div>
                    <span>${quote.totalGuests} khách</span>
                </article>

                <section class="checkout-section">
                    <h2>Thông tin chuyến đi</h2>
                    <div class="checkout-data">
                        <div>
                            <span>Nhận phòng</span>
                            <strong>${quote.checkInDate}</strong>
                        </div>
                        <div>
                            <span>Trả phòng</span>
                            <strong>${quote.checkOutDate}</strong>
                        </div>
                        <div>
                            <span>Số đêm</span>
                            <strong>${quote.totalNights}</strong>
                        </div>
                        <div>
                            <span>Số khách</span>
                            <strong>${quote.totalGuests}</strong>
                        </div>
                    </div>
                </section>

                <section class="checkout-section">
                    <h2>Mã giảm giá</h2>
                    <form method="get" action="${pageContext.request.contextPath}/booking/create" class="voucher-form">
                        <input type="hidden" name="homestayId" value="${quote.homestay.homestayId}">
                        <input type="hidden" name="checkIn" value="${quote.checkInDate}">
                        <input type="hidden" name="checkOut" value="${quote.checkOutDate}">
                        <input type="hidden" name="guests" value="${quote.totalGuests}">
                        <input type="text" name="voucher" maxlength="50" placeholder="Nhập mã voucher"
                               value="<c:out value='${voucherCode}'/>">
                        <button type="submit">Áp dụng</button>
                    </form>
                    <c:if test="${not empty quote.voucher}">
                        <p class="voucher-ok">Đã áp dụng mã <strong>
                                <c:out value="${quote.voucher.voucherCode}"/>
                            </strong> – giảm <fmt:formatNumber value="${quote.voucher.discountRate}" maxFractionDigits="0"/>%</p>
                        </c:if>
                </section>

                <section class="checkout-section">
                    <h2>Chính sách hủy (tự áp dụng khi đặt)</h2>
                    <c:choose>
                        <c:when test="${not empty quote.homestay.cancellationPolicy}">
                            <article class="policy-admin-card">
                                <div class="policy-card-head">
                                    <div>
                                        <small>POLICY</small>
                                        <h2>
                                            <c:out value="${quote.homestay.cancellationPolicy.policyName}"/>
                                        </h2>
                                    </div>
                                    <span class="booking-status status-Active">ACTIVE</span>
                                </div>
                                <p>
                                    <c:out value="${quote.homestay.cancellationPolicy.description}"/>
                                </p>
                                <div class="policy-rules">
                                    <span>
                                        <b>100%</b> trước ${quote.homestay.cancellationPolicy.fullRefundDays} ngày</span>
                                    <span>
                                        <b>
                                            <fmt:formatNumber value="${quote.homestay.cancellationPolicy.partialRefundPercent}" maxFractionDigits="0"/>%</b> trước ${quote.homestay.cancellationPolicy.partialRefundDays} ngày</span>
                                    <span>
                                        <b>0%</b> sau thời hạn</span>
                                </div>
                            </article>
                        </c:when>
                        <c:otherwise>
                            <p>Homestay chưa gắn chính sách hủy.</p>
                        </c:otherwise>
                    </c:choose>
                </section>

                <form method="post" action="${pageContext.request.contextPath}/booking/create" class="confirm-form">
                    <input type="hidden" name="homestayId" value="${quote.homestay.homestayId}">
                    <input type="hidden" name="checkIn" value="${quote.checkInDate}">
                    <input type="hidden" name="checkOut" value="${quote.checkOutDate}">
                    <input type="hidden" name="guests" value="${quote.totalGuests}">
                    <input type="hidden" name="voucherCode" value="<c:out value='${quote.voucher.voucherCode}'/>">

                    <section class="checkout-section">
                        <h2>Phương thức thanh toán</h2>
                        <div class="payment-options">
                            <c:forEach items="${quote.paymentMethods}" var="method" varStatus="status">
                                <label class="payment-option">
                                    <input type="radio" name="paymentMethodId"
                                           value="${method.paymentMethodId}" required
                                           ${status.first ? 'checked' : ''}>
                                    <span>
                                        <strong>
                                            <c:out value="${method.methodName}"/>
                                        </strong>
                                        <small>${method.online ? 'Thanh toán trực tuyến' : 'Thanh toán tại homestay'}</small>
                                    </span>
                                </label>
                            </c:forEach>
                            <c:if test="${empty quote.paymentMethods}">
                                <p>Homestay chưa cấu hình phương thức thanh toán.</p>
                            </c:if>
                        </div>
                    </section>

                    <section class="checkout-section">
                        <h2>Ghi chú cho chủ nhà</h2>
                        <textarea name="note" maxlength="255" rows="4" placeholder="Ví dụ: Tôi dự kiến nhận phòng lúc 14:00">
                        </textarea>
                    </section>

                    <button class="confirm-button" type="submit" ${empty quote.paymentMethods ? 'disabled' : ''}>Xác nhận đặt phòng</button>
                    <p class="terms-note">Khi xác nhận, booking sẽ được tạo với trạng thái chờ xử lý.</p>
                </form>
            </section>

            <aside class="price-summary">
                <h2>Chi tiết giá</h2>
                <c:forEach items="${quote.nights}" var="night">
                    <div class="price-row">
                        <span>${night.stayDate}</span>
                        <span>
                            <fmt:formatNumber value="${night.nightPrice}" pattern="#,##0"/> ₫</span>
                    </div>
                </c:forEach>
                <div class="price-row subtotal">
                    <span>Tạm tính</span>
                    <span>
                        <fmt:formatNumber value="${quote.originalAmount}" pattern="#,##0"/> ₫</span>
                </div>
                <c:if test="${quote.discountAmount > 0}">
                    <div class="price-row discount">
                        <span>Giảm giá</span>
                        <span>− <fmt:formatNumber value="${quote.discountAmount}" pattern="#,##0"/> ₫</span>
                    </div>
                </c:if>
                <div class="price-total">
                    <span>Tổng cộng</span>
                    <strong>
                        <fmt:formatNumber value="${quote.totalAmount}" pattern="#,##0"/> ₫</strong>
                </div>
            </aside>
        </main>
    </body>
</html>

