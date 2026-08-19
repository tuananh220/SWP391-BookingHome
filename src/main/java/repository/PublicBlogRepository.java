/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

/**
 *
 * @author Admin
 */
import dal.DBContext;
import entity.Blog;
import interfaces.IPublicBlogRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PublicBlogRepository extends DBContext
        implements IPublicBlogRepository {

    public PublicBlogRepository() {
        super();
    }

    @Override
    public List<Blog> findPublished(String keyword) throws SQLException {
        List<Blog> blogs = new ArrayList<Blog>();
        String sql = blogSelect() + " WHERE b.IsPublished = 1 ";
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (hasKeyword) {
            sql += "AND (b.Title LIKE ? OR b.Content LIKE ?) ";
        }
        sql += "ORDER BY b.CreatedAt DESC";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (hasKeyword) {
                String value = "%" + keyword.trim() + "%";
                statement.setString(1, value);
                statement.setString(2, value);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    blogs.add(mapBlog(resultSet));
                }
            }
        }
        return blogs;
    }

    @Override
    public Blog findPublishedBySlug(String slug) throws SQLException {
        String sql = blogSelect()
                + " WHERE b.Slug = ? AND b.IsPublished = 1";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, slug);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapBlog(resultSet) : null;
            }
        }
    }

    private String blogSelect() {
        return "SELECT b.BlogID, b.AuthorID, b.Title, b.Slug, "
                + "b.ThumbnailURL, b.Content, b.IsPublished, "
                + "b.CreatedAt, b.UpdatedAt, u.FullName AS AuthorName "
                + "FROM Blogs b INNER JOIN Users u ON u.UserID = b.AuthorID";
    }

    private Blog mapBlog(ResultSet resultSet) throws SQLException {
        Blog blog = new Blog();
        blog.setBlogId(resultSet.getInt("BlogID"));
        blog.setAuthorId(resultSet.getInt("AuthorID"));
        blog.setAuthorName(resultSet.getString("AuthorName"));
        blog.setTitle(resultSet.getString("Title"));
        blog.setSlug(resultSet.getString("Slug"));
        blog.setThumbnailUrl(resultSet.getString("ThumbnailURL"));
        blog.setContent(resultSet.getString("Content"));
        blog.setPublished(resultSet.getBoolean("IsPublished"));
        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        Timestamp updatedAt = resultSet.getTimestamp("UpdatedAt");
        if (createdAt != null) {
            blog.setCreatedAt(createdAt.toLocalDateTime());
        }
        if (updatedAt != null) {
            blog.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return blog;
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
