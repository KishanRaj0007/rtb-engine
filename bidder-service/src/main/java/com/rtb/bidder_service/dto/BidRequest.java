package com.rtb.bidder_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BidRequest(
    String impressionId,
    String siteId,
    String adTypeId,
    String geoId,
    String deviceCategoryId,
    String advertiserId,
    String osId
) {
}
