/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

/**
 *
 * @author Admin
 */
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface IStatisticsService {

    // Cho Host
    BigDecimal getHostRevenueByMonth(Integer hostId, int month, int year);

    int getHostTotalBookings(Integer hostId, LocalDate startDate, LocalDate endDate);

    // Cho Admin
    BigDecimal getTotalSystemRevenue(int month, int year);

    Map<String, Object> getAdminDashboardOverview();
}
