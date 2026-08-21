/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import entity.HomestayForm;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import entity.Homestay;
import entity.HomestaySchedule;
import service.HostScheduleService;
import service.HostHomestayService;
import ultis.ParseUtil;

/**
 *
 * @author Admin
 */
@WebServlet(name = "HostScheduleController", urlPatterns = {"/host/schedule"})
public class HostScheduleController extends HttpServlet {

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
            out.println("<title>Servlet HostScheduleController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet HostScheduleController at " + request.getContextPath() + "</h1>");
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
        Integer homestayId = ParseUtil.toPositiveInteger(
                request.getParameter("homestayId")
        );
        User currentUser = getCurrentUser(request);
        List<Homestay> homestays = new HostHomestayService()
            .getHomestays(currentUser.getUserId());
        if (homestayId == null && !homestays.isEmpty()) {
            homestayId = homestays.get(0).getHomestayId();
        }
        if (homestayId == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                "Bạn chưa có homestay.");
            return;
        }

        LocalDate fromDate = ParseUtil.toLocalDate(
            request.getParameter("fromDate")
        );
        LocalDate toDate = ParseUtil.toLocalDate(
            request.getParameter("toDate")
        );
        if (fromDate == null) {
            fromDate = LocalDate.now();
        }
        if (toDate == null) {
            toDate = fromDate.plusDays(59);
        }
        HostScheduleService service = new HostScheduleService();
        HomestayForm homestay = service.getOwnedHomestay(
                homestayId, currentUser.getUserId()
        );
        if (homestay == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("homestay", homestay);
        try {
            List<HomestaySchedule> schedules = service.getScheduleRange(
                homestay, currentUser.getUserId(), fromDate, toDate
            );
            request.setAttribute("schedules", schedules);
            request.setAttribute("availableCount", schedules.stream()
                .filter(day -> day.isAvailable() && !day.isBooked())
                .count());
            request.setAttribute("bookedCount", schedules.stream()
                .filter(HomestaySchedule::isBooked).count());
            request.setAttribute("lockedCount", schedules.stream()
                .filter(day -> !day.isAvailable() && !day.isBooked())
                .count());
        } catch (IllegalArgumentException exception) {
            request.setAttribute("schedules", java.util.Collections.emptyList());
            request.setAttribute("error", exception.getMessage());
        }
        request.setAttribute("homestays", homestays);
        request.setAttribute("fromDate", fromDate);
        request.setAttribute("toDate", toDate);
        request.setAttribute("today", LocalDate.now());
        request.getRequestDispatcher("/views/host/schedule.jsp")
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
        Integer homestayId = ParseUtil.toPositiveInteger(
                request.getParameter("homestayId")
        );
        String action = request.getParameter("action");
        User currentUser = getCurrentUser(request);
        HostScheduleService service = new HostScheduleService();
        if ("bulk-lock".equals(action) || "bulk-open".equals(action)) {
            LocalDate fromDate = ParseUtil.toLocalDate(request.getParameter("fromDate"));
            LocalDate toDate = ParseUtil.toLocalDate(request.getParameter("toDate"));
            if (homestayId == null || fromDate == null || toDate == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
            }
            try {
            boolean success = service.updateRange(
                homestayId, currentUser.getUserId(), fromDate, toDate,
                "bulk-open".equals(action),
                request.getParameter("lockReason")
            );
            request.getSession().setAttribute(
                success ? "flashSuccess" : "flashError",
                success ? "Đã cập nhật hàng loạt lịch."
                    : "Không thể cập nhật hàng loạt lịch."
            );
            } catch (IllegalArgumentException exception) {
            request.getSession().setAttribute(
                "flashError", exception.getMessage()
            );
            }
            response.sendRedirect(request.getContextPath()
                + "/host/schedule?homestayId=" + homestayId
                + "&fromDate=" + fromDate + "&toDate=" + toDate);
            return;
        }
        LocalDate scheduleDate = ParseUtil.toLocalDate(
            request.getParameter("scheduleDate")
        );
        if (homestayId == null || scheduleDate == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            boolean success;
            if ("clear".equals(action)) {
                success = service.clear(
                        homestayId, currentUser.getUserId(), scheduleDate
                );
            } else {
                success = service.save(
                        homestayId,
                        currentUser.getUserId(),
                        scheduleDate,
                        ParseUtil.toNonNegativeBigDecimal(
                                request.getParameter("customPrice")
                        ),
                        request.getParameter("available") != null,
                        request.getParameter("lockReason")
                );
            }
            request.getSession().setAttribute(
                    success ? "flashSuccess" : "flashError",
                    success
                            ? "Đã cập nhật lịch homestay."
                            : "Không thể cập nhật lịch."
            );
        } catch (IllegalArgumentException exception) {
            request.getSession().setAttribute(
                    "flashError", exception.getMessage()
            );
        }

        response.sendRedirect(
                request.getContextPath()
                + "/host/schedule?homestayId=" + homestayId
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
