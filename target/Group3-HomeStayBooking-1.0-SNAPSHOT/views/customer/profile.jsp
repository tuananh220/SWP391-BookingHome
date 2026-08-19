<%-- 
    Document   : profile
    Created on : Aug 18, 2026, 3:43:25 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ sơ | Homestay Booking</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Tinos:ital,wght@0,400;0,700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css?v=3">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=4">
</head>
<body class="dashboard-body">
<jsp:include page="/views/fragments/customer-header.jsp"/>
<main class="profile-wrap">
    <form method="post" action="${pageContext.request.contextPath}/profile" class="auth-card profile-card">
        <div class="card-heading">
            <p class="kicker">TÀI KHOẢN CỦA TÔI</p>
            <h2>Chỉnh sửa hồ sơ</h2>
        </div>
        <c:if test="${not empty requestScope.success}">
            <div class="alert success"><c:out value="${requestScope.success}"/></div>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <div class="alert error"><c:out value="${requestScope.error}"/></div>
        </c:if>
        <label>Email
            <input type="email" value="<c:out value='${sessionScope.currentUser.email}'/>" disabled>
        </label>
        <label>Họ và tên
            <input type="text" name="fullName" maxlength="100" required
                   value="<c:out value='${sessionScope.currentUser.fullName}'/>">
        </label>
        <label>Số điện thoại
            <input type="tel" name="phoneNumber" maxlength="20"
                   value="<c:out value='${sessionScope.currentUser.phoneNumber}'/>">
        </label>
        <label>Địa chỉ
            <input type="text" name="address" maxlength="255"
                   value="<c:out value='${sessionScope.currentUser.address}'/>">
        </label>
        <label>URL ảnh đại diện
            <input type="url" name="avatarUrl" maxlength="500"
                   value="<c:out value='${sessionScope.currentUser.avatarUrl}'/>">
        </label>
        <button type="submit">Lưu thay đổi</button>
    </form>

    <form method="post" action="${pageContext.request.contextPath}/change-password" class="auth-card profile-card">
        <div class="card-heading">
            <p class="kicker">BẢO MẬT</p>
            <h2>Đổi mật khẩu</h2>
        </div>
        <c:if test="${not empty requestScope.passwordSuccess}">
            <div class="alert success"><c:out value="${requestScope.passwordSuccess}"/></div>
        </c:if>
        <c:if test="${not empty requestScope.passwordError}">
            <div class="alert error"><c:out value="${requestScope.passwordError}"/></div>
        </c:if>
        <label>Mật khẩu hiện tại
            <input type="password" name="currentPassword" minlength="6" required>
        </label>
        <label>Mật khẩu mới
            <input type="password" name="newPassword" minlength="6" required>
        </label>
        <label>Xác nhận mật khẩu mới
            <input type="password" name="confirmPassword" minlength="6" required>
        </label>
        <button type="submit">Đổi mật khẩu</button>
    </form>
</main>
</body>
</html>

