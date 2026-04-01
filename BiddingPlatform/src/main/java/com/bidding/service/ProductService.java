package com.bidding.service;

import com.bidding.enums.AuctionStatus;
import com.bidding.model.Product;
import com.bidding.util.DataStore;
import com.bidding.util.FileManager;
import com.bidding.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all product-related business logic:
 * adding products, listing, and closing auctions.
 */
public class ProductService {

    // Generic DataStore for Products
    private DataStore<Product> productStore;

    public ProductService() {
        productStore = new DataStore<>();
        List<Product> savedProducts = FileManager.loadProducts();
        productStore.setAll(savedProducts);
    }

    /**
     * Add a new product listing by a seller.
     * Returns null on success, error string on failure.
     */
    public String addProduct(String name, String description, Double basePrice, Integer sellerId) {
        if (name == null || name.trim().isEmpty()) return "Product name cannot be empty.";
        if (basePrice == null || basePrice <= 0) return "Base price must be greater than 0.";

        Integer newId = IdGenerator.nextProductId();
        Product product = new Product(newId, name.trim(),
            description == null ? "" : description.trim(), basePrice, sellerId);

        productStore.add(product);
        FileManager.saveProducts(productStore.getAll());

        return null; // null = success
    }

    /**
     * Get all open products (available for bidding).
     */
    public List<Product> getOpenProducts() {
        List<Product> open = new ArrayList<>();
        for (Product p : productStore.getAll()) {
            if (p.getStatus() == AuctionStatus.OPEN) {
                open.add(p);
            }
        }
        return open;
    }

    /**
     * Get all products listed by a specific seller.
     */
    public List<Product> getProductsBySeller(Integer sellerId) {
        List<Product> result = new ArrayList<>();
        for (Product p : productStore.getAll()) {
            if (p.getSellerId().equals(sellerId)) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * Get all products (for admin/log view).
     */
    public List<Product> getAllProducts() {
        return productStore.getAll();
    }

    /**
     * Find a product by its ID.
     */
    public Product findById(Integer productId) {
        for (Product p : productStore.getAll()) {
            if (p.getProductId().equals(productId)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Update a product in memory and persist to file.
     * Used after a bid is placed or auction is closed.
     */
    public void updateProduct(Product updated) {
        List<Product> all = productStore.getAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getProductId().equals(updated.getProductId())) {
                all.set(i, updated);
                break;
            }
        }
        productStore.setAll(all);
        FileManager.saveProducts(productStore.getAll());
    }

    /**
     * Close an auction — only the seller who owns it can close it.
     * Returns null on success, error string on failure.
     */
    public String closeAuction(Integer productId, Integer sellerId) {
        Product product = findById(productId);
        if (product == null) return "Product not found.";
        if (!product.getSellerId().equals(sellerId)) return "You can only close your own auctions.";
        if (product.getStatus() == AuctionStatus.CLOSED) return "Auction is already closed.";

        product.setStatus(AuctionStatus.CLOSED);
        updateProduct(product);
        return null; // success
    }
}
