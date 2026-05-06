package com.bookstore.onlinebookstore.service;

import com.bookstore.onlinebookstore.model.User;
import com.bookstore.onlinebookstore.dto.UserDTO;
import com.bookstore.onlinebookstore.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * UserService - Business Logic Layer
 * Demonstrates Encapsulation and Abstraction
 * Handles user-related business operations
 */
public class UserService {
    
    private UserRepository userRepository;
    
    /**
     * Constructor
     */
    public UserService() {
        this.userRepository = new UserRepository();
    }
    
    /**
     * Register a new user
     * @param username - username
     * @param email - email (unique identifier)
     * @param firstName - first name
     * @param lastName - last name
     * @param phoneNumber - phone number
     * @param password - password
     * @return UserDTO if successful, null if email exists
     */
    public UserDTO registerUser(String username, String email, String firstName,
                               String lastName, String phoneNumber, String password) {
        // Validate input
        if (!isValidEmail(email)) {
            System.out.println("Invalid email format");
            return null;
        }
        
        if (userRepository.userExists(email)) {
            System.out.println("Email already registered");
            return null;
        }
        
        // Create new user
        User user = new User(username, email, firstName, lastName, phoneNumber, password);
        
        // Save to repository
        if (userRepository.create(user)) {
            return convertUserToDTO(user);
        }
        return null;
    }
    
    /**
     * Login user
     * @param email - user email
     * @param password - user password
     * @return UserDTO if credentials are correct, null otherwise
     */
    public UserDTO loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            System.out.println("User not found");
            return null;
        }
        
        // Simple password comparison (in production, use password hashing)
        if (!user.getPassword().equals(password)) {
            System.out.println("Invalid password");
            return null;
        }
        
        return convertUserToDTO(user);
    }
    
    /**
     * Get user profile by email
     * @param email - user email
     * @return UserDTO if found, null otherwise
     */
    public UserDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return convertUserToDTO(user);
        }
        return null;
    }
    
    /**
     * Update user profile
     * @param email - user email
     * @param username - new username
     * @param firstName - new first name
     * @param lastName - new last name
     * @param phoneNumber - new phone number
     * @return UserDTO if successful, null otherwise
     */
    public UserDTO updateUserProfile(String email, String username, String firstName, String lastName, String phoneNumber) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }

        if (username != null && !username.isEmpty()) {
            user.setUsername(username);
        }
        if (firstName != null && !firstName.isEmpty()) {
            user.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            user.setLastName(lastName);
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            user.setPhoneNumber(phoneNumber);
        }

        if (userRepository.update(user)) {
            return convertUserToDTO(user);
        }
        return null;
    }
    
    /**
     * Delete user by email
     * @param email - user email
     * @return true if successful, false otherwise
     */
    public boolean deleteUserAccount(String email) {
        return userRepository.deleteByEmail(email);
    }

    /**
     * Update user profile without username (for profile update endpoint)
     * @param email - user email
     * @param firstName - new first name
     * @param lastName - new last name
     * @param phoneNumber - new phone number
     * @return UserDTO if successful, null otherwise
     */
    public UserDTO updateUser(String email, String firstName, String lastName, String phoneNumber) {
        return updateUserProfile(email, null, firstName, lastName, phoneNumber);
    }
    
    /**
     * Add points to user
     * @param email - user email
     * @param points - points to add
     * @return updated UserDTO or null
     */
    public UserDTO addPointsToUser(String email, int points) {
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            return null;
        }
        
        user.addPoints(points);
        if (userRepository.update(user)) {
            return convertUserToDTO(user);
        }
        return null;
    }
    
    /**
     * Get all users
     * @return List of UserDTOs
     */
    public List<UserDTO> getAllUsers() {
        List<UserDTO> userDTOList = new ArrayList<>();
        Object[] users = userRepository.findAll();
        
        for (Object userObj : users) {
            if (userObj instanceof User) {
                userDTOList.add(convertUserToDTO((User) userObj));
            }
        }
        return userDTOList;
    }
    
    /**
     * Convert User model to UserDTO (for API responses)
     * @param user - User object
     * @return UserDTO object
     */
    private UserDTO convertUserToDTO(User user) {
        return new UserDTO(
            user.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhoneNumber(),
            user.getPoints()
        );
    }
    
    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}

