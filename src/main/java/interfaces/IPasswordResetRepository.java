package interfaces;

import java.sql.SQLException;

public interface IPasswordResetRepository {

    int createToken(int userId, String otp) throws SQLException;

    Integer findValidUserId(String email, String otp) throws SQLException;

    boolean markUsed(int userId, String otp) throws SQLException;
}
