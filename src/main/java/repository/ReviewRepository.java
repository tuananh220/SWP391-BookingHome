/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import dal.DBContext;
import entity.Booking;
import entity.Review;
import interfaces.IReviewRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

public class ReviewRepository extends DBContext
        implements IReviewRepository {

    public ReviewRepository() {
        super();
    }

    @Override
    public Booking findReviewableBooking(int bookingId, int customerId)
            throws SQLException {
        String sql = "SELECT b.BookingID, b.CustomerID, b.HomestayID, "
                + "b.CheckInDate, b.CheckOutDate, b.BookingStatus, "
                + "h.Title AS HomestayTitle "
                + "FROM Bookings b "
                + "INNER JOIN Homestays h ON h.HomestayID = b.HomestayID "
                + "WHERE b.BookingID = ? AND b.CustomerID = ? "
                + "AND b.BookingStatus = 'Completed' "
                + "AND NOT EXISTS (SELECT 1 FROM Reviews r "
                + "WHERE r.BookingID = b.BookingID)";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            statement.setInt(2, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Booking booking = new Booking();
                    booking.setBookingId(resultSet.getInt("BookingID"));
                    booking.setCustomerId(resultSet.getInt("CustomerID"));
                    booking.setHomestayId(resultSet.getInt("HomestayID"));
                    booking.setCheckInDate(
                            resultSet.getDate("CheckInDate").toLocalDate()
                    );
                    booking.setCheckOutDate(
                            resultSet.getDate("CheckOutDate").toLocalDate()
                    );
                    booking.setBookingStatus(
                            resultSet.getString("BookingStatus")
                    );
                    booking.setHomestayTitle(
                            resultSet.getString("HomestayTitle")
                    );
                    return booking;
                }
            }
        }
        return null;
    }

    @Override
    public Review findByBookingId(int bookingId) throws SQLException {
        String sql = "SELECT ReviewID, BookingID, CustomerID, HomestayID, "
                + "RatingStars, Comment, HostResponse, IsVisible, CreatedAt "
                + "FROM Reviews WHERE BookingID = ?";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapReview(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public int createReview(Review review, List<String> imageUrls)
            throws SQLException {
        ensureConnection();
        boolean oldAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);

            String sql = "INSERT INTO Reviews "
                    + "(BookingID, CustomerID, HomestayID, RatingStars, "
                    + "Comment, IsVisible) VALUES (?, ?, ?, ?, ?, 1)";
            int reviewId;
            try (PreparedStatement statement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, review.getBookingId());
                statement.setInt(2, review.getCustomerId());
                statement.setInt(3, review.getHomestayId());
                statement.setInt(4, review.getRatingStars());
                statement.setString(5, review.getComment());
                statement.executeUpdate();

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Không thể tạo review.");
                    }
                    reviewId = keys.getInt(1);
                }
            }

            if (imageUrls != null && !imageUrls.isEmpty()) {
                insertImages(reviewId, imageUrls);
            }

            connection.commit();
            return reviewId;
        } catch (SQLException exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    private void insertImages(int reviewId, List<String> imageUrls)
            throws SQLException {
        String sql = "INSERT INTO ReviewImages (ReviewID, ImageURL) "
                + "VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (String imageUrl : imageUrls) {
                statement.setInt(1, reviewId);
                statement.setString(2, imageUrl);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private Review mapReview(ResultSet resultSet) throws SQLException {
        Review review = new Review();
        review.setReviewId(resultSet.getInt("ReviewID"));
        review.setBookingId(resultSet.getInt("BookingID"));
        review.setCustomerId(resultSet.getInt("CustomerID"));
        review.setHomestayId(resultSet.getInt("HomestayID"));
        review.setRatingStars(resultSet.getInt("RatingStars"));
        review.setComment(resultSet.getString("Comment"));
        review.setHostResponse(resultSet.getString("HostResponse"));
        review.setVisible(resultSet.getBoolean("IsVisible"));

        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        if (createdAt != null) {
            review.setCreatedAt(createdAt.toLocalDateTime());
        }
        return review;
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
