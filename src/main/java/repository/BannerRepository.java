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
import entity.Banner;
import interfaces.IBannerRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BannerRepository extends DBContext implements IBannerRepository {

    public BannerRepository() {
        super();
    }

    @Override
    public List<Banner> findAll() throws SQLException {
        return query("SELECT BannerID, Title, ImageURL, TargetURL, "
                + "DisplayOrder, IsActive, CreatedAt FROM Banners "
                + "ORDER BY DisplayOrder, BannerID");
    }

    @Override
    public List<Banner> findActive() throws SQLException {
        return query("SELECT BannerID, Title, ImageURL, TargetURL, "
                + "DisplayOrder, IsActive, CreatedAt FROM Banners "
                + "WHERE IsActive = 1 ORDER BY DisplayOrder, BannerID");
    }

    @Override
    public Banner findById(int bannerId) throws SQLException {
        String sql = "SELECT BannerID, Title, ImageURL, TargetURL, "
                + "DisplayOrder, IsActive, CreatedAt "
                + "FROM Banners WHERE BannerID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bannerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapBanner(resultSet) : null;
            }
        }
    }

    @Override
    public int create(Banner banner) throws SQLException {
        String sql = "INSERT INTO Banners "
                + "(Title, ImageURL, TargetURL, DisplayOrder, IsActive) "
                + "VALUES (?, ?, ?, ?, ?)";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(statement, banner);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    @Override
    public boolean update(Banner banner) throws SQLException {
        String sql = "UPDATE Banners SET Title = ?, ImageURL = ?, "
                + "TargetURL = ?, DisplayOrder = ?, IsActive = ? "
                + "WHERE BannerID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, banner);
            statement.setInt(6, banner.getBannerId());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean setActive(int bannerId, boolean active)
            throws SQLException {
        String sql = "UPDATE Banners SET IsActive = ? WHERE BannerID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, active);
            statement.setInt(2, bannerId);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int bannerId) throws SQLException {
        String sql = "DELETE FROM Banners WHERE BannerID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bannerId);
            return statement.executeUpdate() > 0;
        }
    }

    private List<Banner> query(String sql) throws SQLException {
        List<Banner> banners = new ArrayList<Banner>();
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                banners.add(mapBanner(resultSet));
            }
        }
        return banners;
    }

    private void setParameters(PreparedStatement statement, Banner banner)
            throws SQLException {
        statement.setString(1, banner.getTitle());
        statement.setString(2, banner.getImageUrl());
        statement.setString(3, banner.getTargetUrl());
        statement.setInt(4, banner.getDisplayOrder());
        statement.setBoolean(5, banner.isActive());
    }

    private Banner mapBanner(ResultSet resultSet) throws SQLException {
        Banner banner = new Banner();
        banner.setBannerId(resultSet.getInt("BannerID"));
        banner.setTitle(resultSet.getString("Title"));
        banner.setImageUrl(resultSet.getString("ImageURL"));
        banner.setTargetUrl(resultSet.getString("TargetURL"));
        banner.setDisplayOrder(resultSet.getInt("DisplayOrder"));
        banner.setActive(resultSet.getBoolean("IsActive"));
        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        if (createdAt != null) {
            banner.setCreatedAt(createdAt.toLocalDateTime());
        }
        return banner;
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
