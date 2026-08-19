/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author Admin
 */
import entity.Banner;
import interfaces.IBannerRepository;
import repository.BannerRepository;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ultis.ValidationUtil;

public class BannerService {

    private final IBannerRepository repository;

    public BannerService() {
        repository = new BannerRepository();
    }

    public List<Banner> getAll() {
        try {
            return repository.findAll();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Banner>();
        }
    }

    public List<Banner> getActive() {
        try {
            return repository.findActive();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Banner>();
        }
    }

    public Banner getById(int bannerId) {
        try {
            return repository.findById(bannerId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public int create(Banner banner) {
        validate(banner);
        try {
            return repository.create(banner);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public boolean update(Banner banner) {
        validate(banner);
        try {
            return repository.update(banner);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean setActive(int bannerId, boolean active) {
        try {
            return repository.setActive(bannerId, active);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean delete(int bannerId) {
        try {
            return repository.delete(bannerId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private void validate(Banner banner) {
        if (ValidationUtil.isBlank(banner.getImageUrl())
                || banner.getImageUrl().trim().length() > 500) {
            throw new IllegalArgumentException("URL hình ảnh không hợp lệ.");
        }
        banner.setImageUrl(banner.getImageUrl().trim());
        if (banner.getTitle() != null) {
            banner.setTitle(banner.getTitle().trim());
            if (banner.getTitle().length() > 100) {
                throw new IllegalArgumentException("Tiêu đề quá dài.");
            }
        }
        if (banner.getTargetUrl() != null) {
            banner.setTargetUrl(banner.getTargetUrl().trim());
            if (banner.getTargetUrl().isEmpty()) {
                banner.setTargetUrl(null);
            }
            if (banner.getTargetUrl() != null
                    && banner.getTargetUrl().length() > 500) {
                throw new IllegalArgumentException("Target URL quá dài.");
            }
        }
        if (banner.getDisplayOrder() < 0) {
            throw new IllegalArgumentException(
                    "Thứ tự hiển thị không được âm."
            );
        }
    }
}
