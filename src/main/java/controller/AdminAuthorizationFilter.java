/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Filter.java to edit this template
 */
package controller;

import entity.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = {"/admin/*"}) //moi request bat dau bang /admin se duoc filter nay xu ly
public class AdminAuthorizationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,// ham doFilter duoc goi khi co request den /admin/*
                         FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request; //
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        User user = null; //kiem tra xem user hien tai co phai la admin hay khong
        if (httpRequest.getSession(false) != null) {
            user = (User) httpRequest.getSession(false)
                    .getAttribute("currentUser"); //lay thong tin user hien tai tu session
        }
        if (user == null) {// neu user chua dang nhap thi chuyen huong den trang login
            httpResponse.sendRedirect(
                    httpRequest.getContextPath() + "/login" // neu user chua dang nhap thi chuyen huong den trang login
            );
            return;
        }
        if (!"Admin".equals(user.getRoleName())) { // neu user hien tai khong phai la admin thi tra ve 403
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN); // neu user khong phai la admin thi tra ve 403
            return;
        }
        chain.doFilter(request, response);// cho phep request tiep tuc duoc xu ly
}
}