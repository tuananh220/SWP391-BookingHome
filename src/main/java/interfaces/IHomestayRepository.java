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
import entity.Homestay;
import entity.HomestaySearchCriteria;

import java.sql.SQLException;
import java.util.List;

public interface IHomestayRepository {

    List<Homestay> search(HomestaySearchCriteria criteria) throws SQLException;

    Homestay findActiveById(int homestayId) throws SQLException;

    List<Amenity> findAllAmenities() throws SQLException;

    List<String> findActiveCities() throws SQLException;
}
