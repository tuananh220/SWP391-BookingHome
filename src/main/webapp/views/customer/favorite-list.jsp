<%-- 
    Document   : favorite-list
    Created on : Aug 18, 2026, 7:39:13 PM
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
        <title>Homestay yêu thích</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body>
        <jsp:include page="/views/fragments/customer-header.jsp"/>
        <main class="catalog-wrap">
            <div class="result-heading">
                <div>
                    <p class="eyebrow">BỘ SƯU TẬP CỦA TÔI</p>
                    <h2>Homestay yêu thích</h2>
                </div>
                <span>${homestays.size()} địa điểm</span>
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
                        <h3>Danh sách yêu thích đang trống</h3>
                        <p>Lưu lại những homestay bạn muốn ghé thăm.</p>
                        <a class="primary-link" href="${pageContext.request.contextPath}/homestays">Khám phá ngay</a>
                    </section>
                </c:when>
                <c:otherwise>
                    <section class="property-grid">
                        <c:forEach items="${homestays}" var="item">
                            <article class="property-card favorite-card">
                                <a class="image-wrap" href="${pageContext.request.contextPath}/homestay-detail?id=${item.homestayId}">
                                    <c:choose>
                                        <c:when test="${not empty item.primaryImageUrl}">
                                            <img src="<c:out value='${item.primaryImageUrl}'/>" alt="<c:out value='${item.title}'/>">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="image-placeholder">HOMESTAY</div>
                                        </c:otherwise>
                                    </c:choose>
                                </a>
                                <form method="post" action="${pageContext.request.contextPath}/customer/favorite-action" class="remove-favorite">
                                    <input type="hidden" name="homestayId" value="${item.homestayId}">
                                    <button type="submit" name="action" value="remove" title="Xóa khỏi yêu thích">♥</button>
                                </form>
                                <div class="property-content">
                                    <div class="location">
                                        <c:out value="${item.district}"/>, <c:out value="${item.city}"/>
                                    </div>
                                    <h3>
                                        <a href="${pageContext.request.contextPath}/homestay-detail?id=${item.homestayId}">
                                            <c:out value="${item.title}"/>
                                        </a>
                                    </h3>
                                    <div class="card-bottom">
                                        <p>
                                            <strong>
                                                <fmt:formatNumber value="${item.pricePerNight}" pattern="#,##0"/> ₫</strong> / đêm</p>
                                        <p class="rating">★ <fmt:formatNumber value="${item.averageRating}" maxFractionDigits="1"/> <span>(${item.reviewCount})</span>
                                        </p>
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

