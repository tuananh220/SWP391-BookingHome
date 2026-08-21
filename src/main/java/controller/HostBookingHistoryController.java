package controller;

import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import service.HostBookingService;
import service.HostHomestayService;
import ultis.ParseUtil;

@WebServlet(name = "HostBookingHistoryController", urlPatterns = {"/host/booking-history"})
public class HostBookingHistoryController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("currentUser");
        Integer homestayId = ParseUtil.toPositiveInteger(
                request.getParameter("homestayId")
        );
        LocalDate fromDate = ParseUtil.toLocalDate(
                request.getParameter("fromDate")
        );
        LocalDate toDate = ParseUtil.toLocalDate(
                request.getParameter("toDate")
        );
        HostBookingService service = new HostBookingService();
        try {
            request.setAttribute("bookings", service.getHistory(
                    user.getUserId(), request.getParameter("status"),
                    homestayId, fromDate, toDate
            ));
            request.setAttribute("cancellationSummary",
                    service.getCancellationSummary(
                            user.getUserId(), homestayId, fromDate, toDate
                    ));
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
            request.setAttribute("bookings", java.util.Collections.emptyList());
            request.setAttribute("cancellationSummary",
                    new entity.HostCancellationSummary());
        }
        request.setAttribute("homestays",
                new HostHomestayService().getHomestays(user.getUserId()));
        request.getRequestDispatcher("/views/host/booking-history.jsp")
                .forward(request, response);
    }
}