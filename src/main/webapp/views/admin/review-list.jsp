<%-- 
    Document   : review-list
    Created on : Aug 18, 2026, 8:25:00 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý đánh giá</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body class="admin-body">
        <aside class="admin-sidebar">
            <a class="admin-logo" href="${pageContext.request.contextPath}/admin/dashboard">HOMESTAY<br>
                <span>ADMIN</span>
            </a>
            <nav>
                <a href="${pageContext.request.contextPath}/admin/dashboard">Tổng quan</a>
                <a href="${pageContext.request.contextPath}/admin/users">Tài khoản</a>
                <a href="${pageContext.request.contextPath}/admin/homestays">Homestay</a>
                <a class="active" href="${pageContext.request.contextPath}/admin/reviews">Đánh giá</a>
                <a href="${pageContext.request.contextPath}/admin/policies">Chính sách hủy</a>
            </nav>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button>Đăng xuất</button>
            </form>
        </aside>
        <main class="admin-main">
            <header class="admin-page-head">
                <div>
                    <p class="eyebrow">REVIEW MANAGEMENT</p>
                    <h1>Quản lý đánh giá</h1>
                </div>
                <span>${reviews.size()} kết quả</span>
            </header>
            <c:if test="${not empty sessionScope.flashSuccess}">
                <div class="notice success">
                    <c:out value="${sessionScope.flashSuccess}"/>
                </div>
                <c:remove var="flashSuccess" scope="session"/>
            </c:if>
            <form class="admin-filter" method="get" action="${pageContext.request.contextPath}/admin/reviews">
                <input type="search" name="keyword" placeholder="Khách hàng, homestay hoặc nội dung" value="<c:out value='${param.keyword}'/>">
                <select name="visibility">
                    <option value="">Tất cả</option>
                    <option value="visible" ${param.visibility == 'visible' ? 'selected' : ''}>Đang hiển thị</option>
                    <option value="hidden" ${param.visibility == 'hidden' ? 'selected' : ''}>Đang ẩn</option>
                </select>
                <label class="reported-check">
                    <input type="checkbox" name="reported" value="true" ${param.reported == 'true' ? 'checked' : ''}> Có báo cáo chờ xử lý</label>
                <button>Tìm kiếm</button>
                <a href="${pageContext.request.contextPath}/admin/reviews">Xóa lọc</a>
            </form>
            <div class="admin-table-wrap">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Khách hàng</th>
                            <th>Homestay</th>
                            <th>Đánh giá</th>
                            <th>Nội dung</th>
                            <th>Hiển thị</th>
                            <th>Báo cáo</th>
                            <th>
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${reviews}" var="review">
                            <tr>
                                <td>#${review.reviewId}</td>
                                <td>
                                    <strong>
                                        <c:out value="${review.customerName}"/>
                                    </strong>
                                    <small>Booking #${review.bookingId}</small>
                                </td>
                                <td>
                                    <c:out value="${review.homestayTitle}"/>
                                </td>
                                <td>
                                    <strong class="admin-stars">${review.ratingStars}/5 ★</strong>
                                </td>
                                <td class="review-excerpt">
                                    <c:out value="${review.comment}"/>
                                </td>
                                <td>
                                    <span class="booking-status ${review.visible ? 'status-Active' : 'status-Blocked'}">${review.visible ? 'Visible' : 'Hidden'}</span>
                                </td>
                                <td>
                                    <c:if test="${review.reportCount > 0}">
                                        <span class="report-badge">${review.reportCount} pending</span>
                                    </c:if>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/review-detail?id=${review.reviewId}">Chi tiết</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </main>
    </body>
</html>

