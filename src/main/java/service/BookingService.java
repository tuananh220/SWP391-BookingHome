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
import entity.BookingQuote;
import entity.CancellationPolicy;
import entity.Homestay;
import entity.PaymentMethod;
import entity.Voucher;
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
import java.util.Map;

public class BookingService {

    private final IBookingRepository bookingRepository;

    public BookingService() {
        this.bookingRepository = new BookingRepository();
    }

    public BookingQuote createQuote(int homestayId, LocalDate checkIn,
            LocalDate checkOut, int guests,
            String voucherCode) {
        validateDatesAndGuests(checkIn, checkOut, guests);

        try {
            Homestay homestay = bookingRepository
                    .findBookableHomestay(homestayId);
            if (homestay == null) {
                throw new IllegalArgumentException(
                        "Homestay không tồn tại hoặc chưa hoạt động."
                );
            }
            if (guests > homestay.getMaxGuests()) {
                throw new IllegalArgumentException(
                        "Số khách vượt quá sức chứa của homestay."
                );
            }
            if (!bookingRepository.isAvailable(
                    homestayId, checkIn, checkOut)) {
                throw new IllegalArgumentException(
                        "Homestay không còn trống trong thời gian đã chọn."
                );
            }

            Map<LocalDate, BigDecimal> customPrices
                    = bookingRepository.findCustomPrices(
                            homestayId, checkIn, checkOut
                    );
            List<BookingNight> nights = buildNights(
                    homestay, checkIn, checkOut, customPrices
            );
            BigDecimal originalAmount = calculateOriginalAmount(nights);
            Voucher voucher = null;
            BigDecimal discountAmount = BigDecimal.ZERO;

            if (!ValidationUtil.isBlank(voucherCode)) {
                voucher = bookingRepository.findValidVoucher(
                        voucherCode.trim(), homestayId, originalAmount
                );
                if (voucher == null) {
                    throw new IllegalArgumentException(
                            "Voucher không hợp lệ, đã hết hạn hoặc hết lượt."
                    );
                }
                discountAmount = calculateDiscount(originalAmount, voucher);
            }

            BigDecimal totalAmount = originalAmount
                    .subtract(discountAmount)
                    .max(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);

            BookingQuote quote = new BookingQuote();
            quote.setHomestay(homestay);
            quote.setCheckInDate(checkIn);
            quote.setCheckOutDate(checkOut);
            quote.setTotalGuests(guests);
            quote.setTotalNights(nights.size());
            quote.setNights(nights);
            quote.setOriginalAmount(originalAmount);
            quote.setDiscountAmount(discountAmount);
            quote.setTotalAmount(totalAmount);
            quote.setVoucher(voucher);
            quote.setPaymentMethods(
                    bookingRepository.findPaymentMethods(homestayId)
            );
            return quote;
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new IllegalStateException(
                    "Không thể xử lý dữ liệu đặt phòng."
            );
        }
    }

    public int createBooking(int customerId, int homestayId,
            LocalDate checkIn, LocalDate checkOut,
            int guests, String voucherCode,
            int paymentMethodId, String note) {
        BookingQuote quote = createQuote(
                homestayId, checkIn, checkOut, guests, voucherCode
        );

        if (note != null && note.trim().length() > 255) {
            throw new IllegalArgumentException(
                    "Ghi chú không được vượt quá 255 ký tự."
            );
        }

        try {
            if (!bookingRepository.supportsPaymentMethod(
                    homestayId, paymentMethodId)) {
                throw new IllegalArgumentException(
                        "Phương thức thanh toán không hợp lệ."
                );
            }

            Booking booking = buildBooking(customerId, quote, note);
            return bookingRepository.createBooking(
                    booking,
                    quote.getNights(),
                    quote.getVoucher(),
                    paymentMethodId
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new IllegalStateException(
                    "Không thể tạo booking. Phòng có thể vừa được người khác đặt."
            );
        }
    }

    private Booking buildBooking(int customerId, BookingQuote quote,
            String note) {
        Booking booking = new Booking();
        Homestay homestay = quote.getHomestay();
        Voucher voucher = quote.getVoucher();
        CancellationPolicy policy = homestay.getCancellationPolicy();

        booking.setCustomerId(customerId);
        booking.setHomestayId(homestay.getHomestayId());
        booking.setVoucherId(voucher == null ? null : voucher.getVoucherId());
        booking.setCheckInDate(quote.getCheckInDate());
        booking.setCheckOutDate(quote.getCheckOutDate());
        booking.setTotalGuests(quote.getTotalGuests());
        booking.setOriginalAmount(quote.getOriginalAmount());
        booking.setDiscountAmount(quote.getDiscountAmount());
        booking.setTotalAmount(quote.getTotalAmount());
        booking.setBookingStatus("Pending");
        booking.setNote(
                ValidationUtil.isBlank(note) ? null : note.trim()
        );

        if (policy != null) {
            booking.setCancellationPolicyId(policy.getPolicyId());
            booking.setCancellationPolicyName(policy.getPolicyName());
            booking.setFullRefundDaysSnapshot(policy.getFullRefundDays());
            booking.setPartialRefundDaysSnapshot(
                    policy.getPartialRefundDays()
            );
            booking.setPartialRefundPercentSnapshot(
                    BigDecimal.valueOf(policy.getPartialRefundPercent())
            );
        }
        return booking;
    }

    private List<BookingNight> buildNights(
            Homestay homestay,
            LocalDate checkIn,
            LocalDate checkOut,
            Map<LocalDate, BigDecimal> customPrices
    ) {
        List<BookingNight> nights = new ArrayList<BookingNight>();
        LocalDate date = checkIn;

        while (date.isBefore(checkOut)) {
            BookingNight night = new BookingNight();
            night.setHomestayId(homestay.getHomestayId());
            night.setStayDate(date);
            night.setNightPrice(
                    customPrices.containsKey(date)
                    ? customPrices.get(date)
                    : homestay.getPricePerNight()
            );
            night.setActive(true);
            nights.add(night);
            date = date.plusDays(1);
        }
        return nights;
    }

    private BigDecimal calculateOriginalAmount(List<BookingNight> nights) {
        BigDecimal amount = BigDecimal.ZERO;
        for (BookingNight night : nights) {
            amount = amount.add(night.getNightPrice());
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDiscount(BigDecimal originalAmount,
            Voucher voucher) {
        BigDecimal discount = originalAmount
                .multiply(voucher.getDiscountRate())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        if (voucher.getMaxDiscountAmount() != null
                && discount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
            discount = voucher.getMaxDiscountAmount();
        }
        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    private void validateDatesAndGuests(LocalDate checkIn,
            LocalDate checkOut,
            int guests) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException(
                    "Vui lòng chọn ngày nhận và trả phòng."
            );
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Ngày nhận phòng không được ở trong quá khứ."
            );
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException(
                    "Ngày trả phòng phải sau ngày nhận phòng."
            );
        }
        long totalNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (totalNights > 30) {
            throw new IllegalArgumentException(
                    "Mỗi booking được đặt tối đa 30 đêm."
            );
        }
        if (guests <= 0) {
            throw new IllegalArgumentException(
                    "Số khách phải lớn hơn 0."
            );
        }
    }
}
