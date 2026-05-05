package com.bookstore.onlinebookstore.service;

import com.bookstore.onlinebookstore.dto.PaymentCardDTO;
import com.bookstore.onlinebookstore.model.PaymentCard;
import com.bookstore.onlinebookstore.repository.PaymentCardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentCardService {
    private final PaymentCardRepository paymentCardRepository;

    public PaymentCardService(PaymentCardRepository paymentCardRepository) {
        this.paymentCardRepository = paymentCardRepository;
    }

    // Validation methods
    public List<String> validateCard(String cardNumber, String expiryMonth, String expiryYear, String cardholderName) {
        List<String> errors = new ArrayList<>();

        // Validate card number - 16 digits only
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            errors.add("Card number is required");
        } else {
            String cleanNumber = cardNumber.replaceAll("\\s+", "");
            if (cleanNumber.length() != 16 || !cleanNumber.matches("\\d+")) {
                errors.add("Card number must be exactly 16 digits");
            }
        }

        // Validate expiry month - 01-12
        if (expiryMonth == null || expiryMonth.trim().isEmpty()) {
            errors.add("Expiry month is required");
        } else {
            try {
                int month = Integer.parseInt(expiryMonth);
                if (month < 1 || month > 12) {
                    errors.add("Expiry month must be between 01 and 12");
                }
            } catch (NumberFormatException e) {
                errors.add("Expiry month must be a valid number");
            }
        }

        // Validate expiry year - must be current year or later
        if (expiryYear == null || expiryYear.trim().isEmpty()) {
            errors.add("Expiry year is required");
        } else {
            try {
                int year = Integer.parseInt(expiryYear);
                int currentYear = LocalDateTime.now().getYear();
                if (year < currentYear) {
                    errors.add("Expiry year cannot be in the past");
                }
                // Also check if month/year combo has expired
                if (year == currentYear) {
                    int month = Integer.parseInt(expiryMonth);
                    int currentMonth = LocalDateTime.now().getMonthValue();
                    if (month < currentMonth) {
                        errors.add("Card has expired");
                    }
                }
            } catch (NumberFormatException e) {
                errors.add("Expiry year must be a valid number (YYYY)");
            }
        }

        // Validate cardholder name
        if (cardholderName == null || cardholderName.trim().isEmpty()) {
            errors.add("Cardholder name is required");
        }

        return errors;
    }

    // CRUD operations
    public PaymentCard addCard(String userId, String cardNumber, String cardholderName,
                             String expiryMonth, String expiryYear) {
        List<String> errors = validateCard(cardNumber, expiryMonth, expiryYear, cardholderName);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        String paymentCardId = UUID.randomUUID().toString();
        PaymentCard card = new PaymentCard(
            paymentCardId,
            userId,
            cardNumber.replaceAll("\\s+", ""),
            cardholderName.trim(),
            expiryMonth,
            expiryYear,
            false, // New cards are not default by default
            LocalDateTime.now()
        );

        paymentCardRepository.add(card);
        return card;
    }

    public List<PaymentCardDTO> getCardsByUserId(String userId) {
        List<PaymentCard> cards = paymentCardRepository.findByUserId(userId);
        List<PaymentCardDTO> dtos = new ArrayList<>();
        for (PaymentCard card : cards) {
            dtos.add(convertToDTO(card));
        }
        return dtos;
    }

    public PaymentCardDTO updateCard(String userId, String paymentCardId, String cardholderName,
                                     String expiryMonth, String expiryYear) {
        PaymentCard card = paymentCardRepository.findById(paymentCardId);
        if (card == null) {
            throw new IllegalArgumentException("Card not found");
        }
        if (!card.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized: Card does not belong to this user");
        }

        // Validate the new expiry info (cardholder name and expiry can be updated, but card number cannot)
        List<String> errors = validateCard(card.getCardNumber(), expiryMonth, expiryYear, cardholderName);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        card.setCardholderName(cardholderName.trim());
        card.setExpiryMonth(expiryMonth);
        card.setExpiryYear(expiryYear);
        paymentCardRepository.update(card);

        return convertToDTO(card);
    }

    public void deleteCard(String userId, String paymentCardId) {
        PaymentCard card = paymentCardRepository.findById(paymentCardId);
        if (card == null) {
            throw new IllegalArgumentException("Card not found");
        }
        if (!card.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized: Card does not belong to this user");
        }

        paymentCardRepository.delete(paymentCardId);
    }

    public void setDefaultCard(String userId, String paymentCardId) {
        List<PaymentCard> userCards = paymentCardRepository.findByUserId(userId);
        
        for (PaymentCard card : userCards) {
            if (card.getPaymentCardId().equals(paymentCardId)) {
                card.setDefault(true);
            } else if (card.isDefault()) {
                card.setDefault(false);
            }
            paymentCardRepository.update(card);
        }
    }

    public PaymentCardDTO getDefaultCard(String userId) {
        List<PaymentCard> cards = paymentCardRepository.findByUserId(userId);
        for (PaymentCard card : cards) {
            if (card.isDefault()) {
                return convertToDTO(card);
            }
        }
        return null;
    }

    // Helper method
    private PaymentCardDTO convertToDTO(PaymentCard card) {
        return new PaymentCardDTO(
            card.getPaymentCardId(),
            card.getCardholderName(),
            card.getCardLast4(),
            card.getExpiryMonth(),
            card.getExpiryYear(),
            card.isDefault(),
            card.getCreatedAt().toString()
        );
    }
}
