package controller;

import entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AuthService;

@WebServlet(name = "ChangePasswordController", urlPatterns = {"/change-password"})
public class ChangePasswordController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/profile");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User currentUser = (User) request.getSession()
                .getAttribute("currentUser");
        try {
            boolean success = new AuthService().changePassword(
                    currentUser.getUserId(),
                    request.getParameter("currentPassword"),
                    request.getParameter("newPassword"),
                    request.getParameter("confirmPassword")
            );
            if (success) {
                request.setAttribute("passwordSuccess", "Đổi mật khẩu thành công.");
            } else {
                request.setAttribute("passwordError", "Không thể đổi mật khẩu.");
            }
        } catch (IllegalArgumentException exception) {
            request.setAttribute("passwordError", exception.getMessage());
        }
        request.getRequestDispatcher("/views/customer/profile.jsp")
                .forward(request, response);
    }
}
