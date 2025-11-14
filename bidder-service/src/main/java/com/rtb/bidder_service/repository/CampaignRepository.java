package com.rtb.bidder_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rtb.bidder_service.model.Campaign;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByAdvertiserId(String advertiserId);
}
