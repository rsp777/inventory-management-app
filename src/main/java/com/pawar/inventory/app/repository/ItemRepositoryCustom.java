package com.pawar.inventory.app.repository;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.springframework.http.ResponseEntity;

import com.pawar.inventory.entity.Item;

public interface ItemRepositoryCustom {

	Item getItem(String item_name) throws ClientProtocolException, IOException;

	Iterable<Item> getItems() throws ClientProtocolException, IOException;

	String itemAdd(String description, String category, float length, float width, float height)
			throws ClientProtocolException, IOException;

	void deleteItem(int item_id) throws ClientProtocolException, IOException;

	ResponseEntity<String> itemEdit(int itemId, String description, String category, float length, float width,
			float height) throws ClientProtocolException, IOException;
}
