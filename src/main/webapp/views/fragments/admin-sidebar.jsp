<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<aside class="admin-sidebar">
    <a class="admin-logo" href="${pageContext.request.contextPath}/admin/dashboard">HOMESTAY<br>
        <span>ADMIN</span>
    </a>
    <nav>
        <a class="${param.active == 'dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dashboard">Tổng quan</a>
        <a class="${param.active == 'users' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users">Tài khoản</a>
        <a class="${param.active == 'homestays' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/homestays">Homestay</a>
        <a class="${param.active == 'reviews' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/reviews">Đánh giá</a>
        <a class="${param.active == 'policies' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/policies">Chính sách hủy</a>
        <a class="${param.active == 'blogs' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/blogs">Blog</a>
        <a class="${param.active == 'banners' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/banners">Banner</a>
    </nav>
    <form method="post" action="${pageContext.request.contextPath}/logout">
        <button>Đăng xuất</button>
    </form>
</aside>
