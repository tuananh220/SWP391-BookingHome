/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Booking {

    private int bookingId;
    private int customerId;
    private int homestayId;
    private Integer voucherId;
    private String voucherCode;
    private Integer cancellationPolicyId;
    private String cancellationPolicyName;
    private Integer fullRefundDaysSnapshot;
    private Integer partialRefundDaysSnapshot;
    private BigDecimal partialRefundPercentSnapshot;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int totalGuests;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String bookingStatus;
    private String note;
    private String cancelReason;
    private String rejectReason;
    private String cancelledBy;
    private BigDecimal refundAmount;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;

    // Fields used when displaying booking history/detail
    private String homestayTitle;
    private String homestayImageUrl;
    private String paymentStatus;
    private String paymentMethodName;
    private boolean paymentOnline;
    private String customerName;
    private String customerPhone;

    public Booking() {
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getHomestayId() {
        return homestayId;
    }

    public void setHomestayId(int homestayId) {
        this.homestayId = homestayId;
    }

    public Integer getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Integer voucherId) {
        this.voucherId = voucherId;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public Integer getCancellationPolicyId() {
        return cancellationPolicyId;
    }

    public void setCancellationPolicyId(Integer cancellationPolicyId) {
        this.cancellationPolicyId = cancellationPolicyId;
    }

    public String getCancellationPolicyName() {
        return cancellationPolicyName;
    }

    public void setCancellationPolicyName(String cancellationPolicyName) {
        this.cancellationPolicyName = cancellationPolicyName;
    }

    public Integer getFullRefundDaysSnapshot() {
        return fullRefundDaysSnapshot;
    }

    public void setFullRefundDaysSnapshot(Integer fullRefundDaysSnapshot) {
        this.fullRefundDaysSnapshot = fullRefundDaysSnapshot;
    }

    public Integer getPartialRefundDaysSnapshot() {
        return partialRefundDaysSnapshot;
    }

    public void setPartialRefundDaysSnapshot(Integer partialRefundDaysSnapshot) {
        this.partialRefundDaysSnapshot = partialRefundDaysSnapshot;
    }

    public BigDecimal getPartialRefundPercentSnapshot() {
        return partialRefundPercentSnapshot;
    }

    public void setPartialRefundPercentSnapshot(BigDecimal partialRefundPercentSnapshot) {
        this.partialRefundPercentSnapshot = partialRefundPercentSnapshot;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getTotalGuests() {
        return totalGuests;
    }

    public void setTotalGuests(int totalGuests) {
        this.totalGuests = totalGuests;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getHomestayTitle() {
        return homestayTitle;
    }

    public void setHomestayTitle(String homestayTitle) {
        this.homestayTitle = homestayTitle;
    }

    public String getHomestayImageUrl() {
        return homestayImageUrl;
    }

    public void setHomestayImageUrl(String homestayImageUrl) {
        this.homestayImageUrl = homestayImageUrl;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    public boolean isPaymentOnline() {
        return paymentOnline;
    }

    public void setPaymentOnline(boolean paymentOnline) {
        this.paymentOnline = paymentOnline;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
}

