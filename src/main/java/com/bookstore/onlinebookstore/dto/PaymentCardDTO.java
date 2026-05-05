package com.bookstore.onlinebookstore.dto;

public class PaymentCardDTO {
    private String paymentCardId;
    private String cardholderName;
    private String cardLast4;
    private String expiryMonth;
    private String expiryYear;
    private boolean isDefault;
    private String createdAt;

    public PaymentCardDTO() {}

    public PaymentCardDTO(String paymentCardId, String cardholderName, String cardLast4,
                         String expiryMonth, String expiryYear, boolean isDefault, String createdAt) {
        this.paymentCardId = paymentCardId;
        this.cardholderName = cardholderName;
        this.cardLast4 = cardLast4;
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

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
