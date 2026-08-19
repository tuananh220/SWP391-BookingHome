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
import interfaces.ICustomerBlogRepository;
import repository.CustomerBlogRepository;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ultis.SlugUtil;
import ultis.ValidationUtil;

public class CustomerBlogService {

    private final ICustomerBlogRepository repository;

    public CustomerBlogService() {
        repository = new CustomerBlogRepository();
    }

    public List<Blog> getMyBlogs(int authorId) {
        try {
            return repository.findByAuthorId(authorId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Blog>();
        }
    }

    public Blog getMyBlog(int blogId, int authorId) {
        try {
            return repository.findByIdAndAuthorId(blogId, authorId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public int create(Blog blog) {
        normalizeAndValidate(blog);
        try {
            if (repository.existsSlug(blog.getSlug(), null)) {
                throw new IllegalArgumentException(
                        "Slug đã tồn tại, vui lòng nhập slug khác."
                );
            }
            return repository.create(blog);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public boolean updateAndResubmit(Blog blog) {
        if (getMyBlog(blog.getBlogId(), blog.getAuthorId()) == null) {
            throw new IllegalArgumentException("Không tìm thấy bài viết.");
        }
        normalizeAndValidate(blog);
        try {
            if (repository.existsSlug(blog.getSlug(), blog.getBlogId())) {
                throw new IllegalArgumentException(
                        "Slug đã tồn tại, vui lòng nhập slug khác."
                );
            }
            return repository.updateAndResubmit(blog);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean delete(int blogId, int authorId) {
        try {
            return repository.delete(blogId, authorId);
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
            throw new IllegalArgumentException(
                    "Nội dung bài viết không được để trống."
            );
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
