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
import service.AdminReviewService;
import ultis.ParseUtil;

/**
 *
 * @author Admin
 */
@WebServlet(name = "AdminReviewReportAction", urlPatterns = {"/admin/review-report-action"})
public class AdminReviewReportAction extends HttpServlet {

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
            out.println("<title>Servlet AdminReviewReportAction</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AdminReviewReportAction at " + request.getContextPath() + "</h1>");
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
        Integer reportId = ParseUtil.toPositiveInteger(
                request.getParameter("reportId")
        );
        Integer reviewId = ParseUtil.toPositiveInteger(
                request.getParameter("reviewId")
        );
        if (reportId == null || reviewId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            boolean success = new AdminReviewService().updateReportStatus(
                    reportId, request.getParameter("status")
            );
            request.getSession().setAttribute(
                    success ? "flashSuccess" : "flashError",
                    success ? "Đã cập nhật báo cáo."
                            : "Không thể cập nhật báo cáo."
            );
        } catch (IllegalArgumentException exception) {
            request.getSession().setAttribute(
                    "flashError", exception.getMessage()
            );
        }
        response.sendRedirect(
                request.getContextPath()
                + "/admin/review-detail?id=" + reviewId
        );
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
