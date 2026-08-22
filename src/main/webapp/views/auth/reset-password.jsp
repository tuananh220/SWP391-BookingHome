<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đặt lại mật khẩu | Homestay Booking</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css?v=3">
    </head>
    <body>
        <main class="auth-shell">
            <section class="brand-panel">
                <span class="eyebrow">MÃ XÁC NHẬN</span>
                <h1>Nhập OTP<br>và mật khẩu mới.</h1>
                <p>Mã OTP có hiệu lực trong 15 phút.</p>
            </section>
            <section class="form-panel">
                <form method="post" action="${pageContext.request.contextPath}/reset-password" class="auth-card">
                    <div class="card-heading">
                        <p class="kicker">Bước 2</p>
                        <h2>Đặt lại mật khẩu</h2>
                    </div>
                    <c:if test="${not empty requestScope.otp}">
                        <div class="alert success">Mã OTP của bạn: <strong><c:out value="${requestScope.otp}"/></strong> (demo, dùng để nhập bên dưới)</div>
                    </c:if>
                    <c:if test="${not empty requestScope.error}">
                        <div class="alert error"><c:out value="${requestScope.error}"/></div>
                    </c:if>
                    <input type="hidden" name="email" value="<c:out value='${requestScope.email}'/>">
                    <label>Email
                        <input type="email" value="<c:out value='${requestScope.email}'/>" disabled>
                    </label>
                    <label>Mã OTP
                        <input type="text" name="otp" maxlength="6" required
                               value="<c:out value='${requestScope.otp}'/>">
                    </label>
                    <div class="field-grid">
                        <label>Mật khẩu mới
                            <input type="password" name="password" minlength="6" required>
                        </label>
                        <label>Xác nhận mật khẩu
                            <input type="password" name="confirmPassword" minlength="6" required>
                        </label>
                    </div>
                    <button type="submit">Đổi mật khẩu</button>
                    <p class="switch-link">
                        <a href="${pageContext.request.contextPath}/forgot-password">Gửi lại OTP</a>
                    </p>
                </form>
            </section>
        </main>
    </body>
</html>
