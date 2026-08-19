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
import interfaces.ICustomerBlogRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CustomerBlogRepository extends DBContext
        implements ICustomerBlogRepository {

    public CustomerBlogRepository() {
        super();
    }

    @Override
    public List<Blog> findByAuthorId(int authorId) throws SQLException {
        List<Blog> blogs = new ArrayList<Blog>();
        String sql = blogSelect()
                + " WHERE b.AuthorID = ? ORDER BY b.CreatedAt DESC";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, authorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    blogs.add(mapBlog(resultSet));
                }
            }
        }
        return blogs;
    }

    @Override
    public Blog findByIdAndAuthorId(int blogId, int authorId)
            throws SQLException {
        String sql = blogSelect()
                + " WHERE b.BlogID = ? AND b.AuthorID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, blogId);
            statement.setInt(2, authorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapBlog(resultSet) : null;
            }
        }
    }

    @Override
    public boolean existsSlug(String slug, Integer excludedBlogId)
            throws SQLException {
        String sql = "SELECT 1 FROM Blogs WHERE Slug = ? "
                + (excludedBlogId == null ? "" : "AND BlogID <> ?");
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, slug);
            if (excludedBlogId != null) {
                statement.setInt(2, excludedBlogId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public int create(Blog blog) throws SQLException {
        String sql = "INSERT INTO Blogs "
                + "(AuthorID, Title, Slug, ThumbnailURL, Content, IsPublished) "
                + "VALUES (?, ?, ?, ?, ?, 0)";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, blog.getAuthorId());
            statement.setString(2, blog.getTitle());
            statement.setString(3, blog.getSlug());
            statement.setString(4, blog.getThumbnailUrl());
            statement.setString(5, blog.getContent());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    @Override
    public boolean updateAndResubmit(Blog blog) throws SQLException {
        String sql = "UPDATE Blogs SET Title = ?, Slug = ?, "
                + "ThumbnailURL = ?, Content = ?, IsPublished = 0, "
                + "UpdatedAt = SYSDATETIME() "
                + "WHERE BlogID = ? AND AuthorID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, blog.getTitle());
            statement.setString(2, blog.getSlug());
            statement.setString(3, blog.getThumbnailUrl());
            statement.setString(4, blog.getContent());
            statement.setInt(5, blog.getBlogId());
            statement.setInt(6, blog.getAuthorId());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int blogId, int authorId) throws SQLException {
        String sql = "DELETE FROM Blogs WHERE BlogID = ? AND AuthorID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, blogId);
            statement.setInt(2, authorId);
            return statement.executeUpdate() > 0;
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
