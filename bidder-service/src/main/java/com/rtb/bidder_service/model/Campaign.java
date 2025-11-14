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

    private BigDecimal bidPrice;
    public Campaign() {}
    public Campaign(String advertiserId, BigDecimal budget, String targetingGeo, String targetingOs, BigDecimal bidPrice) {
        this.advertiserId = advertiserId;
        this.budget = budget;
        this.targetingGeo = targetingGeo;
        this.targetingOs = targetingOs;
        this.bidPrice = bidPrice;
    }
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
    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }
}