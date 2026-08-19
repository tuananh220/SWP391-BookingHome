package service;

import entity.Amenity;
import interfaces.IHostAmenityRepository;
import repository.HostAmenityRepository;
import ultis.ValidationUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HostAmenityService {

    private final IHostAmenityRepository repository;

    public HostAmenityService() {
        repository = new HostAmenityRepository();
    }

    public List<Amenity> getAmenities() {
        try {
            return repository.findAll();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Amenity>();
        }
    }

    public Amenity getAmenity(int amenityId) {
        try {
            return repository.findById(amenityId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public int create(Amenity amenity) {
        normalizeAndValidate(amenity);
        try {
            if (repository.existsName(amenity.getAmenityName(), null)) {
                throw new IllegalArgumentException("Tên tiện ích đã tồn tại.");
            }
            return repository.create(amenity);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public boolean update(Amenity amenity) {
        if (getAmenity(amenity.getAmenityId()) == null) {
            throw new IllegalArgumentException("Không tìm thấy tiện ích.");
        }
        normalizeAndValidate(amenity);
        try {
            if (repository.existsName(
                    amenity.getAmenityName(), amenity.getAmenityId())) {
                throw new IllegalArgumentException("Tên tiện ích đã tồn tại.");
            }
            return repository.update(amenity);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean delete(int amenityId) {
        try {
            if (repository.isUsed(amenityId)) {
                throw new IllegalArgumentException(
                        "Không thể xóa vì tiện ích đang được homestay sử dụng."
                );
            }
            return repository.delete(amenityId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private void normalizeAndValidate(Amenity amenity) {
        if (ValidationUtil.isBlank(amenity.getAmenityName())
                || amenity.getAmenityName().trim().length() > 100) {
            throw new IllegalArgumentException("Tên tiện ích không hợp lệ.");
        }
        amenity.setAmenityName(amenity.getAmenityName().trim());
        if (amenity.getIconClass() != null) {
            amenity.setIconClass(amenity.getIconClass().trim());
            if (amenity.getIconClass().isEmpty()) {
                amenity.setIconClass(null);
            }
            if (amenity.getIconClass() != null
                    && amenity.getIconClass().length() > 100) {
                throw new IllegalArgumentException("Icon class quá dài.");
            }
        }
    }
}
