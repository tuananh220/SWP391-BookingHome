<%-- 
    Document   : homestay-list
    Created on : Aug 18, 2026, 4:32:10 PM
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
        <title>Tìm homestay</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body>
        <jsp:include page="/views/fragments/customer-header.jsp"/>

        <c:if test="${not empty banners}">
            <section class="public-banners">
                <c:forEach items="${banners}" var="banner">
                    <c:choose>
                        <c:when test="${not empty banner.targetUrl}">
                            <c:url value="${banner.targetUrl}" var="bannerLink"/>
                            <a href="<c:out value='${bannerLink}'/>">
                                <img src="<c:out value='${banner.imageUrl}'/>" alt="<c:out value='${banner.title}'/>">
                                <c:if test="${not empty banner.title}">
                                    <span>
                                        <c:out value="${banner.title}"/>
                                    </span>
                                </c:if>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <div>
                                <img src="<c:out value='${banner.imageUrl}'/>" alt="<c:out value='${banner.title}'/>">
                                <c:if test="${not empty banner.title}">
                                    <span>
                                        <c:out value="${banner.title}"/>
                                    </span>
                                </c:if>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </section>
        </c:if>

        <section class="search-hero">
            <p class="eyebrow">TÌM NƠI DỪNG CHÂN</p>
            <h1>Ở đâu cũng có thể là nhà.</h1>
            <form method="get" action="${pageContext.request.contextPath}/homestays" class="search-form">
                <label class="wide">Từ khóa
                    <input type="text" name="keyword" placeholder="Tên hoặc địa chỉ homestay"
                           value="<c:out value='${param.keyword}'/>">
                </label>
                <label>Thành phố
                    <select name="city">
                        <option value="">Tất cả</option>
                        <c:forEach items="${cities}" var="city">
                            <option value="<c:out value='${city}'/>"
                                    ${param.city == city ? 'selected' : ''}>
                                <c:out value="${city}"/>
                            </option>
                        </c:forEach>
                    </select>
                </label>
                <label>Nhận phòng
                    <input type="date" name="checkIn" id="checkIn" value="<c:out value='${param.checkIn}'/>" >
                </label>
                <label>Trả phòng
                    <input type="date" name="checkOut" value="<c:out value='${param.checkOut}'/>">
                </label>
                <label>Số khách
                    <input type="number" name="guests" min="1" value="<c:out value='${param.guests}'/>">
                </label>
                <label>Giá từ
                    <input type="number" name="minPrice" min="0" step="1000"
                           value="<c:out value='${param.minPrice}'/>">
                </label>
                <label>Giá đến
                    <input type="number" name="maxPrice" min="0" step="1000"
                           value="<c:out value='${param.maxPrice}'/>">
                </label>
                <label>Tiện ích
                    <select name="amenityId">
                        <option value="">Tất cả</option>
                        <c:forEach items="${amenities}" var="amenity">
                            <option value="${amenity.amenityId}"
                                    ${param.amenityId == amenity.amenityId ? 'selected' : ''}>
                                <c:out value="${amenity.amenityName}"/>
                            </option>
                        </c:forEach>
                    </select>
                </label>
                <label>Đánh giá
                    <select name="minRating">
                        <option value="">Tất cả</option>
                        <option value="3" ${param.minRating == '3' ? 'selected' : ''}>Từ 3 sao</option>
                        <option value="4" ${param.minRating == '4' ? 'selected' : ''}>Từ 4 sao</option>
                        <option value="5" ${param.minRating == '5' ? 'selected' : ''}>5 sao</option>
                    </select>
                </label>
                <button type="submit">Tìm kiếm</button>
            </form>
        </section>

        <main class="catalog-wrap">
            <div class="result-heading">
                <div>
                    <p class="eyebrow">KẾT QUẢ</p>
                    <h2>${homestays.size()} homestay phù hợp</h2>
                </div>
                <a class="clear-link" href="${pageContext.request.contextPath}/homestays">Xóa bộ lọc</a>
            </div>

            <c:if test="${not empty requestScope.error}">
                <div class="alert">
                    <c:out value="${requestScope.error}"/>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty homestays}">
                    <section class="empty-state">
                        <h3>Chưa tìm thấy nơi phù hợp</h3>
                        <p>Hãy thử thay đổi ngày, mức giá hoặc tiện ích.</p>
                    </section>
                </c:when>
                <c:otherwise>
                    <section class="property-grid">
                        <c:forEach items="${homestays}" var="item">
                            <article class="property-card">
                                <a class="image-wrap" href="${pageContext.request.contextPath}/homestay-detail?id=${item.homestayId}&checkIn=${param.checkIn}&checkOut=${param.checkOut}&guests=${param.guests}">
                                    <c:choose>
                                        <c:when test="${not empty item.primaryImageUrl}">
                                            <img src="<c:out value='${item.primaryImageUrl}'/>" alt="<c:out value='${item.title}'/>">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="image-placeholder">HOMESTAY</div>
                                        </c:otherwise>
                                    </c:choose>
                                    <span class="guest-badge">Tối đa ${item.maxGuests} khách</span>
                                </a>
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

