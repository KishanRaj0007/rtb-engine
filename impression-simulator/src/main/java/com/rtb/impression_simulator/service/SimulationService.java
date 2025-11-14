package com.rtb.impression_simulator.service;

import com.rtb.impression_simulator.dto.BidRequest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct; // <-- NEW IMPORT
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Random;

@Service
// REMOVED "implements CommandLineRunner"
public class SimulationService {

    private static final Logger log = LoggerFactory.getLogger(SimulationService.class);

    private final ResourceLoader resourceLoader;
    private final KafkaProducerService kafkaProducerService;
    private final Random random = new Random();

    // This holds 500k+ records in memory
    private List<CSVRecord> csvRecords;

    public SimulationService(ResourceLoader resourceLoader, KafkaProducerService kafkaProducerService) {
        this.resourceLoader = resourceLoader;
        this.kafkaProducerService = kafkaProducerService;
    }

    // @PostConstruct tells Spring to run this method
    // ONCE after the service is created.
    @PostConstruct
    public void loadCsvData() throws Exception {
        log.info("Loading Kaggle dataset from classpath...");
        Resource resource = resourceLoader.getResource("classpath:data.csv");
        Reader reader = new InputStreamReader(resource.getInputStream());

        CSVParser csvParser = CSVFormat.DEFAULT.builder()
               .setHeader()
               .setSkipHeaderRecord(true)
               .build()
               .parse(reader);

        this.csvRecords = csvParser.getRecords();
        log.info("Successfully loaded {} records from data.csv", this.csvRecords.size());
    }

    /**
     * This is the new "worker" method.
     * It will be run by virtual threads in parallel--> maybe say 500 set as of now, I will do hyperparameter tuning.
     * Each thread will run this loop indefinitely.
     */
    public void runSimulationLoop() {
        try {
            while (true) {
                // 1. Pick a random row
                CSVRecord randomRecord = csvRecords.get(random.nextInt(csvRecords.size()));
                
                // 2. Create a BidRequest
                BidRequest request = BidRequest.fromCsvRow(randomRecord.values());

                // 3. Send it to Kafka. This is an I/O-bound call.
                // The virtual thread will "park" here, freeing the
                // OS thread for another worker.
                kafkaProducerService.sendBidRequest(request);

                // 4. NO SLEEP. I want 100% throughput.-> faced many problems with sleep.
            }
        } catch (Exception e) {
            // Log if a single worker thread crashes
            log.error("Error in a simulation worker loop", e);
        }
    }
}