<%-- 
    Document   : schedule
    Created on : Aug 18, 2026, 6:13:16 PM
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
    <title> Lịch và giá | <c:out value="${homestay.title}"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
</head>
<body class="host-body">
    <jsp:include page="/views/host/header.jsp"/>
    <main class="host-wrap">
        <div class="host-page-head">
            <div>
                <p class="eyebrow">60 NGÀY TIẾP THEO</p>
                <h1>Lịch & giá</h1>
                <p><c:out value="${homestay.title}"/> · Giá cơ bản <fmt:formatNumber value="${homestay.pricePerNight}" pattern="#,##0"/> ₫</p>
            </div>
        </div>
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
        <div class="schedule-legend">
            <span><i class="available-dot"></i> Còn trống </span>
            <span><i class="locked-dot"></i> Host khóa </span>
            <span><i class="booked-dot"></i> Đã có booking </span>
        </div>
        <div class="schedule-table-wrap">
            <table class="schedule-table">
                <thead>
                    <tr>
                        <th>Ngày</th>
                        <th>Giá áp dụng</th>
                        <th>Giá tùy chỉnh</th>
                        <th>Mở bán</th>
                        <th>Lý do khóa</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${schedules}" var="day">
                        <c:set var="formId" value="schedule-${day.scheduleDate}"/>
                        <tr class="${day.booked ? 'booked-row' : (!day.available ? 'locked-row' : '')}">
                            <td><strong>${day.scheduleDate}</strong><c:if test="${day.booked}"><small>Booking #${day.bookingId}</small></c:if></td>
                            <td><strong><fmt:formatNumber value="${day.effectivePrice}" pattern="#,##0"/> ₫</strong></td>
                            <td>
                                <input form="${formId}" type="number" name="customPrice" min="0" step="1000" value="${day.customPrice}" placeholder="Giá mặc định" ${day.booked ? 'disabled' : ''}>
                            </td>
                            <td>
                                <label class="switch-label">
                                    <input form="${formId}" type="checkbox" name="available" ${day.available && !day.booked ? 'checked' : ''} ${day.booked ? 'disabled' : ''}>
                                    <span>${day.booked ? 'Đã đặt' : (day.available ? 'Mở' : 'Khóa')}</span>
                                </label>
                            </td>
                            <td>
                                <input form="${formId}" type="text" name="lockReason" maxlength="100" value="<c:out value='${day.lockReason}'/>" placeholder="Bảo trì..." ${day.booked ? 'disabled' : ''}>
                            </td>
                            <td class="schedule-actions">
                                <c:choose>
                                    <c:when test="${day.booked}">
                                        <a href="${pageContext.request.contextPath}/host/booking-detail?id=${day.bookingId}">Xem booking</a>
                                    </c:when>
                                    <c:otherwise>
                                        <form id="${formId}" method="post" action="${pageContext.request.contextPath}/host/schedule">
                                            <input type="hidden" name="homestayId" value="${homestay.homestayId}">
                                            <input type="hidden" name="scheduleDate" value="${day.scheduleDate}">
                                            <button type="submit" name="action" value="save">Lưu</button>
                                            <c:if test="${day.scheduleId > 0}">
                                                <button class="clear-button" type="submit" name="action" value="clear">Mặc định</button>
                                            </c:if>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </main>
</body>
</html>
