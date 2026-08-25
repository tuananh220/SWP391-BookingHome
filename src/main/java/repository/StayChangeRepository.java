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
                    return mapEligibleBooking(resultSet);
                }
            }
        }
        return null;
    }

    private Booking mapEligibleBooking(ResultSet resultSet)
            throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(resultSet.getInt("BookingID"));
        booking.setCustomerId(resultSet.getInt("CustomerID"));
        booking.setHomestayId(resultSet.getInt("HomestayID"));
        booking.setCheckInDate(
                resultSet.getDate("CheckInDate").toLocalDate());
        booking.setCheckOutDate(
                resultSet.getDate("CheckOutDate").toLocalDate());
        booking.setTotalGuests(resultSet.getInt("TotalGuests"));
        booking.setTotalAmount(resultSet.getBigDecimal("TotalAmount"));
        booking.setBookingStatus(resultSet.getString("BookingStatus"));
        booking.setPartialRefundPercentSnapshot(
                resultSet.getBigDecimal("PartialRefundPercentSnapshot"));
        booking.setHomestayTitle(resultSet.getString("HomestayTitle"));
        return booking;
    }

    @Override
    public Booking findEligibleBookingForRequest(int requestId, int customerId)
            throws SQLException {
        String sql = "SELECT b.BookingID, b.CustomerID, b.HomestayID, "
                + "b.CheckInDate, b.CheckOutDate, b.TotalGuests, "
                + "b.TotalAmount, b.BookingStatus, "
                + "b.PartialRefundPercentSnapshot, h.Title AS HomestayTitle "
                + "FROM StayChangeRequests s INNER JOIN Bookings b "
                + "ON b.BookingID = s.BookingID INNER JOIN Homestays h "
                + "ON h.HomestayID = b.HomestayID "
                + "WHERE s.RequestID = ? AND s.CustomerID = ? "
                + "AND s.Status = 'Pending' AND b.BookingStatus = 'Confirmed'";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, requestId);
            statement.setInt(2, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapEligibleBooking(resultSet);
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
                        ? resultSet.getBigDecimal("Total")
                        : BigDecimal.ZERO;
            }
        }
    }

    @Override
    public int create(StayChangeRequest request) throws SQLException {
        String sql = "INSERT INTO StayChangeRequests "
                + "(BookingID, CustomerID, RequestType, OriginalCheckOutDate, "
                + "RequestedCheckOutDate, ExtraAmount, RefundAmount, "
                + "RefundAccountName, RefundBankName, RefundAccountNumber, "
                + "Status, CustomerNote) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Pending', ?)";
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
            statement.setString(8, request.getRefundAccountName());
            statement.setString(9, request.getRefundBankName());
            statement.setString(10, request.getRefundAccountNumber());
            statement.setString(11, request.getCustomerNote());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    @Override
    public StayChangeRequest findByIdAndCustomerId(
            int requestId, int customerId) throws SQLException {
        String sql = requestSelect()
                + " WHERE s.RequestID = ? AND s.CustomerID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, requestId);
            statement.setInt(2, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRequest(resultSet) : null;
            }
        }
    }

    @Override
    public boolean updatePending(StayChangeRequest request)
            throws SQLException {
        String sql = "UPDATE StayChangeRequests SET RequestType = ?, "
                + "OriginalCheckOutDate = ?, RequestedCheckOutDate = ?, "
                + "ExtraAmount = ?, RefundAmount = ?, CustomerNote = ?, "
                + "RefundAccountName = ?, RefundBankName = ?, "
                + "RefundAccountNumber = ?, "
                + "UpdatedAt = SYSDATETIME() WHERE RequestID = ? "
                + "AND CustomerID = ? AND Status = 'Pending'";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.getRequestType());
            statement.setDate(2, Date.valueOf(request.getOriginalCheckOutDate()));
            statement.setDate(3, Date.valueOf(request.getRequestedCheckOutDate()));
            statement.setBigDecimal(4, request.getExtraAmount());
            statement.setBigDecimal(5, request.getRefundAmount());
            statement.setString(6, request.getCustomerNote());
            statement.setString(7, request.getRefundAccountName());
            statement.setString(8, request.getRefundBankName());
            statement.setString(9, request.getRefundAccountNumber());
            statement.setInt(10, request.getRequestId());
            statement.setInt(11, request.getCustomerId());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public List<StayChangeRequest> findByCustomerId(int customerId)
            throws SQLException {
        return queryRequests(
                requestSelect() + " WHERE s.CustomerID = ? ORDER BY s.CreatedAt DESC",
                customerId, null);
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
                throw new SQLException(
                        "Yêu cầu phải đang Pending và booking phải ở trạng thái Confirmed.");
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
    public boolean accept(int requestId, int hostId)
            throws SQLException {
        ensureConnection();
        boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            RequestLock lock = lockRequest(requestId, hostId);
            if (lock == null) {
                connection.rollback();
                throw new SQLException(
                        "Yêu cầu phải đang Pending và booking phải ở trạng thái Confirmed.");
            }

            if ("Extension".equals(lock.requestType)) {
                acceptExtension(lock);
            } else if ("EarlyCheckout".equals(lock.requestType)) {
                acceptEarlyCheckout(lock);
            } else {
                throw new SQLException("Loại request không hợp lệ.");
            }

            String updateRequest = "UPDATE StayChangeRequests SET "
                    + "Status = 'Accepted', "
                    + "RefundStatus = CASE WHEN RequestType = 'EarlyCheckout' "
                    + "AND RefundAmount > 0 THEN 'Pending' ELSE RefundStatus END, "
                    + "RespondedBy = ?, "
                    + "RespondedAt = SYSDATETIME(), UpdatedAt = SYSDATETIME() "
                    + "WHERE RequestID = ? AND Status = 'Pending'";
            try (PreparedStatement statement = connection.prepareStatement(updateRequest)) {
                statement.setInt(1, hostId);
                statement.setInt(2, requestId);
                if (statement.executeUpdate() == 0) {
                    connection.rollback();
                    throw new SQLException(
                            "Yêu cầu đã được xử lý bởi một thao tác khác.");
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

    @Override
    public boolean completeRefund(int requestId, int hostId)
            throws SQLException {
        ensureConnection();
        boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            String lockSql = "SELECT s.RequestID, s.BookingID, "
                + "s.RequestedCheckOutDate, s.RefundAmount "
                + "FROM StayChangeRequests s "
                    + "INNER JOIN Bookings b ON b.BookingID = s.BookingID "
                    + "INNER JOIN Homestays h ON h.HomestayID = b.HomestayID "
                    + "WHERE s.RequestID = ? AND h.HostID = ? "
                    + "AND s.RequestType = 'EarlyCheckout' "
                    + "AND s.Status = 'Accepted' AND s.RefundStatus = 'Pending'";
            int bookingId;
            LocalDate requestedCheckOut;
            BigDecimal refundAmount;
            try (PreparedStatement statement = connection.prepareStatement(lockSql)) {
                statement.setInt(1, requestId);
                statement.setInt(2, hostId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        connection.rollback();
                        return false;
                    }
                    bookingId = resultSet.getInt("BookingID");
                    requestedCheckOut = resultSet.getDate(
                            "RequestedCheckOutDate").toLocalDate();
                    refundAmount = resultSet.getBigDecimal("RefundAmount");
                }
            }

            String refundSql = "UPDATE Refunds SET RefundStatus = 'Completed', "
                    + "RefundedAt = SYSDATETIME() "
                    + "WHERE StayChangeRequestID = ? AND RefundStatus = 'Pending'";
            try (PreparedStatement statement = connection.prepareStatement(refundSql)) {
                statement.setInt(1, requestId);
                if (statement.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            String requestSql = "UPDATE StayChangeRequests SET "
                    + "RefundStatus = 'Completed', UpdatedAt = SYSDATETIME() "
                    + "WHERE RequestID = ? AND RefundStatus = 'Pending'";
            try (PreparedStatement statement = connection.prepareStatement(requestSql)) {
                statement.setInt(1, requestId);
                if (statement.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }
            if (!updateEarlyCheckoutBooking(
                    bookingId, requestedCheckOut, refundAmount)) {
                connection.rollback();
                return false;
            }
                releaseEarlyCheckoutNights(
                    bookingId, requestedCheckOut
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

    private void acceptExtension(RequestLock lock)
            throws SQLException {
        Integer methodId = findLatestPaymentMethod(lock.bookingId);
        if (lock.extraAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SQLException("Số tiền gia hạn không hợp lệ.");
        }
        if (methodId == null) {
            throw new SQLException("Booking chưa có phương thức thanh toán.");
        }
        String paymentSql = "INSERT INTO Payments "
                + "(BookingID, PaymentMethodID, StayChangeRequestID, "
                + "PaymentType, Amount, PaymentStatus) "
                + "VALUES (?, ?, ?, 'Extension', ?, 'Pending')";
        try (PreparedStatement statement = connection.prepareStatement(paymentSql)) {
            statement.setInt(1, lock.bookingId);
            statement.setInt(2, methodId);
            statement.setInt(3, lock.requestId);
            statement.setBigDecimal(4, lock.extraAmount);
            statement.executeUpdate();
        }
    }

    private void acceptEarlyCheckout(RequestLock lock) throws SQLException {
        if (lock.refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            Integer paymentId = findCompletedPayment(lock.bookingId);
            if (paymentId == null) {
                throw new SQLException(
                        "Booking chưa có giao dịch đã thanh toán."
                );
            }
            String refundSql = "INSERT INTO Refunds "
                    + "(BookingID, PaymentID, StayChangeRequestID, Amount, "
                    + "Reason, RefundStatus) "
                    + "VALUES (?, ?, ?, ?, N'Trả phòng sớm', "
                    + "'Pending')";
            try (PreparedStatement statement = connection.prepareStatement(refundSql)) {
                statement.setInt(1, lock.bookingId);
                statement.setInt(2, paymentId);
                statement.setInt(3, lock.requestId);
                statement.setBigDecimal(4, lock.refundAmount);
                statement.executeUpdate();
            }
        } else {
            if (!updateEarlyCheckoutBooking(
                    lock.bookingId, lock.requestedDate, lock.refundAmount)) {
                throw new SQLException("Không thể cập nhật booking.");
            }
            releaseEarlyCheckoutNights(
                    lock.bookingId, lock.requestedDate
            );
        }
    }

    private void releaseEarlyCheckoutNights(int bookingId,
            LocalDate requestedCheckOut) throws SQLException {
        String nightSql = "UPDATE BookingNights SET IsActive = 0 "
                + "WHERE BookingID = ? AND StayDate >= ?";
        try (PreparedStatement statement = connection.prepareStatement(nightSql)) {
            statement.setInt(1, bookingId);
            statement.setDate(2, Date.valueOf(requestedCheckOut));
            statement.executeUpdate();
        }
    }

    private boolean updateEarlyCheckoutBooking(int bookingId,
            LocalDate requestedCheckOut, BigDecimal refundAmount)
            throws SQLException {
        String bookingSql = "UPDATE Bookings SET CheckOutDate = ?, "
                + "TotalAmount = CASE WHEN TotalAmount >= ? "
                + "THEN TotalAmount - ? ELSE 0 END, "
                + "RefundAmount = RefundAmount + ?, UpdatedAt = SYSDATETIME() "
                + "WHERE BookingID = ?";
        try (PreparedStatement statement = connection.prepareStatement(bookingSql)) {
            statement.setDate(1, Date.valueOf(requestedCheckOut));
            statement.setBigDecimal(2, refundAmount);
            statement.setBigDecimal(3, refundAmount);
            statement.setBigDecimal(4, refundAmount);
            statement.setInt(5, bookingId);
            return statement.executeUpdate() > 0;
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
        String sql = "SELECT TOP 1 p.PaymentMethodID FROM Payments p "
            + "INNER JOIN PaymentMethods pm "
            + "ON pm.PaymentMethodID = p.PaymentMethodID "
            + "WHERE p.BookingID = ? AND pm.IsOnline = 1 "
            + "AND pm.IsActive = 1 ORDER BY p.PaymentID DESC";
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

    
    private List<StayChangeRequest> queryRequests(String sql, int firstId,
            String status)
            throws SQLException {
        List<StayChangeRequest> requests = new ArrayList<StayChangeRequest>();
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
                + "s.RefundAccountName, s.RefundBankName, s.RefundAccountNumber, "
                + "s.RefundStatus, s.Status, s.CustomerNote, s.ResponseNote, s.RespondedBy, "
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
        request.setRefundAccountName(resultSet.getString("RefundAccountName"));
        request.setRefundBankName(resultSet.getString("RefundBankName"));
        request.setRefundAccountNumber(resultSet.getString("RefundAccountNumber"));
        request.setRefundStatus(resultSet.getString("RefundStatus"));
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
