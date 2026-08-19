/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Admin
 */
public class ReviewImage {
    private Integer reviewImageId;
    private Integer reviewId;
    private String imageUrl;

    public ReviewImage() {}

    public Integer getReviewImageId() { return reviewImageId; }
    public void setReviewImageId(Integer reviewImageId) { this.reviewImageId = reviewImageId; }

    public Integer getReviewId() { return reviewId; }
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
