package com.bookstore.onlinebookstore.controller;

import com.bookstore.onlinebookstore.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/book/{bookId}")
    public Map<String, Object> getBookReviews(@PathVariable("bookId") String bookId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> reviews = reviewService.getReviewsByBookId(bookId);
            response.put("status", "success");
            response.put("reviews", reviews);
            response.put("count", reviews.size());
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return response;
        }
    }

    @GetMapping("/user/{userId}")
    public Map<String, Object> getUserReviews(@PathVariable("userId") String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> reviews = reviewService.getReviewsByUserId(userId);
            response.put("status", "success");
            response.put("reviews", reviews);
            response.put("count", reviews.size());
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return response;
        }
    }

    @GetMapping("/top")
    public Map<String, Object> getTopRecentReviews() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> reviews = reviewService.getTopRecentReviews(3);
            response.put("status", "success");
            response.put("reviews", reviews);
            response.put("count", reviews.size());
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return response;
        }
    }

    @PostMapping("/user/{userId}/book/{bookId}")
    public Map<String, Object> createReview(@PathVariable("userId") String userId,
                                            @PathVariable("bookId") String bookId,
                                            @RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            int rating = Integer.parseInt(payload.get("rating").toString());
            String comment = payload.get("comment") != null ? payload.get("comment").toString() : "";
            Map<String, Object> review = reviewService.createReview(userId, bookId, rating, comment);
            response.put("status", "success");
            response.put("message", "Review added successfully");
            response.put("review", review);
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return response;
        }
    }

    @PutMapping("/user/{userId}/{reviewId}")
    public Map<String, Object> updateReview(@PathVariable("userId") String userId,
                                            @PathVariable("reviewId") String reviewId,
                                            @RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            int rating = Integer.parseInt(payload.get("rating").toString());
            String comment = payload.get("comment") != null ? payload.get("comment").toString() : "";
            Map<String, Object> review = reviewService.updateReview(userId, reviewId, rating, comment);
            response.put("status", "success");
            response.put("message", "Review updated successfully");
            response.put("review", review);
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return response;
        }
    }

    @DeleteMapping("/user/{userId}/{reviewId}")
    public Map<String, Object> deleteReview(@PathVariable("userId") String userId,
                                            @PathVariable("reviewId") String reviewId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean removed = reviewService.deleteReview(userId, reviewId);
            response.put("status", removed ? "success" : "error");
            response.put("message", removed ? "Review deleted successfully" : "Review not found");
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return response;
        }
    }
}
