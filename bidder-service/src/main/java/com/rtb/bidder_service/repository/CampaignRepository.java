package com.rtb.bidder_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rtb.bidder_service.model.Campaign;

// This is an interface, not a class.
// We extend JpaRepository and tell it two things:
// 1. We are managing the "Campaign" entity.
// 2. The Primary Key of the Campaign entity is of type "Long".
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    // This is the "magic" of Spring Data JPA.
    // By simply defining a method with this name, Spring will
    // automatically write and execute the SQL query:
    // "SELECT * FROM campaigns WHERE advertiser_id =?"
    List<Campaign> findByAdvertiserId(String advertiserId);
}
