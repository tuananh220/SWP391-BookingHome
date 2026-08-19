/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

/**
 *
 * @author Admin
 */
import entity.AdminDashboardSummary;
import entity.Role;
import entity.User;
import java.sql.SQLException;
import java.util.List;

public interface IAdminUserRepository {

    AdminDashboardSummary getDashboardSummary() throws SQLException;

    List<User> findUsers(String keyword, String roleName, String status)
            throws SQLException;

    User findUserById(int userId) throws SQLException;

    List<Role> findRoles() throws SQLException;

    boolean updateUser(User user) throws SQLException;

    boolean hasRelatedData(int userId) throws SQLException;

    boolean deleteUser(int userId) throws SQLException;
}
