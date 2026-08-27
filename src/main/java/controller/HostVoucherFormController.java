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
        User currentUser = getCurrentUser(request);// lay thong tin user hien tai
        HostVoucherService service = new HostVoucherService();// khoi tao service de lay thong tin voucher tu database
        Integer voucherId = ParseUtil.toPositiveInteger(
                request.getParameter("id")// lay ID voucher tu request
        );

        Voucher voucher;
        if (voucherId == null) {
            voucher = new Voucher();
            voucher.setMinOrderValue(BigDecimal.ZERO);
            voucher.setUsageLimit(1);// neu voucher moi tao thi mac dinh min order value = 0 va usage limit = 1
        } else {
            voucher = service.getVoucher(voucherId, currentUser.getUserId());// lay voucher tu database theo ID va hostId
            if (voucher == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);// neu voucher khong ton tai thi tra ve 404
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
        User currentUser = getCurrentUser(request);// lay thong tin user hien tai
        HostVoucherService service = new HostVoucherService();// khoi tao service de lay thong tin voucher tu database
        Voucher voucher = buildVoucher(request, currentUser.getUserId());// lay thong tin voucher tu request va gan hostId la user hien tai

        try {
            boolean editing = voucher.getVoucherId() > 0;// neu voucherId > 0 thi la edit, nguoc lai la create
            boolean success = editing         
                    ? service.update(voucher) // neu la edit thi goi update
                    : service.create(voucher) > 0;// neu la create thi goi create va tra ve true neu create thanh cong
            if (success) {
                request.getSession().setAttribute( 
                        "flashSuccess",
                        editing
                                ? "Cập nhật voucher thành công."
                                : "Tạo voucher thành công."
                );
                response.sendRedirect(
                        request.getContextPath() + "/host/vouchers" // neu create hoac update thanh cong thi chuyen huong ve trang danh sach voucher
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

    private Voucher buildVoucher(HttpServletRequest request, int hostId) {// dung private de dam bao tinh dong goi, han che pham vi truy cap va giam su phu thuoc giua cac class vi buildvoucher chi la phuong thuc ho tro noi bo
        Voucher voucher = new Voucher();
        Integer voucherId = ParseUtil.toPositiveInteger(
                request.getParameter("voucherId")//lay ID
        );
        voucher.setVoucherId(voucherId == null ? 0 : voucherId);//neu voucher khong ton tai thi ID=0, neu ton tai thi dung ID do
        voucher.setCreatedById(hostId);// gan host dang tao voucher
        voucher.setHomestayId(ParseUtil.toPositiveInteger(//lay homestay trung voi ID
                request.getParameter("homestayId")
        ));
        voucher.setVoucherCode(request.getParameter("voucherCode"));// lay voucher code
        voucher.setDiscountRate(ParseUtil.toNonNegativeBigDecimal(//lay discount rate
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

    private void forwardForm(HttpServletRequest request, // ham ho tro chuyen huong den trang voucher-form.jsp
            HttpServletResponse response,
            HostVoucherService service,
            Voucher voucher, int hostId)
            throws ServletException, IOException {
        request.setAttribute("voucher", voucher);// gan voucher vao request de hien thi thong tin voucher tren form
        request.setAttribute("homestays", service.getHomestays(hostId)); // gan danh sach homestay vao request de hien thi tren form
        request.getRequestDispatcher("/views/host/voucher-form.jsp") // chuyen huong den trang voucher-form.jsp
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
