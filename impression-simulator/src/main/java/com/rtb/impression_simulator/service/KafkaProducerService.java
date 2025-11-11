package com.rtb.impression_simulator.service;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rtb.impression_simulator.dto.BidRequest;

@Service
public class KafkaProducerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    public static final String TOPIC = "bid-requests";

    private final KafkaTemplate<String, BidRequest> kafkaTemplate;
    
    // This is a thread-safe counter, perfect for high-concurrency
    private final AtomicLong requestCounter = new AtomicLong(0);

    public KafkaProducerService(KafkaTemplate<String, BidRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBidRequest(BidRequest request) {
        // This is the asynchronous send.
        kafkaTemplate.send(TOPIC, request.impressionId(), request);

        // Increment the counter and get the new value.
        long count = requestCounter.incrementAndGet();

        // (THE FIX) Log every 1000th request. This is deterministic and reliable.
        if (count % 1000 == 0) {
            log.info("Successfully sent {} total requests. Last ID: {}", count, request.impressionId());
        }
    }
}
