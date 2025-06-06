package com.bipsqwake.compromise_ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableRetry
@SpringBootApplication
public class CompromiseWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompromiseWsApplication.class, args);
	}

}
