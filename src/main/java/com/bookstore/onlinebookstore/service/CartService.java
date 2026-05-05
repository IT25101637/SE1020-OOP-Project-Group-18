package com.bookstore.onlinebookstore.service;

import com.bookstore.onlinebookstore.dto.CartItemDTO;
import com.bookstore.onlinebookstore.model.CartItem;
import com.bookstore.onlinebookstore.model.User;
import com.bookstore.onlinebookstore.repository.CartRepository;
import com.bookstore.onlinebookstore.repository.UserRepository;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CartService - Business logic for cart CRUD operations
 */
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public CartService() {
        this.cartRepository = new CartRepository();
        this.userRepository = new UserRepository();
    }

    public Map<String, Object> getCartSummary(String userId) {
        if (!isValidUser(userId)) {
            return null;
        }

        Object[] items = cartRepository.findByUserId(userId);
        CartItemDTO[] itemDTOs = new CartItemDTO[items.length];

        int totalQuantity = 0;
        double subtotal = 0;
        for (int i = 0; i < items.length; i++) {
            CartItem item = (CartItem) items[i];
            itemDTOs[i] = convertToDTO(item);
            totalQuantity += item.getQuantity();
            subtotal += item.getLineTotal();
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("userId", userId);
        summary.put("items", itemDTOs);
        summary.put("itemCount", itemDTOs.length);
        summary.put("totalQuantity", totalQuantity);
        summary.put("subtotal", subtotal);
        return summary;
    }

    public CartItemDTO addToCart(String userId, String bookId, String title, String author,
                                 double price, String imageUrl, int quantity) {
        if (!isValidUser(userId)) {
            return null;
        }

        if (bookId == null || bookId.trim().isEmpty() || title == null || title.trim().isEmpty() ||
                author == null || author.trim().isEmpty() || imageUrl == null || imageUrl.trim().isEmpty() ||
                price <= 0 || quantity <= 0) {
            return null;
        }

        CartItem existing = cartRepository.findByUserIdAndBookId(userId, bookId);
        if (existing != null) {
            int updatedQty = existing.getQuantity() + quantity;
            CartItem updated = cartRepository.updateQuantity(userId, bookId, updatedQty);
            return updated != null ? convertToDTO(updated) : null;
        }

        CartItem created = cartRepository.add(new CartItem(userId, bookId, title, author, price, imageUrl, quantity));
        return created != null ? convertToDTO(created) : null;
    }

    public CartItemDTO updateQuantity(String userId, String bookId, int quantity) {
        if (!isValidUser(userId) || bookId == null || bookId.trim().isEmpty()) {
            return null;
        }

        if (quantity <= 0) {
            boolean deleted = cartRepository.deleteByUserIdAndBookId(userId, bookId);
            return deleted ? new CartItemDTO("", userId, bookId, "", "", 0, "", 0) : null;
        }

        CartItem updated = cartRepository.updateQuantity(userId, bookId, quantity);
        return updated != null ? convertToDTO(updated) : null;
    }

    public boolean removeFromCart(String userId, String bookId) {
        if (!isValidUser(userId) || bookId == null || bookId.trim().isEmpty()) {
            return false;
        }
        return cartRepository.deleteByUserIdAndBookId(userId, bookId);
    }

    public int clearCart(String userId) {
        if (!isValidUser(userId)) {
            return 0;
        }
        return cartRepository.clearByUserId(userId);
    }

    private CartItemDTO convertToDTO(CartItem item) {
        return new CartItemDTO(
                item.getCartId(),
                item.getUserId(),
                item.getBookId(),
                item.getTitle(),
                item.getAuthor(),
                item.getPrice(),
                item.getImageUrl(),
                item.getQuantity()
        );
    }

    private boolean isValidUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }
        User user = userRepository.findById(userId);
        return user != null;
    }
}
