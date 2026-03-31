package com.pawar.inventory.app.service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.pawar.inventory.app.dto.EndpointDTO;
import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.model.Menu;

@Service
@Transactional
public class EndpointServiceImpl implements EndpointService {

	private static final Logger logger = LoggerFactory.getLogger(EndpointServiceImpl.class);
	private static final Set<String> NAVIGATION_MENU_TYPES = Set.of(
			AppConstants.MenuType.UI,
			AppConstants.MenuType.PARENT_UI,
			AppConstants.MenuType.CHILD_UI,
			AppConstants.MenuType.PARENT,
			AppConstants.MenuType.CHILD);

	private final MenuService menuService;

	public EndpointServiceImpl(MenuService menuService) {
		this.menuService = menuService;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<EndpointDTO> getEndpointById(Long id) {
		return getAllEndpoints().stream()
				.filter(endpoint -> endpoint.getId() != null && endpoint.getId().equals(id))
				.findFirst();
	}

	@Override
	@Transactional(readOnly = true)
	public List<EndpointDTO> getAllEndpoints() {
		List<EndpointDTO> endpoints = menuService.getAllMenus().stream()
				.filter(this::isEndpointMenu)
				.map(this::convertToDTO)
				.sorted(Comparator.comparing(EndpointDTO::getUpdatedAt,
						Comparator.nullsLast(Comparator.reverseOrder())))
				.collect(Collectors.toList());
		logger.info("Resolved {} endpoints from menu table", endpoints.size());
		return endpoints;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EndpointDTO> getActiveEndpoints() {
		return getAllEndpoints().stream()
				.filter(endpoint -> "active".equalsIgnoreCase(endpoint.getStatus()))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public long countActiveEndpoints() {
		return getActiveEndpoints().size();
	}

	@Override
	@Transactional(readOnly = true)
	public long countTotalEndpoints() {
		return getAllEndpoints().size();
	}

	private EndpointDTO convertToDTO(Menu menu) {
		EndpointDTO dto = new EndpointDTO();
		dto.setId((long) menu.getMenu_id());
		dto.setMethod(inferHttpMethod(menu));
		dto.setPath(resolveEndpointPath(menu));
		dto.setStatus("active");
		dto.setDescription(menu.getMenuName());
		dto.setCreatedAt(menu.getCreatedDttm());
		dto.setUpdatedAt(menu.getLastUpdatedDttm());
		dto.setCreatedBy(menu.getCreatedSource());
		dto.setUpdatedBy(menu.getLastUpdatedSource());
		return dto;
	}

	private boolean isEndpointMenu(Menu menu) {
		if (menu == null || !StringUtils.hasText(menu.getMenu_link())) {
			return false;
		}
		String menuType = menu.getMenu_type();
		if (menuType != null && NAVIGATION_MENU_TYPES.contains(menuType)) {
			return false;
		}
		return StringUtils.hasText(menu.getProtocol()) || StringUtils.hasText(menu.getHostname())
				|| menu.getMenu_link().startsWith("/api/");
	}

	private String resolveEndpointPath(Menu menu) {
		String protocol = menu.getProtocol() == null ? "" : menu.getProtocol().trim();
		String hostname = menu.getHostname() == null ? "" : menu.getHostname().trim();
		String menuLink = menu.getMenu_link() == null ? "" : menu.getMenu_link().trim();
		if (StringUtils.hasText(protocol) || StringUtils.hasText(hostname)) {
			return protocol + hostname + menuLink;
		}
		return menuLink;
	}

	private String inferHttpMethod(Menu menu) {
		String menuName = menu.getMenuName() == null ? "" : menu.getMenuName().trim().toLowerCase(Locale.ROOT);
		String menuLink = menu.getMenu_link() == null ? "" : menu.getMenu_link().trim().toLowerCase(Locale.ROOT);
		String combined = menuName + " " + menuLink;
		if (combined.contains("delete") || combined.contains("remove")) {
			return "DELETE";
		}
		if (combined.contains("update") || combined.contains("edit")) {
			return "PUT";
		}
		if (combined.contains("create") || combined.contains("add") || combined.contains("assign")
				|| combined.contains("unassign") || combined.contains("login") || combined.contains("signout")
				|| combined.contains("register")) {
			return "POST";
		}
		return "GET";
	}
}