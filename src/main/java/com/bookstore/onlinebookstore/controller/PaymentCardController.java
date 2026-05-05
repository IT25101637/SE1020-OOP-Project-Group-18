package com.bookstore.onlinebookstore.controller;

import com.bookstore.onlinebookstore.dto.PaymentCardDTO;
import com.bookstore.onlinebookstore.service.PaymentCardService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment-cards")
@CrossOrigin("*")
public class PaymentCardController {
    private final PaymentCardService paymentCardService;

    public PaymentCardController(PaymentCardService paymentCardService) {
        this.paymentCardService = paymentCardService;
    }

    // Get all cards for a user
    @GetMapping("/{userId}")
    public Map<String, Object> getCards(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<PaymentCardDTO> cards = paymentCardService.getCardsByUserId(userId);
            response.put("success", true);
            response.put("cards", cards);
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    // Add new card
    @PostMapping("/{userId}")
    public Map<String, Object> addCard(@PathVariable String userId,
                                       @RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            String cardNumber = payload.get("cardNumber");
            String cardholderName = payload.get("cardholderName");
            String expiryMonth = payload.get("expiryMonth");
            String expiryYear = payload.get("expiryYear");

            paymentCardService.addCard(userId, cardNumber, cardholderName, expiryMonth, expiryYear);
            response.put("success", true);
            response.put("message", "Card added successfully");
            response.put("cards", paymentCardService.getCardsByUserId(userId));
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    // Update card
    @PutMapping("/{userId}/{cardId}")
    public Map<String, Object> updateCard(@PathVariable String userId,
                                          @PathVariable String cardId,
                                          @RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            String cardholderName = payload.get("cardholderName");
            String expiryMonth = payload.get("expiryMonth");
            String expiryYear = payload.get("expiryYear");

            paymentCardService.updateCard(userId, cardId, cardholderName, expiryMonth, expiryYear);
            response.put("success", true);
            response.put("message", "Card updated successfully");
            response.put("cards", paymentCardService.getCardsByUserId(userId));
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    // Delete card
    @DeleteMapping("/{userId}/{cardId}")
    public Map<String, Object> deleteCard(@PathVariable String userId,
                                          @PathVariable String cardId) {
        Map<String, Object> response = new HashMap<>();
        try {
            paymentCardService.deleteCard(userId, cardId);
            response.put("success", true);
            response.put("message", "Card deleted successfully");
            response.put("cards", paymentCardService.getCardsByUserId(userId));
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    // Set default card
    @PostMapping("/{userId}/{cardId}/set-default")
    public Map<String, Object> setDefaultCard(@PathVariable String userId,
                                             @PathVariable String cardId) {
        Map<String, Object> response = new HashMap<>();
        try {
            paymentCardService.setDefaultCard(userId, cardId);
            response.put("success", true);
            response.put("message", "Default card updated");
            response.put("cards", paymentCardService.getCardsByUserId(userId));
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    // Get default card
    @GetMapping("/{userId}/default")
    public Map<String, Object> getDefaultCard(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            PaymentCardDTO defaultCard = paymentCardService.getDefaultCard(userId);
            response.put("success", true);
            response.put("defaultCard", defaultCard);
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }
}
