/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import entity.CancellationPolicy;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import service.AdminPolicyService;
import ultis.ParseUtil;

/**
 *
 * @author Admin
 */
@WebServlet(name = "AdminPolicyFormController", urlPatterns = {"/admin/policy-form"})
public class AdminPolicyFormController extends HttpServlet {

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
            out.println("<title>Servlet AdminPolicyFormController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AdminPolicyFormController at " + request.getContextPath() + "</h1>");
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
        Integer policyId = ParseUtil.toPositiveInteger(
                request.getParameter("id")
        );
        CancellationPolicy policy;
        if (policyId == null) {
            policy = new CancellationPolicy();
            policy.setPartialRefundPercent(50);
        } else {
            policy = new AdminPolicyService().getPolicy(policyId);
            if (policy == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }
        request.setAttribute("policy", policy);
        request.getRequestDispatcher("/views/admin/policy-form.jsp")
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
        CancellationPolicy policy = buildPolicy(request);
        User admin = (User) request.getSession().getAttribute("currentUser");
        policy.setCreatedById(admin.getUserId());
        AdminPolicyService service = new AdminPolicyService();
        try {
            boolean editing = policy.getPolicyId() > 0;
            boolean success = editing
                    ? service.update(policy)
                    : service.create(policy) > 0;
            if (success) {
                request.getSession().setAttribute(
                        "flashSuccess",
                        editing ? "Cập nhật chính sách thành công."
                                : "Tạo chính sách thành công."
                );
                response.sendRedirect(
                        request.getContextPath() + "/admin/policies"
                );
                return;
            }
            request.setAttribute("error", "Không thể lưu chính sách.");
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
        }
        request.setAttribute("policy", policy);
        request.getRequestDispatcher("/views/admin/policy-form.jsp")
                .forward(request, response);
    }

    private CancellationPolicy buildPolicy(HttpServletRequest request) {
        CancellationPolicy policy = new CancellationPolicy();
        Integer policyId = ParseUtil.toPositiveInteger(
                request.getParameter("policyId")
        );
        policy.setPolicyId(policyId == null ? 0 : policyId);
        policy.setPolicyName(request.getParameter("policyName"));
        policy.setDescription(request.getParameter("description"));
        Integer fullDays = ParseUtil.toNonNegativeInteger(
                request.getParameter("fullRefundDays")
        );
        Integer partialDays = ParseUtil.toNonNegativeInteger(
                request.getParameter("partialRefundDays")
        );
        BigDecimal percent = ParseUtil.toNonNegativeBigDecimal(
                request.getParameter("partialRefundPercent")
        );
        policy.setFullRefundDays(fullDays == null ? -1 : fullDays);
        policy.setPartialRefundDays(partialDays == null ? -1 : partialDays);
        policy.setPartialRefundPercent(
                percent == null ? -1 : percent.doubleValue()
        );
        policy.setActive(true);
        return policy;
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
