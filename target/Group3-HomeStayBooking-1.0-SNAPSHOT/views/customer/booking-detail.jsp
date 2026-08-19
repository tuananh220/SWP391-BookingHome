<%-- 
    Document   : booking-detail
    Created on : Aug 18, 2026, 5:07:01 PM
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
        <title>Booking #${booking.bookingId}</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body>
        <jsp:include page="/views/fragments/customer-header.jsp"/>

        <main class="booking-detail-wrap">
            <c:if test="${not empty sessionScope.flashError}">
                <div class="notice error"><c:out value="${sessionScope.flashError}"/></div>
                <c:remove var="flashError" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.flashSuccess}">
                <div class="notice success"><c:out value="${sessionScope.flashSuccess}"/></div>
                <c:remove var="flashSuccess" scope="session"/>
            </c:if>

            <section class="booking-detail-head">
                <div>
                    <p class="eyebrow">BOOKING #${booking.bookingId}</p>
                    <h1><c:out value="${booking.homestayTitle}"/></h1>
                </div>
                <span class="booking-status status-${booking.bookingStatus}"><c:out value="${booking.bookingStatus}"/></span>
            </section>

            <div class="booking-detail-grid">
                <div>
                    <section class="detail-panel">
                        <h2>Thông tin lưu trú</h2>
                        <div class="detail-data-grid">
                            <div><span>Ngày nhận phòng</span><strong>${booking.checkInDate}</strong></div>
                            <div><span>Ngày trả phòng</span><strong>${booking.checkOutDate}</strong></div>
                            <div><span>Số khách</span><strong>${booking.totalGuests}</strong></div>
                            <div><span>Ngày tạo</span><strong>${booking.createdAt}</strong></div>
                        </div>
                        <c:if test="${not empty booking.note}"><p class="booking-note"><strong>Ghi chú:</strong> <c:out value="${booking.note}"/></p></c:if>
                        </section>

                        <section class="detail-panel">
                            <h2>Thanh toán</h2>
                            <div class="detail-data-grid">
                                <div><span>Phương thức</span><strong><c:out value="${empty booking.paymentMethodName ? 'Không phát sinh' : booking.paymentMethodName}"/></strong></div>
                            <div><span>Trạng thái</span><strong><c:out value="${empty booking.paymentStatus ? 'Không phát sinh' : booking.paymentStatus}"/></strong></div>
                        </div>
                        <c:if test="${not empty pendingOnlinePayment}">
                            <a class="payment-action" href="${pageContext.request.contextPath}/customer/payment?bookingId=${booking.bookingId}">Thanh toán ngay</a>
                        </c:if>
                    </section>

                    <c:if test="${booking.bookingStatus == 'Cancelled'}">
                        <section class="detail-panel cancelled-panel">
                            <h2>Thông tin hủy</h2>
                            <p><strong>Người hủy:</strong> <c:out value="${booking.cancelledBy}"/></p>
                            <p><strong>Lý do:</strong> <c:out value="${booking.cancelReason}"/></p>
                            <p><strong>Tiền hoàn:</strong> <fmt:formatNumber value="${booking.refundAmount}" pattern="#,##0"/> ₫</p>
                        </section>
                    </c:if>

                    <c:if test="${booking.bookingStatus == 'Confirmed'}">
                        <section class="detail-panel stay-change-invite">
                            <div><p class="eyebrow">THỜI GIAN LƯU TRÚ</p><h2>Bạn muốn thay đổi ngày trả phòng?</h2><p>Gửi yêu cầu gia hạn hoặc trả phòng sớm đến chủ nhà.</p></div>
                            <a href="${pageContext.request.contextPath}/customer/stay-change-form?bookingId=${booking.bookingId}">Tạo yêu cầu</a>
                        </section>
                    </c:if>

                    <c:if test="${canReview}">
                        <section class="detail-panel review-invite">
                            <div>
                                <p class="eyebrow">CHUYẾN ĐI ĐÃ HOÀN THÀNH</p>
                                <h2>Trải nghiệm của bạn thế nào?</h2>
                                <p>Hãy chia sẻ đánh giá để giúp những khách hàng khác.</p>
                            </div>
                            <a href="${pageContext.request.contextPath}/customer/review/create?bookingId=${booking.bookingId}">Viết đánh giá</a>
                        </section>
                    </c:if>

                    <c:if test="${booking.bookingStatus == 'Pending' || booking.bookingStatus == 'Confirmed'}">
                        <section class="detail-panel cancel-panel">
                            <h2>Hủy booking</h2>
                            <p>Số tiền hoàn dự kiến theo chính sách: <strong><fmt:formatNumber value="${estimatedRefund}" pattern="#,##0"/> ₫</strong></p>
                            <form method="post" action="${pageContext.request.contextPath}/customer/cancel-booking" class="cancel-form">
                                <input type="hidden" name="bookingId" value="${booking.bookingId}">
                                <label>Lý do hủy
                                    <textarea name="reason" rows="3" maxlength="255" required placeholder="Cho chúng tôi biết lý do bạn muốn hủy"></textarea>
                                </label>
                                <button type="submit">Xác nhận hủy booking</button>
                            </form>
                        </section>
                    </c:if>
                </div>

                <aside class="detail-price-panel">
                    <h2>Chi tiết chi phí</h2>
                    <div><span>Giá ban đầu</span><span><fmt:formatNumber value="${booking.originalAmount}" pattern="#,##0"/> ₫</span></div>
                    <div class="discount"><span>Giảm giá</span><span>− <fmt:formatNumber value="${booking.discountAmount}" pattern="#,##0"/> ₫</span></div>
                    <div class="grand-total"><span>Tổng cộng</span><strong><fmt:formatNumber value="${booking.totalAmount}" pattern="#,##0"/> ₫</strong></div>
                    <c:if test="${not empty booking.cancellationPolicyName}">
                        <div class="policy-summary">
                            <small>Chính sách hủy</small>
                            <strong><c:out value="${booking.cancellationPolicyName}"/></strong>
                            <p>Hoàn 100% trước ${booking.fullRefundDaysSnapshot} ngày; hoàn <fmt:formatNumber value="${booking.partialRefundPercentSnapshot}" maxFractionDigits="0"/>% trước ${booking.partialRefundDaysSnapshot} ngày.</p>
                        </div>
                    </c:if>
                </aside>
            </div>
        </main>
    </body>
</html>




