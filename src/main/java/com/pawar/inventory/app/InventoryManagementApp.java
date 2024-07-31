package com.pawar.inventory.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.aspectj.SpringConfiguredConfiguration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;


@SpringBootApplication
@ComponentScan(basePackages = "org.glassfish.jersey.server.spring")
public class InventoryManagementApp extends SpringBootServletInitializer{
	
	
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		 AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
	        context.register(SpringConfiguredConfiguration.class);

	        DispatcherServlet servlet = new DispatcherServlet();
	        servlet.setContextConfigLocation(context.getConfigLocations()+"");

	        ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcher", servlet);
	        registration.setLoadOnStartup(1);
	        registration.addMapping("/InventoryUI");
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(InventoryManagementApp.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(InventoryManagementApp.class, args);
	}

}
