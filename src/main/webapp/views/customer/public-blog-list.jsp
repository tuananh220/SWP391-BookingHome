<%-- 
    Document   : public-blog-list
    Created on : Aug 18, 2026, 9:18:36 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Câu chuyện du lịch</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3"></head><body>
        <jsp:include page="/views/fragments/customer-header.jsp"/>
        <section class="blog-hero"><p class="eyebrow">TRAVEL JOURNAL</p><h1>Chuyện đi và chuyện ở.</h1><form method="get" action="${pageContext.request.contextPath}/blogs"><input type="search" name="keyword" placeholder="Tìm câu chuyện, địa điểm..." value="<c:out value='${param.keyword}'/>"><button>Tìm kiếm</button></form></section>
        <main class="public-blog-wrap"><c:choose><c:when test="${empty blogs}"><section class="empty-state"><h3>Chưa có bài viết phù hợp</h3></section></c:when><c:otherwise><section class="public-blog-grid"><c:forEach items="${blogs}" var="blog"><article class="public-blog-card"><a href="${pageContext.request.contextPath}/blog?slug=${blog.slug}" class="public-blog-image"><c:choose><c:when test="${not empty blog.thumbnailUrl}"><img src="<c:out value='${blog.thumbnailUrl}'/>" alt="<c:out value='${blog.title}'/>"></c:when><c:otherwise><span>TRAVEL STORY</span></c:otherwise></c:choose></a><div><small><c:out value="${blog.authorName}"/> · ${blog.createdAt}</small><h2><a href="${pageContext.request.contextPath}/blog?slug=${blog.slug}"><c:out value="${blog.title}"/></a></h2><p><c:out value="${blog.content}"/></p><a class="read-more" href="${pageContext.request.contextPath}/blog?slug=${blog.slug}">Đọc câu chuyện →</a></div></article></c:forEach></section></c:otherwise></c:choose></main></body></html>

