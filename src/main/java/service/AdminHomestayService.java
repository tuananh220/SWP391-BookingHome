/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author Admin
 */
import entity.CancellationPolicy;
import entity.Homestay;
import interfaces.IAdminHomestayRepository;
import repository.AdminHomestayRepository;
import ultis.ValidationUtil;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ultis.ValidationUtil;

public class AdminHomestayService {

    private final IAdminHomestayRepository repository;

    public AdminHomestayService() {
        repository = new AdminHomestayRepository();
    }

    public List<Homestay> getHomestays(String keyword, String status) {
        try {
            return repository.findAll(keyword, status);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Homestay>();
        }
    }

    public Homestay getHomestay(int homestayId) {
        try {
            return repository.findById(homestayId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public List<CancellationPolicy> getPolicies() {
        try {
            return repository.findPolicies();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<CancellationPolicy>();
        }
    }

    public boolean update(Homestay homestay) {
        validate(homestay);
        try {
            return repository.update(homestay);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(int homestayId, String status, int adminId,
            String reason) {
        if (!Arrays.asList("Pending", "Active", "Rejected", "Hidden")
                .contains(status)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        }
        if ("Rejected".equals(status) && ValidationUtil.isBlank(reason)) {
            throw new IllegalArgumentException("Vui lòng nhập lý do từ chối.");
        }
        if (reason != null && reason.trim().length() > 255) {
            throw new IllegalArgumentException("Lý do quá dài.");
        }
        try {
            return repository.updateStatus(
                    homestayId, status, adminId,
                    ValidationUtil.isBlank(reason) ? null : reason.trim()
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private void validate(Homestay homestay) {
        if (ValidationUtil.isBlank(homestay.getTitle())
                || homestay.getTitle().length() > 200) {
            throw new IllegalArgumentException("Tên homestay không hợp lệ.");
        }
        if (ValidationUtil.isBlank(homestay.getAddress())
                || ValidationUtil.isBlank(homestay.getCity())
                || ValidationUtil.isBlank(homestay.getDistrict())) {
            throw new IllegalArgumentException("Địa chỉ không hợp lệ.");
        }
        if (homestay.getPricePerNight() == null
                || homestay.getPricePerNight().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá không hợp lệ.");
        }
        if (homestay.getMaxGuests() <= 0) {
            throw new IllegalArgumentException("Số khách không hợp lệ.");
        }
    }
}
