package com.bipsqwake.compromise_ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableScheduling
@EnableRetry
@EnableAspectJAutoProxy 
@SpringBootApplication
public class CompromiseWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompromiseWsApplication.class, args);
	}

}
