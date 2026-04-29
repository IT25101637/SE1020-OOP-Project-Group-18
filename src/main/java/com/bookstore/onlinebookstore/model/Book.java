package com.bookstore.onlinebookstore.model;

/**
 * Book - Domain model representing a book in the online bookstore
 * Demonstrates OOP encapsulation with private fields and public accessors
 */
public class Book {
	private String bookId;
	private String title;
	private String author;
	private double price;
	private String category; // Business, Technology, Romantic, Adventure, Fictional
	private String description;
	private String imageUrl;
	private boolean isFeatured;

	// Default constructor
	public Book() {
	}

	// Full constructor
	public Book(String bookId, String title, String author, double price, String category, String description,
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

	// Constructor for creation (without bookId)
	public Book(String title, String author, double price, String category, String description, String imageUrl,
			boolean isFeatured) {
		this.title = title;
		this.author = author;
		this.price = price;
		this.category = category;
		this.description = description;
		this.imageUrl = imageUrl;
		this.isFeatured = isFeatured;
	}

	// Serialize to pipe-delimited format
	public String toPipeDelimitedString() {
		return bookId + "|" + title + "|" + author + "|" + price + "|" + category + "|" + description + "|"
				+ imageUrl + "|" + isFeatured;
	}

	// Getters and Setters
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean isFeatured() {
		return isFeatured;
	}

	public void setFeatured(boolean featured) {
		isFeatured = featured;
	}
}
