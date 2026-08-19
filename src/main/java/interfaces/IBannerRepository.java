/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

/**
 *
 * @author Admin
 */
import entity.Banner;

import java.sql.SQLException;
import java.util.List;

public interface IBannerRepository {

    List<Banner> findAll() throws SQLException;

    List<Banner> findActive() throws SQLException;

    Banner findById(int bannerId) throws SQLException;

    int create(Banner banner) throws SQLException;

    boolean update(Banner banner) throws SQLException;

    boolean setActive(int bannerId, boolean active) throws SQLException;

    boolean delete(int bannerId) throws SQLException;
}
