package com.pawar.inventory.app.controller;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.validation.BindingResult;

import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.dto.ItemRequestDTO;
import com.pawar.inventory.app.exception.MenuNotFoundException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.service.MenuAccessService;
import com.pawar.inventory.app.service.MenuService;
import com.pawar.inventory.app.util.MenuFilterUtil;
import com.pawar.inventory.app.util.MenuFilterUtil.MenuCategories;
import com.pawar.inventory.app.util.ResponseUtil;
import com.pawar.inventory.app.util.SessionUtil;
import com.pawar.inventory.entity.Category;
import com.pawar.inventory.entity.Item;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Item Management Controller
 * Handles CRUD operations for inventory items
 */
@Controller
@RequestMapping("/api")
public class ItemController {
    
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    
    private final MenuService menuService;
    private final MenuAccessService menuAccessService;

    public ItemController(MenuService menuService, MenuAccessService menuAccessService) {
        this.menuService = menuService;
        this.menuAccessService = menuAccessService;
    }
    
    /**
     * Displays the item management page
     */
    @GetMapping("/itemInfo")
    public String itemInfo(Model model, HttpServletRequest request, HttpSession httpSession) {
        String decodedToken = SessionUtil.getSessionToken(httpSession);
        
        try {
            logger.info("Loading item page");
            
            // Get accessible menus
            List<Menu> menus = menuAccessService.getAccessibleMenus(decodedToken);
            MenuCategories categories = MenuFilterUtil.categorizeMenus(menus);
            
            // Get all items and categories
            Iterable<Item> items = menuService.getItems();
            Iterable<Category> allCategories = menuService.getfindAllCategories();
            
            // Add to model
            ResponseUtil.addBasicViewAttributes(model, categories, request.getRequestURI());
            model.addAttribute("items", items);
            model.addAttribute("categories", allCategories);
            
            logger.info("Item page loaded");
            return AppConstants.View.ITEM;
            
        } catch (IOException | MenuNotFoundException exception) {
            logger.error("Error loading items", exception);
            return AppConstants.View.ITEM;
        }
    }
    
    /**
     * Gets details of a specific item
     */
    @GetMapping("/getItem/{item_name}")
    public ResponseEntity<Item> getItem(@PathVariable String item_name) {
        try {
            logger.info("Fetching item: {}", item_name);
            
            Item item = menuService.getItem(item_name);
            
            logger.info("Item fetched: {}", item);
            return ResponseEntity.ok(item);
            
        } catch (IOException exception) {
            logger.error("Error fetching item: {}", item_name, exception);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Creates a new item
     */
    @PostMapping("/itemAdd")
    public String itemAdd(Model model, @Valid @ModelAttribute ItemRequestDTO requestDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors());
            return AppConstants.View.ITEM;
        }
        
        try {
            logger.info("Creating item: {}", requestDTO.getDescription());
            
            String response = menuService.itemAdd(requestDTO.getDescription(), requestDTO.getCategory(),
                    requestDTO.getLength(), requestDTO.getWidth(), requestDTO.getHeight());
            logger.info("Item created: {}", response);
            
            return AppConstants.Redirect.ITEM_INFO;
            
        } catch (IOException exception) {
            logger.error("Error creating item: {}", requestDTO.getDescription(), exception);
            return AppConstants.View.ITEM;
        }
    }
    
    /**
     * Updates an existing item
     */
    @PutMapping("/itemEdit")
    public String itemEdit(Model model, @Valid @ModelAttribute ItemRequestDTO requestDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors());
            return AppConstants.View.ITEM;
        }
        
        try {
            if (requestDTO.getItemId() == null || requestDTO.getItemId() <= 0) {
                model.addAttribute("error", "Item ID is required for update");
                return AppConstants.View.ITEM;
            }

            logger.info("Updating item: {}", requestDTO.getDescription());
            
            ResponseEntity<String> response = menuService.itemEdit(requestDTO.getItemId(), requestDTO.getDescription(),
                    requestDTO.getCategory(), requestDTO.getLength(), requestDTO.getWidth(), requestDTO.getHeight());
            
            logger.info("Item updated: {}", response.getStatusCode());
            return AppConstants.Redirect.ITEM_INFO;
            
        } catch (IOException exception) {
            logger.error("Error updating item: {}", requestDTO.getItemId(), exception);
            return AppConstants.View.ITEM;
        }
    }
    
    /**
     * Deletes an item
     */
    @DeleteMapping("/deleteItem/{itemId}")
    public String deleteItem(@PathVariable int itemId) {
        try {
            logger.info("Deleting item: {}", itemId);
            
            menuService.deleteItem(itemId);
            
            return AppConstants.Redirect.ITEM_INFO;
            
        } catch (IOException exception) {
            logger.error("Error deleting item: {}", itemId, exception);
            return AppConstants.View.ITEM;
        }
    }
    
    /**
     * Item inquiry page
     */
    @GetMapping("/itemInquiry")
    public String itemInquiry(Model model) {
        logger.info("Loading item inquiry page");
        return "itemInquiry";
    }
    
    /**
     * Retrieves all items
     */
    @GetMapping("/items")
    public ResponseEntity<Iterable<Item>> getAllItems() {
        try {
            logger.info("Fetching all items");
            Iterable<Item> items = menuService.getItems();
            return ResponseEntity.ok(items);
        } catch (IOException exception) {
            logger.error("Error fetching all items", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
