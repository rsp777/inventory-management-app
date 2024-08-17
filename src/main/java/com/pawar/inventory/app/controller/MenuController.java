package com.pawar.inventory.app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.context.LazyContextVariable;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawar.inventory.app.exception.MenuNotFoundException;
import com.pawar.inventory.app.exception.ParentMenuNotFoundException;
import com.pawar.inventory.app.exception.UnauthorizedException;
import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.model.ResponseMessage;
import com.pawar.inventory.app.service.MenuAccessService;
import com.pawar.inventory.app.service.MenuService;
import com.pawar.inventory.entity.Category;
import com.pawar.inventory.entity.Inventory;
import com.pawar.inventory.entity.Item;
import com.pawar.inventory.entity.Location;
import com.pawar.inventory.entity.Lpn;
import com.pawar.todo.dto.UserDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api")
// @EnableJpaRepositories
@EnableJdbcRepositories
public class MenuController {
	private final static Logger logger = Logger.getLogger(MenuController.class.getName());

	@Autowired
	private MenuService menuService;

	@Autowired
	private MenuAccessService menuAccessService;

	@GetMapping("/index")
	public String index(Model model) {

		return "index";
	}

	@PostMapping("/addMenu")
	public ResponseEntity<?> addMenu(@RequestParam String newProtocol, @RequestParam String newMenuName,
			@RequestParam String newMenuLink, @RequestParam String newHostname, @RequestParam String newMenuType,
			@RequestParam String newParentMenuName) {
		logger.info("New Protocol : " + newProtocol);
		logger.info("New Menu : " + newMenuName);
		logger.info("New Menu Link : " + newMenuLink);
		logger.info("New Hostname  : " + newHostname);
		logger.info("New Menu Type : " + newMenuType);
		logger.info("Parent Menu : " + newParentMenuName);

		Menu newMenu;

		newMenu = menuService.addMenu(newProtocol, newMenuName, newMenuLink, newHostname, newMenuType,
				newParentMenuName);
		logger.info("New Menu is now created : " + newMenu);
		return ResponseEntity.ok("Menu Added Successfully : " + newMenu);

	}

	@PatchMapping("/updateMenu")
	public ResponseEntity<?> updateMenu(@RequestParam String newProtocol, @RequestParam String newMenuName,
			@RequestParam String newMenuLink, @RequestParam String newHostname, @RequestParam String newMenuType) {
		logger.info("Update Protocol : " + newProtocol);
		logger.info("Update Menu : " + newMenuName);
		logger.info("Update Menu Link : " + newMenuLink);
		logger.info("Update Hostname  : " + newHostname);
		logger.info("Update Menu Type : " + newMenuType);

		menuService.updateMenu(newProtocol, newMenuName, newMenuLink, newHostname, newMenuType);
		logger.info("New Menu is now update : " + newMenuName);
		return ResponseEntity.ok("Menu Update Successfully : " + newMenuName);
	}

