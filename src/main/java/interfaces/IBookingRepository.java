package interfaces;

import entity.Booking;
import entity.BookingNight;
import entity.Homestay;
import entity.PaymentMethod;
import entity.Voucher;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IBookingRepository {

    Homestay findBookableHomestay(int homestayId) throws SQLException;

    boolean isAvailable(int homestayId, LocalDate checkIn,
            LocalDate checkOut) throws SQLException;

    Map<LocalDate, BigDecimal> findCustomPrices(
            int homestayId, LocalDate checkIn, LocalDate checkOut
    ) throws SQLException;

    Voucher findValidVoucher(String voucherCode, int homestayId,
            BigDecimal orderAmount) throws SQLException;

    List<PaymentMethod> findPaymentMethods(int homestayId)
            throws SQLException;

    boolean supportsPaymentMethod(int homestayId, int paymentMethodId)
            throws SQLException;

    int createBooking(Booking booking, List<BookingNight> nights,
            Voucher voucher, int paymentMethodId)
            throws SQLException;

    List<Booking> findByCustomerId(int customerId) throws SQLException;

    Booking findByIdAndCustomerId(int bookingId, int customerId)
            throws SQLException;

    boolean cancelBooking(int bookingId, int customerId,
            String reason, BigDecimal refundAmount)
            throws SQLException;
}
