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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
            } else if ("Extension".equals(paymentType)) {
                if (!applyExtensionAfterPayment(bookingId, paymentId)) {
                    connection.rollback();
                    return false;
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

    private boolean applyExtensionAfterPayment(int bookingId, int paymentId)
            throws SQLException {
        String requestSql = "SELECT s.RequestID, b.HomestayID, "
                + "s.OriginalCheckOutDate, s.RequestedCheckOutDate, "
                + "s.ExtraAmount, h.PricePerNight "
                + "FROM StayChangeRequests s INNER JOIN Payments p "
                + "ON p.StayChangeRequestID = s.RequestID "
                + "INNER JOIN Bookings b ON b.BookingID = s.BookingID "
                + "INNER JOIN Homestays h ON h.HomestayID = b.HomestayID "
                + "WHERE p.PaymentID = ? AND p.BookingID = ? "
                + "AND p.PaymentType = 'Extension' "
                + "AND s.RequestType = 'Extension' AND s.Status = 'Accepted'";
        int homestayId;
        LocalDate originalCheckOut;
        LocalDate requestedCheckOut;
        BigDecimal extraAmount;
        BigDecimal pricePerNight;
        try (PreparedStatement statement = connection.prepareStatement(requestSql)) {
            statement.setInt(1, paymentId);
            statement.setInt(2, bookingId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return false;
                }
                homestayId = resultSet.getInt("HomestayID");
                originalCheckOut = resultSet.getDate(
                        "OriginalCheckOutDate").toLocalDate();
                requestedCheckOut = resultSet.getDate(
                        "RequestedCheckOutDate").toLocalDate();
                extraAmount = resultSet.getBigDecimal("ExtraAmount");
                pricePerNight = resultSet.getBigDecimal("PricePerNight");
            }
        }

        String conflictSql = "SELECT COUNT(*) AS Total FROM BookingNights "
                + "WITH (UPDLOCK, HOLDLOCK) WHERE HomestayID = ? "
                + "AND IsActive = 1 AND StayDate >= ? AND StayDate < ?";
        try (PreparedStatement statement = connection.prepareStatement(conflictSql)) {
            statement.setInt(1, homestayId);
            statement.setDate(2, Date.valueOf(originalCheckOut));
            statement.setDate(3, Date.valueOf(requestedCheckOut));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt("Total") > 0) {
                    return false;
                }
            }
        }

        Map<LocalDate, BigDecimal> customPrices = findCustomPrices(
                homestayId, originalCheckOut, requestedCheckOut
        );
        String nightSql = "INSERT INTO BookingNights "
                + "(BookingID, HomestayID, StayDate, NightPrice, IsActive) "
                + "VALUES (?, ?, ?, ?, 1)";
        try (PreparedStatement statement = connection.prepareStatement(nightSql)) {
            LocalDate stayDate = originalCheckOut;
            while (stayDate.isBefore(requestedCheckOut)) {
                statement.setInt(1, bookingId);
                statement.setInt(2, homestayId);
                statement.setDate(3, Date.valueOf(stayDate));
                statement.setBigDecimal(4, customPrices.getOrDefault(
                        stayDate, pricePerNight));
                statement.addBatch();
                stayDate = stayDate.plusDays(1);
            }
            statement.executeBatch();
        }

        String bookingSql = "UPDATE Bookings SET CheckOutDate = ?, "
                + "OriginalAmount = OriginalAmount + ?, "
                + "TotalAmount = TotalAmount + ?, UpdatedAt = SYSDATETIME() "
                + "WHERE BookingID = ? AND CheckOutDate < ?";
        try (PreparedStatement statement = connection.prepareStatement(bookingSql)) {
            statement.setDate(1, Date.valueOf(requestedCheckOut));
            statement.setBigDecimal(2, extraAmount);
            statement.setBigDecimal(3, extraAmount);
            statement.setInt(4, bookingId);
            statement.setDate(5, Date.valueOf(requestedCheckOut));
            return statement.executeUpdate() > 0;
        }
    }

    private Map<LocalDate, BigDecimal> findCustomPrices(int homestayId,
            LocalDate fromDate, LocalDate toDate) throws SQLException {
        Map<LocalDate, BigDecimal> prices = new HashMap<LocalDate, BigDecimal>();
        String sql = "SELECT ScheduleDate, CustomPrice FROM HomestaySchedules "
                + "WHERE HomestayID = ? AND IsAvailable = 1 "
                + "AND ScheduleDate >= ? AND ScheduleDate < ? "
                + "AND CustomPrice IS NOT NULL";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            statement.setDate(2, Date.valueOf(fromDate));
            statement.setDate(3, Date.valueOf(toDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    prices.put(
                            resultSet.getDate("ScheduleDate").toLocalDate(),
                            resultSet.getBigDecimal("CustomPrice")
                    );
                }
            }
        }
        return prices;
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
