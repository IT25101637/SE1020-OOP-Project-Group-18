package com.bookstore.onlinebookstore.service;

import com.bookstore.onlinebookstore.model.Payment;
import com.bookstore.onlinebookstore.model.PaymentCard;
import com.bookstore.onlinebookstore.repository.PaymentCardRepository;
import com.bookstore.onlinebookstore.repository.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentService(PaymentRepository paymentRepository, PaymentCardRepository paymentCardRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentCardRepository = paymentCardRepository;
    }

    // Generate order ID in format ORD-TIMESTAMP-RANDOM
    public String generateOrderId() {
        long timestamp = System.currentTimeMillis();
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + timestamp + "-" + random;
    }

    // Process payment (mock: always succeeds)
    public Payment processPayment(String userId, String userEmail, String paymentCardId,
                                 double totalAmount, String cartItemsJson) {
        // Validate card exists and belongs to user
        PaymentCard card = paymentCardRepository.findById(paymentCardId);
        if (card == null) {
            throw new IllegalArgumentException("Payment card not found");
        }
        if (!card.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized: Card does not belong to this user");
        }

        // Create payment record
        String paymentId = UUID.randomUUID().toString();
        String orderId = generateOrderId();
        String cardLast4 = card.getCardLast4();

        Payment payment = new Payment(
            paymentId,
            orderId,
            userId,
            userEmail,
            cardLast4,
            totalAmount,
            LocalDateTime.now(),
            "success", // Mock: always successful
            cartItemsJson,
            LocalDateTime.now()
        );

        // Save payment to file
        paymentRepository.add(payment);

        return payment;
    }

    public List<Payment> getPaymentsByUserId(String userId) {
        return paymentRepository.findByUserId(userId);
    }

    public Payment getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    public boolean hasUserPurchasedBook(String userId, String bookId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        for (Payment payment : payments) {
            if (!"success".equalsIgnoreCase(payment.getStatus())) {
                continue;
            }

            try {
                JsonNode itemsNode = objectMapper.readTree(payment.getCartItems());
                if (itemsNode.isArray()) {
                    for (JsonNode item : itemsNode) {
                        if (bookId.equals(item.path("bookId").asText())) {
                            return true;
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    public List<Map<String, Object>> getPurchasedBooksByUserId(String userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        Map<String, Map<String, Object>> byBookId = new LinkedHashMap<>();

        for (Payment payment : payments) {
            if (!"success".equalsIgnoreCase(payment.getStatus())) {
                continue;
            }

            try {
                JsonNode itemsNode = objectMapper.readTree(payment.getCartItems());
                if (!itemsNode.isArray()) {
                    continue;
                }

                for (JsonNode item : itemsNode) {
                    String bookId = item.path("bookId").asText("");
                    if (bookId.isEmpty()) {
                        continue;
                    }

                    Map<String, Object> existing = byBookId.get(bookId);
                    int quantity = item.path("quantity").asInt(1);
                    double price = item.path("price").asDouble(0);

                    if (existing == null) {
                        Map<String, Object> entry = new LinkedHashMap<>();
                        entry.put("bookId", bookId);
                        entry.put("title", item.path("title").asText("Unknown Title"));
                        entry.put("author", item.path("author").asText("Unknown Author"));
                        entry.put("imageUrl", item.path("imageUrl").asText(""));
                        entry.put("price", price);
                        entry.put("totalPurchasedQuantity", quantity);
                        entry.put("lastPurchasedAt", payment.getPaymentDate().toString());
                        byBookId.put(bookId, entry);
                    } else {
                        int totalQty = ((Number) existing.get("totalPurchasedQuantity")).intValue() + quantity;
                        existing.put("totalPurchasedQuantity", totalQty);
                        existing.put("lastPurchasedAt", payment.getPaymentDate().toString());
                        if (((String) existing.get("title")).startsWith("Unknown")) {
                            existing.put("title", item.path("title").asText((String) existing.get("title")));
                        }
                        if (((String) existing.get("author")).startsWith("Unknown")) {
                            existing.put("author", item.path("author").asText((String) existing.get("author")));
                        }
                        if (((String) existing.get("imageUrl")).isEmpty()) {
                            existing.put("imageUrl", item.path("imageUrl").asText(""));
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        return new ArrayList<>(byBookId.values());
    }
}
