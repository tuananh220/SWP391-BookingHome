<%-- 
    Document   : booking-error
    Created on : Aug 18, 2026, 4:48:10 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Không thể đặt phòng</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=4">
    </head>
    <body>
        <jsp:include page="/views/fragments/customer-header.jsp"/>
        <main class="status-page error-page">
            <div class="status-icon">!</div>
            <p class="eyebrow">ĐẶT PHÒNG CHƯA HOÀN TẤT</p>
            <h1>Không thể xử lý yêu cầu</h1>
            <p>
                <c:out value="${requestScope.error}"/>
            </p>
            <div class="status-actions">
                <a href="javascript:history.back()">Quay lại kiểm tra</a>
                <a class="secondary" href="${pageContext.request.contextPath}/homestays">Tìm homestay khác</a>
            </div>
        </main>
    </body>
</html>

