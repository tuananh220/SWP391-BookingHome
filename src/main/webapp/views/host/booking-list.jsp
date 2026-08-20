<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Yêu cầu đặt phòng | Chủ nhà</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
</head>
<body class="host-body">
    <jsp:include page="/views/host/header.jsp"/>
    <main class="host-wrap">
        <div class="host-page-head">
            <div>
                <p class="eyebrow">QUẢN LÝ ĐẶT PHÒNG</p>
                <h1>Yêu cầu của khách</h1>
            </div>
            <span>Xin chào, <strong><c:out value="${sessionScope.currentUser.fullName}"/></strong></span>
        </div>
        <c:if test="${not empty sessionScope.flashSuccess}">
            <div class="notice success">
                <c:out value="${sessionScope.flashSuccess}"/>
            </div>
            <c:remove var="flashSuccess" scope="session"/>
        </c:if>
        <nav class="status-tabs">
            <a class="${empty selectedStatus ? 'active' : ''}" href="${pageContext.request.contextPath}/host/bookings">Tất cả</a>
            <a class="${selectedStatus == 'Pending' ? 'active' : ''}" href="${pageContext.request.contextPath}/host/bookings?status=Pending">
                Chờ xử lý
            </a>
            <a class="${selectedStatus == 'Confirmed' ? 'active' : ''}" href="${pageContext.request.contextPath}/host/bookings?status=Confirmed">
                Đã xác nhận
            </a>
            <a class="${selectedStatus == 'Completed' ? 'active' : ''}" href="${pageContext.request.contextPath}/host/bookings?status=Completed">
                Hoàn thành
            </a>
            <a class="${selectedStatus == 'Rejected' ? 'active' : ''}" href="${pageContext.request.contextPath}/host/bookings?status=Rejected">
                Từ chối
            </a>
            <a class="${selectedStatus == 'Cancelled' ? 'active' : ''}" href="${pageContext.request.contextPath}/host/bookings?status=Cancelled">
                Đã hủy
            </a>
        </nav>
        <c:choose>
            <c:when test="${empty bookings}">
                <section class="empty-state">
                    <h3>Không có booking phù hợp</h3>
                    <p>Các yêu cầu mới sẽ xuất hiện tại đây.</p>
                </section>
            </c:when>
            <c:otherwise>
                <div class="host-table-wrap">
                    <table class="host-table">
                        <thead>
                            <tr>
                                <th>Booking</th>
                                <th>Khách hàng</th>
                                <th>Lưu trú</th>
                                <th>Thanh toán</th>
                                <th>Tổng tiền</th>
                                <th>Trạng thái</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${bookings}" var="booking">
                                <tr>
                                    <td><small>#${booking.bookingId}</small><strong><c:out value="${booking.homestayTitle}"/></strong></td>
                                    <td><strong><c:out value="${booking.customerName}"/></strong><small><c:out value="${booking.customerPhone}"/></small></td>
                                    <td><strong>${booking.checkInDate}</strong><small>đến ${booking.checkOutDate} · ${booking.totalGuests} khách</small></td>
                                    <td><strong><c:out value="${booking.paymentMethodName}"/></strong><small><c:out value="${booking.paymentStatus}"/></small></td>
                                    <td><strong><fmt:formatNumber value="${booking.totalAmount}" pattern="#,##0"/> ₫</strong></td>
                                    <td><span class="booking-status status-${booking.bookingStatus}"><c:out value="${booking.bookingStatus}"/></span></td>
                                    <td><a class="table-action" href="${pageContext.request.contextPath}/host/booking-detail?id=${booking.bookingId}">Chi tiết</a></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </main>
</body>
</html>
