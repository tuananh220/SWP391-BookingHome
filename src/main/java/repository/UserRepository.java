/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import dal.DBContext;
import entity.User;
import interfaces.IUserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 *
 * @author Admin
 */
public class UserRepository extends DBContext implements IUserRepository {

    public UserRepository() {
        super();
    }

    @Override  
    public User findByEmail(String email ) throws SQLException {
        String sql = "SELECT u.UserID, u.RoleID, r.RoleName, u.FullName, "
                + "u.Email, u.PasswordHash, u.PhoneNumber, u.AvatarURL, "
                + "u.Address, u.Status, u.CreatedAt, u.UpdatedAt "
                + "FROM Users u "
                + "INNER JOIN Roles r ON r.RoleID = u.RoleID "
                + "WHERE LOWER(u.Email) = LOWER(?)"
                ;

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        }
        return null;
    }
    
    @Override
    public User findById(int userId) throws SQLException {
        String sql = "SELECT u.UserID, u.RoleID, r.RoleName, u.FullName, "
                + "u.Email, u.PasswordHash, u.PhoneNumber, u.AvatarURL, "
                + "u.Address, u.Status, u.CreatedAt, u.UpdatedAt "
                + "FROM Users u "
                + "INNER JOIN Roles r ON r.RoleID = u.RoleID "
                + "WHERE u.UserID = ?";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM Users WHERE LOWER(Email) = LOWER(?)";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public int createUser(User user, String roleName) throws SQLException {
        String sql = "INSERT INTO Users "
                + "(RoleID, FullName, Email, PasswordHash, PhoneNumber, Status) "
                + "VALUES ((SELECT RoleID FROM Roles "
                + "WHERE RoleName = ?), ?, ?, ?, ?, 'Active')";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, roleName);
            statement.setString(2, user.getFullName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPasswordHash());
            statement.setString(5, user.getPhoneNumber());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return 0;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public boolean updateProfile(User user) throws SQLException {
        String sql = "UPDATE Users SET FullName = ?, PhoneNumber = ?, "
                + "Address = ?, AvatarURL = ?, UpdatedAt = SYSDATETIME() "
                + "WHERE UserID = ?";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getFullName());
            statement.setString(2, user.getPhoneNumber());
            statement.setString(3, user.getAddress());
            statement.setString(4, user.getAvatarUrl());
            statement.setInt(5, user.getUserId());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updatePassword(int userId, String passwordHash)
            throws SQLException {
        String sql = "UPDATE Users SET PasswordHash = ?, "
                + "UpdatedAt = SYSDATETIME() WHERE UserID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordHash);
            statement.setInt(2, userId);
            return statement.executeUpdate() > 0;
        }
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
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
}
