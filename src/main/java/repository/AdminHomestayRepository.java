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
import entity.Amenity;
import entity.CancellationPolicy;
import entity.Homestay;
import entity.HomestayImage;
import interfaces.IAdminHomestayRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AdminHomestayRepository extends DBContext
        implements IAdminHomestayRepository {

    public AdminHomestayRepository() {
        super();
    }

    @Override
    public List<Homestay> findAll(String keyword, String status)
            throws SQLException {
        List<Homestay> homestays = new ArrayList<Homestay>();
        List<String> parameters = new ArrayList<String>();
        StringBuilder sql = new StringBuilder(homestaySelect());
        sql.append(" WHERE 1 = 1 ");
        if (!isBlank(keyword)) {
            sql.append("AND (h.Title LIKE ? OR h.Address LIKE ? ");
            sql.append("OR h.City LIKE ? OR host.FullName LIKE ?) ");
            String value = "%" + keyword.trim() + "%";
            parameters.add(value);
            parameters.add(value);
            parameters.add(value);
            parameters.add(value);
        }
        if (!isBlank(status)) {
            sql.append("AND h.Status = ? ");
            parameters.add(status);
        }
        sql.append("ORDER BY CASE WHEN h.Status = 'Pending' THEN 0 ELSE 1 END, ");
        sql.append("h.CreatedAt DESC");

        ensureConnection();
        try (PreparedStatement statement
                = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setString(i + 1, parameters.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    homestays.add(mapHomestay(resultSet));
                }
            }
        }
        return homestays;
    }

    @Override
    public Homestay findById(int homestayId) throws SQLException {
        String sql = homestaySelect() + " WHERE h.HomestayID = ?";
        ensureConnection();
        Homestay homestay = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    homestay = mapHomestay(resultSet);
                }
            }
        }
        if (homestay != null) {
            homestay.setImages(findImages(homestayId));
            homestay.setAmenities(findAmenities(homestayId));
        }
        return homestay;
    }

    @Override
    public List<CancellationPolicy> findPolicies() throws SQLException {
        List<CancellationPolicy> policies
                = new ArrayList<CancellationPolicy>();
        String sql = "SELECT PolicyID, PolicyName, Description, "
                + "FullRefundDays, PartialRefundDays, "
                + "PartialRefundPercent, IsActive "
                + "FROM CancellationPolicies ORDER BY IsActive DESC, PolicyID";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                CancellationPolicy policy = new CancellationPolicy();
                policy.setPolicyId(resultSet.getInt("PolicyID"));
                policy.setPolicyName(resultSet.getString("PolicyName"));
                policy.setDescription(resultSet.getString("Description"));
                policy.setFullRefundDays(resultSet.getInt("FullRefundDays"));
                policy.setPartialRefundDays(resultSet.getInt("PartialRefundDays"));
                policy.setPartialRefundPercent(resultSet.getDouble("PartialRefundPercent"));
                policy.setActive(resultSet.getBoolean("IsActive"));
                policies.add(policy);
            }
        }
        return policies;
    }

    @Override
    public boolean update(Homestay homestay) throws SQLException {
        String sql = "UPDATE Homestays SET CancellationPolicyID = ?, "
                + "Title = ?, Description = ?, Address = ?, City = ?, "
                + "District = ?, Latitude = ?, Longitude = ?, "
                + "PricePerNight = ?, MaxGuests = ?, UpdatedAt = SYSDATETIME() "
                + "WHERE HomestayID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (homestay.getCancellationPolicyId() == null) {
                statement.setNull(1, Types.INTEGER);
            } else {
                statement.setInt(1, homestay.getCancellationPolicyId());
            }
            statement.setString(2, homestay.getTitle());
            statement.setString(3, homestay.getDescription());
            statement.setString(4, homestay.getAddress());
            statement.setString(5, homestay.getCity());
            statement.setString(6, homestay.getDistrict());
            statement.setBigDecimal(7, homestay.getLatitude());
            statement.setBigDecimal(8, homestay.getLongitude());
            statement.setBigDecimal(9, homestay.getPricePerNight());
            statement.setInt(10, homestay.getMaxGuests());
            statement.setInt(11, homestay.getHomestayId());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(int homestayId, String status, int adminId,
            String reason) throws SQLException {
        ensureConnection();
        boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            Integer hostId = null;
            String findSql = "SELECT HostID FROM Homestays "
                    + "WITH (UPDLOCK) WHERE HomestayID = ?";
            try (PreparedStatement statement = connection.prepareStatement(findSql)) {
                statement.setInt(1, homestayId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        hostId = resultSet.getInt("HostID");
                    }
                }
            }
            if (hostId == null) {
                connection.rollback();
                return false;
            }

            String updateSql = "UPDATE Homestays SET Status = ?, "
                    + "UpdatedAt = SYSDATETIME() WHERE HomestayID = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
                statement.setString(1, status);
                statement.setInt(2, homestayId);
                statement.executeUpdate();
            }

            String title = "Cập nhật trạng thái homestay";
            String message = "Homestay của bạn đã chuyển sang trạng thái " + status;
            if (!isBlank(reason)) {
                message += ". Lý do: " + reason;
            }
            createNotification(hostId, adminId, homestayId, title, message);
            connection.commit();
            return true;
        } catch (SQLException exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    private void createNotification(int userId, int adminId, int homestayId,
            String title, String message)
            throws SQLException {
        String sql = "INSERT INTO Notifications "
                + "(Title, Message, Type, RelatedID, CreatedByID) "
                + "VALUES (?, ?, 'System', ?, ?)";
        int notificationId;
        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, title);
            statement.setString(2, message);
            statement.setInt(3, homestayId);
            statement.setInt(4, adminId);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Không thể tạo thông báo.");
                }
                notificationId = keys.getInt(1);
            }
        }
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO UserNotifications "
                + "(NotificationID, UserID, IsRead) VALUES (?, ?, 0)")) {
            statement.setInt(1, notificationId);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    private List<HomestayImage> findImages(int homestayId) throws SQLException {
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
                    images.add(image);
                }
            }
        }
        return images;
    }

    private List<Amenity> findAmenities(int homestayId) throws SQLException {
        List<Amenity> amenities = new ArrayList<Amenity>();
        String sql = "SELECT a.AmenityID, a.AmenityName, a.IconClass "
                + "FROM Amenities a INNER JOIN HomestayAmenities ha "
                + "ON ha.AmenityID = a.AmenityID WHERE ha.HomestayID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Amenity amenity = new Amenity();
                    amenity.setAmenityId(resultSet.getInt("AmenityID"));
                    amenity.setAmenityName(resultSet.getString("AmenityName"));
                    amenity.setIconClass(resultSet.getString("IconClass"));
                    amenities.add(amenity);
                }
            }
        }
        return amenities;
    }

    private String homestaySelect() {
        return "SELECT h.HomestayID, h.HostID, h.CancellationPolicyID, "
                + "h.Title, h.Description, h.Address, h.City, h.District, "
                + "h.Latitude, h.Longitude, h.PricePerNight, h.MaxGuests, "
                + "h.Status, h.CreatedAt, h.UpdatedAt, host.FullName AS HostName, "
                + "host.Email AS HostEmail, cp.PolicyName, "
                + "cp.Description AS PolicyDescription, cp.FullRefundDays, "
                + "cp.PartialRefundDays, cp.PartialRefundPercent, "
                + "(SELECT TOP 1 hi.ImageURL FROM HomestayImages hi "
                + "WHERE hi.HomestayID = h.HomestayID "
                + "ORDER BY hi.IsPrimary DESC, hi.ImageID) AS PrimaryImageURL, "
                + "COALESCE((SELECT AVG(CAST(r.RatingStars AS FLOAT)) "
                + "FROM Reviews r WHERE r.HomestayID = h.HomestayID), 0) AS AverageRating, "
                + "(SELECT COUNT(*) FROM Reviews r "
                + "WHERE r.HomestayID = h.HomestayID) AS ReviewCount "
                + "FROM Homestays h INNER JOIN Users host "
                + "ON host.UserID = h.HostID LEFT JOIN CancellationPolicies cp "
                + "ON cp.PolicyID = h.CancellationPolicyID";
    }

    private Homestay mapHomestay(ResultSet resultSet) throws SQLException {
        Homestay homestay = new Homestay();
        homestay.setHomestayId(resultSet.getInt("HomestayID"));
        homestay.setHostId(resultSet.getInt("HostID"));
        homestay.setHostName(resultSet.getString("HostName"));
        homestay.setHostEmail(resultSet.getString("HostEmail"));
        int policyId = resultSet.getInt("CancellationPolicyID");
        if (!resultSet.wasNull()) {
            homestay.setCancellationPolicyId(policyId);
            CancellationPolicy policy = new CancellationPolicy();
            policy.setPolicyId(policyId);
            policy.setPolicyName(resultSet.getString("PolicyName"));
            policy.setDescription(resultSet.getString("PolicyDescription"));
            policy.setFullRefundDays(resultSet.getInt("FullRefundDays"));
            policy.setPartialRefundDays(resultSet.getInt("PartialRefundDays"));
            policy.setPartialRefundPercent(resultSet.getDouble("PartialRefundPercent"));
            homestay.setCancellationPolicy(policy);
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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
