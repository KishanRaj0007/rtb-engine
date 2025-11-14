package com.rtb.impression_simulator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

@Component
public class SimulationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SimulationRunner.class);
    private final SimulationService simulationService;

    // This is how many parallel workers we will create.
    private static final int PARALLEL_WORKERS = 500;

    public SimulationRunner(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting simulation with {} parallel virtual thread workers...", PARALLEL_WORKERS);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            
            for (int i = 0; i < PARALLEL_WORKERS; i++) {
                executor.submit(() -> simulationService.runSimulationLoop());
            }
        }
    }
}