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
import interfaces.IBookingRepository;
import repository.BookingRepository;
import ultis.ValidationUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import ultis.ValidationUtil;

public class BookingManagementService {

    private final IBookingRepository bookingRepository;

    public BookingManagementService() {
        bookingRepository = new BookingRepository();
    }

    public List<Booking> getCustomerBookings(int customerId) {
        try {
            return bookingRepository.findByCustomerId(customerId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Booking>();
        }
    }

    public Booking getCustomerBooking(int bookingId, int customerId) {
        if (bookingId <= 0 || customerId <= 0) {
            return null;
        }
        try {
            return bookingRepository.findByIdAndCustomerId(
                    bookingId, customerId
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
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
                    .divide(
                            BigDecimal.valueOf(100),
                            2,
                            RoundingMode.HALF_UP
                    );
        }
        return BigDecimal.ZERO;
    }

    public boolean cancelBooking(int bookingId, int customerId,
            String reason) {
        if (ValidationUtil.isBlank(reason)) {
            throw new IllegalArgumentException(
                    "Vui lòng nhập lý do hủy phòng."
            );
        }
        reason = reason.trim();
        if (reason.length() > 255) {
            throw new IllegalArgumentException(
                    "Lý do hủy không được vượt quá 255 ký tự."
            );
        }

        Booking booking = getCustomerBooking(bookingId, customerId);
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
                    bookingId, customerId, reason, refundAmount
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
