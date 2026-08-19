/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

import entity.Booking;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface IHostBookingRepository {

    List<Booking> findByHostId(int hostId, String status)
            throws SQLException;

    Booking findByIdAndHostId(int bookingId, int hostId)
            throws SQLException;

    boolean confirmBooking(int bookingId, int hostId)
            throws SQLException;

    boolean rejectBooking(int bookingId, int hostId, String reason)
            throws SQLException;

    boolean cancelBooking(int bookingId, int hostId, String reason,
            java.math.BigDecimal refundAmount) throws SQLException;
}
