package com.bidding.model;

import com.bidding.enums.AuctionStatus;

/**
 * Represents a product/auction item listed by a seller.
 * Stored using BufferedReader/Writer (CSV format).
 */
public class Product {

    private Integer productId;        // Wrapper class
    private String name;
    private String description;
    private Double basePrice;         // Wrapper class
    private Double highestBid;        // Tracks current highest bid
    private String highestBidder;     // Username of highest bidder
    private AuctionStatus status;     // OPEN or CLOSED
    private Integer sellerId;         // Who listed this product

    // Constructor
    public Product(Integer productId, String name, String description,
                   Double basePrice, Integer sellerId) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.highestBid = basePrice;   // Highest bid starts at base price
        this.highestBidder = "None";
        this.status = AuctionStatus.OPEN;
        this.sellerId = sellerId;
    }

    // Full constructor (used when loading from file)
    public Product(Integer productId, String name, String description,
                   Double basePrice, Double highestBid, String highestBidder,
                   AuctionStatus status, Integer sellerId) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.highestBid = highestBid;
        this.highestBidder = highestBidder;
        this.status = status;
        this.sellerId = sellerId;
    }

    // Getters and Setters
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }

    public Double getHighestBid() { return highestBid; }
    public void setHighestBid(Double highestBid) { this.highestBid = highestBid; }

    public String getHighestBidder() { return highestBidder; }
    public void setHighestBidder(String highestBidder) { this.highestBidder = highestBidder; }

    public AuctionStatus getStatus() { return status; }
    public void setStatus(AuctionStatus status) { this.status = status; }

    public Integer getSellerId() { return sellerId; }
    public void setSellerId(Integer sellerId) { this.sellerId = sellerId; }

    /**
     * Converts product to CSV line for file storage.
     * Fields separated by | to avoid conflicts with commas in description.
     */
    public String toCsvLine() {
        return productId + "|" + name + "|" + description + "|" +
               basePrice + "|" + highestBid + "|" + highestBidder + "|" +
               status.name() + "|" + sellerId;
    }

    /**
     * Creates a Product object from a CSV line.
     */
    public static Product fromCsvLine(String line) {
        String[] parts = line.split("\\|", -1);
        return new Product(
            Integer.parseInt(parts[0]),
            parts[1],
            parts[2],
            Double.parseDouble(parts[3]),
            Double.parseDouble(parts[4]),
            parts[5],
            AuctionStatus.valueOf(parts[6]),
            Integer.parseInt(parts[7])
        );
    }

    @Override
    public String toString() {
        return "Product{id=" + productId + ", name='" + name + "', highestBid=" + highestBid + ", status=" + status + "}";
    }
}
