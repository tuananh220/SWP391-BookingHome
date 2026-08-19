/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

/**
 *
 * @author Admin
 */


import entity.User;


public interface IAuthService {
    User login(String email, String password);

    boolean register(String fullName, String email,
                     String password, String confirmPassword,
                     String phoneNumber, String roleName);

    String createResetOtp(String email);

    boolean resetPassword(String email, String otp,
            String password, String confirmPassword);

    boolean changePassword(int userId, String currentPassword,
            String newPassword, String confirmPassword);
}
