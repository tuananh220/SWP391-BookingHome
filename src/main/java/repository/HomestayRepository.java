/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import dal.DBContext;
import entity.Amenity;
import entity.CancellationPolicy;
import entity.Homestay;
import entity.HomestayImage;
import entity.HomestaySearchCriteria;
import entity.Review;
import interfaces.IHomestayRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class HomestayRepository extends DBContext
        implements IHomestayRepository {

    public HomestayRepository() {
        super();
    }

    @Override
    public List<Homestay> search(HomestaySearchCriteria criteria)
            throws SQLException {
        List<Homestay> homestays = new ArrayList<Homestay>();
        List<Object> parameters = new ArrayList<Object>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT h.HomestayID, h.HostID, h.CancellationPolicyID, ");
        sql.append("h.Title, h.Description, h.Address, h.City, h.District, ");
        sql.append("h.Latitude, h.Longitude, h.PricePerNight, h.MaxGuests, ");
        sql.append("h.Status, h.CreatedAt, h.UpdatedAt, u.FullName AS HostName, ");
        sql.append("(SELECT TOP 1 hi.ImageURL FROM HomestayImages hi ");
        sql.append(" WHERE hi.HomestayID = h.HomestayID ");
        sql.append(" ORDER BY hi.IsPrimary DESC, hi.ImageID) AS PrimaryImageURL, ");
        sql.append("COALESCE((SELECT AVG(CAST(rv.RatingStars AS FLOAT)) ");
        sql.append(" FROM Reviews rv WHERE rv.HomestayID = h.HomestayID ");
        sql.append(" AND rv.IsVisible = 1), 0) AS AverageRating, ");
        sql.append("(SELECT COUNT(*) FROM Reviews rv ");
        sql.append(" WHERE rv.HomestayID = h.HomestayID ");
        sql.append(" AND rv.IsVisible = 1) AS ReviewCount ");
        sql.append("FROM Homestays h ");
        sql.append("INNER JOIN Users u ON u.UserID = h.HostID ");
        sql.append("WHERE h.Status = 'Active' ");

        if (!isBlank(criteria.getKeyword())) {
            sql.append("AND (h.Title LIKE ? OR h.Description LIKE ? ");
            sql.append("OR h.Address LIKE ? OR h.City LIKE ?) ");
            String keyword = "%" + criteria.getKeyword().trim() + "%";
            parameters.add(keyword);
            parameters.add(keyword);
            parameters.add(keyword);
            parameters.add(keyword);
        }

        if (!isBlank(criteria.getCity())) {
            sql.append("AND h.City = ? ");
            parameters.add(criteria.getCity().trim());
        }

        if (criteria.getMinPrice() != null) {
            sql.append("AND h.PricePerNight >= ? ");
            parameters.add(criteria.getMinPrice());
        }

        if (criteria.getMaxPrice() != null) {
            sql.append("AND h.PricePerNight <= ? ");
            parameters.add(criteria.getMaxPrice());
        }

        if (criteria.getGuests() != null) {
            sql.append("AND h.MaxGuests >= ? ");
            parameters.add(criteria.getGuests());
        }

        if (criteria.getAmenityId() != null) {
            sql.append("AND EXISTS (SELECT 1 FROM HomestayAmenities ha ");
            sql.append("WHERE ha.HomestayID = h.HomestayID ");
            sql.append("AND ha.AmenityID = ?) ");
            parameters.add(criteria.getAmenityId());
        }

        if (criteria.getMinRating() != null) {
            sql.append("AND COALESCE((SELECT AVG(CAST(r2.RatingStars AS FLOAT)) ");
            sql.append("FROM Reviews r2 WHERE r2.HomestayID = h.HomestayID ");
            sql.append("AND r2.IsVisible = 1), 0) >= ? ");
            parameters.add(criteria.getMinRating());
        }

        if (criteria.getCheckInDate() != null
                && criteria.getCheckOutDate() != null) {
            sql.append("AND NOT EXISTS (SELECT 1 FROM BookingNights bn ");
            sql.append("WHERE bn.HomestayID = h.HomestayID ");
            sql.append("AND bn.IsActive = 1 AND bn.StayDate >= ? ");
            sql.append("AND bn.StayDate < ?) ");
            parameters.add(Date.valueOf(criteria.getCheckInDate()));
            parameters.add(Date.valueOf(criteria.getCheckOutDate()));

            sql.append("AND NOT EXISTS (SELECT 1 FROM HomestaySchedules hs ");
            sql.append("WHERE hs.HomestayID = h.HomestayID ");
            sql.append("AND hs.IsAvailable = 0 AND hs.ScheduleDate >= ? ");
            sql.append("AND hs.ScheduleDate < ?) ");
            parameters.add(Date.valueOf(criteria.getCheckInDate()));
            parameters.add(Date.valueOf(criteria.getCheckOutDate()));
        }

        sql.append("ORDER BY h.CreatedAt DESC");

        ensureConnection();
        try (PreparedStatement statement =
                     connection.prepareStatement(sql.toString())) {
            setParameters(statement, parameters);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    homestays.add(mapHomestay(resultSet));
                }
            }
        }
        return homestays;
    }

    @Override
    public Homestay findActiveById(int homestayId) throws SQLException {
        String sql = "SELECT h.HomestayID, h.HostID, "
                + "h.CancellationPolicyID, h.Title, h.Description, "
                + "h.Address, h.City, h.District, h.Latitude, h.Longitude, "
                + "h.PricePerNight, h.MaxGuests, h.Status, h.CreatedAt, "
                + "h.UpdatedAt, u.FullName AS HostName, "
                + "(SELECT TOP 1 hi.ImageURL FROM HomestayImages hi "
                + "WHERE hi.HomestayID = h.HomestayID "
                + "ORDER BY hi.IsPrimary DESC, hi.ImageID) AS PrimaryImageURL, "
                + "COALESCE((SELECT AVG(CAST(rv.RatingStars AS FLOAT)) "
                + "FROM Reviews rv WHERE rv.HomestayID = h.HomestayID "
                + "AND rv.IsVisible = 1), 0) AS AverageRating, "
                + "(SELECT COUNT(*) FROM Reviews rv "
                + "WHERE rv.HomestayID = h.HomestayID "
                + "AND rv.IsVisible = 1) AS ReviewCount, "
                + "cp.PolicyID, cp.PolicyName, cp.Description AS PolicyDescription, "
                + "cp.FullRefundDays, cp.PartialRefundDays, "
                + "cp.PartialRefundPercent, cp.IsActive AS PolicyActive "
                + "FROM Homestays h "
                + "INNER JOIN Users u ON u.UserID = h.HostID "
                + "LEFT JOIN CancellationPolicies cp "
                + "ON cp.PolicyID = h.CancellationPolicyID "
                + "WHERE h.HomestayID = ? AND h.Status = 'Active'";

        ensureConnection();
        Homestay homestay = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    homestay = mapHomestay(resultSet);
                    mapPolicy(resultSet, homestay);
                }
            }
        }

        if (homestay != null) {
            homestay.setImages(findImagesByHomestayId(homestayId));
            homestay.setAmenities(findAmenitiesByHomestayId(homestayId));
            homestay.setReviews(findReviewsByHomestayId(homestayId));
        }
        return homestay;
    }

    @Override
    public List<Amenity> findAllAmenities() throws SQLException {
        List<Amenity> amenities = new ArrayList<Amenity>();
        String sql = "SELECT AmenityID, AmenityName, IconClass "
                + "FROM Amenities ORDER BY AmenityName";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                amenities.add(mapAmenity(resultSet));
            }
        }
        return amenities;
    }

    @Override
    public List<String> findActiveCities() throws SQLException {
        List<String> cities = new ArrayList<String>();
        String sql = "SELECT DISTINCT City FROM Homestays "
                + "WHERE Status = 'Active' ORDER BY City";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                cities.add(resultSet.getString("City"));
            }
        }
        return cities;
    }

    private List<HomestayImage> findImagesByHomestayId(int homestayId)
            throws SQLException {
        List<HomestayImage> images = new ArrayList<HomestayImage>();
        String sql = "SELECT ImageID, HomestayID, ImageURL, IsPrimary, CreatedAt "
                + "FROM HomestayImages WHERE HomestayID = ? "
                + "ORDER BY IsPrimary DESC, ImageID";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    HomestayImage image = new HomestayImage();
                    image.setImageId(resultSet.getInt("ImageID"));
                    image.setHomestayId(resultSet.getInt("HomestayID"));
                    image.setImageUrl(resultSet.getString("ImageURL"));
                    image.setPrimary(resultSet.getBoolean("IsPrimary"));
                    Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
                    if (createdAt != null) {
                        image.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    images.add(image);
                }
            }
        }
        return images;
    }

    private List<Amenity> findAmenitiesByHomestayId(int homestayId)
            throws SQLException {
        List<Amenity> amenities = new ArrayList<Amenity>();
        String sql = "SELECT a.AmenityID, a.AmenityName, a.IconClass "
                + "FROM Amenities a INNER JOIN HomestayAmenities ha "
                + "ON ha.AmenityID = a.AmenityID "
                + "WHERE ha.HomestayID = ? ORDER BY a.AmenityName";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    amenities.add(mapAmenity(resultSet));
                }
            }
        }
        return amenities;
    }

    private List<Review> findReviewsByHomestayId(int homestayId)
            throws SQLException {
        List<Review> reviews = new ArrayList<Review>();
        String sql = "SELECT r.ReviewID, r.BookingID, r.CustomerID, "
                + "r.HomestayID, r.RatingStars, r.Comment, r.HostResponse, "
                + "r.IsVisible, r.CreatedAt, u.FullName AS CustomerName "
                + "FROM Reviews r INNER JOIN Users u "
                + "ON u.UserID = r.CustomerID "
                + "WHERE r.HomestayID = ? AND r.IsVisible = 1 "
                + "ORDER BY r.CreatedAt DESC";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Review review = new Review();
                    review.setReviewId(resultSet.getInt("ReviewID"));
                    review.setBookingId(resultSet.getInt("BookingID"));
                    review.setCustomerId(resultSet.getInt("CustomerID"));
                    review.setCustomerName(resultSet.getString("CustomerName"));
                    review.setHomestayId(resultSet.getInt("HomestayID"));
                    review.setRatingStars(resultSet.getInt("RatingStars"));
                    review.setComment(resultSet.getString("Comment"));
                    review.setHostResponse(resultSet.getString("HostResponse"));
                    review.setVisible(resultSet.getBoolean("IsVisible"));
                    Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
                    if (createdAt != null) {
                        review.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    reviews.add(review);
                }
            }
        }

        for (Review review : reviews) {
            review.setImageUrls(findReviewImageUrls(review.getReviewId()));
        }
        return reviews;
    }

    private List<String> findReviewImageUrls(int reviewId)
            throws SQLException {
        List<String> imageUrls = new ArrayList<String>();
        String sql = "SELECT ImageURL FROM ReviewImages "
                + "WHERE ReviewID = ? ORDER BY ReviewImageID";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reviewId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    imageUrls.add(resultSet.getString("ImageURL"));
                }
            }
        }
        return imageUrls;
    }

    private Homestay mapHomestay(ResultSet resultSet) throws SQLException {
        Homestay homestay = new Homestay();
        homestay.setHomestayId(resultSet.getInt("HomestayID"));
        homestay.setHostId(resultSet.getInt("HostID"));
        homestay.setHostName(resultSet.getString("HostName"));

        int policyId = resultSet.getInt("CancellationPolicyID");
        if (!resultSet.wasNull()) {
            homestay.setCancellationPolicyId(policyId);
        }

        homestay.setTitle(resultSet.getString("Title"));
        homestay.setDescription(resultSet.getString("Description"));
        homestay.setAddress(resultSet.getString("Address"));
        homestay.setCity(resultSet.getString("City"));
        homestay.setDistrict(resultSet.getString("District"));
        homestay.setLatitude(resultSet.getBigDecimal("Latitude"));
        homestay.setLongitude(resultSet.getBigDecimal("Longitude"));
        homestay.setPricePerNight(resultSet.getBigDecimal("PricePerNight"));
        homestay.setMaxGuests(resultSet.getInt("MaxGuests"));
        homestay.setStatus(resultSet.getString("Status"));
        homestay.setPrimaryImageUrl(resultSet.getString("PrimaryImageURL"));
        homestay.setAverageRating(resultSet.getDouble("AverageRating"));
        homestay.setReviewCount(resultSet.getInt("ReviewCount"));

        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        Timestamp updatedAt = resultSet.getTimestamp("UpdatedAt");
        if (createdAt != null) {
            homestay.setCreatedAt(createdAt.toLocalDateTime());
        }
        if (updatedAt != null) {
            homestay.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return homestay;
    }

    private void mapPolicy(ResultSet resultSet, Homestay homestay)
            throws SQLException {
        int policyId = resultSet.getInt("PolicyID");
        if (resultSet.wasNull()) {
            return;
        }

        CancellationPolicy policy = new CancellationPolicy();
        policy.setPolicyId(policyId);
        policy.setPolicyName(resultSet.getString("PolicyName"));
        policy.setDescription(resultSet.getString("PolicyDescription"));
        policy.setFullRefundDays(resultSet.getInt("FullRefundDays"));
        policy.setPartialRefundDays(resultSet.getInt("PartialRefundDays"));
        policy.setPartialRefundPercent(
                resultSet.getDouble("PartialRefundPercent")
        );
        policy.setActive(resultSet.getBoolean("PolicyActive"));
        homestay.setCancellationPolicy(policy);
    }

    private Amenity mapAmenity(ResultSet resultSet) throws SQLException {
        Amenity amenity = new Amenity();
        amenity.setAmenityId(resultSet.getInt("AmenityID"));
        amenity.setAmenityName(resultSet.getString("AmenityName"));
        amenity.setIconClass(resultSet.getString("IconClass"));
        return amenity;
    }

    private void setParameters(PreparedStatement statement,
                               List<Object> parameters) throws SQLException {
        for (int index = 0; index < parameters.size(); index++) {
            Object value = parameters.get(index);
            int parameterIndex = index + 1;

            if (value instanceof BigDecimal) {
                statement.setBigDecimal(parameterIndex, (BigDecimal) value);
            } else if (value instanceof Integer) {
                statement.setInt(parameterIndex, (Integer) value);
            } else if (value instanceof Date) {
                statement.setDate(parameterIndex, (Date) value);
            } else {
                statement.setString(parameterIndex, String.valueOf(value));
            }
        }
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

