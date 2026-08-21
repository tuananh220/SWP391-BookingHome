<%-- 
    Document   : payment
    Created on : Aug 18, 2026, 5:36:29 PM
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
        <title>Thanh toán booking #${booking.bookingId}</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body class="payment-body">
        <jsp:include page="/views/fragments/customer-header.jsp"/>

        <main class="payment-wrap">
            <section class="payment-brand-panel">
                <p class="eyebrow">THANH TOÁN AN TOÀN</p>
                <h1>
                    <c:out value="${payment.paymentMethodName}"/>
                </h1>
                <p>Đây là màn hình mô phỏng thanh toán phục vụ cho project học tập. Không có khoản tiền thật nào được xử lý.</p>
                <div class="gateway-mark">
                    <c:out value="${payment.paymentMethodCode}"/>
                </div>
            </section>

            <section class="payment-confirm-panel">
                <p class="eyebrow">THÔNG TIN GIAO DỊCH</p>
                <h2>Xác nhận thanh toán</h2>
                <div class="payment-summary-line">
                    <span>Booking</span>
                    <strong>#${booking.bookingId}</strong>
                </div>
                <div class="payment-summary-line">
                    <span>Homestay</span>
                    <strong>
                        <c:out value="${booking.homestayTitle}"/>
                    </strong>
                </div>
                <div class="payment-summary-line">
                    <span>Thời gian</span>
                    <strong>${booking.checkInDate} → ${booking.checkOutDate}</strong>
                </div>
                <div class="payment-summary-line payment-amount">
                    <span>Số tiền</span>
                    <strong>
                        <fmt:formatNumber value="${payment.amount}" pattern="#,##0"/> ₫</strong>
                </div>

                <form method="post" action="${pageContext.request.contextPath}/customer/payment">
                    <input type="hidden" name="bookingId" value="${booking.bookingId}">
                    <input type="hidden" name="paymentId" value="${payment.paymentId}">
                    <button type="submit" class="pay-now-button">Mô phỏng thanh toán thành công</button>
                </form>
                <p class="demo-warning">Sau khi xác nhận, Payment chuyển thành Completed và Booking chuyển thành Confirmed.</p>
            </section>
        </main>
    </body>
</html>

