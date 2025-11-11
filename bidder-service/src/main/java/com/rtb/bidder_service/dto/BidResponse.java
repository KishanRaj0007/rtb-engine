package com.rtb.bidder_service.dto;

import java.math.BigDecimal;

/**
 * This DTO represents our bid, which we will send
 * to the 'bid-responses' Kafka topic.
 */
public record BidResponse(
    String impressionId,
    String campaignId, // The DB ID of the campaign
    String advertiserId,
    BigDecimal bidPrice
) {}