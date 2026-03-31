package com.pawar.inventory.app;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication(scanBasePackages = {"com.pawar.inventory.app", "com.pawar.sop.http"})
@EnableScheduling
@OpenAPIDefinition(info = @Info(title = "Inventory Management API", version = "2.0", description = "API for managing inventory"))
public class InventoryManagementApp implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(InventoryManagementApp.class);
	private static final String SEP = "=".repeat(72);

	@Autowired
	private DataSource dataSource;

	@Autowired
	private Environment env;

	@Value("${spring.datasource.url:NOT_CONFIGURED}")
	private String datasourceUrl;

	@Value("${spring.kafka.bootstrap-servers:NOT_CONFIGURED}")
	private String kafkaBootstrapServers;

	@Value("${server.port:8080}")
	private String serverPort;

	@Value("${server.servlet.context-path:/}")
	private String contextPath;

	@Value("${spring.application.name:InventoryManagementApp}")
	private String appName;

	public static void main(String[] args) {
		SpringApplication.run(InventoryManagementApp.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		logStartupStatus();
	}

	// -------------------------------------------------------------------------
	// Startup status banner
	// -------------------------------------------------------------------------

	private void logStartupStatus() {
		log.info(SEP);
		log.info("  APPLICATION STARTUP STATUS");
		log.info(SEP);
		logApplicationInfo();
		logDatabaseStatus();
		logKafkaStatus();
		log.info(SEP);
	}

	// -------------------------------------------------------------------------
	// Application info
	// -------------------------------------------------------------------------

	private void logApplicationInfo() {
		String[] activeProfiles = env.getActiveProfiles();
		String profiles = activeProfiles.length > 0 ? String.join(", ", activeProfiles) : "default";
		log.info("  [APP]    Name     : {}", appName);
		log.info("  [APP]    Port     : {}", serverPort);
		log.info("  [APP]    Context  : {}", contextPath);
		log.info("  [APP]    Profiles : {}", profiles);
		log.info("  [APP]    Base URL : http://localhost:{}{}", serverPort, contextPath);
		log.info("  [APP]    Actuator : http://localhost:{}{}/actuator/health", serverPort, contextPath);
	}

	// -------------------------------------------------------------------------
	// Database connectivity check
	// -------------------------------------------------------------------------

	private void logDatabaseStatus() {
		String safeUrl = sanitizeJdbcUrl(datasourceUrl);
		try (Connection conn = dataSource.getConnection()) {
			String product = conn.getMetaData().getDatabaseProductName();
			String version = conn.getMetaData().getDatabaseProductVersion();
			log.info("  [DB]     Status   : UP");
			log.info("  [DB]     Product  : {} {}", product, version);
			log.info("  [DB]     URL      : {}", safeUrl);
		} catch (SQLException e) {
			log.warn("  [DB]     Status   : DOWN");
			log.warn("  [DB]     URL      : {}", safeUrl);
			log.warn("  [DB]     Cause    : {}", e.getMessage());
		}
	}

	// -------------------------------------------------------------------------
	// Kafka broker reachability check
	// -------------------------------------------------------------------------

	private void logKafkaStatus() {
		if ("NOT_CONFIGURED".equals(kafkaBootstrapServers)) {
			log.info("  [KAFKA]  Status   : NOT_CONFIGURED");
			return;
		}
		log.info("  [KAFKA]  Brokers  : {}", kafkaBootstrapServers);
		boolean anyUp = false;
		for (String broker : kafkaBootstrapServers.split(",")) {
			broker = broker.trim();
			String[] parts = broker.split(":");
			if (parts.length != 2) {
				log.warn("  [KAFKA]  Broker   : {} -> INVALID_FORMAT", broker);
				continue;
			}
			try (Socket socket = new Socket()) {
				socket.connect(new InetSocketAddress(parts[0], Integer.parseInt(parts[1])), 3000);
				log.info("  [KAFKA]  Broker   : {} -> REACHABLE", broker);
				anyUp = true;
			} catch (Exception e) {
				log.warn("  [KAFKA]  Broker   : {} -> UNREACHABLE ({})", broker, e.getMessage());
			}
		}
		log.info("  [KAFKA]  Status   : {}", anyUp ? "UP" : "DOWN");
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private String sanitizeJdbcUrl(String url) {
		if (url == null || url.isBlank()) return "NOT_CONFIGURED";
		return url.replaceAll("(?i)(password|passwd|pwd)=[^&;]*", "$1=***");
	}
}
