package com.pawar.inventory.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.pawar.inventory.app", "com.pawar.sop.http"})
public class InventoryManagementApp{
		
	public static void main(String[] args) {
		SpringApplication.run(InventoryManagementApp.class, args);
	}

}
