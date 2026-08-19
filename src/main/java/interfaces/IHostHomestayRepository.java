/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

import entity.Amenity;
import entity.CancellationPolicy;
import entity.Homestay;
import entity.HomestayForm;
import entity.PaymentMethod;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface IHostHomestayRepository {

    List<Homestay> findByHostId(int hostId) throws SQLException;

    HomestayForm findFormByIdAndHostId(int homestayId, int hostId)
            throws SQLException;

    List<Amenity> findAmenities() throws SQLException;

    List<PaymentMethod> findPaymentMethods() throws SQLException;

    List<CancellationPolicy> findActivePolicies() throws SQLException;

    int create(HomestayForm form) throws SQLException;

    boolean update(HomestayForm form) throws SQLException;

    boolean hide(int homestayId, int hostId) throws SQLException;

    boolean submitForApproval(int homestayId, int hostId)
            throws SQLException;
}
