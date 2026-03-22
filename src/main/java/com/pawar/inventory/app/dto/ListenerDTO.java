package com.pawar.inventory.app.dto;

import java.time.LocalDateTime;

public class ListenerDTO {
    
    private Long id;
    private String listenerName;
    private String listenerType;
    private String portChannel;
    private String status;
    private LocalDateTime lastActivity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String description;
    private String configuration;
    
    // Default constructor
    public ListenerDTO() {
    }
    
    // Constructor with all fields
    public ListenerDTO(Long id, String listenerName, String listenerType, 
                      String portChannel, String status, LocalDateTime lastActivity,
                      LocalDateTime createdAt, LocalDateTime updatedAt,
                      String createdBy, String updatedBy, String description,
                      String configuration) {
        this.id = id;
        this.listenerName = listenerName;
        this.listenerType = listenerType;
        this.portChannel = portChannel;
        this.status = status;
        this.lastActivity = lastActivity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.description = description;
        this.configuration = configuration;
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
    
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getConfiguration() {
        return configuration;
    }
    
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
}
