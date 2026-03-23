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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.dto.CategoryRequestDTO;
import com.pawar.inventory.app.exception.MenuNotFoundException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.service.MenuAccessService;
import com.pawar.inventory.app.service.MenuService;
import com.pawar.inventory.app.util.MenuFilterUtil;
import com.pawar.inventory.app.util.MenuFilterUtil.MenuCategories;
import com.pawar.inventory.app.util.ResponseUtil;
import com.pawar.inventory.app.util.SessionUtil;
import com.pawar.inventory.entity.Category;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Category Management Controller
 * Handles CRUD operations for inventory categories
 */
@Controller
@RequestMapping("/api")
public class CategoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    
    private final MenuService menuService;
    private final MenuAccessService menuAccessService;

    public CategoryController(MenuService menuService, MenuAccessService menuAccessService) {
        this.menuService = menuService;
        this.menuAccessService = menuAccessService;
    }
    
    /**
     * Displays the category management page
     */
    @GetMapping("/categoryInfo")
    public String categoryInfo(Model model, HttpServletRequest request, HttpSession httpSession) {
        String decodedToken = SessionUtil.getSessionToken(httpSession);
        
        try {
            logger.info("Loading category page");
            
            // Get accessible menus
            List<Menu> menus = menuAccessService.getAccessibleMenus(decodedToken);
            MenuCategories categories = MenuFilterUtil.categorizeMenus(menus);
            
            // Get all categories
            Iterable<Category> allCategories = menuService.getfindAllCategories();
            
            // Add to model
            ResponseUtil.addBasicViewAttributes(model, categories, request.getRequestURI());
            model.addAttribute("categories", allCategories);
            
            logger.info("Category page loaded");
            return AppConstants.View.CATEGORY;
            
        } catch (IOException | MenuNotFoundException exception) {
            logger.error("Error loading categories", exception);
            return AppConstants.View.CATEGORY;
        }
    }
    
    /**
     * Creates a new category
     */
    @PostMapping("/categoryAdd/{category_name}")
    public String categoryAdd(Model model, @PathVariable String category_name) {
        try {
            logger.info("Creating category: {}", category_name);
            
            String response = menuService.categoryAdd(category_name);
            logger.info("Category created: {}", response);
            
            return AppConstants.Redirect.CATEGORY_INFO;
            
        } catch (IOException exception) {
            logger.error("Error creating category: {}", category_name, exception);
            return AppConstants.View.CATEGORY;
        }
    }
    
    /**
     * Updates an existing category
     */
    @PutMapping(value = "/categoryEdit/{category_name}", 
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> categoryEdit(@PathVariable String category_name, 
            @Valid @RequestBody CategoryRequestDTO requestDTO) {
        
        try {
            logger.info("Updating category: {}", category_name);
            
            menuService.categoryEdit(category_name, requestDTO.getCategoryName());
            
            return ResponseEntity.ok("Category updated successfully");
            
        } catch (Exception exception) {
            logger.error("Error updating category: {}", category_name, exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating category: " + exception.getMessage());
        }
    }
    
    /**
     * Deletes a category by name
     */
    @DeleteMapping("/categoryDeleteByName/{category_name}")
    public String categoryDelete(@PathVariable String category_name) {
        try {
            logger.info("Deleting category: {}", category_name);
            
            menuService.categoryDelete(category_name);
            
            return AppConstants.Redirect.CATEGORY_INFO;
            
        } catch (IOException exception) {
            logger.error("Error deleting category: {}", category_name, exception);
            return AppConstants.View.CATEGORY;
        }
    }
    
    /**
     * Deletes a category by ID
     */
    @DeleteMapping("/categoryDelete/{category_id}")
    public String deleteCategory(@PathVariable int category_id) {
        try {
            logger.info("Deleting category by ID: {}", category_id);
            
            menuService.deleteCategory(category_id);
            
            return AppConstants.Redirect.CATEGORY_INFO;
            
        } catch (IOException exception) {
            logger.error("Error deleting category by ID: {}", category_id, exception);
            return AppConstants.View.CATEGORY;
        }
    }
    
    /**
     * Retrieves all categories
     */
    @GetMapping("/categories")
    public ResponseEntity<Iterable<Category>> getAllCategories() {
        try {
            logger.info("Fetching all categories");
            Iterable<Category> categories = menuService.getfindAllCategories();
            return ResponseEntity.ok(categories);
        } catch (IOException exception) {
            logger.error("Error fetching all categories", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
