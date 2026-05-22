package com.bookstore.onlinebookstore.dto;

/**
 * BookDTO - Data Transfer Object for Book
 * Used for API responses (excludes sensitive fields if any)
 * Demonstrates information hiding principle
 */
public class BookDTO {
	private String bookId;
	private String title;
	private String author;
	private double price;
	private String category;
	private String description;
	private String imageUrl;
	private boolean isFeatured;

	// Constructor
	public BookDTO(String bookId, String title, String author, double price, String category, String description,
			String imageUrl, boolean isFeatured) {
		this.bookId = bookId;
		this.title = title;
		this.author = author;
		this.price = price;
		this.category = category;
		this.description = description;
		this.imageUrl = imageUrl;
		this.isFeatured = isFeatured;
	}

	// Getters
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

	public String getCategory() {
		return category;
	}

	public String getDescription() {
		return description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public boolean isFeatured() {
		return isFeatured;
	}

	// Explicit getter named getIsFeatured so Jackson serializes the field as
	// "isFeatured" in JSON (not "featured", which is what isFeatured() produces)
	public boolean getIsFeatured() {
		return isFeatured;
	}
}
