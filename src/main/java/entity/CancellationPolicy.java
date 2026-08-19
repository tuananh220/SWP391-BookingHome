/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Admin
 */
public class CancellationPolicy {

    private int policyId;
    private String policyName;
    private String description;
    private int fullRefundDays;
    private int partialRefundDays;
    private double partialRefundPercent;
    private boolean active;
    private Integer createdById;

    public CancellationPolicy() {
    }

    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFullRefundDays() {
        return fullRefundDays;
    }

    public void setFullRefundDays(int fullRefundDays) {
        this.fullRefundDays = fullRefundDays;
    }

    public int getPartialRefundDays() {
        return partialRefundDays;
    }

    public void setPartialRefundDays(int partialRefundDays) {
        this.partialRefundDays = partialRefundDays;
    }

    public double getPartialRefundPercent() {
        return partialRefundPercent;
    }

    public void setPartialRefundPercent(double partialRefundPercent) {
        this.partialRefundPercent = partialRefundPercent;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Integer createdById) {
        this.createdById = createdById;
    }
}
