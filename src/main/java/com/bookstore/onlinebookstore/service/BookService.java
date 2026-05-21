package com.bookstore.onlinebookstore.service;

import com.bookstore.onlinebookstore.dto.BookDTO;
import com.bookstore.onlinebookstore.model.Book;
import com.bookstore.onlinebookstore.repository.BookRepository;

/**
 * BookService - Business Logic Layer for Book operations
 * Encapsulates validation and business rules
 */
public class BookService {
	private BookRepository bookRepository;

	public BookService() {
		this.bookRepository = new BookRepository();
	}

	// CREATE BOOK
	public BookDTO createBook(String title, String author, double price, String category, String description,
			String imageUrl, boolean isFeatured) {
		// Validate required fields
		if (title == null || title.trim().isEmpty() || author == null || author.trim().isEmpty()
				|| category == null || category.trim().isEmpty() || imageUrl == null
				|| imageUrl.trim().isEmpty()) {
			return null;
		}

		// Validate price
		if (price <= 0) {
			return null;
		}

		// Validate category
		if (!isValidCategory(category)) {
			return null;
		}

		// Create book
		Book book = new Book(title.trim(), author.trim(), price, category.trim(), description != null ? description.trim() : "", imageUrl.trim(), isFeatured);

		Book createdBook = bookRepository.create(book);
		if (createdBook != null) {
			return convertBookToDTO(createdBook);
		}
		return null;
	}

	// READ ALL BOOKS
	public Object[] getAllBooks() {
		Object[] books = bookRepository.findAll();
		BookDTO[] dtos = new BookDTO[books.length];
		for (int i = 0; i < books.length; i++) {
			dtos[i] = convertBookToDTO((Book) books[i]);
		}
		return dtos;
	}

	// READ FEATURED BOOKS
	public Object[] getFeaturedBooks() {
		Object[] books = bookRepository.findFeatured();
		BookDTO[] dtos = new BookDTO[books.length];
		for (int i = 0; i < books.length; i++) {
			dtos[i] = convertBookToDTO((Book) books[i]);
		}
		return dtos;
	}

	// READ BOOK BY ID
	public BookDTO getBookById(String bookId) {
		if (bookId == null || bookId.trim().isEmpty()) {
			return null;
		}

		Book book = bookRepository.findById(bookId);
		if (book == null) {
			return null;
		}

		return convertBookToDTO(book);
	}

	// READ BOOKS BY CATEGORY
	public Object[] getBooksByCategory(String category) {
		if (category == null || category.trim().isEmpty()) {
			return new BookDTO[0];
		}

		// If "All Genre", return all books
		if (category.equalsIgnoreCase("all-genre") || category.equalsIgnoreCase("All Genre")) {
			return getAllBooks();
		}

		if (!isValidCategory(category)) {
			return new BookDTO[0];
		}

		Object[] books = bookRepository.findByCategory(category);
		BookDTO[] dtos = new BookDTO[books.length];
		for (int i = 0; i < books.length; i++) {
			dtos[i] = convertBookToDTO((Book) books[i]);
		}
		return dtos;
	}

	// READ ALL BOOKS SORTED BY PRICE
	public Object[] getAllBooksSortedByPrice() {
		Object[] books = bookRepository.findAllSortedByPrice();
		BookDTO[] dtos = new BookDTO[books.length];
		for (int i = 0; i < books.length; i++) {
			dtos[i] = convertBookToDTO((Book) books[i]);
		}
		return dtos;
	}

	// READ ALL BOOKS SORTED BY NAME
	public Object[] getAllBooksSortedByName() {
		Object[] books = bookRepository.findAllSortedByName();
		BookDTO[] dtos = new BookDTO[books.length];
		for (int i = 0; i < books.length; i++) {
			dtos[i] = convertBookToDTO((Book) books[i]);
		}
		return dtos;
	}

	// READ BOOKS BY CATEGORY SORTED BY PRICE
	public Object[] getBooksByCategorySortedByPrice(String category) {
		if (category == null || category.trim().isEmpty()) {
			return new BookDTO[0];
		}
		if (category.equalsIgnoreCase("all-genre") || category.equalsIgnoreCase("All Genre")) {
			return getAllBooksSortedByPrice();
		}
		if (!isValidCategory(category)) {
			return new BookDTO[0];
		}
		Object[] books = bookRepository.findByCategorySortedByPrice(category);
		BookDTO[] dtos = new BookDTO[books.length];
		for (int i = 0; i < books.length; i++) {
			dtos[i] = convertBookToDTO((Book) books[i]);
		}
		return dtos;
	}

	// READ BOOKS BY CATEGORY SORTED BY NAME
	public Object[] getBooksByCategorySortedByName(String category) {
		if (category == null || category.trim().isEmpty()) {
			return new BookDTO[0];
		}
		if (category.equalsIgnoreCase("all-genre") || category.equalsIgnoreCase("All Genre")) {
			return getAllBooksSortedByName();
		}
		if (!isValidCategory(category)) {
			return new BookDTO[0];
		}
		Object[] books = bookRepository.findByCategorySortedByName(category);
		BookDTO[] dtos = new BookDTO[books.length];
		for (int i = 0; i < books.length; i++) {
			dtos[i] = convertBookToDTO((Book) books[i]);
		}
		return dtos;
	}

	// UPDATE BOOK
	public BookDTO updateBook(String bookId, String title, String author, double price, String category,
			String description, String imageUrl, boolean isFeatured) {
		// Validate required fields
		if (bookId == null || bookId.trim().isEmpty() || title == null || title.trim().isEmpty()
				|| author == null || author.trim().isEmpty() || category == null || category.trim().isEmpty()
				|| imageUrl == null || imageUrl.trim().isEmpty()) {
			return null;
		}

		// Validate price
		if (price <= 0) {
			return null;
		}

		// Validate category
		if (!isValidCategory(category)) {
			return null;
		}

		// Check if book exists
		Book existingBook = bookRepository.findById(bookId);
		if (existingBook == null) {
			return null;
		}

		// Update book
		existingBook.setTitle(title.trim());
		existingBook.setAuthor(author.trim());
		existingBook.setPrice(price);
		existingBook.setCategory(category.trim());
		existingBook.setDescription(description != null ? description.trim() : "");
		existingBook.setImageUrl(imageUrl.trim());
		existingBook.setFeatured(isFeatured);

		Book updatedBook = bookRepository.update(existingBook);
		if (updatedBook != null) {
			return convertBookToDTO(updatedBook);
		}
		return null;
	}

	// DELETE BOOK
	public boolean deleteBook(String bookId) {
		if (bookId == null || bookId.trim().isEmpty()) {
			return false;
		}
		return bookRepository.deleteById(bookId);
	}

	// PRIVATE HELPER METHODS

	// Convert Book to BookDTO
	private BookDTO convertBookToDTO(Book book) {
		return new BookDTO(book.getBookId(), book.getTitle(), book.getAuthor(), book.getPrice(),
				book.getCategory(), book.getDescription(), book.getImageUrl(), book.isFeatured());
	}

	// Validate category
	private boolean isValidCategory(String category) {
		if (category == null || category.trim().isEmpty()) {
			return false;
		}

		String cat = category.trim().toLowerCase();
		return cat.equals("business") || cat.equals("technology") || cat.equals("romantic")
				|| cat.equals("adventure") || cat.equals("fictional");
	}
}
