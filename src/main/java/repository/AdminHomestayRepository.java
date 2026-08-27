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

public class AdminHomestayRepository extends DBContext// su dung DBContext de ket noi voi database
        implements IAdminHomestayRepository {

    public AdminHomestayRepository() {
        super();
    }

    @Override
    public List<Homestay> findAll(String keyword, String status)// phuong thuc findAll de lay danh sach homestay theo keyword va status
            throws SQLException {
        List<Homestay> homestays = new ArrayList<Homestay>();// khoi tao danh sach homestay rong
        List<String> parameters = new ArrayList<String>();// khoi tao danh sach tham so rong
        StringBuilder sql = new StringBuilder(homestaySelect());// khoi tao cau lenh sql de lay danh sach homestay
        sql.append(" WHERE 1 = 1 ");// dieu kien de lay tat ca homestay
        if (!isBlank(keyword)) {// neu keyword khong rong thi them dieu kien
            sql.append("AND (h.Title LIKE ? OR h.Address LIKE ? ");// dieu kien de tim kiem homestay theo title hoac address
            sql.append("OR h.City LIKE ? OR host.FullName LIKE ?) ");// dieu kien de tim kiem homestay theo city hoac host name
            String value = "%" + keyword.trim() + "%";// them % vao truoc va sau keyword de tim kiem theo chuoi con
            parameters.add(value);// them tham so vao danh sach tham so
            parameters.add(value);
            parameters.add(value);
            parameters.add(value);
        }
        if (!isBlank(status)) {// neu status khong rong thi them dieu kien
            sql.append("AND h.Status = ? ");// dieu kien de tim kiem homestay theo status
            parameters.add(status);// them tham so vao danh sach tham so
        }
        sql.append("ORDER BY CASE WHEN h.Status = 'Pending' THEN 0 ELSE 1 END, ");// sap xep homestay theo status, homestay pending se duoc hien thi truoc
        sql.append("h.CreatedAt DESC");// sap xep homestay theo ngay tao, homestay moi nhat se duoc hien

        ensureConnection();
        try (PreparedStatement statement // tao PreparedStatement de thuc thi cau lenh sql
                = connection.prepareStatement(sql.toString())) {// chuyen cau lenh sql tu StringBuilder sang String
            for (int i = 0; i < parameters.size(); i++) {// duyet qua danh sach tham so va gan vao PreparedStatement
                statement.setString(i + 1, parameters.get(i));// gan tham so vao PreparedStatement, i + 1 vi PreparedStatement bat dau tu 1
            }
            try (ResultSet resultSet = statement.executeQuery()) {// thuc thi cau lenh sql va tra ve ResultSet
                while (resultSet.next()) {// duyet qua ResultSet va gan vao danh sach homestay
                    homestays.add(mapHomestay(resultSet));// gan homestay vao danh sach homestay
                }
            }
        }
        return homestays;
    }

    @Override
    public Homestay findById(int homestayId) throws SQLException {// phuong thuc findById de lay homestay theo homestayId
        String sql = homestaySelect() + " WHERE h.HomestayID = ?";// cau lenh sql de lay homestay theo homestayId
        ensureConnection();
        Homestay homestay = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {// tao PreparedStatement de thuc thi cau lenh sql
            statement.setInt(1, homestayId);// gan homestayId vao PreparedStatement
            try (ResultSet resultSet = statement.executeQuery()) {// thuc thi cau lenh sql va tra ve ResultSet
                if (resultSet.next()) {// neu co ket qua thi gan vao homestay
                    homestay = mapHomestay(resultSet);// gan homestay vao homestay
                }
            }
        }
        if (homestay != null) {// neu homestay khong rong thi gan images va amenities vao homestay
            homestay.setImages(findImages(homestayId));// gan images vao homestay
            homestay.setAmenities(findAmenities(homestayId));// gan amenities vao homestay
        }
        return homestay;
    }

    @Override
    public List<CancellationPolicy> findPolicies() throws SQLException {// phuong thuc findPolicies de lay danh sach cancellation policy
        List<CancellationPolicy> policies 
                = new ArrayList<CancellationPolicy>();// khoi tao danh sach cancellation policy rong
        String sql = "SELECT PolicyID, PolicyName, Description, "// cau lenh sql de lay danh sach cancellation policy
                + "FullRefundDays, PartialRefundDays, "
                + "PartialRefundPercent, IsActive "
                + "FROM CancellationPolicies ORDER BY IsActive DESC, PolicyID";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {// duyet qua ResultSet va gan vao danh sach cancellation policy
                CancellationPolicy policy = new CancellationPolicy();// khoi tao cancellation policy moi
                policy.setPolicyId(resultSet.getInt("PolicyID"));// gan policyId vao cancellation policy
                policy.setPolicyName(resultSet.getString("PolicyName"));// gan policyName vao cancellation policy
                policy.setDescription(resultSet.getString("Description"));// gan description vao cancellation policy
                policy.setFullRefundDays(resultSet.getInt("FullRefundDays"));// gan fullRefundDays vao cancellation policy
                policy.setPartialRefundDays(resultSet.getInt("PartialRefundDays"));// gan partialRefundDays vao cancellation policy
                policy.setPartialRefundPercent(resultSet.getDouble("PartialRefundPercent"));// gan partialRefundPercent vao cancellation policy
                policy.setActive(resultSet.getBoolean("IsActive"));// gan isActive vao cancellation policy
                policies.add(policy);
            }
        }
        return policies;
    }

    @Override
    public boolean update(Homestay homestay) throws SQLException { // phuong thuc update de cap nhat homestay
        String sql = "UPDATE Homestays SET CancellationPolicyID = ?, " // cau lenh sql de cap nhat homestay
                + "Title = ?, Description = ?, Address = ?, City = ?, " 
                + "District = ?, Latitude = ?, Longitude = ?, "
                + "PricePerNight = ?, MaxGuests = ?, UpdatedAt = SYSDATETIME() "
                + "WHERE HomestayID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) { // tao PreparedStatement de thuc thi cau lenh sql
            if (homestay.getCancellationPolicyId() == null) { // neu cancellationPolicyId la null thi gan null vao PreparedStatement
                statement.setNull(1, Types.INTEGER); // gan null vao PreparedStatement
            } else {
                statement.setInt(1, homestay.getCancellationPolicyId()); // gan cancellationPolicyId vao PreparedStatement
            }
            statement.setString(2, homestay.getTitle()); // gan title vao PreparedStatement
            statement.setString(3, homestay.getDescription()); // gan description vao PreparedStatement
            statement.setString(4, homestay.getAddress()); // gan address vao PreparedStatement
            statement.setString(5, homestay.getCity()); // gan city vao PreparedStatement
            statement.setString(6, homestay.getDistrict()); // gan district vao PreparedStatement
            statement.setBigDecimal(7, homestay.getLatitude()); // gan latitude vao PreparedStatement
            statement.setBigDecimal(8, homestay.getLongitude()); // gan longitude vao PreparedStatement
            statement.setBigDecimal(9, homestay.getPricePerNight()); // gan pricePerNight vao PreparedStatement
            statement.setInt(10, homestay.getMaxGuests()); // gan maxGuests vao PreparedStatement
            statement.setInt(11, homestay.getHomestayId()); // gan homestayId vao PreparedStatement
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(int homestayId, String status, int adminId,
            String reason) throws SQLException {
        ensureConnection();
        boolean oldAutoCommit = connection.getAutoCommit(); // luu trang thai auto commit cu de phuc hoi sau khi thuc thi xong
        try {
            connection.setAutoCommit(false);
            Integer hostId = null; // khoi tao hostId de lay hostId tu database
            String findSql = "SELECT HostID FROM Homestays " // cau lenh sql de lay hostId tu database
                    + "WITH (UPDLOCK) WHERE HomestayID = ?";
            try (PreparedStatement statement = connection.prepareStatement(findSql)) { // tao PreparedStatement de thuc thi cau lenh sql
                statement.setInt(1, homestayId); // gan homestayId vao PreparedStatement
                try (ResultSet resultSet = statement.executeQuery()) { // thuc thi cau lenh sql va tra ve ResultSet 
                    if (resultSet.next()) {
                        hostId = resultSet.getInt("HostID"); // gan hostId vao hostId
                    }
                }
            }
            if (hostId == null) {
                connection.rollback();
                return false;
            }

            String updateSql = "UPDATE Homestays SET Status = ?, " // cau lenh sql de cap nhat status cua homestay
                    + "UpdatedAt = SYSDATETIME() WHERE HomestayID = ?"; // cau lenh sql de cap nhat status cua homestay
            try (PreparedStatement statement = connection.prepareStatement(updateSql)) { // tao PreparedStatement de thuc thi cau lenh sql
                statement.setString(1, status); // gan status vao PreparedStatement
                statement.setInt(2, homestayId); // gan homestayId vao PreparedStatement
                statement.executeUpdate(); // thuc thi cau lenh sql de cap nhat status cua homestay
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

    private void createNotification(int userId, int adminId, int homestayId, // phuong thuc createNotification de tao thong bao cho user khi homestay thay doi trang thai
            String title, String message) // phuong thuc createNotification de tao thong bao cho user khi homestay thay doi trang thai
            throws SQLException {
        String sql = "INSERT INTO Notifications " // cau lenh sql de tao thong bao cho user khi homestay thay doi trang thai
                + "(Title, Message, Type, RelatedID, CreatedByID) "
                + "VALUES (?, ?, 'System', ?, ?)";
        int notificationId;
        try (PreparedStatement statement = connection.prepareStatement( // tao PreparedStatement de thuc thi cau lenh sql
                sql, Statement.RETURN_GENERATED_KEYS)) { // tra ve ResultSet chua key moi tao
            statement.setString(1, title);
            statement.setString(2, message);
            statement.setInt(3, homestayId);
            statement.setInt(4, adminId);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) { // lay key moi tao tu ResultSet
                if (!keys.next()) { // neu khong co key moi tao thi throw exception
                    throw new SQLException("Không thể tạo thông báo."); 
                }
                notificationId = keys.getInt(1); // lay key moi tao tu ResultSet 
            }
        }
        try (PreparedStatement statement = connection.prepareStatement(// tao PreparedStatement de thuc thi cau lenh sql
                "INSERT INTO UserNotifications "
                + "(NotificationID, UserID, IsRead) VALUES (?, ?, 0)")) {
            statement.setInt(1, notificationId); // gan notificationId vao PreparedStatement
            statement.setInt(2, userId); // gan userId vao PreparedStatement
            statement.executeUpdate(); // thuc thi cau lenh sql de tao thong bao cho user khi homestay thay doi trang thai
        }
    }

    private List<HomestayImage> findImages(int homestayId) throws SQLException { // phuong thuc findImages de lay danh sach images cua homestay
        List<HomestayImage> images = new ArrayList<HomestayImage>(); // khoi tao danh sach images rong 
        String sql = "SELECT ImageID, HomestayID, ImageURL, IsPrimary, CreatedAt "// cau lenh sql de lay danh sach images cua homestay
                + "FROM HomestayImages WHERE HomestayID = ? "
                + "ORDER BY IsPrimary DESC, ImageID";
        try (PreparedStatement statement = connection.prepareStatement(sql)) { // tao PreparedStatement de thuc thi cau lenh sql
            statement.setInt(1, homestayId); // gan homestayId vao PreparedStatement
            try (ResultSet resultSet = statement.executeQuery()) { // thuc thi cau lenh sql va tra ve ResultSet
                while (resultSet.next()) { // duyet qua ResultSet va gan vao danh sach images cua homestay
                    HomestayImage image = new HomestayImage(); // khoi tao image moi
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

    private List<Amenity> findAmenities(int homestayId) throws SQLException { // phuong thuc findAmenities de lay danh sach amenities cua homestay
        List<Amenity> amenities = new ArrayList<Amenity>(); //  khoi tao danh sach amenities rong
        String sql = "SELECT a.AmenityID, a.AmenityName, a.IconClass " // cau lenh sql de lay danh sach amenities cua homestay
                + "FROM Amenities a INNER JOIN HomestayAmenities ha " // INNER JOIN de lay danh sach amenities cua homestay
                + "ON ha.AmenityID = a.AmenityID WHERE ha.HomestayID = ?"; // dieu kien de lay danh sach amenities cua homestay
        try (PreparedStatement statement = connection.prepareStatement(sql)) { // tao PreparedStatement de thuc thi cau lenh sql
            statement.setInt(1, homestayId); // gan homestayId vao PreparedStatement
            try (ResultSet resultSet = statement.executeQuery()) { // thuc thi cau lenh sql va tra ve ResultSet
                while (resultSet.next()) { // duyet qua ResultSet va gan vao danh sach amenities cua homestay
                    Amenity amenity = new Amenity(); // khoi tao amenity moi
                    amenity.setAmenityId(resultSet.getInt("AmenityID")); // gan amenityId vao amenity
                    amenity.setAmenityName(resultSet.getString("AmenityName")); // gan amenityName vao amenity
                    amenity.setIconClass(resultSet.getString("IconClass")); //  gan iconClass vao amenity
                    amenities.add(amenity);
                }
            }
        }
        return amenities;
    }

    private String homestaySelect() { // phuong thuc homestaySelect de tra ve cau lenh sql de lay danh sach homestay
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

    private Homestay mapHomestay(ResultSet resultSet) throws SQLException { // phuong thuc mapHomestay de gan gia tri tu ResultSet vao Homestay
        Homestay homestay = new Homestay(); // khoi tao homestay moi
        homestay.setHomestayId(resultSet.getInt("HomestayID")); // gan homestayId vao homestay
        homestay.setHostId(resultSet.getInt("HostID")); //  gan hostId vao homestay
        homestay.setHostName(resultSet.getString("HostName")); // gan hostName vao homestay
        homestay.setHostEmail(resultSet.getString("HostEmail")); // gan hostEmail vao homestay
        int policyId = resultSet.getInt("CancellationPolicyID"); // lay cancellationPolicyId tu ResultSet
        if (!resultSet.wasNull()) { // kiem tra xem cancellationPolicyId co null hay khong
            homestay.setCancellationPolicyId(policyId); // gan cancellationPolicyId vao homestay
            CancellationPolicy policy = new CancellationPolicy(); // khoi tao cancellationPolicy moi
            policy.setPolicyId(policyId); // gan policyId vao cancellationPolicy
            policy.setPolicyName(resultSet.getString("PolicyName")); // gan policyName vao cancellationPolicy
            policy.setDescription(resultSet.getString("PolicyDescription")); // gan description vao cancellationPolicy
            policy.setFullRefundDays(resultSet.getInt("FullRefundDays")); // gan fullRefundDays vao cancellationPolicy
            policy.setPartialRefundDays(resultSet.getInt("PartialRefundDays")); // gan partialRefundDays vao cancellationPolicy
            policy.setPartialRefundPercent(resultSet.getDouble("PartialRefundPercent")); // gan partialRefundPercent vao cancellationPolicy
            homestay.setCancellationPolicy(policy);
        }
        homestay.setTitle(resultSet.getString("Title")); // gan title vao homestay
        homestay.setDescription(resultSet.getString("Description")); // gan description vao homestay
        homestay.setAddress(resultSet.getString("Address")); // gan address vao homestay
        homestay.setCity(resultSet.getString("City")); // gan city vao homestay
        homestay.setDistrict(resultSet.getString("District")); // gan district vao homestay
        homestay.setLatitude(resultSet.getBigDecimal("Latitude")); // gan latitude vao homestay
        homestay.setLongitude(resultSet.getBigDecimal("Longitude")); // gan longitude vao homestay
        homestay.setPricePerNight(resultSet.getBigDecimal("PricePerNight")); // gan pricePerNight vao homestay
        homestay.setMaxGuests(resultSet.getInt("MaxGuests")); // gan maxGuests vao homestay
        homestay.setStatus(resultSet.getString("Status")); // gan status vao homestay
        homestay.setPrimaryImageUrl(resultSet.getString("PrimaryImageURL")); // gan primaryImageUrl vao homestay
        homestay.setAverageRating(resultSet.getDouble("AverageRating")); // gan averageRating vao homestay
        homestay.setReviewCount(resultSet.getInt("ReviewCount")); // gan reviewCount vao homestay
        Timestamp createdAt = resultSet.getTimestamp("CreatedAt"); // lay createdAt tu ResultSet
        Timestamp updatedAt = resultSet.getTimestamp("UpdatedAt"); // lay updatedAt tu ResultSet
        if (createdAt != null) {
            homestay.setCreatedAt(createdAt.toLocalDateTime());// gan createdAt vao homestay
        }
        if (updatedAt != null) {
            homestay.setUpdatedAt(updatedAt.toLocalDateTime()); // gan updatedAt vao homestay
        }
        return homestay;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty(); // kiem tra xem value co null hoac rong hay khong
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
