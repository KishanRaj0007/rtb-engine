package com.rtb.bidder_service.dto;

import java.math.BigDecimal;

public record BidResponse(
    String impressionId,
    String campaignId, // The DB ID of the campaign
    String advertiserId,
    BigDecimal bidPrice
) {}