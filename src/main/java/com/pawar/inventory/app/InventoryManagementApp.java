package com.pawar.inventory.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.kafka.annotation.EnableKafka;


@SpringBootApplication
public class InventoryManagementApp {
//extends SpringBootServletInitializer{

//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//		return application.sources(InventoryManagementApp.class);
//	}

	public static void main(String[] args) {
		SpringApplication.run(InventoryManagementApp.class, args);
	}

}
