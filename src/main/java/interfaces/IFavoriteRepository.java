/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

import entity.Homestay;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface IFavoriteRepository {

    List<Homestay> findByCustomerId(int customerId) throws SQLException;

    boolean exists(int customerId, int homestayId) throws SQLException;

    boolean add(int customerId, int homestayId) throws SQLException;

    boolean remove(int customerId, int homestayId) throws SQLException;
}
