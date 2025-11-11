package com.rtb.impression_simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImpressionSimulatorApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ImpressionSimulatorApplication.class);

		// This is the magic line. It tells Spring Boot "Do NOT start a web server."
		// This will save the memory that was causing our OOM error.
		app.setWebApplicationType(WebApplicationType.NONE); 
		app.run(args);
	}

}
