/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

import entity.Blog;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface IBlogService {

    List<Blog> getPublishedBlogs();

    Blog getBlogBySlug(String slug);

    boolean createBlog(Blog blog);

    boolean updateBlog(Blog blog);
}