	@GetMapping("/showMenu")
	public String showMenu(Model model, HttpServletRequest request, HttpSession httpSession) {
		String decodedToken = (String) httpSession.getAttribute("decodedtoken");
		List<Menu> menus;
		try {
			menus = menuAccessService.getAccessibleMenus(decodedToken);
			List<Menu> rf = new ArrayList<>();
			List<Menu> nav = new ArrayList<>();
			// logger.info("" + menus);
			for (Menu menu : menus) {
				if (menu.getMenu_type().equals("RF")) {
					rf.add(menu);
				} else if (menu.getMenu_type().equals("UI")) {
					nav.add(menu);
				}
			}
			logger.info("Request URI :" + request.getRequestURI());
			model.addAttribute("menus", rf);
			model.addAttribute("currentMenu", request.getRequestURI());
			model.addAttribute("nav_menus", nav);
			return "menu";
		} catch (JsonProcessingException | MenuNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "menu";
		}
//		List<Menu> menus = menuService.getAllMenus();

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
			logger.log(Level.SEVERE, "IOException occurred: ", e);
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
		String decodedToken = (String) httpSession.getAttribute("decodedtoken");
		logger.info("Item");
		try {
			List<Menu> menus;
			try {
				menus = menuAccessService.getAccessibleMenus(decodedToken);
				List<Menu> rf = new ArrayList<>();
				List<Menu> nav = new ArrayList<>();
				// logger.info("" + menus);
				for (Menu menu : menus) {
					if (menu.getMenu_type().equals("RF")) {
						rf.add(menu);
					} else if (menu.getMenu_type().equals("UI")) {
						nav.add(menu);
					}
				}
				Iterable<Item> items;

				Iterable<Category> categories = menuService.getfindAllCategories();
				items = menuService.getItems();
				logger.info("Fetched Items : " + items);
				model.addAttribute("categories", categories);
				model.addAttribute("items", items);
				model.addAttribute("nav_menus", nav);
				model.addAttribute("currentMenu", request.getRequestURI());

				// model.addAttribute("newCategory", new Category());
				return "item";
			} catch (MenuNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "item";
			}

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
		String decodedToken = (String) httpSession.getAttribute("decodedtoken");
		try {
			List<Menu> menus = menuAccessService.getAccessibleMenus(decodedToken);
			List<Menu> rf = new ArrayList<>();
			List<Menu> nav = new ArrayList<>();
			// logger.info("" + menus);
			for (Menu menu : menus) {
				if (menu.getMenu_type().equals("RF")) {
					rf.add(menu);
				} else if (menu.getMenu_type().equals("UI")) {
					nav.add(menu);
				}
			}
			Iterable<Category> categories;

			categories = menuService.getfindAllCategories();
			logger.info("Fetched Categories : ");

			model.addAttribute("currentMenu", request.getRequestURI());
			model.addAttribute("categories", categories);
			model.addAttribute("nav_menus", nav);

			// model.addAttribute("newCategory", new Category());
			return "category";
		} catch (IOException | MenuNotFoundException e) {

			e.printStackTrace();
			return "category";
		}

	}

	@SuppressWarnings("unlikely-arg-type")
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
		logger.info("Edit Existing Category to : " + category_name);
		logger.info("Category : " + category);
		menuService.categoryEdit(category_name, category);
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
	public String itemEdit(Model model, @RequestParam String description, @RequestParam String category,
			@RequestParam float length, @RequestParam float width, @RequestParam float height) {
		try {
			ResponseEntity<String> responseMessage = menuService.itemEdit(description, category, length, width, height);
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

	@DeleteMapping(value = "/deleteItem/{description}")
	public String deleteItem(@PathVariable String description) {
		logger.info("Delete Existing Item : " + description);
		try {
			menuService.deleteItem(description);
			logger.info("Item deleted successfully : " + description);
			return "item";
		} catch (IOException e) {

			e.printStackTrace();
			return "item";
		}

	}

	@GetMapping("/locationInfo")
	public String locationInfo(Model model, HttpServletRequest request, HttpSession httpSession) {
		logger.info("Location");
		String decodedToken = (String) httpSession.getAttribute("decodedtoken");
		try {
			List<Menu> menus = menuAccessService.getAccessibleMenus(decodedToken);
			List<Menu> rf = new ArrayList<>();
			List<Menu> nav = new ArrayList<>();
			// logger.info("" + menus);
			for (Menu menu : menus) {
				if (menu.getMenu_type().equals("RF")) {
					rf.add(menu);
				} else if (menu.getMenu_type().equals("UI")) {
					nav.add(menu);
				}
			}
			Iterable<Location> locations;

			locations = menuService.getLocations();
			logger.info("Fetched Locations : " + locations);
			model.addAttribute("locations", locations);
			model.addAttribute("nav_menus", nav);
			model.addAttribute("currentMenu", request.getRequestURI());

			// model.addAttribute("newCategory", new Category());
			return "location";
		} catch (IOException | MenuNotFoundException e) {

			e.printStackTrace();
			return "location";
		}

	}

	@PostMapping("/locationAdd")
	public String locationAdd(Model model, @RequestParam String locn_brcd, @RequestParam String locn_class,
			@RequestParam float length, @RequestParam float width, @RequestParam float height,
			@RequestParam float max_vol, @RequestParam float max_qty, @RequestParam float max_weight) {
		logger.info("New Location to Add : " + locn_brcd);
		try {
			String responseMessage = menuService.locationAdd(locn_brcd, locn_class, length, width, height, max_vol,
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
	public String locationEdit(Model model, @RequestParam String locn_brcd, @RequestParam String locn_class,
			@RequestParam float length, @RequestParam float width, @RequestParam float height,
			@RequestParam float max_vol, @RequestParam float max_qty, @RequestParam float max_weight) {
		logger.info("Location to Edit : " + locn_brcd);
		try {
			ResponseEntity<String> responseMessage = menuService.locationEdit(locn_brcd, locn_class, length, width,
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
		String decodedToken = (String) httpSession.getAttribute("decodedtoken");
		try {
			List<Menu> menus = menuAccessService.getAccessibleMenus(decodedToken);
			List<Menu> rf = new ArrayList<>();
			List<Menu> nav = new ArrayList<>();
			// logger.info("" + menus);
			for (Menu menu : menus) {
				if (menu.getMenu_type().equals("RF")) {
					rf.add(menu);
				} else if (menu.getMenu_type().equals("UI")) {
					nav.add(menu);
				}
			}
			Iterable<Lpn> lpns;

			lpns = menuService.getLpns();
			logger.info("Fetched Lpns : " + lpns);
			model.addAttribute("lpns", lpns);
			model.addAttribute("nav_menus", nav);
			model.addAttribute("currentMenu", request.getRequestURI());

			// model.addAttribute("newCategory", new Category());
			return "lpn";
		} catch (IOException | MenuNotFoundException e) {

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
		String decodedToken = (String) httpSession.getAttribute("decodedtoken");
		try {
			List<Menu> menus = menuAccessService.getAccessibleMenus(decodedToken);
			List<Menu> rf = new ArrayList<>();
			List<Menu> nav = new ArrayList<>();
			// logger.info("" + menus);
			for (Menu menu : menus) {
				if (menu.getMenu_type().equals("RF")) {
					rf.add(menu);
				} else if (menu.getMenu_type().equals("UI")) {
					nav.add(menu);
				}
			}
			Iterable<Inventory> inventories;

			inventories = menuService.getInventories();
			logger.info("Fetched Inventories : " + inventories);
			model.addAttribute("inventories", inventories);
			model.addAttribute("nav_menus", nav);
			model.addAttribute("currentMenu", request.getRequestURI());

			// model.addAttribute("newCategory", new Category());
			return "inventory";
		} catch (IOException | MenuNotFoundException e) {

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
			logger.log(Level.SEVERE, "IOException occurred: ", e);
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
		logger.info("LPN : " + lpn_name);
		String response = "";
//		try {
			response = menuService.checkActiveInventory(lpn_name);
			logger.info("Active Location : "+response);
//			model.addAttribute("actLoc", response);
//			logger.info(""+model.getAttribute("actLoc"));
			return response;
//		} 
//		catch (IOException e) {
//			logger.log(Level.SEVERE, "IOException occurred: ", e);
//			// e.printStackTrace();
//			return "putawayLpnToActiveSys";
//		}
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
			logger.log(Level.SEVERE, "IOException occurred: ", e);
			// e.printStackTrace();
			return "putawayLpnToActive";
		}
	}

	/**
	 * @param model
	 * @param redirectAttributes
	 * @param username
	 * @param password
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/signIn")
	public String signIn(Model model, HttpSession httpSession, RedirectAttributes redirectAttributes,
			@RequestParam String username, @RequestParam String password) {
		try {
			logger.info("Username: " + username);
			String signInResponse = menuService.signIn(username, password);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> token = mapper.readValue(signInResponse, Map.class);
			String decodedtoken = token.get("token");
			logger.info("token values : " + decodedtoken);
			// redirectAttributes.addFlashAttribute("decodedtoken", decodedtoken);
			// DecodedJWT decodedJWT = JWT.decode(decodedtoken);
			// String user_name = decodedJWT.getSubject();
			// logger.info("decodedJWT : " + user_name);
			// redirectAttributes.addFlashAttribute("user_name", user_name);
			httpSession.setAttribute("decodedtoken", decodedtoken);
			logger.info("User logged in successfully: " + username);

			return "redirect:/api/showMenu";

		} catch (UnauthorizedException e) {
			String invalidCredMessage = "Invalid Credentials for User : " + username;
			redirectAttributes.addFlashAttribute("invalidCredMessage", invalidCredMessage);
			logger.log(Level.SEVERE, "Unauthorized access for user: " + username);
			return "redirect:/api/index";
		} catch (HttpClientErrorException httpeErr) {
			String invalidCredMessage = "Http Client Error : " + httpeErr.getMessage();
			redirectAttributes.addFlashAttribute("invalidCredMessage", invalidCredMessage);
			httpeErr.printStackTrace();
			logger.log(Level.SEVERE, "Http Client Error : " + httpeErr.getMessage());
			return "redirect:/api/index";
		} catch (JsonProcessingException jsonProExp) {
			String invalidCredMessage = "Error processing Json  : " + jsonProExp.getMessage();
			redirectAttributes.addFlashAttribute("invalidCredMessage", invalidCredMessage);
			logger.log(Level.SEVERE, "Error processing Json  : " + jsonProExp.getMessage());
			return "redirect:/api/index";
		} catch (JWTDecodeException jwtDecodeException) {
			String invalidCredMessage = "There was an unexpected error  : " + jwtDecodeException.getMessage();
			redirectAttributes.addFlashAttribute("invalidCredMessage", invalidCredMessage);
			logger.log(Level.SEVERE, "There was an unexpected error : " + jwtDecodeException.getMessage());
			return "redirect:/api/index";
		} catch (Exception e) {
			String invalidCredMessage = "An Error Occured  : " + e.getMessage();
			redirectAttributes.addFlashAttribute("invalidCredMessage", invalidCredMessage);
			logger.log(Level.SEVERE, "An Error Occured  : " + e.getMessage());
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
		String decodedToken = (String) httpSession.getAttribute("decodedtoken");
		List<Menu> menus;

		try {
			menus = menuAccessService.getAccessibleMenus(decodedToken);
			List<Menu> rf = new ArrayList<>();
			List<Menu> nav = new ArrayList<>();
			List<Menu> side = new ArrayList<>();
			// logger.info("" + menus);
			for (Menu menu : menus) {
				if (menu.getMenu_type().equals("RF")) {
					rf.add(menu);
				} else if (menu.getMenu_type().equals("UI")) {
					nav.add(menu);
				} else if (!menu.getMenu_type().equals("AUTH")) {
					side.add(menu);
				}
			}
			logger.info("Side Menus : " + side);
			model.addAttribute("menus", rf);
			model.addAttribute("currentMenu", request.getRequestURI());
			model.addAttribute("nav_menus", nav);
			model.addAttribute("side_menus", side);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		} catch (MenuNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JWTDecodeException e) {
			model.addAttribute("responseMessage", "Session Expired please Login Again");
			return "redirect:/api/index";
		}

		logger.info("settings");
		return "settings";
	}

	@GetMapping("/userlist")
	public String userList(Model model, HttpServletRequest request, HttpSession httpSession) {
		String decodedToken = (String) httpSession.getAttribute("decodedtoken");
		List<Menu> menus;
		List<UserDto> userDtos;
		try {
			userDtos = menuAccessService.getUsers();
			menus = menuAccessService.getAccessibleMenus(decodedToken);
			List<Menu> rf = new ArrayList<>();
			List<Menu> nav = new ArrayList<>();
			List<Menu> side = new ArrayList<>();
			// logger.info("" + menus);
			for (Menu menu : menus) {
				if (menu.getMenu_type().equals("RF")) {
					rf.add(menu);
				} else if (menu.getMenu_type().equals("UI")) {
					nav.add(menu);
				} else if (!menu.getMenu_type().equals("AUTH")) {
					side.add(menu);
				}
			}
			logger.info("Side Menus : " + side);
			model.addAttribute("menus", rf);
			model.addAttribute("currentMenu", request.getRequestURI());
			model.addAttribute("nav_menus", nav);
			model.addAttribute("side_menus", side);
			model.addAttribute("users", userDtos);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		} catch (MenuNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("userlist");
		return "userlist";
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
		String decodedToken = (String) httpSession.getAttribute("decodedtoken");
		List<Menu> menus;
		List<Menu> allMenus;
		
		try {
			
			menus = menuAccessService.getAccessibleMenus(decodedToken);
			allMenus = menuService.getAllMenus();
			List<Menu> rf = new ArrayList<>();
			List<Menu> nav = new ArrayList<>();
			List<Menu> side = new ArrayList<>();
			// logger.info("" + menus);
			for (Menu menu : menus) {
				if (menu.getMenu_type().equals("RF")) {
					rf.add(menu);
				} else if (menu.getMenu_type().equals("UI")) {
					nav.add(menu);
				} else if (!menu.getMenu_type().equals("AUTH")) {
					side.add(menu);
				}
			}
			logger.info("Side Menus : " + side);
			model.addAttribute("menus", rf);
			model.addAttribute("currentMenu", request.getRequestURI());
			model.addAttribute("nav_menus", nav);
			model.addAttribute("side_menus", side);
			model.addAttribute("allMenus", allMenus);
			logger.info("All menus : "+allMenus);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		} catch (MenuNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("menuList");
		return "menuList";
	}
}
