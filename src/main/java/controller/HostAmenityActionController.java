package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.HostAmenityService;
import ultis.ParseUtil;

@WebServlet(name = "HostAmenityActionController", urlPatterns = {"/host/amenity-action"})
public class HostAmenityActionController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer amenityId = ParseUtil.toPositiveInteger(
                request.getParameter("amenityId")
        );
        if (amenityId == null
                || !"delete".equals(request.getParameter("action"))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            boolean success = new HostAmenityService().delete(amenityId);
            request.getSession().setAttribute(
                    success ? "flashSuccess" : "flashError",
                    success ? "Đã xóa tiện ích." : "Không thể xóa tiện ích."
            );
        } catch (IllegalArgumentException exception) {
            request.getSession().setAttribute(
                    "flashError", exception.getMessage()
            );
        }
        response.sendRedirect(request.getContextPath() + "/host/amenities");
    }
}
