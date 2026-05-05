package com.bookstore.onlinebookstore.repository;

import com.bookstore.onlinebookstore.model.Review;
import com.bookstore.onlinebookstore.util.FileHandler;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewRepository {
    private static final String FILE_PATH = "src/main/resources/data/reviews.txt";

    public Review add(Review review) {
        List<String> lines = FileHandler.readFile(FILE_PATH);
        lines.add(review.toPipeDelimitedString());
        FileHandler.writeFile(FILE_PATH, lines);
        return review;
    }

    public List<Review> findAll() {
        List<Review> reviews = new ArrayList<>();
        List<String> lines = FileHandler.readFile(FILE_PATH);
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            Review review = Review.fromPipeDelimitedString(line.trim());
            if (review != null) {
                reviews.add(review);
            }
        }
        return reviews;
    }

    public Review findById(String reviewId) {
        for (Review review : findAll()) {
            if (review.getReviewId().equals(reviewId)) {
                return review;
            }
        }
        return null;
    }

    public List<Review> findByBookId(String bookId) {
        List<Review> reviews = new ArrayList<>();
        for (Review review : findAll()) {
            if (review.getBookId().equals(bookId)) {
                reviews.add(review);
            }
        }
        return reviews;
    }

    public List<Review> findByUserId(String userId) {
        List<Review> reviews = new ArrayList<>();
        for (Review review : findAll()) {
            if (review.getUserId().equals(userId)) {
                reviews.add(review);
            }
        }
        return reviews;
    }

    public Review findByUserIdAndBookId(String userId, String bookId) {
        for (Review review : findAll()) {
            if (review.getUserId().equals(userId) && review.getBookId().equals(bookId)) {
                return review;
            }
        }
        return null;
    }

    public Review update(Review updatedReview) {
        List<Review> all = findAll();
        List<String> updatedLines = new ArrayList<>();
        Review result = null;

        for (Review review : all) {
            if (review.getReviewId().equals(updatedReview.getReviewId())) {
                updatedLines.add(updatedReview.toPipeDelimitedString());
                result = updatedReview;
            } else {
                updatedLines.add(review.toPipeDelimitedString());
            }
        }

        FileHandler.writeFile(FILE_PATH, updatedLines);
        return result;
    }

    public boolean deleteById(String reviewId) {
        List<Review> all = findAll();
        List<String> updatedLines = new ArrayList<>();
        boolean removed = false;

        for (Review review : all) {
            if (review.getReviewId().equals(reviewId)) {
                removed = true;
                continue;
            }
            updatedLines.add(review.toPipeDelimitedString());
        }

        FileHandler.writeFile(FILE_PATH, updatedLines);
        return removed;
    }
}
