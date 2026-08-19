<%-- 
    Document   : review-create
    Created on : Aug 18, 2026, 5:23:57 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đánh giá homestay</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body>
        <jsp:include page="/views/fragments/customer-header.jsp"/>

        <main class="review-form-wrap">
            <section class="review-intro">
                <p class="eyebrow">CHIA SẺ TRẢI NGHIỆM</p>
                <h1>Đánh giá kỳ nghỉ của bạn</h1>
                <p>Bạn đang đánh giá <strong><c:out value="${booking.homestayTitle}"/></strong>, booking #${booking.bookingId}.</p>
            </section>

            <form method="post"
                  action="${pageContext.request.contextPath}/customer/review/create"
                  enctype="multipart/form-data" class="review-form">
                <input type="hidden" name="bookingId" value="${booking.bookingId}">

                <c:if test="${not empty requestScope.error}">
                    <div class="notice error"><c:out value="${requestScope.error}"/></div>
                </c:if>

                <fieldset class="rating-field">
                    <legend>Bạn hài lòng đến mức nào?</legend>
                    <div class="star-options">
                        <c:forEach begin="1" end="5" var="star">
                            <label>
                                <input type="radio" name="ratingStars" value="${star}"
                                       required ${selectedRating == star ? 'checked' : ''}>
                                <span>${star} ★</span>
                            </label>
                        </c:forEach>
                    </div>
                </fieldset>

                <label class="review-label">Nhận xét
                    <textarea name="comment" rows="7" maxlength="2000"
                              placeholder="Không gian, vị trí, tiện ích và trải nghiệm của bạn..."><c:out value="${comment}"/></textarea>
                </label>

                <label class="review-label upload-box">Ảnh trải nghiệm
                    <input type="file" name="images" accept="image/jpeg,image/png,image/webp" multiple>
                    <span>Tối đa 5 ảnh; mỗi ảnh không quá 5 MB. Hỗ trợ JPG, PNG và WEBP.</span>
                </label>

                <button type="submit" class="submit-review">Gửi đánh giá</button>
            </form>
        </main>
    </body>
</html>

