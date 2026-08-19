/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

/**
 *
 * @author Admin
 */
import dal.DBContext;
import entity.AdminDashboardSummary;
import entity.Role;
import entity.User;
import interfaces.IAdminUserRepository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AdminUserRepository extends DBContext
        implements IAdminUserRepository {

    public AdminUserRepository() {
        super();
    }

    @Override
    public AdminDashboardSummary getDashboardSummary() throws SQLException {
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM Users u INNER JOIN Roles r "
                + "ON r.RoleID = u.RoleID WHERE r.RoleName = N'Customer') AS TotalCustomers, "
                + "(SELECT COUNT(*) FROM Users u INNER JOIN Roles r "
                + "ON r.RoleID = u.RoleID WHERE r.RoleName = N'Home Owner') AS TotalOwners, "
                + "(SELECT COUNT(*) FROM Homestays) AS TotalHomestays, "
                + "(SELECT COUNT(*) FROM Homestays WHERE Status = 'Active') AS ActiveHomestays, "
                + "(SELECT COUNT(*) FROM Homestays WHERE Status = 'Pending') AS PendingHomestays, "
                + "(SELECT COUNT(*) FROM Bookings) AS TotalBookings, "
                + "(SELECT COUNT(*) FROM Bookings WHERE BookingStatus = 'Pending') AS PendingBookings, "
                + "(SELECT COALESCE(SUM(Amount), 0) FROM Payments "
                + "WHERE PaymentStatus = 'Completed') AS TotalRevenue";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            AdminDashboardSummary summary = new AdminDashboardSummary();
            if (resultSet.next()) {
                summary.setTotalCustomers(resultSet.getInt("TotalCustomers"));
                summary.setTotalOwners(resultSet.getInt("TotalOwners"));
                summary.setTotalHomestays(resultSet.getInt("TotalHomestays"));
                summary.setActiveHomestays(resultSet.getInt("ActiveHomestays"));
                summary.setPendingHomestays(resultSet.getInt("PendingHomestays"));
                summary.setTotalBookings(resultSet.getInt("TotalBookings"));
                summary.setPendingBookings(resultSet.getInt("PendingBookings"));
                BigDecimal revenue = resultSet.getBigDecimal("TotalRevenue");
                summary.setTotalRevenue(
                        revenue == null ? BigDecimal.ZERO : revenue
                );
            }
            return summary;
        }
    }

    @Override
    public List<User> findUsers(String keyword, String roleName, String status)
            throws SQLException {
        List<User> users = new ArrayList<User>();
        List<String> parameters = new ArrayList<String>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT u.UserID, u.RoleID, r.RoleName, u.FullName, ");
        sql.append("u.Email, u.PasswordHash, u.PhoneNumber, u.AvatarURL, ");
        sql.append("u.Address, u.Status, u.CreatedAt, u.UpdatedAt ");
        sql.append("FROM Users u INNER JOIN Roles r ON r.RoleID = u.RoleID ");
        sql.append("WHERE 1 = 1 ");

        if (!isBlank(keyword)) {
            sql.append("AND (u.FullName LIKE ? OR u.Email LIKE ? ");
            sql.append("OR u.PhoneNumber LIKE ?) ");
            String value = "%" + keyword.trim() + "%";
            parameters.add(value);
            parameters.add(value);
            parameters.add(value);
        }
        if (!isBlank(roleName)) {
            sql.append("AND r.RoleName = ? ");
            parameters.add(roleName);
        }
        if (!isBlank(status)) {
            sql.append("AND u.Status = ? ");
            parameters.add(status);
        }
        sql.append("ORDER BY u.CreatedAt DESC");

        ensureConnection();
        try (PreparedStatement statement
                = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setString(i + 1, parameters.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mapUser(resultSet));
                }
            }
        }
        return users;
    }

    @Override
    public User findUserById(int userId) throws SQLException {
        String sql = "SELECT u.UserID, u.RoleID, r.RoleName, u.FullName, "
                + "u.Email, u.PasswordHash, u.PhoneNumber, u.AvatarURL, "
                + "u.Address, u.Status, u.CreatedAt, u.UpdatedAt "
                + "FROM Users u INNER JOIN Roles r ON r.RoleID = u.RoleID "
                + "WHERE u.UserID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapUser(resultSet) : null;
            }
        }
    }

    @Override
    public List<Role> findRoles() throws SQLException {
        List<Role> roles = new ArrayList<Role>();
        String sql = "SELECT RoleID, RoleName FROM Roles ORDER BY RoleID";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                roles.add(new Role(
                        resultSet.getInt("RoleID"),
                        resultSet.getString("RoleName")
                ));
            }
        }
        return roles;
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET RoleID = ?, FullName = ?, Email = ?, "
                + "PhoneNumber = ?, Address = ?, Status = ?, "
                + "UpdatedAt = SYSDATETIME() WHERE UserID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, user.getRoleId());
            statement.setString(2, user.getFullName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPhoneNumber());
            statement.setString(5, user.getAddress());
            statement.setString(6, user.getStatus());
            statement.setInt(7, user.getUserId());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean hasRelatedData(int userId) throws SQLException {
        String sql = "SELECT CASE WHEN "
                + "EXISTS (SELECT 1 FROM Homestays WHERE HostID = ?) "
                + "OR EXISTS (SELECT 1 FROM Bookings WHERE CustomerID = ?) "
                + "OR EXISTS (SELECT 1 FROM Reviews WHERE CustomerID = ?) "
                + "OR EXISTS (SELECT 1 FROM Vouchers WHERE CreatedByID = ?) "
                + "THEN 1 ELSE 0 END AS HasData";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            statement.setInt(3, userId);
            statement.setInt(4, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getBoolean("HasData");
            }
        }
    }

    @Override
    public boolean deleteUser(int userId) throws SQLException {
        ensureConnection();
        boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            executeDelete("DELETE FROM PasswordResetTokens WHERE UserID = ?", userId);
            executeDelete("DELETE FROM UserNotifications WHERE UserID = ?", userId);
            executeDelete("DELETE FROM FavoriteHomestays WHERE CustomerID = ?", userId);
            executeDelete("DELETE FROM Blogs WHERE AuthorID = ?", userId);
            executeDelete("DELETE FROM ReviewReports WHERE ReporterID = ?", userId);
            String clearPolicy = "UPDATE CancellationPolicies SET CreatedByID = NULL "
                    + "WHERE CreatedByID = ?";
            try (PreparedStatement statement
                    = connection.prepareStatement(clearPolicy)) {
                statement.setInt(1, userId);
                statement.executeUpdate();
            }
            String clearNoti = "UPDATE Notifications SET CreatedByID = NULL "
                    + "WHERE CreatedByID = ?";
            try (PreparedStatement statement
                    = connection.prepareStatement(clearNoti)) {
                statement.setInt(1, userId);
                statement.executeUpdate();
            }
            String sql = "DELETE FROM Users WHERE UserID = ?";
            boolean deleted;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                deleted = statement.executeUpdate() > 0;
            }
            if (!deleted) {
                connection.rollback();
                return false;
            }
            connection.commit();
            return true;
        } catch (SQLException exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    private void executeDelete(String sql, int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("UserID"));
        user.setRoleId(resultSet.getInt("RoleID"));
        user.setRoleName(resultSet.getString("RoleName"));
        user.setFullName(resultSet.getString("FullName"));
        user.setEmail(resultSet.getString("Email"));
        user.setPasswordHash(resultSet.getString("PasswordHash"));
        user.setPhoneNumber(resultSet.getString("PhoneNumber"));
        user.setAvatarUrl(resultSet.getString("AvatarURL"));
        user.setAddress(resultSet.getString("Address"));
        user.setStatus(resultSet.getString("Status"));
        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        Timestamp updatedAt = resultSet.getTimestamp("UpdatedAt");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return user;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
