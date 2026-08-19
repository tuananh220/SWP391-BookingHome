<%-- 
    Document   : dashboard
    Created on : Aug 18, 2026, 6:46:59 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Dashboard | Chủ nhà</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3"></head>
    <body class="host-body">
        <jsp:include page="/views/host/header.jsp"/>
        <main class="host-wrap dashboard-host-wrap">
            <div class="host-page-head"><div><p class="eyebrow">TỔNG QUAN KINH DOANH</p><h1>Dashboard</h1><p>${dashboard.fromDate} → ${dashboard.toDate}</p></div><nav class="period-switch"><a class="${dashboard.period == '7days' ? 'active' : ''}" href="${pageContext.request.contextPath}/host/dashboard?period=7days">7 ngày</a><a class="${dashboard.period == '30days' ? 'active' : ''}" href="${pageContext.request.contextPath}/host/dashboard?period=30days">30 ngày</a><a class="${dashboard.period == '1year' ? 'active' : ''}" href="${pageContext.request.contextPath}/host/dashboard?period=1year">1 năm</a></nav></div>

            <section class="metric-grid">
                <article class="metric-card primary"><span>Doanh thu</span><strong><fmt:formatNumber value="${dashboard.summary.totalRevenue}" pattern="#,##0"/> ₫</strong><small>Payment đã hoàn tất</small></article>
                <article class="metric-card"><span>Tổng booking</span><strong>${dashboard.summary.totalBookings}</strong><small>Trong khoảng thời gian</small></article>
                <article class="metric-card"><span>Chờ xử lý</span><strong>${dashboard.summary.pendingBookings}</strong><small>Cần phản hồi</small></article>
                <article class="metric-card"><span>Đã hủy</span><strong>${dashboard.summary.cancelledBookings}</strong><small>Booking bị hủy</small></article>
            </section>

            <div class="analytics-grid">
                <section class="analytics-panel revenue-panel">
                    <div class="panel-head"><div><p class="eyebrow">BIỂU ĐỒ</p><h2>Doanh thu theo ${dashboard.period == '1year' ? 'tháng' : 'ngày'}</h2></div></div>
                    <div class="bar-chart ${dashboard.period == '30days' ? 'dense' : ''}">
                        <c:forEach items="${dashboard.revenuePoints}" var="point">
                            <div class="bar-column" title="${point.label}: ${point.amount} VNĐ">
                                <div class="bar-value"><c:if test="${point.amount > 0}"><fmt:formatNumber value="${point.amount}" pattern="#,##0"/></c:if></div>
                                <div class="bar-track"><div class="bar-fill" style="height:${point.percentage}%"></div></div>
                                <small>${point.label}</small>
                            </div>
                        </c:forEach>
                    </div>
                </section>

                <section class="analytics-panel status-panel">
                    <div class="panel-head"><div><p class="eyebrow">THỐNG KÊ</p><h2>Trạng thái booking</h2></div></div>
                    <c:choose><c:when test="${empty dashboard.statusStats}"><p>Chưa có dữ liệu booking.</p></c:when><c:otherwise><div class="status-chart"><c:forEach items="${dashboard.statusStats}" var="stat"><div class="status-chart-row"><div><strong><c:out value="${stat.status}"/></strong><span>${stat.total} booking · ${stat.percentage}%</span></div><div class="status-track"><span class="status-${stat.status}" style="width:${stat.percentage}%"></span></div></div></c:forEach></div></c:otherwise></c:choose>
                    <div class="quick-counts"><span>Đã xác nhận <b>${dashboard.summary.confirmedBookings}</b></span><span>Hoàn thành <b>${dashboard.summary.completedBookings}</b></span></div>
                </section>
            </div>
        </main>
    </body>
</html>

