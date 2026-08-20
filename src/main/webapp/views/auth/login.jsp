<%-- 
    Document   : login
    Created on : Aug 18, 2026, 3:43:02 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập | Homestay Booking</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Tinos:ital,wght@0,400;0,700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css?v=3">
</head>
<body>
    <main class="auth-shell">
        <section class="brand-panel">
            <span class="eyebrow">HOMESTAY BOOKING</span>
            <h1>Mỗi chuyến đi,<br>một mái nhà.</h1>
            <p>Tìm không gian lưu trú phù hợp và quản lý chuyến đi của bạn dễ dàng.</p>
        </section>
        <section class="form-panel">
            <form method="post" action="${pageContext.request.contextPath}/login" class="auth-card">
                <div class="card-heading">
                    <p class="kicker">Chào mừng trở lại</p>
                    <h2>Đăng nhập</h2>
                    <p>Nhập thông tin tài khoản của bạn.</p>
                </div>
                <c:if test="${not empty sessionScope.flashSuccess}">
                    <div class="alert success">
                        <c:out value="${sessionScope.flashSuccess}"/>
                    </div>
                    <c:remove var="flashSuccess" scope="session"/>
                </c:if>
                <c:if test="${not empty requestScope.error}">
                    <div class="alert error">
                        <c:out value="${requestScope.error}"/>
                    </div>
                </c:if>
                <label>
                    Email
                    <input type="email" name="email" maxlength="100" required autocomplete="email" value="<c:out value='${requestScope.email}'/>">
                </label>
                <label>
                    Mật khẩu
                    <input type="password" name="password" minlength="6" required autocomplete="current-password">
                </label>
                <button type="submit">Đăng nhập</button>
                <p class="switch-link"><a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a></p>
                <p class="switch-link">Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register">Đăng ký ngay</a></p>
            </form>
        </section>
    </main>
</body>
</html>
