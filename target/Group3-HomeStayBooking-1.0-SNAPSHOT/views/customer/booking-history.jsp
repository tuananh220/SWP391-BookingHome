<%-- 
    Document   : booking-history
    Created on : Aug 18, 2026, 5:07:23 PM
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
        <title>Lịch sử đặt phòng</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body>
        <jsp:include page="/views/fragments/customer-header.jsp"/>

        <main class="history-wrap">
            <div class="history-heading">
                <p class="eyebrow">CHUYẾN ĐI CỦA TÔI</p>
                <h1>Lịch sử đặt phòng</h1>
                <p>Theo dõi các yêu cầu đặt phòng, thanh toán và chuyến đi đã hoàn thành.</p>
            </div>

            <c:if test="${not empty sessionScope.flashSuccess}">
                <div class="notice success"><c:out value="${sessionScope.flashSuccess}"/></div>
                <c:remove var="flashSuccess" scope="session"/>
            </c:if>

            <c:choose>
                <c:when test="${empty bookings}">
                    <section class="empty-state">
                        <h3>Bạn chưa có booking nào</h3>
                        <p>Khám phá các homestay và bắt đầu chuyến đi đầu tiên.</p>
                        <a class="primary-link" href="${pageContext.request.contextPath}/homestays">Tìm homestay</a>
                    </section>
                </c:when>
                <c:otherwise>
                    <section class="booking-list">
                        <c:forEach items="${bookings}" var="booking">
                            <article class="booking-row">
                                <div class="booking-thumb">
                                    <c:choose>
                                        <c:when test="${not empty booking.homestayImageUrl}">
                                            <img src="<c:out value='${booking.homestayImageUrl}'/>" alt="Homestay">
                                        </c:when>
                                        <c:otherwise><span>HOMESTAY</span></c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="booking-row-main">
                                    <div class="booking-row-head">
                                        <div>
                                            <small>Booking #${booking.bookingId}</small>
                                            <h2><c:out value="${booking.homestayTitle}"/></h2>
                                        </div>
                                        <span class="booking-status status-${booking.bookingStatus}"><c:out value="${booking.bookingStatus}"/></span>
                                    </div>
                                    <div class="booking-facts">
                                        <span><b>Nhận phòng</b>${booking.checkInDate}</span>
                                        <span><b>Trả phòng</b>${booking.checkOutDate}</span>
                                        <span><b>Khách</b>${booking.totalGuests}</span>
                                        <span><b>Tổng tiền</b><fmt:formatNumber value="${booking.totalAmount}" pattern="#,##0"/> ₫</span>
                                    </div>
                                    <div class="booking-row-foot">
                                        <span>Thanh toán: <strong><c:out value="${empty booking.paymentStatus ? 'Không phát sinh' : booking.paymentStatus}"/></strong></span>
                                        <a href="${pageContext.request.contextPath}/customer/booking-detail?id=${booking.bookingId}">Xem chi tiết →</a>
                                    </div>
                                </div>
                            </article>
                        </c:forEach>
                    </section>
                </c:otherwise>
            </c:choose>
        </main>
    </body>
</html>

