package com.bookstore.onlinebookstore.controller;

import com.bookstore.onlinebookstore.dto.AdminDTO;
import com.bookstore.onlinebookstore.model.Admin;
import com.bookstore.onlinebookstore.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AdminController - REST API endpoints for admin management
 * Provides CRUD operations through HTTP endpoints
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * POST /api/admin/register - Create a new admin
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerAdmin(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String username = request.get("username");
        String email = request.get("email");
        String phoneNumber = request.get("phoneNumber");
        String password = request.get("password");

        // Validate required fields
        if (username == null || username.isEmpty() ||
            email == null || email.isEmpty() ||
            phoneNumber == null || phoneNumber.isEmpty() ||
            password == null || password.isEmpty()) {
            response.put("status", "error");
            response.put("message", "All fields are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        AdminDTO adminDTO = adminService.createAdmin(username, email, phoneNumber, password);
        if (adminDTO != null) {
            response.put("status", "success");
            response.put("message", "Admin registered successfully");
            response.put("admin", adminDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        response.put("status", "error");
        response.put("message", "Email already exists or registration failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * POST /api/admin/login - Login admin
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginAdmin(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String email = request.get("email");
        String password = request.get("password");

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Email and password are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        AdminDTO adminDTO = adminService.loginAdmin(email, password);
        if (adminDTO != null) {
            response.put("status", "success");
            response.put("message", "Login successful");
            response.put("admin", adminDTO);
            return ResponseEntity.ok(response);
        }

        response.put("status", "error");
        response.put("message", "Invalid email or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * GET /api/admin/profile/{email} - Get admin profile
     */
    @GetMapping("/profile/{email}")
    public ResponseEntity<Map<String, Object>> getAdminProfile(@PathVariable String email) {
        Map<String, Object> response = new HashMap<>();

        AdminDTO adminDTO = adminService.getAdminProfile(email);
        if (adminDTO != null) {
            response.put("status", "success");
            response.put("admin", adminDTO);
            return ResponseEntity.ok(response);
        }

        response.put("status", "error");
        response.put("message", "Admin not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * GET /api/admin/all - Get all admins
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllAdmins() {
        Map<String, Object> response = new HashMap<>();

        Object[] allAdmins = adminService.getAllAdmins();
        response.put("status", "success");
        response.put("admins", allAdmins);
        response.put("count", allAdmins.length);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/admin/profile/{email} - Update admin profile
     */
    @PutMapping("/profile/{email}")
    public ResponseEntity<Map<String, Object>> updateAdminProfile(
            @PathVariable String email,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String username = request.get("username");
        String phoneNumber = request.get("phoneNumber");
        String password = request.get("password");

        AdminDTO adminDTO = adminService.updateAdminProfile(email, username, phoneNumber, password);
        if (adminDTO != null) {
            response.put("status", "success");
            response.put("message", "Admin profile updated successfully");
            response.put("admin", adminDTO);
            return ResponseEntity.ok(response);
        }

        response.put("status", "error");
        response.put("message", "Failed to update admin profile");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * DELETE /api/admin/{email} - Delete admin by email
     */
    @DeleteMapping("/{email}")
    public ResponseEntity<Map<String, Object>> deleteAdmin(@PathVariable String email) {
        Map<String, Object> response = new HashMap<>();

        if (adminService.deleteAdminAccount(email)) {
            response.put("status", "success");
            response.put("message", "Admin deleted successfully");
            return ResponseEntity.ok(response);
        }

        response.put("status", "error");
        response.put("message", "Failed to delete admin");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
