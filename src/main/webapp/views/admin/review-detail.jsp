<%-- 
    Document   : review-detail
    Created on : Aug 18, 2026, 8:24:34 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Review #${review.reviewId}</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body class="admin-body">
        <aside class="admin-sidebar">
            <a class="admin-logo" href="${pageContext.request.contextPath}/admin/dashboard">HOMESTAY<br>
                <span>ADMIN</span>
            </a>
            <nav>
                <a href="${pageContext.request.contextPath}/admin/dashboard">Tổng quan</a>
                <a href="${pageContext.request.contextPath}/admin/reviews" class="active">Đánh giá</a>
            </nav>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button>Đăng xuất</button>
            </form>
        </aside>
        <main class="admin-main">
            <header class="admin-page-head">
                <div>
                    <p class="eyebrow">REVIEW #${review.reviewId}</p>
                    <h1>
                        <c:out value="${review.homestayTitle}"/>
                    </h1>
                    <p>Khách hàng: <strong>
                            <c:out value="${review.customerName}"/>
                        </strong> · Booking #${review.bookingId}</p>
                </div>
                <a href="${pageContext.request.contextPath}/admin/reviews">← Danh sách</a>
            </header>
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
            <div class="admin-review-grid">
                <div>
                    <form method="post" action="${pageContext.request.contextPath}/admin/review-edit" class="admin-editor admin-review-editor">
                        <input type="hidden" name="reviewId" value="${review.reviewId}">
                        <label>Số sao<input type="number" name="ratingStars" min="1" max="5" required value="${review.ratingStars}">
                        </label>
                        <label>Bình luận<textarea name="comment" rows="7">
                                <c:out value="${review.comment}"/>
                            </textarea>
                        </label>
                        <label>Phản hồi của chủ nhà<textarea name="hostResponse" rows="5">
                                <c:out value="${review.hostResponse}"/>
                            </textarea>
                        </label>
                        <label class="visible-check">
                            <input type="checkbox" name="visible" ${review.visible ? 'checked' : ''}> Cho phép hiển thị công khai</label>
                        <button class="admin-save">Lưu review</button>
                    </form>
                    <c:if test="${not empty review.imageUrls}">
                        <section class="admin-review-images">
                            <h2>Ảnh trải nghiệm</h2>
                            <div>
                                <c:forEach items="${review.imageUrls}" var="image">
                                    <img src="${pageContext.request.contextPath}/<c:out value='${image}'/>" alt="Ảnh review">
                                </c:forEach>
                            </div>
                        </section>
                    </c:if>
                </div>
                <aside>
                    <section class="admin-status-panel">
                        <h2>Thao tác</h2>
                        <form method="post" action="${pageContext.request.contextPath}/admin/review-action">
                            <input type="hidden" name="reviewId" value="${review.reviewId}">
                            <button name="action" value="${review.visible ? 'hide' : 'show'}">${review.visible ? 'Ẩn review' : 'Hiện review'}</button>
                            <button class="reject" name="action" value="delete">Xóa review</button>
                        </form>
                    </section>
                    <section class="report-panel">
                        <h2>Báo cáo (${review.reports.size()})</h2>
                        <c:choose>
                            <c:when test="${empty review.reports}">
                                <p>Review chưa bị báo cáo.</p>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${review.reports}" var="report">
                                    <article>
                                        <div>
                                            <strong>
                                                <c:out value="${report.reporterName}"/>
                                            </strong>
                                            <span class="booking-status status-${report.status}">
                                                <c:out value="${report.status}"/>
                                            </span>
                                        </div>
                                        <p>
                                            <c:out value="${report.reason}"/>
                                        </p>
                                        <small>${report.createdAt}</small>
                                        <c:if test="${report.status == 'Pending'}">
                                            <form method="post" action="${pageContext.request.contextPath}/admin/review-report-action">
                                                <input type="hidden" name="reportId" value="${report.reportId}">
                                                <input type="hidden" name="reviewId" value="${review.reviewId}">
                                                <button name="status" value="Reviewed">Đã xử lý</button>
                                                <button name="status" value="Dismissed">Bỏ qua</button>
                                            </form>
                                        </c:if>
                                    </article>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </section>
                </aside>
            </div>
        </main>
    </body>
</html>

