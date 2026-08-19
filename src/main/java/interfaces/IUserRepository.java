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
import java.sql.SQLException;


public interface IUserRepository {
    User findByEmail(String email) throws SQLException;

    User findById(int userId) throws SQLException;

    boolean existsByEmail(String email) throws SQLException;

    int createUser(User user, String roleName) throws SQLException;

    boolean updateProfile(User user) throws SQLException;

    boolean updatePassword(int userId, String passwordHash)
            throws SQLException;
}
