<%-- 
    Document   : blog-list
    Created on : Aug 18, 2026, 9:09:50 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bài viết của tôi</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
</head>
<body>
    <jsp:include page="/views/fragments/customer-header.jsp"/>
    <main class="customer-blog-wrap">
        <div class="host-page-head">
            <div>
                <p class="eyebrow">CHIA SẺ HÀNH TRÌNH</p>
                <h1>Bài viết của tôi</h1>
                <p>Viết về trải nghiệm du lịch và gửi Admin kiểm duyệt.</p>
            </div>
            <a class="host-create-button" href="${pageContext.request.contextPath}/customer/blog-form"> + Viết bài mới </a>
        </div>
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
        <c:choose>
            <c:when test="${empty blogs}">
                <section class="empty-state">
                    <h3>Bạn chưa có bài viết</h3>
                    <p>Hãy chia sẻ câu chuyện từ chuyến đi của mình.</p>
                </section>
            </c:when>
            <c:otherwise>
                <section class="customer-blog-grid">
                    <c:forEach items="${blogs}" var="blog">
                        <article class="customer-blog-card">
                            <div class="customer-blog-thumb">
                                <c:choose>
                                    <c:when test="${not empty blog.thumbnailUrl}">
                                        <img src="<c:out value='${blog.thumbnailUrl}'/>" alt="Blog">
                                    </c:when>
                                    <c:otherwise>
                                        <span>TRAVEL STORY</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="customer-blog-content">
                                <span class="booking-status ${blog.published ? 'status-Active' : 'status-Pending'}">
                                    ${blog.published ? 'Đã xuất bản' : 'Chờ Admin duyệt'}
                                </span>
                                <h2><c:out value="${blog.title}"/></h2>
                                <p>/<c:out value="${blog.slug}"/></p>
                                <small> Cập nhật: <c:out value="${blog.updatedAtText}"/></small>
                                <div class="customer-blog-actions">
                                    <a href="${pageContext.request.contextPath}/customer/blog-form?id=${blog.blogId}">Chỉnh sửa</a>
                                    <form method="post" action="${pageContext.request.contextPath}/customer/blog-action">
                                        <input type="hidden" name="blogId" value="${blog.blogId}">
                                        <button name="action" value="delete">Xóa</button>
                                    </form>
                                </div>
                            </div>
                        </article>
                    </c:forEach>
                </section>
            </c:otherwise>
        </c:choose>
    </main>
</body>
</html>
