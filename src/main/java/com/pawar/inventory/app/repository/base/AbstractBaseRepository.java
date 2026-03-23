package com.pawar.inventory.app.repository.base;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Abstract base repository class providing common HTTP client operations
 * and JSON serialization/deserialization utilities.
 * 
 * All custom repositories should extend this class to avoid code duplication
 * for HTTP operations.
 */
public abstract class AbstractBaseRepository {
    
    protected static final Logger logger = LoggerFactory.getLogger(AbstractBaseRepository.class);
    protected final HttpClient httpClient;
    protected final ObjectMapper objectMapper;
    protected final RestTemplate restTemplate;
    
    public AbstractBaseRepository() {
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Performs HTTP GET request and returns JSON response as string
     */
    protected String httpGetAsString(String url) throws ClientProtocolException, IOException {
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }
    
    /**
     * Performs HTTP GET request and deserializes JSON to specified type
     */
    protected <T> T httpGetAsObject(String url, Class<T> typeClass) 
            throws ClientProtocolException, IOException {
        String json = httpGetAsString(url);
        logger.info("GET Response from {}: {}", url, json);
        return objectMapper.readValue(json, typeClass);
    }
    
    /**
     * Performs HTTP GET request and deserializes JSON to List of specified type
     */
    protected <T> T httpGetAsList(String url, TypeReference<T> typeRef) 
            throws ClientProtocolException, IOException {
        String json = httpGetAsString(url);
        logger.info("GET Response from {}: {}", url, json);
        return objectMapper.readValue(json, typeRef);
    }
    
    /**
     * Performs HTTP DELETE request
     */
    protected String httpDelete(String url) throws ClientProtocolException, IOException {
        HttpDelete request = new HttpDelete(url);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }
    
    /**
     * Performs HTTP POST request with JSON payload using RestTemplate
     */
    protected String httpPostForObject(String url, String jsonPayload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        org.springframework.http.HttpEntity<String> httpEntity = 
            new org.springframework.http.HttpEntity<>(jsonPayload, headers);
        
        String response = restTemplate.postForObject(url, httpEntity, String.class);
        logger.info("POST Response from {}: {}", url, response);
        return response;
    }
    
    /**
     * Performs HTTP PUT request with JSON payload using RestTemplate
     */
    protected org.springframework.http.ResponseEntity<String> httpPutForObject(
            String url, String jsonPayload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        org.springframework.http.HttpEntity<String> httpEntity = 
            new org.springframework.http.HttpEntity<>(jsonPayload, headers);
        
        org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
            url, org.springframework.http.HttpMethod.PUT, httpEntity, String.class
        );
        logger.info("PUT Response from {}: {}", url, response.getStatusCode());
        return response;
    }
    
    /**
     * Converts object to JSON string
     */
    protected String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }
    
    /**
     * Converts JSON string to object
     */
    protected <T> T fromJson(String json, Class<T> typeClass) throws Exception {
        return objectMapper.readValue(json, typeClass);
    }
}
