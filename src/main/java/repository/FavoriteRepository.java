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
import entity.Homestay;
import interfaces.IFavoriteRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FavoriteRepository extends DBContext
        implements IFavoriteRepository {

    public FavoriteRepository() {
        super();
    }

    @Override
    public List<Homestay> findByCustomerId(int customerId)
            throws SQLException {
        List<Homestay> homestays = new ArrayList<Homestay>();
        String sql = "SELECT h.HomestayID, h.HostID, h.Title, h.Address, "
                + "h.City, h.District, h.PricePerNight, h.MaxGuests, "
                + "h.Status, h.CreatedAt, h.UpdatedAt, "
                + "(SELECT TOP 1 hi.ImageURL FROM HomestayImages hi "
                + "WHERE hi.HomestayID = h.HomestayID "
                + "ORDER BY hi.IsPrimary DESC, hi.ImageID) AS PrimaryImageURL, "
                + "COALESCE((SELECT AVG(CAST(r.RatingStars AS FLOAT)) "
                + "FROM Reviews r WHERE r.HomestayID = h.HomestayID "
                + "AND r.IsVisible = 1), 0) AS AverageRating, "
                + "(SELECT COUNT(*) FROM Reviews r "
                + "WHERE r.HomestayID = h.HomestayID "
                + "AND r.IsVisible = 1) AS ReviewCount "
                + "FROM FavoriteHomestays f INNER JOIN Homestays h "
                + "ON h.HomestayID = f.HomestayID "
                + "WHERE f.CustomerID = ? "
                + "ORDER BY f.CreatedAt DESC";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    homestays.add(mapHomestay(resultSet));
                }
            }
        }
        return homestays;
    }

    @Override
    public boolean exists(int customerId, int homestayId)
            throws SQLException {
        String sql = "SELECT 1 FROM FavoriteHomestays "
                + "WHERE CustomerID = ? AND HomestayID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            statement.setInt(2, homestayId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public boolean add(int customerId, int homestayId) throws SQLException {
        String sql = "INSERT INTO FavoriteHomestays "
                + "(CustomerID, HomestayID) "
                + "SELECT ?, h.HomestayID FROM Homestays h "
                + "WHERE h.HomestayID = ? AND h.Status = 'Active' "
                + "AND NOT EXISTS (SELECT 1 FROM FavoriteHomestays f "
                + "WHERE f.CustomerID = ? AND f.HomestayID = h.HomestayID)";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            statement.setInt(2, homestayId);
            statement.setInt(3, customerId);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean remove(int customerId, int homestayId)
            throws SQLException {
        String sql = "DELETE FROM FavoriteHomestays "
                + "WHERE CustomerID = ? AND HomestayID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            statement.setInt(2, homestayId);
            return statement.executeUpdate() > 0;
        }
    }

    private Homestay mapHomestay(ResultSet resultSet) throws SQLException {
        Homestay homestay = new Homestay();
        homestay.setHomestayId(resultSet.getInt("HomestayID"));
        homestay.setHostId(resultSet.getInt("HostID"));
        homestay.setTitle(resultSet.getString("Title"));
        homestay.setAddress(resultSet.getString("Address"));
        homestay.setCity(resultSet.getString("City"));
        homestay.setDistrict(resultSet.getString("District"));
        homestay.setPricePerNight(resultSet.getBigDecimal("PricePerNight"));
        homestay.setMaxGuests(resultSet.getInt("MaxGuests"));
        homestay.setStatus(resultSet.getString("Status"));
        homestay.setPrimaryImageUrl(resultSet.getString("PrimaryImageURL"));
        homestay.setAverageRating(resultSet.getDouble("AverageRating"));
        homestay.setReviewCount(resultSet.getInt("ReviewCount"));
        homestay.setFavorite(true);

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

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
