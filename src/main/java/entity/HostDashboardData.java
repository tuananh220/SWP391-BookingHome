/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Admin
 */

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HostDashboardData {

    private String period;
    private LocalDate fromDate;
    private LocalDate toDate;
    private HostDashboardSummary summary;
    private List<RevenuePoint> revenuePoints = new ArrayList<RevenuePoint>();
    private List<BookingStatusStat> statusStats = new ArrayList<BookingStatusStat>();

    public HostDashboardData() {
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public HostDashboardSummary getSummary() {
        return summary;
    }

    public void setSummary(HostDashboardSummary summary) {
        this.summary = summary;
    }

    public List<RevenuePoint> getRevenuePoints() {
        return revenuePoints;
    }

    public void setRevenuePoints(List<RevenuePoint> revenuePoints) {
        this.revenuePoints = revenuePoints;
    }

    public List<BookingStatusStat> getStatusStats() {
        return statusStats;
    }

    public void setStatusStats(List<BookingStatusStat> statusStats) {
        this.statusStats = statusStats;
    }
}
