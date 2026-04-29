package com.bookstore.onlinebookstore.service;

import com.bookstore.onlinebookstore.dto.AdminDTO;
import com.bookstore.onlinebookstore.model.Admin;
import com.bookstore.onlinebookstore.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AdminService - Business Logic Layer for Admin
 * Encapsulates all business rules and validation logic
 * Demonstrates Separation of Concerns principle
 */
@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    /**
     * Register a new admin
     */
    public AdminDTO createAdmin(String username, String email, String phoneNumber, String password) {
        // Validate inputs
        if (username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            phoneNumber == null || phoneNumber.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            return null;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            return null;
        }

        // Check if email already exists
        if (adminRepository.findByEmail(email) != null) {
            return null;
        }

        // Create new admin
        Admin admin = new Admin(username, email, phoneNumber, password);
        if (adminRepository.create(admin)) {
            return convertAdminToDTO(admin);
        }
        return null;
    }

    /**
     * Login admin with email and password
     */
    public AdminDTO loginAdmin(String email, String password) {
        // Validate inputs
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }

        Admin admin = adminRepository.findByEmail(email);
        if (admin != null && admin.getPassword().equals(password)) {
            return convertAdminToDTO(admin);
        }
        return null;
    }

    /**
     * Get admin profile
     */
    public AdminDTO getAdminProfile(String email) {
        Admin admin = adminRepository.findByEmail(email);
        if (admin != null) {
            return convertAdminToDTO(admin);
        }
        return null;
    }

    /**
     * Get all admins
     */
    public Object[] getAllAdmins() {
        return adminRepository.findAll();
    }

    /**
     * Update admin profile
     */
    public AdminDTO updateAdminProfile(String email, String username, String phoneNumber, String password) {
        Admin admin = adminRepository.findByEmail(email);
        if (admin == null) {
            return null;
        }

        // Validate inputs
        if (username != null && !username.trim().isEmpty()) {
            admin.setUsername(username);
        }
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            admin.setPhoneNumber(phoneNumber);
        }
        if (password != null && !password.trim().isEmpty()) {
            admin.setPassword(password);
        }

        if (adminRepository.update(admin)) {
            return convertAdminToDTO(admin);
        }
        return null;
    }

    /**
     * Delete admin account
     */
    public boolean deleteAdminAccount(String email) {
        return adminRepository.deleteByEmail(email);
    }

    /**
     * Delete admin by ID
     */
    public boolean deleteAdminById(String adminId) {
        return adminRepository.deleteById(adminId);
    }

    // Private helper methods

    /**
     * Convert Admin to AdminDTO (excludes password)
     */
    private AdminDTO convertAdminToDTO(Admin admin) {
        return new AdminDTO(
            admin.getAdminId(),
            admin.getUsername(),
            admin.getEmail(),
            admin.getPhoneNumber()
        );
    }

    /**
     * Basic email validation
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
