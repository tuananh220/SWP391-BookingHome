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
import entity.HomestaySchedule;
import interfaces.IHostScheduleRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HostScheduleRepository extends DBContext
        implements IHostScheduleRepository {

    public HostScheduleRepository() {
        super();
    }

    @Override
    public List<HomestaySchedule> findEntries(
            int homestayId, int hostId,
            java.time.LocalDate fromDate, java.time.LocalDate toDate
    ) throws SQLException {
        List<HomestaySchedule> entries
                = new ArrayList<HomestaySchedule>();
        String sql = "SELECT hs.ScheduleID, hs.HomestayID, hs.ScheduleDate, "
                + "hs.CustomPrice, hs.IsAvailable, hs.LockReason, "
                + "bn.BookingID, bn.NightPrice FROM HomestaySchedules hs "
                + "INNER JOIN Homestays h ON h.HomestayID = hs.HomestayID "
                + "LEFT JOIN BookingNights bn ON bn.HomestayID = hs.HomestayID "
                + "AND bn.StayDate = hs.ScheduleDate AND bn.IsActive = 1 "
                + "WHERE hs.HomestayID = ? AND h.HostID = ? "
                + "AND hs.ScheduleDate >= ? AND hs.ScheduleDate <= ? "
                + "ORDER BY hs.ScheduleDate";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            statement.setInt(2, hostId);
            statement.setDate(3, Date.valueOf(fromDate));
            statement.setDate(4, Date.valueOf(toDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    entries.add(mapSchedule(resultSet));
                }
            }
        }

        String bookingSql = "SELECT bn.BookingID, bn.HomestayID, "
                + "bn.StayDate, bn.NightPrice FROM BookingNights bn "
                + "INNER JOIN Homestays h ON h.HomestayID = bn.HomestayID "
                + "WHERE bn.HomestayID = ? AND h.HostID = ? "
                + "AND bn.IsActive = 1 AND bn.StayDate >= ? "
                + "AND bn.StayDate <= ? AND NOT EXISTS ("
                + "SELECT 1 FROM HomestaySchedules hs "
                + "WHERE hs.HomestayID = bn.HomestayID "
                + "AND hs.ScheduleDate = bn.StayDate)";
        try (PreparedStatement statement
                = connection.prepareStatement(bookingSql)) {
            statement.setInt(1, homestayId);
            statement.setInt(2, hostId);
            statement.setDate(3, Date.valueOf(fromDate));
            statement.setDate(4, Date.valueOf(toDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    HomestaySchedule entry = new HomestaySchedule();
                    entry.setHomestayId(resultSet.getInt("HomestayID"));
                    entry.setScheduleDate(
                            resultSet.getDate("StayDate").toLocalDate()
                    );
                    entry.setEffectivePrice(
                            resultSet.getBigDecimal("NightPrice")
                    );
                    entry.setAvailable(false);
                    entry.setBooked(true);
                    entry.setBookingId(resultSet.getInt("BookingID"));
                    entry.setLockReason("Booked");
                    entries.add(entry);
                }
            }
        }

        // Check for dates within active bookings that may not have BookingNights
        String activeBookingsSql = "SELECT DISTINCT b.BookingID, b.HomestayID, "
                + "b.CheckInDate, b.CheckOutDate FROM Bookings b "
                + "INNER JOIN Homestays h ON h.HomestayID = b.HomestayID "
                + "WHERE b.HomestayID = ? AND h.HostID = ? "
                + "AND b.BookingStatus IN ('Pending', 'Confirmed') "
                + "AND b.CheckInDate < ? AND b.CheckOutDate > ?";
        try (PreparedStatement statement
                = connection.prepareStatement(activeBookingsSql)) {
            statement.setInt(1, homestayId);
            statement.setInt(2, hostId);
            statement.setDate(3, Date.valueOf(toDate));
            statement.setDate(4, Date.valueOf(fromDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int bookingId = resultSet.getInt("BookingID");
                    java.time.LocalDate checkIn = resultSet.getDate("CheckInDate")
                            .toLocalDate();
                    java.time.LocalDate checkOut = resultSet.getDate("CheckOutDate")
                            .toLocalDate();

                    // Mark all dates in range [checkIn, checkOut) as booked
                    java.time.LocalDate date = checkIn.isBefore(fromDate)
                            ? fromDate
                            : checkIn;
                    while (date.isBefore(checkOut) && !date.isAfter(toDate)) {
                        final java.time.LocalDate currentDate = date;
                        boolean alreadyExists = entries.stream()
                                .anyMatch(e -> e.getScheduleDate().equals(currentDate)
                                        && e.isBooked());
                        if (!alreadyExists) {
                            HomestaySchedule entry = new HomestaySchedule();
                            entry.setHomestayId(homestayId);
                            entry.setScheduleDate(currentDate);
                            entry.setEffectivePrice(BigDecimal.ZERO);
                            entry.setAvailable(false);
                            entry.setBooked(true);
                            entry.setBookingId(bookingId);
                            entry.setLockReason("Booked");
                            entries.add(entry);
                        }
                        date = date.plusDays(1);
                    }
                }
            }
        }
        return entries;
    }

    @Override
    public boolean save(HomestaySchedule schedule, int hostId)
            throws SQLException {
        ensureConnection();
        String updateSql = "UPDATE hs SET CustomPrice = ?, "
                + "IsAvailable = ?, LockReason = ? "
                + "FROM HomestaySchedules hs "
                + "INNER JOIN Homestays h ON h.HomestayID = hs.HomestayID "
                + "WHERE hs.HomestayID = ? AND h.HostID = ? "
                + "AND hs.ScheduleDate = ?";
        try (PreparedStatement statement
                = connection.prepareStatement(updateSql)) {
            statement.setBigDecimal(1, schedule.getCustomPrice());
            statement.setBoolean(2, schedule.isAvailable());
            statement.setString(3, schedule.getLockReason());
            statement.setInt(4, schedule.getHomestayId());
            statement.setInt(5, hostId);
            statement.setDate(6, Date.valueOf(schedule.getScheduleDate()));
            if (statement.executeUpdate() > 0) {
                return true;
            }
        }

        String insertSql = "INSERT INTO HomestaySchedules "
                + "(HomestayID, ScheduleDate, CustomPrice, IsAvailable, LockReason) "
                + "SELECT ?, ?, ?, ?, ? "
                + "WHERE EXISTS (SELECT 1 FROM Homestays "
                + "WHERE HomestayID = ? AND HostID = ?) "
                + "AND NOT EXISTS (SELECT 1 FROM HomestaySchedules "
                + "WHERE HomestayID = ? AND ScheduleDate = ?)";
        try (PreparedStatement statement
                = connection.prepareStatement(insertSql)) {
            statement.setInt(1, schedule.getHomestayId());
            statement.setDate(2, Date.valueOf(schedule.getScheduleDate()));
            statement.setBigDecimal(3, schedule.getCustomPrice());
            statement.setBoolean(4, schedule.isAvailable());
            statement.setString(5, schedule.getLockReason());
            statement.setInt(6, schedule.getHomestayId());
            statement.setInt(7, hostId);
            statement.setInt(8, schedule.getHomestayId());
            statement.setDate(9, Date.valueOf(schedule.getScheduleDate()));
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean clear(int homestayId, int hostId,
            java.time.LocalDate scheduleDate)
            throws SQLException {
        String sql = "DELETE hs FROM HomestaySchedules hs "
                + "INNER JOIN Homestays h ON h.HomestayID = hs.HomestayID "
                + "WHERE hs.HomestayID = ? AND h.HostID = ? "
                + "AND hs.ScheduleDate = ?";
        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, homestayId);
            statement.setInt(2, hostId);
            statement.setDate(3, Date.valueOf(scheduleDate));
            return statement.executeUpdate() > 0;
        }
    }

    private HomestaySchedule mapSchedule(ResultSet resultSet)
            throws SQLException {
        HomestaySchedule schedule = new HomestaySchedule();
        schedule.setScheduleId(resultSet.getInt("ScheduleID"));
        schedule.setHomestayId(resultSet.getInt("HomestayID"));
        schedule.setScheduleDate(
                resultSet.getDate("ScheduleDate").toLocalDate()
        );
        schedule.setCustomPrice(resultSet.getBigDecimal("CustomPrice"));
        schedule.setAvailable(resultSet.getBoolean("IsAvailable"));
        schedule.setLockReason(resultSet.getString("LockReason"));
        int bookingId = resultSet.getInt("BookingID");
        if (!resultSet.wasNull()) {
            schedule.setBooked(true);
            schedule.setBookingId(bookingId);
            schedule.setEffectivePrice(resultSet.getBigDecimal("NightPrice"));
        }
        return schedule;
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Không có kết nối tới cơ sở dữ liệu.");
        }
    }
}
