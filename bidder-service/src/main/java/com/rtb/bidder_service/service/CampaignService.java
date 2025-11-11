package com.rtb.bidder_service.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.rtb.bidder_service.model.Campaign;
import com.rtb.bidder_service.repository.CampaignRepository;

@Service
// We implement CommandLineRunner to load our sample data into Postgres on startup.
public class CampaignService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CampaignService.class);
    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    /**
     * This is the "FAST PATH" method our Kafka listener will call.
     *
     * The @Cacheable annotation is the "magic" of Spring Cache. 
     * "value = "campaigns"" : This is the name of the cache in Redis.
     * "key = "#advertiserId"" : This tells Spring to use the 'advertiserId'
     *                          parameter as the unique key in Redis.
     *
     * Here is the flow:
     * 1. A request comes in (e.g., getCampaigns("adv-123")).
     * 2. Spring intercepts the call and checks the "campaigns" cache in Redis for a
     *    key named "adv-123".
     * 3. IF FOUND (Cache Hit): The data is returned *instantly* from Redis.
     *    The Java code inside this method is NEVER executed. This is how we
     *    achieve sub-millisecond latency. [2]
     * 4. IF NOT FOUND (Cache Miss): The Java method IS executed. Spring runs
     *    `campaignRepository.findByAdvertiserId(...)` (a slow Postgres query),
     *    gets the result, *automatically saves it to Redis* at key "adv-123",
     *    and then returns the data.
     * 5. The next call for "adv-123" will be a fast cache hit.
     */
    @Cacheable(value = "campaigns", key = "#advertiserId")
    public List<Campaign> getCampaigns(String advertiserId) {
        log.info("--- CACHE MISS --- Running slow database query for advertiser: {}", advertiserId);
        return campaignRepository.findByAdvertiserId(advertiserId);
    }

    /**
     * This is the "SLOW PATH" method. It runs only ONCE when the
     * application starts up to populate our PostgreSQL database.
     */
    @Override
    @CacheEvict(value = "campaigns", allEntries = true)
    public void run(String... args) throws Exception {
        loadSampleData();
    }

    private void loadSampleData() {
        log.info("Loading REAL sample campaign data into PostgreSQL based on data analysis...");

        // Clear any old data
        campaignRepository.deleteAll();

        // === NEW DATASET BASED ON MY GREAT DATA ANALYSIS ===
        List<Campaign> campaigns = new java.util.ArrayList<>();
        
        // --- Top 5 Most Frequent Combos I Found ---
        
        // 1. (79, 187, 56) - 9499 impressions
        campaigns.add(new Campaign("79", new BigDecimal("9000.00"), "187", "56", new BigDecimal("0.75")));
        
        // 2. (88, 187, 56) - 4189 impressions
        campaigns.add(new Campaign("88", new BigDecimal("5000.00"), "187", "56", new BigDecimal("0.65")));
        
        // 3. (90, 187, 56) - 3486 impressions
        campaigns.add(new Campaign("90", new BigDecimal("4000.00"), "187", "56", new BigDecimal("0.60")));
        
        // 4. (97, 187, 56) - 2634 impressions
        campaigns.add(new Campaign("97", new BigDecimal("3000.00"), "187", "56", new BigDecimal("0.55")));
        
        // 5. (139, 187, 55) - 4362 impressions
        campaigns.add(new Campaign("139", new BigDecimal("5000.00"), "187", "55", new BigDecimal("0.50")));

        // --- Other Top Hits for Advertiser 79 I Found ---
        
        // 6. (79, 187, 55) - 7901 impressions
        campaigns.add(new Campaign("79", new BigDecimal("8000.00"), "187", "55", new BigDecimal("0.70")));
        
        // // 7. (79, 187, 59) - 7558 impressions
        // campaigns.add(new Campaign("79", new BigDecimal("7500.00"), "187", "59", new BigDecimal("0.68")));
        
        // 8. (79, 187, 58) - 6285 impressions
        campaigns.add(new Campaign("79", new BigDecimal("6000.00"), "187", "58", new BigDecimal("0.62")));
        
        // 9. (79, 187, 60) - 5745 impressions
        campaigns.add(new Campaign("79", new BigDecimal("6000.00"), "187", "60", new BigDecimal("0.61")));

        campaignRepository.saveAll(campaigns);
        // ==========================================
        
        log.info("...Sample data loading complete. Loaded {} data-driven campaigns.", campaigns.size());
    }
}
