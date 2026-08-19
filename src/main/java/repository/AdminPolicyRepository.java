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
import entity.CancellationPolicy;
import interfaces.IAdminPolicyRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AdminPolicyRepository extends DBContext
        implements IAdminPolicyRepository {

    public AdminPolicyRepository() {
        super();
    }

    @Override
    public List<CancellationPolicy> findAll() throws SQLException {
        List<CancellationPolicy> policies
                = new ArrayList<CancellationPolicy>();
        String sql = policySelect() + " ORDER BY IsActive DESC, PolicyID";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                policies.add(mapPolicy(resultSet));
            }
        }
        return policies;
    }

    @Override
    public CancellationPolicy findById(int policyId) throws SQLException {
        String sql = policySelect() + " WHERE PolicyID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, policyId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapPolicy(resultSet) : null;
            }
        }
    }

    @Override
    public boolean existsName(String name, Integer excludedPolicyId)
            throws SQLException {
        String sql = "SELECT 1 FROM CancellationPolicies "
                + "WHERE LOWER(PolicyName) = LOWER(?) "
                + (excludedPolicyId == null ? "" : "AND PolicyID <> ?");
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            if (excludedPolicyId != null) {
                statement.setInt(2, excludedPolicyId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public int create(CancellationPolicy policy) throws SQLException {
        String sql = "INSERT INTO CancellationPolicies "
                + "(PolicyName, Description, FullRefundDays, "
                + "PartialRefundDays, PartialRefundPercent, IsActive, "
                + "CreatedByID) VALUES (?, ?, ?, ?, ?, 1, ?)";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            setCommonParameters(statement, policy);
            if (policy.getCreatedById() == null) {
                statement.setNull(6, Types.INTEGER);
            } else {
                statement.setInt(6, policy.getCreatedById());
            }
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    @Override
    public boolean update(CancellationPolicy policy) throws SQLException {
        String sql = "UPDATE CancellationPolicies SET PolicyName = ?, "
                + "Description = ?, FullRefundDays = ?, "
                + "PartialRefundDays = ?, PartialRefundPercent = ?, "
                + "UpdatedAt = SYSDATETIME() WHERE PolicyID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setCommonParameters(statement, policy);
            statement.setInt(6, policy.getPolicyId());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean setActive(int policyId, boolean active)
            throws SQLException {
        String sql = "UPDATE CancellationPolicies SET IsActive = ?, "
                + "UpdatedAt = SYSDATETIME() WHERE PolicyID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, active);
            statement.setInt(2, policyId);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean isUsed(int policyId) throws SQLException {
        String sql = "SELECT CASE WHEN "
                + "EXISTS (SELECT 1 FROM Homestays WHERE CancellationPolicyID = ?) "
                + "OR EXISTS (SELECT 1 FROM Bookings WHERE CancellationPolicyID = ?) "
                + "THEN 1 ELSE 0 END AS IsUsed";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, policyId);
            statement.setInt(2, policyId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getBoolean("IsUsed");
            }
        }
    }

    @Override
    public boolean delete(int policyId) throws SQLException {
        String sql = "DELETE FROM CancellationPolicies WHERE PolicyID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, policyId);
            return statement.executeUpdate() > 0;
        }
    }

    private void setCommonParameters(PreparedStatement statement,
            CancellationPolicy policy)
            throws SQLException {
        statement.setString(1, policy.getPolicyName());
        statement.setString(2, policy.getDescription());
        statement.setInt(3, policy.getFullRefundDays());
        statement.setInt(4, policy.getPartialRefundDays());
        statement.setDouble(5, policy.getPartialRefundPercent());
    }

    private String policySelect() {
        return "SELECT PolicyID, PolicyName, Description, FullRefundDays, "
                + "PartialRefundDays, PartialRefundPercent, IsActive, "
                + "CreatedByID FROM CancellationPolicies";
    }

    private CancellationPolicy mapPolicy(ResultSet resultSet)
            throws SQLException {
        CancellationPolicy policy = new CancellationPolicy();
        policy.setPolicyId(resultSet.getInt("PolicyID"));
        policy.setPolicyName(resultSet.getString("PolicyName"));
        policy.setDescription(resultSet.getString("Description"));
        policy.setFullRefundDays(resultSet.getInt("FullRefundDays"));
        policy.setPartialRefundDays(resultSet.getInt("PartialRefundDays"));
        policy.setPartialRefundPercent(
                resultSet.getDouble("PartialRefundPercent")
        );
        policy.setActive(resultSet.getBoolean("IsActive"));
        int createdById = resultSet.getInt("CreatedByID");
        if (!resultSet.wasNull()) {
            policy.setCreatedById(createdById);
        }
        return policy;
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
