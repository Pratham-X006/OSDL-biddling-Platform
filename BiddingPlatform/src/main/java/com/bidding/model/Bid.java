package com.bidding.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single bid placed by a buyer on a product.
 * Stored using BufferedReader/Writer (CSV format).
 */
public class Bid {

    private Integer bidId;           // Wrapper class
    private Integer productId;       // Which product was bid on
    private Integer bidderId;        // Who placed the bid
    private String bidderUsername;
    private Double amount;           // Bid amount (Wrapper class)
    private String timestamp;        // When the bid was placed

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Constructor for new bids
    public Bid(Integer bidId, Integer productId, Integer bidderId,
               String bidderUsername, Double amount) {
        this.bidId = bidId;
        this.productId = productId;
        this.bidderId = bidderId;
        this.bidderUsername = bidderUsername;
        this.amount = amount;
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }

    // Full constructor (used when loading from file)
    public Bid(Integer bidId, Integer productId, Integer bidderId,
               String bidderUsername, Double amount, String timestamp) {
        this.bidId = bidId;
        this.productId = productId;
        this.bidderId = bidderId;
        this.bidderUsername = bidderUsername;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Integer getBidId() { return bidId; }
    public void setBidId(Integer bidId) { this.bidId = bidId; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public Integer getBidderId() { return bidderId; }
    public void setBidderId(Integer bidderId) { this.bidderId = bidderId; }

    public String getBidderUsername() { return bidderUsername; }
    public void setBidderUsername(String bidderUsername) { this.bidderUsername = bidderUsername; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    /**
     * Converts bid to CSV line for file storage.
     */
    public String toCsvLine() {
        return bidId + "|" + productId + "|" + bidderId + "|" +
               bidderUsername + "|" + amount + "|" + timestamp;
    }

    /**
     * Creates a Bid object from a CSV line.
     */
    public static Bid fromCsvLine(String line) {
        String[] parts = line.split("\\|", -1);
        return new Bid(
            Integer.parseInt(parts[0]),
            Integer.parseInt(parts[1]),
            Integer.parseInt(parts[2]),
            parts[3],
            Double.parseDouble(parts[4]),
            parts[5]
        );
    }

    @Override
    public String toString() {
        return "Bid{id=" + bidId + ", productId=" + productId +
               ", bidder='" + bidderUsername + "', amount=" + amount + "}";
    }
}
