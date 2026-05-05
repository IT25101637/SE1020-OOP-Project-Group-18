package com.bookstore.onlinebookstore.model;

import java.time.LocalDateTime;

public class Review {
    private String reviewId;
    private String userId;
    private String username;
    private String bookId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Review() {
    }

    public Review(String reviewId, String userId, String username, String bookId, int rating,
                  String comment, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.username = username;
        this.bookId = bookId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String toPipeDelimitedString() {
        String safeComment = comment == null ? "" : comment.replace("|", "~");
        String safeUsername = username == null ? "" : username.replace("|", "~");
        return reviewId + "|" + userId + "|" + safeUsername + "|" + bookId + "|" + rating + "|" +
                safeComment + "|" + createdAt + "|" + updatedAt;
    }

    public static Review fromPipeDelimitedString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 8) {
            return null;
        }
        return new Review(
                parts[0],
                parts[1],
                parts[2].replace("~", "|"),
                parts[3],
                Integer.parseInt(parts[4]),
                parts[5].replace("~", "|"),
                LocalDateTime.parse(parts[6]),
                LocalDateTime.parse(parts[7])
        );
    }
}
