package com.pawar.inventory.app.repository;

import java.io.IOException;
import java.util.List;

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
			String email, List<String> roles, List<String> permissions) throws ClientProtocolException, IOException;

	String assignRoleToUser(Integer userId, Integer roleId) throws ClientProtocolException, IOException;

	String unassignRoleFromUser(Integer userId, Integer roleId) throws ClientProtocolException, IOException;

	String getUserRoles(Integer userId) throws ClientProtocolException, IOException;

	String deleteUser(Integer userId) throws ClientProtocolException, IOException;
}