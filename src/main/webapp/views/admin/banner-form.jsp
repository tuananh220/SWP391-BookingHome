<%-- 
    Document   : banner-form
    Created on : Aug 18, 2026, 9:30:27 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${banner.bannerId > 0 ? 'Chỉnh sửa' : 'Thêm'} Banner</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body class="admin-body">
        <aside class="admin-sidebar">
            <a class="admin-logo" href="${pageContext.request.contextPath}/admin/dashboard">HOMESTAY<br>
                <span>ADMIN</span>
            </a>
            <nav>
                <a href="${pageContext.request.contextPath}/admin/dashboard">Tổng quan</a>
                <a class="active" href="${pageContext.request.contextPath}/admin/banners">Banner</a>
            </nav>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button>Đăng xuất</button>
            </form>
        </aside>
        <main class="admin-main">
            <header class="admin-page-head">
                <div>
                    <p class="eyebrow">BANNER EDITOR</p>
                    <h1>${banner.bannerId > 0 ? 'Chỉnh sửa banner' : 'Thêm banner'}</h1>
                </div>
                <a href="${pageContext.request.contextPath}/admin/banners">← Danh sách</a>
            </header>
            <c:if test="${not empty error}">
                <div class="notice error">
                    <c:out value="${error}"/>
                </div>
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/admin/banner-form" class="admin-editor">
                <input type="hidden" name="bannerId" value="${banner.bannerId}">
                <label>Tiêu đề<input name="title" maxlength="100" value="<c:out value='${banner.title}'/>">
                </label>
                <label>URL hình ảnh<input type="url" name="imageUrl" maxlength="500" required value="<c:out value='${banner.imageUrl}'/>">
                </label>
                <label>URL đích<input name="targetUrl" maxlength="500" placeholder="/homestays?city=Ha Noi hoặc https://..." value="<c:out value='${banner.targetUrl}'/>">
                </label>
                <label>Thứ tự hiển thị<input type="number" name="displayOrder" min="0" required value="${banner.displayOrder}">
                </label>
                <label class="visible-check">
                    <input type="checkbox" name="active" ${banner.active ? 'checked' : ''}> Kích hoạt banner</label>
                    <c:if test="${not empty banner.imageUrl}">
                    <div class="banner-form-preview">
                        <img src="<c:out value='${banner.imageUrl}'/>" alt="Preview">
                    </div>
                </c:if>
                <div class="property-form-actions">
                    <a href="${pageContext.request.contextPath}/admin/banners">Hủy</a>
                    <button>Lưu banner</button>
                </div>
            </form>
        </main>
    </body>
</html>

