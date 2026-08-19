/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author Admin
 */
import entity.BookingStatusStat;
import entity.HostDashboardData;
import entity.HostDashboardSummary;
import entity.RevenuePoint;
import interfaces.IHostDashboardRepository;
import repository.HostDashboardRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostDashboardService {

    private final IHostDashboardRepository repository;

    public HostDashboardService() {
        repository = new HostDashboardRepository();
    }

    public HostDashboardData getDashboard(int hostId, String period) {
        if (!"7days".equals(period)
                && !"30days".equals(period)
                && !"1year".equals(period)) {
            period = "7days";
        }

        LocalDate toDate = LocalDate.now();
        LocalDate fromDate;
        boolean monthly = "1year".equals(period);
        if ("30days".equals(period)) {
            fromDate = toDate.minusDays(29);
        } else if (monthly) {
            fromDate = YearMonth.from(toDate)
                    .minusMonths(11).atDay(1);
        } else {
            fromDate = toDate.minusDays(6);
        }

        HostDashboardData data = new HostDashboardData();
        data.setPeriod(period);
        data.setFromDate(fromDate);
        data.setToDate(toDate);

        try {
            HostDashboardSummary summary = repository.getSummary(
                    hostId, fromDate
            );
            List<RevenuePoint> rawPoints = monthly
                    ? repository.getMonthlyRevenue(hostId, fromDate, toDate)
                    : repository.getDailyRevenue(hostId, fromDate, toDate);
            List<RevenuePoint> points = monthly
                    ? fillMonths(rawPoints, fromDate, toDate)
                    : fillDays(rawPoints, fromDate, toDate);
            calculateRevenuePercentages(points);

            List<BookingStatusStat> stats
                    = repository.getBookingStatusStats(hostId, fromDate);
            calculateStatusPercentages(stats);

            data.setSummary(summary);
            data.setRevenuePoints(points);
            data.setStatusStats(stats);
        } catch (SQLException exception) {
            exception.printStackTrace();
            data.setSummary(new HostDashboardSummary());
        }
        return data;
    }

    private List<RevenuePoint> fillDays(List<RevenuePoint> raw,
            LocalDate from, LocalDate to) {
        Map<String, RevenuePoint> values = toMap(raw);
        List<RevenuePoint> points = new ArrayList<RevenuePoint>();
        LocalDate date = from;
        while (!date.isAfter(to)) {
            String label = date.toString();
            points.add(values.containsKey(label)
                    ? values.get(label) : emptyPoint(label));
            date = date.plusDays(1);
        }
        return points;
    }

    private List<RevenuePoint> fillMonths(List<RevenuePoint> raw,
            LocalDate from, LocalDate to) {
        Map<String, RevenuePoint> values = toMap(raw);
        List<RevenuePoint> points = new ArrayList<RevenuePoint>();
        YearMonth month = YearMonth.from(from);
        YearMonth endMonth = YearMonth.from(to);
        while (!month.isAfter(endMonth)) {
            String label = month.toString();
            points.add(values.containsKey(label)
                    ? values.get(label) : emptyPoint(label));
            month = month.plusMonths(1);
        }
        return points;
    }

    private Map<String, RevenuePoint> toMap(List<RevenuePoint> points) {
        Map<String, RevenuePoint> values
                = new HashMap<String, RevenuePoint>();
        for (RevenuePoint point : points) {
            values.put(point.getLabel(), point);
        }
        return values;
    }

    private RevenuePoint emptyPoint(String label) {
        RevenuePoint point = new RevenuePoint();
        point.setLabel(label);
        point.setAmount(BigDecimal.ZERO);
        return point;
    }

    private void calculateRevenuePercentages(List<RevenuePoint> points) {
        BigDecimal max = BigDecimal.ZERO;
        for (RevenuePoint point : points) {
            if (point.getAmount().compareTo(max) > 0) {
                max = point.getAmount();
            }
        }
        if (max.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        for (RevenuePoint point : points) {
            int percentage = point.getAmount()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(max, 0, RoundingMode.HALF_UP)
                    .intValue();
            point.setPercentage(percentage);
        }
    }

    private void calculateStatusPercentages(List<BookingStatusStat> stats) {
        int total = 0;
        for (BookingStatusStat stat : stats) {
            total += stat.getTotal();
        }
        if (total == 0) {
            return;
        }
        for (BookingStatusStat stat : stats) {
            stat.setPercentage((int) Math.round(
                    stat.getTotal() * 100.0 / total
            ));
        }
    }
}
