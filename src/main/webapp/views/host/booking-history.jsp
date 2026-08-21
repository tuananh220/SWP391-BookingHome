<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Lịch sử booking | Chủ nhà</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body class="host-body">
        <jsp:include page="/views/host/header.jsp"/>
        <main class="host-wrap">
            <div class="host-page-head">
                <div>
                    <p class="eyebrow">REVENUE MANAGEMENT</p>
                    <h1>Lịch sử booking</h1>
                    <p>Theo dõi booking và hiệu suất hủy của các homestay.</p>
                </div>
            </div>
            <c:if test="${not empty error}">
                <div class="notice error"><c:out value="${error}"/></div>
            </c:if>
            <form method="get" action="${pageContext.request.contextPath}/host/booking-history" class="admin-filter">
                <select name="status">
                    <option value="">Tất cả trạng thái</option>
                    <option value="Confirmed" ${param.status == 'Confirmed' ? 'selected' : ''}>Đã xác nhận</option>
                    <option value="Completed" ${param.status == 'Completed' ? 'selected' : ''}>Hoàn thành</option>
                    <option value="Rejected" ${param.status == 'Rejected' ? 'selected' : ''}>Từ chối</option>
                    <option value="Cancelled" ${param.status == 'Cancelled' ? 'selected' : ''}>Đã hủy</option>
                </select>
                <select name="homestayId">
                    <option value="">Tất cả homestay</option>
                    <c:forEach items="${homestays}" var="homestay">
                        <option value="${homestay.homestayId}" ${param.homestayId == homestay.homestayId ? 'selected' : ''}><c:out value="${homestay.title}"/></option>
                    </c:forEach>
                </select>
                <label>Từ ngày<input type="date" name="fromDate" value="<c:out value='${param.fromDate}'/>"/></label>
                <label>Đến ngày<input type="date" name="toDate" value="<c:out value='${param.toDate}'/>"/></label>
                <button type="submit">Lọc lịch sử</button>
                <a href="${pageContext.request.contextPath}/host/booking-history">Xóa lọc</a>
            </form>
            <div class="analytics-grid">
                <section class="analytics-panel status-panel">
                    <div class="panel-head"><div><p class="eyebrow">CANCELLATION PERFORMANCE</p><h2>Thống kê hủy</h2></div></div>
                    <div class="status-chart">
                        <div class="status-chart-row"><div><strong>Host đã hủy</strong><span>${cancellationSummary.hostCancelledBookings} booking</span></div><div class="status-track"><span class="status-Cancelled" style="width:${cancellationSummary.cancellationRate}%"></span></div></div>
                        <div class="status-chart-row"><div><strong>Tỷ lệ hủy sau accept</strong><span>${cancellationSummary.hostCancelledBookings} / ${cancellationSummary.acceptedBookings} booking · ${cancellationSummary.cancellationRate}%</span></div><div class="status-track"><span class="status-Rejected" style="width:${cancellationSummary.cancellationRate}%"></span></div></div>
                    </div>
                </section>
            </div>
            <c:choose>
                <c:when test="${empty bookings}"><section class="empty-state"><h3>Không có booking phù hợp</h3></section></c:when>
                <c:otherwise>
                    <div class="host-table-wrap"><table class="host-table"><thead><tr><th>Booking</th><th>Khách hàng</th><th>Lưu trú</th><th>Thanh toán</th><th>Tổng tiền</th><th>Trạng thái</th><th></th></tr></thead><tbody>
                        <c:forEach items="${bookings}" var="booking"><tr><td><small>#${booking.bookingId}</small><strong><c:out value="${booking.homestayTitle}"/></strong></td><td><strong><c:out value="${booking.customerName}"/></strong><small><c:out value="${booking.customerPhone}"/></small></td><td><strong>${booking.checkInDate}</strong><small>đến ${booking.checkOutDate} · ${booking.totalGuests} khách</small></td><td><strong><c:out value="${booking.paymentMethodName}"/></strong><small><c:out value="${booking.paymentStatus}"/></small></td><td><strong><fmt:formatNumber value="${booking.totalAmount}" pattern="#,##0"/> ₫</strong></td><td><span class="booking-status status-${booking.bookingStatus}"><c:out value="${booking.bookingStatus}"/></span></td><td><a class="table-action" href="${pageContext.request.contextPath}/host/booking-detail?id=${booking.bookingId}">Chi tiết</a></td></tr></c:forEach>
                    </tbody></table></div>
                </c:otherwise>
            </c:choose>
        </main>
    </body>
</html>