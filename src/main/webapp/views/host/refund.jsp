<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Hoàn tiền booking | Chủ nhà</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body class="host-body">
        <jsp:include page="/views/host/header.jsp"/>
        <main class="host-wrap">
            <div class="host-page-head">
                <div>
                    <p class="eyebrow">REFUND PAYMENT</p>
                    <h1>Hoàn tiền checkout sớm</h1>
                    <p>Kiểm tra thông tin trước khi xác nhận giao dịch.</p>
                </div>
            </div>
            <section class="host-panel">
                <h2>Thông tin yêu cầu</h2>
                <div class="host-info-grid">
                    <div><span>Request</span><strong>#${changeRequest.requestId}</strong></div>
                    <div><span>Booking</span><strong>#${changeRequest.bookingId}</strong></div>
                    <div><span>Homestay</span><strong><c:out value="${changeRequest.homestayTitle}"/></strong></div>
                    <div><span>Số tiền hoàn</span><strong><fmt:formatNumber value="${changeRequest.refundAmount}" pattern="#,##0"/> ₫</strong></div>
                </div>
            </section>
            <section class="host-panel">
                <h2>Tài khoản nhận tiền</h2>
                <div class="host-info-grid">
                    <div><span>Chủ tài khoản</span><strong><c:out value="${changeRequest.refundAccountName}"/></strong></div>
                    <div><span>Ngân hàng</span><strong><c:out value="${changeRequest.refundBankName}"/></strong></div>
                    <div><span>Số tài khoản</span><strong><c:out value="${changeRequest.refundAccountNumber}"/></strong></div>
                    <div><span>Trạng thái</span><strong><c:out value="${changeRequest.refundStatus}"/></strong></div>
                </div>
            </section>
            <section class="host-panel action-panel">
                <form method="post" action="${pageContext.request.contextPath}/host/refund">
                    <input type="hidden" name="requestId" value="${changeRequest.requestId}">
                    <button class="accept-button" type="submit">Giả lập hoàn tiền thành công</button>
                    <a class="table-action" href="${pageContext.request.contextPath}/host/stay-change-requests">Quay lại</a>
                </form>
            </section>
        </main>
    </body>
</html>