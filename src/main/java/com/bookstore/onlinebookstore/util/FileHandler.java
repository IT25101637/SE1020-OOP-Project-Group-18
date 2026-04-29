package com.bookstore.onlinebookstore.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * FileHandler - Abstract utility class for file operations
 * Demonstrates Abstraction and Information Hiding
 * Handles pipe-delimited file read/write operations
 */
public abstract class FileHandler {
    
    /**
     * Read all lines from a file
     * @param filePath - path to the file
     * @return List of lines, empty list if file not found
     */
    public static List<String> readFile(String filePath) {
        List<String> lines = new ArrayList<>();
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                lines = Files.readAllLines(path);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            e.printStackTrace();
        }
        return lines;
    }
    
    /**
     * Write lines to a file (overwrites existing content)
     * @param filePath - path to the file
     * @param lines - list of lines to write
     */
    public static void writeFile(String filePath, List<String> lines) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.write(path, lines);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filePath);
            e.printStackTrace();
        }
    }
    
    /**
     * Append a line to a file
     * @param filePath - path to the file
     * @param line - line to append
     */
    public static void appendToFile(String filePath, String line) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.write(path, (line + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error appending to file: " + filePath);
            e.printStackTrace();
        }
    }
    
    /**
     * Parse a pipe-delimited string into an array
     * @param line - pipe-delimited line
     * @return array of values
     */
    public static String[] parsePipeDelimitedLine(String line) {
        return line.split("\\|");
    }
    
    /**
     * Check if file exists
     * @param filePath - path to the file
     * @return true if exists, false otherwise
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
}

