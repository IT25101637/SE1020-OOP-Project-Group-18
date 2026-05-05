package com.bookstore.onlinebookstore.service;

import com.bookstore.onlinebookstore.model.Book;
import com.bookstore.onlinebookstore.model.Review;
import com.bookstore.onlinebookstore.model.User;
import com.bookstore.onlinebookstore.repository.BookRepository;
import com.bookstore.onlinebookstore.repository.ReviewRepository;
import com.bookstore.onlinebookstore.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final PaymentService paymentService;

    public ReviewService(ReviewRepository reviewRepository, PaymentService paymentService) {
        this.reviewRepository = reviewRepository;
        this.paymentService = paymentService;
    }

    public Map<String, Object> createReview(String userId, String bookId, int rating, String comment) {
        UserRepository userRepository = new UserRepository();
        BookRepository bookRepository = new BookRepository();

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Book book = bookRepository.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found");
        }

        if (!paymentService.hasUserPurchasedBook(userId, bookId)) {
            throw new IllegalArgumentException("You can only review books you have purchased");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Review comment cannot be empty");
        }

        Review existing = reviewRepository.findByUserIdAndBookId(userId, bookId);
        if (existing != null) {
            throw new IllegalArgumentException("You already reviewed this book. Please edit your review.");
        }

        LocalDateTime now = LocalDateTime.now();
        Review review = new Review(
                UUID.randomUUID().toString(),
                userId,
                user.getUsername(),
                bookId,
                rating,
                comment.trim(),
                now,
                now
        );

        reviewRepository.add(review);
        return toReviewResponse(review, book);
    }

    public List<Map<String, Object>> getReviewsByBookId(String bookId) {
        BookRepository bookRepository = new BookRepository();
        Book book = bookRepository.findById(bookId);
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        reviews.sort(Comparator.comparing(Review::getUpdatedAt).reversed());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Review review : reviews) {
            result.add(toReviewResponse(review, book));
        }
        return result;
    }

    public List<Map<String, Object>> getReviewsByUserId(String userId) {
        BookRepository bookRepository = new BookRepository();
        List<Review> reviews = reviewRepository.findByUserId(userId);
        reviews.sort(Comparator.comparing(Review::getUpdatedAt).reversed());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Review review : reviews) {
            Book book = bookRepository.findById(review.getBookId());
            result.add(toReviewResponse(review, book));
        }
        return result;
    }

    public List<Map<String, Object>> getTopRecentReviews(int limit) {
        BookRepository bookRepository = new BookRepository();
        List<Review> allReviews = reviewRepository.findAll();
        allReviews.sort(Comparator.comparing(Review::getUpdatedAt).reversed());

        List<Map<String, Object>> result = new ArrayList<>();
        int count = 0;
        for (Review review : allReviews) {
            if (count >= limit) break;
            Book book = bookRepository.findById(review.getBookId());
            result.add(toReviewResponse(review, book));
            count++;
        }
        return result;
    }

    public Map<String, Object> updateReview(String userId, String reviewId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Review comment cannot be empty");
        }

        Review existing = reviewRepository.findById(reviewId);
        if (existing == null) {
            throw new IllegalArgumentException("Review not found");
        }

        if (!existing.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only edit your own review");
        }

        existing.setRating(rating);
        existing.setComment(comment.trim());
        existing.setUpdatedAt(LocalDateTime.now());

        Review updated = reviewRepository.update(existing);
        Book book = new BookRepository().findById(updated.getBookId());
        return toReviewResponse(updated, book);
    }

    public boolean deleteReview(String userId, String reviewId) {
        Review existing = reviewRepository.findById(reviewId);
        if (existing == null) {
            throw new IllegalArgumentException("Review not found");
        }

        if (!existing.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own review");
        }

        return reviewRepository.deleteById(reviewId);
    }

    private Map<String, Object> toReviewResponse(Review review, Book book) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("reviewId", review.getReviewId());
        response.put("userId", review.getUserId());
        response.put("username", review.getUsername());
        response.put("bookId", review.getBookId());
        response.put("bookTitle", book != null ? book.getTitle() : "Unknown Book");
        response.put("bookAuthor", book != null ? book.getAuthor() : "Unknown Author");
        response.put("bookImageUrl", book != null ? book.getImageUrl() : "");
        response.put("rating", review.getRating());
        response.put("comment", review.getComment());
        response.put("createdAt", review.getCreatedAt().toString());
        response.put("updatedAt", review.getUpdatedAt().toString());
        return response;
    }
}
