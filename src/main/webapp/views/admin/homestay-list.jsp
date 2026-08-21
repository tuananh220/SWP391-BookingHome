<%-- 
    Document   : homestay-list
    Created on : Aug 18, 2026, 8:09:39 PM
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
        <title>Quản lý homestay</title>
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
                <a class="active" href="${pageContext.request.contextPath}/admin/homestays">Homestay</a>
                <a href="${pageContext.request.contextPath}/admin/reviews">Đánh giá</a>
                <a href="${pageContext.request.contextPath}/admin/policies">Chính sách hủy</a>
            </nav>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button>Đăng xuất</button>
            </form>
        </aside>
        <main class="admin-main">
            <header class="admin-page-head">
                <div>
                    <p class="eyebrow">HOMESTAY DATA</p>
                    <h1>Quản lý homestay</h1>
                </div>
                <span>${homestays.size()} kết quả</span>
            </header>
            <c:if test="${not empty sessionScope.flashSuccess}">
                <div class="notice success">
                    <c:out value="${sessionScope.flashSuccess}"/>
                </div>
                <c:remove var="flashSuccess" scope="session"/>
            </c:if>
            <form method="get" action="${pageContext.request.contextPath}/admin/homestays" class="admin-filter homestay-admin-filter">
                <input type="search" name="keyword" placeholder="Tên, địa chỉ hoặc chủ nhà" value="<c:out value='${param.keyword}'/>">
                <select name="status">
                    <option value="">Tất cả trạng thái</option>
                    <option value="Pending" ${param.status == 'Pending' ? 'selected' : ''}>Pending</option>
                    <option value="Active" ${param.status == 'Active' ? 'selected' : ''}>Active</option>
                    <option value="Rejected" ${param.status == 'Rejected' ? 'selected' : ''}>Rejected</option>
                    <option value="Hidden" ${param.status == 'Hidden' ? 'selected' : ''}>Hidden</option>
                </select>
                <button>Tìm kiếm</button>
                <a href="${pageContext.request.contextPath}/admin/homestays">Xóa lọc</a>
            </form>
            <div class="admin-table-wrap">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Homestay</th>
                            <th>Chủ nhà</th>
                            <th>Giá</th>
                            <th>Sức chứa</th>
                            <th>Đánh giá</th>
                            <th>Trạng thái</th>
                            <th>
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${homestays}" var="item">
                            <tr>
                                <td>#${item.homestayId}</td>
                                <td>
                                    <strong>
                                        <c:out value="${item.title}"/>
                                    </strong>
                                    <small>
                                        <c:out value="${item.district}"/>, <c:out value="${item.city}"/>
                                    </small>
                                </td>
                                <td>
                                    <strong>
                                        <c:out value="${item.hostName}"/>
                                    </strong>
                                    <small>
                                        <c:out value="${item.hostEmail}"/>
                                    </small>
                                </td>
                                <td>
                                    <fmt:formatNumber value="${item.pricePerNight}" pattern="#,##0"/> ₫</td>
                                <td>${item.maxGuests} khách</td>
                                <td>★ <fmt:formatNumber value="${item.averageRating}" maxFractionDigits="1"/> (${item.reviewCount})</td>
                                <td>
                                    <span class="booking-status status-${item.status}">
                                        <c:out value="${item.status}"/>
                                    </span>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/homestay-detail?id=${item.homestayId}">Chi tiết</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </main>
    </body>
</html>

