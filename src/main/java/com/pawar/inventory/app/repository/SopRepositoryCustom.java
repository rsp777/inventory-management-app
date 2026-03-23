package com.pawar.inventory.app.repository;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.pawar.inventory.app.model.CubiscanLog;
import com.pawar.inventory.entity.Category;
import com.pawar.inventory.entity.Grp;
import com.pawar.inventory.entity.SopActionTypeDto;
import com.pawar.inventory.entity.SopLocationRangeDto;

public interface SopRepositoryCustom {

	Iterable<Grp> getGrps() throws JsonMappingException, JsonProcessingException, ClientProtocolException, IOException;

	List<SopLocationRangeDto> getLocationRanges() throws ClientProtocolException, IOException;

	List<SopActionTypeDto> getSopActionTypes() throws ClientProtocolException, IOException;

	List<Category> getCategories() throws ClientProtocolException, IOException;

	String assignBatch(String actionType, String batchAssign, String category)
			throws ClientProtocolException, IOException;

	String addLocationRange(String actionType, String category_name, String fromLocation, String toLocation,
			String isActive, String username) throws ClientProtocolException, IOException;

	List<String> getEligibleUpcsForSop(String category) throws ClientProtocolException, IOException;

	String updateLocationRange(String id, String actionType, String category_name, String fromLocation,
			String toLocation, String isActive, String username) throws ClientProtocolException, IOException;

	void unassignBatch(String sopActionType, String batchAssign, String category_name)
			throws ClientProtocolException, IOException;

	List<CubiscanLog> getCubiscanLogs();
}