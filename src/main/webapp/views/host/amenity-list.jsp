<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tiện ích | Chủ nhà</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
</head>
<body class="host-body">
    <jsp:include page="/views/host/header.jsp"/>
    <main class="host-wrap">
        <div class="host-page-head">
            <div>
                <p class="eyebrow">FACILITIES</p>
                <h1>Quản lý tiện ích</h1>
            </div>
            <a class="host-create-button" href="${pageContext.request.contextPath}/host/amenity-form"> + Thêm tiện ích </a>
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
            <c:when test="${empty amenities}">
                <section class="empty-state">
                    <h3>Chưa có tiện ích</h3>
                </section>
            </c:when>
            <c:otherwise>
                <div class="host-table-wrap">
                    <table class="host-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên tiện ích</th>
                                <th>Icon</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${amenities}" var="amenity">
                                <tr>
                                    <td>#${amenity.amenityId}</td>
                                    <td><strong><c:out value="${amenity.amenityName}"/></strong></td>
                                    <td><c:out value="${empty amenity.iconClass ? '-' : amenity.iconClass}"/></td>
                                    <td>
                                        <a class="table-action" href="${pageContext.request.contextPath}/host/amenity-form?id=${amenity.amenityId}">Sửa</a>
                                        <form method="post" action="${pageContext.request.contextPath}/host/amenity-action" style="display:inline">
                                            <input type="hidden" name="amenityId" value="${amenity.amenityId}">
                                            <button class="table-action" name="action" value="delete" onclick="return confirm('Xóa tiện ích này?')">Xóa</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </main>
</body>
</html>
