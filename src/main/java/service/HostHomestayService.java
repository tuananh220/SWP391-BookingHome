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
import entity.CancellationPolicy;
import entity.Homestay;
import entity.HomestayForm;
import entity.PaymentMethod;
import interfaces.IHostHomestayRepository;
import repository.HostHomestayRepository;
import ultis.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ultis.ValidationUtil;

public class HostHomestayService {

    private final IHostHomestayRepository repository;

    public HostHomestayService() {
        repository = new HostHomestayRepository();
    }

    public List<Homestay> getHomestays(int hostId) {
        try {
            return repository.findByHostId(hostId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Homestay>();
        }
    }

    public HomestayForm getForm(int homestayId, int hostId) {
        try {
            return repository.findFormByIdAndHostId(homestayId, hostId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public List<Amenity> getAmenities() {
        try {
            return repository.findAmenities();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Amenity>();
        }
    }

    public List<PaymentMethod> getPaymentMethods() {
        try {
            return repository.findPaymentMethods();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<PaymentMethod>();
        }
    }

    public List<CancellationPolicy> getPolicies() {
        try {
            return repository.findActivePolicies();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<CancellationPolicy>();
        }
    }

    public int create(HomestayForm form) {
        validate(form);
        try {
            return repository.create(form);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public boolean update(HomestayForm form) {
        validate(form);
        try {
            return repository.update(form);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean hide(int homestayId, int hostId) {
        try {
            return repository.hide(homestayId, hostId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean submitForApproval(int homestayId, int hostId) {
        try {
            return repository.submitForApproval(homestayId, hostId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private void validate(HomestayForm form) {
        if (ValidationUtil.isBlank(form.getTitle())
                || form.getTitle().length() > 200) {
            throw new IllegalArgumentException("Tên homestay không hợp lệ.");
        }
        if (ValidationUtil.isBlank(form.getAddress())
                || form.getAddress().length() > 255) {
            throw new IllegalArgumentException("Địa chỉ không hợp lệ.");
        }
        if (ValidationUtil.isBlank(form.getCity())
                || form.getCity().length() > 100) {
            throw new IllegalArgumentException("Tỉnh / Thành phố không hợp lệ.");
        }
        if (ValidationUtil.isBlank(form.getDistrict())
                || form.getDistrict().length() > 100) {
            throw new IllegalArgumentException("Phường / Xã / Đơn vị hành chính không hợp lệ.");
        }
        if (form.getPricePerNight() == null
                || form.getPricePerNight().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá mỗi đêm không hợp lệ.");
        }
        if (form.getMaxGuests() <= 0) {
            throw new IllegalArgumentException("Số khách tối đa phải lớn hơn 0.");
        }
        if (form.getCancellationPolicyId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn chính sách hủy.");
        }
        if (form.getPaymentMethodIds().isEmpty()) {
            throw new IllegalArgumentException(
                    "Vui lòng chọn ít nhất một phương thức thanh toán."
            );
        }
        if (form.getImageUrls().isEmpty()) {
            throw new IllegalArgumentException(
                    "Vui lòng nhập ít nhất một URL hình ảnh."
            );
        }
        for (String imageUrl : form.getImageUrls()) {
            if (imageUrl.length() > 500) {
                throw new IllegalArgumentException(
                        "URL hình ảnh không được vượt quá 500 ký tự."
                );
            }
        }
        if (form.getLatitude() != null
                && (form.getLatitude().compareTo(BigDecimal.valueOf(-90)) < 0
                || form.getLatitude().compareTo(BigDecimal.valueOf(90)) > 0)) {
            throw new IllegalArgumentException("Vĩ độ không hợp lệ.");
        }
        if (form.getLongitude() != null
                && (form.getLongitude().compareTo(BigDecimal.valueOf(-180)) < 0
                || form.getLongitude().compareTo(BigDecimal.valueOf(180)) > 0)) {
            throw new IllegalArgumentException("Kinh độ không hợp lệ.");
        }
    }
}
