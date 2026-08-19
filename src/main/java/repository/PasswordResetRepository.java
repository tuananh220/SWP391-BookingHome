package repository;

import dal.DBContext;
import interfaces.IPasswordResetRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PasswordResetRepository extends DBContext
        implements IPasswordResetRepository {

    public PasswordResetRepository() {
        super();
    }

    @Override
    public int createToken(int userId, String otp) throws SQLException {
        ensureConnection();
        String disableOld = "UPDATE PasswordResetTokens SET IsUsed = 1 "
                + "WHERE UserID = ? AND IsUsed = 0";
        try (PreparedStatement statement
                = connection.prepareStatement(disableOld)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }

        String sql = "INSERT INTO PasswordResetTokens "
                + "(UserID, OTP, ExpiresAt) VALUES "
                + "(?, ?, DATEADD(MINUTE, 15, SYSDATETIME()))";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setString(2, otp);
            return statement.executeUpdate();
        }
    }

    @Override
    public Integer findValidUserId(String email, String otp)
            throws SQLException {
        String sql = "SELECT t.UserID FROM PasswordResetTokens t "
                + "INNER JOIN Users u ON u.UserID = t.UserID "
                + "WHERE LOWER(u.Email) = LOWER(?) AND t.OTP = ? "
                + "AND t.IsUsed = 0 AND t.ExpiresAt > SYSDATETIME() "
                + "AND u.Status = 'Active'";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, otp);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("UserID");
                }
            }
        }
        return null;
    }

    @Override
    public boolean markUsed(int userId, String otp) throws SQLException {
        String sql = "UPDATE PasswordResetTokens SET IsUsed = 1 "
                + "WHERE UserID = ? AND OTP = ? AND IsUsed = 0";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setString(2, otp);
            return statement.executeUpdate() > 0;
        }
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
