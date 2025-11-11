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
    // Start with 5,000. If your machine is powerful,
    // you can increase this to 10,000 or more.
    private static final int PARALLEL_WORKERS = 500;

    public SimulationRunner(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting simulation with {} parallel virtual thread workers...", PARALLEL_WORKERS);

        // This is the "try-with-resources" pattern for a virtual thread executor [3]
        // It guarantees all 5,000 threads are created and managed.
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            
            for (int i = 0; i < PARALLEL_WORKERS; i++) {
                // Submit 5,000 long-running "worker" tasks.
                // Each task runs on its own new virtual thread.
                executor.submit(() -> simulationService.runSimulationLoop());
            }
            
            // The try-with-resources block will wait here indefinitely
            // as the loops run, keeping the application alive.
        }
    }
}