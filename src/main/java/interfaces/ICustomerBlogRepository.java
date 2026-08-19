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

public interface ICustomerBlogRepository {

    List<Blog> findByAuthorId(int authorId) throws SQLException;

    Blog findByIdAndAuthorId(int blogId, int authorId)
            throws SQLException;

    boolean existsSlug(String slug, Integer excludedBlogId)
            throws SQLException;

    int create(Blog blog) throws SQLException;

    boolean updateAndResubmit(Blog blog) throws SQLException;

    boolean delete(int blogId, int authorId) throws SQLException;
}
