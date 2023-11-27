package com.pawar.inventory.app.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.service.MenuService;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.service.CategoryService;

@Controller
@RequestMapping("/api")
//@EnableJpaRepositories
@EnableJdbcRepositories
public class MenuController {
	private final static Logger logger = Logger.getLogger(MenuController.class.getName());

	@Autowired
	private MenuService menuService;
	
	
	private CategoryService categoryService;
	
	@GetMapping("/index")
    public String index(Model model) {
       
        return "menu";
    }
	
	@GetMapping("/showMenu")
    public String showMenu(Model model) {
        List<Menu> menus = menuService.getAllMenus();
        logger.info(""+menus);
        logger.info(""+model.addAttribute("menus", menus));
        return "menu";
    } 
	
	@GetMapping("/createLpn")
    public String createLpn(Model model) {
		logger.info("Create Lpn");
        return "createLpn";
    }
	
	@PostMapping("/newLpn")
    public String newLpn(@RequestParam String lpn_name,@RequestParam String item_name,@RequestParam int quantity,
    		Model model) {
		logger.info("New Lpn data : "+lpn_name+", "+item_name+", "+quantity);
		 try {
			menuService.newLpn(lpn_name,item_name,quantity);
			model.addAttribute("message","Lpn Created Successfully");
			 return "createLpn";
		} catch (IOException e) {
			logger.log(Level.SEVERE, "IOException occurred: ", e);
//			e.printStackTrace();
			return "createLpn";
		}
       
    }
	
	
	@GetMapping("/lpnInquiry")
    public String lpnInquiry(Model model) {
        List<Menu> menus = menuService.getAllMenus();
        logger.info(""+menus);
        logger.info(""+model.addAttribute("menus", menus));
        return "menu";
    }
	
	@GetMapping("/inventoryByLocation")
    public String inventoryByLocation(Model model) {
        List<Menu> menus = menuService.getAllMenus();
        logger.info(""+menus);
        logger.info(""+model.addAttribute("menus", menus));
        return "menu";
    }
	
	@GetMapping("/inventoryByLpn")
    public String inventoryByLpn(Model model) {
        List<Menu> menus = menuService.getAllMenus();
        logger.info(""+menus);
        logger.info(""+model.addAttribute("menus", menus));
        return "menu";
    }
	
	@GetMapping("/inventoryByItem")
    public String inventoryByItem(Model model) {
        List<Menu> menus = menuService.getAllMenus();
        logger.info(""+menus);
        logger.info(""+model.addAttribute("menus", menus));
        return "menu";
    }
	
	@GetMapping("/itemInquiry")
    public String itemInquiry(Model model) {
		logger.info("Item Inquiry");
        return "itemInquiry";
    }
	
	@GetMapping("/itemInfo")
	public String itemInfo(Model model) {
		logger.info("Item");
	    Iterable<Item> items;
		try {
			items = menuService.getItems();
			logger.info("Fetched Items : "+items);
			model.addAttribute("items", items);
//		    model.addAttribute("newCategory", new Category());
		    return "item";
		} catch (IOException e) {

			e.printStackTrace();
			return "item";
		}
	    
	}
	
	@GetMapping("/getItem/{item_name}")
	public String getItem(@PathVariable String item_name) {
	    logger.info("Item Inquiry : "+item_name);
	    Item item;
		try {
			item = menuService.getItem(item_name);
			logger.info("Fetched Item : "+item);
		    return "itemInquiry";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "itemInquiry";
		}
	    
	}

	@GetMapping("/categoryInfo")
	public String categoryInfo(Model model) {
		logger.info("Category");
	    Iterable<Category> categories;
		try {
			categories = menuService.getfindAllCategories();
			logger.info("Fetched Categories : "+categories);
			model.addAttribute("categories", categories);
//		    model.addAttribute("newCategory", new Category());
		    return "category";
		} catch (IOException e) {

			e.printStackTrace();
			return "category";
		}
	    
	}
	
	@PostMapping("/categoryAdd/{category_name}")
	public String categoryAdd(@PathVariable String category_name) {
		logger.info("New Category to Add : "+category_name);
		try {
			String responseMessage = menuService.categoryAdd(category_name);
			logger.info("Response Message : "+responseMessage);
			logger.info("responseMessage.equals(\"Category already exists\") : "+responseMessage.equals("Category already exists"));
			if (responseMessage.equals("Category already exists")) {
				return "category"; 
			} 
			else {
				logger.info("Category added : "+category_name);
			    return "category";	
			}
			
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "category";
		}
	    
	}


	@PutMapping(value = "/categoryEdit/{category_name}", consumes = "application/json", produces = "application/json")
	public String categoryEdit(@PathVariable String category_name,@RequestBody Category category) {
		logger.info("Edit Existing Category to : "+category_name);
		logger.info("Category : "+category);

		menuService.categoryEdit(category_name,category);
		logger.info("Category updated : "+category_name);
		return "category";
	}
	
	@DeleteMapping("/categoryDelete/{category_name}")
	public String categoryDelete(@PathVariable String category_name) {
		logger.info("Delete Existing Category : "+category_name);
		try {
			menuService.categoryDelete(category_name);
			return "category";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "category";
		}
		
	}
	
	@DeleteMapping(value = "/deleteCategory/{category_id}")
	public String deleteCategory(@PathVariable int category_id) {
		logger.info("Delete Existing Category ID : "+category_id);
		try {
			menuService.deleteCategory(category_id);
			return "category";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "category";
		}
		
	}
	
	
	@GetMapping("/locationInquiry")
    public String locationInquiry(Model model) {
        List<Menu> menus = menuService.getAllMenus();
        logger.info(""+menus);
        logger.info(""+model.addAttribute("menus", menus));
        return "menu";
    }
	
	@GetMapping("/putawayLpnToReserve")
    public String putawayLpnToReserve(Model model) {
        List<Menu> menus = menuService.getAllMenus();
        logger.info(""+menus);
        logger.info(""+model.addAttribute("menus", menus));
        return "menu";
    }
	
	@GetMapping("/putawayLpnToActive")
    public String putawayLpnToActive(Model model) {
        List<Menu> menus = menuService.getAllMenus();
        logger.info(""+menus);
        logger.info(""+model.addAttribute("menus", menus));
        return "menu";
    }
	
}
