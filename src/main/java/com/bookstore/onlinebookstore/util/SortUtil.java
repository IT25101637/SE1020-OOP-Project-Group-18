package com.bookstore.onlinebookstore.util;

import com.bookstore.onlinebookstore.model.Book;

/**
 * SortUtil - Sorting utility for Book collections
 * Implements bubble sort for sorting by price (ascending) and name (ascending).
 * Operates on plain Object[] arrays (as returned by LinkedListUtil.toArray())
 * and returns a new sorted array without mutating the original.
 */
public class SortUtil {

    /**
     * Sort books by price ascending using bubble sort.
     *
     * @param books Object[] where each element is a Book
     * @return new sorted Object[]
     */
    public static Object[] sortByPrice(Object[] books) {
        if (books == null || books.length <= 1) {
            return books;
        }

        // Shallow copy so we don't mutate the original array
        Object[] sorted = books.clone();
        int n = sorted.length;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                Book a = (Book) sorted[j];
                Book b = (Book) sorted[j + 1];
                if (a.getPrice() > b.getPrice()) {
                    Object tmp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = tmp;
                }
            }
        }
        return sorted;
    }

    /**
     * Sort books by title ascending (case-insensitive) using bubble sort.
     *
     * @param books Object[] where each element is a Book
     * @return new sorted Object[]
     */
    public static Object[] sortByName(Object[] books) {
        if (books == null || books.length <= 1) {
            return books;
        }

        Object[] sorted = books.clone();
        int n = sorted.length;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                Book a = (Book) sorted[j];
                Book b = (Book) sorted[j + 1];
                if (a.getTitle().compareToIgnoreCase(b.getTitle()) > 0) {
                    Object tmp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = tmp;
                }
            }
        }
        return sorted;
    }
}
