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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import service.HostHomestayService;
import ultis.ParseUtil;

/**
 *
 * @author Admin
 */
@WebServlet(name = "HostHomestayFormController", urlPatterns = {"/host/homestay-form"})
public class HostHomestayFormController extends HttpServlet {

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
            out.println("<title>Servlet HostHomestayFormController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet HostHomestayFormController at " + request.getContextPath() + "</h1>");
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
        User currentUser = getCurrentUser(request);
        HostHomestayService service = new HostHomestayService();
        Integer homestayId = ParseUtil.toPositiveInteger(
                request.getParameter("id")
        );

        HomestayForm form;
        if (homestayId == null) {
            form = new HomestayForm();
        } else {
            form = service.getForm(homestayId, currentUser.getUserId());
            if (form == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        forwardForm(request, response, service, form);
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
        User currentUser = getCurrentUser(request);
        HostHomestayService service = new HostHomestayService();
        HomestayForm form = buildForm(request, currentUser.getUserId());

        try {
            boolean editing = form.getHomestayId() > 0;
            boolean success;
            if (editing) {
                success = service.update(form);
            } else {
                success = service.create(form) > 0;
            }

            if (success) {
                request.getSession().setAttribute(
                        "flashSuccess",
                        editing
                                ? "Cập nhật homestay thành công."
                                : "Đã tạo homestay. Vui lòng chờ Admin duyệt."
                );
                response.sendRedirect(
                        request.getContextPath() + "/host/homestays"
                );
                return;
            }
            request.setAttribute("error", "Không thể lưu homestay.");
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
        }

        forwardForm(request, response, service, form);
    }

    private HomestayForm buildForm(HttpServletRequest request, int hostId) {
        HomestayForm form = new HomestayForm();
        Integer homestayId = ParseUtil.toPositiveInteger(
                request.getParameter("homestayId")
        );
        form.setHomestayId(homestayId == null ? 0 : homestayId);
        form.setHostId(hostId);
        form.setCancellationPolicyId(ParseUtil.toPositiveInteger(
                request.getParameter("cancellationPolicyId")
        ));
        form.setTitle(trim(request.getParameter("title")));
        form.setDescription(trim(request.getParameter("description")));
        form.setAddress(trim(request.getParameter("address")));
        form.setCity(trim(request.getParameter("city")));
        form.setDistrict(trim(request.getParameter("district")));
        form.setLatitude(ParseUtil.toBigDecimal(
                request.getParameter("latitude")
        ));
        form.setLongitude(ParseUtil.toBigDecimal(
                request.getParameter("longitude")
        ));
        form.setPricePerNight(ParseUtil.toNonNegativeBigDecimal(
                request.getParameter("pricePerNight")
        ));
        Integer maxGuests = ParseUtil.toPositiveInteger(
                request.getParameter("maxGuests")
        );
        form.setMaxGuests(maxGuests == null ? 0 : maxGuests);
        form.setAmenityIds(parseIds(request.getParameterValues("amenityIds")));
        form.setPaymentMethodIds(parseIds(
                request.getParameterValues("paymentMethodIds")
        ));
        form.setImageUrls(parseImageUrls(request.getParameter("imageUrls")));
        return form;
    }

    private List<Integer> parseIds(String[] values) {
        Set<Integer> ids = new LinkedHashSet<Integer>();
        if (values != null) {
            for (String value : values) {
                Integer id = ParseUtil.toPositiveInteger(value);
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        return new ArrayList<Integer>(ids);
    }

    private List<String> parseImageUrls(String value) {
        Set<String> urls = new LinkedHashSet<String>();
        if (value != null) {
            String[] lines = value.split("\\r?\\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    urls.add(line.trim());
                }
            }
        }
        return new ArrayList<String>(urls);
    }

    private void forwardForm(HttpServletRequest request,
            HttpServletResponse response,
            HostHomestayService service,
            HomestayForm form)
            throws ServletException, IOException {
        request.setAttribute("form", form);
        request.setAttribute("amenities", service.getAmenities());
        request.setAttribute("paymentMethods", service.getPaymentMethods());
        request.setAttribute("policies", service.getPolicies());
        request.getRequestDispatcher("/views/host/homestay-form.jsp")
                .forward(request, response);
    }

    private User getCurrentUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("currentUser");
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
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
