/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Admin
 */
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class HomestayForm {

    private int homestayId;
    private int hostId;
    private Integer cancellationPolicyId;
    private String title;
    private String description;
    private String address;
    private String city;
    private String district;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal pricePerNight;
    private int maxGuests;
    private List<Integer> amenityIds = new ArrayList<Integer>();
    private List<Integer> paymentMethodIds = new ArrayList<Integer>();
    private List<String> imageUrls = new ArrayList<String>();

    public HomestayForm() {
    }

    public int getHomestayId() {
        return homestayId;
    }

    public void setHomestayId(int homestayId) {
        this.homestayId = homestayId;
    }

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public Integer getCancellationPolicyId() {
        return cancellationPolicyId;
    }

    public void setCancellationPolicyId(Integer cancellationPolicyId) {
        this.cancellationPolicyId = cancellationPolicyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public List<Integer> getAmenityIds() {
        return amenityIds;
    }

    public void setAmenityIds(List<Integer> amenityIds) {
        this.amenityIds = amenityIds;
    }

    public List<Integer> getPaymentMethodIds() {
        return paymentMethodIds;
    }

    public void setPaymentMethodIds(List<Integer> paymentMethodIds) {
        this.paymentMethodIds = paymentMethodIds;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getImageUrlsText() {
        StringBuilder value = new StringBuilder();
        for (String imageUrl : imageUrls) {
            if (value.length() > 0) {
                value.append('\n');
            }
            value.append(imageUrl);
        }
        return value.toString();
    }
}
