/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

/**
 *
 * @author Admin
 */
import dal.DBContext;
import entity.Review;
import entity.ReviewReport;
import interfaces.IAdminReviewRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AdminReviewRepository extends DBContext
        implements IAdminReviewRepository {

    public AdminReviewRepository() {
        super();
    }

    @Override
    public List<Review> findAll(String keyword, String visibility,
            boolean reportedOnly) throws SQLException {
        List<Review> reviews = new ArrayList<Review>();
        List<String> parameters = new ArrayList<String>();
        StringBuilder sql = new StringBuilder(reviewSelect());
        sql.append(" WHERE 1 = 1 ");
        if (!isBlank(keyword)) {
            sql.append("AND (u.FullName LIKE ? OR h.Title LIKE ? ");
            sql.append("OR r.Comment LIKE ?) ");
            String value = "%" + keyword.trim() + "%";
            parameters.add(value);
            parameters.add(value);
            parameters.add(value);
        }
        if ("visible".equals(visibility)) {
            sql.append("AND r.IsVisible = 1 ");
        } else if ("hidden".equals(visibility)) {
            sql.append("AND r.IsVisible = 0 ");
        }
        if (reportedOnly) {
            sql.append("AND EXISTS (SELECT 1 FROM ReviewReports rr "
                    + "WHERE rr.ReviewID = r.ReviewID "
                    + "AND rr.Status = 'Pending') ");
        }
        sql.append("ORDER BY ReportCount DESC, r.CreatedAt DESC");

        ensureConnection();
        try (PreparedStatement statement
                = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setString(i + 1, parameters.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reviews.add(mapReview(resultSet));
                }
            }
        }
        return reviews;
    }

    @Override
    public Review findById(int reviewId) throws SQLException {
        String sql = reviewSelect() + " WHERE r.ReviewID = ?";
        ensureConnection();
        Review review = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reviewId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    review = mapReview(resultSet);
                }
            }
        }
        if (review != null) {
            review.setImageUrls(findImages(reviewId));
            review.setReports(findReports(reviewId));
        }
        return review;
    }

    @Override
    public boolean update(Review review) throws SQLException {
        String sql = "UPDATE Reviews SET RatingStars = ?, Comment = ?, "
                + "HostResponse = ?, IsVisible = ?, UpdatedAt = SYSDATETIME() "
                + "WHERE ReviewID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, review.getRatingStars());
            statement.setString(2, review.getComment());
            statement.setString(3, review.getHostResponse());
            statement.setBoolean(4, review.isVisible());
            statement.setInt(5, review.getReviewId());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean setVisibility(int reviewId, boolean visible)
            throws SQLException {
        String sql = "UPDATE Reviews SET IsVisible = ?, "
                + "UpdatedAt = SYSDATETIME() WHERE ReviewID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, visible);
            statement.setInt(2, reviewId);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int reviewId) throws SQLException {
        String sql = "DELETE FROM Reviews WHERE ReviewID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reviewId);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateReportStatus(int reportId, String status)
            throws SQLException {
        String sql = "UPDATE ReviewReports SET Status = ? WHERE ReportID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, reportId);
            return statement.executeUpdate() > 0;
        }
    }

    private List<String> findImages(int reviewId) throws SQLException {
        List<String> images = new ArrayList<String>();
        String sql = "SELECT ImageURL FROM ReviewImages "
                + "WHERE ReviewID = ? ORDER BY ReviewImageID";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reviewId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    images.add(resultSet.getString("ImageURL"));
                }
            }
        }
        return images;
    }

    private List<ReviewReport> findReports(int reviewId) throws SQLException {
        List<ReviewReport> reports = new ArrayList<ReviewReport>();
        String sql = "SELECT rr.ReportID, rr.ReviewID, rr.ReporterID, "
                + "rr.Reason, rr.Status, rr.CreatedAt, "
                + "u.FullName AS ReporterName FROM ReviewReports rr "
                + "INNER JOIN Users u ON u.UserID = rr.ReporterID "
                + "WHERE rr.ReviewID = ? ORDER BY rr.CreatedAt DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reviewId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ReviewReport report = new ReviewReport();
                    report.setReportId(resultSet.getInt("ReportID"));
                    report.setReviewId(resultSet.getInt("ReviewID"));
                    report.setReporterId(resultSet.getInt("ReporterID"));
                    report.setReporterName(resultSet.getString("ReporterName"));
                    report.setReason(resultSet.getString("Reason"));
                    report.setStatus(resultSet.getString("Status"));
                    Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
                    if (createdAt != null) {
                        report.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    reports.add(report);
                }
            }
        }
        return reports;
    }

    private String reviewSelect() {
        return "SELECT r.ReviewID, r.BookingID, r.CustomerID, "
                + "r.HomestayID, r.RatingStars, r.Comment, r.HostResponse, "
                + "r.IsVisible, r.CreatedAt, u.FullName AS CustomerName, "
                + "h.Title AS HomestayTitle, "
                + "(SELECT COUNT(*) FROM ReviewReports rr "
                + "WHERE rr.ReviewID = r.ReviewID "
                + "AND rr.Status = 'Pending') AS ReportCount "
                + "FROM Reviews r INNER JOIN Users u "
                + "ON u.UserID = r.CustomerID INNER JOIN Homestays h "
                + "ON h.HomestayID = r.HomestayID";
    }

    private Review mapReview(ResultSet resultSet) throws SQLException {
        Review review = new Review();
        review.setReviewId(resultSet.getInt("ReviewID"));
        review.setBookingId(resultSet.getInt("BookingID"));
        review.setCustomerId(resultSet.getInt("CustomerID"));
        review.setCustomerName(resultSet.getString("CustomerName"));
        review.setHomestayId(resultSet.getInt("HomestayID"));
        review.setHomestayTitle(resultSet.getString("HomestayTitle"));
        review.setRatingStars(resultSet.getInt("RatingStars"));
        review.setComment(resultSet.getString("Comment"));
        review.setHostResponse(resultSet.getString("HostResponse"));
        review.setVisible(resultSet.getBoolean("IsVisible"));
        review.setReportCount(resultSet.getInt("ReportCount"));
        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        if (createdAt != null) {
            review.setCreatedAt(createdAt.toLocalDateTime());
        }
        return review;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
