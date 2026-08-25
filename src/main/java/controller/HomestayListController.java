/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import entity.Homestay;
import entity.HomestaySearchCriteria;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import service.BannerService;
import service.HomestayService;
import ultis.ParseUtil;

/**
 *
 * @author Admin
 */
@WebServlet(name = "HomestayListController", urlPatterns = {"/homestays"})
public class HomestayListController extends HttpServlet {

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
            out.println("<title>Servlet HomestayListController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet HomestayListController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private HomestayService homestayService;

    @Override
    public void init() {
        homestayService = new HomestayService();
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
        request.setCharacterEncoding("UTF-8");
        HomestaySearchCriteria criteria = buildCriteria(request);
        List<Homestay> homestays;

        try {
            homestays = homestayService.search(criteria);
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
            homestays = new ArrayList<Homestay>();
        }

        request.setAttribute("criteria", criteria);
        request.setAttribute("homestays", homestays);
        request.setAttribute("amenities", homestayService.getAmenities());
        request.setAttribute("cities", homestayService.getCities());
        request.setAttribute("banners", new BannerService().getActive());
        request.getRequestDispatcher("/views/customer/homestay-list.jsp")
                .forward(request, response);
    }

    private HomestaySearchCriteria buildCriteria(HttpServletRequest request) {
        HomestaySearchCriteria criteria = new HomestaySearchCriteria();
        criteria.setKeyword(request.getParameter("keyword"));
        criteria.setCity(request.getParameter("city"));
        criteria.setMinPrice(ParseUtil.toNonNegativeBigDecimal(
                request.getParameter("minPrice")
        ));
        criteria.setMaxPrice(ParseUtil.toNonNegativeBigDecimal(
                request.getParameter("maxPrice")
        ));

        Integer guests = ParseUtil.toPositiveInteger(request.getParameter("guests"));
        criteria.setGuests(guests);

        String[] amenityIdValues = request.getParameterValues("amenityIds");
        if (amenityIdValues == null || amenityIdValues.length == 0) {
            amenityIdValues = request.getParameterValues("amenityId");
        }
        criteria.setAmenityIds(ParseUtil.toPositiveIntegerList(amenityIdValues));
        criteria.setMinRating(ParseUtil.toPositiveInteger(
                request.getParameter("minRating")
        ));

        LocalDate checkIn = ParseUtil.toLocalDate(request.getParameter("checkIn"));
        criteria.setCheckInDate(checkIn);

        LocalDate checkOut = ParseUtil.toLocalDate(request.getParameter("checkOut"));
        criteria.setCheckOutDate(checkOut);

        return criteria;
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
        processRequest(request, response);
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
