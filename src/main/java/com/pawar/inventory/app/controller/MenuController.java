package com.pawar.inventory.app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.context.LazyContextVariable;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawar.inventory.app.exception.MenuNotFoundException;
import com.pawar.inventory.app.exception.ParentMenuNotFoundException;
import com.pawar.inventory.app.exception.ResourceNotFoundException;
import com.pawar.inventory.app.exception.UnauthorizedException;
import com.pawar.inventory.app.model.CubiscanLog;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.model.ResponseMessage;
import com.pawar.inventory.app.service.MenuAccessService;
import com.pawar.inventory.app.service.MenuService;
import com.pawar.inventory.app.service.NavigationService;
import com.pawar.inventory.entity.Category;
import com.pawar.inventory.entity.Grp;
import com.pawar.inventory.entity.Inventory;
import com.pawar.inventory.entity.Item;
import com.pawar.inventory.entity.Location;
import com.pawar.inventory.entity.Lpn;
import com.pawar.inventory.entity.SopActionTypeDto;
import com.pawar.inventory.entity.SopLocationRangeDto;
import com.pawar.todo.dto.UserDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api")

@Tag(name = "Menu API", description = "API for managing menus")
public class MenuController {
	private final static Logger logger = LoggerFactory.getLogger(MenuController.class);

	// Make these final so they MUST be initialized by the constructor
    private final MenuService menuService;
    private final MenuAccessService menuAccessService;
    private final NavigationService navigationService;

	// Single constructor for all dependencies (No @Autowired needed on individual fields)
    public MenuController(MenuService menuService, 
                          MenuAccessService menuAccessService, 
                          NavigationService navigationService) {
        this.menuService = menuService;
        this.menuAccessService = menuAccessService;
        this.navigationService = navigationService;
    }

	/**
     * This method runs automatically before every request in this controller.
     * It ensures the navigation menus are always present in the model.
     */
    @ModelAttribute
    public void handleNavigation(Model model, HttpServletRequest request, HttpSession httpSession) {
        navigationService.populateNavigation(model, request, httpSession);
    }

	@GetMapping("/index")
	public String index(Model model) {

		return "index";
	}

	@Operation(summary = "Add a new menu", description = "Add a new menu to the system")
	@ApiResponse(responseCode = "200", description = "Menu added successfully")
	@ApiResponse(responseCode = "404", description = "Parent menu not found")
	@PostMapping("/addMenu")
	@ResponseBody
	public Menu addMenu(@RequestParam String newProtocol, @RequestParam String newMenuName,
			@RequestParam String newMenuLink, @RequestParam String newHostname, @RequestParam String newMenuType,
			@RequestParam String newParentMenuName) {

		logger.info("New Protocol : " + newProtocol);
		logger.info("New Menu : " + newMenuName);
		logger.info("New Menu Link : " + newMenuLink);
		logger.info("New Hostname  : " + newHostname);
		logger.info("New Menu Type : " + newMenuType);
		logger.info("Parent Menu : " + newParentMenuName);

		// Convert "null" String into null value
		newProtocol = "null".equals(newProtocol) ? null : newProtocol;
		newMenuName = "null".equals(newMenuName) ? null : newMenuName;
		newMenuLink = "null".equals(newMenuLink) ? null : newMenuLink;
		newHostname = "null".equals(newHostname) ? null : newHostname;
		newMenuType = "null".equals(newMenuType) ? null : newMenuType;
		newParentMenuName = "null".equals(newParentMenuName) ? null : newParentMenuName;

		try {
			Menu newMenu = menuService.addMenu(newProtocol, newMenuName, newMenuLink, newHostname, newMenuType,
					newParentMenuName);
			logger.info("New Menu is now created : " + newMenu);
			return newMenu;
		} catch (ParentMenuNotFoundException e) {
			throw new ResourceNotFoundException("Parent Menu does not exist : " + newParentMenuName);
		} catch (Exception e) {
			throw new RuntimeException("Exception while adding new menu : " + newMenuName);
		}
	}

