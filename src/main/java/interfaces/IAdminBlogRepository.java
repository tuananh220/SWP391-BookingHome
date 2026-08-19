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

public interface IAdminBlogRepository {

    List<Blog> findAll(String keyword, String publishStatus)
            throws SQLException;

    Blog findById(int blogId) throws SQLException;

    boolean existsSlug(String slug, Integer excludedBlogId)
            throws SQLException;

    int create(Blog blog) throws SQLException;

    boolean update(Blog blog) throws SQLException;

    boolean setPublished(int blogId, boolean published)
            throws SQLException;

    boolean delete(int blogId) throws SQLException;
}
