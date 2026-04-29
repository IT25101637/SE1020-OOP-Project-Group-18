package com.bookstore.onlinebookstore.dto;

/**
 * AdminDTO - Data Transfer Object for Admin
 * Excludes password for security - never expose passwords in API responses
 * Demonstrates Information Hiding principle
 */
public class AdminDTO {

    private String adminId;
    private String username;
    private String email;
    private String phoneNumber;

    // Default constructor
    public AdminDTO() {
    }

    // Full constructor
    public AdminDTO(String adminId, String username, String email, String phoneNumber) {
        this.adminId = adminId;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "AdminDTO{" +
                "adminId='" + adminId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