	@Operation(summary = "Update a menu", description = "Update an existing menu in the system")
	@ApiResponse(responseCode = "200", description = "Menu updated successfully")
	@PatchMapping("/updateMenu")
	@ResponseBody
	public Menu updateMenu(@RequestParam String newProtocol, @RequestParam String newMenuName,
			@RequestParam String newMenuLink, @RequestParam String newHostname, @RequestParam String newMenuType) {
		logger.info("Update Protocol : " + newProtocol);
		logger.info("Update Menu : " + newMenuName);
		logger.info("Update Menu Link : " + newMenuLink);
		logger.info("Update Hostname  : " + newHostname);
		logger.info("Update Menu Type : " + newMenuType);

		try {
			Menu updatedMenu = menuService.updateMenu(newProtocol, newMenuName, newMenuLink, newHostname, newMenuType);
			logger.info("New Menu is now update : " + newMenuName);
			return updatedMenu;
		} catch (Exception e) {
			throw new RuntimeException("Exception while updating menu : " + newMenuName);
		}
	}

	@GetMapping("/showMenu")
	public String showMenu(Model model, HttpServletRequest request, HttpSession httpSession) {
		logger.info("showMenu");
		try {
			return "menu";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/api/index";
		}

	}

	@GetMapping("/createLpn")
	public String createLpn(Model model) {
		logger.info("Create Lpn");
		return "createLpn";
	}

	@PostMapping("/newLpn")
	public String newLpn(@RequestParam String lpn_name, @RequestParam String item_name, @RequestParam int quantity,
			Model model) {
		logger.info("New Lpn data : " + lpn_name + ", " + item_name + ", " + quantity);
		try {
			menuService.newLpn(lpn_name, item_name, quantity);
			model.addAttribute("message", "Lpn Created Successfully");
			return "createLpn";
		} catch (IOException e) {
			logger.error("IOException occurred: ", e);
			// e.printStackTrace();
			return "createLpn";
		}

	}

	@GetMapping("/lpnInquiry")
	public String lpnInquiry(Model model) {
		List<Menu> menus = menuService.getAllMenus();
		logger.info("" + menus);
		logger.info("" + model.addAttribute("menus", menus));
		return "menu";
	}

	@GetMapping("/inventoryByLocation")
	public String inventoryByLocation(Model model) {
		List<Menu> menus = menuService.getAllMenus();
		logger.info("" + menus);
		logger.info("" + model.addAttribute("menus", menus));
		return "menu";
	}

	@GetMapping("/inventoryByLpn")
	public String inventoryByLpn(Model model) {
		List<Menu> menus = menuService.getAllMenus();
		logger.info("" + menus);
		logger.info("" + model.addAttribute("menus", menus));
		return "menu";
	}

	@GetMapping("/inventoryByItem")
	public String inventoryByItem(Model model) {
		List<Menu> menus = menuService.getAllMenus();
		logger.info("" + menus);
		logger.info("" + model.addAttribute("menus", menus));
		return "menu";
	}

	@GetMapping("/itemInquiry")
	public String itemInquiry(Model model) {
		logger.info("Item Inquiry");
		return "itemInquiry";
	}

	@GetMapping("/itemInfo")
	public String itemInfo(Model model, HttpServletRequest request, HttpSession httpSession) {
		logger.info("Item");
		try {
			Iterable<Item> items;
			Iterable<Category> categories = menuService.getfindAllCategories();
			items = menuService.getItems();
			logger.info("Fetched Items : " + items);
			model.addAttribute("categories", categories);
			model.addAttribute("items", items);
			return "item";
		} catch (IOException e) {
			e.printStackTrace();
			return "item";
		}
	}

	@GetMapping("/getItem/{item_name}")
	public String getItem(@PathVariable String item_name) {
		logger.info("Item Inquiry : " + item_name);
		Item item;
		try {
			item = menuService.getItem(item_name);
			logger.info("Fetched Item : " + item);
			return "itemInquiry";
		} catch (IOException e) {
			e.printStackTrace();
			return "itemInquiry";
		}
	}

