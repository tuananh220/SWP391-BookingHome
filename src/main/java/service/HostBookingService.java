/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author Admin
 */
import entity.Booking;
import entity.BookingNight;
import interfaces.IHostBookingRepository;
import repository.HostBookingRepository;
import ultis.ValidationUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HostBookingService {

    private static final List<String> VALID_STATUSES = Arrays.asList(
            "Pending", "Confirmed", "Rejected", "Cancelled", "Completed"
    );

    private final IHostBookingRepository bookingRepository;

    public HostBookingService() {
        bookingRepository = new HostBookingRepository();
    }

    public List<Booking> getBookings(int hostId, String status) {
        if (!VALID_STATUSES.contains(status)) {
            status = null;
        }
        try {
            return bookingRepository.findByHostId(hostId, status);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Booking>();
        }
    }

    public Booking getBooking(int bookingId, int hostId) {
        try {
            return bookingRepository.findByIdAndHostId(bookingId, hostId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public List<BookingNight> getBookingNights(int bookingId, int hostId) {
        try {
            return bookingRepository.findNightsByBookingIdAndHostId(
                    bookingId, hostId
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<BookingNight>();
        }
    }

    public List<Booking> getHistory(int hostId, String status,
            Integer homestayId, LocalDate fromDate, LocalDate toDate) {
        if (!VALID_STATUSES.contains(status)) {
            status = null;
        }
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "Ngày bắt đầu không được sau ngày kết thúc."
            );
        }
        try {
            return bookingRepository.findHistory(
                    hostId, status, homestayId, fromDate, toDate
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Booking>();
        }
    }

    public entity.HostCancellationSummary getCancellationSummary(int hostId,
            Integer homestayId, LocalDate fromDate, LocalDate toDate) {
        try {
            return bookingRepository.findCancellationSummary(
                    hostId, homestayId, fromDate, toDate
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new entity.HostCancellationSummary();
        }
    }

    public boolean confirmBooking(int bookingId, int hostId) {
        Booking booking = validateProcessableBooking(bookingId, hostId);
        if (booking.isPaymentOnline()
                && "Pending".equals(booking.getPaymentStatus())) {
            throw new IllegalArgumentException(
                    "Booking đang chờ khách thanh toán trực tuyến."
            );
        }

        try {
            return bookingRepository.confirmBooking(bookingId, hostId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean rejectBooking(int bookingId, int hostId, String reason) {
        validateProcessableBooking(bookingId, hostId);

        if (ValidationUtil.isBlank(reason)) {
            throw new IllegalArgumentException(
                    "Vui lòng nhập lý do từ chối."
            );
        }
        reason = reason.trim();
        if (reason.length() > 255) {
            throw new IllegalArgumentException(
                    "Lý do từ chối không được vượt quá 255 ký tự."
            );
        }

        try {
            return bookingRepository.rejectBooking(
                    bookingId, hostId, reason
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public BigDecimal estimateRefund(Booking booking) {
        if (booking == null
                || !"Completed".equals(booking.getPaymentStatus())) {
            return BigDecimal.ZERO;
        }
        Integer fullDays = booking.getFullRefundDaysSnapshot();
        Integer partialDays = booking.getPartialRefundDaysSnapshot();
        BigDecimal partialPercent
                = booking.getPartialRefundPercentSnapshot();
        if (fullDays == null || partialDays == null
                || partialPercent == null) {
            return BigDecimal.ZERO;
        }
        long daysBeforeCheckIn = ChronoUnit.DAYS.between(
                LocalDate.now(), booking.getCheckInDate()
        );
        if (daysBeforeCheckIn >= fullDays) {
            return booking.getTotalAmount();
        }
        if (daysBeforeCheckIn >= partialDays) {
            return booking.getTotalAmount()
                    .multiply(partialPercent)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public boolean cancelBooking(int bookingId, int hostId, String reason) {
        if (ValidationUtil.isBlank(reason)) {
            throw new IllegalArgumentException("Vui lòng nhập lý do hủy.");
        }
        reason = reason.trim();
        if (reason.length() > 255) {
            throw new IllegalArgumentException(
                    "Lý do hủy không được vượt quá 255 ký tự."
            );
        }
        Booking booking = getBooking(bookingId, hostId);
        if (booking == null) {
            throw new IllegalArgumentException("Không tìm thấy booking.");
        }
        if (!"Pending".equals(booking.getBookingStatus())
                && !"Confirmed".equals(booking.getBookingStatus())) {
            throw new IllegalArgumentException(
                    "Booking ở trạng thái này không thể hủy."
            );
        }
        if (!booking.getCheckInDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Không thể hủy sau khi đã đến ngày nhận phòng."
            );
        }
        BigDecimal refundAmount = estimateRefund(booking);
        try {
            return bookingRepository.cancelBooking(
                    bookingId, hostId, reason, refundAmount
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private Booking validateProcessableBooking(int bookingId, int hostId) {
        Booking booking = getBooking(bookingId, hostId);
        if (booking == null) {
            throw new IllegalArgumentException("Không tìm thấy booking.");
        }
        if (!"Pending".equals(booking.getBookingStatus())) {
            throw new IllegalArgumentException(
                    "Booking không còn ở trạng thái chờ xử lý."
            );
        }
        if (booking.isPaymentOnline()) {
            throw new IllegalArgumentException(
                    "Booking thanh toán online sẽ tự xác nhận sau thanh toán."
            );
        }
        return booking;
    }
}
