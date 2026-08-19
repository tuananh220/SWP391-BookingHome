/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

/**
 *
 * @author Admin
 */
import entity.Payment;
import java.sql.SQLException;

public interface IPaymentRepository {

    Payment findLatestByBookingAndCustomer(int bookingId, int customerId)
            throws SQLException;

    boolean completeOnlinePayment(int paymentId, int bookingId,
            int customerId, String transactionId)
            throws SQLException;
}
