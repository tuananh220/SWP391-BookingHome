/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

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
@WebServlet(name = "AdminBlogActionController", urlPatterns = {"/admin/blog-action"})
public class AdminBlogActionController extends HttpServlet {

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
            out.println("<title>Servlet AdminBlogActionController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AdminBlogActionController at " + request.getContextPath() + "</h1>");
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
        processRequest(request, response);
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
        Integer blogId = ParseUtil.toPositiveInteger(
                request.getParameter("blogId")
        );
        if (blogId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String action = request.getParameter("action");
        AdminBlogService service = new AdminBlogService();
        boolean success;
        if ("publish".equals(action)) {
            success = service.setPublished(blogId, true);
        } else if ("unpublish".equals(action)) {
            success = service.setPublished(blogId, false);
        } else if ("delete".equals(action)) {
            success = service.delete(blogId);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        request.getSession().setAttribute(
                success ? "flashSuccess" : "flashError",
                success ? "Đã xử lý bài viết." : "Không thể xử lý bài viết."
        );
        response.sendRedirect(request.getContextPath() + "/admin/blogs");
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
