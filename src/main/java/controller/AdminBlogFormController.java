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
import service.AdminBlogService;
import ultis.ParseUtil;

/**
 *
 * @author Admin
 */
@WebServlet(name = "AdminBlogFormController", urlPatterns = {"/admin/blog-form"})
public class AdminBlogFormController extends HttpServlet {

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
            out.println("<title>Servlet AdminBlogFormController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AdminBlogFormController at " + request.getContextPath() + "</h1>");
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
        Integer blogId = ParseUtil.toPositiveInteger(request.getParameter("id"));
        Blog blog;
        if (blogId == null) {
            blog = new Blog();
            blog.setPublished(true);
        } else {
            blog = new AdminBlogService().getBlog(blogId);
            if (blog == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }
        request.setAttribute("blog", blog);
        request.getRequestDispatcher("/views/admin/blog-form.jsp")
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
        Blog blog = new Blog();
        Integer blogId = ParseUtil.toPositiveInteger(
                request.getParameter("blogId")
        );
        blog.setBlogId(blogId == null ? 0 : blogId);
        User admin = (User) request.getSession().getAttribute("currentUser");
        blog.setAuthorId(admin.getUserId());
        blog.setTitle(request.getParameter("title"));
        blog.setSlug(request.getParameter("slug"));
        blog.setThumbnailUrl(request.getParameter("thumbnailUrl"));
        blog.setContent(request.getParameter("content"));
        blog.setPublished(request.getParameter("published") != null);

        AdminBlogService service = new AdminBlogService();
        try {
            boolean editing = blog.getBlogId() > 0;
            boolean success = editing
                    ? service.update(blog) : service.create(blog) > 0;
            if (success) {
                request.getSession().setAttribute(
                        "flashSuccess",
                        editing ? "Cập nhật blog thành công."
                                : "Tạo blog thành công."
                );
                response.sendRedirect(
                        request.getContextPath() + "/admin/blogs"
                );
                return;
            }
            request.setAttribute("error", "Không thể lưu blog.");
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
        }
        request.setAttribute("blog", blog);
        request.getRequestDispatcher("/views/admin/blog-form.jsp")
                .forward(request, response);
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
