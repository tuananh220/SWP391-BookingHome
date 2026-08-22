<%-- 
    Document   : register
    Created on : Aug 18, 2026, 3:43:09 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký | Homestay Booking</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Tinos:ital,wght@0,400;0,700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css?v=4">
</head>
<body>
<main class="auth-shell">
    <section class="brand-panel">
        <span class="eyebrow">BẮT ĐẦU HÀNH TRÌNH</span>
        <h1>Tạo tài khoản<br>trong vài phút.</h1>
        <p>Đăng ký khách hàng để đặt phòng, hoặc chủ nhà để đăng homestay.</p>
    </section>

    <section class="form-panel">
        <form method="post" action="${pageContext.request.contextPath}/register" class="auth-card">
            <div class="card-heading">
                <p class="kicker">Thành viên mới</p>
                <h2>Đăng ký</h2>
            </div>

            <c:if test="${not empty requestScope.error}">
                <div class="alert error"><c:out value="${requestScope.error}"/></div>
            </c:if>

            <label>Họ và tên
                <input type="text" name="fullName" maxlength="100" required
                       autocomplete="name" value="<c:out value='${requestScope.fullName}'/>">
            </label>
            <label>Email
                <input type="email" name="email" maxlength="100" required
                       autocomplete="email" value="<c:out value='${requestScope.email}'/>">
            </label>
            <label>Số điện thoại
                <input type="tel" name="phoneNumber" maxlength="10"
                       autocomplete="tel" value="<c:out value='${requestScope.phoneNumber}'/>">
            </label>
            <fieldset class="role-choice">
                <legend>Bạn muốn đăng ký với vai trò</legend>
                <label class="role-card">
                    <input type="radio" name="roleName" value="Customer"
                           ${empty requestScope.roleName || requestScope.roleName == 'Customer' ? 'checked' : ''}>
                    <span>
                        <strong>Khách hàng</strong>
                        Đặt phòng, lưu homestay yêu thích và viết bài.
                    </span>
                </label>
                <label class="role-card">
                    <input type="radio" name="roleName" value="Home Owner"
                           ${requestScope.roleName == 'Home Owner' ? 'checked' : ''}>
                    <span>
                        <strong>Chủ nhà</strong>
                        Đăng homestay, nhận booking và quản lý lịch.
                    </span>
                </label>
            </fieldset>
            <div class="field-grid">
                <label>Mật khẩu
                    <input type="password" name="password" minlength="6" required
                           autocomplete="new-password">
                </label>
                <label>Xác nhận mật khẩu
                    <input type="password" name="confirmPassword" minlength="6" required
                           autocomplete="new-password">
                </label>
            </div>

            <button type="submit">Tạo tài khoản</button>
            <p class="switch-link">Đã có tài khoản?
                <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
            </p>
        </form>
    </section>
</main>
</body>
</html>

