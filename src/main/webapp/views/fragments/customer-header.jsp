<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %> <%@
taglib prefix="c" uri="jakarta.tags.core" %>
<header class="site-header">
  <a class="brand" href="${pageContext.request.contextPath}/homestays"
    >HOMESTAY</a
  >
  <nav>
    <a href="${pageContext.request.contextPath}/homestays">Khám phá homestay</a>
    <c:choose>
      <c:when
        test="${not empty sessionScope.currentUser && sessionScope.currentUser.roleName == 'Customer'}"
      >
        <a href="${pageContext.request.contextPath}/customer/bookings"
          >Booking của tôi</a
        >
        <a
          href="${pageContext.request.contextPath}/customer/stay-change-requests"
          >Yêu cầu lưu trú</a
        >
        <a href="${pageContext.request.contextPath}/customer/favorites"
          >Yêu thích</a
        >
        <a href="${pageContext.request.contextPath}/blogs">Blog</a>
        <a href="${pageContext.request.contextPath}/customer/blogs"
          >Blog của tôi</a
        >
        <a href="${pageContext.request.contextPath}/profile">Hồ sơ</a>
        <jsp:include page="/views/fragments/logout.jsp" />
      </c:when>
      <c:when
        test="${not empty sessionScope.currentUser && sessionScope.currentUser.roleName == 'Home Owner'}"
      >
        <a href="${pageContext.request.contextPath}/host/dashboard"
          >Host Center</a
        >
        <a href="${pageContext.request.contextPath}/profile">Hồ sơ</a>
        <jsp:include page="/views/fragments/logout.jsp" />
      </c:when>
      <c:when
        test="${not empty sessionScope.currentUser && sessionScope.currentUser.roleName == 'Admin'}"
      >
        <a href="${pageContext.request.contextPath}/admin/dashboard">Admin</a>
        <a href="${pageContext.request.contextPath}/profile">Hồ sơ</a>
        <jsp:include page="/views/fragments/logout.jsp" />
      </c:when>
      <c:otherwise>
        <a href="${pageContext.request.contextPath}/blogs">Blog</a>
        <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
      </c:otherwise>
    </c:choose>
  </nav>
</header>
