/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author Admin
 */
import entity.Homestay;
import entity.Voucher;
import interfaces.IHostVoucherRepository;
import repository.HostVoucherRepository;
import ultis.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HostVoucherService {

    private final IHostVoucherRepository repository;

    public HostVoucherService() {
        repository = new HostVoucherRepository();
    }

    public List<Voucher> getVouchers(int hostId) {
        try {
            return repository.findByHostId(hostId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Voucher>();
        }
    }

    public Voucher getVoucher(int voucherId, int hostId) {
        try {
            return repository.findByIdAndHostId(voucherId, hostId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public List<Homestay> getHomestays(int hostId) {
        try {
            return repository.findHostHomestays(hostId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<Homestay>();
        }
    }

    public int create(Voucher voucher) {
        normalizeAndValidate(voucher, 0);
        if (voucher.getStartDate() == null
            || voucher.getStartDate().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Ngày bắt đầu voucher không được là ngày/giờ trong quá khứ."
            );
        }
        try {
            if (repository.existsCode(voucher.getVoucherCode(), null)) {
                throw new IllegalArgumentException("Mã voucher đã tồn tại.");
            }
            return repository.create(voucher);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public boolean update(Voucher voucher) {
        Voucher current = getVoucher(
                voucher.getVoucherId(), voucher.getCreatedById()
        );
        if (current == null) {
            throw new IllegalArgumentException("Không tìm thấy voucher.");
        }
        normalizeAndValidate(voucher, current.getUsedCount());
        try {
            if (repository.existsCode(
                    voucher.getVoucherCode(), voucher.getVoucherId())) {
                throw new IllegalArgumentException("Mã voucher đã tồn tại.");
            }
            return repository.update(voucher);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean deactivate(int voucherId, int hostId) {
        try {
            return repository.deactivate(voucherId, hostId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean activate(int voucherId, int hostId) {
        try {
            return repository.activate(voucherId, hostId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private void normalizeAndValidate(Voucher voucher, int usedCount) {
        String code = voucher.getVoucherCode();
        if (ValidationUtil.isBlank(code)) {
            throw new IllegalArgumentException("Mã voucher không được để trống.");
        }
        code = code.trim().toUpperCase();
        if (code.length() > 50 || !code.matches("[A-Z0-9_-]+")) {
            throw new IllegalArgumentException(
                    "Mã voucher chỉ gồm chữ, số, dấu gạch ngang hoặc gạch dưới."
            );
        }
        voucher.setVoucherCode(code);

        BigDecimal rate = voucher.getDiscountRate();
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0
                || rate.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException(
                    "Phần trăm giảm phải lớn hơn 0 và không quá 100."
            );
        }
        if (voucher.getMaxDiscountAmount() != null
                && voucher.getMaxDiscountAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Mức giảm tối đa không hợp lệ."
            );
        }
        if (voucher.getMinOrderValue() == null
                || voucher.getMinOrderValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Giá trị đơn hàng tối thiểu không hợp lệ."
            );
        }
        if (voucher.getStartDate() == null || voucher.getEndDate() == null
                || !voucher.getEndDate().isAfter(voucher.getStartDate())) {
            throw new IllegalArgumentException(
                    "Thời gian kết thúc phải sau thời gian bắt đầu."
            );
        }
        if (voucher.getUsageLimit() <= 0
                || voucher.getUsageLimit() < usedCount) {
            throw new IllegalArgumentException(
                    "Giới hạn sử dụng không được nhỏ hơn số lượt đã dùng."
            );
        }
    }
}
