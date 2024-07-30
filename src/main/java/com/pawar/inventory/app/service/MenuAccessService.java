package com.pawar.inventory.app.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.management.relation.RoleNotFoundException;

import org.apache.http.client.ClientProtocolException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.pawar.inventory.app.exception.MenuNotFoundException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.model.Role;
import com.pawar.todo.dto.UserDto;

public interface MenuAccessService {

	List<Menu> getAccessibleMenus(String jwtToken) throws JsonMappingException, JsonProcessingException,MenuNotFoundException;

	boolean hasAccess(Menu menu, Set<Role> userRoles);

	String[] decodeToken(String token);

	void assignMenusToRole(int menuId, Integer roleId)throws RoleNotFoundException,MenuNotFoundException,JsonProcessingException;

	void unassignMenusToRole(int menuId, Integer roleId)throws RoleNotFoundException,MenuNotFoundException,JsonProcessingException;

	List<UserDto> getUsers() throws ClientProtocolException, IOException;

}
