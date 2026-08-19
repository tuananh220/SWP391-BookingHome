/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import entity.Booking;
import entity.Payment;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.BookingManagementService;
import service.PaymentService;
import ultis.ParseUtil;

/**
 *
 * @author Admin
 */
@WebServlet(name = "CustomerPaymentController", urlPatterns = {"/customer/payment"})
public class CustomerPaymentController extends HttpServlet {

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
            out.println("<title>Servlet CustomerPaymentController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CustomerPaymentController at " + request.getContextPath() + "</h1>");
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
        Integer bookingId = ParseUtil.toPositiveInteger(
                request.getParameter("bookingId")
        );
        if (bookingId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User currentUser = getCurrentUser(request);
        PaymentService paymentService = new PaymentService();
        Payment payment = paymentService.getPendingOnlinePayment(
                bookingId, currentUser.getUserId()
        );
        BookingManagementService bookingService
                = new BookingManagementService();
        Booking booking = bookingService.getCustomerBooking(
                bookingId, currentUser.getUserId()
        );

        if (payment == null || booking == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Booking không có giao dịch trực tuyến đang chờ."
            );
            return;
        }

        request.setAttribute("booking", booking);
        request.setAttribute("payment", payment);
        request.getRequestDispatcher("/views/customer/payment.jsp")
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
        Integer bookingId = ParseUtil.toPositiveInteger(
                request.getParameter("bookingId")
        );
        Integer paymentId = ParseUtil.toPositiveInteger(
                request.getParameter("paymentId")
        );
        if (bookingId == null || paymentId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User currentUser = getCurrentUser(request);
        PaymentService service = new PaymentService();
        try {
            boolean success = service.completePayment(
                    paymentId, bookingId, currentUser.getUserId()
            );
            request.getSession().setAttribute(
                    success ? "flashSuccess" : "flashError",
                    success
                            ? "Thanh toán thành công. Booking đã được xác nhận."
                            : "Thanh toán thất bại. Vui lòng thử lại."
            );
        } catch (IllegalArgumentException exception) {
            request.getSession().setAttribute(
                    "flashError", exception.getMessage()
            );
        }

        response.sendRedirect(
                request.getContextPath()
                + "/customer/booking-detail?id=" + bookingId
        );
    }

    private User getCurrentUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("currentUser");
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
