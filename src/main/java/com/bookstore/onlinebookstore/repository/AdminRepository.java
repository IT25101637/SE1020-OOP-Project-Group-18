package com.bookstore.onlinebookstore.repository;

import com.bookstore.onlinebookstore.model.Admin;
import com.bookstore.onlinebookstore.util.FileHandler;
import com.bookstore.onlinebookstore.util.LinkedListUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AdminRepository - Data Access Layer for Admin
 * Handles all persistence operations using file-based storage
 * Demonstrates abstraction through FileHandler utility
 */
@Repository
public class AdminRepository {

    private static final String ADMINS_FILE_PATH = "src/main/resources/data/admins.txt";
    private LinkedListUtil<Admin> admins;
    private int adminIdCounter = 0;

    // Constructor - loads admins from file on initialization
    public AdminRepository() {
        this.admins = new LinkedListUtil<>();
        loadAdminsFromFile();
    }

    /**
     * CREATE - Add a new admin to the system
     */
    public boolean create(Admin admin) {
        // Check if email already exists
        if (findByEmail(admin.getEmail()) != null) {
            return false;
        }

        // Generate unique adminId
        String adminId = generateAdminId();
        admin.setAdminId(adminId);

        // Add to in-memory list
        admins.add(admin);

        // Save to file
        return saveAdminsToFile();
    }

    /**
     * READ - Get admin by email
     */
    public Admin findByEmail(String email) {
        for (int i = 0; i < admins.size(); i++) {
            Admin admin = admins.get(i);
            if (admin != null && admin.getEmail().equalsIgnoreCase(email)) {
                return admin;
            }
        }
        return null;
    }

    /**
     * READ - Get admin by ID
     */
    public Admin findById(String adminId) {
        for (int i = 0; i < admins.size(); i++) {
            Admin admin = admins.get(i);
            if (admin != null && admin.getAdminId().equals(adminId)) {
                return admin;
            }
        }
        return null;
    }

    /**
     * READ - Get all admins
     */
    public Object[] findAll() {
        return admins.toArray();
    }

    /**
     * UPDATE - Update admin profile
     */
    public boolean update(Admin admin) {
        for (int i = 0; i < admins.size(); i++) {
            Admin existingAdmin = admins.get(i);
            if (existingAdmin != null && existingAdmin.getAdminId().equals(admin.getAdminId())) {
                // Update all fields
                existingAdmin.setUsername(admin.getUsername());
                existingAdmin.setEmail(admin.getEmail());
                existingAdmin.setPhoneNumber(admin.getPhoneNumber());
                existingAdmin.setPassword(admin.getPassword());
                return saveAdminsToFile();
            }
        }
        return false;
    }

    /**
     * DELETE - Remove admin by email
     */
    public boolean deleteByEmail(String email) {
        for (int i = 0; i < admins.size(); i++) {
            Admin admin = admins.get(i);
            if (admin != null && admin.getEmail().equalsIgnoreCase(email)) {
                admins.remove(i);
                return saveAdminsToFile();
            }
        }
        return false;
    }

    /**
     * DELETE - Remove admin by ID
     */
    public boolean deleteById(String adminId) {
        for (int i = 0; i < admins.size(); i++) {
            Admin admin = admins.get(i);
            if (admin != null && admin.getAdminId().equals(adminId)) {
                admins.remove(i);
                return saveAdminsToFile();
            }
        }
        return false;
    }

    // Private helper methods

    /**
     * Load all admins from file into memory
     */
    private void loadAdminsFromFile() {
        List<String> lines = FileHandler.readFile(ADMINS_FILE_PATH);
        for (String line : lines) {
            if (line != null && !line.trim().isEmpty()) {
                Admin admin = parseAdminFromLine(line);
                if (admin != null) {
                    admins.add(admin);
                }
            }
        }
    }

    /**
     * Save all admins from memory to file
     */
    private boolean saveAdminsToFile() {
        try {
            java.util.List<String> lines = new java.util.ArrayList<>();
            for (int i = 0; i < admins.size(); i++) {
                Admin admin = admins.get(i);
                if (admin != null) {
                    lines.add(admin.toPipeDelimitedString());
                }
            }
            FileHandler.writeFile(ADMINS_FILE_PATH, lines);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving admins to file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Parse a pipe-delimited line into an Admin object
     */
    private Admin parseAdminFromLine(String line) {
        try {
            String[] parts = FileHandler.parsePipeDelimitedLine(line);
            if (parts.length >= 5) {
                return new Admin(parts[0], parts[1], parts[2], parts[3], parts[4]);
            }
        } catch (Exception e) {
            System.err.println("Error parsing admin line: " + e.getMessage());
        }
        return null;
    }

    /**
     * Generate unique adminId using timestamp
     */
    private String generateAdminId() {
        return "ADMIN_" + System.currentTimeMillis() + "_" + (++adminIdCounter);
    }
}
