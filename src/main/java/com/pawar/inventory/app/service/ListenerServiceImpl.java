package com.pawar.inventory.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.kafka.common.errors.DuplicateResourceException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawar.inventory.app.dto.ListenerDTO;
import com.pawar.inventory.app.dto.ListenerRequestDTO;
import com.pawar.inventory.app.model.Listener;
import com.pawar.inventory.app.repository.ListenerRepository;

@Service
@Transactional
public class ListenerServiceImpl implements ListenerService {

    private static final Logger logger = LoggerFactory.getLogger(ListenerServiceImpl.class);

    private final ListenerRepository listenerRepository;

    @Autowired
    public ListenerServiceImpl(ListenerRepository listenerRepository) {
        this.listenerRepository = listenerRepository;
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
        listener.setStatus("active");
        listener.setLastActivity(LocalDateTime.now());

        Listener savedListener = listenerRepository.save(listener);
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

        // Update status if provided
        if (requestDTO.getStatus() != null) {
            existingListener.setStatus(requestDTO.getStatus());
            if ("active".equalsIgnoreCase(requestDTO.getStatus())) {
                existingListener.setLastActivity(LocalDateTime.now());
            }
        }

        Listener updatedListener = listenerRepository.save(existingListener);
        logger.info("Listener updated successfully with ID: {}", updatedListener.getId());

        return updatedListener;
    }

    @Override
    public void deleteListener(Long id) {
        logger.info("Deleting listener with ID: {}", id);

        if (!listenerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Listener not found with ID: " + id);
        }

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
        return listenerRepository.findAll();
    }

    @Override
    public Listener activateListener(Long id) {
        logger.info("Activating listener with ID: {}", id);

        Listener listener = listenerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listener not found with ID: " + id));

        listener.activate();
        Listener activatedListener = listenerRepository.save(listener);

        logger.info("Listener activated successfully with ID: {}", id);
        return activatedListener;
    }

    @Override
    public Listener deactivateListener(Long id) {
        logger.info("Deactivating listener with ID: {}", id);

        Listener listener = listenerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listener not found with ID: " + id));

        listener.deactivate();
        Listener deactivatedListener = listenerRepository.save(listener);

        logger.info("Listener deactivated successfully with ID: {}", id);
        return deactivatedListener;
    }

    @Override
    public Listener toggleListenerStatus(Long id) {
        logger.info("Toggling status for listener with ID: {}", id);

        Listener listener = listenerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listener not found with ID: " + id));

        if ("active".equalsIgnoreCase(listener.getStatus())) {
            listener.deactivate();
            logger.info("Listener {} deactivated", id);
        } else {
            listener.activate();
            logger.info("Listener {} activated", id);
        }

        return listenerRepository.save(listener);
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
        dto.setLastActivity(listener.getLastActivity());
        dto.setCreatedAt(listener.getCreatedAt());
        dto.setUpdatedAt(listener.getUpdatedAt());
        dto.setCreatedBy(listener.getCreatedBy());
        dto.setUpdatedBy(listener.getUpdatedBy());
        dto.setDescription(listener.getDescription());
        dto.setConfiguration(listener.getConfiguration());
        return dto;
    }

    @Override
    public Listener convertToEntity(ListenerRequestDTO requestDTO) {
        Listener listener = new Listener();
        listener.setListenerName(requestDTO.getListenerName());
        listener.setListenerType(requestDTO.getListenerType());
        listener.setPortChannel(requestDTO.getPortChannel());
        listener.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : "active");
        listener.setDescription(requestDTO.getDescription());
        listener.setConfiguration(requestDTO.getConfiguration());
        return listener;
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
}
