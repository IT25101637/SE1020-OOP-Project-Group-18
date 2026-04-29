package com.bookstore.onlinebookstore.model;

/**
 * Admin Model - Represents an admin user in the system
 * Demonstrates OOP principles: Encapsulation with private fields and public getters/setters
 */
public class Admin {

    private String adminId;
    private String username;
    private String email;
    private String phoneNumber;
    private String password;

    // Default constructor
    public Admin() {
    }

    // Full constructor
    public Admin(String adminId, String username, String email, String phoneNumber, String password) {
        this.adminId = adminId;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    // Registration constructor (without adminId)
    public Admin(String username, String email, String phoneNumber, String password) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    // Getters and Setters (Encapsulation)
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Convert to pipe-delimited format for file storage
    public String toPipeDelimitedString() {
        return adminId + "|" + username + "|" + email + "|" + phoneNumber + "|" + password;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
