<%-- 
    Document   : voucher-form
    Created on : Aug 18, 2026, 6:33:05 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${voucher.voucherId > 0 ? 'Chỉnh sửa' : 'Tạo'} voucher</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
    </head>
    <body class="host-body">
        <jsp:include page="/views/host/header.jsp"/>
        <main class="voucher-form-wrap">
            <div class="property-form-heading">
                <p class="eyebrow">CHƯƠNG TRÌNH ƯU ĐÃI</p>
                <h1>${voucher.voucherId > 0 ? 'Chỉnh sửa voucher' : 'Tạo voucher mới'}</h1>
            </div>
            <c:if test="${not empty requestScope.error}">
                <div class="notice error">
                    <c:out value="${requestScope.error}"/>
                </div>
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/host/voucher-form" class="voucher-editor">
                <input type="hidden" name="voucherId" value="${voucher.voucherId}">
                <label>Mã voucher<input class="code-input" type="text" name="voucherCode" maxlength="50" required placeholder="SUMMER2026" value="<c:out value='${voucher.voucherCode}'/>">
                </label>
                <label>Phạm vi áp dụng<select name="homestayId">
                        <option value="">Tất cả homestay của tôi</option>
                        <c:forEach items="${homestays}" var="home">
                            <option value="${home.homestayId}" ${voucher.homestayId == home.homestayId ? 'selected' : ''}>
                                <c:out value="${home.title}"/> (${home.status})</option>
                            </c:forEach>
                    </select>
                </label>
                <div class="voucher-field-grid">
                    <label>Phần trăm giảm<input type="number" name="discountRate" min="0.01" max="100" step="0.01" required value="${voucher.discountRate}">
                    </label>
                    <label>Giảm tối đa<input type="number" name="maxDiscountAmount" min="0" step="1000" placeholder="Để trống nếu không giới hạn" value="${voucher.maxDiscountAmount}">
                    </label>
                </div>
                <div class="voucher-field-grid">
                    <label>Đơn hàng tối thiểu<input type="number" name="minOrderValue" min="0" step="1000" required value="${voucher.minOrderValue}">
                    </label>
                    <label>Giới hạn lượt dùng<input type="number" name="usageLimit" min="1" required value="${voucher.usageLimit}">
                    </label>
                </div>
                <div class="voucher-field-grid">
                    <label>Bắt đầu<input type="datetime-local" name="startDate" required value="${voucher.startDateInput}">
                    </label>
                    <label>Kết thúc<input type="datetime-local" name="endDate" required value="${voucher.endDateInput}">
                    </label>
                </div>
                <div class="property-form-actions">
                    <a href="${pageContext.request.contextPath}/host/vouchers">Hủy</a>
                    <button type="submit">Lưu voucher</button>
                </div>
            </form>
        </main>
    </body>
</html>

