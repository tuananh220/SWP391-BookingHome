<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${amenity.amenityId > 0 ? 'Sửa' : 'Thêm'} tiện ích</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
</head>
<body class="host-body">
    <jsp:include page="/views/host/header.jsp"/>
    <main class="property-form-wrap">
        <div class="property-form-heading">
            <p class="eyebrow">FACILITIES</p>
            <h1>${amenity.amenityId > 0 ? 'Chỉnh sửa tiện ích' : 'Thêm tiện ích'}</h1>
        </div>
        <c:if test="${not empty error}">
            <div class="notice error">
                <c:out value="${error}"/>
            </div>
        </c:if>
        <form method="post" action="${pageContext.request.contextPath}/host/amenity-form" class="property-form">
            <input type="hidden" name="amenityId" value="${amenity.amenityId}">
            <section class="form-section">
                <label class="full">
                    Tên tiện ích
                    <input name="amenityName" maxlength="100" required value="<c:out value='${amenity.amenityName}'/>">
                </label>
                <label class="full">
                    Icon class (tuỳ chọn)
                    <input name="iconClass" maxlength="100" placeholder="fa-wifi" value="<c:out value='${amenity.iconClass}'/>">
                </label>
            </section>
            <div class="property-form-actions">
                <a href="${pageContext.request.contextPath}/host/amenities">Hủy</a>
                <button type="submit">Lưu</button>
            </div>
        </form>
    </main>
</body>
</html>
