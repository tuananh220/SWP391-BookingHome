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
import entity.BookingNight;
import entity.StayChangeRequest;
import interfaces.IStayChangeRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StayChangeRepository extends DBContext
        implements IStayChangeRepository {

    public StayChangeRepository() {
        super();
    }

    @Override
    public Booking findEligibleBooking(int bookingId, int customerId)
            throws SQLException {
        String sql = "SELECT b.BookingID, b.CustomerID, b.HomestayID, "
                + "b.CheckInDate, b.CheckOutDate, b.TotalGuests, "
                + "b.TotalAmount, b.BookingStatus, "
                + "b.PartialRefundPercentSnapshot, h.Title AS HomestayTitle "
                + "FROM Bookings b INNER JOIN Homestays h "
                + "ON h.HomestayID = b.HomestayID "
                + "WHERE b.BookingID = ? AND b.CustomerID = ? "
                + "AND b.BookingStatus = 'Confirmed' "
                + "AND NOT EXISTS (SELECT 1 FROM StayChangeRequests s "
                + "WHERE s.BookingID = b.BookingID AND s.Status = 'Pending')";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            statement.setInt(2, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Booking booking = new Booking();
                    booking.setBookingId(resultSet.getInt("BookingID"));
                    booking.setCustomerId(resultSet.getInt("CustomerID"));
                    booking.setHomestayId(resultSet.getInt("HomestayID"));
                    booking.setCheckInDate(resultSet.getDate("CheckInDate").toLocalDate());
                    booking.setCheckOutDate(resultSet.getDate("CheckOutDate").toLocalDate());
                    booking.setTotalGuests(resultSet.getInt("TotalGuests"));
                    booking.setTotalAmount(resultSet.getBigDecimal("TotalAmount"));
                    booking.setBookingStatus(resultSet.getString("BookingStatus"));
                    booking.setPartialRefundPercentSnapshot(
                            resultSet.getBigDecimal("PartialRefundPercentSnapshot")
                    );
                    booking.setHomestayTitle(resultSet.getString("HomestayTitle"));
                    return booking;
                }
            }
        }
        return null;
    }

    @Override
    public BigDecimal sumNightPrices(int bookingId, LocalDate fromDate,
            LocalDate toDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(NightPrice), 0) AS Total "
                + "FROM BookingNights WHERE BookingID = ? AND IsActive = 1 "
                + "AND StayDate >= ? AND StayDate < ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            statement.setDate(2, Date.valueOf(fromDate));
            statement.setDate(3, Date.valueOf(toDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? resultSet.getBigDecimal("Total") : BigDecimal.ZERO;
            }
        }
    }

    @Override
    public int create(StayChangeRequest request) throws SQLException {
        String sql = "INSERT INTO StayChangeRequests "
                + "(BookingID, CustomerID, RequestType, OriginalCheckOutDate, "
                + "RequestedCheckOutDate, ExtraAmount, RefundAmount, Status, "
                + "CustomerNote) VALUES (?, ?, ?, ?, ?, ?, ?, 'Pending', ?)";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, request.getBookingId());
            statement.setInt(2, request.getCustomerId());
            statement.setString(3, request.getRequestType());
            statement.setDate(4, Date.valueOf(request.getOriginalCheckOutDate()));
            statement.setDate(5, Date.valueOf(request.getRequestedCheckOutDate()));
            statement.setBigDecimal(6, request.getExtraAmount());
            statement.setBigDecimal(7, request.getRefundAmount());
            statement.setString(8, request.getCustomerNote());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    @Override
    public List<StayChangeRequest> findByCustomerId(int customerId)
            throws SQLException {
        return queryRequests(
                requestSelect() + " WHERE s.CustomerID = ? ORDER BY s.CreatedAt DESC",
                customerId, null
        );
    }

    @Override
    public List<StayChangeRequest> findByHostId(int hostId, String status)
            throws SQLException {
        String sql = requestSelect() + " WHERE h.HostID = ? ";
        if (status != null) {
            sql += "AND s.Status = ? ";
        }
        sql += "ORDER BY s.CreatedAt DESC";
        return queryRequests(sql, hostId, status);
    }

    @Override
    public StayChangeRequest findByIdAndHostId(int requestId, int hostId)
            throws SQLException {
        String sql = requestSelect()
                + " WHERE s.RequestID = ? AND h.HostID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, requestId);
            statement.setInt(2, hostId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRequest(resultSet) : null;
            }
        }
    }

    @Override
    public boolean cancelByCustomer(int requestId, int customerId)
            throws SQLException {
        String sql = "UPDATE StayChangeRequests SET Status = 'Cancelled', "
                + "UpdatedAt = SYSDATETIME() WHERE RequestID = ? "
                + "AND CustomerID = ? AND Status = 'Pending'";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, requestId);
            statement.setInt(2, customerId);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean reject(int requestId, int hostId, String responseNote)
            throws SQLException {
        ensureConnection();
        boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            RequestLock lock = lockRequest(requestId, hostId);
            if (lock == null) {
                connection.rollback();
                return false;
            }
            String sql = "UPDATE StayChangeRequests SET Status = 'Rejected', "
                    + "ResponseNote = ?, RespondedBy = ?, "
                    + "RespondedAt = SYSDATETIME(), UpdatedAt = SYSDATETIME() "
                    + "WHERE RequestID = ? AND Status = 'Pending'";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, responseNote);
                statement.setInt(2, hostId);
                statement.setInt(3, requestId);
                statement.executeUpdate();
            }
            createNotification(lock.customerId, hostId, lock.bookingId,
                    "Yêu cầu thay đổi lưu trú bị từ chối",
                    "Chủ nhà đã từ chối yêu cầu. Lý do: " + responseNote);
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
    public boolean accept(int requestId, int hostId,
            List<BookingNight> extensionNights)
            throws SQLException {
        ensureConnection();
        boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            RequestLock lock = lockRequest(requestId, hostId);
            if (lock == null) {
                connection.rollback();
                return false;
            }

            if ("Extension".equals(lock.requestType)) {
                acceptExtension(lock, extensionNights);
            } else {
                acceptEarlyCheckout(lock);
            }

            String updateRequest = "UPDATE StayChangeRequests SET "
                    + "Status = 'Accepted', RespondedBy = ?, "
                    + "RespondedAt = SYSDATETIME(), UpdatedAt = SYSDATETIME() "
                    + "WHERE RequestID = ? AND Status = 'Pending'";
            try (PreparedStatement statement
                    = connection.prepareStatement(updateRequest)) {
                statement.setInt(1, hostId);
                statement.setInt(2, requestId);
                statement.executeUpdate();
            }
            createNotification(lock.customerId, hostId, lock.bookingId,
                    "Yêu cầu thay đổi lưu trú được chấp nhận",
                    "Chủ nhà đã chấp nhận yêu cầu "
                    + ("Extension".equals(lock.requestType)
                    ? "gia hạn." : "trả phòng sớm."));
            connection.commit();
            return true;
        } catch (SQLException exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    private void acceptExtension(RequestLock lock,
            List<BookingNight> nights)
            throws SQLException {
        String conflictSql = "SELECT COUNT(*) AS Total FROM BookingNights "
                + "WITH (UPDLOCK, HOLDLOCK) WHERE HomestayID = ? "
                + "AND IsActive = 1 AND StayDate >= ? AND StayDate < ?";
        try (PreparedStatement statement = connection.prepareStatement(conflictSql)) {
            statement.setInt(1, lock.homestayId);
            statement.setDate(2, Date.valueOf(lock.originalDate));
            statement.setDate(3, Date.valueOf(lock.requestedDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt("Total") > 0) {
                    throw new SQLException("Các ngày gia hạn không còn trống.");
                }
            }
        }

        String insertNight = "INSERT INTO BookingNights "
                + "(BookingID, HomestayID, StayDate, NightPrice, IsActive) "
                + "VALUES (?, ?, ?, ?, 1)";
        try (PreparedStatement statement = connection.prepareStatement(insertNight)) {
            for (BookingNight night : nights) {
                statement.setInt(1, lock.bookingId);
                statement.setInt(2, lock.homestayId);
                statement.setDate(3, Date.valueOf(night.getStayDate()));
                statement.setBigDecimal(4, night.getNightPrice());
                statement.addBatch();
            }
            statement.executeBatch();
        }

        String bookingSql = "UPDATE Bookings SET CheckOutDate = ?, "
                + "OriginalAmount = OriginalAmount + ?, "
                + "TotalAmount = TotalAmount + ?, UpdatedAt = SYSDATETIME() "
                + "WHERE BookingID = ?";
        try (PreparedStatement statement = connection.prepareStatement(bookingSql)) {
            statement.setDate(1, Date.valueOf(lock.requestedDate));
            statement.setBigDecimal(2, lock.extraAmount);
            statement.setBigDecimal(3, lock.extraAmount);
            statement.setInt(4, lock.bookingId);
            statement.executeUpdate();
        }

        Integer methodId = findLatestPaymentMethod(lock.bookingId);
        if (methodId != null && lock.extraAmount.compareTo(BigDecimal.ZERO) > 0) {
            String paymentSql = "INSERT INTO Payments "
                    + "(BookingID, PaymentMethodID, PaymentType, Amount, "
                    + "PaymentStatus) VALUES (?, ?, 'Extension', ?, 'Pending')";
            try (PreparedStatement statement = connection.prepareStatement(paymentSql)) {
                statement.setInt(1, lock.bookingId);
                statement.setInt(2, methodId);
                statement.setBigDecimal(3, lock.extraAmount);
                statement.executeUpdate();
            }
        }
    }

    private void acceptEarlyCheckout(RequestLock lock) throws SQLException {
        String nightSql = "UPDATE BookingNights SET IsActive = 0 "
                + "WHERE BookingID = ? AND StayDate >= ?";
        try (PreparedStatement statement = connection.prepareStatement(nightSql)) {
            statement.setInt(1, lock.bookingId);
            statement.setDate(2, Date.valueOf(lock.requestedDate));
            statement.executeUpdate();
        }

        String bookingSql = "UPDATE Bookings SET CheckOutDate = ?, "
                + "TotalAmount = CASE WHEN TotalAmount >= ? "
                + "THEN TotalAmount - ? ELSE 0 END, "
                + "RefundAmount = RefundAmount + ?, UpdatedAt = SYSDATETIME() "
                + "WHERE BookingID = ?";
        try (PreparedStatement statement = connection.prepareStatement(bookingSql)) {
            statement.setDate(1, Date.valueOf(lock.requestedDate));
            statement.setBigDecimal(2, lock.refundAmount);
            statement.setBigDecimal(3, lock.refundAmount);
            statement.setBigDecimal(4, lock.refundAmount);
            statement.setInt(5, lock.bookingId);
            statement.executeUpdate();
        }

        if (lock.refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            Integer paymentId = findCompletedPayment(lock.bookingId);
            if (paymentId != null) {
                String refundSql = "INSERT INTO Refunds "
                        + "(BookingID, PaymentID, StayChangeRequestID, Amount, "
                        + "Reason, RefundStatus, RefundedAt) "
                        + "VALUES (?, ?, ?, ?, N'Trả phòng sớm', "
                        + "'Completed', SYSDATETIME())";
                try (PreparedStatement statement
                        = connection.prepareStatement(refundSql)) {
                    statement.setInt(1, lock.bookingId);
                    statement.setInt(2, paymentId);
                    statement.setInt(3, lock.requestId);
                    statement.setBigDecimal(4, lock.refundAmount);
                    statement.executeUpdate();
                }
            }
        }
    }

    private RequestLock lockRequest(int requestId, int hostId)
            throws SQLException {
        String sql = "SELECT s.RequestID, s.BookingID, s.CustomerID, "
                + "s.RequestType, s.OriginalCheckOutDate, "
                + "s.RequestedCheckOutDate, s.ExtraAmount, s.RefundAmount, "
                + "b.HomestayID FROM StayChangeRequests s "
                + "WITH (UPDLOCK, HOLDLOCK) INNER JOIN Bookings b "
                + "ON b.BookingID = s.BookingID INNER JOIN Homestays h "
                + "ON h.HomestayID = b.HomestayID "
                + "WHERE s.RequestID = ? AND h.HostID = ? "
                + "AND s.Status = 'Pending' AND b.BookingStatus = 'Confirmed'";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, requestId);
            statement.setInt(2, hostId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    RequestLock lock = new RequestLock();
                    lock.requestId = resultSet.getInt("RequestID");
                    lock.bookingId = resultSet.getInt("BookingID");
                    lock.customerId = resultSet.getInt("CustomerID");
                    lock.homestayId = resultSet.getInt("HomestayID");
                    lock.requestType = resultSet.getString("RequestType");
                    lock.originalDate = resultSet.getDate("OriginalCheckOutDate").toLocalDate();
                    lock.requestedDate = resultSet.getDate("RequestedCheckOutDate").toLocalDate();
                    lock.extraAmount = resultSet.getBigDecimal("ExtraAmount");
                    lock.refundAmount = resultSet.getBigDecimal("RefundAmount");
                    return lock;
                }
            }
        }
        return null;
    }

    private Integer findLatestPaymentMethod(int bookingId) throws SQLException {
        String sql = "SELECT TOP 1 PaymentMethodID FROM Payments "
                + "WHERE BookingID = ? ORDER BY PaymentID DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("PaymentMethodID") : null;
            }
        }
    }

    private Integer findCompletedPayment(int bookingId) throws SQLException {
        String sql = "SELECT TOP 1 PaymentID FROM Payments "
                + "WHERE BookingID = ? AND PaymentStatus = 'Completed' "
                + "ORDER BY PaymentID DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("PaymentID") : null;
            }
        }
    }

    private void createNotification(int userId, int createdById,
            int bookingId, String title,
            String message) throws SQLException {
        String sql = "INSERT INTO Notifications "
                + "(Title, Message, Type, RelatedID, CreatedByID) "
                + "VALUES (?, ?, 'StayChange', ?, ?)";
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
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO UserNotifications "
                + "(NotificationID, UserID, IsRead) VALUES (?, ?, 0)")) {
            statement.setInt(1, notificationId);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    private List<StayChangeRequest> queryRequests(String sql, int firstId,
            String status)
            throws SQLException {
        List<StayChangeRequest> requests
                = new ArrayList<StayChangeRequest>();
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, firstId);
            if (status != null) {
                statement.setString(2, status);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(mapRequest(resultSet));
                }
            }
        }
        return requests;
    }

    private String requestSelect() {
        return "SELECT s.RequestID, s.BookingID, s.CustomerID, "
                + "s.RequestType, s.OriginalCheckOutDate, "
                + "s.RequestedCheckOutDate, s.ExtraAmount, s.RefundAmount, "
                + "s.Status, s.CustomerNote, s.ResponseNote, s.RespondedBy, "
                + "s.RespondedAt, s.CreatedAt, b.HomestayID, "
                + "h.Title AS HomestayTitle, u.FullName AS CustomerName "
                + "FROM StayChangeRequests s INNER JOIN Bookings b "
                + "ON b.BookingID = s.BookingID INNER JOIN Homestays h "
                + "ON h.HomestayID = b.HomestayID INNER JOIN Users u "
                + "ON u.UserID = s.CustomerID";
    }

    private StayChangeRequest mapRequest(ResultSet resultSet)
            throws SQLException {
        StayChangeRequest request = new StayChangeRequest();
        request.setRequestId(resultSet.getInt("RequestID"));
        request.setBookingId(resultSet.getInt("BookingID"));
        request.setCustomerId(resultSet.getInt("CustomerID"));
        request.setCustomerName(resultSet.getString("CustomerName"));
        request.setHomestayId(resultSet.getInt("HomestayID"));
        request.setHomestayTitle(resultSet.getString("HomestayTitle"));
        request.setRequestType(resultSet.getString("RequestType"));
        request.setOriginalCheckOutDate(resultSet.getDate("OriginalCheckOutDate").toLocalDate());
        request.setRequestedCheckOutDate(resultSet.getDate("RequestedCheckOutDate").toLocalDate());
        request.setExtraAmount(resultSet.getBigDecimal("ExtraAmount"));
        request.setRefundAmount(resultSet.getBigDecimal("RefundAmount"));
        request.setStatus(resultSet.getString("Status"));
        request.setCustomerNote(resultSet.getString("CustomerNote"));
        request.setResponseNote(resultSet.getString("ResponseNote"));
        int respondedBy = resultSet.getInt("RespondedBy");
        if (!resultSet.wasNull()) {
            request.setRespondedBy(respondedBy);
        }
        Timestamp respondedAt = resultSet.getTimestamp("RespondedAt");
        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        if (respondedAt != null) {
            request.setRespondedAt(respondedAt.toLocalDateTime());
        }
        if (createdAt != null) {
            request.setCreatedAt(createdAt.toLocalDateTime());
        }
        return request;
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }

    private static class RequestLock {

        int requestId;
        int bookingId;
        int customerId;
        int homestayId;
        String requestType;
        LocalDate originalDate;
        LocalDate requestedDate;
        BigDecimal extraAmount;
        BigDecimal refundAmount;
    }
}
