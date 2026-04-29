package com.bookstore.onlinebookstore.util;

/**
 * LinkedListUtil - Generic Linked List implementation
 * Demonstrates Data Structure concepts and Polymorphism
 * Custom implementation for storing and managing objects
 */
public class LinkedListUtil<T> {
    
    /**
     * Node inner class - represents each element in the linked list
     */
    private class Node {
        T data;
        Node next;
        
        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }
    
    private Node head;
    private int size;
    
    /**
     * Constructor - initializes empty linked list
     */
    public LinkedListUtil() {
        this.head = null;
        this.size = 0;
    }
    
    /**
     * Add element at the end of the list
     * @param data - element to add
     */
    public void add(T data) {
        Node newNode = new Node(data);
        
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }
    
    /**
     * Remove element at specified index
     * @param index - index to remove from
     * @return removed element or null
     */
    public T remove(int index) {
        if (index < 0 || index >= size || head == null) {
            return null;
        }
        
        T removedData;
        if (index == 0) {
            removedData = head.data;
            head = head.next;
        } else {
            Node current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            removedData = current.next.data;
            current.next = current.next.next;
        }
        size--;
        return removedData;
    }
    
    /**
     * Get element at specified index
     * @param index - index to get from
     * @return element at index or null
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }
    
    /**
     * Get size of the list
     * @return size
     */
    public int size() {
        return size;
    }
    
    /**
     * Check if list is empty
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Clear the entire list
     */
    public void clear() {
        head = null;
        size = 0;
    }
    
    /**
     * Check if list contains element
     * @param data - element to search
     * @return true if found, false otherwise
     */
    public boolean contains(T data) {
        Node current = head;
        while (current != null) {
            if (current.data.equals(data)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }
    
    /**
     * Convert list to array
     * @return array of elements
     */
    public Object[] toArray() {
        Object[] array = new Object[size];
        Node current = head;
        int index = 0;
        while (current != null) {
            array[index++] = current.data;
            current = current.next;
        }
        return array;
    }
}

