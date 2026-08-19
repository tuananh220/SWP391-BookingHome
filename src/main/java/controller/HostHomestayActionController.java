/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.HostHomestayService;
import ultis.ParseUtil;

/**
 *
 * @author Admin
 */
@WebServlet(name = "HostHomestayActionController", urlPatterns = {"/host/homestay-action"})
public class HostHomestayActionController extends HttpServlet {

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
            out.println("<title>Servlet HostHomestayActionController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet HostHomestayActionController at " + request.getContextPath() + "</h1>");
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
        Integer homestayId = ParseUtil.toPositiveInteger(
                request.getParameter("homestayId")
        );
        String action = request.getParameter("action");
        if (homestayId == null
                || (!"hide".equals(action) && !"show".equals(action))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User currentUser = (User) request.getSession()
                .getAttribute("currentUser");
        HostHomestayService service = new HostHomestayService();
        boolean success;
        String successMessage;
        if ("show".equals(action)) {
            success = service.submitForApproval(
                    homestayId, currentUser.getUserId()
            );
            successMessage = "Đã gửi homestay chờ Admin duyệt lại.";
        } else {
            success = service.hide(homestayId, currentUser.getUserId());
            successMessage = "Đã ẩn homestay.";
        }
        request.getSession().setAttribute(
                success ? "flashSuccess" : "flashError",
                success ? successMessage : "Không thể cập nhật trạng thái."
        );
        response.sendRedirect(
                request.getContextPath() + "/host/homestays"
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
