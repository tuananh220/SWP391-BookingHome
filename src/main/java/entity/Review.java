/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Review {

    private int reviewId;
    private int bookingId;
    private int customerId;
    private String customerName;
    private String homestayTitle;
    private int homestayId;
    private int ratingStars;
    private String comment;
    private String hostResponse;
    private boolean visible;
    private LocalDateTime createdAt;
    private List<String> imageUrls = new ArrayList<String>();
    private int reportCount;
    private List<ReviewReport> reports = new ArrayList<ReviewReport>();

    public Review() {
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
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

    public String getHomestayTitle() {
        return homestayTitle;
    }

    public void setHomestayTitle(String homestayTitle) {
        this.homestayTitle = homestayTitle;
    }

    public int getHomestayId() {
        return homestayId;
    }

    public void setHomestayId(int homestayId) {
        this.homestayId = homestayId;
    }

    public int getRatingStars() {
        return ratingStars;
    }

    public void setRatingStars(int ratingStars) {
        this.ratingStars = ratingStars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getHostResponse() {
        return hostResponse;
    }

    public void setHostResponse(String hostResponse) {
        this.hostResponse = hostResponse;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public List<ReviewReport> getReports() {
        return reports;
    }

    public void setReports(List<ReviewReport> reports) {
        this.reports = reports;
    }
}
