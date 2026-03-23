package com.pawar.inventory.app.repository;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.pawar.inventory.entity.Category;

public interface CategoryRepositoryCustom {

	Iterable<Category> getfindAllCategories() throws ClientProtocolException, IOException;

	String categoryAdd(String category_name) throws ClientProtocolException, IOException;

	void categoryEdit(String category_name, String updatedCategoryName) throws ClientProtocolException, IOException;

	void categoryDelete(String category_name) throws ClientProtocolException, IOException;

	void deleteCategory(int category_id) throws ClientProtocolException, IOException;
}
