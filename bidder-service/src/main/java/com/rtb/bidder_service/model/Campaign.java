package com.rtb.bidder_service.model;

import java.math.BigDecimal;
import jakarta.persistence.Entity; // Ensure you have these imports
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "campaigns")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String advertiserId;
    private BigDecimal budget;
    private String targetingGeo;
    private String targetingOs;

    // === NEW FIELD ===
    // This is the price we will bid
    private BigDecimal bidPrice;
    // =================

    // Default constructor for JPA
    public Campaign() {}

    // === UPDATED CONSTRUCTOR ===
    // We've added bidPrice to the constructor
    public Campaign(String advertiserId, BigDecimal budget, String targetingGeo, String targetingOs, BigDecimal bidPrice) {
        this.advertiserId = advertiserId;
        this.budget = budget;
        this.targetingGeo = targetingGeo;
        this.targetingOs = targetingOs;
        this.bidPrice = bidPrice; // Set the new field
    }
    // ===========================

    // --- Your existing getters and setters ---
    // (I'm assuming they look like this)
    public Long getId() {
        return id;
    }
    
    public String getAdvertiserId() {
        return advertiserId;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public String getTargetingGeo() {
        return targetingGeo;
    }

    public String getTargetingOs() {
        return targetingOs;
    }

    // === NEW GETTER / SETTER ===
    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }
    // ===========================
}