package com.bookstore.onlinebookstore.repository;

import com.bookstore.onlinebookstore.model.Payment;
import com.bookstore.onlinebookstore.util.FileHandler;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentRepository {
    private static final String FILE_PATH = "src/main/resources/data/payments.txt";

    public void add(Payment payment) {
        List<String> lines = FileHandler.readFile(FILE_PATH);
        lines.add(payment.toPipeDelimitedString());
        FileHandler.writeFile(FILE_PATH, lines);
    }

    public List<Payment> findByUserId(String userId) {
        List<Payment> payments = new ArrayList<>();
        List<String> lines = FileHandler.readFile(FILE_PATH);
        
        for (String line : lines) {
            Payment payment = Payment.fromPipeDelimitedString(line.trim());
            if (payment != null && payment.getUserId().equals(userId)) {
                payments.add(payment);
            }
        }
        return payments;
    }

    public Payment findById(String paymentId) {
        List<String> lines = FileHandler.readFile(FILE_PATH);
        for (String line : lines) {
            Payment payment = Payment.fromPipeDelimitedString(line.trim());
            if (payment != null && payment.getPaymentId().equals(paymentId)) {
                return payment;
            }
        }
        return null;
    }

    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        List<String> lines = FileHandler.readFile(FILE_PATH);
        
        for (String line : lines) {
            Payment payment = Payment.fromPipeDelimitedString(line.trim());
            if (payment != null) {
                payments.add(payment);
            }
        }
        return payments;
    }
}
