package com.pawar.inventory.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.pawar.inventory.app.dto.ListenerDTO;
import com.pawar.inventory.app.dto.ListenerRequestDTO;
import com.pawar.inventory.app.exception.DuplicateResourceException;
import com.pawar.inventory.app.exception.ResourceNotFoundException;
import com.pawar.inventory.app.model.Listener;
import com.pawar.inventory.app.repository.ListenerRepository;

@Service
@Transactional
public class ListenerServiceImpl implements ListenerService {

    private static final Logger logger = LoggerFactory.getLogger(ListenerServiceImpl.class);

    private final ListenerRepository listenerRepository;
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final RestTemplate restTemplate;

    @Value("${spring.kafka.bootstrap-servers:}")
    private String kafkaBootstrapServers;

    @Value("${listener.runtime.monitor.enabled:true}")
    private boolean runtimeMonitorEnabled;

    @Value("${listener.runtime.connect-timeout-ms:2000}")
    private int connectTimeoutMs;

    @Value("${listener.runtime.control-enabled:false}")
    private boolean remoteControlEnabled;

    @Value("${listener.runtime.control-base-url:}")
    private String remoteControlBaseUrl;

    @Value("${listener.runtime.control-base-urls:}")
    private String remoteControlBaseUrls;

    public ListenerServiceImpl(ListenerRepository listenerRepository,
            @Autowired(required = false) KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
        this.listenerRepository = listenerRepository;
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
        this.restTemplate = new RestTemplate();
    }

    @PostConstruct
    public void syncKafkaListenerStateOnStartup() {
        List<Listener> listeners = listenerRepository.findAll();
        listeners.forEach(this::applyLocalListenerState);
    }

    @Override
    public Listener createListener(ListenerRequestDTO requestDTO) {
        logger.info("Creating new listener with name: {}", requestDTO.getListenerName());

        // Check for duplicate name
        if (isListenerNameExists(requestDTO.getListenerName(), null)) {
            throw new DuplicateResourceException("Listener with name '" +
                    requestDTO.getListenerName() + "' already exists");
        }

        Listener listener = convertToEntity(requestDTO);
        listener.setStatus(normalizeStatus(requestDTO.getStatus()));
        listener.setLastActivity(LocalDateTime.now());
        listener.setRuntimeStatus("unknown");
        listener.setRuntimeMessage("Listener created, awaiting connectivity check");
        listener.setLastConnectivityCheck(LocalDateTime.now());

        Listener savedListener = listenerRepository.save(listener);
        applyLocalListenerState(savedListener);
        syncRemoteControl(savedListener, savedListener.isActive());
        logger.info("Listener created successfully with ID: {}", savedListener.getId());

        return savedListener;
    }

    @Override
    public Listener updateListener(Long id, ListenerRequestDTO requestDTO) {
        logger.info("Updating listener with ID: {}", id);

        Listener existingListener = listenerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listener not found with ID: " + id));

        // Check for duplicate name (excluding current listener)
        if (isListenerNameExists(requestDTO.getListenerName(), id)) {
            throw new DuplicateResourceException("Listener with name '" +
                    requestDTO.getListenerName() + "' already exists");
        }

        // Update fields
        existingListener.setListenerName(requestDTO.getListenerName());
        existingListener.setListenerType(requestDTO.getListenerType());
        existingListener.setPortChannel(requestDTO.getPortChannel());
        existingListener.setDescription(requestDTO.getDescription());
        existingListener.setConfiguration(requestDTO.getConfiguration());
        existingListener.setServiceKey(requestDTO.getServiceKey());

        // Update status if provided
        if (requestDTO.getStatus() != null) {
            String normalized = normalizeStatus(requestDTO.getStatus());
            existingListener.setStatus(normalized);
            if ("active".equalsIgnoreCase(normalized)) {
                existingListener.setLastActivity(LocalDateTime.now());
                existingListener.setRuntimeStatus("unknown");
                existingListener.setRuntimeMessage("Activation requested, awaiting connectivity check");
            } else {
                existingListener.setRuntimeStatus("disabled");
                existingListener.setRuntimeMessage("Listener disabled by configuration");
            }
        }

        Listener updatedListener = listenerRepository.save(existingListener);
        applyLocalListenerState(updatedListener);
        syncRemoteControl(updatedListener, updatedListener.isActive());
        logger.info("Listener updated successfully with ID: {}", updatedListener.getId());

        return updatedListener;
    }

