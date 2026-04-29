package com.bookstore.onlinebookstore.repository;

import com.bookstore.onlinebookstore.model.Book;
import com.bookstore.onlinebookstore.util.FileHandler;
import com.bookstore.onlinebookstore.util.LinkedListUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * BookRepository - Data Access Layer for Book
 * Handles all CRUD operations with file-based persistence
 * Uses FileHandler for file I/O and LinkedListUtil for in-memory storage
 */
public class BookRepository {
	private static final String BOOKS_FILE = "src/main/resources/data/books.txt";
	private LinkedListUtil<Book> books;
	private static int bookIdCounter = 1;

	public BookRepository() {
		this.books = new LinkedListUtil<>();
		loadBooksFromFile();
	}

	// CREATE - Add new book
	public Book create(Book book) {
		// Check if title already exists
		for (int i = 0; i < books.size(); i++) {
			if (books.get(i).getTitle().equalsIgnoreCase(book.getTitle())) {
				return null; // Book title already exists
			}
		}

		// Generate unique bookId
		String bookId = generateBookId();
		book.setBookId(bookId);

		// Add to in-memory list
		books.add(book);

		// Persist to file
		saveBooksToFile();

		return book;
	}

	// READ - Get all books
	public Object[] findAll() {
		return books.toArray();
	}

	// READ - Get book by ID
	public Book findById(String bookId) {
		for (int i = 0; i < books.size(); i++) {
			if (books.get(i).getBookId().equals(bookId)) {
				return books.get(i);
			}
		}
		return null;
	}

	// READ - Get all featured books
	public Object[] findFeatured() {
		LinkedListUtil<Book> featuredBooks = new LinkedListUtil<>();
		for (int i = 0; i < books.size(); i++) {
			if (books.get(i).isFeatured()) {
				featuredBooks.add(books.get(i));
			}
		}
		return featuredBooks.toArray();
	}

	// READ - Get books by category
	public Object[] findByCategory(String category) {
		LinkedListUtil<Book> categoryBooks = new LinkedListUtil<>();
		for (int i = 0; i < books.size(); i++) {
			if (books.get(i).getCategory().equalsIgnoreCase(category)) {
				categoryBooks.add(books.get(i));
			}
		}
		return categoryBooks.toArray();
	}

	// UPDATE - Update book
	public Book update(Book book) {
		for (int i = 0; i < books.size(); i++) {
			if (books.get(i).getBookId().equals(book.getBookId())) {
				// Update fields
				books.get(i).setTitle(book.getTitle());
				books.get(i).setAuthor(book.getAuthor());
				books.get(i).setPrice(book.getPrice());
				books.get(i).setCategory(book.getCategory());
				books.get(i).setDescription(book.getDescription());
				books.get(i).setImageUrl(book.getImageUrl());
				books.get(i).setFeatured(book.isFeatured());

				// Persist to file
				saveBooksToFile();

				return books.get(i);
			}
		}
		return null;
	}

	// DELETE - Delete book by ID
	public boolean deleteById(String bookId) {
		for (int i = 0; i < books.size(); i++) {
			if (books.get(i).getBookId().equals(bookId)) {
				books.remove(i);
				saveBooksToFile();
				return true;
			}
		}
		return false;
	}

	// PRIVATE METHODS

	// Generate unique book ID
	private String generateBookId() {
		return "BOOK_" + System.currentTimeMillis() + "_" + bookIdCounter++;
	}

	// Save all books to file
	private void saveBooksToFile() {
		List<String> lines = new ArrayList<>();
		for (int i = 0; i < books.size(); i++) {
			lines.add(books.get(i).toPipeDelimitedString());
		}
		FileHandler.writeFile(BOOKS_FILE, lines);
	}

	// Load all books from file
	private void loadBooksFromFile() {
		List<String> lines = FileHandler.readFile(BOOKS_FILE);
		for (String line : lines) {
			if (line != null && !line.trim().isEmpty()) {
				Book book = parseBookFromLine(line);
				if (book != null) {
					books.add(book);
				}
			}
		}
	}

	// Parse book from pipe-delimited line
	private Book parseBookFromLine(String line) {
		try {
			String[] parts = FileHandler.parsePipeDelimitedLine(line);
			if (parts.length >= 8) {
				return new Book(parts[0], // bookId
						parts[1], // title
						parts[2], // author
						Double.parseDouble(parts[3]), // price
						parts[4], // category
						parts[5], // description
						parts[6], // imageUrl
						Boolean.parseBoolean(parts[7])); // isFeatured
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
