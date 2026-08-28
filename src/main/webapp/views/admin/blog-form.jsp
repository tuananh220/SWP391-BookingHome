<%-- 
    Document   : blog-form
    Created on : Aug 18, 2026, 8:51:06 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${blog.blogId > 0 ? 'Chỉnh sửa' : 'Viết'} Blog</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body class="admin-body">
        <jsp:include page="/views/fragments/admin-sidebar.jsp">
            <jsp:param name="active" value="blogs"/>
        </jsp:include>
        <main class="admin-main">
            <header class="admin-page-head">
                <div>
                    <p class="eyebrow">BLOG EDITOR</p>
                    <h1>${blog.blogId > 0 ? 'Chỉnh sửa bài viết' : 'Viết bài mới'}</h1>
                </div>
                <a href="${pageContext.request.contextPath}/admin/blogs">← Danh sách</a>
            </header>
            <c:if test="${not empty error}">
                <div class="notice error">
                    <c:out value="${error}"/>
                </div>
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/admin/blog-form" class="blog-editor">
                <input type="hidden" name="blogId" value="${blog.blogId}">
                <label>Tiêu đề<input name="title" maxlength="200" required value="<c:out value='${blog.title}'/>">
                </label>
                <label>Slug<input name="slug" maxlength="200" placeholder="Để trống để tự tạo từ tiêu đề" value="<c:out value='${blog.slug}'/>">
                    <small>Ví dụ: kinh-nghiem-du-lich-ha-noi</small>
                </label>
                <label>URL ảnh đại diện<input type="url" name="thumbnailUrl" maxlength="500" value="<c:out value='${blog.thumbnailUrl}'/>">
                </label>
                <label>Nội dung<textarea name="content" rows="20" required placeholder="Nội dung bài viết...">
                        <c:out value="${blog.content}"/>
                    </textarea>
                </label>
                <label class="visible-check">
                    <input type="checkbox" name="published" ${blog.published ? 'checked' : ''}> Xuất bản bài viết</label>
                <div class="property-form-actions">
                    <a href="${pageContext.request.contextPath}/admin/blogs">Hủy</a>
                    <button>Lưu bài viết</button>
                </div>
            </form>
        </main>
    </body>
</html>

