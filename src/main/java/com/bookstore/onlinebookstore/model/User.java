package com.bookstore.onlinebookstore.model;

/**
 * User Model - Represents a user in the bookstore application
 * Demonstrates Encapsulation with private fields and public getters/setters
 * Information Hiding - internal state is protected
 */
public class User {
    
    // Private fields - Encapsulation
    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    private int points;
    
    // Default Constructor
    public User() {
        this.points = 0;
    }
    
    // Parameterized Constructor
    public User(String userId, String username, String email, String firstName, 
                String lastName, String phoneNumber, String password, int points) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.points = points;
    }
    
    // Constructor for registration (without userId and points)
    public User(String username, String email, String firstName, 
                String lastName, String phoneNumber, String password) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.points = 0;
    }
    
    // Getters and Setters - Encapsulation
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
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
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
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
    
    public int getPoints() {
        return points;
    }
    
    public void setPoints(int points) {
        this.points = points;
    }
    
    // Additional methods
    public void addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
    }
    
    public void removePoints(int pointsToRemove) {
        if (this.points >= pointsToRemove) {
            this.points -= pointsToRemove;
        }
    }
    
    /**
     * Convert User object to pipe-delimited string format for file storage
     * Format: id|username|email|firstName|lastName|phoneNumber|password|points
     */
    public String toPipeDelimitedString() {
        return String.format("%s|%s|%s|%s|%s|%s|%s|%d",
                userId, username, email, firstName, lastName, phoneNumber, password, points);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", points=" + points +
                '}';
    }
}
