/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import entity.BookingQuote;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.BookingService;
import ultis.ParseUtil;
import java.time.LocalDate;

/**
 *
 * @author Admin
 */
@WebServlet(name="BookingCreateController", urlPatterns={"/booking/create"})
public class BookingCreateController extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
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
            out.println("<title>Servlet BookingCreateController</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet BookingCreateController at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
         User currentUser = getCurrentUser(request);
        if (!isCustomer(currentUser)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Chỉ khách hàng mới có thể đặt phòng."
            );
            return;
            }

        try {
            BookingService bookingService = new BookingService();
            BookingQuote quote = buildQuote(request, bookingService);
            request.setAttribute("quote", quote);
            request.setAttribute("voucherCode", request.getParameter("voucher"));
            request.getRequestDispatcher("/views/customer/booking-create.jsp")
                    .forward(request, response);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            showError(request, response, exception.getMessage());
        }
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User currentUser = getCurrentUser(request);
        if (!isCustomer(currentUser)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Integer homestayId = ParseUtil.toPositiveInteger(
                request.getParameter("homestayId")
        );
        Integer guests = ParseUtil.toPositiveInteger(
                request.getParameter("guests")
        );
        Integer paymentMethodId = ParseUtil.toPositiveInteger(
                request.getParameter("paymentMethodId")
        );
        LocalDate checkIn = ParseUtil.toLocalDate(
                request.getParameter("checkIn")
        );
        LocalDate checkOut = ParseUtil.toLocalDate(
                request.getParameter("checkOut")
        );

        if (homestayId == null || guests == null
                || paymentMethodId == null) {
            showError(request, response, "Thông tin đặt phòng không hợp lệ.");
            return;
        }

        try {
            BookingService bookingService = new BookingService();
            int bookingId = bookingService.createBooking(
                    currentUser.getUserId(),
                    homestayId,
                    checkIn,
                    checkOut,
                    guests,
                    request.getParameter("voucherCode"),
                    paymentMethodId,
                    request.getParameter("note")
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/booking/success?id=" + bookingId
            );
        } catch (IllegalArgumentException | IllegalStateException exception) {
            showError(request, response, exception.getMessage());
        }
    }
    
    private BookingQuote buildQuote(HttpServletRequest request,
                                    BookingService bookingService) {
        Integer homestayId = ParseUtil.toPositiveInteger(
                request.getParameter("homestayId")
        );
        Integer guests = ParseUtil.toPositiveInteger(
                request.getParameter("guests")
        );

        if (homestayId == null || guests == null) {
            throw new IllegalArgumentException(
                    "Thông tin homestay hoặc số khách không hợp lệ."
            );
        }

        return bookingService.createQuote(
                homestayId,
                ParseUtil.toLocalDate(request.getParameter("checkIn")),
                ParseUtil.toLocalDate(request.getParameter("checkOut")),
                guests,
                request.getParameter("voucher")
        );
    }

    private User getCurrentUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("currentUser");
    }

    private boolean isCustomer(User user) {
        return user != null && "Customer".equals(user.getRoleName());
    }

    private void showError(HttpServletRequest request,
                           HttpServletResponse response,
                           String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/views/customer/booking-error.jsp")
                .forward(request, response);
    }
    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
