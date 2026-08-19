package controller;

import entity.Blog;
import entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CustomerBlogService;
import ultis.ParseUtil;

@WebServlet(name = "HostBlogFormController", urlPatterns = {"/host/blog-form"})
public class HostBlogFormController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("currentUser");
        Integer blogId = ParseUtil.toPositiveInteger(request.getParameter("id"));
        Blog blog;
        if (blogId == null) {
            blog = new Blog();
        } else {
            blog = new CustomerBlogService().getMyBlog(
                    blogId, user.getUserId()
            );
            if (blog == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }
        request.setAttribute("blog", blog);
        request.getRequestDispatcher("/views/host/blog-form.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User user = (User) request.getSession().getAttribute("currentUser");
        Blog blog = new Blog();
        Integer blogId = ParseUtil.toPositiveInteger(
                request.getParameter("blogId")
        );
        blog.setBlogId(blogId == null ? 0 : blogId);
        blog.setAuthorId(user.getUserId());
        blog.setTitle(request.getParameter("title"));
        blog.setSlug(request.getParameter("slug"));
        blog.setThumbnailUrl(request.getParameter("thumbnailUrl"));
        blog.setContent(request.getParameter("content"));
        blog.setPublished(false);

        CustomerBlogService service = new CustomerBlogService();
        try {
            boolean editing = blog.getBlogId() > 0;
            boolean success = editing
                    ? service.updateAndResubmit(blog)
                    : service.create(blog) > 0;
            if (success) {
                request.getSession().setAttribute(
                        "flashSuccess",
                        editing
                                ? "Đã cập nhật và gửi bài chờ Admin duyệt lại."
                                : "Đã gửi bài viết chờ Admin duyệt."
                );
                response.sendRedirect(request.getContextPath() + "/host/blogs");
                return;
            }
            request.setAttribute("error", "Không thể lưu bài viết.");
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
        }
        request.setAttribute("blog", blog);
        request.getRequestDispatcher("/views/host/blog-form.jsp")
                .forward(request, response);
    }
}
