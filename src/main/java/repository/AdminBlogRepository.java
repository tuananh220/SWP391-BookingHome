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
import interfaces.IAdminBlogRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AdminBlogRepository extends DBContext
        implements IAdminBlogRepository {

    public AdminBlogRepository() {
        super();
    }

    @Override
    public List<Blog> findAll(String keyword, String publishStatus)
            throws SQLException {
        List<Blog> blogs = new ArrayList<Blog>();
        List<String> parameters = new ArrayList<String>();
        StringBuilder sql = new StringBuilder(blogSelect());
        sql.append(" WHERE 1 = 1 ");
        if (!isBlank(keyword)) {
            sql.append("AND (b.Title LIKE ? OR b.Slug LIKE ? ");
            sql.append("OR u.FullName LIKE ?) ");
            String value = "%" + keyword.trim() + "%";
            parameters.add(value);
            parameters.add(value);
            parameters.add(value);
        }
        if ("published".equals(publishStatus)) {
            sql.append("AND b.IsPublished = 1 ");
        } else if ("draft".equals(publishStatus)) {
            sql.append("AND b.IsPublished = 0 ");
        }
        sql.append("ORDER BY b.CreatedAt DESC");

        ensureConnection();
        try (PreparedStatement statement
                = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setString(i + 1, parameters.get(i));
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
    public Blog findById(int blogId) throws SQLException {
        String sql = blogSelect() + " WHERE b.BlogID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, blogId);
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
                + "VALUES (?, ?, ?, ?, ?, ?)";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(statement, blog, false);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    @Override
    public boolean update(Blog blog) throws SQLException {
        String sql = "UPDATE Blogs SET Title = ?, Slug = ?, "
                + "ThumbnailURL = ?, Content = ?, IsPublished = ?, "
                + "UpdatedAt = SYSDATETIME() WHERE BlogID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, blog, true);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean setPublished(int blogId, boolean published)
            throws SQLException {
        String sql = "UPDATE Blogs SET IsPublished = ?, "
                + "UpdatedAt = SYSDATETIME() WHERE BlogID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, published);
            statement.setInt(2, blogId);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int blogId) throws SQLException {
        String sql = "DELETE FROM Blogs WHERE BlogID = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, blogId);
            return statement.executeUpdate() > 0;
        }
    }

    private void setParameters(PreparedStatement statement, Blog blog,
            boolean update) throws SQLException {
        int index = 1;
        if (!update) {
            statement.setInt(index++, blog.getAuthorId());
        }
        statement.setString(index++, blog.getTitle());
        statement.setString(index++, blog.getSlug());
        statement.setString(index++, blog.getThumbnailUrl());
        statement.setString(index++, blog.getContent());
        statement.setBoolean(index++, blog.isPublished());
        if (update) {
            statement.setInt(index, blog.getBlogId());
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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
