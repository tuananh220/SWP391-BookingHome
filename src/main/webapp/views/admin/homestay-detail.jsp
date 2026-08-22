<%-- Document : homestay-detail Created on : Aug 18, 2026, 8:09:15 PM Author : Admin --%>

    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
        <%@ taglib prefix="c" uri="jakarta.tags.core" %>
            <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>
                        <c:out value="${homestay.title}" /> | Admin
                    </title>
                    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=3">
                </head>

                <body class="admin-body">
                    <aside class="admin-sidebar"><a class="admin-logo"
                            href="${pageContext.request.contextPath}/admin/dashboard">HOMESTAY<br><span>ADMIN</span></a>
                        <nav><a href="${pageContext.request.contextPath}/admin/dashboard">Tổng quan</a><a
                                href="${pageContext.request.contextPath}/admin/users">Tài khoản</a><a class="active"
                                href="${pageContext.request.contextPath}/admin/homestays">Homestay</a></nav>
                        <form method="post" action="${pageContext.request.contextPath}/logout"><button>Đăng
                                xuất</button></form>
                    </aside>
                    <main class="admin-main">
                        <header class="admin-page-head">
                            <div>
                                <p class="eyebrow">HOMESTAY #${homestay.homestayId}</p>
                                <h1>
                                    <c:out value="${homestay.title}" />
                                </h1>
                            </div><a href="${pageContext.request.contextPath}/admin/homestays">← Danh sách</a>
                        </header>
                        <c:if test="${not empty sessionScope.flashSuccess}">
                            <div class="notice success">
                                <c:out value="${sessionScope.flashSuccess}" />
                            </div>
                            <c:remove var="flashSuccess" scope="session" />
                        </c:if>
                        <c:if test="${not empty sessionScope.flashError}">
                            <div class="notice error">
                                <c:out value="${sessionScope.flashError}" />
                            </div>
                            <c:remove var="flashError" scope="session" />
                        </c:if>
                        <section class="admin-homestay-overview">
                            <div class="admin-image-strip">
                                <c:choose>
                                    <c:when test="${empty homestay.images}">
                                        <div>Chưa có ảnh</div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach items="${homestay.images}" var="image" begin="0" end="3"><img
                                                src="<c:out value='${image.imageUrl}'/>" alt="Homestay"></c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="admin-home-meta"><span class="booking-status status-${homestay.status}">
                                    <c:out value="${homestay.status}" />
                                </span>
                                <p>Chủ nhà: <strong>
                                        <c:out value="${homestay.hostName}" />
                                    </strong> ·
                                    <c:out value="${homestay.hostEmail}" />
                                </p>
                                <p>Tiện ích: <c:forEach items="${homestay.amenities}" var="amenity" varStatus="loop">
                                        <c:out value="${amenity.amenityName}" />${loop.last ? '' : ', '}
                                    </c:forEach>
                                </p>
                            </div>
                        </section>
                        <div class="admin-detail-grid">
                            <form method="post" action="${pageContext.request.contextPath}/admin/homestay-edit"
                                class="admin-editor admin-home-editor"><input type="hidden" name="homestayId"
                                    value="${homestay.homestayId}"><label>Tên homestay<input name="title"
                                        maxlength="200" required
                                        value="<c:out value='${homestay.title}'/>"></label><label>Mô tả<textarea
                                        name="description"
                                        rows="5"><c:out value="${homestay.description}"/></textarea></label><label>Địa
                                    chỉ<input name="address" maxlength="255" required
                                        value="<c:out value='${homestay.address}'/>"></label>
                                <div class="admin-field-grid"><label>Thành phố<input name="city" required
                                            value="<c:out value='${homestay.city}'/>"></label><label>Quận/Huyện<input
                                            name="district" value="<c:out value='${homestay.district}'/>"></label></div>
                                <div class="admin-field-grid"><label>Vĩ độ<input type="number" step="0.00000001"
                                            name="latitude" value="${homestay.latitude}"></label><label>Kinh độ<input
                                            type="number" step="0.00000001" name="longitude"
                                            value="${homestay.longitude}"></label></div>
                                <div class="admin-field-grid"><label>Giá mỗi đêm<input type="number" min="0" step="1000"
                                            name="pricePerNight" required
                                            value="${homestay.pricePerNight}"></label><label>Số khách<input
                                            type="number" min="1" name="maxGuests" required
                                            value="${homestay.maxGuests}"></label></div><label>Chính sách hủy<select
                                        name="cancellationPolicyId">
                                        <option value="">Chưa chọn</option>
                                        <c:forEach items="${policies}" var="policy">
                                            <option value="${policy.policyId}"
                                                ${homestay.cancellationPolicyId==policy.policyId ? 'selected' : '' }>
                                                <c:out value="${policy.policyName}" />${policy.active ? '' : '
                                                (Inactive)'}
                                            </option>
                                        </c:forEach>
                                    </select></label><button type="submit" class="admin-save">Lưu thông tin</button>
                            </form>
                            <aside class="admin-status-panel">
                                <h2>Cập nhật trạng thái</h2>
                                <form method="post" action="${pageContext.request.contextPath}/admin/homestay-action">
                                    <input type="hidden" name="homestayId" value="${homestay.homestayId}"><button
                                        class="approve" name="status" value="Active">Phê duyệt / Kích
                                        hoạt</button><button name="status" value="Pending">Chuyển về chờ
                                        duyệt</button><button name="status" value="Hidden">Ẩn homestay</button><label>Lý
                                        do từ chối<textarea name="reason" rows="4"
                                            maxlength="255"></textarea></label><button class="reject" name="status"
                                        value="Rejected">Từ chối homestay</button></form>
                            </aside>
                        </div>
                    </main>
                </body>

                </html>