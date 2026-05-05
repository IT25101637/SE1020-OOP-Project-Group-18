package com.bookstore.onlinebookstore.controller;

import com.bookstore.onlinebookstore.model.Payment;
import com.bookstore.onlinebookstore.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin("*")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Process payment
    @PostMapping("/process")
    public Map<String, Object> processPayment(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            String userId = (String) payload.get("userId");
            String userEmail = (String) payload.get("userEmail");
            String paymentCardId = (String) payload.get("paymentCardId");
            double totalAmount = ((Number) payload.get("totalAmount")).doubleValue();
            String cartItemsJson = (String) payload.get("cartItemsJson");

            Payment payment = paymentService.processPayment(userId, userEmail, paymentCardId, totalAmount, cartItemsJson);
            
            response.put("success", true);
            response.put("message", "Payment processed successfully");
            response.put("payment", payment);
            response.put("orderId", payment.getOrderId());
            response.put("paymentId", payment.getPaymentId());
            
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    // Get payment by ID (for receipt)
    @GetMapping("/{paymentId}")
    public Map<String, Object> getPayment(@PathVariable String paymentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Payment payment = paymentService.getPaymentById(paymentId);
            if (payment == null) {
                response.put("success", false);
                response.put("message", "Payment not found");
                return response;
            }
            response.put("success", true);
            response.put("payment", payment);
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    // Get all payments for user
    @GetMapping("/user/{userId}")
    public Map<String, Object> getUserPayments(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Payment> payments = paymentService.getPaymentsByUserId(userId);
            response.put("success", true);
            response.put("payments", payments);
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    // Get all purchased books for user (derived from successful payments)
    @GetMapping("/user/{userId}/books")
    public Map<String, Object> getUserPurchasedBooks(@PathVariable("userId") String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> books = paymentService.getPurchasedBooksByUserId(userId);
            response.put("status", "success");
            response.put("books", books);
            response.put("count", books.size());
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return response;
        }
    }

    // Check whether a user is eligible to review a specific book
    @GetMapping("/user/{userId}/books/{bookId}/purchased")
    public Map<String, Object> hasPurchasedBook(@PathVariable("userId") String userId,
                                                @PathVariable("bookId") String bookId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean purchased = paymentService.hasUserPurchasedBook(userId, bookId);
            response.put("status", "success");
            response.put("purchased", purchased);
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return response;
        }
    }
}
