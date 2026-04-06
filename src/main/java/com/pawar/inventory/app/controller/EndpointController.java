package com.pawar.inventory.app.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pawar.inventory.app.dto.EndpointDTO;
import com.pawar.inventory.app.exception.ResourceNotFoundException;
import com.pawar.inventory.app.service.EndpointService;

@RestController
@RequestMapping("/api/endpoints")
@CrossOrigin(origins = "*")
public class EndpointController {

	private static final Logger logger = LoggerFactory.getLogger(EndpointController.class);

	private final EndpointService endpointService;

	public EndpointController(EndpointService endpointService) {
		this.endpointService = endpointService;
	}

	@GetMapping
	public ResponseEntity<List<EndpointDTO>> getAllEndpoints() {
		logger.info("Fetching all endpoints");
		return ResponseEntity.ok(endpointService.getAllEndpoints());
	}

	@GetMapping("/{id}")
	public ResponseEntity<EndpointDTO> getEndpointById(@PathVariable Long id) {
		return endpointService.getEndpointById(id)
				.map(ResponseEntity::ok)
				.orElseThrow(() -> new ResourceNotFoundException("Endpoint not found with ID: " + id));
	}

	@GetMapping("/stats")
	public ResponseEntity<Map<String, Object>> getStatistics() {
		return ResponseEntity.ok(Map.of(
				"totalEndpoints", endpointService.countTotalEndpoints(),
				"activeEndpoints", endpointService.countActiveEndpoints(),
				"inactiveEndpoints", endpointService.countTotalEndpoints() - endpointService.countActiveEndpoints()));
	}
}