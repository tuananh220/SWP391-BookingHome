<%-- 
    Document   : policy-list
    Created on : Aug 18, 2026, 8:38:25 PM
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
        <title>Chính sách hủy</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body class="admin-body">
        <jsp:include page="/views/fragments/admin-sidebar.jsp">
            <jsp:param name="active" value="policies"/>
        </jsp:include>
        <main class="admin-main">
            <header class="admin-page-head">
                <div>
                    <p class="eyebrow">CANCELLATION POLICIES</p>
                    <h1>Chính sách hủy</h1>
                </div>
                <a class="admin-create" href="${pageContext.request.contextPath}/admin/policy-form">+ Thêm chính sách</a>
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
            <section class="policy-admin-grid">
                <c:forEach items="${policies}" var="policy">
                    <article class="policy-admin-card ${policy.active ? '' : 'inactive'}">
                        <div class="policy-card-head">
                            <div>
                                <small>POLICY #${policy.policyId}</small>
                                <h2>
                                    <c:out value="${policy.policyName}"/>
                                </h2>
                            </div>
                            <span class="booking-status ${policy.active ? 'status-Active' : 'status-Blocked'}">${policy.active ? 'Active' : 'Inactive'}</span>
                        </div>
                        <p>
                            <c:out value="${policy.description}"/>
                        </p>
                        <div class="policy-rules">
                            <span>
                                <b>100%</b> trước ${policy.fullRefundDays} ngày</span>
                            <span>
                                <b>
                                    <fmt:formatNumber value="${policy.partialRefundPercent}" maxFractionDigits="0"/>%</b> trước ${policy.partialRefundDays} ngày</span>
                            <span>
                                <b>0%</b> sau thời hạn</span>
                        </div>
                        <div class="policy-actions">
                            <a href="${pageContext.request.contextPath}/admin/policy-form?id=${policy.policyId}">Chỉnh sửa</a>
                            <form method="post" action="${pageContext.request.contextPath}/admin/policy-action">
                                <input type="hidden" name="policyId" value="${policy.policyId}">
                                <button name="action" value="${policy.active ? 'deactivate' : 'activate'}">${policy.active ? 'Ngừng' : 'Kích hoạt'}</button>
                                <button class="delete-policy" name="action" value="delete">Xóa</button>
                            </form>
                        </div>
                    </article>
                </c:forEach>
            </section>
        </main>
    </body>
</html>

