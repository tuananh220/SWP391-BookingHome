<%-- 
    Document   : homestay-detail
    Created on : Aug 18, 2026, 4:31:33 PM
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
    <title><c:out value="${homestay.title}"/> | Homestay</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
</head>
<body>
    <jsp:include page="/views/fragments/customer-header.jsp"/>
    <main class="detail-wrap">
        <section class="detail-heading">
            <p class="eyebrow"><c:out value="${homestay.city}"/></p>
            <h1><c:out value="${homestay.title}"/></h1>
            <div class="detail-meta">
                <span> ★ <fmt:formatNumber value="${homestay.averageRating}" maxFractionDigits="1"/> (${homestay.reviewCount} đánh giá) </span>
                <span><c:out value="${homestay.address}"/></span>
                <span>Tối đa ${homestay.maxGuests} khách</span>
            </div>
            <c:if test="${not empty sessionScope.currentUser && sessionScope.currentUser.roleName == 'Customer'}">
                <form class="favorite-detail-form" method="post" action="${pageContext.request.contextPath}/customer/favorite-action">
                    <input type="hidden" name="homestayId" value="${homestay.homestayId}">
                    <input type="hidden" name="returnPage" value="detail">
                    <button type="submit" name="action" value="${isFavorite ? 'remove' : 'add'}">
                        ${isFavorite ? '♥ Đã yêu thích' : '♡ Thêm vào yêu thích'}
                    </button>
                </form>
            </c:if>
        </section>
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
        <section class="gallery ${homestay.images.size() == 1 ? 'single' : ''}">
            <c:choose>
                <c:when test="${empty homestay.images}">
                    <div class="gallery-placeholder">
                        Chưa có hình ảnh
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${homestay.images}" var="image" begin="0" end="4">
                        <img src="<c:out value='${image.imageUrl}'/>" alt="<c:out value='${homestay.title}'/>">
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </section>
        <div class="detail-layout">
            <div class="detail-main">
                <section class="content-section host-line">
                    <div>
                        <p class="eyebrow">CHỦ NHÀ</p>
                        <h2><c:out value="${homestay.hostName}"/></h2>
                    </div>
                    <span class="host-avatar">${homestay.hostName.substring(0,1)}</span>
                </section>
                <section class="content-section">
                    <h2>Về nơi này</h2>
                    <p class="description"><c:out value="${homestay.description}"/></p>
                </section>
                <section class="content-section">
                    <h2>Tiện ích</h2>
                    <div class="amenity-list">
                        <c:forEach items="${homestay.amenities}" var="amenity">
                            <span> ✓ <c:out value="${amenity.amenityName}"/></span>
                        </c:forEach>
                        <c:if test="${empty homestay.amenities}">
                            <p>Chưa cập nhật tiện ích.</p>
                        </c:if>
                    </div>
                </section>
                <c:if test="${not empty homestay.cancellationPolicy}">
                    <section class="content-section policy-box">
                        <p class="eyebrow">CHÍNH SÁCH HỦY</p>
                        <h2><c:out value="${homestay.cancellationPolicy.policyName}"/></h2>
                        <p><c:out value="${homestay.cancellationPolicy.description}"/></p>
                        <div class="policy-rules">
                            <span><b>100%</b> trước ${homestay.cancellationPolicy.fullRefundDays} ngày </span>
                            <span>
                                <b><fmt:formatNumber value="${homestay.cancellationPolicy.partialRefundPercent}" maxFractionDigits="0"/> % </b>
                                trước ${homestay.cancellationPolicy.partialRefundDays} ngày
                            </span>
                            <span><b>0%</b> sau thời hạn </span>
                        </div>
                    </section>
                </c:if>
                <section class="content-section">
                    <h2>Đánh giá của khách</h2>
                    <c:choose>
                        <c:when test="${empty homestay.reviews}">
                            <p>Homestay chưa có đánh giá.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="review-list">
                                <c:forEach items="${homestay.reviews}" var="review">
                                    <article class="review-card">
                                        <div class="review-head">
                                            <strong><c:out value="${review.customerName}"/></strong>
                                            <span>${review.ratingStars}/5 ★</span>
                                        </div>
                                        <p><c:out value="${review.comment}"/></p>
                                        <c:if test="${not empty review.imageUrls}">
                                            <div class="review-images">
                                                <c:forEach items="${review.imageUrls}" var="imageUrl">
                                                    <img src="${pageContext.request.contextPath}/<c:out value='${imageUrl}'/>" alt="Ảnh đánh giá">
                                                </c:forEach>
                                            </div>
                                        </c:if>
                                        <c:if test="${not empty review.hostResponse}">
                                            <div class="host-response">
                                                <strong>Phản hồi của chủ nhà:</strong>
                                                <c:out value="${review.hostResponse}"/>
                                            </div>
                                        </c:if>
                                    </article>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </section>
            </div>
            <aside class="booking-card">
                <p class="price"><strong><fmt:formatNumber value="${homestay.pricePerNight}" pattern="#,##0"/> ₫</strong>/ đêm </p>
                <form method="get" action="${pageContext.request.contextPath}/booking/create">
                    <input type="hidden" name="homestayId" value="${homestay.homestayId}">
                    <div class="date-grid">
                        <label>
                            Nhận phòng
                            <input type="date" name="checkIn" required value="<c:out value='${checkIn}'/>">
                        </label>
                        <label>
                            Trả phòng
                            <input type="date" name="checkOut" required value="<c:out value='${checkOut}'/>">
                        </label>
                    </div>
                    <label>
                        Khách
                        <input type="number" name="guests" min="1" max="${homestay.maxGuests}" required value="${empty guests ? 1 : guests}">
                    </label>
                    <c:choose>
                        <c:when test="${not empty sessionScope.currentUser}">
                            <button type="submit">Tiếp tục đặt phòng</button>
                        </c:when>
                        <c:otherwise>
                            <a class="book-login" href="${pageContext.request.contextPath}/login">Đăng nhập để đặt phòng</a>
                        </c:otherwise>
                    </c:choose>
                </form>
                <p class="small-note">Bạn chưa bị tính phí ở bước này.</p>
            </aside>
        </div>
    </main>
</body>
</html>
