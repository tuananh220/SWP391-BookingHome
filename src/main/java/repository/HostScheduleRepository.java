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
        return entries;
    }

    @Override
    public boolean save(HomestaySchedule schedule, int hostId)
            throws SQLException {
        String sql = "IF EXISTS (SELECT 1 FROM Homestays "
                + "WHERE HomestayID = ? AND HostID = ?) "
                + "BEGIN MERGE HomestaySchedules AS target "
                + "USING (SELECT ? AS HomestayID, ? AS ScheduleDate) source "
                + "ON target.HomestayID = source.HomestayID "
                + "AND target.ScheduleDate = source.ScheduleDate "
                + "WHEN MATCHED THEN UPDATE SET CustomPrice = ?, "
                + "IsAvailable = ?, LockReason = ? "
                + "WHEN NOT MATCHED THEN INSERT "
                + "(HomestayID, ScheduleDate, CustomPrice, IsAvailable, LockReason) "
                + "VALUES (?, ?, ?, ?, ?); END";

        ensureConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, schedule.getHomestayId());
            statement.setInt(2, hostId);
            statement.setInt(3, schedule.getHomestayId());
            statement.setDate(4, Date.valueOf(schedule.getScheduleDate()));
            statement.setBigDecimal(5, schedule.getCustomPrice());
            statement.setBoolean(6, schedule.isAvailable());
            statement.setString(7, schedule.getLockReason());
            statement.setInt(8, schedule.getHomestayId());
            statement.setDate(9, Date.valueOf(schedule.getScheduleDate()));
            statement.setBigDecimal(10, schedule.getCustomPrice());
            statement.setBoolean(11, schedule.isAvailable());
            statement.setString(12, schedule.getLockReason());
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
