package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AuthService;

@WebServlet(name = "ResetPasswordController", urlPatterns = {"/reset-password"})
public class ResetPasswordController extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() {
        authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/auth/reset-password.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        request.setAttribute("email", email);
        try {
            boolean success = authService.resetPassword(
                    email,
                    request.getParameter("otp"),
                    request.getParameter("password"),
                    request.getParameter("confirmPassword")
            );
            if (!success) {
                request.setAttribute("error", "Không thể đổi mật khẩu.");
                request.getRequestDispatcher("/views/auth/reset-password.jsp")
                        .forward(request, response);
                return;
            }
            request.getSession().setAttribute(
                    "flashSuccess",
                    "Đổi mật khẩu thành công. Bạn có thể đăng nhập."
            );
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
            request.getRequestDispatcher("/views/auth/reset-password.jsp")
                    .forward(request, response);
        }
    }
}
