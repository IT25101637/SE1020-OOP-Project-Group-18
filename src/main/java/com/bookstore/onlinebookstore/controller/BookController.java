package com.bookstore.onlinebookstore.controller;

import org.springframework.web.bind.annotation.*;
import com.bookstore.onlinebookstore.dto.BookDTO;
import com.bookstore.onlinebookstore.service.BookService;

import java.util.HashMap;
import java.util.Map;

/**
 * BookController - REST API endpoints for book operations
 * Handles HTTP requests for book CRUD operations
 * Base path: /api/books
 */
@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {
	private BookService bookService;

	public BookController() {
		this.bookService = new BookService();
	}

	/**
	 * GET /api/books - Get all books
	 * @return JSON with all books
	 */
	@GetMapping("/{bookId}")
	public Map<String, Object> getBookById(@PathVariable("bookId") String bookId) {
		Map<String, Object> response = new HashMap<>();
		try {
			BookDTO book = bookService.getBookById(bookId);
			if (book == null) {
				response.put("status", "error");
				response.put("message", "Book not found");
				return response;
			}

			response.put("status", "success");
			response.put("book", book);
			return response;
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", "Error retrieving book: " + e.getMessage());
			return response;
		}
	}

	@GetMapping
	public Map<String, Object> getAllBooks() {
		Map<String, Object> response = new HashMap<>();
		try {
			Object[] books = bookService.getAllBooks();
			response.put("status", "success");
			response.put("message", "Books retrieved successfully");
			response.put("count", books.length);
			response.put("books", books);
			return response;
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", "Error retrieving books: " + e.getMessage());
			return response;
		}
	}

	/**
	 * GET /api/books/featured - Get featured books only
	 * @return JSON with featured books
	 */
	@GetMapping("/featured")
	public Map<String, Object> getFeaturedBooks() {
		Map<String, Object> response = new HashMap<>();
		try {
			Object[] books = bookService.getFeaturedBooks();
			response.put("status", "success");
			response.put("message", "Featured books retrieved successfully");
			response.put("count", books.length);
			response.put("books", books);
			return response;
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", "Error retrieving featured books: " + e.getMessage());
			return response;
		}
	}

	/**
	 * GET /api/books/category/{category} - Get books by category
	 * @param category - Book category (Business, Technology, Romantic, Adventure, Fictional, All Genre)
	 * @return JSON with books in category
	 */
	@GetMapping("/category/{category}")
	public Map<String, Object> getBooksByCategory(@PathVariable String category) {
		Map<String, Object> response = new HashMap<>();
		try {
			Object[] books = bookService.getBooksByCategory(category);
			response.put("status", "success");
			response.put("message", "Books in category '" + category + "' retrieved successfully");
			response.put("count", books.length);
			response.put("books", books);
			return response;
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", "Error retrieving books: " + e.getMessage());
			return response;
		}
	}

	/**
	 * POST /api/books - Create new book
	 * @param request - JSON with book details
	 * @return JSON with created book or error
	 */
	@PostMapping
	public Map<String, Object> createBook(@RequestBody Map<String, Object> request) {
		Map<String, Object> response = new HashMap<>();

		// Validate required fields
		if (!request.containsKey("title") || !request.containsKey("author") || !request.containsKey("price")
				|| !request.containsKey("category") || !request.containsKey("imageUrl")) {
			response.put("status", "error");
			response.put("message", "Missing required fields: title, author, price, category, imageUrl");
			return response;
		}

		try {
			String title = (String) request.get("title");
			String author = (String) request.get("author");
			double price = Double.parseDouble(request.get("price").toString());
			String category = (String) request.get("category");
			String description = (String) request.getOrDefault("description", "");
			String imageUrl = (String) request.get("imageUrl");
			boolean isFeatured = (boolean) request.getOrDefault("isFeatured", false);

			BookDTO book = bookService.createBook(title, author, price, category, description, imageUrl, isFeatured);

			if (book != null) {
				response.put("status", "success");
				response.put("message", "Book created successfully");
				response.put("book", book);
				return response;
			} else {
				response.put("status", "error");
				response.put("message", "Failed to create book. Check required fields and category validity.");
				return response;
			}
		} catch (NumberFormatException e) {
			response.put("status", "error");
			response.put("message", "Invalid price format");
			return response;
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", "Error creating book: " + e.getMessage());
			return response;
		}
	}

	/**
	 * PUT /api/books/{bookId} - Update existing book
	 * @param bookId - Book ID to update
	 * @param request - JSON with updated book details
	 * @return JSON with updated book or error
	 */
	@PutMapping("/{bookId}")
	public Map<String, Object> updateBook(@PathVariable String bookId, @RequestBody Map<String, Object> request) {
		Map<String, Object> response = new HashMap<>();

		// Validate required fields
		if (!request.containsKey("title") || !request.containsKey("author") || !request.containsKey("price")
				|| !request.containsKey("category") || !request.containsKey("imageUrl")) {
			response.put("status", "error");
			response.put("message", "Missing required fields: title, author, price, category, imageUrl");
			return response;
		}

		try {
			String title = (String) request.get("title");
			String author = (String) request.get("author");
			double price = Double.parseDouble(request.get("price").toString());
			String category = (String) request.get("category");
			String description = (String) request.getOrDefault("description", "");
			String imageUrl = (String) request.get("imageUrl");
			boolean isFeatured = (boolean) request.getOrDefault("isFeatured", false);

			BookDTO book = bookService.updateBook(bookId, title, author, price, category, description, imageUrl,
					isFeatured);

			if (book != null) {
				response.put("status", "success");
				response.put("message", "Book updated successfully");
				response.put("book", book);
				return response;
			} else {
				response.put("status", "error");
				response.put("message", "Book not found or invalid data provided");
				return response;
			}
		} catch (NumberFormatException e) {
			response.put("status", "error");
			response.put("message", "Invalid price format");
			return response;
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", "Error updating book: " + e.getMessage());
			return response;
		}
	}

	/**
	 * DELETE /api/books/{bookId} - Delete book
	 * @param bookId - Book ID to delete
	 * @return JSON with success or error message
	 */
	@DeleteMapping("/{bookId}")
	public Map<String, Object> deleteBook(@PathVariable String bookId) {
		Map<String, Object> response = new HashMap<>();

		try {
			boolean deleted = bookService.deleteBook(bookId);

			if (deleted) {
				response.put("status", "success");
				response.put("message", "Book deleted successfully");
				return response;
			} else {
				response.put("status", "error");
				response.put("message", "Book not found");
				return response;
			}
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", "Error deleting book: " + e.getMessage());
			return response;
		}
	}
}
