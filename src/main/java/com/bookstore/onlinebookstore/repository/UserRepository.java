package com.bookstore.onlinebookstore.repository;

import com.bookstore.onlinebookstore.model.User;
import com.bookstore.onlinebookstore.util.FileHandler;
import com.bookstore.onlinebookstore.util.LinkedListUtil;
import java.util.List;

/**
 * UserRepository - Data Access Layer
 * Demonstrates CRUD operations and file handling
 * Manages user data persistence in users.txt file
 */
public class UserRepository {
    
    private static final String USERS_FILE_PATH = "src/main/resources/data/users.txt";
    private LinkedListUtil<User> userList;
    
    /**
     * Constructor - loads all users from file
     */
    public UserRepository() {
        this.userList = new LinkedListUtil<>();
        loadUsersFromFile();
    }
    
    /**
     * Load all users from file into memory
     */
    private void loadUsersFromFile() {
        List<String> lines = FileHandler.readFile(USERS_FILE_PATH);
        for (String line : lines) {
            if (!line.isEmpty()) {
                User user = parseUserFromLine(line);
                if (user != null) {
                    userList.add(user);
                }
            }
        }
    }
    
    /**
     * Parse a pipe-delimited line into a User object
     * Format: id|username|email|firstName|lastName|phoneNumber|password|points
     */
    private User parseUserFromLine(String line) {
        try {
            String[] parts = FileHandler.parsePipeDelimitedLine(line);
            if (parts.length >= 8) {
                return new User(
                    parts[0], // userId
                    parts[1], // username
                    parts[2], // email
                    parts[3], // firstName
                    parts[4], // lastName
                    parts[5], // phoneNumber
                    parts[6], // password
                    Integer.parseInt(parts[7]) // points
                );
            }
        } catch (Exception e) {
            System.err.println("Error parsing user line: " + line);
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * CREATE - Add a new user
     * @param user - user to add
     * @return true if added successfully
     */
    public boolean create(User user) {
        // Check if email already exists
        if (findByEmail(user.getEmail()) != null) {
            System.out.println("Email already exists: " + user.getEmail());
            return false;
        }
        
        // Generate unique user ID
        String userId = generateUserId();
        user.setUserId(userId);
        
        // Add to in-memory list
        userList.add(user);
        
        // Save to file
        saveUsersToFile();
        return true;
    }
    
    /**
     * READ - Find user by email
     * @param email - email to search
     * @return User object or null
     */
    public User findByEmail(String email) {
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            if (user != null && user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * READ - Find user by userId
     * @param userId - userId to search
     * @return User object or null
     */
    public User findById(String userId) {
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            if (user != null && user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * READ - Get all users
     * @return array of all users
     */
    public Object[] findAll() {
        return userList.toArray();
    }
    
    /**
     * UPDATE - Update user information
     * @param user - updated user object
     * @return true if updated successfully
     */
    public boolean update(User user) {
        for (int i = 0; i < userList.size(); i++) {
            User existingUser = userList.get(i);
            if (existingUser != null && existingUser.getUserId().equals(user.getUserId())) {
                // Update existing user
                existingUser.setUsername(user.getUsername());
                existingUser.setFirstName(user.getFirstName());
                existingUser.setLastName(user.getLastName());
                existingUser.setPhoneNumber(user.getPhoneNumber());
                existingUser.setPoints(user.getPoints());
                
                // Save to file
                saveUsersToFile();
                return true;
            }
        }
        return false;
    }



    /**
     * DELETE - Remove user by email
     * @param email - email of user to delete
     * @return true if deleted successfully
     */
    public boolean deleteByEmail(String email) {
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            if (user != null && user.getEmail().equals(email)) {
                userList.remove(i);
                saveUsersToFile();
                return true;
            }
        }
        return false;
    }

    /**
     * DELETE - Remove user by userId
     * @param userId - userId of user to delete
     * @return true if deleted successfully
     */
    public boolean deleteById(String userId) {
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            if (user != null && user.getUserId().equals(userId)) {
                userList.remove(i);
                saveUsersToFile();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Save all users to file
     */
    private void saveUsersToFile() {
        List<String> lines = new java.util.ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            if (user != null) {
                lines.add(user.toPipeDelimitedString());
            }
        }
        FileHandler.writeFile(USERS_FILE_PATH, lines);
    }
    
    /**
     * Generate unique user ID based on timestamp and count
     */
    private String generateUserId() {
        return "USER_" + System.currentTimeMillis() + "_" + userList.size();
    }
    
    /**
     * Check if user exists
     * @param email - email to check
     * @return true if exists
     */
    public boolean userExists(String email) {
        return findByEmail(email) != null;
    }
}

