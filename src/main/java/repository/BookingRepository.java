package repository;

import dal.DBContext;
import entity.Booking;
import entity.BookingNight;
import entity.CancellationPolicy;
import entity.Homestay;
import entity.PaymentMethod;
import entity.Voucher;
import interfaces.IBookingRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingRepository extends DBContext
        implements IBookingRepository {

    public BookingRepository() {
        super();
    }

    @Override
    public Homestay findBookableHomestay(int homestayId) throws SQLException {
        String sql = "SELECT h.HomestayID, h.HostID, h.Title, h.Address, "
                + "h.City, h.District, h.PricePerNight, h.MaxGuests, "
                + "h.CancellationPolicyID, u.FullName AS HostName, "
                + "cp.PolicyName, cp.Description AS PolicyDescription, "
                + "cp.FullRefundDays, cp.PartialRefundDays, "
                + "cp.PartialRefundPercent "
                + "FROM Homestays h "
                + "INNER JOIN Users u ON u.UserID = h.HostID "
                + "LEFT JOIN CancellationPolicies cp "
                + "ON cp.PolicyID = h.CancellationPolicyID "
                + "WHERE h.HomestayID = ? AND h.Status = 'Active'";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Homestay homestay = new Homestay();
                    homestay.setHomestayId(resultSet.getInt("HomestayID"));
                    homestay.setHostId(resultSet.getInt("HostID"));
                    homestay.setHostName(resultSet.getString("HostName"));
                    homestay.setTitle(resultSet.getString("Title"));
                    homestay.setAddress(resultSet.getString("Address"));
                    homestay.setCity(resultSet.getString("City"));
                    homestay.setDistrict(resultSet.getString("District"));
                    homestay.setPricePerNight(
                            resultSet.getBigDecimal("PricePerNight")
                    );
                    homestay.setMaxGuests(resultSet.getInt("MaxGuests"));

                    int policyId = resultSet.getInt("CancellationPolicyID");
                    if (!resultSet.wasNull()) {
                        homestay.setCancellationPolicyId(policyId);
                        CancellationPolicy policy = new CancellationPolicy();
                        policy.setPolicyId(policyId);
                        policy.setPolicyName(resultSet.getString("PolicyName"));
                        policy.setDescription(
                                resultSet.getString("PolicyDescription")
                        );
                        policy.setFullRefundDays(
                                resultSet.getInt("FullRefundDays")
                        );
                        policy.setPartialRefundDays(
                                resultSet.getInt("PartialRefundDays")
                        );
                        policy.setPartialRefundPercent(
                                resultSet.getDouble("PartialRefundPercent")
                        );
                        homestay.setCancellationPolicy(policy);
                    }
                    return homestay;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isAvailable(int homestayId, LocalDate checkIn,
            LocalDate checkOut) throws SQLException {
        ensureConnection();
        return isAvailable(connection, homestayId, checkIn, checkOut, false);
    }

    @Override
    public Map<LocalDate, BigDecimal> findCustomPrices(
            int homestayId, LocalDate checkIn, LocalDate checkOut
    ) throws SQLException {
        Map<LocalDate, BigDecimal> prices
                = new HashMap<LocalDate, BigDecimal>();
        String sql = "SELECT ScheduleDate, CustomPrice "
                + "FROM HomestaySchedules "
                + "WHERE HomestayID = ? AND IsAvailable = 1 "
                + "AND ScheduleDate >= ? AND ScheduleDate < ? "
                + "AND CustomPrice IS NOT NULL";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            statement.setDate(2, Date.valueOf(checkIn));
            statement.setDate(3, Date.valueOf(checkOut));
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

    @Override
    public Voucher findValidVoucher(String voucherCode, int homestayId,
            BigDecimal orderAmount)
            throws SQLException {
        String sql = "SELECT v.VoucherID, v.CreatedByID, v.HomestayID, "
                + "v.VoucherCode, v.DiscountRate, v.MaxDiscountAmount, "
                + "v.MinOrderValue, v.StartDate, v.EndDate, "
                + "v.UsageLimit, v.UsedCount, v.IsActive "
                + "FROM Vouchers v "
                + "INNER JOIN Users creator ON creator.UserID = v.CreatedByID "
                + "INNER JOIN Roles r ON r.RoleID = creator.RoleID "
                + "INNER JOIN Homestays h ON h.HomestayID = ? "
                + "WHERE UPPER(v.VoucherCode) = UPPER(?) "
                + "AND v.IsActive = 1 "
                + "AND SYSDATETIME() BETWEEN v.StartDate AND v.EndDate "
                + "AND v.UsedCount < v.UsageLimit "
                + "AND v.MinOrderValue <= ? "
                + "AND (v.HomestayID = h.HomestayID "
                + "OR (v.HomestayID IS NULL "
                + "AND (r.RoleName = N'Admin' OR v.CreatedByID = h.HostID)))";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            statement.setString(2, voucherCode);
            statement.setBigDecimal(3, orderAmount);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapVoucher(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public List<PaymentMethod> findPaymentMethods(int homestayId)
            throws SQLException {
        List<PaymentMethod> methods = new ArrayList<PaymentMethod>();
        String sql = "SELECT pm.PaymentMethodID, pm.MethodCode, "
                + "pm.MethodName, pm.IsOnline, pm.IsActive "
                + "FROM PaymentMethods pm "
                + "WHERE pm.IsActive = 1 AND ("
                + "EXISTS (SELECT 1 FROM HomestayPaymentMethods hpm "
                + "WHERE hpm.HomestayID = ? "
                + "AND hpm.PaymentMethodID = pm.PaymentMethodID) "
                + "OR NOT EXISTS (SELECT 1 FROM HomestayPaymentMethods hpm2 "
                + "WHERE hpm2.HomestayID = ?)) "
                + "ORDER BY pm.PaymentMethodID";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            statement.setInt(2, homestayId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    PaymentMethod method = new PaymentMethod();
                    method.setPaymentMethodId(
                            resultSet.getInt("PaymentMethodID")
                    );
                    method.setMethodCode(resultSet.getString("MethodCode"));
                    method.setMethodName(resultSet.getString("MethodName"));
                    method.setOnline(resultSet.getBoolean("IsOnline"));
                    method.setActive(resultSet.getBoolean("IsActive"));
                    methods.add(method);
                }
            }
        }
        return methods;
    }

    @Override
    public boolean supportsPaymentMethod(int homestayId, int paymentMethodId)
            throws SQLException {
        String sql = "SELECT 1 FROM PaymentMethods pm "
                + "WHERE pm.PaymentMethodID = ? AND pm.IsActive = 1 AND ("
                + "EXISTS (SELECT 1 FROM HomestayPaymentMethods hpm "
                + "WHERE hpm.HomestayID = ? "
                + "AND hpm.PaymentMethodID = pm.PaymentMethodID) "
                + "OR NOT EXISTS (SELECT 1 FROM HomestayPaymentMethods hpm2 "
                + "WHERE hpm2.HomestayID = ?))";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, paymentMethodId);
            statement.setInt(2, homestayId);
            statement.setInt(3, homestayId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public int createBooking(Booking booking, List<BookingNight> nights,
            Voucher voucher, int paymentMethodId)
            throws SQLException {
        ensureConnection();
        boolean oldAutoCommit = connection.getAutoCommit();
        int oldIsolation = connection.getTransactionIsolation();

        try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            if (!isAvailable(
                    connection,
                    booking.getHomestayId(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    true
            )) {
                throw new SQLException("Homestay đã được đặt trong thời gian này.");
            }

            int bookingId = insertBooking(booking);
            insertBookingNights(bookingId, nights);

            if (voucher != null) {
                reserveVoucher(bookingId, booking, voucher);
            }

            if (booking.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
                insertPayment(bookingId, booking.getTotalAmount(), paymentMethodId);
            }

            connection.commit();
            return bookingId;
        } catch (SQLException exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setTransactionIsolation(oldIsolation);
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    @Override
    public List<Booking> findByCustomerId(int customerId)
            throws SQLException {
        List<Booking> bookings = new ArrayList<Booking>();
        String sql = bookingDisplaySelect()
                + " WHERE b.CustomerID = ? ORDER BY b.CreatedAt DESC";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bookings.add(mapBooking(resultSet));
                }
            }
        }
        return bookings;
    }

    @Override
    public Booking findByIdAndCustomerId(int bookingId, int customerId)
            throws SQLException {
        String sql = bookingDisplaySelect()
                + " WHERE b.BookingID = ? AND b.CustomerID = ?";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            statement.setInt(2, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapBooking(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public boolean cancelBooking(int bookingId, int customerId,
            String reason, BigDecimal refundAmount)
            throws SQLException {
        ensureConnection();
        boolean oldAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);

            Integer voucherId = null;
            String lockSql = "SELECT VoucherID FROM Bookings "
                    + "WITH (UPDLOCK, HOLDLOCK) "
                    + "WHERE BookingID = ? AND CustomerID = ? "
                    + "AND BookingStatus IN ('Pending', 'Confirmed')";
            try (PreparedStatement statement
                    = connection.prepareStatement(lockSql)) {
                statement.setInt(1, bookingId);
                statement.setInt(2, customerId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        connection.rollback();
                        return false;
                    }
                    int value = resultSet.getInt("VoucherID");
                    if (!resultSet.wasNull()) {
                        voucherId = value;
                    }
                }
            }

            String updateBooking = "UPDATE Bookings SET "
                    + "BookingStatus = 'Cancelled', CancelReason = ?, "
                    + "CancelledBy = 'Customer', CancelledAt = SYSDATETIME(), "
                    + "RefundAmount = ?, UpdatedAt = SYSDATETIME() "
                    + "WHERE BookingID = ? AND CustomerID = ?";
            try (PreparedStatement statement
                    = connection.prepareStatement(updateBooking)) {
                statement.setString(1, reason);
                statement.setBigDecimal(2, refundAmount);
                statement.setInt(3, bookingId);
                statement.setInt(4, customerId);
                statement.executeUpdate();
            }

            String releaseNights = "UPDATE BookingNights SET IsActive = 0 "
                    + "WHERE BookingID = ?";
            try (PreparedStatement statement
                    = connection.prepareStatement(releaseNights)) {
                statement.setInt(1, bookingId);
                statement.executeUpdate();
            }

            if (voucherId != null) {
                releaseVoucher(bookingId, voucherId);
            }

            if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
                createRefundForCompletedPayment(
                        bookingId, refundAmount, reason
                );
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

    private String bookingDisplaySelect() {
        return "SELECT b.BookingID, b.CustomerID, b.HomestayID, "
                + "b.VoucherID, b.CancellationPolicyID, "
                + "b.CancellationPolicyName, b.FullRefundDaysSnapshot, "
                + "b.PartialRefundDaysSnapshot, "
                + "b.PartialRefundPercentSnapshot, b.CheckInDate, "
                + "b.CheckOutDate, b.TotalGuests, b.OriginalAmount, "
                + "b.DiscountAmount, b.TotalAmount, b.BookingStatus, "
                + "b.Note, b.CancelReason, b.CancelledBy, b.CancelledAt, "
                + "b.RefundAmount, b.CreatedAt, h.Title AS HomestayTitle, "
                + "(SELECT TOP 1 hi.ImageURL FROM HomestayImages hi "
                + "WHERE hi.HomestayID = h.HomestayID "
                + "ORDER BY hi.IsPrimary DESC, hi.ImageID) AS HomestayImageURL, "
                + "pay.PaymentStatus, pay.MethodName AS PaymentMethodName "
                + "FROM Bookings b "
                + "INNER JOIN Homestays h ON h.HomestayID = b.HomestayID "
                + "OUTER APPLY (SELECT TOP 1 p.PaymentStatus, pm.MethodName "
                + "FROM Payments p INNER JOIN PaymentMethods pm "
                + "ON pm.PaymentMethodID = p.PaymentMethodID "
                + "WHERE p.BookingID = b.BookingID "
                + "ORDER BY p.PaymentID DESC) pay";
    }

    private Booking mapBooking(ResultSet resultSet) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(resultSet.getInt("BookingID"));
        booking.setCustomerId(resultSet.getInt("CustomerID"));
        booking.setHomestayId(resultSet.getInt("HomestayID"));

        int voucherId = resultSet.getInt("VoucherID");
        if (!resultSet.wasNull()) {
            booking.setVoucherId(voucherId);
        }
        int policyId = resultSet.getInt("CancellationPolicyID");
        if (!resultSet.wasNull()) {
            booking.setCancellationPolicyId(policyId);
        }

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
        booking.setCancelledBy(resultSet.getString("CancelledBy"));
        booking.setRefundAmount(resultSet.getBigDecimal("RefundAmount"));
        booking.setHomestayTitle(resultSet.getString("HomestayTitle"));
        booking.setHomestayImageUrl(
                resultSet.getString("HomestayImageURL")
        );
        booking.setPaymentStatus(resultSet.getString("PaymentStatus"));
        booking.setPaymentMethodName(
                resultSet.getString("PaymentMethodName")
        );

        Timestamp cancelledAt = resultSet.getTimestamp("CancelledAt");
        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        if (cancelledAt != null) {
            booking.setCancelledAt(cancelledAt.toLocalDateTime());
        }
        if (createdAt != null) {
            booking.setCreatedAt(createdAt.toLocalDateTime());
        }
        return booking;
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

    private void createRefundForCompletedPayment(
            int bookingId, BigDecimal refundAmount, String reason
    ) throws SQLException {
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

    private int insertBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO Bookings (CustomerID, HomestayID, "
                + "VoucherID, CancellationPolicyID, CancellationPolicyName, "
                + "FullRefundDaysSnapshot, PartialRefundDaysSnapshot, "
                + "PartialRefundPercentSnapshot, CheckInDate, CheckOutDate, "
                + "TotalGuests, OriginalAmount, DiscountAmount, TotalAmount, "
                + "BookingStatus, Note) VALUES "
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Pending', ?)";

        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, booking.getCustomerId());
            statement.setInt(2, booking.getHomestayId());
            setNullableInteger(statement, 3, booking.getVoucherId());
            setNullableInteger(statement, 4, booking.getCancellationPolicyId());
            statement.setString(5, booking.getCancellationPolicyName());
            setNullableInteger(statement, 6, booking.getFullRefundDaysSnapshot());
            setNullableInteger(statement, 7, booking.getPartialRefundDaysSnapshot());

            if (booking.getPartialRefundPercentSnapshot() == null) {
                statement.setNull(8, Types.DECIMAL);
            } else {
                statement.setBigDecimal(
                        8, booking.getPartialRefundPercentSnapshot()
                );
            }

            statement.setDate(9, Date.valueOf(booking.getCheckInDate()));
            statement.setDate(10, Date.valueOf(booking.getCheckOutDate()));
            statement.setInt(11, booking.getTotalGuests());
            statement.setBigDecimal(12, booking.getOriginalAmount());
            statement.setBigDecimal(13, booking.getDiscountAmount());
            statement.setBigDecimal(14, booking.getTotalAmount());
            statement.setString(15, booking.getNote());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Không thể tạo booking.");
    }

    private void insertBookingNights(int bookingId,
            List<BookingNight> nights)
            throws SQLException {
        String sql = "INSERT INTO BookingNights "
                + "(BookingID, HomestayID, StayDate, NightPrice, IsActive) "
                + "VALUES (?, ?, ?, ?, 1)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (BookingNight night : nights) {
                statement.setInt(1, bookingId);
                statement.setInt(2, night.getHomestayId());
                statement.setDate(3, Date.valueOf(night.getStayDate()));
                statement.setBigDecimal(4, night.getNightPrice());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void reserveVoucher(int bookingId, Booking booking,
            Voucher voucher) throws SQLException {
        String updateSql = "UPDATE Vouchers SET UsedCount = UsedCount + 1 "
                + "WHERE VoucherID = ? AND IsActive = 1 "
                + "AND UsedCount < UsageLimit";
        try (PreparedStatement statement
                = connection.prepareStatement(updateSql)) {
            statement.setInt(1, voucher.getVoucherId());
            if (statement.executeUpdate() == 0) {
                throw new SQLException("Voucher đã hết lượt sử dụng.");
            }
        }

        String insertSql = "INSERT INTO VoucherUsages "
                + "(VoucherID, BookingID, CustomerID, DiscountAmount, "
                + "UsageStatus) VALUES (?, ?, ?, ?, 'Reserved')";
        try (PreparedStatement statement
                = connection.prepareStatement(insertSql)) {
            statement.setInt(1, voucher.getVoucherId());
            statement.setInt(2, bookingId);
            statement.setInt(3, booking.getCustomerId());
            statement.setBigDecimal(4, booking.getDiscountAmount());
            statement.executeUpdate();
        }
    }

    private void insertPayment(int bookingId, BigDecimal amount,
            int paymentMethodId) throws SQLException {
        String sql = "INSERT INTO Payments "
                + "(BookingID, PaymentMethodID, PaymentType, Amount, "
                + "PaymentStatus) VALUES (?, ?, 'Booking', ?, 'Pending')";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            statement.setInt(2, paymentMethodId);
            statement.setBigDecimal(3, amount);
            statement.executeUpdate();
        }
    }

    private boolean isAvailable(Connection currentConnection, int homestayId,
            LocalDate checkIn, LocalDate checkOut,
            boolean lockRows) throws SQLException {
        String lockHint = lockRows ? " WITH (UPDLOCK, HOLDLOCK) " : " ";
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM BookingNights" + lockHint
                + "WHERE HomestayID = ? AND IsActive = 1 "
                + "AND StayDate >= ? AND StayDate < ?) + "
                + "(SELECT COUNT(*) FROM HomestaySchedules" + lockHint
                + "WHERE HomestayID = ? AND IsAvailable = 0 "
                + "AND ScheduleDate >= ? AND ScheduleDate < ?) AS ConflictCount";

        try (PreparedStatement statement
                = currentConnection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            statement.setDate(2, Date.valueOf(checkIn));
            statement.setDate(3, Date.valueOf(checkOut));
            statement.setInt(4, homestayId);
            statement.setDate(5, Date.valueOf(checkIn));
            statement.setDate(6, Date.valueOf(checkOut));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        && resultSet.getInt("ConflictCount") == 0;
            }
        }
    }

    private Voucher mapVoucher(ResultSet resultSet) throws SQLException {
        Voucher voucher = new Voucher();
        voucher.setVoucherId(resultSet.getInt("VoucherID"));
        voucher.setCreatedById(resultSet.getInt("CreatedByID"));

        int homestayId = resultSet.getInt("HomestayID");
        if (!resultSet.wasNull()) {
            voucher.setHomestayId(homestayId);
        }

        voucher.setVoucherCode(resultSet.getString("VoucherCode"));
        voucher.setDiscountRate(resultSet.getBigDecimal("DiscountRate"));
        voucher.setMaxDiscountAmount(
                resultSet.getBigDecimal("MaxDiscountAmount")
        );
        voucher.setMinOrderValue(resultSet.getBigDecimal("MinOrderValue"));

        Timestamp startDate = resultSet.getTimestamp("StartDate");
        Timestamp endDate = resultSet.getTimestamp("EndDate");
        if (startDate != null) {
            voucher.setStartDate(startDate.toLocalDateTime());
        }
        if (endDate != null) {
            voucher.setEndDate(endDate.toLocalDateTime());
        }

        voucher.setUsageLimit(resultSet.getInt("UsageLimit"));
        voucher.setUsedCount(resultSet.getInt("UsedCount"));
        voucher.setActive(resultSet.getBoolean("IsActive"));
        return voucher;
    }

    private void setNullableInteger(PreparedStatement statement, int index,
            Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
