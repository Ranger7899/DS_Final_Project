package com.wedding.catering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CateringServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CateringServiceApplication.class, args);
	}

}
