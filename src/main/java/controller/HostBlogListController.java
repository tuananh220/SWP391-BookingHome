package controller;

import entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CustomerBlogService;

@WebServlet(name = "HostBlogListController", urlPatterns = {"/host/blogs"})
public class HostBlogListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("currentUser");
        request.setAttribute(
                "blogs",
                new CustomerBlogService().getMyBlogs(user.getUserId())
        );
        request.getRequestDispatcher("/views/host/blog-list.jsp")
                .forward(request, response);
    }
}
