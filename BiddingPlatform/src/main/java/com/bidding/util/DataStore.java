package com.bidding.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic in-memory data store using Generics.
 * Acts as the runtime cache for Users, Products, and Bids.
 *
 * @param <T> The type of object to store (User, Product, Bid)
 */
public class DataStore<T> {

    // Internal list to hold all objects of type T
    private List<T> items;

    public DataStore() {
        this.items = new ArrayList<>();
    }

    /**
     * Add a new item to the store.
     */
    public void add(T item) {
        items.add(item);
    }

    /**
     * Remove an item from the store.
     */
    public void remove(T item) {
        items.remove(item);
    }

    /**
     * Get all items in the store.
     */
    public List<T> getAll() {
        return new ArrayList<>(items); // Return a copy to prevent external mutation
    }

    /**
     * Replace all items (used after reloading from file).
     */
    public void setAll(List<T> newItems) {
        this.items = new ArrayList<>(newItems);
    }

    /**
     * Clear all items.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Get count of items.
     */
    public int size() {
        return items.size();
    }

    /**
     * Check if store is empty.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
