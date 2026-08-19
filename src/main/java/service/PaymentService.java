/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author Admin
 */
import entity.Payment;
import interfaces.IPaymentRepository;
import repository.PaymentRepository;

import java.sql.SQLException;
import java.util.UUID;

public class PaymentService {

    private final IPaymentRepository paymentRepository;

    public PaymentService() {
        paymentRepository = new PaymentRepository();
    }

    public Payment getPayment(int bookingId, int customerId) {
        try {
            return paymentRepository.findLatestByBookingAndCustomer(
                    bookingId, customerId
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public Payment getPendingOnlinePayment(int bookingId, int customerId) {
        Payment payment = getPayment(bookingId, customerId);
        if (payment == null
                || !payment.isOnline()
                || !"Pending".equals(payment.getPaymentStatus())) {
            return null;
        }
        return payment;
    }

    public boolean completePayment(int paymentId, int bookingId,
            int customerId) {
        Payment payment = getPendingOnlinePayment(bookingId, customerId);
        if (payment == null || payment.getPaymentId() != paymentId) {
            throw new IllegalArgumentException(
                    "Giao dịch không tồn tại hoặc đã được xử lý."
            );
        }

        String transactionId = "DEMO-"
                + UUID.randomUUID().toString().replace("-", "")
                        .substring(0, 20).toUpperCase();
        try {
            return paymentRepository.completeOnlinePayment(
                    paymentId, bookingId, customerId, transactionId
            );
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
