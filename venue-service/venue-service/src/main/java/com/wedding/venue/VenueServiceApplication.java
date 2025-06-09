package com.wedding.venue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VenueServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VenueServiceApplication.class, args);
	}

}
