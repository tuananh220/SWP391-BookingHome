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
import service.BookingManagementService;
import ultis.ParseUtil;

/**
 *
 * @author Admin
 */
@WebServlet(name = "CustomerCancelBookingController", urlPatterns = {"/customer/cancel-booking"})
public class CustomerCancelBookingController extends HttpServlet {

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
            out.println("<title>Servlet CustomerCancelBookingController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CustomerCancelBookingController at " + request.getContextPath() + "</h1>");
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
        request.setCharacterEncoding("UTF-8");
        Integer bookingId = ParseUtil.toPositiveInteger(
                request.getParameter("bookingId")
        );
        if (bookingId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User currentUser = (User) request.getSession()
                .getAttribute("currentUser");
        BookingManagementService service
                = new BookingManagementService();

        try {
            boolean success = service.cancelBooking(
                    bookingId,
                    currentUser.getUserId(),
                    request.getParameter("reason")
            );
            if (success) {
                request.getSession().setAttribute(
                        "flashSuccess",
                        "Hủy booking thành công."
                );
                response.sendRedirect(
                        request.getContextPath() + "/customer/bookings"
                );
            } else {
                request.getSession().setAttribute(
                        "flashError",
                        "Không thể hủy booking. Vui lòng thử lại."
                );
                response.sendRedirect(
                        request.getContextPath()
                        + "/customer/booking-detail?id=" + bookingId
                );
            }
        } catch (IllegalArgumentException exception) {
            request.getSession().setAttribute(
                    "flashError", exception.getMessage()
            );
            response.sendRedirect(
                    request.getContextPath()
                    + "/customer/booking-detail?id=" + bookingId
            );
        }

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
