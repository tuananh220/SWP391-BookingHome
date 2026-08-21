<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<header class="host-header">
  <a
    class="brand light"
    href="${pageContext.request.contextPath}/host/dashboard"
    >HOST CENTER</a
  >
  <nav>
    <a href="${pageContext.request.contextPath}/host/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/host/homestays">Homestay</a>
    <a href="${pageContext.request.contextPath}/host/schedule">Lịch & giá</a>
    <a href="${pageContext.request.contextPath}/host/bookings">Booking</a>
    <a href="${pageContext.request.contextPath}/host/booking-history"
      >Lịch sử</a
    >
    <a href="${pageContext.request.contextPath}/host/stay-change-requests"
      >Gia hạn</a
    >
    <a href="${pageContext.request.contextPath}/host/vouchers">Voucher</a>
    <a href="${pageContext.request.contextPath}/host/amenities">Tiện ích</a>
    <a href="${pageContext.request.contextPath}/host/blogs">Blog</a>
    <a href="${pageContext.request.contextPath}/profile">Hồ sơ</a>
    <form method="post" action="${pageContext.request.contextPath}/logout">
      <button type="submit">Đăng xuất</button>
    </form>
  </nav>
</header>
