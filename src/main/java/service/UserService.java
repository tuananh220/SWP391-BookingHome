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
import interfaces.IUserRepository;
import repository.UserRepository;
import ultis.ValidationUtil;

import java.sql.SQLException;

public class UserService {
    private final IUserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public User updateProfile(int userId, String fullName,
                              String phoneNumber, String address,
                              String avatarUrl) {
        fullName = fullName == null ? null : fullName.trim();

        if (ValidationUtil.isBlank(fullName) || fullName.length() > 100) {
            throw new IllegalArgumentException("Họ tên không hợp lệ.");
        }

        if (phoneNumber != null && phoneNumber.trim().length() > 10) {
            throw new IllegalArgumentException(
                    "Số điện thoại tối đa 10 ký tự."
            );
        }

        try {
            User user = userRepository.findById(userId);
            if (user == null) {
                return null;
            }

            user.setFullName(fullName);
            user.setPhoneNumber(emptyToNull(phoneNumber));
            user.setAddress(emptyToNull(address));
            user.setAvatarUrl(emptyToNull(avatarUrl));

            if (!userRepository.updateProfile(user)) {
                return null;
            }

            user.setPasswordHash(null);
            return user;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private String emptyToNull(String value) {
        return ValidationUtil.isBlank(value) ? null : value.trim();
    }
}
