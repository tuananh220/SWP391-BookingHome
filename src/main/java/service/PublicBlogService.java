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
import interfaces.IPublicBlogRepository;
import repository.PublicBlogRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PublicBlogService {

    private final IPublicBlogRepository repository;

    public PublicBlogService() {
        repository = new PublicBlogRepository();
    }

    public List<Blog> getPublishedBlogs(String keyword) {
        try {
            return repository.findPublished(keyword);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Blog>();
        }
    }

    public Blog getPublishedBlog(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return null;
        }
        try {
            return repository.findPublishedBySlug(slug.trim());
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
