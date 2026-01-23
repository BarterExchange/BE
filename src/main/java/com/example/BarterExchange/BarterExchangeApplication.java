package com.example.BarterExchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BarterExchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarterExchangeApplication.class, args);
	}

}
