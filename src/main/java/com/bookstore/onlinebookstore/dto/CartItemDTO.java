package com.bookstore.onlinebookstore.dto;

/**
 * CartItemDTO - API response DTO for cart item
 */
public class CartItemDTO {
    private String cartId;
    private String userId;
    private String bookId;
    private String title;
    private String author;
    private double price;
    private String imageUrl;
    private int quantity;
    private double lineTotal;

    public CartItemDTO(String cartId, String userId, String bookId, String title, String author,
                       double price, String imageUrl, int quantity) {
        this.cartId = cartId;
        this.userId = userId;
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.lineTotal = price * quantity;
    }

    public String getCartId() {
        return cartId;
    }

    public String getUserId() {
        return userId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLineTotal() {
        return lineTotal;
    }
}
