package com.pawar.inventory.app.repository;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.pawar.inventory.entity.Inventory;

public interface InventoryRepositoryCustom {

	Iterable<Inventory> getInventories() throws ClientProtocolException, IOException;

	Inventory getInventoryById(int id) throws ClientProtocolException, IOException;

	List<Inventory> getInventoriesByItemId(int itemId) throws ClientProtocolException, IOException;

	List<Inventory> getInventoriesByLocationId(int locationId) throws ClientProtocolException, IOException;

	List<Inventory> getInventoriesByLpnId(int lpnId) throws ClientProtocolException, IOException;

	List<Inventory> searchInventories(Integer itemId, Integer locationId, Integer lpnId)
			throws ClientProtocolException, IOException;
}