<%-- 
    Document   : blog-list
    Created on : Aug 18, 2026, 8:51:24 PM
    Author     : Admin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Blog</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/catalog.css?v=5">
    <style>
        .admin-blog-grid {
            display:grid;
            grid-template-columns:repeat(3,minmax(0,1fr));
            gap:18px;
        }
        .admin-blog-card {
            background:#fff;
            display:flex;
            flex-direction:column;
            min-width:0;
            height:100%;
            overflow:hidden;
            border:1px solid #e6e2da;
        }
        .admin-blog-thumb {
            position:relative;
            width:100%;
            height:180px;
            flex:0 0 180px;
            overflow:hidden;
            background:#dfe6e1;
            color:#657a72;
            letter-spacing:2px;
        }
        .admin-blog-thumb img {
            position:absolute;
            top:0;
            left:0;
            width:100%;
            height:100%;
            max-width:100%;
            max-height:100%;
            object-fit:cover;
            object-position:center;
            display:block;
        }
        .admin-blog-placeholder {
            position:absolute;
            top:0;
            right:0;
            bottom:0;
            left:0;
            display:flex;
            align-items:center;
            justify-content:center;
        }
        .admin-blog-thumb .booking-status {
            position:absolute;
            top:12px;
            right:12px;
            z-index:1;
        }
        .admin-blog-content {
            padding:18px 20px 16px;
            flex:1;
            display:flex;
            flex-direction:column;
            min-width:0;
        }
        .admin-blog-content>small {
            color:#84918b;
            font-size:12px;
        }
        .admin-blog-content h2 {
            font:22px/1.3 Tinos,"Times New Roman",Times,serif;
            margin:8px 0;
            color:#18352e;
            display:-webkit-box;
            -webkit-line-clamp:2;
            -webkit-box-orient:vertical;
            overflow:hidden;
            min-height:2.6em;
        }
        .admin-blog-content code {
            display:block;
            font-size:11px;
            color:#8d652c;
            overflow:hidden;
            text-overflow:ellipsis;
            white-space:nowrap;
        }
        .admin-blog-actions {
            display:flex;
            flex-wrap:wrap;
            align-items:center;
            justify-content:space-between;
            gap:8px 12px;
            border-top:1px solid #e8e4dc;
            margin-top:auto;
            padding-top:12px;
        }
        .admin-blog-actions>a,.admin-blog-actions button {
            border:0;
            background:none;
            color:#8d6226;
            text-decoration:none;
            font:inherit;
            font-size:11px;
            font-weight:800;
            cursor:pointer;
            white-space:nowrap;
            padding:0;
        }
        .admin-blog-actions form {
            margin:0;
            display:flex;
            gap:10px;
        }
        .admin-blog-actions .danger {
            color:#a84d3f;
        }
        @media(max-width:950px) {
            .admin-blog-grid {
                grid-template-columns:repeat(2,minmax(0,1fr));
            }
        }
        @media(max-width:620px) {
            .admin-blog-grid {
                grid-template-columns:minmax(0,1fr);
            }
        }
    </style>
</head>
<body class="admin-body">
    <aside class="admin-sidebar">
        <a class="admin-logo" href="${pageContext.request.contextPath}/admin/dashboard">HOMESTAY<br><span>ADMIN</span></a>
        <nav>
            <a href="${pageContext.request.contextPath}/admin/dashboard">Tổng quan</a>
            <a href="${pageContext.request.contextPath}/admin/users">Tài khoản</a>
            <a href="${pageContext.request.contextPath}/admin/homestays">Homestay</a>
            <a href="${pageContext.request.contextPath}/admin/reviews">Đánh giá</a>
            <a href="${pageContext.request.contextPath}/admin/policies">Chính sách hủy</a>
            <a class="active" href="${pageContext.request.contextPath}/admin/blogs">Blog</a>
            <a href="${pageContext.request.contextPath}/admin/banners">Banner</a>
        </nav>
        <form method="post" action="${pageContext.request.contextPath}/logout">
            <button>Đăng xuất</button>
        </form>
    </aside>
    <main class="admin-main">
        <header class="admin-page-head">
            <div>
                <p class="eyebrow">CONTENT MANAGEMENT</p>
                <h1>Quản lý Blog</h1>
            </div>
            <a class="admin-create" href="${pageContext.request.contextPath}/admin/blog-form"> + Viết bài mới </a>
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
        <form class="admin-filter homestay-admin-filter" method="get" action="${pageContext.request.contextPath}/admin/blogs">
            <input type="search" name="keyword" placeholder="Tiêu đề, slug hoặc tác giả" value="<c:out value='${param.keyword}'/>">
            <select name="status">
                <option value="">Tất cả</option>
                <option value="published" ${param.status == 'published' ? 'selected' : ''}>Đã xuất bản</option>
                <option value="draft" ${param.status == 'draft' ? 'selected' : ''}>Bản nháp</option>
            </select>
            <button>Tìm kiếm</button>
            <a href="${pageContext.request.contextPath}/admin/blogs">Xóa lọc</a>
        </form>
        <c:choose>
            <c:when test="${empty blogs}">
                <section class="empty-state">
                    <h3>Chưa có bài viết</h3>
                </section>
            </c:when>
            <c:otherwise>
                <section class="admin-blog-grid">
                    <c:forEach items="${blogs}" var="blog">
                        <article class="admin-blog-card">
                            <div class="admin-blog-thumb" style="height:180px;overflow:hidden;position:relative;flex:0 0 180px;">
                                <c:choose>
                                    <c:when test="${not empty blog.thumbnailUrl}">
                                        <img src="<c:out value='${blog.thumbnailUrl}'/>" alt="<c:out value='${blog.title}'/>" width="640" height="180" style="position:absolute;top:0;left:0;width:100%;height:100%;object-fit:cover;">
                                    </c:when>
                                    <c:otherwise>
                                        <span class="admin-blog-placeholder">BLOG</span>
                                    </c:otherwise>
                                </c:choose>
                                <span class="booking-status ${blog.published ? 'status-Active' : 'status-Pending'}"> ${blog.published ? 'Đã xuất bản' : 'Bản nháp'} </span>
                            </div>
                            <div class="admin-blog-content">
                                <small><c:out value="${blog.authorName}"/> · <c:out value="${blog.createdAtText}"/></small>
                                <h2><c:out value="${blog.title}"/></h2>
                                <code>/<c:out value="${blog.slug}"/></code>
                                <div class="admin-blog-actions">
                                    <a href="${pageContext.request.contextPath}/admin/blog-form?id=${blog.blogId}">Chỉnh sửa</a>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/blog-action">
                                        <input type="hidden" name="blogId" value="${blog.blogId}">
                                        <button name="action" value="${blog.published ? 'unpublish' : 'publish'}"> ${blog.published ? 'Ẩn bài' : 'Xuất bản'} </button>
                                        <button class="danger" name="action" value="delete">Xóa</button>
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
