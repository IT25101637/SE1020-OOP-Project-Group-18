package com.bookstore.onlinebookstore.controller;

import com.bookstore.onlinebookstore.dto.UserDTO;
import com.bookstore.onlinebookstore.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * UserController - REST API Controller
 * Handles HTTP requests for user operations
 * Provides endpoints for authentication and profile management
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    private UserService userService;
    
    /**
     * Constructor
     */
    public UserController() {
        this.userService = new UserService();
    }
    
    /**
     * POST /api/users/register - Register a new user
     * Request body: {username, email, firstName, lastName, phoneNumber, password}
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String email = request.get("email");
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");
            String phoneNumber = request.get("phoneNumber");
            String password = request.get("password");
            
            // Validation
            if (username == null || email == null || firstName == null || 
                lastName == null || phoneNumber == null || password == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "All fields are required");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Register user
            UserDTO userDTO = userService.registerUser(username, email, firstName, 
                                                       lastName, phoneNumber, password);
            
            if (userDTO != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Registration successful");
                response.put("user", userDTO);
                response.put("status", "success");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Registration failed. Email may already exist.");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error during registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * POST /api/users/login - Login user
     * Request body: {email, password}
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            
            if (email == null || password == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Email and password are required");
                return ResponseEntity.badRequest().body(error);
            }
            
            UserDTO userDTO = userService.loginUser(email, password);
            
            if (userDTO != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("user", userDTO);
                response.put("status", "success");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error during login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * GET /api/users/profile/:email - Get user profile
     */
    @GetMapping("/profile/{email}")
    public ResponseEntity<?> getUserProfile(@PathVariable String email) {
        try {
            UserDTO userDTO = userService.getUserProfile(email);
            
            if (userDTO != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Profile retrieved successfully");
                response.put("user", userDTO);
                response.put("status", "success");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error retrieving profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * PUT /api/users/profile/:email - Update user profile
     * Request body: {username, firstName, lastName, phoneNumber}
     */
    @PutMapping("/profile/{email}")
    public ResponseEntity<?> updateUserProfile(@PathVariable String email,
                                               @RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");
            String phoneNumber = request.get("phoneNumber");
            
            if (username == null || firstName == null || lastName == null || phoneNumber == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "All fields are required");
                return ResponseEntity.badRequest().body(error);
            }
            
            UserDTO userDTO = userService.updateUserProfile(email, username, firstName, 
                                                            lastName, phoneNumber);
            
            if (userDTO != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Profile updated successfully");
                response.put("user", userDTO);
                response.put("status", "success");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error updating profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * PUT /api/users/update - Update user profile
     * Request body: {email, firstName, lastName, phoneNumber}
     */
    @PutMapping("/update/{email}")
    public ResponseEntity<?> updateUser(@PathVariable String email, @RequestBody Map<String, String> request) {
        try {
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");
            String phoneNumber = request.get("phoneNumber");

            UserDTO updatedUser = userService.updateUser(email, firstName, lastName, phoneNumber);

            if (updatedUser != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Profile updated successfully");
                response.put("user", updatedUser);
                response.put("status", "success");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "User not found or update failed");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error during profile update: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * DELETE /api/users/:email - Delete user account
     */
    @DeleteMapping("/{email}")
    public ResponseEntity<?> deleteUserAccount(@PathVariable String email) {
        try {
            boolean deleted = userService.deleteUserAccount(email);
            
            if (deleted) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Account deleted successfully");
                response.put("status", "success");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error deleting account: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * DELETE /api/users/delete - Delete user profile
     * Request param: email
     */
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        try {
            boolean deleted = userService.deleteUserAccount(email);

            if (deleted) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "User deleted successfully");
                response.put("status", "success");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "User not found or delete failed");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error during user deletion: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

