package com.pawar.inventory.app.util;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.model.Menu;

/**
 * Utility class for common menu operations.
 * Extracts repeated menu filtering logic from controllers.
 */
public class MenuFilterUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(MenuFilterUtil.class);
    
    private MenuFilterUtil() {
        // Prevent instantiation
    }
    
    /**
     * Categorizes menus by type into separate lists.
     * Menu types: RF (Right Frame), UI (User Interface), AUTH (Authentication)
     * 
     * @param menus Source list of menus
     * @return MenuCategories containing categorized menus
     */
    public static MenuCategories categorizeMenus(List<Menu> menus) {
        MenuCategories categories = new MenuCategories();
        
        if (menus == null || menus.isEmpty()) {
            return categories;
        }
        
        for (Menu menu : menus) {
            if (menu == null || menu.getMenu_type() == null) {
                continue;
            }
            
            String menuType = menu.getMenu_type();
            
            if (AppConstants.MenuType.RF.equals(menuType)) {
                categories.rightFrameMenus.add(menu);
                logger.debug("Added menu to RF: {}", menu.getMenuName());
            } else if (AppConstants.MenuType.UI.equals(menuType)) {
                categories.navigationMenus.add(menu);
                logger.debug("Added menu to UI: {}", menu.getMenuName());
            } else if (!AppConstants.MenuType.AUTH.equals(menuType)) {
                categories.sideMenus.add(menu);
                logger.debug("Added menu to SIDE: {}", menu.getMenuName());
            }
        }
        
        logger.info("Categorized {} menus - RF: {}, UI: {}, SIDE: {}",
                   menus.size(),
                   categories.rightFrameMenus.size(),
                   categories.navigationMenus.size(),
                   categories.sideMenus.size());
        
        return categories;
    }
    
    /**
     * Container class for categorized menus
     */
    public static class MenuCategories {
        public List<Menu> rightFrameMenus = new ArrayList<>();      // RF type
        public List<Menu> navigationMenus = new ArrayList<>();      // UI type
        public List<Menu> sideMenus = new ArrayList<>();            // Other (not AUTH)
        
        public boolean isEmpty() {
            return rightFrameMenus.isEmpty() && navigationMenus.isEmpty() && sideMenus.isEmpty();
        }
    }
}
