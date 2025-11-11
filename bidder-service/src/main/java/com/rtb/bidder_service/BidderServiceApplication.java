package com.rtb.bidder_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BidderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BidderServiceApplication.class, args);
	}

}
