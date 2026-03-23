package com.pawar.inventory.app.service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.util.SessionUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class NavigationService {

    private static final Logger logger = LoggerFactory.getLogger(NavigationService.class);

    private final MenuAccessService menuAccessService;

    public NavigationService(MenuAccessService menuAccessService) {
        this.menuAccessService = menuAccessService;
    }

    public void populateNavigation(Model model, HttpServletRequest request, HttpSession session) {
        String decodedToken = SessionUtil.getSessionToken(session);
        if (decodedToken == null)
            return;

        try {
            List<Menu> menus = menuAccessService.getAccessibleMenus(decodedToken);
            List<Menu> rf = new ArrayList<>();
            List<Menu> nav = new ArrayList<>();

            for (Menu menu : menus) {

                // Null-safe check to avoid potential crashes
                String type = menu.getMenu_type();

                // 1. GLOBAL NULL-SAFETY:
                // This is the "Magic Fix". No matter the type, if the link is null,
                // we make it an empty string so Thymeleaf won't crash.
                if (menu.getMenu_link() == null) {
                    menu.setMenu_link("");
                }

                if (AppConstants.MenuType.RF.equals(type)) {
                    rf.add(menu);
                } else if (AppConstants.MenuType.UI.equals(type)
                        || AppConstants.MenuType.PARENT_UI.equals(type)
                        || AppConstants.MenuType.CHILD_UI.equals(type)) {
                    nav.add(menu);
                }
            }
            logger.info("navigation menus :  {}", nav);
            model.addAttribute("menus", rf);
            model.addAttribute("nav_menus", nav);
            model.addAttribute("currentMenu", request.getRequestURI());

        } catch (Exception e) {
            logger.error("Error building navigation", e);
        }
    }
}