    @Override
    public void deleteListener(Long id) {
        logger.info("Deleting listener with ID: {}", id);

        if (!listenerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Listener not found with ID: " + id);
        }

        listenerRepository.findById(id).ifPresent(listener -> {
            listener.setStatus("inactive");
            applyLocalListenerState(listener);
        });

        listenerRepository.deleteById(id);
        logger.info("Listener deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Listener> getListenerById(Long id) {
        logger.debug("Fetching listener with ID: {}", id);
        return listenerRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Listener> getAllListeners() {
        logger.debug("Fetching all listeners");
        return listenerRepository.findAllByOrderByUpdatedAtDesc();
    }

    @Override
    public Listener activateListener(Long id) {
        logger.info("Activating listener with ID: {}", id);

        Listener listener = listenerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listener not found with ID: " + id));

        listener.activate();
        listener.setRuntimeStatus("unknown");
        listener.setRuntimeMessage("Activation requested, awaiting connectivity check");
        listener.setLastConnectivityCheck(LocalDateTime.now());
        Listener activatedListener = listenerRepository.save(listener);
        applyLocalListenerState(activatedListener);
        syncRemoteControl(activatedListener, true);

        logger.info("Listener activated successfully with ID: {}", id);
        return activatedListener;
    }

    @Override
    public Listener deactivateListener(Long id) {
        logger.info("Deactivating listener with ID: {}", id);

        Listener listener = listenerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listener not found with ID: " + id));

        listener.deactivate();
        listener.setRuntimeStatus("disabled");
        listener.setRuntimeMessage("Listener disabled by configuration");
        listener.setLastConnectivityCheck(LocalDateTime.now());
        Listener deactivatedListener = listenerRepository.save(listener);
        applyLocalListenerState(deactivatedListener);
        syncRemoteControl(deactivatedListener, false);

        logger.info("Listener deactivated successfully with ID: {}", id);
        return deactivatedListener;
    }

    @Override
    public Map<String, Object> bulkActivate(List<Long> ids) {
        logger.info("Bulk activating {} listeners", ids.size());
        return processBulkStatusChange(ids, true);
    }

    @Override
    public Map<String, Object> bulkDeactivate(List<Long> ids) {
        logger.info("Bulk deactivating {} listeners", ids.size());
        return processBulkStatusChange(ids, false);
    }

    @Override
    public Listener toggleListenerStatus(Long id) {
        logger.info("Toggling status for listener with ID: {}", id);

        Listener listener = listenerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listener not found with ID: " + id));

        if ("active".equalsIgnoreCase(listener.getStatus())) {
            listener.deactivate();
            listener.setRuntimeStatus("disabled");
            listener.setRuntimeMessage("Listener disabled by configuration");
            syncRemoteControl(listener, false);
            logger.info("Listener {} deactivated", id);
        } else {
            listener.activate();
            listener.setRuntimeStatus("unknown");
            listener.setRuntimeMessage("Activation requested, awaiting connectivity check");
            syncRemoteControl(listener, true);
            logger.info("Listener {} activated", id);
        }
        listener.setLastConnectivityCheck(LocalDateTime.now());

        Listener savedListener = listenerRepository.save(listener);
        applyLocalListenerState(savedListener);
        return savedListener;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Listener> getActiveListeners() {
        logger.debug("Fetching all active listeners");
        return listenerRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Listener> getInactiveListeners() {
        logger.debug("Fetching all inactive listeners");
        return listenerRepository.findAllInactive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Listener> getListenersByType(String type) {
        logger.debug("Fetching listeners by type: {}", type);
        return listenerRepository.findByListenerType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Listener> searchListeners(String searchTerm) {
        logger.debug("Searching listeners with term: {}", searchTerm);
        return listenerRepository.searchListeners(searchTerm);
    }

    @Override
    public ListenerDTO convertToDTO(Listener listener) {
        ListenerDTO dto = new ListenerDTO();
        dto.setId(listener.getId());
        dto.setListenerName(listener.getListenerName());
        dto.setListenerType(listener.getListenerType());
        dto.setPortChannel(listener.getPortChannel());
        dto.setStatus(listener.getStatus());
        dto.setRuntimeStatus(listener.getRuntimeStatus());
        dto.setRuntimeMessage(listener.getRuntimeMessage());
        dto.setLastActivity(listener.getLastActivity());
        dto.setLastConnectivityCheck(listener.getLastConnectivityCheck());
        dto.setCreatedAt(listener.getCreatedAt());
        dto.setUpdatedAt(listener.getUpdatedAt());
        dto.setCreatedBy(listener.getCreatedBy());
        dto.setUpdatedBy(listener.getUpdatedBy());
        dto.setDescription(listener.getDescription());
        dto.setConfiguration(listener.getConfiguration());
        dto.setServiceKey(listener.getServiceKey());
        return dto;
    }

    @Override
    public Listener convertToEntity(ListenerRequestDTO requestDTO) {
        Listener listener = new Listener();
        listener.setListenerName(requestDTO.getListenerName());
        listener.setListenerType(requestDTO.getListenerType());
        listener.setPortChannel(requestDTO.getPortChannel());
        listener.setStatus(normalizeStatus(requestDTO.getStatus()));
        listener.setDescription(requestDTO.getDescription());
        listener.setConfiguration(requestDTO.getConfiguration());
        listener.setServiceKey(requestDTO.getServiceKey());
        listener.setRuntimeStatus("unknown");
        return listener;
    }

    @Override
    @Scheduled(fixedDelayString = "${listener.runtime.monitor.interval-ms:30000}")
    public void refreshRuntimeStatuses() {
        if (!runtimeMonitorEnabled) {
            return;
        }

        List<Listener> listeners = listenerRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Listener listener : listeners) {
            refreshSingleListenerRuntimeStatus(listener, now);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isListenerNameExists(String name, Long excludeId) {
        if (excludeId != null) {
            return listenerRepository.findByListenerNameAndIdNot(name, excludeId).isPresent();
        }
        return listenerRepository.existsByListenerName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveListeners() {
        return listenerRepository.countByStatus("active");
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalListeners() {
        return listenerRepository.count();
    }

    private Map<String, Object> processBulkStatusChange(List<Long> ids, boolean activate) {
        List<Map<String, Object>> results = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        int successCount = 0;
        int failureCount = 0;
        int skippedCount = 0;

        for (Long id : ids) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", id);

            if (id == null || id <= 0) {
                item.put("status", "failed");
                item.put("success", false);
                item.put("message", "Invalid listener ID");
                results.add(item);
                failureCount++;
                continue;
            }

            if (!seen.add(id)) {
                item.put("status", "skipped");
                item.put("success", true);
                item.put("message", "Duplicate ID in request, skipped");
                results.add(item);
                skippedCount++;
                continue;
            }

            try {
                Listener current = listenerRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Listener not found with ID: " + id));
                item.put("listenerName", current.getListenerName());

                boolean alreadyInState = activate ? current.isActive() : !current.isActive();
                if (alreadyInState) {
                    item.put("status", "skipped");
                    item.put("success", true);
                    item.put("message", "Listener already " + (activate ? "active" : "inactive"));
                    results.add(item);
                    skippedCount++;
                    continue;
                }

                Listener updated = activate ? activateListener(id) : deactivateListener(id);
                item.put("status", "success");
                item.put("success", true);
                item.put("message", "Listener " + (activate ? "activated" : "deactivated") + " successfully");
                item.put("listener", convertToDTO(updated));
                results.add(item);
                successCount++;
            } catch (Exception ex) {
                logger.warn("Bulk {} failed for listener ID {}", activate ? "activate" : "deactivate", id, ex);
                item.put("status", "failed");
                item.put("success", false);
                item.put("message", StringUtils.hasText(ex.getMessage()) ? ex.getMessage() : "Operation failed");
                results.add(item);
                failureCount++;
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("action", activate ? "activate" : "deactivate");
        response.put("totalRequested", ids.size());
        response.put("successCount", successCount);
        response.put("failureCount", failureCount);
        response.put("skippedCount", skippedCount);
        response.put("results", results);
        response.put("message", "Bulk " + (activate ? "activation" : "deactivation") + " completed");
        return response;
    }

    private void refreshSingleListenerRuntimeStatus(Listener listener, LocalDateTime now) {
        if (!listener.isActive()) {
            listener.setRuntimeStatus("disabled");
            listener.setRuntimeMessage("Listener disabled by configuration");
            listener.setLastConnectivityCheck(now);
            listenerRepository.save(listener);
            return;
        }

        boolean reachable = isListenerReachable(listener);
        listener.setRuntimeStatus(reachable ? "connected" : "disconnected");
        listener.setRuntimeMessage(reachable ? "Connectivity check succeeded" : "Connectivity check failed");
        listener.setLastConnectivityCheck(now);

        if (reachable) {
            listener.setLastActivity(now);
        }
        listenerRepository.save(listener);
    }

    private boolean isListenerReachable(Listener listener) {
        if ("kafka".equalsIgnoreCase(listener.getListenerType())) {
            return isAnyKafkaBrokerReachable();
        }
        return isHostPortReachable(listener.getPortChannel());
    }

    private boolean isAnyKafkaBrokerReachable() {
        if (!StringUtils.hasText(kafkaBootstrapServers)) {
            return false;
        }
        return Arrays.stream(kafkaBootstrapServers.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .anyMatch(this::isHostPortReachable);
    }

    private boolean isHostPortReachable(String hostPort) {
        if (!StringUtils.hasText(hostPort) || !hostPort.contains(":")) {
            return false;
        }

        String[] parts = hostPort.split(":");
        if (parts.length != 2) {
            return false;
        }

        String host = parts[0].trim();
        int port;
        try {
            port = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException ex) {
            return false;
        }

        try (java.net.Socket socket = new java.net.Socket()) {
            socket.connect(new java.net.InetSocketAddress(host, port), connectTimeoutMs);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private String normalizeStatus(String status) {
        if ("inactive".equalsIgnoreCase(status)) {
            return "inactive";
        }
        return "active";
    }

    private void syncRemoteControl(Listener listener, boolean enable) {
        if (!remoteControlEnabled) {
            return;
        }

        try {
            String targetBaseUrl = resolveRemoteBaseUrl(listener);
            if (!StringUtils.hasText(targetBaseUrl)) {
                logger.warn("Remote control base URL not configured for listener '{}' (serviceKey='{}')",
                        listener.getListenerName(), listener.getServiceKey());
                return;
            }
            String targetIdentifier = resolveRemoteListenerIdentifier(listener);
            String encodedTarget = URLEncoder.encode(targetIdentifier, StandardCharsets.UTF_8);
            String endpoint = targetBaseUrl + "/api/listeners/" + encodedTarget + (enable ? "/enable" : "/disable");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, request, String.class);
            logger.info("Remote control sync for listener '{}' using target '{}' at '{}' returned status {}",
                    listener.getListenerName(), targetIdentifier, targetBaseUrl, response.getStatusCode().value());
        } catch (Exception ex) {
            logger.warn("Remote control sync failed for listener '{}': {}", listener.getListenerName(),
                    ex.getMessage());
        }
    }

    private String resolveRemoteBaseUrl(Listener listener) {
        Map<String, String> serviceUrlMap = parseRemoteControlBaseUrls();
        String key = listener != null ? normalizeServiceKey(listener.getServiceKey()) : "";

        if (StringUtils.hasText(key) && serviceUrlMap.containsKey(key)) {
            return trimTrailingSlash(serviceUrlMap.get(key));
        }

        if (StringUtils.hasText(key) && !serviceUrlMap.isEmpty()) {
            logger.warn("No remote control URL mapping found for serviceKey '{}' on listener '{}'. Available keys: {}",
                    key,
                    listener != null ? listener.getListenerName() : "unknown",
                    serviceUrlMap.keySet());
        }

        if (StringUtils.hasText(remoteControlBaseUrl)) {
            return trimTrailingSlash(remoteControlBaseUrl);
        }

        if (serviceUrlMap.size() == 1) {
            return trimTrailingSlash(serviceUrlMap.values().iterator().next());
        }

        return "";
    }

    private Map<String, String> parseRemoteControlBaseUrls() {
        Map<String, String> mappings = new LinkedHashMap<>();
        if (!StringUtils.hasText(remoteControlBaseUrls)) {
            return mappings;
        }

        String[] entries = remoteControlBaseUrls.split(",");
        int generatedIndex = 1;
        for (String entry : entries) {
            if (!StringUtils.hasText(entry)) {
                continue;
            }

            String trimmedEntry = entry.trim();
            String[] parts = trimmedEntry.split("=", 2);

            if (parts.length == 2) {
                String key = normalizeServiceKey(parts[0]);
                String value = parts[1] != null ? parts[1].trim() : "";
                if (StringUtils.hasText(key) && StringUtils.hasText(value)) {
                    mappings.put(key, trimTrailingSlash(value));
                }
                continue;
            }

            if (trimmedEntry.startsWith("http://") || trimmedEntry.startsWith("https://")) {
                String generatedKey = "service" + generatedIndex++;
                mappings.put(generatedKey, trimTrailingSlash(trimmedEntry));
                logger.info("Mapped remote control URL '{}' to generated service key '{}'", trimmedEntry, generatedKey);
            } else {
                logger.warn("Invalid listener.runtime.control-base-urls entry '{}' - expected key=value or http(s) URL", trimmedEntry);
            }
        }

        return mappings;
    }

    private String normalizeServiceKey(String serviceKey) {
        if (!StringUtils.hasText(serviceKey)) {
            return "";
        }
        return serviceKey.trim().toLowerCase(Locale.ROOT);
    }

    private String trimTrailingSlash(String url) {
        if (!StringUtils.hasText(url)) {
            return "";
        }

        String normalized = url.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private String resolveRemoteListenerIdentifier(Listener listener) {
        if (listener == null) {
            return "";
        }

        if ("kafka".equalsIgnoreCase(listener.getListenerType()) && StringUtils.hasText(listener.getPortChannel())) {
            return listener.getPortChannel();
        }

        return listener.getListenerName();
    }

    private void applyLocalListenerState(Listener listener) {
        if (!"kafka".equalsIgnoreCase(listener.getListenerType())) {
            return;
        }

        if (kafkaListenerEndpointRegistry == null) {
            if (remoteControlEnabled && StringUtils.hasText(resolveRemoteBaseUrl(listener))) {
                logger.info("KafkaListenerEndpointRegistry not available; remote control is enabled, skipping local enforcement for listener '{}' and channel '{}'",
                        listener.getListenerName(), listener.getPortChannel());
            } else {
                logger.warn("KafkaListenerEndpointRegistry not available; skipping local listener state enforcement for listener '{}' and channel '{}'",
                        listener.getListenerName(), listener.getPortChannel());
            }
            return;
        }

        logger.info(
                (listener.isActive() ? "Enabling" : "Disabling")
                        + " local Kafka listener containers for listener '{}' and channel '{}'",
                listener.getListenerName(), listener.getPortChannel());
        List<MessageListenerContainer> matchedContainers = kafkaListenerEndpointRegistry.getListenerContainers()
                .stream()
                .filter(container -> isMatchingKafkaContainer(container, listener))
                .collect(Collectors.toList());

        if (matchedContainers.isEmpty()) {
            if (remoteControlEnabled && StringUtils.hasText(resolveRemoteBaseUrl(listener))) {
                logger.info(
                        "No local Kafka listener container found for listener '{}' and channel '{}'; remote control is enabled, skipping local enforcement",
                        listener.getListenerName(), listener.getPortChannel());
            } else {
                logger.warn("No local Kafka listener container found for listener '{}' and channel '{}",
                        listener.getListenerName(), listener.getPortChannel());
            }
            return;
        }

        boolean shouldRun = listener.isActive();
        for (MessageListenerContainer container : matchedContainers) {
            if (shouldRun && !container.isRunning()) {
                container.start();
                logger.info("Started Kafka listener container '{}' due to listener '{}' activation",
                        container.getListenerId(), listener.getListenerName());
            } else if (!shouldRun && container.isRunning()) {
                container.stop();
                logger.info("Stopped Kafka listener container '{}' due to listener '{}' deactivation",
                        container.getListenerId(), listener.getListenerName());
            }
        }
    }

    private boolean isMatchingKafkaContainer(MessageListenerContainer container, Listener listener) {
        String listenerId = container.getListenerId();
        String listenerName = listener.getListenerName();
        String portChannel = listener.getPortChannel();
        logger.info("Checking Kafka listener container '{}' for match with listener '{}' (name: '{}')",
                listenerId, listener.getId(), listenerName);

        if (StringUtils.hasText(listenerId)) {
            if (StringUtils.hasText(listenerName) && listenerName.equalsIgnoreCase(listenerId)) {
                return true;
            }
            if (StringUtils.hasText(portChannel) && portChannel.equalsIgnoreCase(listenerId)) {
                return true;
            }
        }

        String[] topics = container.getContainerProperties().getTopics();
        if (topics == null || topics.length == 0) {
            return false;
        }

        return Arrays.stream(topics)
                .filter(StringUtils::hasText)
                .anyMatch(topic -> topic.equalsIgnoreCase(listenerName)
                        || (StringUtils.hasText(portChannel) && topic.equalsIgnoreCase(portChannel)));
    }
}
