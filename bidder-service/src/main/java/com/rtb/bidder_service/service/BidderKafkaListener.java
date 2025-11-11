package com.rtb.bidder_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate; // NEW IMPORT
import org.springframework.stereotype.Service;

import com.rtb.bidder_service.dto.BidRequest;
import com.rtb.bidder_service.dto.BidResponse; // NEW IMPORT
import com.rtb.bidder_service.model.Campaign;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class BidderKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(BidderKafkaListener.class);
    private static final String REQUEST_TOPIC = "bid-requests";
    private static final String RESPONSE_TOPIC = "bid-responses"; // NEW TOPIC

    private final CampaignService campaignService;
    private final Timer bidRequestTimer;

    // === NEW KAFKA TEMPLATE ===
    private final KafkaTemplate<String, BidResponse> kafkaTemplate;
    // ==========================

    // === UPDATED CONSTRUCTOR ===
    public BidderKafkaListener(CampaignService campaignService,
                               MeterRegistry meterRegistry,
                               KafkaTemplate<String, BidResponse> kafkaTemplate) { // Added template
        this.campaignService = campaignService;
        this.kafkaTemplate = kafkaTemplate; // Set the template

        this.bidRequestTimer = Timer.builder("rtb.bid.request.timer")
            .description("Measures the p99 latency and throughput of handling a bid request")
            .publishPercentileHistogram(true)
            .register(meterRegistry);
    }
    // ==========================

    @KafkaListener(topics = REQUEST_TOPIC, groupId = "${spring.kafka.consumer.group-id}", concurrency = "3")
    public void handleBidRequest(BidRequest request) {

        bidRequestTimer.record(() -> {
            try {
                log.info("Received request: {}", request.impressionId());

                List<Campaign> campaigns = campaignService.getCampaigns(request.advertiserId());

                boolean didBid = false;
                if (campaigns != null && !campaigns.isEmpty()) {
                    for (Campaign campaign : campaigns) {
                        if (matchesTargeting(campaign, request)) {
                            log.info("--- BIDDING --- Impression: {} Matched Campaign: {}",
                                    request.impressionId(), campaign.getId());

                            // === THE 0.01% STEP: SEND THE BID ===
                            // We are now replying with our bid
                            BidResponse bidResponse = new BidResponse(
                                request.impressionId(),
                                String.valueOf(campaign.getId()), // The campaign's DB ID
                                campaign.getAdvertiserId(),
                                campaign.getBidPrice()
                            );

                            // Send to the 'bid-responses' topic
                            // We use impressionId as the key to group all bids
                            // for the same auction together.
                            kafkaTemplate.send(RESPONSE_TOPIC, request.impressionId(), bidResponse);
                            // ====================================

                            didBid = true;
                            break; 
                        }
                    }
                }

                if (!didBid) {
                    log.info("--- NO BID --- Impression: {} No matching campaign for advertiser: {}",
                            request.impressionId(), request.advertiserId());
                }
            } catch (Exception e) {
                log.error("Error processing message: {}", request.impressionId(), e);
            }
        });
    }

    private boolean matchesTargeting(Campaign campaign, BidRequest request) {
        boolean geoMatch = campaign.getTargetingGeo() == null ||
                        campaign.getTargetingGeo().isEmpty() ||
                        campaign.getTargetingGeo().contains(request.geoId());

        boolean osMatch = campaign.getTargetingOs() == null ||
                        campaign.getTargetingOs().isEmpty() ||
                        campaign.getTargetingOs().contains(request.osId());

        return geoMatch && osMatch;
    }
}