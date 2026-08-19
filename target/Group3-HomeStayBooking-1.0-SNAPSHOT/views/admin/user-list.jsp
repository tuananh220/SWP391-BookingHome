<%-- 
    Document   : user-list
    Created on : Aug 18, 2026, 7:54:16 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Quản lý tài khoản</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3"></head><body class="admin-body">
        <aside class="admin-sidebar"><a class="admin-logo" href="${pageContext.request.contextPath}/admin/dashboard">HOMESTAY<br><span>ADMIN</span></a><nav><a href="${pageContext.request.contextPath}/admin/dashboard">Tổng quan</a><a class="active" href="${pageContext.request.contextPath}/admin/users">Tài khoản</a><a href="${pageContext.request.contextPath}/admin/homestays">Homestay</a><a href="${pageContext.request.contextPath}/admin/reviews">Đánh giá</a><a href="${pageContext.request.contextPath}/admin/policies">Chính sách hủy</a></nav><form method="post" action="${pageContext.request.contextPath}/logout"><button>Đăng xuất</button></form></aside>
        <main class="admin-main"><header class="admin-page-head"><div><p class="eyebrow">USER MANAGEMENT</p><h1>Quản lý tài khoản</h1></div><span>${users.size()} kết quả</span></header>
            <c:if test="${not empty sessionScope.flashSuccess}"><div class="notice success"><c:out value="${sessionScope.flashSuccess}"/></div><c:remove var="flashSuccess" scope="session"/></c:if>
            <c:if test="${not empty sessionScope.flashError}"><div class="notice error"><c:out value="${sessionScope.flashError}"/></div><c:remove var="flashError" scope="session"/></c:if>
            <form method="get" action="${pageContext.request.contextPath}/admin/users" class="admin-filter"><input type="search" name="keyword" placeholder="Tên, email hoặc số điện thoại" value="<c:out value='${param.keyword}'/>"><select name="role"><option value="">Tất cả vai trò</option><c:forEach items="${roles}" var="role"><option value="<c:out value='${role.roleName}'/>" ${param.role == role.roleName ? 'selected' : ''}><c:out value="${role.roleName}"/></option></c:forEach></select><select name="status"><option value="">Tất cả trạng thái</option><option value="Active" ${param.status == 'Active' ? 'selected' : ''}>Active</option><option value="Blocked" ${param.status == 'Blocked' ? 'selected' : ''}>Blocked</option><option value="Pending" ${param.status == 'Pending' ? 'selected' : ''}>Pending</option></select><button>Tìm kiếm</button><a href="${pageContext.request.contextPath}/admin/users">Xóa lọc</a></form>
            <div class="admin-table-wrap"><table class="admin-table"><thead><tr><th>ID</th><th>Người dùng</th><th>Liên hệ</th><th>Vai trò</th><th>Trạng thái</th><th>Ngày tạo</th><th></th></tr></thead><tbody><c:forEach items="${users}" var="user"><tr><td>#${user.userId}</td><td><strong><c:out value="${user.fullName}"/></strong><small><c:out value="${user.address}"/></small></td><td><strong><c:out value="${user.email}"/></strong><small><c:out value="${user.phoneNumber}"/></small></td><td><span class="role-chip"><c:out value="${user.roleName}"/></span></td><td><span class="booking-status status-${user.status}"><c:out value="${user.status}"/></span></td><td>${user.createdAt}</td><td><a href="${pageContext.request.contextPath}/admin/user-form?id=${user.userId}">Chỉnh sửa</a>
<form method="post" action="${pageContext.request.contextPath}/admin/user-action" style="display:inline">
<input type="hidden" name="userId" value="${user.userId}">
<button name="action" value="delete" onclick="return confirm('Xóa tài khoản này?')">Xóa</button>
</form>
</td></tr></c:forEach></tbody></table></div></main></body></html>

