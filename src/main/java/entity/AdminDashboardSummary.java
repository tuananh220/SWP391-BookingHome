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

public class AdminDashboardSummary {

    private int totalCustomers;
    private int totalOwners;
    private int totalHomestays;
    private int activeHomestays;
    private int pendingHomestays;
    private int totalBookings;
    private int pendingBookings;
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    public AdminDashboardSummary() {
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(int totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public int getTotalOwners() {
        return totalOwners;
    }

    public void setTotalOwners(int totalOwners) {
        this.totalOwners = totalOwners;
    }

    public int getTotalHomestays() {
        return totalHomestays;
    }

    public void setTotalHomestays(int totalHomestays) {
        this.totalHomestays = totalHomestays;
    }

    public int getActiveHomestays() {
        return activeHomestays;
    }

    public void setActiveHomestays(int activeHomestays) {
        this.activeHomestays = activeHomestays;
    }

    public int getPendingHomestays() {
        return pendingHomestays;
    }

    public void setPendingHomestays(int pendingHomestays) {
        this.pendingHomestays = pendingHomestays;
    }

    public int getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }

    public int getPendingBookings() {
        return pendingBookings;
    }

    public void setPendingBookings(int pendingBookings) {
        this.pendingBookings = pendingBookings;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
