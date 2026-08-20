<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên mật khẩu | Homestay Booking</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css?v=3">
</head>
<body>
    <main class="auth-shell">
        <section class="brand-panel">
            <span class="eyebrow">KHÔI PHỤC TÀI KHOẢN</span>
            <h1>Quên<br>mật khẩu?</h1>
            <p>Nhập email đã đăng ký để nhận mã OTP và đặt lại mật khẩu.</p>
        </section>
        <section class="form-panel">
            <form method="post" action="${pageContext.request.contextPath}/forgot-password" class="auth-card">
                <div class="card-heading">
                    <p class="kicker">Bước 1</p>
                    <h2>Quên mật khẩu</h2>
                </div>
                <c:if test="${not empty requestScope.error}">
                    <div class="alert error">
                        <c:out value="${requestScope.error}"/>
                    </div>
                </c:if>
                <label>
                    Email
                    <input type="email" name="email" maxlength="100" required value="<c:out value='${requestScope.email}'/>">
                </label>
                <button type="submit">Gửi mã OTP</button>
                <p class="switch-link"><a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a></p>
            </form>
        </section>
    </main>
</body>
</html>
