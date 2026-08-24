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
import entity.Payment;
import interfaces.IPaymentRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PaymentRepository extends DBContext
        implements IPaymentRepository {

    public PaymentRepository() {
        super();
    }

    @Override
    public Payment findLatestByBookingAndCustomer(int bookingId,
            int customerId)
            throws SQLException {
        String sql = "SELECT TOP 1 p.PaymentID, p.BookingID, "
                + "p.PaymentMethodID, p.TransactionID, p.PaymentType, "
                + "p.Amount, p.PaymentStatus, p.PaidAt, p.CreatedAt, "
                + "pm.MethodCode, pm.MethodName, pm.IsOnline "
                + "FROM Payments p "
                + "INNER JOIN Bookings b ON b.BookingID = p.BookingID "
                + "INNER JOIN PaymentMethods pm "
                + "ON pm.PaymentMethodID = p.PaymentMethodID "
                + "WHERE p.BookingID = ? AND b.CustomerID = ? "
                + "ORDER BY p.PaymentID DESC";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            statement.setInt(2, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapPayment(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public boolean completeOnlinePayment(int paymentId, int bookingId,
            int customerId,
            String transactionId)
            throws SQLException {
        ensureConnection();
        boolean oldAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);

            String paymentType = null;
            String typeSql = "SELECT p.PaymentType FROM Payments p "
                    + "INNER JOIN PaymentMethods pm "
                    + "ON pm.PaymentMethodID = p.PaymentMethodID "
                    + "INNER JOIN Bookings b ON b.BookingID = p.BookingID "
                    + "WHERE p.PaymentID = ? AND p.BookingID = ? "
                    + "AND b.CustomerID = ? AND p.PaymentStatus = 'Pending' "
                    + "AND pm.IsOnline = 1";
            try (PreparedStatement statement
                    = connection.prepareStatement(typeSql)) {
                statement.setInt(1, paymentId);
                statement.setInt(2, bookingId);
                statement.setInt(3, customerId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        paymentType = resultSet.getString("PaymentType");
                    }
                }
            }
            if (paymentType == null) {
                connection.rollback();
                return false;
            }

            String paymentSql = "UPDATE p SET p.PaymentStatus = 'Completed', "
                    + "p.TransactionID = ?, p.PaidAt = SYSDATETIME() "
                    + "FROM Payments p "
                    + "INNER JOIN PaymentMethods pm "
                    + "ON pm.PaymentMethodID = p.PaymentMethodID "
                    + "INNER JOIN Bookings b ON b.BookingID = p.BookingID "
                    + "WHERE p.PaymentID = ? AND p.BookingID = ? "
                    + "AND b.CustomerID = ? AND p.PaymentStatus = 'Pending' "
                    + "AND pm.IsOnline = 1 "
                    + "AND ((p.PaymentType = 'Booking' "
                    + "AND b.BookingStatus = 'Pending') "
                    + "OR (p.PaymentType = 'Extension' "
                    + "AND b.BookingStatus = 'Confirmed'))";

            int affectedRows;
            try (PreparedStatement statement
                    = connection.prepareStatement(paymentSql)) {
                statement.setString(1, transactionId);
                statement.setInt(2, paymentId);
                statement.setInt(3, bookingId);
                statement.setInt(4, customerId);
                affectedRows = statement.executeUpdate();
            }

            if (affectedRows == 0) {
                connection.rollback();
                return false;
            }

            if ("Booking".equals(paymentType)) {
                String bookingSql = "UPDATE Bookings SET "
                        + "BookingStatus = 'Confirmed', "
                        + "ConfirmedAt = SYSDATETIME(), "
                        + "UpdatedAt = SYSDATETIME() "
                        + "WHERE BookingID = ? AND CustomerID = ? "
                        + "AND BookingStatus = 'Pending'";
                try (PreparedStatement statement
                        = connection.prepareStatement(bookingSql)) {
                    statement.setInt(1, bookingId);
                    statement.setInt(2, customerId);
                    if (statement.executeUpdate() == 0) {
                        connection.rollback();
                        return false;
                    }
                }
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

    private Payment mapPayment(ResultSet resultSet) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(resultSet.getInt("PaymentID"));
        payment.setBookingId(resultSet.getInt("BookingID"));
        payment.setPaymentMethodId(resultSet.getInt("PaymentMethodID"));
        payment.setPaymentMethodCode(resultSet.getString("MethodCode"));
        payment.setPaymentMethodName(resultSet.getString("MethodName"));
        payment.setOnline(resultSet.getBoolean("IsOnline"));
        payment.setTransactionId(resultSet.getString("TransactionID"));
        payment.setPaymentType(resultSet.getString("PaymentType"));
        payment.setAmount(resultSet.getBigDecimal("Amount"));
        payment.setPaymentStatus(resultSet.getString("PaymentStatus"));

        Timestamp paidAt = resultSet.getTimestamp("PaidAt");
        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        if (paidAt != null) {
            payment.setPaidAt(paidAt.toLocalDateTime());
        }
        if (createdAt != null) {
            payment.setCreatedAt(createdAt.toLocalDateTime());
        }
        return payment;
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
