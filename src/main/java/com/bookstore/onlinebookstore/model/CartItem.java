package com.bookstore.onlinebookstore.model;

/**
 * CartItem - Domain model for a single user cart line item
 */
public class CartItem {
    private String cartId;
    private String userId;
    private String bookId;
    private String title;
    private String author;
    private double price;
    private String imageUrl;
    private int quantity;

    public CartItem() {
    }

    public CartItem(String cartId, String userId, String bookId, String title, String author,
                    double price, String imageUrl, int quantity) {
        this.cartId = cartId;
        this.userId = userId;
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public CartItem(String userId, String bookId, String title, String author,
                    double price, String imageUrl, int quantity) {
        this.userId = userId;
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public String toPipeDelimitedString() {
        return safe(cartId) + "|" + safe(userId) + "|" + safe(bookId) + "|" + safe(title) + "|"
                + safe(author) + "|" + price + "|" + safe(imageUrl) + "|" + quantity;
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("|", "/").trim();
    }

    public double getLineTotal() {
        return price * quantity;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
