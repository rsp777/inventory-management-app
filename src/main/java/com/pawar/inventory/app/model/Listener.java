package com.pawar.inventory.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "listeners")
public class Listener {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "listener_name", nullable = false, length = 100)
    private String listenerName;

    @Column(name = "listener_type", nullable = false, length = 50)
    private String listenerType;

    @Column(name = "port_channel", nullable = false, length = 100)
    private String portChannel;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "active";

    @Column(name = "runtime_status", length = 20)
    private String runtimeStatus = "unknown";

    @Column(name = "runtime_message", length = 255)
    private String runtimeMessage;

    @Column(name = "last_connectivity_check")
    private LocalDateTime lastConnectivityCheck;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "configuration", columnDefinition = "TEXT")
    private String configuration;

    @Column(name = "service_key", length = 100)
    private String serviceKey;

    // Constructors
    public Listener() {
    }

    public Listener(String listenerName, String listenerType, String portChannel) {
        this.listenerName = listenerName;
        this.listenerType = listenerType;
        this.portChannel = portChannel;
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

    public String getRuntimeStatus() {
        return runtimeStatus;
    }

    public void setRuntimeStatus(String runtimeStatus) {
        this.runtimeStatus = runtimeStatus;
    }

    public String getRuntimeMessage() {
        return runtimeMessage;
    }

    public void setRuntimeMessage(String runtimeMessage) {
        this.runtimeMessage = runtimeMessage;
    }

    public LocalDateTime getLastConnectivityCheck() {
        return lastConnectivityCheck;
    }

    public void setLastConnectivityCheck(LocalDateTime lastConnectivityCheck) {
        this.lastConnectivityCheck = lastConnectivityCheck;
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

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "active";
        }
        if (runtimeStatus == null) {
            runtimeStatus = "unknown";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public boolean isActive() {
        return "active".equalsIgnoreCase(status);
    }

    public void activate() {
        this.status = "active";
        this.lastActivity = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = "inactive";
    }
}
