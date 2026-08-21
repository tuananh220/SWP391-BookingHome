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
        <aside class="admin-sidebar">
            <a class="admin-logo" href="${pageContext.request.contextPath}/admin/dashboard">HOMESTAY<br>
                <span>ADMIN</span>
            </a>
            <nav>
                <a href="${pageContext.request.contextPath}/admin/dashboard">Tổng quan</a>
                <a class="active" href="${pageContext.request.contextPath}/admin/users">Tài khoản</a>
            </nav>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button>Đăng xuất</button>
            </form>
        </aside>
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
                <div class="admin-field-grid">
                    <label>Số điện thoại<input type="text" name="phoneNumber" maxlength="20" value="<c:out value='${user.phoneNumber}'/>">
                    </label>
                    <label>Vai trò<select name="roleId" required>
                            <c:forEach items="${roles}" var="role">
                                <option value="${role.roleId}" ${user.roleId == role.roleId ? 'selected' : ''}>
                                    <c:out value="${role.roleName}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </label>
                </div>
                <label>Địa chỉ<input type="text" name="address" maxlength="255" value="<c:out value='${user.address}'/>">
                </label>
                <label>Trạng thái<select name="status" required>
                        <option value="Active" ${user.status == 'Active' ? 'selected' : ''}>Active</option>
                        <option value="Blocked" ${user.status == 'Blocked' ? 'selected' : ''}>Blocked</option>
                        <option value="Pending" ${user.status == 'Pending' ? 'selected' : ''}>Pending</option>
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

