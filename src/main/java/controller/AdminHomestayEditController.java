/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import entity.Homestay;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AdminHomestayService;
import ultis.ParseUtil;

/**
 *
 * @author Admin
 */
@WebServlet(name = "AdminHomestayEditController", urlPatterns = {"/admin/homestay-edit"})
public class AdminHomestayEditController extends HttpServlet {

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
            out.println("<title>Servlet AdminHomestayEditController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AdminHomestayEditController at " + request.getContextPath() + "</h1>");
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
        processRequest(request, response);
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
        if (homestayId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Homestay homestay = new Homestay();
        homestay.setHomestayId(homestayId);
        homestay.setCancellationPolicyId(ParseUtil.toPositiveInteger(
                request.getParameter("cancellationPolicyId")
        ));
        homestay.setTitle(trim(request.getParameter("title")));
        homestay.setDescription(trim(request.getParameter("description")));
        homestay.setAddress(trim(request.getParameter("address")));
        homestay.setCity(trim(request.getParameter("city")));
        homestay.setDistrict(trim(request.getParameter("district")));
        homestay.setLatitude(ParseUtil.toBigDecimal(
                request.getParameter("latitude")
        ));
        homestay.setLongitude(ParseUtil.toBigDecimal(
                request.getParameter("longitude")
        ));
        homestay.setPricePerNight(ParseUtil.toNonNegativeBigDecimal(
                request.getParameter("pricePerNight")
        ));
        Integer maxGuests = ParseUtil.toPositiveInteger(
                request.getParameter("maxGuests")
        );
        homestay.setMaxGuests(maxGuests == null ? 0 : maxGuests);

        AdminHomestayService service = new AdminHomestayService();
        try {
            boolean success = service.update(homestay);
            request.getSession().setAttribute(
                    success ? "flashSuccess" : "flashError",
                    success ? "Cập nhật homestay thành công."
                            : "Không thể cập nhật homestay."
            );
        } catch (IllegalArgumentException exception) {
            request.getSession().setAttribute(
                    "flashError", exception.getMessage()
            );
        }
        response.sendRedirect(
                request.getContextPath()
                + "/admin/homestay-detail?id=" + homestayId
        );
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
