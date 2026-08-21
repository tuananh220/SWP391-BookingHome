<%-- 
    Document   : stay-change-list
    Created on : Aug 18, 2026, 7:22:07 PM
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
        <title>Yêu cầu thay đổi lưu trú</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body>
        <jsp:include page="/views/fragments/customer-header.jsp"/>
        <main class="history-wrap">
            <div class="history-heading">
                <p class="eyebrow">QUẢN LÝ YÊU CẦU</p>
                <h1>Gia hạn & trả phòng sớm</h1>
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
                <c:when test="${empty requests}">
                    <section class="empty-state">
                        <h3>Chưa có yêu cầu nào</h3>
                    </section>
                </c:when>
                <c:otherwise>
                    <section class="change-request-list">
                        <c:forEach items="${requests}" var="item">
                            <article class="change-request-card">
                                <div class="change-request-head">
                                    <div>
                                        <small>Booking #${item.bookingId}</small>
                                        <h2>
                                            <c:out value="${item.homestayTitle}"/>
                                        </h2>
                                    </div>
                                    <span class="booking-status status-${item.status}">
                                        <c:out value="${item.status}"/>
                                    </span>
                                </div>
                                <div class="change-request-data">
                                    <span>
                                        <b>Loại</b>${item.requestType == 'Extension' ? 'Gia hạn' : 'Trả phòng sớm'}</span>
                                    <span>
                                        <b>Ngày cũ</b>${item.originalCheckOutDate}</span>
                                    <span>
                                        <b>Ngày yêu cầu</b>${item.requestedCheckOutDate}</span>
                                    <span>
                                        <b>${item.requestType == 'Extension' ? 'Tiền thêm' : 'Tiền hoàn'}</b>
                                        <fmt:formatNumber value="${item.requestType == 'Extension' ? item.extraAmount : item.refundAmount}" pattern="#,##0"/> ₫</span>
                                </div>
                                <c:if test="${not empty item.responseNote}">
                                    <p class="response-note">
                                        <strong>Phản hồi:</strong> <c:out value="${item.responseNote}"/>
                                    </p>
                                </c:if>
                                <c:if test="${item.status == 'Pending'}">
                                    <div class="change-request-actions">
                                        <a href="${pageContext.request.contextPath}/customer/stay-change-form?requestId=${item.requestId}">Chỉnh sửa</a>
                                        <form method="post" action="${pageContext.request.contextPath}/customer/stay-change-action">
                                            <input type="hidden" name="requestId" value="${item.requestId}">
                                            <button type="submit" name="action" value="cancel">Hủy yêu cầu</button>
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

