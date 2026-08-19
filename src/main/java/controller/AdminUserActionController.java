package controller;

import entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AdminUserService;
import ultis.ParseUtil;

@WebServlet(name = "AdminUserActionController", urlPatterns = {"/admin/user-action"})
public class AdminUserActionController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = ParseUtil.toPositiveInteger(
                request.getParameter("userId")
        );
        if (userId == null || !"delete".equals(request.getParameter("action"))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        User currentAdmin = (User) request.getSession()
                .getAttribute("currentUser");
        try {
            boolean success = new AdminUserService().deleteUser(
                    userId, currentAdmin.getUserId()
            );
            request.getSession().setAttribute(
                    success ? "flashSuccess" : "flashError",
                    success ? "Đã xóa tài khoản." : "Không thể xóa tài khoản."
            );
        } catch (IllegalArgumentException exception) {
            request.getSession().setAttribute(
                    "flashError", exception.getMessage()
            );
        }
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
}
