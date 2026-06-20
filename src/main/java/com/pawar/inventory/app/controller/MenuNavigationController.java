package com.pawar.inventory.app.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pawar.inventory.app.dto.MenuDefinitionRequestDTO;
import com.pawar.inventory.app.exception.MenuNotFoundException;
import com.pawar.inventory.app.exception.ParentMenuNotFoundException;
import com.pawar.inventory.app.exception.ResourceNotFoundException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.service.MenuAccessService;
import com.pawar.inventory.app.service.MenuService;
import com.pawar.inventory.app.service.TransactionLogService;
import com.pawar.inventory.app.util.MenuFilterUtil;
import com.pawar.inventory.app.util.MenuFilterUtil.MenuCategories;
import com.pawar.inventory.app.util.ResponseUtil;
import com.pawar.inventory.app.util.SessionUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * Menu Navigation and Display Controller
 * Handles menu display, creation, and management
 */
@Controller
@RequestMapping("/api")
public class MenuNavigationController {
    
    private static final Logger logger = LoggerFactory.getLogger(MenuNavigationController.class);
    
    private final MenuService menuService;
    private final MenuAccessService menuAccessService;
    private final TransactionLogService transactionLogService;

    public MenuNavigationController(MenuService menuService, MenuAccessService menuAccessService,
            TransactionLogService transactionLogService) {
        this.menuService = menuService;
        this.menuAccessService = menuAccessService;
        this.transactionLogService = transactionLogService;
    }
    
    /**
     * Displays the main menu page with accessible menus for the user
     */
    @GetMapping("/showMenu")
    public String showMenu(Model model, HttpServletRequest request, HttpSession httpSession) {
        String decodedToken = SessionUtil.getSessionToken(httpSession);
        
        try {
            logger.info("Request URI: {}", request.getRequestURI());
            
            // Get menus accessible to the user
            List<Menu> menus = menuAccessService.getAccessibleMenus(decodedToken);
            
            // Categorize menus by type
            MenuCategories categories = MenuFilterUtil.categorizeMenus(menus);
            
            // Add to model
            ResponseUtil.addViewAttributes(model, categories, request.getRequestURI());

            transactionLogService.recordMenuTransaction(
                    "MENU_NAVIGATION_SHOW",
                    "uri=" + request.getRequestURI() + ",rf=" + categories.rightFrameMenus.size() + ",nav="
                            + categories.navigationMenus.size() + ",side=" + categories.sideMenus.size(),
                    SessionUtil.getSessionUserName(httpSession));
            
            return "menu";
            
        } catch (JsonProcessingException | MenuNotFoundException exception) {
            logger.error("Error loading menus", exception);
            return "menu";
        }
    }
    
    /**
     * Creates a new menu item
     */
    @PostMapping("/addMenu")
    public ResponseEntity<?> addMenu(@Valid @ModelAttribute MenuDefinitionRequestDTO requestDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid menu definition");
        }

        String newProtocol = normalizeNullString(requestDTO.getNewProtocol());
        String newMenuName = normalizeNullString(requestDTO.getNewMenuName());
        String newMenuLink = normalizeNullString(requestDTO.getNewMenuLink());
        String newHostname = normalizeNullString(requestDTO.getNewHostname());
        String newMenuType = normalizeNullString(requestDTO.getNewMenuType());
        String newParentMenuName = normalizeNullString(requestDTO.getNewParentMenuName());
        
        try {
            logger.info("Creating menu: {}", newMenuName);
            
            Menu newMenu = menuService.addMenu(newProtocol, newMenuName, newMenuLink, 
                    newHostname, newMenuType, newParentMenuName);

                transactionLogService.recordMenuTransaction(
                    "MENU_CREATE",
                    "menuName=" + newMenuName + ",menuType=" + newMenuType + ",menuLink=" + newMenuLink,
                    null);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(newMenu);
        } catch (ParentMenuNotFoundException exception) {
            throw new ResourceNotFoundException("Parent Menu does not exist : " + newParentMenuName);
        } catch (Exception exception) {
            logger.error("Error creating menu: {}", newMenuName, exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating menu: " + exception.getMessage());
        }
    }
    
    /**
     * Updates an existing menu item
     */
    @PatchMapping("/updateMenu")
    public ResponseEntity<?> updateMenu(@Valid @ModelAttribute MenuDefinitionRequestDTO requestDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid menu definition");
        }

        String newProtocol = normalizeNullString(requestDTO.getNewProtocol());
        String newMenuName = normalizeNullString(requestDTO.getNewMenuName());
        String newMenuLink = normalizeNullString(requestDTO.getNewMenuLink());
        String newHostname = normalizeNullString(requestDTO.getNewHostname());
        String newMenuType = normalizeNullString(requestDTO.getNewMenuType());
        
        try {
            logger.info("Updating menu: {}", newMenuName);
            
            menuService.updateMenu(newProtocol, newMenuName, newMenuLink, 
                    newHostname, newMenuType);

                transactionLogService.recordMenuTransaction(
                    "MENU_UPDATE",
                    "menuName=" + newMenuName + ",menuType=" + newMenuType + ",menuLink=" + newMenuLink,
                    null);
            
            return ResponseEntity.ok("Menu updated successfully");
            
        } catch (Exception exception) {
            logger.error("Error updating menu: {}", newMenuName, exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating menu: " + exception.getMessage());
        }
    }

    private String normalizeNullString(String value) {
        return "null".equals(value) ? null : value;
    }
    
    /**
     * Displays the menu list management page
     */
    @GetMapping("/menulist")
    public String menuList(Model model, HttpServletRequest request, HttpSession httpSession) {
        String decodedToken = SessionUtil.getSessionToken(httpSession);
        
        try {
            logger.info("Loading menu list");
            
            // Get accessible and all menus
            List<Menu> accessibleMenus = menuAccessService.getAccessibleMenus(decodedToken);
            List<Menu> allMenus = menuService.getAllMenus();
            
            // Categorize accessible menus
            MenuCategories categories = MenuFilterUtil.categorizeMenus(accessibleMenus);
            
            // Add to model
            ResponseUtil.addViewAttributes(model, categories, request.getRequestURI());
            model.addAttribute("allMenus", allMenus);

            transactionLogService.recordMenuTransaction(
                    "MENU_NAVIGATION_LIST",
                    "uri=" + request.getRequestURI() + ",accessible=" + accessibleMenus.size() + ",all="
                            + allMenus.size(),
                    SessionUtil.getSessionUserName(httpSession));
            
            logger.info("Menu list loaded - Total: {}", allMenus.size());
            
            return "menulist";
            
        } catch (JsonProcessingException | MenuNotFoundException exception) {
            logger.error("Error loading menu list", exception);
            return "menulist";
        }
    }
    
    /**
     * Retrieves all available menus
     */
    @GetMapping("/allMenus")
    public ResponseEntity<List<Menu>> getAllMenus() {
        try {
            logger.info("Fetching all menus");
            List<Menu> menus = menuService.getAllMenus();
            return ResponseEntity.ok(menus);
        } catch (Exception exception) {
            logger.error("Error fetching all menus", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
