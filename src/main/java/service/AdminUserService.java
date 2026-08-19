/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author Admin
 */
import entity.AdminDashboardSummary;
import entity.Role;
import entity.User;
import interfaces.IAdminUserRepository;
import repository.AdminUserRepository;
import ultis.ValidationUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ultis.ValidationUtil;

public class AdminUserService {

    private final IAdminUserRepository repository;

    public AdminUserService() {
        repository = new AdminUserRepository();
    }

    public AdminDashboardSummary getDashboardSummary() {
        try {
            return repository.getDashboardSummary();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new AdminDashboardSummary();
        }
    }

    public List<User> getUsers(String keyword, String roleName,
            String status) {
        try {
            return repository.findUsers(keyword, roleName, status);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<User>();
        }
    }

    public User getUser(int userId) {
        try {
            return repository.findUserById(userId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public List<Role> getRoles() {
        try {
            return repository.findRoles();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Role>();
        }
    }

    public boolean updateUser(User user, int currentAdminId) {
        validate(user);
        if (user.getUserId() == currentAdminId) {
            User currentAdmin = getUser(currentAdminId);
            if (currentAdmin == null
                    || user.getRoleId() != currentAdmin.getRoleId()
                    || !"Active".equals(user.getStatus())) {
                throw new IllegalArgumentException(
                        "Admin không thể tự hạ quyền hoặc khóa tài khoản của mình."
                );
            }
        }
        try {
            return repository.updateUser(user);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId, int currentAdminId) {
        if (userId == currentAdminId) {
            throw new IllegalArgumentException(
                    "Admin không thể xóa tài khoản của chính mình."
            );
        }
        User user = getUser(userId);
        if (user == null) {
            throw new IllegalArgumentException("Không tìm thấy tài khoản.");
        }
        try {
            if (repository.hasRelatedData(userId)) {
                throw new IllegalArgumentException(
                        "Không thể xóa vì tài khoản đã có homestay, booking, đánh giá hoặc voucher. Hãy khóa tài khoản thay thế."
                );
            }
            return repository.deleteUser(userId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new IllegalArgumentException(
                    "Không thể xóa tài khoản vì còn dữ liệu liên quan. Hãy khóa tài khoản thay thế."
            );
        }
    }

    private void validate(User user) {
        if (ValidationUtil.isBlank(user.getFullName())
                || user.getFullName().length() > 100) {
            throw new IllegalArgumentException("Họ tên không hợp lệ.");
        }
        if (!ValidationUtil.isValidEmail(user.getEmail())
                || user.getEmail().length() > 100) {
            throw new IllegalArgumentException("Email không hợp lệ.");
        }
        if (user.getRoleId() <= 0) {
            throw new IllegalArgumentException("Vai trò không hợp lệ.");
        }
        if (!Arrays.asList("Active", "Blocked", "Pending")
                .contains(user.getStatus())) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        }
        if (user.getPhoneNumber() != null
                && user.getPhoneNumber().length() > 20) {
            throw new IllegalArgumentException("Số điện thoại quá dài.");
        }
    }
}
