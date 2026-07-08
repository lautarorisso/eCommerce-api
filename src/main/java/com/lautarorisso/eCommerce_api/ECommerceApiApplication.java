package com.lautarorisso.eCommerce_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ECommerceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceApiApplication.class, args);
	}

}
