<%-- 
    Document   : user-form
    Created on : Aug 18, 2026, 7:53:54 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chỉnh sửa tài khoản</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body class="admin-body">
        <jsp:include page="/views/fragments/admin-sidebar.jsp">
            <jsp:param name="active" value="users"/>
        </jsp:include>
        <main class="admin-main">
            <header class="admin-page-head">
                <div>
                    <p class="eyebrow">USER #${user.userId}</p>
                    <h1>Chỉnh sửa tài khoản</h1>
                </div>
                <a href="${pageContext.request.contextPath}/admin/users">← Danh sách tài khoản</a>
            </header>
            <c:if test="${not empty error}">
                <div class="notice error">
                    <c:out value="${error}"/>
                </div>
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/admin/user-form" class="admin-editor">
                <input type="hidden" name="userId" value="${user.userId}">
                <label>Họ và tên<input type="text" name="fullName" maxlength="100" required value="<c:out value='${user.fullName}'/>">
                </label>
                <label>Email<input type="email" name="email" maxlength="100" required value="<c:out value='${user.email}'/>">
                </label>
                <label>Số điện thoại<input type="text" name="phoneNumber" maxlength="20" value="<c:out value='${user.phoneNumber}'/>">
                </label>
                <label>Địa chỉ<input type="text" name="address" maxlength="255" value="<c:out value='${user.address}'/>">
                </label>
                <label>Trạng thái<select name="status" required>
                        <option value="Active" ${user.status == 'Active' ? 'selected' : ''}>Active</option>
                   
                        <option value="Deactive" ${user.status == 'Deactive' ? 'selected' : ''}>Deactive</option>
                    </select>
                </label>
                <div class="property-form-actions">
                    <a href="${pageContext.request.contextPath}/admin/users">Hủy</a>
                    <button type="submit">Lưu thay đổi</button>
                </div>
            </form>
        </main>
    </body>
</html>

