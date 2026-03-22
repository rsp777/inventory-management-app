package com.pawar.inventory.app.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;


public class CubiscanLog {
  
    @JsonProperty("id")
	private Long id; // Primary key

	
	private CsMeasureData csMeasureData;
	
    @JsonProperty("status")
    private String status;
  
    
    private String payload;
    
    @JsonProperty("topic")
    private String topic;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("created_dttm")
    private LocalDateTime createdDttm;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("lastUpdatedDttm")
    private LocalDateTime lastUpdatedDttm;

	public CubiscanLog() {
	}
	
	public CubiscanLog(Long id, CsMeasureData csMeasureData, String status,String payload,
			String topic,LocalDateTime createdDttm, LocalDateTime lastUpdatedDttm) {
		this.id = id;
		this.csMeasureData = csMeasureData;
		this.status = status;
		this.payload = payload;
		this.topic = topic;
		this.createdDttm = createdDttm;
		this.lastUpdatedDttm = lastUpdatedDttm;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CsMeasureData getCsMeasureData() {
		return csMeasureData;
	}

	public void setCsMeasureData(CsMeasureData csMeasureData) {
		this.csMeasureData = csMeasureData;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
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

	@Override
	public String toString() {
		return "CubiscanLog [id=" + id + ", csMeasureData=" + csMeasureData + ", status=" + status + ", payload="
				+ payload + ", topic=" + topic + ", createdDttm=" + createdDttm + ", lastUpdatedDttm=" + lastUpdatedDttm
				+ "]";
	}
}
