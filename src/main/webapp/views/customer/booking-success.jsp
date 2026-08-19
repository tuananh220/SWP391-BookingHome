<%-- 
    Document   : booking-success
    Created on : Aug 18, 2026, 4:48:34 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đặt phòng thành công</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=4">
    </head>
    <body>
        <jsp:include page="/views/fragments/customer-header.jsp"/>
        <main class="status-page">
            <div class="status-icon">✓</div>
            <p class="eyebrow">YÊU CẦU ĐÃ ĐƯỢC GỬI</p>
            <h1>Đặt phòng thành công</h1>
            <p>Mã booking của bạn là <strong>#${bookingId}</strong>. Booking đang chờ chủ nhà xác nhận hoặc hoàn tất thanh toán.</p>
            <div class="status-actions">
                <a href="${pageContext.request.contextPath}/homestays">Tiếp tục khám phá</a>
                <a class="secondary" href="${pageContext.request.contextPath}/home">Về trang chủ</a>
            </div>
        </main>
    </body>
</html>


