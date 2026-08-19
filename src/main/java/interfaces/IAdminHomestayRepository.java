/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

/**
 *
 * @author Admin
 */
import entity.Amenity;
import entity.CancellationPolicy;
import entity.Homestay;

import java.sql.SQLException;
import java.util.List;

public interface IAdminHomestayRepository {

    List<Homestay> findAll(String keyword, String status)
            throws SQLException;

    Homestay findById(int homestayId) throws SQLException;

    List<CancellationPolicy> findPolicies() throws SQLException;

    boolean update(Homestay homestay) throws SQLException;

    boolean updateStatus(int homestayId, String status, int adminId,
            String reason) throws SQLException;
}
