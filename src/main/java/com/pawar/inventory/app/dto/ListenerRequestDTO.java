package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ListenerRequestDTO {
    
    private Long id;
    
    @NotBlank(message = "Listener name is required")
    @Size(max = 100, message = "Listener name must not exceed 100 characters")
    private String listenerName;
    
    @NotBlank(message = "Listener type is required")
    @Size(max = 50, message = "Listener type must not exceed 50 characters")
    private String listenerType;
    
    @NotBlank(message = "Port/Channel is required")
    @Size(max = 100, message = "Port/Channel must not exceed 100 characters")
    private String portChannel;
    
    private String status;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 100, message = "Service key must not exceed 100 characters")
    private String serviceKey;
    
    private String configuration;
    
    // Default constructor
    public ListenerRequestDTO() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getListenerName() {
        return listenerName;
    }
    
    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
    }
    
    public String getListenerType() {
        return listenerType;
    }
    
    public void setListenerType(String listenerType) {
        this.listenerType = listenerType;
    }
    
    public String getPortChannel() {
        return portChannel;
    }
    
    public void setPortChannel(String portChannel) {
        this.portChannel = portChannel;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }
    
    public String getConfiguration() {
        return configuration;
    }
    
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
}
