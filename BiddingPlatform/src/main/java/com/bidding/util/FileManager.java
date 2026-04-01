package com.bidding.util;

import com.bidding.model.Bid;
import com.bidding.model.Product;
import com.bidding.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all file I/O for the application.
 *
 * Users      → Java Serialization (ObjectOutputStream / ObjectInputStream)
 * Products   → BufferedReader / BufferedWriter (CSV format)
 * Bids       → BufferedReader / BufferedWriter (CSV format)
 * Product ID lookup → RandomAccessFile (fast seek)
 */
public class FileManager {

    // File paths
    private static final String DATA_DIR = "data/";
    private static final String USERS_FILE = DATA_DIR + "users.ser";
    private static final String PRODUCTS_FILE = DATA_DIR + "products.csv";
    private static final String BIDS_FILE = DATA_DIR + "bids.csv";

    /**
     * Ensure data directory exists.
     */
    public static void initDataDirectory() {
        new File(DATA_DIR).mkdirs();
    }

    // ─────────────────────────────────────────────
    // USER FILE OPERATIONS (Serialization)
    // ─────────────────────────────────────────────

    /**
     * Save all users to file using Java Serialization.
     */
    @SuppressWarnings("unchecked")
    public static void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    /**
     * Load all users from serialized file.
     */
    @SuppressWarnings("unchecked")
    public static List<User> loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading users: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ─────────────────────────────────────────────
    // PRODUCT FILE OPERATIONS (BufferedReader/Writer)
    // ─────────────────────────────────────────────

    /**
     * Save all products to file using BufferedWriter.
     */
    public static void saveProducts(List<Product> products) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product p : products) {
                bw.write(p.toCsvLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving products: " + e.getMessage());
        }
    }

    /**
     * Load all products from file using BufferedReader.
     */
    public static List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) return products;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    products.add(Product.fromCsvLine(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading products: " + e.getMessage());
        }
        return products;
    }

    // ─────────────────────────────────────────────
    // BID FILE OPERATIONS (BufferedReader/Writer)
    // ─────────────────────────────────────────────

    /**
     * Save all bids to file using BufferedWriter.
     */
    public static void saveBids(List<Bid> bids) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BIDS_FILE))) {
            for (Bid b : bids) {
                bw.write(b.toCsvLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving bids: " + e.getMessage());
        }
    }

    /**
     * Load all bids from file using BufferedReader.
     */
    public static List<Bid> loadBids() {
        List<Bid> bids = new ArrayList<>();
        File file = new File(BIDS_FILE);
        if (!file.exists()) return bids;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    bids.add(Bid.fromCsvLine(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading bids: " + e.getMessage());
        }
        return bids;
    }

    // ─────────────────────────────────────────────
    // RANDOM ACCESS FILE — Fast Product Lookup
    // ─────────────────────────────────────────────

    /**
     * Uses RandomAccessFile to quickly check if a product file exists and
     * read its first line (header/first product check).
     * Demonstrates RAF usage for fast random access to file data.
     */
    public static String readFirstProductLine() {
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) return null;

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            if (raf.length() == 0) return null;
            raf.seek(0); // Go to start of file
            return raf.readLine();
        } catch (IOException e) {
            System.err.println("RAF error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Uses RandomAccessFile to append a single product line efficiently.
     * Seeks to end of file and writes.
     */
    public static void appendProductWithRAF(Product product) {
        File file = new File(PRODUCTS_FILE);
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            raf.seek(raf.length()); // Seek to end
            raf.writeBytes(product.toCsvLine() + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("RAF append error: " + e.getMessage());
        }
    }
}
