package com.rtb.impression_simulator.dto;

import java.util.UUID;

public record BidRequest(
    String impressionId,
    String siteId,
    String adTypeId,
    String geoId,
    String deviceCategoryId,
    String advertiserId,
    String osId
) {
    public static BidRequest fromCsvRow(String csvRow) {
        // Accept a raw CSV row string, split into fields and validate.
        String[] parts = csvRow.split(",");
        if (parts.length < 9) {
            throw new IllegalArgumentException("CSV row has too few columns: " + csvRow);
        }

        return new BidRequest(
            UUID.randomUUID().toString(),
            parts[1].trim(), // site_id
            parts[2].trim(), // ad_type_id
            parts[3].trim(), // geo_id
            parts[4].trim(), // device_category_id
            parts[5].trim(), // advertiser_id
            parts[8].trim()  // os_id (index 8)
        );
    }

    // Overload that accepts already-split CSV fields (e.g., from CSVRecord.values())
    public static BidRequest fromCsvRow(String[] parts) {
        if (parts == null || parts.length < 9) {
            throw new IllegalArgumentException("CSV parts array has too few elements");
        }

        return new BidRequest(
            UUID.randomUUID().toString(),
            parts[1].trim(), // site_id
            parts[2].trim(), // ad_type_id
            parts[3].trim(), // geo_id
            parts[4].trim(), // device_category_id
            parts[5].trim(), // advertiser_id
            parts[8].trim()  // os_id (index 8)
        );
    }
}
