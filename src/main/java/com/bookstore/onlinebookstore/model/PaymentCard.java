package com.bookstore.onlinebookstore.model;

import java.time.LocalDateTime;

public class PaymentCard {
    private String paymentCardId;
    private String userId;
    private String cardNumber;
    private String cardholderName;
    private String expiryMonth;
    private String expiryYear;
    private boolean isDefault;
    private LocalDateTime createdAt;

    public PaymentCard() {}

    public PaymentCard(String paymentCardId, String userId, String cardNumber, String cardholderName,
                      String expiryMonth, String expiryYear, boolean isDefault, LocalDateTime createdAt) {
        this.paymentCardId = paymentCardId;
        this.userId = userId;
        this.cardNumber = cardNumber;
        this.cardholderName = cardholderName;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getPaymentCardId() {
        return paymentCardId;
    }

    public void setPaymentCardId(String paymentCardId) {
        this.paymentCardId = paymentCardId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Get last 4 digits for display
    public String getCardLast4() {
        if (cardNumber != null && cardNumber.length() >= 4) {
            return cardNumber.substring(cardNumber.length() - 4);
        }
        return "";
    }

    // Pipe-delimited format for file storage
    public String toPipeDelimitedString() {
        return paymentCardId + "|" + userId + "|" + cardNumber + "|" + cardholderName + "|" +
               expiryMonth + "|" + expiryYear + "|" + isDefault + "|" + createdAt;
    }

    public static PaymentCard fromPipeDelimitedString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 8) {
            return null;
        }
        return new PaymentCard(
            parts[0],
            parts[1],
            parts[2],
            parts[3],
            parts[4],
            parts[5],
            Boolean.parseBoolean(parts[6]),
            LocalDateTime.parse(parts[7])
        );
    }
}
