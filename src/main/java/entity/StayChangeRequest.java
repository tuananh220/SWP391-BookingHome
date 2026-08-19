/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Admin
 */
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StayChangeRequest {

    private int requestId;
    private int bookingId;
    private int customerId;
    private String customerName;
    private int homestayId;
    private String homestayTitle;
    private String requestType;
    private LocalDate originalCheckOutDate;
    private LocalDate requestedCheckOutDate;
    private BigDecimal extraAmount = BigDecimal.ZERO;
    private BigDecimal refundAmount = BigDecimal.ZERO;
    private String status;
    private String customerNote;
    private String responseNote;
    private Integer respondedBy;
    private LocalDateTime respondedAt;
    private LocalDateTime createdAt;

    public StayChangeRequest() {
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getHomestayId() {
        return homestayId;
    }

    public void setHomestayId(int homestayId) {
        this.homestayId = homestayId;
    }

    public String getHomestayTitle() {
        return homestayTitle;
    }

    public void setHomestayTitle(String homestayTitle) {
        this.homestayTitle = homestayTitle;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public LocalDate getOriginalCheckOutDate() {
        return originalCheckOutDate;
    }

    public void setOriginalCheckOutDate(LocalDate originalCheckOutDate) {
        this.originalCheckOutDate = originalCheckOutDate;
    }

    public LocalDate getRequestedCheckOutDate() {
        return requestedCheckOutDate;
    }

    public void setRequestedCheckOutDate(LocalDate requestedCheckOutDate) {
        this.requestedCheckOutDate = requestedCheckOutDate;
    }

    public BigDecimal getExtraAmount() {
        return extraAmount;
    }

    public void setExtraAmount(BigDecimal extraAmount) {
        this.extraAmount = extraAmount;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerNote() {
        return customerNote;
    }

    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }

    public String getResponseNote() {
        return responseNote;
    }

    public void setResponseNote(String responseNote) {
        this.responseNote = responseNote;
    }

    public Integer getRespondedBy() {
        return respondedBy;
    }

    public void setRespondedBy(Integer respondedBy) {
        this.respondedBy = respondedBy;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
