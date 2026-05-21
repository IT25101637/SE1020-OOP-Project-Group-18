package com.bookstore.onlinebookstore.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

public class Payment {
    private String paymentId;
    private String orderId;
    private String userId;
    private String userEmail;
    private String cardLast4;
    private double totalAmount;
    private LocalDateTime paymentDate;
    private String status; // success, pending, failed
    private String cartItems; // serialized JSON of cart items
    private LocalDateTime createdAt;

    public Payment() {}

    public Payment(String paymentId, String orderId, String userId, String userEmail, String cardLast4,
                  double totalAmount, LocalDateTime paymentDate, String status, String cartItems,
                  LocalDateTime createdAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.cardLast4 = cardLast4;
        this.totalAmount = totalAmount;
        this.paymentDate = paymentDate;
        this.status = status;
        this.cartItems = cartItems;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCartItems() {
        return cartItems;
    }

    public void setCartItems(String cartItems) {
        this.cartItems = cartItems;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Pipe-delimited format for file storage
    public String toPipeDelimitedString() {
        return paymentId + "|" + orderId + "|" + userId + "|" + userEmail + "|" + cardLast4 + "|" +
               totalAmount + "|" + paymentDate + "|" + status + "|" + cartItems.replace("|", "~") + "|" + createdAt;
    }

    // Flexible formatter that handles 0–9 nanosecond digits
    private static final DateTimeFormatter FLEXIBLE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .optionalEnd()
            .toFormatter();

    public static Payment fromPipeDelimitedString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 10) {
            return null;
        }
        try {
            return new Payment(
                parts[0],
                parts[1],
                parts[2],
                parts[3],
                parts[4],
                Double.parseDouble(parts[5]),
                LocalDateTime.parse(parts[6].trim(), FLEXIBLE_FORMATTER),
                parts[7],
                parts[8].replace("~", "|"),
                LocalDateTime.parse(parts[9].trim(), FLEXIBLE_FORMATTER)
            );
        } catch (Exception e) {
            System.err.println("Error parsing Payment: " + e.getMessage());
            return null;
        }
    }
}