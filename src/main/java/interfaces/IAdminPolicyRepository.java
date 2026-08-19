/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

/**
 *
 * @author Admin
 */
import entity.CancellationPolicy;

import java.sql.SQLException;
import java.util.List;

public interface IAdminPolicyRepository {

    List<CancellationPolicy> findAll() throws SQLException;

    CancellationPolicy findById(int policyId) throws SQLException;

    boolean existsName(String name, Integer excludedPolicyId)
            throws SQLException;

    int create(CancellationPolicy policy) throws SQLException;

    boolean update(CancellationPolicy policy) throws SQLException;

    boolean setActive(int policyId, boolean active) throws SQLException;

    boolean isUsed(int policyId) throws SQLException;

    boolean delete(int policyId) throws SQLException;
}
