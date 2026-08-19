/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

/**
 *
 * @author Admin
 */
import entity.Blog;
import java.sql.SQLException;
import java.util.List;

public interface IPublicBlogRepository {

    List<Blog> findPublished(String keyword) throws SQLException;

    Blog findPublishedBySlug(String slug) throws SQLException;
}
