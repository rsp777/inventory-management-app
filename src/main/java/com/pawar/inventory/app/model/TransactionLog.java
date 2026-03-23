package com.pawar.inventory.app.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pawar.inventory.app.config.AppConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
public class TransactionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    @Column(name = "menu_log_id")
    private Long id;

    @JsonInclude(value = Include.CUSTOM)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @JsonProperty("created_dttm")
    @Column(name = "created_dttm")
    private LocalDateTime createdDttm;

    @JsonInclude(value = Include.CUSTOM)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @JsonProperty("last_updated_dttm")
    @Column(name = "last_updated_dttm")
    private LocalDateTime lastUpdatedDttm;

    @JsonInclude(value = Include.CUSTOM)
    @Column(name = "created_source")
    private String createdSource;

    @JsonInclude(value = Include.CUSTOM)
    @Column(name = "last_updated_source")
    private String lastUpdatedSource;

    @Column(name = "data")
    private String data;

    @JsonProperty("transaction_name")
    @Column(name = "transaction_name")
    private String transaction_name;

    public TransactionLog() {
    }

    public TransactionLog(Long id, LocalDateTime createdDttm, LocalDateTime lastUpdatedDttm,
            String createdSource, String lastUpdatedSource, String data, String transaction_name) {
        this.id = id;
        this.createdDttm = createdDttm;
        this.lastUpdatedDttm = lastUpdatedDttm;
        this.createdSource = createdSource;
        this.lastUpdatedSource = lastUpdatedSource;
        this.data = data;
        this.transaction_name = transaction_name;
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public LocalDateTime getCreatedDttm() {
        return createdDttm;
    }

    public void setCreatedDttm(LocalDateTime createdDttm) {
        this.createdDttm = createdDttm;
    }

    public LocalDateTime getLastUpdatedDttm() {
        return lastUpdatedDttm;
    }

    public void setLastUpdatedDttm(LocalDateTime lastUpdatedDttm) {
        this.lastUpdatedDttm = lastUpdatedDttm;
    }

    public String getCreatedSource() {
        return createdSource;
    }

    public void setCreatedSource(String createdSource) {
        this.createdSource = createdSource;
    }

    public String getLastUpdatedSource() {
        return lastUpdatedSource;
    }

    public void setLastUpdatedSource(String lastUpdatedSource) {
        this.lastUpdatedSource = lastUpdatedSource;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTransaction_name() {
        return transaction_name;
    }

    public void setTransaction_name(String transaction_name) {
        this.transaction_name = transaction_name;
    }

    public String getTransactionName() {
        return transaction_name;
    }

    public void setTransactionName(String transactionName) {
        this.transaction_name = transactionName;
    }

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdDttm == null) {
            createdDttm = now;
        }
        if (lastUpdatedDttm == null) {
            lastUpdatedDttm = now;
        }
        if (createdSource == null || createdSource.isBlank()) {
            createdSource = AppConstants.Application.AUDIT_SOURCE_SYSTEM;
        }
        if (lastUpdatedSource == null || lastUpdatedSource.isBlank()) {
            lastUpdatedSource = createdSource;
        }
    }

    @PreUpdate
    public void onUpdate() {
        lastUpdatedDttm = LocalDateTime.now();
        if (lastUpdatedSource == null || lastUpdatedSource.isBlank()) {
            lastUpdatedSource = AppConstants.Application.AUDIT_SOURCE_SYSTEM;
        }
    }

    @Override
    public String toString() {
        return "TransactionLog [id=" + id + ", createdDttm=" + createdDttm + ", lastUpdatedDttm="
                + lastUpdatedDttm + ", createdSource=" + createdSource + ", lastUpdatedSource=" + lastUpdatedSource
                + ", data=" + data + ", transaction_name=" + transaction_name + "]";
    }

}
