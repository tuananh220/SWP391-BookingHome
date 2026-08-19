package repository;

import dal.DBContext;
import entity.Amenity;
import interfaces.IHostAmenityRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HostAmenityRepository extends DBContext
        implements IHostAmenityRepository {

    public HostAmenityRepository() {
        super();
    }

    @Override
    public List<Amenity> findAll() throws SQLException {
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
    public Amenity findById(int amenityId) throws SQLException {
        String sql = "SELECT AmenityID, AmenityName, IconClass "
                + "FROM Amenities WHERE AmenityID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, amenityId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapAmenity(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public boolean existsName(String amenityName, Integer excludeId)
            throws SQLException {
        String sql = "SELECT 1 FROM Amenities WHERE LOWER(AmenityName) = LOWER(?)";
        if (excludeId != null) {
            sql += " AND AmenityID <> ?";
        }
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, amenityName);
            if (excludeId != null) {
                statement.setInt(2, excludeId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public int create(Amenity amenity) throws SQLException {
        String sql = "INSERT INTO Amenities (AmenityName, IconClass) "
                + "VALUES (?, ?)";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, amenity.getAmenityName());
            statement.setString(2, amenity.getIconClass());
            if (statement.executeUpdate() == 0) {
                return 0;
            }
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public boolean update(Amenity amenity) throws SQLException {
        String sql = "UPDATE Amenities SET AmenityName = ?, IconClass = ? "
                + "WHERE AmenityID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, amenity.getAmenityName());
            statement.setString(2, amenity.getIconClass());
            statement.setInt(3, amenity.getAmenityId());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean isUsed(int amenityId) throws SQLException {
        String sql = "SELECT 1 FROM HomestayAmenities WHERE AmenityID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, amenityId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public boolean delete(int amenityId) throws SQLException {
        String sql = "DELETE FROM Amenities WHERE AmenityID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, amenityId);
            return statement.executeUpdate() > 0;
        }
    }

    private Amenity mapAmenity(ResultSet resultSet) throws SQLException {
        Amenity amenity = new Amenity();
        amenity.setAmenityId(resultSet.getInt("AmenityID"));
        amenity.setAmenityName(resultSet.getString("AmenityName"));
        amenity.setIconClass(resultSet.getString("IconClass"));
        return amenity;
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
