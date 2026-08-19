/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import entity.Blog;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CustomerBlogService;
import ultis.ParseUtil;

/**
 *
 * @author Admin
 */
@WebServlet(name = "CustomerBlogFormController", urlPatterns = {"/customer/blog-form"})
public class CustomerBlogFormController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet CustomerBlogFormController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CustomerBlogFormController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getCustomer(request, response);
        if (user == null) {
            return;
        }
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
        request.getRequestDispatcher("/views/customer/blog-form.jsp")
                .forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User user = getCustomer(request, response);
        if (user == null) {
            return;
        }

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
                response.sendRedirect(
                        request.getContextPath() + "/customer/blogs"
                );
                return;
            }
            request.setAttribute("error", "Không thể lưu bài viết.");
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
        }
        request.setAttribute("blog", blog);
        request.getRequestDispatcher("/views/customer/blog-form.jsp")
                .forward(request, response);
    }

    private User getCustomer(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("currentUser");
        if (!"Customer".equals(user.getRoleName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        return user;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
