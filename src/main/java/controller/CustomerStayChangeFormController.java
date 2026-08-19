/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import entity.Booking;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import service.StayChangeService;
import ultis.ParseUtil;

/**
 *
 * @author Admin
 */
@WebServlet(name = "CustomerStayChangeFormController", urlPatterns = {"/customer/stay-change-form"})
public class CustomerStayChangeFormController extends HttpServlet {

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
            out.println("<title>Servlet CustomerStayChangeFormController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CustomerStayChangeFormController at " + request.getContextPath() + "</h1>");
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
        User user = getUser(request);
        StayChangeService service = new StayChangeService();
        Booking booking = service.getEligibleBooking(
                bookingId, user.getUserId()
        );
        if (booking == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Booking không thể tạo yêu cầu thay đổi."
            );
            return;
        }
        request.setAttribute("booking", booking);
        request.getRequestDispatcher(
                "/views/customer/stay-change-form.jsp"
        ).forward(request, response);
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
        LocalDate requestedDate = ParseUtil.toLocalDate(
                request.getParameter("requestedCheckOutDate")
        );
        if (bookingId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User user = getUser(request);
        StayChangeService service = new StayChangeService();
        try {
            int requestId = service.createRequest(
                    bookingId,
                    user.getUserId(),
                    request.getParameter("requestType"),
                    requestedDate,
                    request.getParameter("customerNote")
            );
            if (requestId <= 0) {
                throw new IllegalStateException("Không thể tạo yêu cầu.");
            }
            request.getSession().setAttribute(
                    "flashSuccess", "Đã gửi yêu cầu đến chủ nhà."
            );
            response.sendRedirect(
                    request.getContextPath()
                    + "/customer/stay-change-requests"
            );
        } catch (IllegalArgumentException | IllegalStateException exception) {
            Booking booking = service.getEligibleBooking(
                    bookingId, user.getUserId()
            );
            request.setAttribute("booking", booking);
            request.setAttribute("error", exception.getMessage());
            request.getRequestDispatcher(
                    "/views/customer/stay-change-form.jsp"
            ).forward(request, response);
        }
    }

    private User getUser(HttpServletRequest request) {
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
