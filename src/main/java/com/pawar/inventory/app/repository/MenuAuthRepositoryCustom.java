package com.pawar.inventory.app.repository;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.pawar.inventory.app.model.Menu;

import jakarta.servlet.http.HttpSession;

public interface MenuAuthRepositoryCustom {

	Menu addMenu(Menu newMenu);

	Menu updateMenu(Menu updatedMenu);

	String signIn(String username, String password) throws ClientProtocolException, IOException;

	String signout(HttpSession httpSession) throws ClientProtocolException, IOException;

	Menu getMenu(String menuName);

	String getUrl(String menuName);

	String userAdd(String firstname, String middlename, String lastname, String username, String password,
			String email);
}