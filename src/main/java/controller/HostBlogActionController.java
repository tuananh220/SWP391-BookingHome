package controller;

import entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CustomerBlogService;
import ultis.ParseUtil;

@WebServlet(name = "HostBlogActionController", urlPatterns = {"/host/blog-action"})
public class HostBlogActionController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer blogId = ParseUtil.toPositiveInteger(
                request.getParameter("blogId")
        );
        User user = (User) request.getSession().getAttribute("currentUser");
        if (blogId == null || !"delete".equals(request.getParameter("action"))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        boolean success = new CustomerBlogService().delete(
                blogId, user.getUserId()
        );
        request.getSession().setAttribute(
                success ? "flashSuccess" : "flashError",
                success ? "Đã xóa bài viết." : "Không thể xóa bài viết."
        );
        response.sendRedirect(request.getContextPath() + "/host/blogs");
    }
}
