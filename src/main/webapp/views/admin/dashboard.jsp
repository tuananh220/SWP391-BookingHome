<%-- 
    Document   : dashboard
    Created on : Aug 18, 2026, 7:54:38 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Admin Dashboard</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body class="admin-body">
        <aside class="admin-sidebar">
            <a class="admin-logo" href="${pageContext.request.contextPath}/admin/dashboard">HOMESTAY<br>
                <span>ADMIN</span>
            </a>
            <nav>
                <a class="active" href="${pageContext.request.contextPath}/admin/dashboard">Tổng quan</a>
                <a href="${pageContext.request.contextPath}/admin/users">Tài khoản</a>
                <a href="${pageContext.request.contextPath}/admin/homestays">Homestay</a>
                <a href="${pageContext.request.contextPath}/admin/reviews">Đánh giá</a>
                <a href="${pageContext.request.contextPath}/admin/policies">Chính sách hủy</a>
                <a href="${pageContext.request.contextPath}/admin/blogs">Blog</a>
                <a href="${pageContext.request.contextPath}/admin/banners">Banner</a>
            </nav>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button>Đăng xuất</button>
            </form>
        </aside>
        <main class="admin-main">
            <header class="admin-page-head">
                <div>
                    <p class="eyebrow">HỆ THỐNG</p>
                    <h1>Tổng quan quản trị</h1>
                </div>
                <span>Xin chào, <strong>${sessionScope.currentUser.fullName}</strong>
                </span>
            </header>
            <section class="admin-metrics">
                <article class="accent">
                    <span>Doanh thu</span>
                    <strong>
                        <fmt:formatNumber value="${summary.totalRevenue}" pattern="#,##0"/> ₫</strong>
                </article>
                <article>
                    <span>Khách hàng</span>
                    <strong>${summary.totalCustomers}</strong>
                </article>
                <article>
                    <span>Chủ nhà</span>
                    <strong>${summary.totalOwners}</strong>
                </article>
                <article>
                    <span>Homestay</span>
                    <strong>${summary.totalHomestays}</strong>
                </article>
                <article>
                    <span>Booking</span>
                    <strong>${summary.totalBookings}</strong>
                </article>
                <article>
                    <span>Chờ duyệt homestay</span>
                    <strong>${summary.pendingHomestays}</strong>
                </article>
                <article>
                    <span>Homestay hoạt động</span>
                    <strong>${summary.activeHomestays}</strong>
                </article>
                <article>
                    <span>Booking chờ xử lý</span>
                    <strong>${summary.pendingBookings}</strong>
                </article>
            </section>
            <section class="admin-shortcuts">
                <h2>Truy cập nhanh</h2>
                <div>
                    <a href="${pageContext.request.contextPath}/admin/users">
                        <b>Quản lý tài khoản</b>
                        <span>Xem, chỉnh sửa và khóa người dùng →</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/homestays">
                        <b>Duyệt homestay</b>
                        <span>Xử lý các homestay đang Pending →</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/reviews">
                        <b>Quản lý đánh giá</b>
                        <span>Ẩn hoặc xử lý báo cáo review →</span>
                    </a>
                </div>
            </section>
        </main>
    </body>
</html>

