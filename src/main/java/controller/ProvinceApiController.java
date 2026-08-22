package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import service.ProvinceService;
import ultis.ParseUtil;

/**
 * Controller API phục vụ danh sách Tỉnh/Thành phố và Xã/Phường theo cơ cấu mới 2025/2026.
 */
@WebServlet(name = "ProvinceApiController", urlPatterns = {"/api/provinces"})
public class ProvinceApiController extends HttpServlet {

    private ProvinceService provinceService;

    @Override
    public void init() throws ServletException {
        provinceService = new ProvinceService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "public, max-age=3600");

        String provinceParam = request.getParameter("provinceCode");
        if (provinceParam == null || provinceParam.isEmpty()) {
            provinceParam = request.getParameter("province");
        }

        Integer provinceCode = ParseUtil.toPositiveInteger(provinceParam);
        String jsonResult;

        if (provinceCode != null && provinceCode > 0) {
            jsonResult = provinceService.getWardsByProvinceJson(provinceCode);
        } else {
            jsonResult = provinceService.getProvincesJson();
        }

        try (PrintWriter out = response.getWriter()) {
            out.write(jsonResult);
        }
    }
}
