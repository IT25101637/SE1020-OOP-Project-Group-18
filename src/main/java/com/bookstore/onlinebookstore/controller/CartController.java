package com.bookstore.onlinebookstore.controller;

import com.bookstore.onlinebookstore.dto.CartItemDTO;
import com.bookstore.onlinebookstore.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * CartController - REST endpoints for cart CRUD
 * Base path: /api/cart
 */
@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {
    private final CartService cartService;

    public CartController() {
        this.cartService = new CartService();
    }

    @GetMapping("/{userId}")
    public Map<String, Object> getCart(@PathVariable("userId") String userId) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> summary = cartService.getCartSummary(userId);

        if (summary == null) {
            response.put("status", "error");
            response.put("message", "Invalid user");
            return response;
        }

        response.put("status", "success");
        response.put("message", "Cart retrieved successfully");
        response.putAll(summary);
        return response;
    }

    @PostMapping("/{userId}/items")
    public Map<String, Object> addItem(@PathVariable("userId") String userId, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String bookId = (String) request.get("bookId");
            String title = (String) request.get("title");
            String author = (String) request.get("author");
            String imageUrl = (String) request.get("imageUrl");
            double price = Double.parseDouble(request.get("price").toString());
            int quantity = Integer.parseInt(request.getOrDefault("quantity", 1).toString());

            CartItemDTO item = cartService.addToCart(userId, bookId, title, author, price, imageUrl, quantity);
            if (item == null) {
                response.put("status", "error");
                response.put("message", "Failed to add item to cart");
                return response;
            }

            response.put("status", "success");
            response.put("message", "Item added to cart");
            response.put("item", item);
            response.put("cart", cartService.getCartSummary(userId));
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Invalid request payload");
            return response;
        }
    }

    @PutMapping("/{userId}/items/{bookId}")
    public Map<String, Object> updateQuantity(@PathVariable("userId") String userId,
                                              @PathVariable("bookId") String bookId,
                                              @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            int quantity = Integer.parseInt(request.get("quantity").toString());
            CartItemDTO item = cartService.updateQuantity(userId, bookId, quantity);
            if (item == null) {
                response.put("status", "error");
                response.put("message", "Failed to update cart item");
                return response;
            }

            response.put("status", "success");
            response.put("message", quantity <= 0 ? "Item removed from cart" : "Cart item updated");
            response.put("item", item);
            response.put("cart", cartService.getCartSummary(userId));
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Invalid quantity value");
            return response;
        }
    }

    @DeleteMapping("/{userId}/items/{bookId}")
    public Map<String, Object> removeItem(@PathVariable("userId") String userId, @PathVariable("bookId") String bookId) {
        Map<String, Object> response = new HashMap<>();
        boolean removed = cartService.removeFromCart(userId, bookId);

        if (!removed) {
            response.put("status", "error");
            response.put("message", "Item not found in cart");
            return response;
        }

        response.put("status", "success");
        response.put("message", "Item removed from cart");
        response.put("cart", cartService.getCartSummary(userId));
        return response;
    }

    @DeleteMapping("/{userId}/clear")
    public Map<String, Object> clearCart(@PathVariable("userId") String userId) {
        Map<String, Object> response = new HashMap<>();
        int removed = cartService.clearCart(userId);

        response.put("status", "success");
        response.put("message", "Cart cleared");
        response.put("removedItems", removed);
        response.put("cart", cartService.getCartSummary(userId));
        return response;
    }
}
