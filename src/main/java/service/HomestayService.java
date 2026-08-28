/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author Admin
 */
import entity.Amenity;
import entity.Homestay;
import entity.HomestaySearchCriteria;
import interfaces.IHomestayRepository;
import repository.HomestayRepository;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HomestayService {

    private final IHomestayRepository homestayRepository;

    public HomestayService() {
        this.homestayRepository = new HomestayRepository();
    }

    public List<Homestay> search(HomestaySearchCriteria criteria) {
        validateCriteria(criteria);
        try {
            return homestayRepository.search(criteria);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Homestay>();
        }
    }

    public Homestay getDetail(int homestayId) {
        if (homestayId <= 0) {
            return null;
        }
        try {
            return homestayRepository.findActiveById(homestayId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public List<Amenity> getAmenities() {
        try {
            return homestayRepository.findAllAmenities();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Amenity>();
        }
    }

    public List<String> getCities() {
        try {
            return homestayRepository.findActiveCities();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<String>();
        }
    }

    private void validateCriteria(HomestaySearchCriteria criteria) {
        if (criteria == null) {
            throw new IllegalArgumentException("Thông tin tìm kiếm không hợp lệ.");
        }

        LocalDate checkIn = criteria.getCheckInDate();
        LocalDate checkOut = criteria.getCheckOutDate();
        if ((checkIn == null) != (checkOut == null)) {
            throw new IllegalArgumentException(
                "Vui lòng chọn cả ngày nhận và ngày trả phòng hoặc bỏ trống cả hai."
            );
        }
        if (checkIn != null && checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Ngày nhận phòng không được ở trong quá khứ."
            );
        }
        if (checkIn != null && !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException(
                    "Ngày trả phòng phải sau ngày nhận phòng."
            );
        }

        BigDecimal minPrice = criteria.getMinPrice();
        BigDecimal maxPrice = criteria.getMaxPrice();
        if (minPrice != null && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException(
                    "Giá thấp nhất không được lớn hơn giá cao nhất."
            );
        }
    }
}
