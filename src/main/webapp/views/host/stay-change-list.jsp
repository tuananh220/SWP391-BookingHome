<%-- 
    Document   : stay-change-list
    Created on : Aug 18, 2026, 7:19:38 PM
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
        <title>Yêu cầu thay đổi lưu trú | Chủ nhà</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body class="host-body">
        <jsp:include page="/views/host/header.jsp"/>
        <main class="host-wrap">
            <div class="host-page-head">
                <div>
                    <p class="eyebrow">YÊU CẦU CỦA KHÁCH</p>
                    <h1>Gia hạn & trả phòng sớm</h1>
                </div>
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
            <nav class="status-tabs">
                <a class="${empty selectedStatus ? 'active' : ''}" href="${pageContext.request.contextPath}/host/stay-change-requests">Tất cả</a>
                <a class="${selectedStatus == 'Pending' ? 'active' : ''}" href="?status=Pending">Chờ xử lý</a>
                <a class="${selectedStatus == 'Accepted' ? 'active' : ''}" href="?status=Accepted">Đã chấp nhận</a>
                <a class="${selectedStatus == 'Rejected' ? 'active' : ''}" href="?status=Rejected">Từ chối</a>
            </nav>
            <c:choose>
                <c:when test="${empty requests}">
                    <section class="empty-state">
                        <h3>Không có yêu cầu phù hợp</h3>
                    </section>
                </c:when>
                <c:otherwise>
                    <section class="host-change-grid">
                        <c:forEach items="${requests}" var="item">
                            <article class="host-change-card">
                                <div class="change-request-head">
                                    <div>
                                        <small>#${item.requestId} · Booking #${item.bookingId}</small>
                                        <h2>
                                            <c:out value="${item.homestayTitle}"/>
                                        </h2>
                                        <p>Khách: <strong>
                                                <c:out value="${item.customerName}"/>
                                            </strong>
                                        </p>
                                    </div>
                                    <span class="booking-status status-${item.status}">
                                        <c:out value="${item.status}"/>
                                    </span>
                                </div>
                                <div class="change-request-data">
                                    <span>
                                        <b>Loại</b>${item.requestType == 'Extension' ? 'Gia hạn' : 'Trả sớm'}</span>
                                    <span>
                                        <b>Ngày cũ</b>${item.originalCheckOutDate}</span>
                                    <span>
                                        <b>Ngày mới</b>${item.requestedCheckOutDate}</span>
                                    <span>
                                        <b>${item.requestType == 'Extension' ? 'Thu thêm' : 'Hoàn dự kiến'}</b>
                                        <fmt:formatNumber value="${item.requestType == 'Extension' ? item.extraAmount : item.refundAmount}" pattern="#,##0"/> ₫</span>
                                </div>
                                <c:if test="${not empty item.customerNote}">
                                    <p>
                                        <strong>Ghi chú:</strong> <c:out value="${item.customerNote}"/>
                                    </p>
                                </c:if>
                                <c:if test="${item.status == 'Pending'}">
                                    <div class="host-change-actions">
                                        <form method="post" action="${pageContext.request.contextPath}/host/stay-change-action">
                                            <input type="hidden" name="requestId" value="${item.requestId}">
                                            <button class="accept-button" name="action" value="accept">Chấp nhận</button>
                                        </form>
                                        <form method="post" action="${pageContext.request.contextPath}/host/stay-change-action">
                                            <input type="hidden" name="requestId" value="${item.requestId}">
                                            <input type="text" name="responseNote" maxlength="255" required placeholder="Lý do từ chối">
                                            <button class="reject-button" name="action" value="reject">Từ chối</button>
                                        </form>
                                    </div>
                                </c:if>
                            </article>
                        </c:forEach>
                    </section>
                </c:otherwise>
            </c:choose>
        </main>
    </body>
</html>

