/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import entity.Banner;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.BannerService;
import ultis.ParseUtil;

/**
 *
 * @author Admin
 */
@WebServlet(name = "AdminBannerFormController", urlPatterns = {"/admin/banner-form"})
public class AdminBannerFormController extends HttpServlet {

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
            out.println("<title>Servlet AdminBannerFormController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AdminBannerFormController at " + request.getContextPath() + "</h1>");
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
        Integer bannerId = ParseUtil.toPositiveInteger(request.getParameter("id"));
        Banner banner;
        if (bannerId == null) {
            banner = new Banner();
            banner.setActive(true);
        } else {
            banner = new BannerService().getById(bannerId);
            if (banner == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }
        request.setAttribute("banner", banner);
        request.getRequestDispatcher("/views/admin/banner-form.jsp")
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
        Banner banner = new Banner();
        Integer bannerId = ParseUtil.toPositiveInteger(
                request.getParameter("bannerId")
        );
        banner.setBannerId(bannerId == null ? 0 : bannerId);
        banner.setTitle(request.getParameter("title"));
        banner.setImageUrl(request.getParameter("imageUrl"));
        banner.setTargetUrl(request.getParameter("targetUrl"));
        Integer order = ParseUtil.toNonNegativeInteger(
                request.getParameter("displayOrder")
        );
        banner.setDisplayOrder(order == null ? -1 : order);
        banner.setActive(request.getParameter("active") != null);

        BannerService service = new BannerService();
        try {
            boolean editing = banner.getBannerId() > 0;
            boolean success = editing
                    ? service.update(banner) : service.create(banner) > 0;
            if (success) {
                request.getSession().setAttribute(
                        "flashSuccess",
                        editing ? "Cập nhật banner thành công."
                                : "Tạo banner thành công."
                );
                response.sendRedirect(
                        request.getContextPath() + "/admin/banners"
                );
                return;
            }
            request.setAttribute("error", "Không thể lưu banner.");
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
        }
        request.setAttribute("banner", banner);
        request.getRequestDispatcher("/views/admin/banner-form.jsp")
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
