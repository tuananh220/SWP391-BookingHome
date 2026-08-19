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
import entity.Booking;
import interfaces.IHostBookingRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class HostBookingRepository extends DBContext
        implements IHostBookingRepository {

    public HostBookingRepository() {
        super();
    }

    @Override
    public List<Booking> findByHostId(int hostId, String status)
            throws SQLException {
        List<Booking> bookings = new ArrayList<Booking>();
        StringBuilder sql = new StringBuilder(hostBookingSelect());
        sql.append(" WHERE h.HostID = ? ");
        if (status != null) {
            sql.append("AND b.BookingStatus = ? ");
        }
        sql.append("ORDER BY b.CreatedAt DESC");

        ensureConnection();
        try (PreparedStatement statement
                = connection.prepareStatement(sql.toString())) {
            statement.setInt(1, hostId);
            if (status != null) {
                statement.setString(2, status);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bookings.add(mapBooking(resultSet));
                }
            }
        }
        return bookings;
    }

    @Override
    public Booking findByIdAndHostId(int bookingId, int hostId)
            throws SQLException {
        String sql = hostBookingSelect()
                + " WHERE b.BookingID = ? AND h.HostID = ?";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            statement.setInt(2, hostId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapBooking(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public boolean confirmBooking(int bookingId, int hostId)
            throws SQLException {
        ensureConnection();
        boolean oldAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);
            Integer customerId = findProcessableCustomer(
                    bookingId, hostId, true
            );
            if (customerId == null) {
                connection.rollback();
                return false;
            }

            String sql = "UPDATE Bookings SET BookingStatus = 'Confirmed', "
                    + "ConfirmedAt = SYSDATETIME(), "
                    + "UpdatedAt = SYSDATETIME() WHERE BookingID = ? "
                    + "AND BookingStatus = 'Pending'";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, bookingId);
                if (statement.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            createNotification(
                    customerId,
                    hostId,
                    bookingId,
                    "Booking đã được xác nhận",
                    "Chủ homestay đã xác nhận yêu cầu đặt phòng của bạn."
            );
            connection.commit();
            return true;
        } catch (SQLException exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    @Override
    public boolean rejectBooking(int bookingId, int hostId, String reason)
            throws SQLException {
        ensureConnection();
        boolean oldAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);
            Integer customerId = findProcessableCustomer(
                    bookingId, hostId, true
            );
            if (customerId == null) {
                connection.rollback();
                return false;
            }

            Integer voucherId = findVoucherId(bookingId);
            String updateSql = "UPDATE Bookings SET "
                    + "BookingStatus = 'Rejected', RejectReason = ?, "
                    + "UpdatedAt = SYSDATETIME() "
                    + "WHERE BookingID = ? AND BookingStatus = 'Pending'";
            try (PreparedStatement statement
                    = connection.prepareStatement(updateSql)) {
                statement.setString(1, reason);
                statement.setInt(2, bookingId);
                if (statement.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            String nightSql = "UPDATE BookingNights SET IsActive = 0 "
                    + "WHERE BookingID = ?";
            try (PreparedStatement statement
                    = connection.prepareStatement(nightSql)) {
                statement.setInt(1, bookingId);
                statement.executeUpdate();
            }

            if (voucherId != null) {
                releaseVoucher(bookingId, voucherId);
            }

            String paymentSql = "UPDATE Payments SET PaymentStatus = 'Failed' "
                    + "WHERE BookingID = ? AND PaymentStatus = 'Pending'";
            try (PreparedStatement statement
                    = connection.prepareStatement(paymentSql)) {
                statement.setInt(1, bookingId);
                statement.executeUpdate();
            }

            createNotification(
                    customerId,
                    hostId,
                    bookingId,
                    "Booking đã bị từ chối",
                    "Chủ homestay đã từ chối yêu cầu đặt phòng. Lý do: "
                    + reason
            );
            connection.commit();
            return true;
        } catch (SQLException exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    @Override
    public boolean cancelBooking(int bookingId, int hostId, String reason,
            BigDecimal refundAmount) throws SQLException {
        ensureConnection();
        boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            Integer customerId = null;
            Integer voucherId = null;
            String lockSql = "SELECT b.CustomerID, b.VoucherID FROM Bookings b "
                    + "WITH (UPDLOCK, HOLDLOCK) "
                    + "INNER JOIN Homestays h ON h.HomestayID = b.HomestayID "
                    + "WHERE b.BookingID = ? AND h.HostID = ? "
                    + "AND b.BookingStatus IN ('Pending', 'Confirmed')";
            try (PreparedStatement statement
                    = connection.prepareStatement(lockSql)) {
                statement.setInt(1, bookingId);
                statement.setInt(2, hostId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        connection.rollback();
                        return false;
                    }
                    customerId = resultSet.getInt("CustomerID");
                    int value = resultSet.getInt("VoucherID");
                    if (!resultSet.wasNull()) {
                        voucherId = value;
                    }
                }
            }

            String updateSql = "UPDATE Bookings SET "
                    + "BookingStatus = 'Cancelled', CancelReason = ?, "
                    + "CancelledBy = 'Home Owner', CancelledAt = SYSDATETIME(), "
                    + "RefundAmount = ?, UpdatedAt = SYSDATETIME() "
                    + "WHERE BookingID = ?";
            try (PreparedStatement statement
                    = connection.prepareStatement(updateSql)) {
                statement.setString(1, reason);
                statement.setBigDecimal(2, refundAmount);
                statement.setInt(3, bookingId);
                statement.executeUpdate();
            }

            String nightSql = "UPDATE BookingNights SET IsActive = 0 "
                    + "WHERE BookingID = ?";
            try (PreparedStatement statement
                    = connection.prepareStatement(nightSql)) {
                statement.setInt(1, bookingId);
                statement.executeUpdate();
            }

            if (voucherId != null) {
                releaseVoucher(bookingId, voucherId);
            }

            if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
                createRefund(bookingId, refundAmount, reason);
            }

            createNotification(
                    customerId,
                    hostId,
                    bookingId,
                    "Booking đã bị chủ nhà hủy",
                    "Chủ homestay đã hủy booking. Lý do: " + reason
            );
            connection.commit();
            return true;
        } catch (SQLException exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    private void createRefund(int bookingId, BigDecimal refundAmount,
            String reason) throws SQLException {
        Integer paymentId = null;
        String findPayment = "SELECT TOP 1 PaymentID FROM Payments "
                + "WHERE BookingID = ? AND PaymentStatus = 'Completed' "
                + "ORDER BY PaymentID DESC";
        try (PreparedStatement statement
                = connection.prepareStatement(findPayment)) {
            statement.setInt(1, bookingId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    paymentId = resultSet.getInt("PaymentID");
                }
            }
        }
        if (paymentId == null) {
            return;
        }
        String refundSql = "INSERT INTO Refunds "
                + "(BookingID, PaymentID, Amount, Reason, RefundStatus, "
                + "RefundedAt) VALUES (?, ?, ?, ?, 'Completed', SYSDATETIME())";
        try (PreparedStatement statement
                = connection.prepareStatement(refundSql)) {
            statement.setInt(1, bookingId);
            statement.setInt(2, paymentId);
            statement.setBigDecimal(3, refundAmount);
            statement.setString(4, reason);
            statement.executeUpdate();
        }
        String paymentSql = "UPDATE Payments SET PaymentStatus = 'Refunded' "
                + "WHERE PaymentID = ?";
        try (PreparedStatement statement
                = connection.prepareStatement(paymentSql)) {
            statement.setInt(1, paymentId);
            statement.executeUpdate();
        }
    }

    private Integer findProcessableCustomer(int bookingId, int hostId,
            boolean lockRows)
            throws SQLException {
        String hint = lockRows ? " WITH (UPDLOCK, HOLDLOCK) " : " ";
        String sql = "SELECT b.CustomerID FROM Bookings b" + hint
                + "INNER JOIN Homestays h ON h.HomestayID = b.HomestayID "
                + "WHERE b.BookingID = ? AND h.HostID = ? "
                + "AND b.BookingStatus = 'Pending' "
                + "AND (b.TotalAmount = 0 OR EXISTS ("
                + "SELECT 1 FROM Payments p "
                + "INNER JOIN PaymentMethods pm "
                + "ON pm.PaymentMethodID = p.PaymentMethodID "
                + "WHERE p.BookingID = b.BookingID AND pm.IsOnline = 0))";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            statement.setInt(2, hostId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("CustomerID");
                }
            }
        }
        return null;
    }

    private Integer findVoucherId(int bookingId) throws SQLException {
        String sql = "SELECT VoucherID FROM Bookings WHERE BookingID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int voucherId = resultSet.getInt("VoucherID");
                    return resultSet.wasNull() ? null : voucherId;
                }
            }
        }
        return null;
    }

    private void releaseVoucher(int bookingId, int voucherId)
            throws SQLException {
        String usageSql = "UPDATE VoucherUsages SET UsageStatus = 'Released' "
                + "WHERE BookingID = ? AND UsageStatus <> 'Released'";
        int released;
        try (PreparedStatement statement
                = connection.prepareStatement(usageSql)) {
            statement.setInt(1, bookingId);
            released = statement.executeUpdate();
        }

        if (released > 0) {
            String voucherSql = "UPDATE Vouchers SET UsedCount = "
                    + "CASE WHEN UsedCount > 0 THEN UsedCount - 1 ELSE 0 END "
                    + "WHERE VoucherID = ?";
            try (PreparedStatement statement
                    = connection.prepareStatement(voucherSql)) {
                statement.setInt(1, voucherId);
                statement.executeUpdate();
            }
        }
    }

    private void createNotification(int userId, int createdById,
            int bookingId, String title,
            String message) throws SQLException {
        String sql = "INSERT INTO Notifications "
                + "(Title, Message, Type, RelatedID, CreatedByID) "
                + "VALUES (?, ?, 'Booking', ?, ?)";
        int notificationId;
        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, title);
            statement.setString(2, message);
            statement.setInt(3, bookingId);
            statement.setInt(4, createdById);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Không thể tạo thông báo.");
                }
                notificationId = keys.getInt(1);
            }
        }

        String recipientSql = "INSERT INTO UserNotifications "
                + "(NotificationID, UserID, IsRead) VALUES (?, ?, 0)";
        try (PreparedStatement statement
                = connection.prepareStatement(recipientSql)) {
            statement.setInt(1, notificationId);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    private String hostBookingSelect() {
        return "SELECT b.BookingID, b.CustomerID, b.HomestayID, "
                + "b.VoucherID, b.CancellationPolicyID, "
                + "b.CancellationPolicyName, b.FullRefundDaysSnapshot, "
                + "b.PartialRefundDaysSnapshot, "
                + "b.PartialRefundPercentSnapshot, b.CheckInDate, b.CheckOutDate, "
                + "b.TotalGuests, b.OriginalAmount, b.DiscountAmount, "
                + "b.TotalAmount, b.BookingStatus, b.Note, b.CancelReason, "
                + "b.RejectReason, b.RefundAmount, b.CreatedAt, "
                + "h.Title AS HomestayTitle, customer.FullName AS CustomerName, "
                + "customer.PhoneNumber AS CustomerPhone, "
                + "pay.PaymentStatus, pay.MethodName AS PaymentMethodName, "
                + "pay.IsOnline AS PaymentOnline "
                + "FROM Bookings b "
                + "INNER JOIN Homestays h ON h.HomestayID = b.HomestayID "
                + "INNER JOIN Users customer ON customer.UserID = b.CustomerID "
                + "OUTER APPLY (SELECT TOP 1 p.PaymentStatus, pm.MethodName, "
                + "pm.IsOnline FROM Payments p "
                + "INNER JOIN PaymentMethods pm "
                + "ON pm.PaymentMethodID = p.PaymentMethodID "
                + "WHERE p.BookingID = b.BookingID "
                + "ORDER BY p.PaymentID DESC) pay";
    }

    private Booking mapBooking(ResultSet resultSet) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(resultSet.getInt("BookingID"));
        booking.setCustomerId(resultSet.getInt("CustomerID"));
        booking.setHomestayId(resultSet.getInt("HomestayID"));
        booking.setCancellationPolicyName(
                resultSet.getString("CancellationPolicyName")
        );
        int fullDays = resultSet.getInt("FullRefundDaysSnapshot");
        if (!resultSet.wasNull()) {
            booking.setFullRefundDaysSnapshot(fullDays);
        }
        int partialDays = resultSet.getInt("PartialRefundDaysSnapshot");
        if (!resultSet.wasNull()) {
            booking.setPartialRefundDaysSnapshot(partialDays);
        }
        booking.setPartialRefundPercentSnapshot(
                resultSet.getBigDecimal("PartialRefundPercentSnapshot")
        );
        booking.setCheckInDate(
                resultSet.getDate("CheckInDate").toLocalDate()
        );
        booking.setCheckOutDate(
                resultSet.getDate("CheckOutDate").toLocalDate()
        );
        booking.setTotalGuests(resultSet.getInt("TotalGuests"));
        booking.setOriginalAmount(resultSet.getBigDecimal("OriginalAmount"));
        booking.setDiscountAmount(resultSet.getBigDecimal("DiscountAmount"));
        booking.setTotalAmount(resultSet.getBigDecimal("TotalAmount"));
        booking.setBookingStatus(resultSet.getString("BookingStatus"));
        booking.setNote(resultSet.getString("Note"));
        booking.setCancelReason(resultSet.getString("CancelReason"));
        booking.setRejectReason(resultSet.getString("RejectReason"));
        booking.setRefundAmount(resultSet.getBigDecimal("RefundAmount"));
        booking.setHomestayTitle(resultSet.getString("HomestayTitle"));
        booking.setCustomerName(resultSet.getString("CustomerName"));
        booking.setCustomerPhone(resultSet.getString("CustomerPhone"));
        booking.setPaymentStatus(resultSet.getString("PaymentStatus"));
        booking.setPaymentMethodName(
                resultSet.getString("PaymentMethodName")
        );
        booking.setPaymentOnline(resultSet.getBoolean("PaymentOnline"));

        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        if (createdAt != null) {
            booking.setCreatedAt(createdAt.toLocalDateTime());
        }
        return booking;
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
