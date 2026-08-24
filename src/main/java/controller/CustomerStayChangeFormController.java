/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import entity.Booking;
import entity.StayChangeRequest;
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
        Integer requestId = ParseUtil.toPositiveInteger(
            request.getParameter("requestId")
        );
        Integer bookingId = ParseUtil.toPositiveInteger(
            request.getParameter("bookingId")
        );
        User user = getUser(request);
        StayChangeService service = new StayChangeService();
        StayChangeRequest existing = requestId == null
            ? null : service.getCustomerRequest(requestId, user.getUserId());
        if (requestId != null && (existing == null
            || !"Pending".equals(existing.getStatus()))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Chỉ có thể chỉnh sửa yêu cầu đang chờ xử lý.");
            return;
        }
        if (existing != null) {
            bookingId = existing.getBookingId();
        }
        if (bookingId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Booking booking = existing == null
            ? service.getEligibleBooking(bookingId, user.getUserId())
            : service.getEligibleBookingForRequest(
                requestId, user.getUserId()
            );
        if (booking == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Booking không thể tạo yêu cầu thay đổi."
            );
            return;
        }
        request.setAttribute("booking", booking);
        request.setAttribute("editRequest", existing);
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
        Integer requestId = ParseUtil.toPositiveInteger(
            request.getParameter("requestId")
        );
        LocalDate requestedDate = ParseUtil.toLocalDate(
                request.getParameter("requestedCheckOutDate")
        );
        if (bookingId == null && requestId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User user = getUser(request);
        StayChangeService service = new StayChangeService();
        try {
            if (requestId != null) {
                if (!service.updateRequest(
                        requestId, user.getUserId(),
                        request.getParameter("requestType"),
                        requestedDate,
                        request.getParameter("customerNote"),
                        request.getParameter("refundAccountName"),
                        request.getParameter("refundBankName"),
                        request.getParameter("refundAccountNumber"))) {
                    throw new IllegalStateException(
                            "Không thể chỉnh sửa yêu cầu."
                    );
                }
            } else {
                int createdRequestId = service.createRequest(
                        bookingId, user.getUserId(),
                        request.getParameter("requestType"), requestedDate,
                        request.getParameter("customerNote"),
                        request.getParameter("refundAccountName"),
                        request.getParameter("refundBankName"),
                        request.getParameter("refundAccountNumber")
                );
                if (createdRequestId <= 0) {
                    throw new IllegalStateException("Không thể tạo yêu cầu.");
                }
            }
            request.getSession().setAttribute(
                    "flashSuccess",
                    requestId == null
                    ? "Đã gửi yêu cầu đến chủ nhà."
                    : "Đã cập nhật yêu cầu."
            );
            response.sendRedirect(
                    request.getContextPath()
                    + "/customer/stay-change-requests"
            );
        } catch (IllegalArgumentException | IllegalStateException exception) {
            Booking booking = service.getEligibleBooking(
                    bookingId == null ? 0 : bookingId, user.getUserId()
            );
                StayChangeRequest editRequest = requestId == null
                    ? null : service.getCustomerRequest(
                        requestId, user.getUserId()
                    );
                if (editRequest != null) {
                booking = service.getEligibleBookingForRequest(
                    requestId, user.getUserId()
                );
                }
            request.setAttribute("booking", booking);
                request.setAttribute("editRequest", editRequest);
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
