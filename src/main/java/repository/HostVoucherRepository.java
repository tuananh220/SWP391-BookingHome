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
import entity.Voucher;
import interfaces.IHostVoucherRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class HostVoucherRepository extends DBContext
        implements IHostVoucherRepository {

    private static final String VOUCHER_SELECT =
        "SELECT v.VoucherID, v.CreatedByID, v.HomestayID, "
        + "v.VoucherCode, v.DiscountRate, v.MaxDiscountAmount, "
        + "v.MinOrderValue, v.StartDate, v.EndDate, v.UsageLimit, "
        + "v.UsedCount, v.IsActive, v.CreatedAt, v.UpdatedAt, "
        + "h.Title AS HomestayTitle "
        + "FROM Vouchers v LEFT JOIN Homestays h "
        + "ON h.HomestayID = v.HomestayID";

    private static final String VOUCHER_INSERT =
        "INSERT INTO Vouchers "
        + "(CreatedByID, HomestayID, VoucherCode, DiscountRate, "
        + "MaxDiscountAmount, MinOrderValue, StartDate, EndDate, "
        + "UsageLimit, UsedCount, IsActive) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 1)";

    private static final String VOUCHER_UPDATE =
        "UPDATE Vouchers SET HomestayID = ?, VoucherCode = ?, "
        + "DiscountRate = ?, MaxDiscountAmount = ?, "
        + "MinOrderValue = ?, StartDate = ?, EndDate = ?, "
        + "UsageLimit = ?, UpdatedAt = SYSDATETIME() "
        + "WHERE VoucherID = ? AND CreatedByID = ? "
        + "AND UsageLimit >= UsedCount";

    public HostVoucherRepository() {
        super();
    }

    @Override
    public List<Voucher> findByHostId(int hostId) throws SQLException {
        List<Voucher> vouchers = new ArrayList<Voucher>();
        String sql = VOUCHER_SELECT
                + " WHERE v.CreatedByID = ? ORDER BY v.StartDate DESC";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hostId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    vouchers.add(mapVoucher(resultSet));
                }
            }
        }
        return vouchers;
    }

    @Override
    public Voucher findByIdAndHostId(int voucherId, int hostId)
            throws SQLException {
        String sql = VOUCHER_SELECT
                + " WHERE v.VoucherID = ? AND v.CreatedByID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, voucherId);
            statement.setInt(2, hostId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapVoucher(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public List<Homestay> findHostHomestays(int hostId) throws SQLException {
        List<Homestay> homestays = new ArrayList<Homestay>();
        String sql = "SELECT HomestayID, Title, Status FROM Homestays "
                + "WHERE HostID = ? ORDER BY Title";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hostId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Homestay homestay = new Homestay();
                    homestay.setHomestayId(resultSet.getInt("HomestayID"));
                    homestay.setTitle(resultSet.getString("Title"));
                    homestay.setStatus(resultSet.getString("Status"));
                    homestays.add(homestay);
                }
            }
        }
        return homestays;
    }

    @Override
    public boolean existsCode(String voucherCode, Integer excludedVoucherId)
            throws SQLException {
        String sql = "SELECT 1 FROM Vouchers "
            + "WHERE UPPER(VoucherCode) = UPPER(?) "
                + (excludedVoucherId == null ? "" : "AND VoucherID <> ?");
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, voucherCode);
            if (excludedVoucherId != null) {
                statement.setInt(2, excludedVoucherId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public int create(Voucher voucher) throws SQLException {
        if (!ownsHomestay(voucher.getHomestayId(), voucher.getCreatedById())) {
            return 0;
        }

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(
            VOUCHER_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(statement, voucher, false);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    @Override
    public boolean update(Voucher voucher) throws SQLException {
        if (!ownsHomestay(voucher.getHomestayId(), voucher.getCreatedById())) {
            return false;
        }

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(
            VOUCHER_UPDATE)) {
            setParameters(statement, voucher, true);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deactivate(int voucherId, int hostId)
            throws SQLException {
        String sql = "UPDATE Vouchers SET IsActive = 0, "
                + "UpdatedAt = SYSDATETIME() "
                + "WHERE VoucherID = ? AND CreatedByID = ? AND IsActive = 1";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, voucherId);
            statement.setInt(2, hostId);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean activate(int voucherId, int hostId)
            throws SQLException {
        String sql = "UPDATE Vouchers SET IsActive = 1, "
                + "UpdatedAt = SYSDATETIME() "
                + "WHERE VoucherID = ? AND CreatedByID = ? AND IsActive = 0 "
                + "AND EndDate > SYSDATETIME() AND UsedCount < UsageLimit";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, voucherId);
            statement.setInt(2, hostId);
            return statement.executeUpdate() > 0;
        }
    }

    private boolean ownsHomestay(Integer homestayId, int hostId)
            throws SQLException {
        if (homestayId == null) {
            return true;
        }
        String sql = "SELECT 1 FROM Homestays "
                + "WHERE HomestayID = ? AND HostID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            statement.setInt(2, hostId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void setParameters(PreparedStatement statement, Voucher voucher,
            boolean update) throws SQLException {
        int index = 1;
        if (!update) {
            statement.setInt(index++, voucher.getCreatedById());
        }
        if (voucher.getHomestayId() == null) {
            statement.setNull(index++, Types.INTEGER);
        } else {
            statement.setInt(index++, voucher.getHomestayId());
        }
        statement.setString(index++, voucher.getVoucherCode());
        statement.setBigDecimal(index++, voucher.getDiscountRate());
        statement.setBigDecimal(index++, voucher.getMaxDiscountAmount());
        statement.setBigDecimal(index++, voucher.getMinOrderValue());
        statement.setTimestamp(
                index++, Timestamp.valueOf(voucher.getStartDate())
        );
        statement.setTimestamp(index++, Timestamp.valueOf(voucher.getEndDate()));
        statement.setInt(index++, voucher.getUsageLimit());
        if (update) {
            statement.setInt(index++, voucher.getVoucherId());
            statement.setInt(index, voucher.getCreatedById());
        }
    }

    private Voucher mapVoucher(ResultSet resultSet) throws SQLException {
        Voucher voucher = new Voucher();
        voucher.setVoucherId(resultSet.getInt("VoucherID"));
        voucher.setCreatedById(resultSet.getInt("CreatedByID"));
        int homestayId = resultSet.getInt("HomestayID");
        if (!resultSet.wasNull()) {
            voucher.setHomestayId(homestayId);
        }
        voucher.setVoucherCode(resultSet.getString("VoucherCode"));
        voucher.setDiscountRate(resultSet.getBigDecimal("DiscountRate"));
        voucher.setMaxDiscountAmount(
                resultSet.getBigDecimal("MaxDiscountAmount")
        );
        voucher.setMinOrderValue(resultSet.getBigDecimal("MinOrderValue"));
        voucher.setStartDate(
                resultSet.getTimestamp("StartDate").toLocalDateTime()
        );
        voucher.setEndDate(
                resultSet.getTimestamp("EndDate").toLocalDateTime()
        );
        voucher.setUsageLimit(resultSet.getInt("UsageLimit"));
        voucher.setUsedCount(resultSet.getInt("UsedCount"));
        voucher.setActive(resultSet.getBoolean("IsActive"));
        voucher.setHomestayTitle(resultSet.getString("HomestayTitle"));
        return voucher;
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