	@GetMapping("/categoryInfo")
	public String categoryInfo(Model model, HttpServletRequest request, HttpSession httpSession) {
		logger.info("Category");
		try {
			Iterable<Category> categories;
			categories = menuService.getfindAllCategories();
			logger.info("Fetched Categories : ");
			model.addAttribute("categories", categories);
			return "category";
		} catch (IOException e) {
			e.printStackTrace();
			return "category";
		}
	}

	@PostMapping("/categoryAdd/{category_name}")
	public String categoryAdd(Model model, @PathVariable String category_name) {
		logger.info("New Category to Add : " + category_name);
		ResponseMessage responseMessage = new ResponseMessage();
		try {
			String response = menuService.categoryAdd(category_name);
			responseMessage.setResponseMessage(response);
			model.addAttribute("responseMessage", new LazyContextVariable<String>() {

				protected String loadValue() {
					return responseMessage.getResponseMessage();
				}
			});

			logger.info("Response Message : " + responseMessage.getResponseMessage());

			return "category";

		} catch (IOException e) {

			e.printStackTrace();
			return "category";
		}

	}

	@PutMapping(value = "/categoryEdit/{category_name}", consumes = "application/json", produces = "application/json")
	public String categoryEdit(Model model, @PathVariable String category_name, @RequestBody Category category) {

		try {
			logger.info("Edit Existing Category to : " + category_name);
			logger.info("Category : " + category);
			menuService.categoryEdit(category_name, category);
		} catch (ClientProtocolException e) {
			logger.error("Failed to edit category '{}': {}", category_name, e.getMessage(), e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Category updated : " + category_name);
		return "category";
	}

	@DeleteMapping("/categoryDelete/{category_name}")
	public String categoryDelete(@PathVariable String category_name) {
		logger.info("Delete Existing Category : " + category_name);
		try {
			menuService.categoryDelete(category_name);
			return "category";
		} catch (IOException e) {

			e.printStackTrace();
			return "category";
		}

	}

	@DeleteMapping(value = "/deleteCategory/{category_id}")
	public String deleteCategory(@PathVariable int category_id) {
		logger.info("Delete Existing Category ID : " + category_id);
		try {
			menuService.deleteCategory(category_id);
			return "category";
		} catch (IOException e) {

			e.printStackTrace();
			return "category";
		}

	}

	@PostMapping("/itemAdd")
	public String itemAdd(Model model, @RequestParam String description, @RequestParam String category,
			@RequestParam float length, @RequestParam float width, @RequestParam float height) {
		logger.info("New Item to Add : " + description);
		try {
			String response = menuService.itemAdd(description, category, length, width, height);
			ResponseMessage responseMessage = new ResponseMessage();
			responseMessage.setResponseMessage(response);
			model.addAttribute("responseMessage", responseMessage.getResponseMessage());
			// if (responseMessage.equals("Item already exists")) {
			logger.info("Response Message : " + responseMessage);
			return "item";
			// } else {
			// logger.info("Item added : " + description);
			// return "item";
			// }

		} catch (IOException e) {
			e.printStackTrace();
			return "item";
		}

	}

	@PutMapping("/itemEdit")
	public String itemEdit(Model model, @RequestParam int itemId, @RequestParam String description,
			@RequestParam String category,
			@RequestParam float length, @RequestParam float width, @RequestParam float height) {
		try {
			ResponseEntity<String> responseMessage = menuService.itemEdit(itemId, description, category, length, width,
					height);
			logger.info("responseMessage : " + responseMessage.getBody());
			// ResponseMessage responseMessage = new ResponseMessage();
			// responseMessage.setResponseMessage(response);
			model.addAttribute("responseMessage", responseMessage.getBody());
			// if (responseMessage.equals("Item already exists")) {
			logger.info("Response Message : " + responseMessage);
			return "item";
			// } else {
			// logger.info("Item Edit : " + description);
			// return "item";
			// }

		} catch (IOException e) {
			e.printStackTrace();
			return "item";
		}

	}

	@DeleteMapping(value = "/deleteItem/{item_id}")
	public String deleteItem(@PathVariable int item_id) {
		logger.info("Delete Existing Item : " + item_id);
		try {
			menuService.deleteItem(item_id);
			logger.info("Item deleted successfully : " + item_id);
			return "item";
		} catch (IOException e) {

			e.printStackTrace();
			return "item";
		}

	}

	@GetMapping("/locationInfo")
	public String locationInfo(Model model, HttpServletRequest request, HttpSession httpSession) {
		logger.info("Location");
		try {
			Iterable<Location> locations;
			Iterable<Grp> grps;
			grps = menuService.getGrps();
			locations = menuService.getLocations();
			logger.info("Fetched Locations : " + locations);
			model.addAttribute("grps", grps);
			model.addAttribute("locations", locations);
			return "location";
		} catch (IOException e) {
			e.printStackTrace();
			return "location";
		}
	}

	@PostMapping("/locationAdd")
	public String locationAdd(Model model, @RequestParam String locn_brcd, @RequestParam String grp,
			@RequestParam String locn_class, @RequestParam float length, @RequestParam float width,
			@RequestParam float height, @RequestParam float max_vol, @RequestParam float max_qty,
			@RequestParam float max_weight) {
		logger.info("New Location to Add : " + locn_brcd);
		try {
			String responseMessage = menuService.locationAdd(locn_brcd, grp, locn_class, length, width, height, max_vol,
					max_qty, max_weight);
			model.addAttribute("responseMessage", responseMessage);
			if (responseMessage.equals("Location already exists")) {
				logger.info("Response Message : " + responseMessage);
				return "location";
			} else {
				logger.info("Location added : " + locn_brcd);
				return "location";
			}

		} catch (IOException e) {
			e.printStackTrace();
			return "item";
		}

	}

	@PutMapping("/locationEdit")
	public String locationEdit(Model model, @RequestParam String locn_brcd, @RequestParam String grp,
			@RequestParam String locn_class, @RequestParam float length, @RequestParam float width,
			@RequestParam float height, @RequestParam float max_vol, @RequestParam float max_qty,
			@RequestParam float max_weight) {
		logger.info("Location to Edit : " + locn_brcd);
		try {
			ResponseEntity<String> responseMessage = menuService.locationEdit(locn_brcd, grp, locn_class, length, width,
					height, max_vol, max_qty, max_weight);
			model.addAttribute("responseMessage", responseMessage.getBody());
			logger.info("Response Message : " + responseMessage.getBody());
			return "location";

		} catch (IOException e) {
			e.printStackTrace();
			return "location";
		}

	}

	@DeleteMapping(value = "/deleteLocation/{locn_brcd}")
	public String deleteLocation(@PathVariable String locn_brcd) {
		logger.info("Delete Existing Item : " + locn_brcd);
		try {
			menuService.deleteLocation(locn_brcd);
			logger.info("Location deleted successfully : " + locn_brcd);
			return "location";
		} catch (IOException e) {

			e.printStackTrace();
			return "location";
		}

	}

	@GetMapping("/lpnInfo")
	public String lpnInfo(Model model, HttpServletRequest request, HttpSession httpSession) {
		logger.info("LPN");
		try {
			Iterable<Lpn> lpns;
			lpns = menuService.getLpns();
			logger.info("Fetched Lpns : " + lpns);
			model.addAttribute("lpns", lpns);
			return "lpn";
		} catch (IOException e) {
			e.printStackTrace();
			return "lpn";
		}
	}

	@PutMapping("/lpnEdit")
	public String lpnEdit(@RequestParam String lpn_name, @RequestParam String item_desc, @RequestParam float length,
			@RequestParam float width, @RequestParam float height, @RequestParam int quantity,
			@RequestParam int adjustQty, @RequestParam int lpn_facility_status, @RequestParam float volume) {
		logger.info("Lpn to Edit : " + lpn_name);
		try {
			String responseMessage = menuService.lpnEdit(lpn_name, item_desc, length, width, height, quantity,
					adjustQty, lpn_facility_status, volume);
			if (responseMessage.equals("Lpn already exists")) {
				logger.info("Response Message : " + responseMessage);
				return "lpn";
			} else {
				logger.info("Lpn Edit : " + lpn_name);
				return "lpn";
			}

		} catch (IOException e) {
			e.printStackTrace();
			return "lpn";
		}

	}

	@GetMapping("/inventoryInfo")
	public String inventoryInfo(Model model, HttpServletRequest request, HttpSession httpSession) {
		logger.info("Inventory");
		try {
			Iterable<Inventory> inventories;
			inventories = menuService.getInventories();
			logger.info("Fetched Inventories : " + inventories);
			model.addAttribute("inventories", inventories);
			return "inventory";
		} catch (IOException e) {
			e.printStackTrace();
			return "inventory";
		}
	}

	@GetMapping("/locationInquiry")
	public String locationInquiry(Model model) {
		List<Menu> menus = menuService.getAllMenus();
		logger.info("" + menus);
		logger.info("" + model.addAttribute("menus", menus));
		return "menu";
	}

	@GetMapping("/putawayLpnToReserve")
	public String putawayLpnToReserve(Model model) {
		logger.info("putawayLpnToReserve");
		return "putawayLpnToReserve";
	}

	@PostMapping("/locateLpnToReserve")
	public String putawayLpnToReserve(Model model, @RequestParam String lpn_name, @RequestParam String resv_locn) {
		logger.info("Location LPN : " + lpn_name + " to " + resv_locn + " Reserve location ");
		String response = "";
		try {
			response = menuService.locateLpnToResv(lpn_name, resv_locn);
			model.addAttribute("responseMessage", response);
			return "putawayLpnToReserve";
		} catch (IOException e) {
			logger.info("IOException occurred: ", e);
			// e.printStackTrace();
			return "putawayLpnToReserve";
		}
	}

	@GetMapping("/putawayLpnToActive")
	public String putawayLpnToActive(Model model) {
		logger.info("putawayLpnToActive");
		return "putawayLpnToActive";
	}

	@GetMapping("/putawayLpnToActiveSys")
	public String putawayLpnToActiveSys(Model model) {
		logger.info("putawayLpnToActiveSys");
		return "putawayLpnToActiveSys";
	}

	@PostMapping("/checkActiveInventory")
	@ResponseBody
	public String checkActiveInventory(Model model, @RequestParam String lpn_name) {

		String response = "";

		try {
			logger.info("LPN : " + lpn_name);
			response = menuService.checkActiveInventory(lpn_name);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Active Location : " + response);
		// model.addAttribute("actLoc", response);
		// logger.info(""+model.getAttribute("actLoc"));
		return response;
		// }
		// catch (IOException e) {
		// logger.info("IOException occurred: ", e);
		// // e.printStackTrace();
		// return "putawayLpnToActiveSys";
		// }
	}

	@PostMapping("/locateLpnToActive")
	public String putawayLpnToActive(Model model, @RequestParam String lpn_name, @RequestParam String active_locn) {
		logger.info("Location LPN : " + lpn_name + " to " + active_locn + " Active location ");
		String response = "";
		try {
			response = menuService.locateLpnToActive(lpn_name, active_locn);
			model.addAttribute("responseMessage", response);
			return "putawayLpnToActive";
		} catch (IOException e) {
			logger.info("IOException occurred: ", e);
			// e.printStackTrace();
			return "putawayLpnToActive";
		}
	}

	@PostMapping("/signIn")
	public String signIn(Model model, HttpSession httpSession, RedirectAttributes redirectAttributes,
			@RequestParam String username, @RequestParam String password) throws JsonProcessingException {
		try {
			logger.info("Username: " + username);
			String signInResponse = menuService.signIn(username, password);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> token = mapper.readValue(signInResponse, Map.class);
			String decodedtoken = token.get("token");
			logger.info("token values : " + decodedtoken);
			httpSession.setAttribute("decodedtoken", decodedtoken);
			logger.info("User logged in successfully: " + username);

			return "redirect:/api/showMenu";

		} catch (UnauthorizedException e) {
			String invalidCredMessage = "Invalid Credentials for User : " + username;
			redirectAttributes.addFlashAttribute("invalidCredMessage", invalidCredMessage);
			logger.info("Unauthorized access for user: " + username);
			return "redirect:/api/index";
		} catch (HttpClientErrorException httpeErr) {
			String invalidCredMessage = "Http Client Error : " + httpeErr.getMessage();
			redirectAttributes.addFlashAttribute("invalidCredMessage", invalidCredMessage);
			httpeErr.printStackTrace();
			logger.info("Http Client Error : " + httpeErr.getMessage());
			return "redirect:/api/index";
		} catch (JsonProcessingException jsonProExp) {
			String invalidCredMessage = "Error processing Json  : " + jsonProExp.getMessage();
			redirectAttributes.addFlashAttribute("invalidCredMessage", invalidCredMessage);
			logger.info("Error processing Json  : " + jsonProExp.getMessage());
			return "redirect:/api/index";
		} catch (Exception e) {
			String invalidCredMessage = "An Error Occured  : " + e.getMessage();
			redirectAttributes.addFlashAttribute("invalidCredMessage", invalidCredMessage);
			logger.info("An Error Occured  : " + e.getMessage());
			return "redirect:/api/index";
		}
	}

	@GetMapping("/logout")
	public String logout(RedirectAttributes redirectAttributes, HttpSession httpSession) {
		String response;
		try {
			response = menuService.signout(httpSession);
			logger.info("Response : " + response);
			redirectAttributes.addFlashAttribute("response", response);
			httpSession.removeAttribute("decodedtoken");
			return "redirect:/api/index";
		} catch (IOException e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("response", "An error occured while logging out!!");
			return null;
		}

	}

	@GetMapping("/settings")
	public String settings(Model model, HttpServletRequest request, HttpSession httpSession) {
		logger.info("settings");
		return "settings";
	}

	@GetMapping("/userlist")
	public String userList(Model model) { // Removed unused parameters
		logger.info("Accessing userlist");
		try {
			List<UserDto> userDtos = menuAccessService.getUsers();
			model.addAttribute("users", userDtos);
			return "userlist"; // Returns the success view
		} catch (IOException e) {
			// Log the error properly instead of e.printStackTrace()
			logger.error("Failed to fetch users: ", e);
			// Add an error message to the model so the user sees something went wrong
			model.addAttribute("errorMessage", "Unable to load the user list at this time.");
			// Return an error view, or route back to the userlist with the error message
			return "error";
		}
	}

	@PostMapping("/userAdd")
	public String userAdd(Model model, @RequestParam String firstname, @RequestParam String middlename,
			@RequestParam String lastname, @RequestParam String username, @RequestParam String password,
			@RequestParam String email) {
		logger.info("New User to Add : " + username);
		String response = menuService.userAdd(firstname, middlename, lastname, username, password, email);
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setResponseMessage(response);
		model.addAttribute("responseMessage", responseMessage.getResponseMessage());
		logger.info("Response Message : " + responseMessage);
		return "userlist";
	}

	@GetMapping("/menulist")
	public String menuList(Model model, HttpServletRequest request, HttpSession httpSession) {
		logger.info("menulist");
		List<Menu> allMenus = menuService.getAllMenus();
		model.addAttribute("allMenus", allMenus);
		logger.info("All menus : " + allMenus);
		return "menulist";
	}

	@GetMapping("/sopConfig")
	public String sopConfig(Model model, HttpServletRequest request, HttpSession httpSession) {
		logger.info("sop-config");
		try {
			List<SopLocationRangeDto> sopLocationRangeDtos = menuService.getLocationRanges();
			List<Category> categories = menuService.getCategories();
			List<SopActionTypeDto> sopActionTypeDtos = menuService.getSopActionTypes();
			logger.info("Location Range : " + sopLocationRangeDtos);
			logger.info("Action Type : " + sopActionTypeDtos);
			logger.info("Categories : " + categories);

			model.addAttribute("locationRanges", sopLocationRangeDtos);
			model.addAttribute("actionTypes", sopActionTypeDtos);
			model.addAttribute("categories", categories);

			Map<Integer, Boolean> selectedRanges = new java.util.HashMap<>();
			for (SopLocationRangeDto range : sopLocationRangeDtos) {
				selectedRanges.put(range.getId(), false); // Initialize all as unchecked
			}
			logger.info("selectedRanges : " + selectedRanges);
			model.addAttribute("selectedRanges", selectedRanges);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("responseMessage", "An error occured, Please check with administrator");
			return "sop-config";
		}
		return "sop-config";
	}

	@PostMapping("/sop/runBatch")
	public String runBatch(HttpServletRequest request, @RequestParam("actionType") String sopActionType,
			@RequestParam("category_name") String category_name, @RequestParam("activeTab") String activeTab,
			Model model, HttpSession httpSession) {
		try {
			logger.info("Run Batch for Action Type : " + sopActionType + " and Category : " + category_name);
			List<SopLocationRangeDto> sopLocationRangeDtos = menuService.getLocationRanges();
			List<Category> categories = menuService.getCategories();
			List<SopActionTypeDto> sopActionTypeDtos = menuService.getSopActionTypes();
			logger.info("Location Range : " + sopLocationRangeDtos);
			logger.info("Action Type : " + sopActionTypeDtos);
			logger.info("Categories : " + categories);

			model.addAttribute("locationRanges", sopLocationRangeDtos);
			model.addAttribute("actionTypes", sopActionTypeDtos);
			model.addAttribute("categories", categories);

			// Check actionType is assign or unassign
			if (sopActionType.equals("ASSIGN")) {
				String batchAssign = "BATCHTIMEASSIGN";
				menuService.assignBatch(sopActionType, batchAssign, category_name);
				logger.info("Assign Batch triggered for category : " + category_name);
				model.addAttribute("responseMessage", sopActionType + "Batch Submitted Successfully!!");
				return "sop-config";
			} else if (sopActionType.equals("UNASSIGN")) {
				String batchAssign = "BATCHTIMEUNASSIGN";
				try {
					menuService.unassignBatch(sopActionType, batchAssign, category_name);
					logger.info("Unassign Batch triggered for category : " + category_name);
					model.addAttribute("responseMessage", sopActionType + "Batch Submitted Successfully!!");
					return "sop-config";

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("activeTab", activeTab);
		return "sop-config";
	}

	@PostMapping("/sop/location-range/add")
	public String addLocationRange(HttpServletRequest request, @RequestParam("actionType") String sopActionType,
			@RequestParam("category") String category_name, @RequestParam("fromLocation") String fromLocation,
			@RequestParam("toLocation") String toLocation,
			@RequestParam("isActive") String isActive, @RequestParam("activeTab") String activeTab, Model model,
			HttpSession httpSession) {
		String decodedToken = (String) httpSession.getAttribute("decodedtoken");
		try {
			String username = menuAccessService.getUserName(decodedToken);
			List<SopLocationRangeDto> sopLocationRangeDtos = menuService.getLocationRanges();
			List<Category> categories = menuService.getCategories();
			List<SopActionTypeDto> sopActionTypeDtos = menuService.getSopActionTypes();
			logger.info("Location Range : " + sopLocationRangeDtos);
			logger.info("Action Type : " + sopActionTypeDtos);
			logger.info("Categories : " + categories);

			model.addAttribute("locationRanges", sopLocationRangeDtos);
			model.addAttribute("actionTypes", sopActionTypeDtos);
			model.addAttribute("categories", categories);
			logger.info("Add Location Range for Action Type : " + sopActionType + " and Category : " + category_name);

			String locationRangeAddresponseMessage = menuService.addLocationRange(sopActionType, category_name,
					fromLocation, toLocation,
					isActive, username);

			model.addAttribute("locationRangeAddresponseMessage", locationRangeAddresponseMessage);

		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("activeTab", activeTab);
		return "sop-config";
	}

	@PostMapping("/sop/location-range/update")
	public String updatecationRange(HttpServletRequest request,
			@RequestParam("id") String id,
			@RequestParam("actionType") String sopActionType,
			@RequestParam("category") String category_name, @RequestParam("fromLocation") String fromLocation,
			@RequestParam("toLocation") String toLocation,
			@RequestParam("isActive") String isActive, @RequestParam("activeTab") String activeTab, Model model,
			HttpSession httpSession) {
		String decodedToken = (String) httpSession.getAttribute("decodedtoken");
		try {
			String username = menuAccessService.getUserName(decodedToken);
			List<SopLocationRangeDto> sopLocationRangeDtos = menuService.getLocationRanges();
			List<Category> categories = menuService.getCategories();
			List<SopActionTypeDto> sopActionTypeDtos = menuService.getSopActionTypes();
			logger.info("Location Range : " + sopLocationRangeDtos);
			logger.info("Action Type : " + sopActionTypeDtos);
			logger.info("Categories : " + categories);

			model.addAttribute("locationRanges", sopLocationRangeDtos);
			model.addAttribute("actionTypes", sopActionTypeDtos);
			model.addAttribute("categories", categories);
			logger.info("Add Location Range for Action Type : " + sopActionType + " and Category : " + category_name);

			String locationRangeUpdateresponseMessage = menuService.updateLocationRange(id, sopActionType,
					category_name, fromLocation, toLocation,
					isActive, username);
			logger.info("locationRangeUpdateresponseMessage : " + locationRangeUpdateresponseMessage);
			model.addAttribute("locationRangeUpdateresponseMessage", locationRangeUpdateresponseMessage);

		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("activeTab", activeTab);
		return "sop-config";
	}

	@PostMapping("/sop/getEligibleUpcsForSop/{category}")
	public String getEligibleUpcsForSop(HttpServletRequest request, @RequestParam("category") String category,
			@RequestParam("activeTab") String activeTab,
			Model model, HttpSession httpSession) {
		try {
			logger.info("Eligible UPCs For Category : " + category);
			List<SopLocationRangeDto> sopLocationRangeDtos = menuService.getLocationRanges();
			List<Category> categories = menuService.getCategories();
			List<SopActionTypeDto> sopActionTypeDtos = menuService.getSopActionTypes();
			logger.info("Location Range : " + sopLocationRangeDtos);
			logger.info("Action Type : " + sopActionTypeDtos);
			logger.info("Categories : " + categories);

			model.addAttribute("locationRanges", sopLocationRangeDtos);
			model.addAttribute("actionTypes", sopActionTypeDtos);
			model.addAttribute("categories", categories);

			// Check actionType is assign or unassign
			if (category != null) {
				List<String> eligibleUpcsForSop = menuService.getEligibleUpcsForSop(category);
				return "sop-config";
			} else {
				// response = menuService.unassignBatch(actionType,category);
				try {
					logger.info("Unable to fetch  eligibleUpcsForSop of category : {}" + category);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("activeTab", activeTab);
		return "sop-config";
	}

	@GetMapping("/cubiscanLog")
	public String cubiscanLog(Model model, HttpServletRequest request, HttpSession httpSession) {
		logger.info("cubiscan-log");
		try {
			List<CubiscanLog> cubiscanLogs = menuService.getCubiscanLogs();
			logger.info("" + cubiscanLogs);
			model.addAttribute("cubiscanLogs", cubiscanLogs);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("responseMessage", "An error occured, Please check with administrator");
			return "cubiscan-log";
		}
		return "cubiscan-log";
	}

	@GetMapping("/endpoint")
	public String endpoint(Model model, HttpServletRequest request, HttpSession httpSession) {
		logger.info("endpoint");
		return "endpoint";
	}
}
