package controller;

import entity.StayChangeRequest;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import service.StayChangeService;
import ultis.ParseUtil;

@WebServlet(name = "HostRefundController", urlPatterns = {"/host/refund"})
public class HostRefundController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        Integer requestId = ParseUtil.toPositiveInteger(
                request.getParameter("requestId")
        );
        if (requestId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User host = (User) request.getSession().getAttribute("currentUser");
        StayChangeRequest changeRequest = new StayChangeService()
                .getHostRequest(requestId, host.getUserId());
        if (changeRequest == null
                || !"EarlyCheckout".equals(changeRequest.getRequestType())
                || !"Accepted".equals(changeRequest.getStatus())
                || !"Pending".equals(changeRequest.getRefundStatus())) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Yêu cầu không có khoản hoàn tiền đang chờ.");
            return;
        }

        request.setAttribute("changeRequest", changeRequest);
        request.getRequestDispatcher("/views/host/refund.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        Integer requestId = ParseUtil.toPositiveInteger(
                request.getParameter("requestId")
        );
        if (requestId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User host = (User) request.getSession().getAttribute("currentUser");
        try {
            boolean success = new StayChangeService().completeRefund(
                    requestId, host.getUserId()
            );
            request.getSession().setAttribute(
                    success ? "flashSuccess" : "flashError",
                    success ? "Đã hoàn tiền thành công."
                            : "Không thể hoàn tiền."
            );
        } catch (IllegalArgumentException exception) {
            request.getSession().setAttribute(
                    "flashError", exception.getMessage()
            );
        }
        response.sendRedirect(request.getContextPath()
                + "/host/stay-change-requests");
    }
}