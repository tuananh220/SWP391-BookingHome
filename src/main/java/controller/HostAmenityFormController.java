package controller;

import entity.Amenity;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.HostAmenityService;
import ultis.ParseUtil;

@WebServlet(name = "HostAmenityFormController", urlPatterns = {"/host/amenity-form"})
public class HostAmenityFormController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer amenityId = ParseUtil.toPositiveInteger(
                request.getParameter("id")
        );
        Amenity amenity;
        if (amenityId == null) {
            amenity = new Amenity();
        } else {
            amenity = new HostAmenityService().getAmenity(amenityId);
            if (amenity == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }
        request.setAttribute("amenity", amenity);
        request.getRequestDispatcher("/views/host/amenity-form.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Amenity amenity = new Amenity();
        Integer amenityId = ParseUtil.toPositiveInteger(
                request.getParameter("amenityId")
        );
        amenity.setAmenityId(amenityId == null ? 0 : amenityId);
        amenity.setAmenityName(request.getParameter("amenityName"));
        amenity.setIconClass(request.getParameter("iconClass"));

        HostAmenityService service = new HostAmenityService();
        try {
            boolean editing = amenity.getAmenityId() > 0;
            boolean success = editing
                    ? service.update(amenity)
                    : service.create(amenity) > 0;
            if (success) {
                request.getSession().setAttribute(
                        "flashSuccess",
                        editing ? "Cập nhật tiện ích thành công."
                                : "Thêm tiện ích thành công."
                );
                response.sendRedirect(
                        request.getContextPath() + "/host/amenities"
                );
                return;
            }
            request.setAttribute("error", "Không thể lưu tiện ích.");
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
        }
        request.setAttribute("amenity", amenity);
        request.getRequestDispatcher("/views/host/amenity-form.jsp")
                .forward(request, response);
    }
}
