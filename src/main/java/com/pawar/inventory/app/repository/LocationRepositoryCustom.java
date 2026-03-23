package com.pawar.inventory.app.repository;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.springframework.http.ResponseEntity;

import com.pawar.inventory.entity.Location;

public interface LocationRepositoryCustom {

	Iterable<Location> getLocations() throws ClientProtocolException, IOException;

	Location getLocationById(int id) throws ClientProtocolException, IOException;

	List<Location> searchLocationsByCode(String code) throws ClientProtocolException, IOException;

	String locationAdd(String locn_brcd, String grp, String locn_class, float length, float width, float height,
			float max_volume, float max_qty, float max_weight) throws ClientProtocolException, IOException;

	ResponseEntity<String> locationEdit(String locn_brcd, String grp, String locn_class, float length, float width,
			float height, float max_volume, float max_qty, float max_weight)
			throws ClientProtocolException, IOException;

	void deleteLocation(String locn_brcd) throws ClientProtocolException, IOException;
}
