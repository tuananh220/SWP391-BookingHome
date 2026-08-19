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

public interface IPaymentService {

    String createVnPayPaymentUrl(Integer bookingId, String ipAddress);

    boolean processPaymentCallback(String transactionId, String responseCode, Integer bookingId);

    Payment getPaymentByBookingId(Integer bookingId);
}
