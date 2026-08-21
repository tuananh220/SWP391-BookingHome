<%-- 
    Document   : voucher-list
    Created on : Aug 18, 2026, 6:36:26 PM
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
        <title>Voucher | Chủ nhà</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body class="host-body">
        <jsp:include page="/views/host/header.jsp"/>
        <main class="host-wrap">
            <div class="host-page-head">
                <div>
                    <p class="eyebrow">ƯU ĐÃI CỦA TÔI</p>
                    <h1>Quản lý voucher</h1>
                </div>
                <a class="host-create-button" href="${pageContext.request.contextPath}/host/voucher-form">+ Tạo voucher</a>
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
                <c:when test="${empty vouchers}">
                    <section class="empty-state">
                        <h3>Chưa có voucher</h3>
                        <p>Tạo mã ưu đãi đầu tiên để thu hút khách hàng.</p>
                    </section>
                </c:when>
                <c:otherwise>
                    <section class="voucher-grid">
                        <c:forEach items="${vouchers}" var="voucher">
                            <article class="voucher-card ${voucher.active ? '' : 'inactive'}">
                                <div class="voucher-cut">
                                    <span>${voucher.active ? 'ACTIVE' : 'INACTIVE'}</span>
                                    <strong>
                                        <fmt:formatNumber value="${voucher.discountRate}" maxFractionDigits="0"/>%</strong>
                                    <small>GIẢM GIÁ</small>
                                </div>
                                <div class="voucher-content">
                                    <div class="voucher-code">
                                        <c:out value="${voucher.voucherCode}"/>
                                    </div>
                                    <p>Áp dụng: <strong>
                                            <c:out value="${empty voucher.homestayTitle ? 'Tất cả homestay của tôi' : voucher.homestayTitle}"/>
                                        </strong>
                                    </p>
                                    <p>Đơn tối thiểu: <fmt:formatNumber value="${voucher.minOrderValue}" pattern="#,##0"/> ₫</p>
                                    <p>Giảm tối đa: <c:choose>
                                            <c:when test="${empty voucher.maxDiscountAmount}">Không giới hạn</c:when>
                                            <c:otherwise>
                                                <fmt:formatNumber value="${voucher.maxDiscountAmount}" pattern="#,##0"/> ₫</c:otherwise>
                                        </c:choose>
                                    </p>
                                    <p>Thời gian: ${voucher.startDate} → ${voucher.endDate}</p>
                                    <div class="voucher-usage">
                                        <span>Đã dùng ${voucher.usedCount}/${voucher.usageLimit}</span>
                                        <progress value="${voucher.usedCount}" max="${voucher.usageLimit}">
                                        </progress>
                                    </div>
                                    <div class="voucher-actions">
                                        <a href="${pageContext.request.contextPath}/host/voucher-form?id=${voucher.voucherId}">Chỉnh sửa</a>
                                        <form method="post" action="${pageContext.request.contextPath}/host/voucher-action">
                                            <input type="hidden" name="voucherId" value="${voucher.voucherId}">
                                            <c:choose>
                                                <c:when test="${voucher.active}">
                                                    <button type="submit" name="action" value="deactivate">Ngừng voucher</button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button type="submit" name="action" value="activate">Kích hoạt</button>
                                                </c:otherwise>
                                            </c:choose>
                                        </form>
                                    </div>
                                </div>
                            </article>
                        </c:forEach>
                    </section>
                </c:otherwise>
            </c:choose>
        </main>
    </body>
</html>


