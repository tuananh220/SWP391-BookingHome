/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

/**
 *
 * @author Admin
 */
import dal.DBContext;
import entity.BookingStatusStat;
import entity.HostDashboardSummary;
import entity.RevenuePoint;
import interfaces.IHostDashboardRepository;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HostDashboardRepository extends DBContext
        implements IHostDashboardRepository {

    public HostDashboardRepository() {
        super();
    }

    @Override
    public HostDashboardSummary getSummary(int hostId, LocalDate fromDate)
            throws SQLException {
        String sql = "SELECT "
                + "(SELECT COALESCE(SUM(p.Amount), 0) FROM Payments p "
                + "INNER JOIN Bookings b ON b.BookingID = p.BookingID "
                + "INNER JOIN Homestays h ON h.HomestayID = b.HomestayID "
                + "WHERE h.HostID = ? AND p.PaymentStatus = 'Completed' "
                + "AND p.PaidAt >= ?) AS TotalRevenue, "
                + "COUNT(*) AS TotalBookings, "
                + "SUM(CASE WHEN b.BookingStatus = 'Pending' THEN 1 ELSE 0 END) AS PendingBookings, "
                + "SUM(CASE WHEN b.BookingStatus = 'Confirmed' THEN 1 ELSE 0 END) AS ConfirmedBookings, "
                + "SUM(CASE WHEN b.BookingStatus = 'Cancelled' THEN 1 ELSE 0 END) AS CancelledBookings, "
                + "SUM(CASE WHEN b.BookingStatus = 'Completed' THEN 1 ELSE 0 END) AS CompletedBookings "
                + "FROM Bookings b INNER JOIN Homestays h "
                + "ON h.HomestayID = b.HomestayID "
                + "WHERE h.HostID = ? AND b.CreatedAt >= ?";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hostId);
            statement.setDate(2, Date.valueOf(fromDate));
            statement.setInt(3, hostId);
            statement.setDate(4, Date.valueOf(fromDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                HostDashboardSummary summary = new HostDashboardSummary();
                if (resultSet.next()) {
                    BigDecimal revenue = resultSet.getBigDecimal("TotalRevenue");
                    summary.setTotalRevenue(
                            revenue == null ? BigDecimal.ZERO : revenue
                    );
                    summary.setTotalBookings(
                            resultSet.getInt("TotalBookings")
                    );
                    summary.setPendingBookings(
                            resultSet.getInt("PendingBookings")
                    );
                    summary.setConfirmedBookings(
                            resultSet.getInt("ConfirmedBookings")
                    );
                    summary.setCancelledBookings(
                            resultSet.getInt("CancelledBookings")
                    );
                    summary.setCompletedBookings(
                            resultSet.getInt("CompletedBookings")
                    );
                }
                return summary;
            }
        }
    }

    @Override
    public List<RevenuePoint> getDailyRevenue(
            int hostId, LocalDate fromDate, LocalDate toDate
    ) throws SQLException {
        String sql = "SELECT CONVERT(VARCHAR(10), CAST(p.PaidAt AS DATE), 23) AS Label, "
                + "SUM(p.Amount) AS Revenue, "
                + "COUNT(DISTINCT p.BookingID) AS BookingCount "
                + "FROM Payments p "
                + "INNER JOIN Bookings b ON b.BookingID = p.BookingID "
                + "INNER JOIN Homestays h ON h.HomestayID = b.HomestayID "
                + "WHERE h.HostID = ? AND p.PaymentStatus = 'Completed' "
                + "AND p.PaidAt >= ? AND p.PaidAt < DATEADD(DAY, 1, ?) "
                + "GROUP BY CAST(p.PaidAt AS DATE) "
                + "ORDER BY CAST(p.PaidAt AS DATE)";
        return queryRevenue(sql, hostId, fromDate, toDate);
    }

    @Override
    public List<RevenuePoint> getMonthlyRevenue(
            int hostId, LocalDate fromDate, LocalDate toDate
    ) throws SQLException {
        String sql = "SELECT CONCAT(YEAR(p.PaidAt), '-', "
                + "RIGHT('0' + CAST(MONTH(p.PaidAt) AS VARCHAR(2)), 2)) AS Label, "
                + "SUM(p.Amount) AS Revenue, "
                + "COUNT(DISTINCT p.BookingID) AS BookingCount "
                + "FROM Payments p "
                + "INNER JOIN Bookings b ON b.BookingID = p.BookingID "
                + "INNER JOIN Homestays h ON h.HomestayID = b.HomestayID "
                + "WHERE h.HostID = ? AND p.PaymentStatus = 'Completed' "
                + "AND p.PaidAt >= ? AND p.PaidAt < DATEADD(DAY, 1, ?) "
                + "GROUP BY YEAR(p.PaidAt), MONTH(p.PaidAt) "
                + "ORDER BY YEAR(p.PaidAt), MONTH(p.PaidAt)";
        return queryRevenue(sql, hostId, fromDate, toDate);
    }

    @Override
    public List<BookingStatusStat> getBookingStatusStats(
            int hostId, LocalDate fromDate
    ) throws SQLException {
        List<BookingStatusStat> stats = new ArrayList<BookingStatusStat>();
        String sql = "SELECT b.BookingStatus, COUNT(*) AS Total "
                + "FROM Bookings b INNER JOIN Homestays h "
                + "ON h.HomestayID = b.HomestayID "
                + "WHERE h.HostID = ? AND b.CreatedAt >= ? "
                + "GROUP BY b.BookingStatus ORDER BY Total DESC";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hostId);
            statement.setDate(2, Date.valueOf(fromDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    BookingStatusStat stat = new BookingStatusStat();
                    stat.setStatus(resultSet.getString("BookingStatus"));
                    stat.setTotal(resultSet.getInt("Total"));
                    stats.add(stat);
                }
            }
        }
        return stats;
    }

    private List<RevenuePoint> queryRevenue(
            String sql, int hostId, LocalDate fromDate, LocalDate toDate
    ) throws SQLException {
        List<RevenuePoint> points = new ArrayList<RevenuePoint>();
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hostId);
            statement.setDate(2, Date.valueOf(fromDate));
            statement.setDate(3, Date.valueOf(toDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    RevenuePoint point = new RevenuePoint();
                    point.setLabel(resultSet.getString("Label"));
                    point.setAmount(resultSet.getBigDecimal("Revenue"));
                    point.setBookingCount(resultSet.getInt("BookingCount"));
                    points.add(point);
                }
            }
        }
        return points;
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
