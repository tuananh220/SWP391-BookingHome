<%-- 
    Document   : home
    Created on : Aug 18, 2026, 3:43:19 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang chủ | Homestay Booking</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css?v=3">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=4">
</head>
<body class="dashboard-body">
    <jsp:include page="/views/fragments/customer-header.jsp"/>
    <main class="dashboard">
        <p class="kicker">ĐĂNG NHẬP THÀNH CÔNG</p>
        <h1>Xin chào, <c:out value="${sessionScope.currentUser.fullName}"/> ! </h1>
        <p> Bộ khung authentication đã hoạt động. Bạn có thể tiếp tục phát triển tìm kiếm và đặt homestay. </p>
        <div class="info-card">
            <span>Vai trò</span>
            <strong><c:out value="${sessionScope.currentUser.roleName}"/></strong>
            <span>Email</span>
            <strong><c:out value="${sessionScope.currentUser.email}"/></strong>
        </div>
    </main>
</body>
</html>
