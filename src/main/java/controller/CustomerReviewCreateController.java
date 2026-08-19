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
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import service.ReviewService;
import ultis.FileUploadUtil;
import ultis.ParseUtil;

/**
 *
 * @author Admin
 */
@WebServlet(name = "CustomerReviewCreateController", urlPatterns = {"/customer/review/create"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5L * 1024L * 1024L,
        maxRequestSize = 26L * 1024L * 1024L
)
public class CustomerReviewCreateController extends HttpServlet {

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
            out.println("<title>Servlet CustomerReviewCreateController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CustomerReviewCreateController at " + request.getContextPath() + "</h1>");
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
        ReviewService service = new ReviewService();
        Booking booking = service.getReviewableBooking(
                bookingId, currentUser.getUserId()
        );
        if (booking == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Booking không thể đánh giá hoặc đã được đánh giá."
            );
            return;
        }

        request.setAttribute("booking", booking);
        request.getRequestDispatcher("/views/customer/review-create.jsp")
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
        Integer bookingId = ParseUtil.toPositiveInteger(
                request.getParameter("bookingId")
        );
        Integer ratingStars = ParseUtil.toPositiveInteger(
                request.getParameter("ratingStars")
        );
        if (bookingId == null || ratingStars == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User currentUser = getCurrentUser(request);
        ReviewService service = new ReviewService();
        Booking booking = service.getReviewableBooking(
                bookingId, currentUser.getUserId()
        );
        if (booking == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        List<String> imageUrls = new ArrayList<String>();
        try {
            imageUrls = FileUploadUtil.saveReviewImages(
                    request.getParts(), getServletContext()
            );
            service.createReview(
                    bookingId,
                    currentUser.getUserId(),
                    ratingStars,
                    request.getParameter("comment"),
                    imageUrls
            );

            request.getSession().setAttribute(
                    "flashSuccess", "Cảm ơn bạn đã đánh giá homestay."
            );
            response.sendRedirect(
                    request.getContextPath()
                    + "/customer/booking-detail?id=" + bookingId
            );
        } catch (IllegalArgumentException | IllegalStateException exception) {
            forwardWithError(
                    request, response, booking, ratingStars,
                    exception.getMessage()
            );
        } catch (IOException exception) {
            exception.printStackTrace();
            forwardWithError(
                    request, response, booking, ratingStars,
                    "Không thể lưu ảnh tải lên."
            );
        }
    }

    private void forwardWithError(HttpServletRequest request,
            HttpServletResponse response,
            Booking booking, Integer ratingStars,
            String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.setAttribute("booking", booking);
        request.setAttribute("selectedRating", ratingStars);
        request.setAttribute("comment", request.getParameter("comment"));
        request.getRequestDispatcher("/views/customer/review-create.jsp")
                .forward(request, response);
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
