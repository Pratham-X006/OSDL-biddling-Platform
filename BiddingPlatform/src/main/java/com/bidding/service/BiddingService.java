package com.bidding.service;

import com.bidding.model.Bid;
import com.bidding.model.Product;
import com.bidding.model.User;
import com.bidding.enums.AuctionStatus;
import com.bidding.util.DataStore;
import com.bidding.util.FileManager;
import com.bidding.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all bidding logic.
 *
 * Uses multithreading (BidTask implements Runnable) and
 * synchronized blocks to prevent race conditions when multiple
 * users bid simultaneously.
 */
public class BiddingService {

    private DataStore<Bid> bidStore;
    private ProductService productService;

    // Lock object for synchronized bidding
    private final Object bidLock = new Object();

    public BiddingService(ProductService productService) {
        this.productService = productService;
        this.bidStore = new DataStore<>();
        List<Bid> savedBids = FileManager.loadBids();
        bidStore.setAll(savedBids);
    }

    /**
     * Place a bid using a background thread (BidTask).
     * Returns null on success, error message on failure.
     *
     * @param productId  The product being bid on
     * @param bidder     The user placing the bid
     * @param amount     The bid amount
     * @param callback   Called on the JavaFX thread with result (null = success)
     */
    public void placeBidAsync(Integer productId, User bidder, Double amount,
                              BidResultCallback callback) {
        // Create and start a BidTask thread
        BidTask task = new BidTask(productId, bidder, amount, callback);
        Thread thread = new Thread(task);
        thread.setDaemon(true); // App can exit even if thread is running
        thread.start();
    }

    /**
     * Synchronous bid placement — used internally by BidTask.
     * SYNCHRONIZED to prevent race conditions.
     */
    private String placeBidInternal(Integer productId, User bidder, Double amount) {
        synchronized (bidLock) {
            // Validate: product must exist and be open
            Product product = productService.findById(productId);
            if (product == null) return "Product not found.";
            if (product.getStatus() == AuctionStatus.CLOSED) return "This auction is already closed.";

            // Prevent seller from bidding on their own product
            if (product.getSellerId().equals(bidder.getUserId())) {
                return "You cannot bid on your own product.";
            }

            // Bid must be higher than current highest bid
            if (amount <= product.getHighestBid()) {
                return "Bid must be higher than current highest bid of ₹" +
                       String.format("%.2f", product.getHighestBid());
            }

            // Create and store the bid
            Integer bidId = IdGenerator.nextBidId();
            Bid bid = new Bid(bidId, productId, bidder.getUserId(),
                              bidder.getUsername(), amount);
            bidStore.add(bid);
            FileManager.saveBids(bidStore.getAll());

            // Update the product's highest bid
            product.setHighestBid(amount);
            product.setHighestBidder(bidder.getUsername());
            productService.updateProduct(product);

            return null; // success
        }
    }

    /**
     * Get all bids for a specific product.
     */
    public List<Bid> getBidsForProduct(Integer productId) {
        List<Bid> result = new ArrayList<>();
        for (Bid b : bidStore.getAll()) {
            if (b.getProductId().equals(productId)) {
                result.add(b);
            }
        }
        return result;
    }

    /**
     * Get all bids placed by a specific user.
     */
    public List<Bid> getBidsByUser(Integer userId) {
        List<Bid> result = new ArrayList<>();
        for (Bid b : bidStore.getAll()) {
            if (b.getBidderId().equals(userId)) {
                result.add(b);
            }
        }
        return result;
    }

    /**
     * Get all bids (for auction log).
     */
    public List<Bid> getAllBids() {
        return bidStore.getAll();
    }

    // ─────────────────────────────────────────────
    // INNER CLASS: BidTask (Multithreading — Runnable)
    // ─────────────────────────────────────────────

    /**
     * Runnable task that processes a bid in a background thread.
     * Demonstrates multithreading requirement.
     */
    private class BidTask implements Runnable {

        private final Integer productId;
        private final User bidder;
        private final Double amount;
        private final BidResultCallback callback;

        public BidTask(Integer productId, User bidder, Double amount,
                       BidResultCallback callback) {
            this.productId = productId;
            this.bidder = bidder;
            this.amount = amount;
            this.callback = callback;
        }

        @Override
        public void run() {
            // Simulate brief processing delay (realistic async feel)
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Place the bid synchronously (thread-safe)
            String result = placeBidInternal(productId, bidder, amount);

            // Return result to caller via callback on JavaFX thread
            if (callback != null) {
                javafx.application.Platform.runLater(() -> callback.onResult(result));
            }
        }
    }

    // ─────────────────────────────────────────────
    // CALLBACK INTERFACE
    // ─────────────────────────────────────────────

    /**
     * Callback interface for async bid result.
     * result is null on success, error string on failure.
     */
    public interface BidResultCallback {
        void onResult(String errorOrNull);
    }
}
