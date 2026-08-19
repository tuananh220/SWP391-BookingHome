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
import interfaces.IAdminPolicyRepository;
import repository.AdminPolicyRepository;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ultis.ValidationUtil;

public class AdminPolicyService {

    private final IAdminPolicyRepository repository;

    public AdminPolicyService() {
        repository = new AdminPolicyRepository();
    }

    public List<CancellationPolicy> getPolicies() {
        try {
            return repository.findAll();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<CancellationPolicy>();
        }
    }

    public CancellationPolicy getPolicy(int policyId) {
        try {
            return repository.findById(policyId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public int create(CancellationPolicy policy) {
        validate(policy);
        try {
            if (repository.existsName(policy.getPolicyName(), null)) {
                throw new IllegalArgumentException(
                        "Tên chính sách đã tồn tại."
                );
            }
            return repository.create(policy);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public boolean update(CancellationPolicy policy) {
        validate(policy);
        try {
            if (repository.existsName(
                    policy.getPolicyName(), policy.getPolicyId())) {
                throw new IllegalArgumentException(
                        "Tên chính sách đã tồn tại."
                );
            }
            return repository.update(policy);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean setActive(int policyId, boolean active) {
        try {
            return repository.setActive(policyId, active);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean delete(int policyId) {
        try {
            if (repository.isUsed(policyId)) {
                throw new IllegalArgumentException(
                        "Chính sách đang được sử dụng, chỉ có thể ngừng hoạt động."
                );
            }
            return repository.delete(policyId);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private void validate(CancellationPolicy policy) {
        if (ValidationUtil.isBlank(policy.getPolicyName())
                || policy.getPolicyName().length() > 100) {
            throw new IllegalArgumentException(
                    "Tên chính sách không hợp lệ."
            );
        }
        policy.setPolicyName(policy.getPolicyName().trim());
        if (policy.getFullRefundDays() < 0
                || policy.getPartialRefundDays() < 0
                || policy.getFullRefundDays()
                < policy.getPartialRefundDays()) {
            throw new IllegalArgumentException(
                    "Số ngày hoàn toàn phần phải lớn hơn hoặc bằng số ngày hoàn một phần."
            );
        }
        if (policy.getPartialRefundPercent() < 0
                || policy.getPartialRefundPercent() > 100) {
            throw new IllegalArgumentException(
                    "Phần trăm hoàn phải từ 0 đến 100."
            );
        }
    }
}
