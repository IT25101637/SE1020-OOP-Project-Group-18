package com.bookstore.onlinebookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * PageController - Handles routing for HTML pages
 * Routes page requests to their corresponding HTML files
 */
@Controller
public class PageController {

	/**
	 * Serve admin login page
	 * Maps /admin/login to admin/login.html
	 */
	@GetMapping("/admin/login")
	public String adminLogin() {
		return "forward:/admin/login.html";
	}

	/**
	 * Serve admin dashboard page
	 * Maps /admin/dashboard to admin/dashboard.html
	 */
	@GetMapping("/admin/dashboard")
	public String adminDashboard() {
		return "forward:/admin/dashboard.html";
	}

	/**
	 * Serve admin books management page
	 * Maps /admin/books to admin/books.html
	 */
	@GetMapping("/admin/books")
	public String adminBooks() {
		return "forward:/admin/books.html";
	}

	/**
	 * Serve cart page
	 * Maps /cart to cart.html
	 */
	@GetMapping("/cart")
	public String cartPage() {
		return "forward:/cart.html";
	}
}
