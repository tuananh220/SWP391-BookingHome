/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author Admin
 */
import entity.Blog;
import interfaces.IAdminBlogRepository;
import repository.AdminBlogRepository;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ultis.SlugUtil;
import ultis.ValidationUtil;

public class AdminBlogService {

    private final IAdminBlogRepository repository;

    public AdminBlogService() {
        repository = new AdminBlogRepository();
    }

    public List<Blog> getBlogs(String keyword, String status) {
        try {
            return repository.findAll(keyword, status);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Blog>();
        }
    }

    public Blog getBlog(int blogId) {
        try {
            return repository.findById(blogId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public int create(Blog blog) {
        normalizeAndValidate(blog);
        try {
            if (repository.existsSlug(blog.getSlug(), null)) {
                throw new IllegalArgumentException("Slug đã tồn tại.");
            }
            return repository.create(blog);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public boolean update(Blog blog) {
        normalizeAndValidate(blog);
        try {
            if (repository.existsSlug(blog.getSlug(), blog.getBlogId())) {
                throw new IllegalArgumentException("Slug đã tồn tại.");
            }
            return repository.update(blog);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean setPublished(int blogId, boolean published) {
        try {
            return repository.setPublished(blogId, published);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean delete(int blogId) {
        try {
            return repository.delete(blogId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private void normalizeAndValidate(Blog blog) {
        if (ValidationUtil.isBlank(blog.getTitle())
                || blog.getTitle().trim().length() > 200) {
            throw new IllegalArgumentException("Tiêu đề không hợp lệ.");
        }
        blog.setTitle(blog.getTitle().trim());
        if (ValidationUtil.isBlank(blog.getContent())) {
            throw new IllegalArgumentException("Nội dung không được để trống.");
        }
        String slug = ValidationUtil.isBlank(blog.getSlug())
                ? SlugUtil.create(blog.getTitle())
                : SlugUtil.create(blog.getSlug());
        if (slug.isEmpty() || slug.length() > 200) {
            throw new IllegalArgumentException("Slug không hợp lệ.");
        }
        blog.setSlug(slug);
        if (blog.getThumbnailUrl() != null) {
            blog.setThumbnailUrl(blog.getThumbnailUrl().trim());
            if (blog.getThumbnailUrl().isEmpty()) {
                blog.setThumbnailUrl(null);
            }
            if (blog.getThumbnailUrl() != null
                    && blog.getThumbnailUrl().length() > 500) {
                throw new IllegalArgumentException("URL ảnh quá dài.");
            }
        }
    }
}
