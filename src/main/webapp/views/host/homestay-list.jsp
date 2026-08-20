<%-- 
    Document   : homestay-list
    Created on : Aug 18, 2026, 6:18:53 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Homestay của tôi</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
</head>
<body class="host-body">
    <jsp:include page="/views/host/header.jsp"/>
    <main class="host-wrap">
        <div class="host-page-head">
            <div>
                <p class="eyebrow">TÀI SẢN CỦA TÔI</p>
                <h1>Quản lý homestay</h1>
            </div>
            <a class="host-create-button" href="${pageContext.request.contextPath}/host/homestay-form"> + Thêm homestay </a>
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
            <c:when test="${empty homestays}">
                <section class="empty-state">
                    <h3>Bạn chưa có homestay</h3>
                    <p>Hãy thêm nơi lưu trú đầu tiên để bắt đầu nhận booking.</p>
                </section>
            </c:when>
            <c:otherwise>
                <section class="host-property-grid">
                    <c:forEach items="${homestays}" var="item">
                        <article class="host-property-card">
                            <div class="host-property-image">
                                <c:choose>
                                    <c:when test="${not empty item.primaryImageUrl}">
                                        <img src="<c:out value='${item.primaryImageUrl}'/>" alt="Homestay">
                                    </c:when>
                                    <c:otherwise>
                                        <span>HOMESTAY</span>
                                    </c:otherwise>
                                </c:choose>
                                <span class="booking-status status-${item.status}"><c:out value="${item.status}"/></span>
                            </div>
                            <div class="host-property-content">
                                <p><c:out value="${item.district}"/> , <c:out value="${item.city}"/></p>
                                <h2><c:out value="${item.title}"/></h2>
                                <div class="host-property-stats">
                                    <span><strong><fmt:formatNumber value="${item.pricePerNight}" pattern="#,##0"/> ₫</strong>/đêm </span>
                                    <span> ★ <fmt:formatNumber value="${item.averageRating}" maxFractionDigits="1"/> (${item.reviewCount}) </span>
                                </div>
                                <div class="host-property-actions">
                                    <a href="${pageContext.request.contextPath}/host/homestay-form?id=${item.homestayId}">Chỉnh sửa</a>
                                    <a href="${pageContext.request.contextPath}/host/schedule?homestayId=${item.homestayId}">Lịch & giá</a>
                                    <form method="post" action="${pageContext.request.contextPath}/host/homestay-action">
                                        <input type="hidden" name="homestayId" value="${item.homestayId}">
                                        <c:choose>
                                            <c:when test="${item.status == 'Hidden'}">
                                                <button type="submit" name="action" value="show">Gửi duyệt lại</button>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="submit" name="action" value="hide">Ẩn</button>
                                            </c:otherwise>
                                        </c:choose>
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
