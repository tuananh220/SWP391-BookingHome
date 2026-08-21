<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${blog.blogId > 0 ? 'Chỉnh sửa' : 'Viết'} bài</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body class="host-body">
        <jsp:include page="/views/host/header.jsp"/>
        <main class="customer-blog-editor-wrap">
            <div class="property-form-heading">
                <p class="eyebrow">HOST STORY</p>
                <h1>${blog.blogId > 0 ? 'Chỉnh sửa bài viết' : 'Viết bài mới'}</h1>
            </div>
            <c:if test="${not empty error}">
                <div class="notice error">
                    <c:out value="${error}"/>
                </div>
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/host/blog-form" class="blog-editor customer-editor">
                <input type="hidden" name="blogId" value="${blog.blogId}">
                <label>Tiêu đề<input name="title" maxlength="200" required value="<c:out value='${blog.title}'/>">
                </label>
                <label>Slug<input name="slug" maxlength="200" placeholder="Để trống để tự tạo" value="<c:out value='${blog.slug}'/>">
                </label>
                <label>URL ảnh đại diện<input type="url" name="thumbnailUrl" maxlength="500" value="<c:out value='${blog.thumbnailUrl}'/>">
                </label>
                <label>Nội dung<textarea name="content" rows="16" required>
                        <c:out value="${blog.content}"/>
                    </textarea>
                </label>
                <div class="property-form-actions">
                    <a href="${pageContext.request.contextPath}/host/blogs">Hủy</a>
                    <button>Gửi bài viết</button>
                </div>
            </form>
        </main>
    </body>
</html>
