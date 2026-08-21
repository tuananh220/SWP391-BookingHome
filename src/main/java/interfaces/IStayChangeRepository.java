/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

/**
 *
 * @author Admin
 */
import entity.Booking;
import entity.BookingNight;
import entity.StayChangeRequest;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface IStayChangeRepository {

    Booking findEligibleBooking(int bookingId, int customerId)
            throws SQLException;

    Booking findEligibleBookingForRequest(int requestId, int customerId)
            throws SQLException;

    BigDecimal sumNightPrices(int bookingId, LocalDate fromDate,
            LocalDate toDate) throws SQLException;

    int create(StayChangeRequest request) throws SQLException;

    StayChangeRequest findByIdAndCustomerId(int requestId, int customerId)
            throws SQLException;

    boolean updatePending(StayChangeRequest request) throws SQLException;

    List<StayChangeRequest> findByCustomerId(int customerId)
            throws SQLException;

    List<StayChangeRequest> findByHostId(int hostId, String status)
            throws SQLException;

    StayChangeRequest findByIdAndHostId(int requestId, int hostId)
            throws SQLException;

    boolean cancelByCustomer(int requestId, int customerId)
            throws SQLException;

    boolean reject(int requestId, int hostId, String responseNote)
            throws SQLException;

    boolean accept(int requestId, int hostId,
            List<BookingNight> extensionNights)
            throws SQLException;
}
