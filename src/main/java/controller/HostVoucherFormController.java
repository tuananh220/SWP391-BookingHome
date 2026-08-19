/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import entity.User;
import entity.Voucher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import service.HostVoucherService;
import ultis.ParseUtil;

/**
 *
 * @author Admin
 */
@WebServlet(name = "HostVoucherFormController", urlPatterns = {"/host/voucher-form"})
public class HostVoucherFormController extends HttpServlet {

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
            out.println("<title>Servlet HostVoucherFormController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet HostVoucherFormController at " + request.getContextPath() + "</h1>");
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
        HostVoucherService service = new HostVoucherService();
        Integer voucherId = ParseUtil.toPositiveInteger(
                request.getParameter("id")
        );

        Voucher voucher;
        if (voucherId == null) {
            voucher = new Voucher();
            voucher.setMinOrderValue(BigDecimal.ZERO);
            voucher.setUsageLimit(1);
        } else {
            voucher = service.getVoucher(voucherId, currentUser.getUserId());
            if (voucher == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }
        forwardForm(request, response, service, voucher, currentUser.getUserId());
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
        HostVoucherService service = new HostVoucherService();
        Voucher voucher = buildVoucher(request, currentUser.getUserId());

        try {
            boolean editing = voucher.getVoucherId() > 0;
            boolean success = editing
                    ? service.update(voucher)
                    : service.create(voucher) > 0;
            if (success) {
                request.getSession().setAttribute(
                        "flashSuccess",
                        editing
                                ? "Cập nhật voucher thành công."
                                : "Tạo voucher thành công."
                );
                response.sendRedirect(
                        request.getContextPath() + "/host/vouchers"
                );
                return;
            }
            request.setAttribute("error", "Không thể lưu voucher.");
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
        }

        forwardForm(
                request, response, service, voucher, currentUser.getUserId()
        );
    }

    private Voucher buildVoucher(HttpServletRequest request, int hostId) {
        Voucher voucher = new Voucher();
        Integer voucherId = ParseUtil.toPositiveInteger(
                request.getParameter("voucherId")
        );
        voucher.setVoucherId(voucherId == null ? 0 : voucherId);
        voucher.setCreatedById(hostId);
        voucher.setHomestayId(ParseUtil.toPositiveInteger(
                request.getParameter("homestayId")
        ));
        voucher.setVoucherCode(request.getParameter("voucherCode"));
        voucher.setDiscountRate(ParseUtil.toNonNegativeBigDecimal(
                request.getParameter("discountRate")
        ));
        voucher.setMaxDiscountAmount(ParseUtil.toNonNegativeBigDecimal(
                request.getParameter("maxDiscountAmount")
        ));
        BigDecimal minOrder = ParseUtil.toNonNegativeBigDecimal(
                request.getParameter("minOrderValue")
        );
        voucher.setMinOrderValue(
                minOrder == null ? BigDecimal.ZERO : minOrder
        );
        voucher.setStartDate(ParseUtil.toLocalDateTime(
                request.getParameter("startDate")
        ));
        voucher.setEndDate(ParseUtil.toLocalDateTime(
                request.getParameter("endDate")
        ));
        Integer usageLimit = ParseUtil.toPositiveInteger(
                request.getParameter("usageLimit")
        );
        voucher.setUsageLimit(usageLimit == null ? 0 : usageLimit);
        voucher.setActive(true);
        return voucher;
    }

    private void forwardForm(HttpServletRequest request,
            HttpServletResponse response,
            HostVoucherService service,
            Voucher voucher, int hostId)
            throws ServletException, IOException {
        request.setAttribute("voucher", voucher);
        request.setAttribute("homestays", service.getHomestays(hostId));
        request.getRequestDispatcher("/views/host/voucher-form.jsp")
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
