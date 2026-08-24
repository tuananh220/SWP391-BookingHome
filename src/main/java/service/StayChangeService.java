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
import entity.StayChangeRequest;
import interfaces.IStayChangeRepository;
import repository.StayChangeRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ultis.ValidationUtil;

public class StayChangeService {

    private final IStayChangeRepository repository;

    public StayChangeService() {
        repository = new StayChangeRepository();
    }

    public Booking getEligibleBooking(int bookingId, int customerId) {
        try {
            return repository.findEligibleBooking(bookingId, customerId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public StayChangeRequest getCustomerRequest(
            int requestId, int customerId
    ) {
        try {
            return repository.findByIdAndCustomerId(requestId, customerId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public Booking getEligibleBookingForRequest(
            int requestId, int customerId
    ) {
        try {
            return repository.findEligibleBookingForRequest(
                    requestId, customerId
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public int createRequest(int bookingId, int customerId,
            String requestType,
            LocalDate requestedCheckOutDate,
            String customerNote, String refundAccountName,
            String refundBankName, String refundAccountNumber) {
        Booking booking = getEligibleBooking(bookingId, customerId);
        if (booking == null) {
            throw new IllegalArgumentException(
                    "Booking không thể tạo yêu cầu thay đổi."
            );
        }
        validateNote(customerNote);

        StayChangeRequest request = new StayChangeRequest();
        request.setBookingId(bookingId);
        request.setCustomerId(customerId);
        request.setRequestType(requestType);
        request.setOriginalCheckOutDate(booking.getCheckOutDate());
        request.setRequestedCheckOutDate(requestedCheckOutDate);
        request.setCustomerNote(
                ValidationUtil.isBlank(customerNote)
                ? null : customerNote.trim()
        );
        setRefundAccount(request, requestType, refundAccountName,
            refundBankName, refundAccountNumber);

        if ("Extension".equals(requestType)) {
            prepareExtension(request, booking);
        } else if ("EarlyCheckout".equals(requestType)) {
            prepareEarlyCheckout(request, booking);
        } else {
            throw new IllegalArgumentException("Loại yêu cầu không hợp lệ.");
        }

        try {
            return repository.create(request);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public boolean updateRequest(int requestId, int customerId,
            String requestType, LocalDate requestedCheckOutDate,
            String customerNote, String refundAccountName,
            String refundBankName, String refundAccountNumber) {
        StayChangeRequest existing = getCustomerRequest(
                requestId, customerId
        );
        if (existing == null || !"Pending".equals(existing.getStatus())) {
            throw new IllegalArgumentException(
                    "Chỉ có thể chỉnh sửa yêu cầu đang chờ xử lý."
            );
        }

        Booking booking = getEligibleBookingForRequest(
                requestId, customerId
        );
        if (booking == null) {
            throw new IllegalArgumentException(
                    "Booking không còn đủ điều kiện thay đổi."
            );
        }
        validateNote(customerNote);

        StayChangeRequest request = new StayChangeRequest();
        request.setRequestId(requestId);
        request.setBookingId(booking.getBookingId());
        request.setCustomerId(customerId);
        request.setRequestType(requestType);
        request.setOriginalCheckOutDate(booking.getCheckOutDate());
        request.setRequestedCheckOutDate(requestedCheckOutDate);
        request.setCustomerNote(
                ValidationUtil.isBlank(customerNote)
                ? null : customerNote.trim()
        );
        setRefundAccount(request, requestType, refundAccountName,
            refundBankName, refundAccountNumber);

        if ("Extension".equals(requestType)) {
            prepareExtension(request, booking);
        } else if ("EarlyCheckout".equals(requestType)) {
            prepareEarlyCheckout(request, booking);
        } else {
            throw new IllegalArgumentException("Loại yêu cầu không hợp lệ.");
        }

        try {
            return repository.updatePending(request);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public List<StayChangeRequest> getCustomerRequests(int customerId) {
        try {
            return repository.findByCustomerId(customerId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<StayChangeRequest>();
        }
    }

    public List<StayChangeRequest> getHostRequests(int hostId, String status) {
        if (!Arrays.asList("Pending", "Accepted", "Rejected", "Cancelled")
                .contains(status)) {
            status = null;
        }
        try {
            return repository.findByHostId(hostId, status);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<StayChangeRequest>();
        }
    }

    public StayChangeRequest getHostRequest(int requestId, int hostId) {
        try {
            return repository.findByIdAndHostId(requestId, hostId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public boolean cancelRequest(int requestId, int customerId) {
        try {
            return repository.cancelByCustomer(requestId, customerId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean rejectRequest(int requestId, int hostId,
            String responseNote) {
        if (ValidationUtil.isBlank(responseNote)) {
            throw new IllegalArgumentException("Vui lòng nhập lý do từ chối.");
        }
        responseNote = responseNote.trim();
        if (responseNote.length() > 255) {
            throw new IllegalArgumentException(
                    "Phản hồi không được vượt quá 255 ký tự."
            );
        }
        try {
            return repository.reject(requestId, hostId, responseNote);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean acceptRequest(int requestId, int hostId) {
        StayChangeRequest request = getHostRequest(requestId, hostId);
        if (request == null || !"Pending".equals(request.getStatus())) {
            throw new IllegalArgumentException("Yêu cầu không còn hợp lệ.");
        }

        List<BookingNight> extensionNights
                = new ArrayList<BookingNight>();
        if ("Extension".equals(request.getRequestType())) {
            BookingQuote quote = new BookingService().createQuote(
                    request.getHomestayId(),
                    request.getOriginalCheckOutDate(),
                    request.getRequestedCheckOutDate(),
                    1,
                    null
            );
            extensionNights = quote.getNights();
        }

        try {
            return repository.accept(requestId, hostId, extensionNights);
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new IllegalStateException(
                    "Không thể chấp nhận yêu cầu: "
                    + exception.getMessage(), exception
            );
        }
    }

    public boolean completeRefund(int requestId, int hostId) {
        try {
            return repository.completeRefund(requestId, hostId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private void prepareExtension(StayChangeRequest request,
            Booking booking) {
        LocalDate requested = request.getRequestedCheckOutDate();
        if (requested == null || !requested.isAfter(booking.getCheckOutDate())) {
            throw new IllegalArgumentException(
                    "Ngày trả phòng mới phải sau ngày trả phòng hiện tại."
            );
        }
        if (ChronoUnit.DAYS.between(booking.getCheckOutDate(), requested) > 30) {
            throw new IllegalArgumentException("Chỉ được gia hạn tối đa 30 ngày.");
        }

        BookingQuote quote = new BookingService().createQuote(
                booking.getHomestayId(),
                booking.getCheckOutDate(),
                requested,
                booking.getTotalGuests(),
                null
        );
        request.setExtraAmount(quote.getOriginalAmount());
        request.setRefundAmount(BigDecimal.ZERO);
    }

    private void prepareEarlyCheckout(StayChangeRequest request,
            Booking booking) {
        LocalDate requested = request.getRequestedCheckOutDate();
        if (requested == null
                || !requested.isAfter(booking.getCheckInDate())
                || !requested.isBefore(booking.getCheckOutDate())
                || requested.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Ngày trả phòng sớm không hợp lệ."
            );
        }

        try {
            BigDecimal unusedAmount = repository.sumNightPrices(
                    booking.getBookingId(),
                    requested,
                    booking.getCheckOutDate()
            );
            BigDecimal percent = booking.getPartialRefundPercentSnapshot();
            BigDecimal refund = percent == null
                    ? BigDecimal.ZERO
                    : unusedAmount.multiply(percent).divide(
                            BigDecimal.valueOf(100),
                            2,
                            RoundingMode.HALF_UP
                    );
            request.setExtraAmount(BigDecimal.ZERO);
            request.setRefundAmount(refund);
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new IllegalStateException("Không thể tính tiền hoàn.");
        }
    }

    private void validateNote(String note) {
        if (note != null && note.trim().length() > 255) {
            throw new IllegalArgumentException(
                    "Ghi chú không được vượt quá 255 ký tự."
            );
        }
    }

    private void setRefundAccount(StayChangeRequest request,
            String requestType, String accountName, String bankName,
            String accountNumber) {
        if (!"EarlyCheckout".equals(requestType)) {
            return;
        }
        if (ValidationUtil.isBlank(accountName)
                || ValidationUtil.isBlank(bankName)
                || ValidationUtil.isBlank(accountNumber)) {
            throw new IllegalArgumentException(
                    "Vui lòng nhập đầy đủ thông tin tài khoản nhận hoàn tiền."
            );
        }
        accountName = accountName.trim();
        bankName = bankName.trim();
        accountNumber = accountNumber.trim();
        if (accountName.length() > 100 || bankName.length() > 100
                || accountNumber.length() > 50
                || !accountNumber.matches("[0-9]+")) {
            throw new IllegalArgumentException(
                    "Thông tin tài khoản hoàn tiền không hợp lệ."
            );
        }
        request.setRefundAccountName(accountName);
        request.setRefundBankName(bankName);
        request.setRefundAccountNumber(accountNumber);
    }
}
