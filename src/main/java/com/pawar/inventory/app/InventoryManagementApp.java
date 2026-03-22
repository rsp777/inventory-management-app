package com.pawar.inventory.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication(scanBasePackages = {"com.pawar.inventory.app", "com.pawar.sop.http"})
@OpenAPIDefinition(info = @Info(title = "Inventory Management API", version = "2.0", description = "API for managing inventory"))
public class InventoryManagementApp{
		
	public static void main(String[] args) {
		SpringApplication.run(InventoryManagementApp.class, args);
	}

}
