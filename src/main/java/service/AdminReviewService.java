/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author Admin
 */
import entity.Review;
import interfaces.IAdminReviewRepository;
import repository.AdminReviewRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminReviewService {

    private final IAdminReviewRepository repository;

    public AdminReviewService() {
        repository = new AdminReviewRepository();
    }

    public List<Review> getReviews(String keyword, String visibility,
            boolean reportedOnly) {
        try {
            return repository.findAll(keyword, visibility, reportedOnly);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Review>();
        }
    }

    public Review getReview(int reviewId) {
        try {
            return repository.findById(reviewId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public boolean update(Review review) {
        if (review.getRatingStars() < 1 || review.getRatingStars() > 5) {
            throw new IllegalArgumentException("Số sao phải từ 1 đến 5.");
        }
        try {
            return repository.update(review);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean setVisibility(int reviewId, boolean visible) {
        try {
            return repository.setVisibility(reviewId, visible);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean delete(int reviewId) {
        try {
            return repository.delete(reviewId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean updateReportStatus(int reportId, String status) {
        if (!Arrays.asList("Reviewed", "Dismissed").contains(status)) {
            throw new IllegalArgumentException(
                    "Trạng thái báo cáo không hợp lệ."
            );
        }
        try {
            return repository.updateReportStatus(reportId, status);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
