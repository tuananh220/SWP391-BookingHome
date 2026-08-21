<%-- 
    Document   : booking-detail
    Created on : Aug 18, 2026, 5:49:51 PM
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
        <title>Booking #${booking.bookingId} | Chủ nhà</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body class="host-body">
        <jsp:include page="/views/host/header.jsp"/>

        <main class="host-detail-wrap">
            <c:if test="${not empty sessionScope.flashSuccess}">
                <div class="notice success">
                    <c:out value="${sessionScope.flashSuccess}"/>
                </div>
                <c:remove var="flashSuccess" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.flashError}">
                <div class="notice error">
                    <c:out value="${sessionScope.flashError}"/>
                </div>
                <c:remove var="flashError" scope="session"/>
            </c:if>

            <section class="host-detail-head">
                <div>
                    <p class="eyebrow">BOOKING #${booking.bookingId}</p>
                    <h1>
                        <c:out value="${booking.homestayTitle}"/>
                    </h1>
                </div>
                <span class="booking-status status-${booking.bookingStatus}">
                    <c:out value="${booking.bookingStatus}"/>
                </span>
            </section>

            <div class="host-detail-grid">
                <div>
                    <section class="host-panel">
                        <h2>Thông tin booking</h2>
                        <div class="host-info-grid">
                            <div>
                                <span>Mã booking</span>
                                <strong>#${booking.bookingId}</strong>
                            </div>
                            <div>
                                <span>Mã homestay</span>
                                <strong>#${booking.homestayId}</strong>
                            </div>
                            <div>
                                <span>Trạng thái</span>
                                <strong><c:out value="${booking.bookingStatus}"/></strong>
                            </div>
                            <div>
                                <span>Thời điểm tạo</span>
                                <strong>${booking.createdAt}</strong>
                            </div>
                        </div>
                    </section>
                    <section class="host-panel">
                        <h2>Thông tin khách hàng</h2>
                        <div class="host-info-grid">
                            <div>
                                <span>Họ và tên</span>
                                <strong>
                                    <c:out value="${booking.customerName}"/>
                                </strong>
                            </div>
                            <div>
                                <span>Số điện thoại</span>
                                <strong>
                                    <c:out value="${empty booking.customerPhone ? 'Chưa cập nhật' : booking.customerPhone}"/>
                                </strong>
                            </div>
                        </div>
                    </section>
                    <section class="host-panel">
                        <h2>Thông tin lưu trú</h2>
                        <div class="host-info-grid">
                            <div>
                                <span>Nhận phòng</span>
                                <strong>${booking.checkInDate}</strong>
                            </div>
                            <div>
                                <span>Trả phòng</span>
                                <strong>${booking.checkOutDate}</strong>
                            </div>
                            <div>
                                <span>Số khách</span>
                                <strong>${booking.totalGuests}</strong>
                            </div>
                            <div>
                                <span>Ngày tạo</span>
                                <strong>${booking.createdAt}</strong>
                            </div>
                        </div>
                        <c:if test="${not empty booking.note}">
                            <p class="booking-note">
                                <strong>Ghi chú:</strong> <c:out value="${booking.note}"/>
                            </p>
                        </c:if>
                    </section>
                    <section class="host-panel">
                        <h2>Giá theo từng đêm</h2>
                        <c:choose>
                            <c:when test="${empty bookingNights}">
                                <p>Chưa có dữ liệu giá theo đêm.</p>
                            </c:when>
                            <c:otherwise>
                                <div class="host-info-grid">
                                    <c:forEach items="${bookingNights}" var="night">
                                        <div>
                                            <span>${night.stayDate}</span>
                                            <strong><fmt:formatNumber value="${night.nightPrice}" pattern="#,##0"/> ₫</strong>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </section>
                    <c:if test="${not empty booking.rejectReason}">
                        <section class="host-panel rejected-note">
                            <h2>Lý do từ chối</h2>
                            <p>
                                <c:out value="${booking.rejectReason}"/>
                            </p>
                        </section>
                    </c:if>
                    <c:if test="${booking.bookingStatus == 'Cancelled'}">
                        <section class="host-panel rejected-note">
                            <h2>Thông tin hủy</h2>
                            <p><strong>Người hủy:</strong> <c:out value="${booking.cancelledBy}"/></p>
                            <p><strong>Thời điểm:</strong> ${booking.cancelledAt}</p>
                            <p><strong>Lý do:</strong> <c:out value="${booking.cancelReason}"/></p>
                            <p><strong>Tiền hoàn:</strong> <fmt:formatNumber value="${booking.refundAmount}" pattern="#,##0"/> ₫</p>
                        </section>
                    </c:if>
                </div>

                <aside>
                    <section class="host-panel host-payment-box">
                        <h2>Thanh toán</h2>
                        <p>
                            <span>Phương thức</span>
                            <strong>
                                <c:out value="${empty booking.paymentMethodName ? 'Miễn phí' : booking.paymentMethodName}"/>
                            </strong>
                        </p>
                        <p>
                            <span>Trạng thái</span>
                            <strong>
                                <c:out value="${empty booking.paymentStatus ? 'Không phát sinh' : booking.paymentStatus}"/>
                            </strong>
                        </p>
                        <p>
                            <span>Giá ban đầu</span>
                            <strong><fmt:formatNumber value="${booking.originalAmount}" pattern="#,##0"/> ₫</strong>
                        </p>
                        <p>
                            <span>Giảm giá</span>
                            <strong>- <fmt:formatNumber value="${booking.discountAmount}" pattern="#,##0"/> ₫</strong>
                        </p>
                        <p class="host-total">
                            <span>Tổng tiền</span>
                            <strong>
                                <fmt:formatNumber value="${booking.totalAmount}" pattern="#,##0"/> ₫</strong>
                        <c:if test="${not empty booking.voucherCode}">
                            <p><span>Voucher</span><strong><c:out value="${booking.voucherCode}"/></strong></p>
                        </c:if>
                        </p>

                    <c:if test="${not empty booking.cancellationPolicyName}">
                        <section class="host-panel">
                            <h2>Chính sách hủy</h2>
                            <p><strong><c:out value="${booking.cancellationPolicyName}"/></strong></p>
                            <p><small>Hoàn 100% trước ${booking.fullRefundDaysSnapshot} ngày; hoàn <fmt:formatNumber value="${booking.partialRefundPercentSnapshot}" maxFractionDigits="0"/>% trước ${booking.partialRefundDaysSnapshot} ngày.</small></p>
                        </section>
                    </c:if>
                    </section>

                    <c:if test="${booking.bookingStatus == 'Pending' && booking.paymentOnline}">
                        <div class="notice">Booking đang chờ khách hoàn tất thanh toán online và sẽ tự động xác nhận.</div>
                    </c:if>

                    <c:if test="${canProcess}">
                        <section class="host-panel action-panel">
                            <form method="post" action="${pageContext.request.contextPath}/host/booking-action">
                                <input type="hidden" name="bookingId" value="${booking.bookingId}">
                                <button class="accept-button" type="submit" name="action" value="confirm">Xác nhận booking</button>
                            </form>
                            <form method="post" action="${pageContext.request.contextPath}/host/booking-action" class="reject-form">
                                <input type="hidden" name="bookingId" value="${booking.bookingId}">
                                <label>Lý do từ chối<textarea name="reason" maxlength="255" rows="3" required>
                                    </textarea>
                                </label>
                                <button class="reject-button" type="submit" name="action" value="reject">Từ chối booking</button>
                            </form>
                        </section>
                    </c:if>
                </aside>
            </div>
        </main>
    </body>
</html>

