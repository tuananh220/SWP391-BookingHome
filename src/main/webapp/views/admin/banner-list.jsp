<%-- 
    Document   : banner-list
    Created on : Aug 18, 2026, 9:31:00 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Banner</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body class="admin-body">
        <aside class="admin-sidebar">
            <a class="admin-logo" href="${pageContext.request.contextPath}/admin/dashboard">HOMESTAY<br>
                <span>ADMIN</span>
            </a>
            <nav>
                <a href="${pageContext.request.contextPath}/admin/dashboard">Tổng quan</a>
                <a href="${pageContext.request.contextPath}/admin/users">Tài khoản</a>
                <a href="${pageContext.request.contextPath}/admin/homestays">Homestay</a>
                <a href="${pageContext.request.contextPath}/admin/blogs">Blog</a>
                <a class="active" href="${pageContext.request.contextPath}/admin/banners">Banner</a>
            </nav>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button>Đăng xuất</button>
            </form>
        </aside>
        <main class="admin-main">
            <header class="admin-page-head">
                <div>
                    <p class="eyebrow">HOMEPAGE CONTENT</p>
                    <h1>Quản lý Banner</h1>
                </div>
                <a class="admin-create" href="${pageContext.request.contextPath}/admin/banner-form">+ Thêm banner</a>
            </header>
            <c:if test="${not empty sessionScope.flashSuccess}">
                <div class="notice success">
                    <c:out value="${sessionScope.flashSuccess}"/>
                </div>
                <c:remove var="flashSuccess" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.flashError}">
                <div class="notice error">
                    <c:out value="${sessionScope.flashError}"/>
                </div>
                <c:remove var="flashError" scope="session"/>
            </c:if>
            <section class="banner-admin-list">
                <c:forEach items="${banners}" var="banner">
                    <article class="banner-admin-card ${banner.active ? '' : 'inactive'}">
                        <div class="banner-preview">
                            <img src="<c:out value='${banner.imageUrl}'/>" alt="Banner">
                            <span class="booking-status ${banner.active ? 'status-Active' : 'status-Blocked'}">${banner.active ? 'Active' : 'Inactive'}</span>
                        </div>
                        <div class="banner-admin-info">
                            <span>Thứ tự ${banner.displayOrder}</span>
                            <h2>
                                <c:out value="${empty banner.title ? 'Banner không tiêu đề' : banner.title}"/>
                            </h2>
                            <p>Liên kết: <c:out value="${empty banner.targetUrl ? 'Không có' : banner.targetUrl}"/>
                            </p>
                            <div class="admin-blog-actions">
                                <a href="${pageContext.request.contextPath}/admin/banner-form?id=${banner.bannerId}">Chỉnh sửa</a>
                                <form method="post" action="${pageContext.request.contextPath}/admin/banner-action">
                                    <input type="hidden" name="bannerId" value="${banner.bannerId}">
                                    <button name="action" value="${banner.active ? 'deactivate' : 'activate'}">${banner.active ? 'Ngừng' : 'Kích hoạt'}</button>
                                    <button class="danger" name="action" value="delete">Xóa</button>
                                </form>
                            </div>
                        </div>
                    </article>
                </c:forEach>
            </section>
        </main>
    </body>
</html>

