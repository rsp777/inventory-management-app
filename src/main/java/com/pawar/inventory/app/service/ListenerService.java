package com.pawar.inventory.app.service;

import com.pawar.inventory.app.dto.ListenerDTO;
import com.pawar.inventory.app.dto.ListenerRequestDTO;
import com.pawar.inventory.app.model.Listener;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ListenerService {

    // CRUD Operations
    Listener createListener(ListenerRequestDTO requestDTO);

    Listener updateListener(Long id, ListenerRequestDTO requestDTO);

    void deleteListener(Long id);

    Optional<Listener> getListenerById(Long id);

    List<Listener> getAllListeners();

    // Status Operations
    Listener activateListener(Long id);

    Listener deactivateListener(Long id);

    Listener toggleListenerStatus(Long id);

    Map<String, Object> bulkActivate(List<Long> ids);

    Map<String, Object> bulkDeactivate(List<Long> ids);

    // Query Operations
    List<Listener> getActiveListeners();

    List<Listener> getInactiveListeners();

    List<Listener> getListenersByType(String type);

    List<Listener> searchListeners(String searchTerm);

    void refreshRuntimeStatuses();

    // Utility Methods
    ListenerDTO convertToDTO(Listener listener);

    Listener convertToEntity(ListenerRequestDTO requestDTO);

    boolean isListenerNameExists(String name, Long excludeId);

    long countActiveListeners();

    long countTotalListeners();
}
