/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import entity.Booking;
import entity.Review;
import interfaces.IReviewRepository;
import repository.ReviewRepository;
import ultis.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class ReviewService {

    private final IReviewRepository reviewRepository;

    public ReviewService() {
        reviewRepository = new ReviewRepository();
    }

    public Booking getReviewableBooking(int bookingId, int customerId) {
        try {
            return reviewRepository.findReviewableBooking(
                    bookingId, customerId
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public boolean canReview(int bookingId, int customerId) {
        return getReviewableBooking(bookingId, customerId) != null;
    }

    public int createReview(int bookingId, int customerId,
            int ratingStars, String comment,
            List<String> imageUrls) {
        if (ratingStars < 1 || ratingStars > 5) {
            throw new IllegalArgumentException(
                    "Số sao đánh giá phải từ 1 đến 5."
            );
        }
        if (comment != null) {
            comment = comment.trim();
        }
        if (ValidationUtil.isBlank(comment)) {
            comment = null;
        }

        Booking booking = getReviewableBooking(bookingId, customerId);
        if (booking == null) {
            throw new IllegalArgumentException(
                    "Booking không thể đánh giá hoặc đã được đánh giá."
            );
        }

        Review review = new Review();
        review.setBookingId(bookingId);
        review.setCustomerId(customerId);
        review.setHomestayId(booking.getHomestayId());
        review.setRatingStars(ratingStars);
        review.setComment(comment);
        review.setVisible(true);

        try {
            return reviewRepository.createReview(review, imageUrls);
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new IllegalStateException("Không thể lưu đánh giá.");
        }
    }
}
