/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

/**
 *
 * @author Admin
 */
import entity.Review;

import java.sql.SQLException;
import java.util.List;

public interface IAdminReviewRepository {

    List<Review> findAll(String keyword, String visibility,
            boolean reportedOnly) throws SQLException;

    Review findById(int reviewId) throws SQLException;

    boolean update(Review review) throws SQLException;

    boolean setVisibility(int reviewId, boolean visible)
            throws SQLException;

    boolean delete(int reviewId) throws SQLException;

    boolean updateReportStatus(int reportId, String status)
            throws SQLException;
}
