package com.pawar.inventory.app.service;

import java.util.List;
import java.util.Optional;

import com.pawar.inventory.app.dto.EndpointDTO;

public interface EndpointService {

	Optional<EndpointDTO> getEndpointById(Long id);

	List<EndpointDTO> getAllEndpoints();

	List<EndpointDTO> getActiveEndpoints();

	long countActiveEndpoints();

	long countTotalEndpoints();
}