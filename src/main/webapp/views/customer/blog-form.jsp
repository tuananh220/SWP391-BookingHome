<%-- 
    Document   : blog-form
    Created on : Aug 18, 2026, 9:09:09 PM
    Author     : Admin
--%>

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
    <body>
        <jsp:include page="/views/fragments/customer-header.jsp"/>
        <main class="customer-blog-editor-wrap">
            <div class="property-form-heading">
                <p class="eyebrow">TRAVEL STORY</p>
                <h1>${blog.blogId > 0 ? 'Chỉnh sửa bài viết' : 'Chia sẻ câu chuyện'}</h1>
                <p>Bài viết sẽ được gửi cho Admin kiểm duyệt trước khi xuất bản.</p>
            </div>
            <c:if test="${not empty error}">
                <div class="notice error">
                    <c:out value="${error}"/>
                </div>
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/customer/blog-form" class="blog-editor customer-editor">
                <input type="hidden" name="blogId" value="${blog.blogId}">
                <label>Tiêu đề<input name="title" maxlength="200" required placeholder="Chuyến đi đáng nhớ của tôi..." value="<c:out value='${blog.title}'/>">
                </label>
                <label>Slug<input name="slug" maxlength="200" placeholder="Để trống để tự tạo" value="<c:out value='${blog.slug}'/>">
                </label>
                <label>URL ảnh đại diện<input type="url" name="thumbnailUrl" maxlength="500" placeholder="https://..." value="<c:out value='${blog.thumbnailUrl}'/>">
                </label>
                <label>Nội dung<textarea name="content" rows="20" required placeholder="Hãy kể về hành trình, địa điểm và trải nghiệm của bạn...">
                        <c:out value="${blog.content}"/>
                    </textarea>
                </label>
                <div class="customer-blog-note">Khi lưu hoặc chỉnh sửa, bài viết sẽ chuyển về trạng thái chờ Admin duyệt.</div>
                <div class="property-form-actions">
                    <a href="${pageContext.request.contextPath}/customer/blogs">Hủy</a>
                    <button>Gửi bài viết</button>
                </div>
            </form>
        </main>
    </body>
</html>

