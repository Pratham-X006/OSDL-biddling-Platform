package com.bidding.util;

import java.io.*;

/**
 * Generates unique IDs for Users, Products, and Bids.
 * Uses DataInputStream / DataOutputStream for binary file storage of counters.
 * Ensures no duplicate IDs even across application restarts.
 */
public class IdGenerator {

    private static final String ID_FILE = "data/id_counters.dat";

    // Counter fields
    private static int userIdCounter = 1;
    private static int productIdCounter = 1;
    private static int bidIdCounter = 1;

    /**
     * Load ID counters from binary file on startup.
     * Uses DataInputStream (byte-level file handling).
     */
    public static void loadCounters() {
        File file = new File(ID_FILE);
        if (!file.exists()) return;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            userIdCounter = dis.readInt();
            productIdCounter = dis.readInt();
            bidIdCounter = dis.readInt();
        } catch (IOException e) {
            System.err.println("Warning: Could not load ID counters. Starting from 1.");
        }
    }

    /**
     * Save ID counters to binary file.
     * Uses DataOutputStream (byte-level file handling).
     */
    public static void saveCounters() {
        File file = new File(ID_FILE);
        file.getParentFile().mkdirs();

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            dos.writeInt(userIdCounter);
            dos.writeInt(productIdCounter);
            dos.writeInt(bidIdCounter);
        } catch (IOException e) {
            System.err.println("Error: Could not save ID counters.");
        }
    }

    /**
     * Get the next unique user ID.
     */
    public static synchronized int nextUserId() {
        int id = userIdCounter++;
        saveCounters();
        return id;
    }

    /**
     * Get the next unique product ID.
     */
    public static synchronized int nextProductId() {
        int id = productIdCounter++;
        saveCounters();
        return id;
    }

    /**
     * Get the next unique bid ID.
     */
    public static synchronized int nextBidId() {
        int id = bidIdCounter++;
        saveCounters();
        return id;
    }
}
