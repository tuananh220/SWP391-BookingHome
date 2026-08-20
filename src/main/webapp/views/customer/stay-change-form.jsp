<%-- 
    Document   : stay-change-form
    Created on : Aug 18, 2026, 7:22:36 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thay đổi thời gian lưu trú</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
</head>
<body>
    <jsp:include page="/views/fragments/customer-header.jsp"/>
    <main class="change-form-wrap">
        <div class="property-form-heading">
            <p class="eyebrow">BOOKING #${booking.bookingId}</p>
            <h1>Thay đổi thời gian lưu trú</h1>
            <p><c:out value="${booking.homestayTitle}"/> · Trả phòng hiện tại: ${booking.checkOutDate} </p>
        </div>
        <c:if test="${not empty error}">
            <div class="notice error">
                <c:out value="${error}"/>
            </div>
        </c:if>
        <form method="post" action="${pageContext.request.contextPath}/customer/stay-change-form" class="change-form">
            <input type="hidden" name="bookingId" value="${booking.bookingId}">
            <label>
                Loại yêu cầu
                <select name="requestType" required>
                    <option value="Extension" ${param.requestType == 'Extension' ? 'selected' : ''}> Gia hạn thời gian lưu trú </option>
                    <option value="EarlyCheckout" ${param.requestType == 'EarlyCheckout' ? 'selected' : ''}> Trả phòng sớm </option>
                </select>
            </label>
            <label>
                Ngày trả phòng mong muốn
                <input type="date" name="requestedCheckOutDate" required value="<c:out value='${param.requestedCheckOutDate}'/>">
            </label>
            <label>
                Ghi chú
                <textarea name="customerNote" rows="5" maxlength="255" placeholder="Lý do hoặc yêu cầu dành cho chủ nhà"><c:out value="${param.customerNote}"/></textarea>
            </label>
            <button type="submit">Gửi yêu cầu</button>
            <small>Hệ thống sẽ tính tiền thêm hoặc tiền hoàn dự kiến sau khi gửi.</small>
        </form>
    </main>
</body>
</html>
