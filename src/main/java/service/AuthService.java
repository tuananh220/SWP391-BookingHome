/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author Admin
 */
import entity.User;
import interfaces.IAuthService;
import interfaces.IPasswordResetRepository;
import interfaces.IUserRepository;
import repository.PasswordResetRepository;
import repository.UserRepository;
import ultis.PasswordUtil;
import ultis.ValidationUtil;

import java.sql.SQLException;
import java.util.Random;

public class AuthService implements IAuthService {
    private final IUserRepository userRepository;
    private final IPasswordResetRepository resetRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
        this.resetRepository = new PasswordResetRepository();
    }

    public AuthService(IUserRepository userRepository) {
        this.userRepository = userRepository;
        this.resetRepository = new PasswordResetRepository();
    }

    @Override
    public User login(String email, String password) {
        email = ValidationUtil.normalizeEmail(email);

        if (!ValidationUtil.isValidEmail(email)
                || ValidationUtil.isBlank(password)) {
            return null;
        }

        try {
            User user = userRepository.findByEmail(email);

            if (user == null) {
                return null;
            }

            if (!PasswordUtil.verify(password, user.getPasswordHash())) {
                return null;
            }

            if (!"Active".equals(user.getStatus())) {
                return null;
            }

            // Không lưu password hash trong session.
            user.setPasswordHash(null);
            return user;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean register(String fullName, String email,
                            String password, String confirmPassword,
                            String phoneNumber, String roleName) {
        fullName = fullName == null ? null : fullName.trim();
        email = ValidationUtil.normalizeEmail(email);
        phoneNumber = phoneNumber == null ? null : phoneNumber.trim();
        String allowedRole = resolveRegisterRole(roleName);

        validateRegistration(
                fullName,
                email,
                password,
                confirmPassword,
                phoneNumber
        );

        try {
            if (userRepository.existsByEmail(email)) {
                return false;
            }

            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPasswordHash(PasswordUtil.hash(password));
            user.setPhoneNumber(
                    ValidationUtil.isBlank(phoneNumber) ? null : phoneNumber
            );

            return userRepository.createUser(user, allowedRole) > 0;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private String resolveRegisterRole(String roleName) {
        if ("Home Owner".equals(roleName)) {
            return "Home Owner";
        }
        return "Customer";
    }

    private void validateRegistration(String fullName, String email,
                                      String password, String confirmPassword,
                                      String phoneNumber) {
        if (ValidationUtil.isBlank(fullName) || fullName.length() > 100) {
            throw new IllegalArgumentException(
                    "Họ tên không được để trống và tối đa 100 ký tự."
            );
        }

        if (!ValidationUtil.isValidEmail(email) || email.length() > 100) {
            throw new IllegalArgumentException("Email không hợp lệ.");
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(
                    "Mật khẩu phải có ít nhất 6 ký tự."
            );
        }

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException(
                    "Xác nhận mật khẩu không khớp."
            );
        }

        if (phoneNumber != null && phoneNumber.length() > 20) {
            throw new IllegalArgumentException(
                    "Số điện thoại tối đa 20 ký tự."
            );
        }
    }

    @Override
    public String createResetOtp(String email) {
        email = ValidationUtil.normalizeEmail(email);
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Email không hợp lệ.");
        }
        try {
            User user = userRepository.findByEmail(email);
            if (user == null || !"Active".equals(user.getStatus())) {
                throw new IllegalArgumentException(
                        "Không tìm thấy tài khoản đang hoạt động với email này."
                );
            }
            String otp = String.valueOf(100000 + new Random().nextInt(900000));
            resetRepository.createToken(user.getUserId(), otp);
            return otp;
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new IllegalStateException(
                    "Không thể tạo mã OTP. Vui lòng thử lại."
            );
        }
    }

    @Override
    public boolean resetPassword(String email, String otp,
            String password, String confirmPassword) {
        email = ValidationUtil.normalizeEmail(email);
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Email không hợp lệ.");
        }
        if (ValidationUtil.isBlank(otp) || otp.trim().length() != 6) {
            throw new IllegalArgumentException("Mã OTP phải gồm 6 số.");
        }
        validateNewPassword(password, confirmPassword);
        try {
            Integer userId = resetRepository.findValidUserId(
                    email, otp.trim()
            );
            if (userId == null) {
                throw new IllegalArgumentException(
                        "OTP không đúng hoặc đã hết hạn."
                );
            }
            boolean updated = userRepository.updatePassword(
                    userId, PasswordUtil.hash(password)
            );
            if (updated) {
                resetRepository.markUsed(userId, otp.trim());
            }
            return updated;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changePassword(int userId, String currentPassword,
            String newPassword, String confirmPassword) {
        if (ValidationUtil.isBlank(currentPassword)) {
            throw new IllegalArgumentException(
                    "Vui lòng nhập mật khẩu hiện tại."
            );
        }
        validateNewPassword(newPassword, confirmPassword);
        try {
            User user = userRepository.findById(userId);
            if (user == null) {
                return false;
            }
            if (!PasswordUtil.verify(currentPassword, user.getPasswordHash())) {
                throw new IllegalArgumentException(
                        "Mật khẩu hiện tại không đúng."
                );
            }
            return userRepository.updatePassword(
                    userId, PasswordUtil.hash(newPassword)
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private void validateNewPassword(String password, String confirmPassword) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(
                    "Mật khẩu mới phải có ít nhất 6 ký tự."
            );
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException(
                    "Xác nhận mật khẩu không khớp."
            );
        }
    }
}
