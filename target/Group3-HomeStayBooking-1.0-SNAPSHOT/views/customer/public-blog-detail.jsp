<%-- 
    Document   : public-blog-detail
    Created on : Aug 18, 2026, 9:18:09 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title><c:out value="${blog.title}"/></title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3"></head><body>
        <jsp:include page="/views/fragments/customer-header.jsp"/>
        <article class="public-blog-detail"><header><p class="eyebrow">TRAVEL JOURNAL</p><h1><c:out value="${blog.title}"/></h1><p>Bởi <strong><c:out value="${blog.authorName}"/></strong> · ${blog.createdAt}</p></header><c:if test="${not empty blog.thumbnailUrl}"><img class="blog-cover" src="<c:out value='${blog.thumbnailUrl}'/>" alt="<c:out value='${blog.title}'/>"></c:if><div class="blog-article-content"><c:out value="${blog.content}"/></div></article></body></html>

