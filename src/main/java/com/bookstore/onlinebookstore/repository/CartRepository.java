package com.bookstore.onlinebookstore.repository;

import com.bookstore.onlinebookstore.model.CartItem;
import com.bookstore.onlinebookstore.util.FileHandler;
import com.bookstore.onlinebookstore.util.LinkedListUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * CartRepository - File-backed persistence for user cart items
 */
public class CartRepository {
    private static final String CARTS_FILE = "src/main/resources/data/carts.txt";
    private final LinkedListUtil<CartItem> cartItems;
    private static int cartIdCounter = 1;

    public CartRepository() {
        this.cartItems = new LinkedListUtil<>();
        loadFromFile();
    }

    public CartItem add(CartItem item) {
        if (item.getCartId() == null || item.getCartId().trim().isEmpty()) {
            item.setCartId(generateCartId());
        }
        cartItems.add(item);
        saveToFile();
        return item;
    }

    public CartItem findByUserIdAndBookId(String userId, String bookId) {
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            if (item != null && userId.equals(item.getUserId()) && bookId.equals(item.getBookId())) {
                return item;
            }
        }
        return null;
    }

    public Object[] findByUserId(String userId) {
        LinkedListUtil<CartItem> userItems = new LinkedListUtil<>();
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            if (item != null && userId.equals(item.getUserId())) {
                userItems.add(item);
            }
        }
        return userItems.toArray();
    }

    public CartItem updateQuantity(String userId, String bookId, int quantity) {
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            if (item != null && userId.equals(item.getUserId()) && bookId.equals(item.getBookId())) {
                item.setQuantity(quantity);
                saveToFile();
                return item;
            }
        }
        return null;
    }

    public boolean deleteByUserIdAndBookId(String userId, String bookId) {
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            if (item != null && userId.equals(item.getUserId()) && bookId.equals(item.getBookId())) {
                cartItems.remove(i);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    public int clearByUserId(String userId) {
        int removedCount = 0;
        for (int i = cartItems.size() - 1; i >= 0; i--) {
            CartItem item = cartItems.get(i);
            if (item != null && userId.equals(item.getUserId())) {
                cartItems.remove(i);
                removedCount++;
            }
        }
        if (removedCount > 0) {
            saveToFile();
        }
        return removedCount;
    }

    private String generateCartId() {
        return "CART_" + System.currentTimeMillis() + "_" + cartIdCounter++;
    }

    private void loadFromFile() {
        List<String> lines = FileHandler.readFile(CARTS_FILE);
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            CartItem item = parseLine(line);
            if (item != null) {
                cartItems.add(item);
            }
        }
    }

    private void saveToFile() {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            if (item != null) {
                lines.add(item.toPipeDelimitedString());
            }
        }
        FileHandler.writeFile(CARTS_FILE, lines);
    }

    private CartItem parseLine(String line) {
        try {
            String[] parts = FileHandler.parsePipeDelimitedLine(line);
            if (parts.length >= 8) {
                return new CartItem(
                        parts[0],
                        parts[1],
                        parts[2],
                        parts[3],
                        parts[4],
                        Double.parseDouble(parts[5]),
                        parts[6],
                        Integer.parseInt(parts[7])
                );
            }
        } catch (Exception e) {
            System.err.println("Error parsing cart line: " + line);
            e.printStackTrace();
        }
        return null;
    }
}
