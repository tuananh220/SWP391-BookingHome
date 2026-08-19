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
import entity.HomestayForm;
import entity.PaymentMethod;
import interfaces.IHostHomestayRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class HostHomestayRepository extends DBContext
        implements IHostHomestayRepository {

    public HostHomestayRepository() {
        super();
    }

    @Override
    public List<Homestay> findByHostId(int hostId) throws SQLException {
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
                + "FROM Homestays h WHERE h.HostID = ? "
                + "ORDER BY h.CreatedAt DESC";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hostId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Homestay homestay = new Homestay();
                    homestay.setHomestayId(resultSet.getInt("HomestayID"));
                    homestay.setHostId(resultSet.getInt("HostID"));
                    homestay.setTitle(resultSet.getString("Title"));
                    homestay.setAddress(resultSet.getString("Address"));
                    homestay.setCity(resultSet.getString("City"));
                    homestay.setDistrict(resultSet.getString("District"));
                    homestay.setPricePerNight(
                            resultSet.getBigDecimal("PricePerNight")
                    );
                    homestay.setMaxGuests(resultSet.getInt("MaxGuests"));
                    homestay.setStatus(resultSet.getString("Status"));
                    homestay.setPrimaryImageUrl(
                            resultSet.getString("PrimaryImageURL")
                    );
                    homestay.setAverageRating(
                            resultSet.getDouble("AverageRating")
                    );
                    homestay.setReviewCount(resultSet.getInt("ReviewCount"));

                    Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
                    Timestamp updatedAt = resultSet.getTimestamp("UpdatedAt");
                    if (createdAt != null) {
                        homestay.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    if (updatedAt != null) {
                        homestay.setUpdatedAt(updatedAt.toLocalDateTime());
                    }
                    homestays.add(homestay);
                }
            }
        }
        return homestays;
    }

    @Override
    public HomestayForm findFormByIdAndHostId(int homestayId, int hostId)
            throws SQLException {
        String sql = "SELECT HomestayID, HostID, CancellationPolicyID, "
                + "Title, Description, Address, City, District, Latitude, "
                + "Longitude, PricePerNight, MaxGuests "
                + "FROM Homestays WHERE HomestayID = ? AND HostID = ?";

        ensureConnection();
        HomestayForm form = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            statement.setInt(2, hostId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    form = new HomestayForm();
                    form.setHomestayId(resultSet.getInt("HomestayID"));
                    form.setHostId(resultSet.getInt("HostID"));
                    int policyId = resultSet.getInt("CancellationPolicyID");
                    if (!resultSet.wasNull()) {
                        form.setCancellationPolicyId(policyId);
                    }
                    form.setTitle(resultSet.getString("Title"));
                    form.setDescription(resultSet.getString("Description"));
                    form.setAddress(resultSet.getString("Address"));
                    form.setCity(resultSet.getString("City"));
                    form.setDistrict(resultSet.getString("District"));
                    form.setLatitude(resultSet.getBigDecimal("Latitude"));
                    form.setLongitude(resultSet.getBigDecimal("Longitude"));
                    form.setPricePerNight(
                            resultSet.getBigDecimal("PricePerNight")
                    );
                    form.setMaxGuests(resultSet.getInt("MaxGuests"));
                }
            }
        }

        if (form != null) {
            form.setAmenityIds(findSelectedIds(
                    "SELECT AmenityID AS SelectedID FROM HomestayAmenities "
                    + "WHERE HomestayID = ?",
                    homestayId
            ));
            form.setPaymentMethodIds(findSelectedIds(
                    "SELECT PaymentMethodID AS SelectedID "
                    + "FROM HomestayPaymentMethods WHERE HomestayID = ?",
                    homestayId
            ));
            form.setImageUrls(findImageUrls(homestayId));
        }
        return form;
    }

    @Override
    public List<Amenity> findAmenities() throws SQLException {
        List<Amenity> amenities = new ArrayList<Amenity>();
        String sql = "SELECT AmenityID, AmenityName, IconClass "
                + "FROM Amenities ORDER BY AmenityName";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Amenity amenity = new Amenity();
                amenity.setAmenityId(resultSet.getInt("AmenityID"));
                amenity.setAmenityName(resultSet.getString("AmenityName"));
                amenity.setIconClass(resultSet.getString("IconClass"));
                amenities.add(amenity);
            }
        }
        return amenities;
    }

    @Override
    public List<PaymentMethod> findPaymentMethods() throws SQLException {
        List<PaymentMethod> methods = new ArrayList<PaymentMethod>();
        String sql = "SELECT PaymentMethodID, MethodCode, MethodName, "
                + "IsOnline, IsActive FROM PaymentMethods "
                + "WHERE IsActive = 1 ORDER BY PaymentMethodID";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                PaymentMethod method = new PaymentMethod();
                method.setPaymentMethodId(
                        resultSet.getInt("PaymentMethodID")
                );
                method.setMethodCode(resultSet.getString("MethodCode"));
                method.setMethodName(resultSet.getString("MethodName"));
                method.setOnline(resultSet.getBoolean("IsOnline"));
                method.setActive(resultSet.getBoolean("IsActive"));
                methods.add(method);
            }
        }
        return methods;
    }

    @Override
    public List<CancellationPolicy> findActivePolicies()
            throws SQLException {
        List<CancellationPolicy> policies
                = new ArrayList<CancellationPolicy>();
        String sql = "SELECT PolicyID, PolicyName, Description, "
                + "FullRefundDays, PartialRefundDays, "
                + "PartialRefundPercent, IsActive "
                + "FROM CancellationPolicies WHERE IsActive = 1 "
                + "ORDER BY PolicyID";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                CancellationPolicy policy = new CancellationPolicy();
                policy.setPolicyId(resultSet.getInt("PolicyID"));
                policy.setPolicyName(resultSet.getString("PolicyName"));
                policy.setDescription(resultSet.getString("Description"));
                policy.setFullRefundDays(
                        resultSet.getInt("FullRefundDays")
                );
                policy.setPartialRefundDays(
                        resultSet.getInt("PartialRefundDays")
                );
                policy.setPartialRefundPercent(
                        resultSet.getDouble("PartialRefundPercent")
                );
                policy.setActive(resultSet.getBoolean("IsActive"));
                policies.add(policy);
            }
        }
        return policies;
    }

    @Override
    public int create(HomestayForm form) throws SQLException {
        ensureConnection();
        boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            String sql = "INSERT INTO Homestays "
                    + "(HostID, CancellationPolicyID, Title, Description, "
                    + "Address, City, District, Latitude, Longitude, "
                    + "PricePerNight, MaxGuests, Status) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Pending')";
            int homestayId;
            try (PreparedStatement statement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {
                setHomestayParameters(statement, form, false);
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Không thể tạo homestay.");
                    }
                    homestayId = keys.getInt(1);
                }
            }

            insertRelations(homestayId, form);
            connection.commit();
            return homestayId;
        } catch (SQLException exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    @Override
    public boolean update(HomestayForm form) throws SQLException {
        ensureConnection();
        boolean oldAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            String sql = "UPDATE Homestays SET CancellationPolicyID = ?, "
                    + "Title = ?, Description = ?, Address = ?, City = ?, "
                    + "District = ?, Latitude = ?, Longitude = ?, "
                    + "PricePerNight = ?, MaxGuests = ?, "
                    + "Status = CASE WHEN Status = 'Rejected' "
                    + "THEN 'Pending' ELSE Status END, "
                    + "UpdatedAt = SYSDATETIME() "
                    + "WHERE HomestayID = ? AND HostID = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                setHomestayParameters(statement, form, true);
                if (statement.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            deleteRelations(form.getHomestayId());
            insertRelations(form.getHomestayId(), form);
            connection.commit();
            return true;
        } catch (SQLException exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    @Override
    public boolean hide(int homestayId, int hostId) throws SQLException {
        String sql = "UPDATE Homestays SET Status = 'Hidden', "
                + "UpdatedAt = SYSDATETIME() "
                + "WHERE HomestayID = ? AND HostID = ? "
                + "AND Status <> 'Hidden'";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            statement.setInt(2, hostId);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean submitForApproval(int homestayId, int hostId)
            throws SQLException {
        String sql = "UPDATE Homestays SET Status = 'Pending', "
                + "UpdatedAt = SYSDATETIME() "
                + "WHERE HomestayID = ? AND HostID = ? "
                + "AND Status = 'Hidden'";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            statement.setInt(2, hostId);
            return statement.executeUpdate() > 0;
        }
    }

    private void setHomestayParameters(PreparedStatement statement,
            HomestayForm form,
            boolean update) throws SQLException {
        int index = 1;
        if (!update) {
            statement.setInt(index++, form.getHostId());
        }
        if (form.getCancellationPolicyId() == null) {
            statement.setNull(index++, Types.INTEGER);
        } else {
            statement.setInt(index++, form.getCancellationPolicyId());
        }
        statement.setString(index++, form.getTitle());
        statement.setString(index++, form.getDescription());
        statement.setString(index++, form.getAddress());
        statement.setString(index++, form.getCity());
        statement.setString(index++, form.getDistrict());
        statement.setBigDecimal(index++, form.getLatitude());
        statement.setBigDecimal(index++, form.getLongitude());
        statement.setBigDecimal(index++, form.getPricePerNight());
        statement.setInt(index++, form.getMaxGuests());
        if (update) {
            statement.setInt(index++, form.getHomestayId());
            statement.setInt(index, form.getHostId());
        }
    }

    private void deleteRelations(int homestayId) throws SQLException {
        String[] statements = {
            "DELETE FROM HomestayAmenities WHERE HomestayID = ?",
            "DELETE FROM HomestayPaymentMethods WHERE HomestayID = ?",
            "DELETE FROM HomestayImages WHERE HomestayID = ?"
        };
        for (String sql : statements) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, homestayId);
                statement.executeUpdate();
            }
        }
    }

    private void insertRelations(int homestayId, HomestayForm form)
            throws SQLException {
        insertIds(
                "INSERT INTO HomestayAmenities (HomestayID, AmenityID) "
                + "VALUES (?, ?)",
                homestayId,
                form.getAmenityIds()
        );
        insertIds(
                "INSERT INTO HomestayPaymentMethods "
                + "(HomestayID, PaymentMethodID) VALUES (?, ?)",
                homestayId,
                form.getPaymentMethodIds()
        );

        String imageSql = "INSERT INTO HomestayImages "
                + "(HomestayID, ImageURL, IsPrimary) VALUES (?, ?, ?)";
        try (PreparedStatement statement
                = connection.prepareStatement(imageSql)) {
            for (int i = 0; i < form.getImageUrls().size(); i++) {
                statement.setInt(1, homestayId);
                statement.setString(2, form.getImageUrls().get(i));
                statement.setBoolean(3, i == 0);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void insertIds(String sql, int homestayId, List<Integer> ids)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Integer id : ids) {
                statement.setInt(1, homestayId);
                statement.setInt(2, id);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private List<Integer> findSelectedIds(String sql, int homestayId)
            throws SQLException {
        List<Integer> ids = new ArrayList<Integer>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ids.add(resultSet.getInt("SelectedID"));
                }
            }
        }
        return ids;
    }

    private List<String> findImageUrls(int homestayId) throws SQLException {
        List<String> urls = new ArrayList<String>();
        String sql = "SELECT ImageURL FROM HomestayImages "
                + "WHERE HomestayID = ? ORDER BY IsPrimary DESC, ImageID";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    urls.add(resultSet.getString("ImageURL"));
                }
            }
        }
        return urls;
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
