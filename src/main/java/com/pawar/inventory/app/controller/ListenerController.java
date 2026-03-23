package com.pawar.inventory.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pawar.inventory.app.dto.ListenerDTO;
import com.pawar.inventory.app.dto.ListenerRequestDTO;
import com.pawar.inventory.app.exception.ResourceNotFoundException;
import com.pawar.inventory.app.model.Listener;
import com.pawar.inventory.app.service.ListenerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/listeners")
@CrossOrigin(origins = "*")
public class ListenerController {

    private static final Logger logger = LoggerFactory.getLogger(ListenerController.class);

    private final ListenerService listenerService;

    public ListenerController(ListenerService listenerService) {
        this.listenerService = listenerService;
    }

    // Get all listeners
    @GetMapping
    public ResponseEntity<List<ListenerDTO>> getAllListeners() {
        logger.info("Fetching all listeners");
        List<Listener> listeners = listenerService.getAllListeners();
        List<ListenerDTO> listenerDTOs = listeners.stream()
                .map(listenerService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listenerDTOs);
    }

    // Get listener by ID
    @GetMapping("/{id}")
    public ResponseEntity<ListenerDTO> getListenerById(@PathVariable Long id) {
        logger.info("Fetching listener with ID: {}", id);

        return listenerService.getListenerById(id)
                .map(listener -> ResponseEntity.ok(listenerService.convertToDTO(listener)))
                .orElseThrow(() -> new ResourceNotFoundException("Listener not found with ID: " + id));
    }

    // Create new listener
    @PostMapping
    public ResponseEntity<ListenerDTO> createListener(@Valid @RequestBody ListenerRequestDTO requestDTO) {
        logger.info("Creating new listener: {}", requestDTO.getListenerName());

        Listener listener = listenerService.createListener(requestDTO);
        ListenerDTO listenerDTO = listenerService.convertToDTO(listener);

        return new ResponseEntity<>(listenerDTO, HttpStatus.CREATED);
    }

    // Update listener
    @PutMapping("/{id}")
    public ResponseEntity<ListenerDTO> updateListener(
            @PathVariable Long id,
            @Valid @RequestBody ListenerRequestDTO requestDTO) {
        logger.info("Updating listener with ID: {}", id);

        requestDTO.setId(id);
        Listener listener = listenerService.updateListener(id, requestDTO);
        ListenerDTO listenerDTO = listenerService.convertToDTO(listener);

        return ResponseEntity.ok(listenerDTO);
    }

    // Delete listener
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteListener(@PathVariable Long id) {
        logger.info("Deleting listener with ID: {}", id);

        listenerService.deleteListener(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Listener deleted successfully");
        response.put("id", String.valueOf(id));

        return ResponseEntity.ok(response);
    }

    // Activate listener
    @PostMapping("/{id}/activate")
    public ResponseEntity<ListenerDTO> activateListener(@PathVariable Long id) {
        logger.info("Activating listener with ID: {}", id);

        Listener listener = listenerService.activateListener(id);
        ListenerDTO listenerDTO = listenerService.convertToDTO(listener);

        return ResponseEntity.ok(listenerDTO);
    }

    // Deactivate listener
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ListenerDTO> deactivateListener(@PathVariable Long id) {
        logger.info("Deactivating listener with ID: {}", id);

        Listener listener = listenerService.deactivateListener(id);
        ListenerDTO listenerDTO = listenerService.convertToDTO(listener);

        return ResponseEntity.ok(listenerDTO);
    }

    // Toggle listener status
    @PostMapping("/{id}/toggle-status")
    public ResponseEntity<ListenerDTO> toggleListenerStatus(@PathVariable Long id) {
        logger.info("Toggling status for listener with ID: {}", id);

        Listener listener = listenerService.toggleListenerStatus(id);
        ListenerDTO listenerDTO = listenerService.convertToDTO(listener);

        return ResponseEntity.ok(listenerDTO);
    }

    // Get active listeners
    @GetMapping("/status/active")
    public ResponseEntity<List<ListenerDTO>> getActiveListeners() {
        logger.info("Fetching all active listeners");

        List<Listener> listeners = listenerService.getActiveListeners();
        List<ListenerDTO> listenerDTOs = listeners.stream()
                .map(listenerService::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listenerDTOs);
    }

    // Get inactive listeners
    @GetMapping("/status/inactive")
    public ResponseEntity<List<ListenerDTO>> getInactiveListeners() {
        logger.info("Fetching all inactive listeners");

        List<Listener> listeners = listenerService.getInactiveListeners();
        List<ListenerDTO> listenerDTOs = listeners.stream()
                .map(listenerService::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listenerDTOs);
    }

    // Get listeners by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ListenerDTO>> getListenersByType(@PathVariable String type) {
        logger.info("Fetching listeners by type: {}", type);

        List<Listener> listeners = listenerService.getListenersByType(type);
        List<ListenerDTO> listenerDTOs = listeners.stream()
                .map(listenerService::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listenerDTOs);
    }

    // Search listeners
    @GetMapping("/search")
    public ResponseEntity<List<ListenerDTO>> searchListeners(@RequestParam String query) {
        logger.info("Searching listeners with query: {}", query);

        List<Listener> listeners = listenerService.searchListeners(query);
        List<ListenerDTO> listenerDTOs = listeners.stream()
                .map(listenerService::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listenerDTOs);
    }

    // Get statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        logger.info("Fetching listener statistics");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalListeners", listenerService.countTotalListeners());
        stats.put("activeListeners", listenerService.countActiveListeners());
        stats.put("inactiveListeners", listenerService.countTotalListeners() - listenerService.countActiveListeners());

        return ResponseEntity.ok(stats);
    }
}
