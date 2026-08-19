/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

/**
 *
 * @author Admin
 */
import entity.BookingStatusStat;
import entity.HostDashboardSummary;
import entity.RevenuePoint;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface IHostDashboardRepository {

    HostDashboardSummary getSummary(int hostId, LocalDate fromDate)
            throws SQLException;

    List<RevenuePoint> getDailyRevenue(int hostId, LocalDate fromDate,
            LocalDate toDate) throws SQLException;

    List<RevenuePoint> getMonthlyRevenue(int hostId, LocalDate fromDate,
            LocalDate toDate) throws SQLException;

    List<BookingStatusStat> getBookingStatusStats(int hostId,
            LocalDate fromDate)
            throws SQLException;
}
