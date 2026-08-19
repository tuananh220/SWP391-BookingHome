/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AdminUserService;
import ultis.ParseUtil;

/**
 *
 * @author Admin
 */
@WebServlet(name = "AdminUserFormController", urlPatterns = {"/admin/user-form"})
public class AdminUserFormController extends HttpServlet {

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
            out.println("<title>Servlet AdminUserFormController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AdminUserFormController at " + request.getContextPath() + "</h1>");
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
        Integer userId = ParseUtil.toPositiveInteger(
                request.getParameter("id")
        );
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        AdminUserService service = new AdminUserService();
        User user = service.getUser(userId);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        forwardForm(request, response, service, user);
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
        Integer userId = ParseUtil.toPositiveInteger(
                request.getParameter("userId")
        );
        Integer roleId = ParseUtil.toPositiveInteger(
                request.getParameter("roleId")
        );
        if (userId == null || roleId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User user = new User();
        user.setUserId(userId);
        user.setRoleId(roleId);
        user.setFullName(trim(request.getParameter("fullName")));
        user.setEmail(trim(request.getParameter("email")));
        user.setPhoneNumber(emptyToNull(request.getParameter("phoneNumber")));
        user.setAddress(emptyToNull(request.getParameter("address")));
        user.setStatus(request.getParameter("status"));

        AdminUserService service = new AdminUserService();
        User currentAdmin = (User) request.getSession()
                .getAttribute("currentUser");
        try {
            if (service.updateUser(user, currentAdmin.getUserId())) {
                if (user.getUserId() == currentAdmin.getUserId()) {
                    User refreshedAdmin = service.getUser(user.getUserId());
                    if (refreshedAdmin != null) {
                        refreshedAdmin.setPasswordHash(null);
                        request.getSession().setAttribute(
                                "currentUser", refreshedAdmin
                        );
                    }
                }
                request.getSession().setAttribute(
                        "flashSuccess", "Cập nhật tài khoản thành công."
                );
                response.sendRedirect(
                        request.getContextPath() + "/admin/users"
                );
                return;
            }
            request.setAttribute(
                    "error", "Không thể cập nhật. Email có thể đã tồn tại."
            );
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
        }
        forwardForm(request, response, service, user);
    }

    private void forwardForm(HttpServletRequest request,
            HttpServletResponse response,
            AdminUserService service, User user)
            throws ServletException, IOException {
        request.setAttribute("user", user);
        request.setAttribute("roles", service.getRoles());
        request.getRequestDispatcher("/views/admin/user-form.jsp")
                .forward(request, response);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String emptyToNull(String value) {
        value = trim(value);
        return value == null || value.isEmpty() ? null : value;
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
