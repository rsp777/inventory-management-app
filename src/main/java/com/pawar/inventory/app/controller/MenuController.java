package com.pawar.inventory.app.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.dto.CategoryRequestDTO;
import com.pawar.inventory.app.dto.ItemRequestDTO;
import com.pawar.inventory.app.dto.MenuActiveLpnRequestDTO;
import com.pawar.inventory.app.dto.MenuBatchRequestDTO;
import com.pawar.inventory.app.dto.MenuCheckActiveInventoryRequestDTO;
import com.pawar.inventory.app.dto.MenuEligibleUpcsRequestDTO;
import com.pawar.inventory.app.dto.MenuLocationRequestDTO;
import com.pawar.inventory.app.dto.MenuLocationRangeRequestDTO;
import com.pawar.inventory.app.dto.MenuLpnCreateRequestDTO;
import com.pawar.inventory.app.dto.MenuLpnEditRequestDTO;
import com.pawar.inventory.app.dto.MenuReserveLpnRequestDTO;
import com.pawar.inventory.app.dto.MenuUserAddRequestDTO;
import com.pawar.inventory.app.model.CubiscanLog;
import com.pawar.inventory.app.model.ResponseMessage;
import com.pawar.inventory.app.service.MenuAccessService;
import com.pawar.inventory.app.service.MenuService;
import com.pawar.inventory.app.service.NavigationService;
import com.pawar.inventory.app.util.SessionUtil;
import com.pawar.inventory.entity.Category;
import com.pawar.inventory.entity.SopActionTypeDto;
import com.pawar.inventory.entity.SopLocationRangeDto;
import com.pawar.todo.dto.UserDto;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
	public String index() {
		return "redirect:/api/auth/index";
	}

	@GetMapping("/createLpn")
	public String createLpn() {
		return "redirect:/lpn/create";
	}

	@PostMapping("/newLpn")
	public String newLpnAlias(Model model, @Valid @ModelAttribute MenuLpnCreateRequestDTO requestDTO,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("message", "Invalid LPN data");
			return AppConstants.View.CREATE_LPN;
		}
		return handleLegacyNewLpn(model, requestDTO);
	}

	@PostMapping("/legacy/newLpn")
	public String newLpn(Model model, @Valid @ModelAttribute MenuLpnCreateRequestDTO requestDTO,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("message", "Invalid LPN data");
			return AppConstants.View.CREATE_LPN;
		}
		return handleLegacyNewLpn(model, requestDTO);
	}

	@GetMapping("/lpnInquiry")
	public String lpnInquiry() {
		return "redirect:/lpn/inquiry";
	}

	@GetMapping("/inventoryByLocation")
	public String inventoryByLocation() {
		return "redirect:/inventory/by-location";
	}

	@GetMapping("/inventoryByLpn")
	public String inventoryByLpn() {
		return "redirect:/inventory/by-lpn";
	}

	@GetMapping("/inventoryByItem")
	public String inventoryByItem() {
		return "redirect:/inventory/by-item";
	}

	@GetMapping("/legacy/itemInquiry")
	public String itemInquiry() {
		return "redirect:/api/itemInquiry";
	}

	@GetMapping("/legacy/itemInfo")
	public String itemInfo() {
		return AppConstants.Redirect.ITEM_INFO;
	}

	@GetMapping("/legacy/getItem/{item_name}")
	public String getItem(@PathVariable String item_name) {
		logger.info("Legacy item inquiry alias hit for item: {}", item_name);
		return "redirect:/api/itemInquiry";
	}

	@GetMapping("/legacy/categoryInfo")
	public String categoryInfo() {
		return AppConstants.Redirect.CATEGORY_INFO;
	}

	@PostMapping("/legacy/categoryAdd/{category_name}")
	public String categoryAdd(@PathVariable String category_name) {
		return handleLegacyCategoryAdd(category_name);
	}

	@PutMapping(value = "/legacy/categoryEdit/{category_name}", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> categoryEdit(@PathVariable String category_name,
			@Valid @RequestBody CategoryRequestDTO requestDTO) {
		return handleLegacyCategoryEdit(category_name, requestDTO);
	}

	@DeleteMapping("/legacy/categoryDelete/{category_name}")
	public String categoryDelete(@PathVariable String category_name) {
		return handleLegacyCategoryDelete(category_name);
	}

	@DeleteMapping(value = "/deleteCategory/{category_id}")
	public String deleteCategoryAlias(@PathVariable int category_id) {
		return handleLegacyDeleteCategory(category_id);
	}

	@DeleteMapping(value = "/legacy/deleteCategory/{category_id}")
	public String deleteCategory(@PathVariable int category_id) {
		return handleLegacyDeleteCategory(category_id);
	}

	@PostMapping("/legacy/itemAdd")
	public String itemAdd(Model model, @Valid @ModelAttribute ItemRequestDTO requestDTO,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("error", bindingResult.getAllErrors());
			return AppConstants.View.ITEM;
		}
		return handleLegacyItemAdd(model, requestDTO);
	}

	@PutMapping("/legacy/itemEdit")
	public String itemEdit(Model model, @Valid @ModelAttribute ItemRequestDTO requestDTO,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("error", bindingResult.getAllErrors());
			return AppConstants.View.ITEM;
		}
		return handleLegacyItemEdit(model, requestDTO);
	}

	@DeleteMapping(value = "/legacy/deleteItem/{item_id}")
	public String deleteItem(@PathVariable int item_id) {
		return handleLegacyDeleteItem(item_id);
	}

	@GetMapping("/locationInfo")
	public String locationInfo() {
		return "redirect:/location";
	}

	@PostMapping("/locationAdd")
	public String locationAddAlias(Model model, @Valid @ModelAttribute MenuLocationRequestDTO requestDTO,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("responseMessage", "Invalid location data");
			return AppConstants.View.LOCATION;
		}
		return handleLegacyLocationAdd(model, requestDTO);
	}

	@PostMapping("/legacy/locationAdd")
	public String locationAdd(Model model, @Valid @ModelAttribute MenuLocationRequestDTO requestDTO,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("responseMessage", "Invalid location data");
			return AppConstants.View.LOCATION;
		}
		return handleLegacyLocationAdd(model, requestDTO);
	}

	@PutMapping("/locationEdit")
	public String locationEditAlias(Model model, @Valid @ModelAttribute MenuLocationRequestDTO requestDTO,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("responseMessage", "Invalid location data");
			return AppConstants.View.LOCATION;
		}
		return handleLegacyLocationEdit(model, requestDTO);
	}

	@PutMapping("/legacy/locationEdit")
	public String locationEdit(Model model, @Valid @ModelAttribute MenuLocationRequestDTO requestDTO,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("responseMessage", "Invalid location data");
			return AppConstants.View.LOCATION;
		}
		return handleLegacyLocationEdit(model, requestDTO);
	}

	@DeleteMapping(value = "/deleteLocation/{locn_brcd}")
	public String deleteLocationAlias(@PathVariable String locn_brcd) {
		return handleLegacyDeleteLocation(locn_brcd);
	}

	@DeleteMapping(value = "/legacy/deleteLocation/{locn_brcd}")
	public String deleteLocation(@PathVariable String locn_brcd) {
		return handleLegacyDeleteLocation(locn_brcd);
	}

	@GetMapping("/lpnInfo")
	public String lpnInfo() {
		return "redirect:/lpn";
	}

	@PutMapping("/lpnEdit")
	public String lpnEditAlias(@Valid @ModelAttribute MenuLpnEditRequestDTO requestDTO, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return AppConstants.View.LPN;
		}
		return handleLegacyLpnEdit(requestDTO);
	}

	@PutMapping("/legacy/lpnEdit")
	public String lpnEdit(@Valid @ModelAttribute MenuLpnEditRequestDTO requestDTO, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return AppConstants.View.LPN;
		}
		return handleLegacyLpnEdit(requestDTO);
	}

	@GetMapping("/inventoryInfo")
	public String inventoryInfo() {
		return "redirect:/inventory";
	}

	@GetMapping("/locationInquiry")
	public String locationInquiry() {
		return "redirect:/location/inquiry";
	}

	@GetMapping("/putawayLpnToReserve")
	public String putawayLpnToReserveAlias() {
		return "redirect:/lpn/putaway-reserve";
	}

	@GetMapping("/legacy/putawayLpnToReserve")
	public String putawayLpnToReserve() {
		return "redirect:/lpn/putaway-reserve";
	}

	@PostMapping("/locateLpnToReserve")
	public String locateLpnToReserveAlias(Model model, @Valid @ModelAttribute MenuReserveLpnRequestDTO requestDTO,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("responseMessage", "Invalid reserve location data");
			return AppConstants.View.PUTAWAY_RESERVE;
		}
		return handleLegacyPutawayReserve(model, requestDTO);
	}

	@PostMapping("/legacy/locateLpnToReserve")
	public String putawayLpnToReserve(Model model, @Valid @ModelAttribute MenuReserveLpnRequestDTO requestDTO,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("responseMessage", "Invalid reserve location data");
			return AppConstants.View.PUTAWAY_RESERVE;
		}
		return handleLegacyPutawayReserve(model, requestDTO);
	}

	@GetMapping("/putawayLpnToActive")
	public String putawayLpnToActiveAlias() {
		return "redirect:/lpn/putaway-active";
	}

	@GetMapping("/legacy/putawayLpnToActive")
	public String putawayLpnToActive() {
		return "redirect:/lpn/putaway-active";
	}

	@GetMapping("/putawayLpnToActiveSys")
	public String putawayLpnToActiveSysAlias() {
		return "redirect:/lpn/putaway-active-sys";
	}

	@GetMapping("/legacy/putawayLpnToActiveSys")
	public String putawayLpnToActiveSys() {
		return "redirect:/lpn/putaway-active-sys";
	}

	@PostMapping("/checkActiveInventory")
	@ResponseBody
	public String checkActiveInventoryAlias(@Valid @ModelAttribute MenuCheckActiveInventoryRequestDTO requestDTO,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "";
		}
		return handleLegacyCheckActiveInventory(requestDTO);
	}

	@PostMapping("/legacy/checkActiveInventory")
	@ResponseBody
	public String checkActiveInventory(@Valid @ModelAttribute MenuCheckActiveInventoryRequestDTO requestDTO,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "";
		}
		return handleLegacyCheckActiveInventory(requestDTO);
	}

	@PostMapping("/locateLpnToActive")
	public String locateLpnToActiveAlias(Model model, @Valid @ModelAttribute MenuActiveLpnRequestDTO requestDTO,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("responseMessage", "Invalid active location data");
			return AppConstants.View.PUTAWAY_ACTIVE;
		}
		return handleLegacyPutawayActive(model, requestDTO);
	}

	@PostMapping("/legacy/locateLpnToActive")
	public String putawayLpnToActive(Model model, @Valid @ModelAttribute MenuActiveLpnRequestDTO requestDTO,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("responseMessage", "Invalid active location data");
			return AppConstants.View.PUTAWAY_ACTIVE;
		}
		return handleLegacyPutawayActive(model, requestDTO);
	}

	@PostMapping("/signIn")
	public String signIn() {
		return "forward:/api/auth/signIn";
	}

	@GetMapping("/logout")
	public String logout() {
		return "redirect:/api/auth/logout";

	}

	@GetMapping("/settings")
	public String settings() {
		return "redirect:/settings";
	}

	@GetMapping("/userlist")
	public String userListAlias(Model model) {
		return handleLegacyUserList(model);
	}

	@GetMapping("/legacy/userlist")
	public String userList(Model model) { // Removed unused parameters
		return handleLegacyUserList(model);
	}

	@PostMapping("/userAdd")
	public String userAddAlias(Model model, @Valid @ModelAttribute MenuUserAddRequestDTO requestDTO,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("responseMessage", "Invalid user data");
			return AppConstants.View.USER_LIST;
		}
		return handleLegacyUserAdd(model, requestDTO);
	}

	@PostMapping("/legacy/userAdd")
	public String userAdd(Model model, @Valid @ModelAttribute MenuUserAddRequestDTO requestDTO,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("responseMessage", "Invalid user data");
			return AppConstants.View.USER_LIST;
		}
		return handleLegacyUserAdd(model, requestDTO);
	}

	@GetMapping("/legacy/menulist")
	public String menuList() {
		return "redirect:/api/menulist";
	}

	@GetMapping("/sopConfig")
	public String sopConfigAlias(Model model) {
		return handleLegacySopConfig(model);
	}

	@GetMapping("/legacy/sopConfig")
	public String sopConfig(Model model) {
		return handleLegacySopConfig(model);
	}

	@PostMapping("/sop/runBatch")
	public String runBatchAlias(@Valid @ModelAttribute MenuBatchRequestDTO requestDTO,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("responseMessage", "Invalid batch request");
			return AppConstants.View.SOP_CONFIG;
		}
		return handleLegacyRunBatch(requestDTO, model);
	}

	@PostMapping("/legacy/sop/runBatch")
	public String runBatch(@Valid @ModelAttribute MenuBatchRequestDTO requestDTO,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("responseMessage", "Invalid batch request");
			return AppConstants.View.SOP_CONFIG;
		}
		return handleLegacyRunBatch(requestDTO, model);
	}

	@PostMapping("/sop/location-range/add")
	public String addLocationRangeAlias(@Valid @ModelAttribute MenuLocationRangeRequestDTO requestDTO,
			BindingResult bindingResult, Model model, HttpSession httpSession) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("activeTab", requestDTO.getActiveTab());
			model.addAttribute("locationRangeAddresponseMessage", "Invalid location range data");
			return AppConstants.View.SOP_CONFIG;
		}
		return handleLegacyAddLocationRange(requestDTO, model, httpSession);
	}

	@PostMapping("/legacy/sop/location-range/add")
	public String addLocationRange(@Valid @ModelAttribute MenuLocationRangeRequestDTO requestDTO,
			BindingResult bindingResult, Model model, HttpSession httpSession) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("activeTab", requestDTO.getActiveTab());
			model.addAttribute("locationRangeAddresponseMessage", "Invalid location range data");
			return AppConstants.View.SOP_CONFIG;
		}
		return handleLegacyAddLocationRange(requestDTO, model, httpSession);
	}

	@PostMapping("/sop/location-range/update")
	public String updatecationRangeAlias(@Valid @ModelAttribute MenuLocationRangeRequestDTO requestDTO,
			BindingResult bindingResult, Model model, HttpSession httpSession) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("activeTab", requestDTO.getActiveTab());
			model.addAttribute("locationRangeUpdateresponseMessage", "Invalid location range data");
			return AppConstants.View.SOP_CONFIG;
		}
		return handleLegacyUpdateLocationRange(requestDTO, model, httpSession);
	}

	@PostMapping("/legacy/sop/location-range/update")
	public String updatecationRange(@Valid @ModelAttribute MenuLocationRangeRequestDTO requestDTO,
			BindingResult bindingResult, Model model, HttpSession httpSession) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("activeTab", requestDTO.getActiveTab());
			model.addAttribute("locationRangeUpdateresponseMessage", "Invalid location range data");
			return AppConstants.View.SOP_CONFIG;
		}
		return handleLegacyUpdateLocationRange(requestDTO, model, httpSession);
	}

	@PostMapping("/sop/getEligibleUpcsForSop/{category}")
	public String getEligibleUpcsForSopAlias(@PathVariable String category,
			@Valid @ModelAttribute MenuEligibleUpcsRequestDTO requestDTO,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("activeTab", requestDTO.getActiveTab());
			return AppConstants.View.SOP_CONFIG;
		}
		return handleLegacyEligibleUpcs(requestDTO, model);
	}

	@PostMapping("/legacy/sop/getEligibleUpcsForSop/{category}")
	public String getEligibleUpcsForSop(@Valid @ModelAttribute MenuEligibleUpcsRequestDTO requestDTO,
			BindingResult bindingResult, Model model, HttpSession httpSession) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("activeTab", requestDTO.getActiveTab());
			return AppConstants.View.SOP_CONFIG;
		}
		return handleLegacyEligibleUpcs(requestDTO, model);
	}

	@GetMapping("/cubiscanLog")
	public String cubiscanLogAlias(Model model) {
		return handleLegacyCubiscanLog(model);
	}

	@GetMapping("/legacy/cubiscanLog")
	public String cubiscanLog(Model model) {
		return handleLegacyCubiscanLog(model);
	}

	@GetMapping("/endpoint")
	public String endpointAlias() {
		return endpoint();
	}

	@GetMapping("/legacy/endpoint")
	public String endpoint() {
		logger.info("endpoint");
		return AppConstants.View.ENDPOINT;
	}

	private List<SopLocationRangeDto> populateSopConfigModel(Model model) throws IOException {
		List<SopLocationRangeDto> sopLocationRangeDtos = menuService.getLocationRanges();
		List<Category> categories = menuService.getCategories();
		List<SopActionTypeDto> sopActionTypeDtos = menuService.getSopActionTypes();
		logger.info("Location Range : {}", sopLocationRangeDtos);
		logger.info("Action Type : {}", sopActionTypeDtos);
		logger.info("Categories : {}", categories);

		model.addAttribute("locationRanges", sopLocationRangeDtos);
		model.addAttribute("actionTypes", sopActionTypeDtos);
		model.addAttribute("categories", categories);
		return sopLocationRangeDtos;
	}

	private String handleLegacyNewLpn(Model model, MenuLpnCreateRequestDTO requestDTO) {
		logger.info("New Lpn data : {}, {}, {}", requestDTO.getLpn_name(), requestDTO.getItem_name(),
				requestDTO.getQuantity());
		try {
			menuService.newLpn(requestDTO.getLpn_name(), requestDTO.getItem_name(), requestDTO.getQuantity());
			model.addAttribute("message", "Lpn Created Successfully");
			return AppConstants.View.CREATE_LPN;
		} catch (IOException e) {
			logger.error("IOException occurred while creating LPN", e);
			return AppConstants.View.CREATE_LPN;
		}
	}

	private String handleLegacyCategoryAdd(String categoryName) {
		try {
			logger.info("Creating legacy category: {}", categoryName);
			menuService.categoryAdd(categoryName);
			return AppConstants.Redirect.CATEGORY_INFO;
		} catch (IOException exception) {
			logger.error("Failed to create category: {}", categoryName, exception);
			return AppConstants.View.CATEGORY;
		}
	}

	private ResponseEntity<?> handleLegacyCategoryEdit(String categoryName, CategoryRequestDTO requestDTO) {
		try {
			logger.info("Updating legacy category: {}", categoryName);
			menuService.categoryEdit(categoryName, requestDTO.getCategoryName());
			return ResponseEntity.ok("Category updated successfully");
		} catch (Exception exception) {
			logger.error("Failed to update category: {}", categoryName, exception);
			return ResponseEntity.badRequest().body("Error updating category: " + exception.getMessage());
		}
	}

	private String handleLegacyCategoryDelete(String categoryName) {
		try {
			logger.info("Deleting legacy category by name: {}", categoryName);
			menuService.categoryDelete(categoryName);
			return AppConstants.Redirect.CATEGORY_INFO;
		} catch (IOException exception) {
			logger.error("Failed to delete category: {}", categoryName, exception);
			return AppConstants.View.CATEGORY;
		}
	}

	private String handleLegacyDeleteCategory(int categoryId) {
		try {
			logger.info("Deleting legacy category by id: {}", categoryId);
			menuService.deleteCategory(categoryId);
			return AppConstants.Redirect.CATEGORY_INFO;
		} catch (IOException exception) {
			logger.error("Failed to delete category id: {}", categoryId, exception);
			return AppConstants.View.CATEGORY;
		}
	}

	private String handleLegacyItemAdd(Model model, ItemRequestDTO requestDTO) {
		try {
			logger.info("Creating legacy item: {}", requestDTO.getDescription());
			menuService.itemAdd(requestDTO.getDescription(), requestDTO.getCategory(), requestDTO.getLength(),
					requestDTO.getWidth(), requestDTO.getHeight());
			return AppConstants.Redirect.ITEM_INFO;
		} catch (IOException exception) {
			logger.error("Failed to create item: {}", requestDTO.getDescription(), exception);
			return AppConstants.View.ITEM;
		}
	}

	private String handleLegacyItemEdit(Model model, ItemRequestDTO requestDTO) {
		if (requestDTO.getItemId() == null || requestDTO.getItemId() <= 0) {
			model.addAttribute("error", "Item ID is required for update");
			return AppConstants.View.ITEM;
		}

		try {
			logger.info("Updating legacy item: {}", requestDTO.getItemId());
			menuService.itemEdit(requestDTO.getItemId(), requestDTO.getDescription(), requestDTO.getCategory(),
					requestDTO.getLength(), requestDTO.getWidth(), requestDTO.getHeight());
			return AppConstants.Redirect.ITEM_INFO;
		} catch (IOException exception) {
			logger.error("Failed to update item: {}", requestDTO.getItemId(), exception);
			return AppConstants.View.ITEM;
		}
	}

	private String handleLegacyDeleteItem(int itemId) {
		try {
			logger.info("Deleting legacy item: {}", itemId);
			menuService.deleteItem(itemId);
			return AppConstants.Redirect.ITEM_INFO;
		} catch (IOException exception) {
			logger.error("Failed to delete item: {}", itemId, exception);
			return AppConstants.View.ITEM;
		}
	}

	private String handleLegacySopConfig(Model model) {
		logger.info("sop-config");
		try {
			List<SopLocationRangeDto> sopLocationRangeDtos = populateSopConfigModel(model);

			Map<Integer, Boolean> selectedRanges = new java.util.HashMap<>();
			for (SopLocationRangeDto range : sopLocationRangeDtos) {
				selectedRanges.put(range.getId(), false);
			}
			logger.info("selectedRanges : {}", selectedRanges);
			model.addAttribute("selectedRanges", selectedRanges);
			return AppConstants.View.SOP_CONFIG;
		} catch (Exception e) {
			logger.error("Failed to load SOP config", e);
			return AppConstants.View.SOP_CONFIG;
		}
	}

	private String handleLegacyRunBatch(MenuBatchRequestDTO requestDTO, Model model) {
		try {
			logger.info("Run Batch for Action Type : {} and Category : {}", requestDTO.getActionType(),
					requestDTO.getCategory_name());
			populateSopConfigModel(model);

			if (AppConstants.SopBatch.ACTION_ASSIGN.equals(requestDTO.getActionType())) {
				String batchAssign = AppConstants.SopBatch.JOB_ASSIGN;
				menuService.assignBatch(requestDTO.getActionType(), batchAssign, requestDTO.getCategory_name());
				logger.info("Assign Batch triggered for category : {}", requestDTO.getCategory_name());
				model.addAttribute("responseMessage", requestDTO.getActionType() + "Batch Submitted Successfully!!");
				return AppConstants.View.SOP_CONFIG;
			}

			if (AppConstants.SopBatch.ACTION_UNASSIGN.equals(requestDTO.getActionType())) {
				String batchAssign = AppConstants.SopBatch.JOB_UNASSIGN;
				menuService.unassignBatch(requestDTO.getActionType(), batchAssign, requestDTO.getCategory_name());
				logger.info("Unassign Batch triggered for category : {}", requestDTO.getCategory_name());
				model.addAttribute("responseMessage", requestDTO.getActionType() + "Batch Submitted Successfully!!");
				return AppConstants.View.SOP_CONFIG;
			}
		} catch (Exception e) {
			logger.error("Failed to run SOP batch", e);
		}
		model.addAttribute("activeTab", requestDTO.getActiveTab());
		return AppConstants.View.SOP_CONFIG;
	}

	private String handleLegacyAddLocationRange(MenuLocationRangeRequestDTO requestDTO, Model model,
			HttpSession httpSession) {
		String decodedToken = SessionUtil.getSessionToken(httpSession);
		try {
			String username = menuAccessService.getUserName(decodedToken);
			populateSopConfigModel(model);
			logger.info("Add Location Range for Action Type : {} and Category : {}", requestDTO.getActionType(),
					requestDTO.getCategory());

			String locationRangeAddresponseMessage = menuService.addLocationRange(requestDTO.getActionType(),
					requestDTO.getCategory(), requestDTO.getFromLocation(), requestDTO.getToLocation(),
					requestDTO.getIsActive(), username);

			model.addAttribute("locationRangeAddresponseMessage", locationRangeAddresponseMessage);
		} catch (Exception e) {
			logger.error("Failed to add location range", e);
		}
		model.addAttribute("activeTab", requestDTO.getActiveTab());
		return AppConstants.View.SOP_CONFIG;
	}

	private String handleLegacyUpdateLocationRange(MenuLocationRangeRequestDTO requestDTO, Model model,
			HttpSession httpSession) {
		String decodedToken = SessionUtil.getSessionToken(httpSession);
		try {
			String username = menuAccessService.getUserName(decodedToken);
			populateSopConfigModel(model);
			logger.info("Add Location Range for Action Type : {} and Category : {}", requestDTO.getActionType(),
					requestDTO.getCategory());

			String locationRangeUpdateresponseMessage = menuService.updateLocationRange(requestDTO.getId(),
					requestDTO.getActionType(), requestDTO.getCategory(), requestDTO.getFromLocation(),
					requestDTO.getToLocation(), requestDTO.getIsActive(), username);
			logger.info("locationRangeUpdateresponseMessage : {}", locationRangeUpdateresponseMessage);
			model.addAttribute("locationRangeUpdateresponseMessage", locationRangeUpdateresponseMessage);
		} catch (Exception e) {
			logger.error("Failed to update location range", e);
		}
		model.addAttribute("activeTab", requestDTO.getActiveTab());
		return AppConstants.View.SOP_CONFIG;
	}

	private String handleLegacyEligibleUpcs(MenuEligibleUpcsRequestDTO requestDTO, Model model) {
		try {
			logger.info("Eligible UPCs For Category : {}", requestDTO.getCategory());
			populateSopConfigModel(model);

			if (requestDTO.getCategory() != null) {
				List<String> eligibleUpcsForSop = menuService.getEligibleUpcsForSop(requestDTO.getCategory());
				model.addAttribute("eligibleUpcsForSop", eligibleUpcsForSop);
				return AppConstants.View.SOP_CONFIG;
			}

			logger.info("Unable to fetch eligibleUpcsForSop of category : {}", requestDTO.getCategory());
		} catch (Exception e) {
			logger.error("Failed to fetch eligible UPCs for SOP", e);
		}
		model.addAttribute("activeTab", requestDTO.getActiveTab());
		return AppConstants.View.SOP_CONFIG;
	}

	private String handleLegacyLocationAdd(Model model, MenuLocationRequestDTO requestDTO) {
		logger.info("New Location to Add : {}", requestDTO.getLocn_brcd());
		try {
			String responseMessage = menuService.locationAdd(requestDTO.getLocn_brcd(), requestDTO.getGrp(),
					requestDTO.getLocn_class(), requestDTO.getLength(), requestDTO.getWidth(), requestDTO.getHeight(),
					requestDTO.getMax_vol(), requestDTO.getMax_qty(), requestDTO.getMax_weight());
			model.addAttribute("responseMessage", responseMessage);
			if ("Location already exists".equals(responseMessage)) {
				logger.info("Response Message : {}", responseMessage);
			} else {
				logger.info("Location added : {}", requestDTO.getLocn_brcd());
			}
			return AppConstants.View.LOCATION;
		} catch (IOException e) {
			logger.error("Failed to add location", e);
			return AppConstants.View.LOCATION;
		}
	}

	private String handleLegacyLocationEdit(Model model, MenuLocationRequestDTO requestDTO) {
		logger.info("Location to Edit : {}", requestDTO.getLocn_brcd());
		try {
			ResponseEntity<String> responseMessage = menuService.locationEdit(requestDTO.getLocn_brcd(),
					requestDTO.getGrp(), requestDTO.getLocn_class(), requestDTO.getLength(), requestDTO.getWidth(),
					requestDTO.getHeight(), requestDTO.getMax_vol(), requestDTO.getMax_qty(),
					requestDTO.getMax_weight());
			model.addAttribute("responseMessage", responseMessage.getBody());
			logger.info("Response Message : {}", responseMessage.getBody());
			return AppConstants.View.LOCATION;
		} catch (IOException e) {
			logger.error("Failed to edit location", e);
			return AppConstants.View.LOCATION;
		}
	}

	private String handleLegacyPutawayReserve(Model model, MenuReserveLpnRequestDTO requestDTO) {
		logger.info("Location LPN : {} to {} Reserve location ", requestDTO.getLpn_name(), requestDTO.getResv_locn());
		try {
			String response = menuService.locateLpnToResv(requestDTO.getLpn_name(), requestDTO.getResv_locn());
			model.addAttribute("responseMessage", response);
			return AppConstants.View.PUTAWAY_RESERVE;
		} catch (IOException e) {
			logger.error("Failed to putaway LPN to reserve", e);
			return AppConstants.View.PUTAWAY_RESERVE;
		}
	}

	private String handleLegacyPutawayActive(Model model, MenuActiveLpnRequestDTO requestDTO) {
		logger.info("Location LPN : {} to {} Active location ", requestDTO.getLpn_name(), requestDTO.getActive_locn());
		try {
			String response = menuService.locateLpnToActive(requestDTO.getLpn_name(), requestDTO.getActive_locn());
			model.addAttribute("responseMessage", response);
			return AppConstants.View.PUTAWAY_ACTIVE;
		} catch (IOException e) {
			logger.error("Failed to putaway LPN to active", e);
			return AppConstants.View.PUTAWAY_ACTIVE;
		}
	}

	private String handleLegacyDeleteLocation(String locn_brcd) {
		logger.info("Delete Existing Item : {}", locn_brcd);
		try {
			menuService.deleteLocation(locn_brcd);
			logger.info("Location deleted successfully : {}", locn_brcd);
			return AppConstants.View.LOCATION;
		} catch (IOException e) {
			logger.error("Failed to delete location", e);
			return AppConstants.View.LOCATION;
		}
	}

	private String handleLegacyLpnEdit(MenuLpnEditRequestDTO requestDTO) {
		logger.info("Lpn to Edit : {}", requestDTO.getLpn_name());
		try {
			String responseMessage = menuService.lpnEdit(requestDTO.getLpn_name(), requestDTO.getItem_desc(),
					requestDTO.getLength(), requestDTO.getWidth(), requestDTO.getHeight(), requestDTO.getQuantity(),
					requestDTO.getAdjustQty(), requestDTO.getLpn_facility_status(), requestDTO.getVolume());
			if ("Lpn already exists".equals(responseMessage)) {
				logger.info("Response Message : {}", responseMessage);
			} else {
				logger.info("Lpn Edit : {}", requestDTO.getLpn_name());
			}
			return AppConstants.View.LPN;
		} catch (IOException e) {
			logger.error("Failed to edit LPN", e);
			return AppConstants.View.LPN;
		}
	}

	private String handleLegacyCheckActiveInventory(MenuCheckActiveInventoryRequestDTO requestDTO) {
		try {
			logger.info("LPN : {}", requestDTO.getLpn_name());
			String response = menuService.checkActiveInventory(requestDTO.getLpn_name());
			logger.info("Active Location : {}", response);
			return response;
		} catch (ClientProtocolException e) {
			logger.error("Client protocol error while checking active inventory", e);
			return "";
		} catch (IOException e) {
			logger.error("I/O error while checking active inventory", e);
			return "";
		}
	}

	private String handleLegacyUserList(Model model) {
		logger.info("Accessing userlist");
		try {
			List<UserDto> userDtos = menuAccessService.getUsers();
			model.addAttribute("users", userDtos);
			return AppConstants.View.USER_LIST;
		} catch (IOException e) {
			logger.error("Failed to fetch users", e);
			model.addAttribute("errorMessage", "Unable to load the user list at this time.");
			return AppConstants.View.ERROR;
		}
	}

	private String handleLegacyUserAdd(Model model, MenuUserAddRequestDTO requestDTO) {
		logger.info("New User to Add : {}", requestDTO.getUsername());
		String response = menuService.userAdd(requestDTO.getFirstname(), requestDTO.getMiddlename(),
				requestDTO.getLastname(), requestDTO.getUsername(), requestDTO.getPassword(), requestDTO.getEmail());
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setResponseMessage(response);
		model.addAttribute("responseMessage", responseMessage.getResponseMessage());
		logger.info("Response Message : {}", responseMessage);
		return AppConstants.View.USER_LIST;
	}

	private String handleLegacyCubiscanLog(Model model) {
		logger.info("cubiscan-log");
		try {
			List<CubiscanLog> cubiscanLogs = menuService.getCubiscanLogs();
			logger.info("{}", cubiscanLogs);
			model.addAttribute("cubiscanLogs", cubiscanLogs);
			return AppConstants.View.CUBISCAN_LOG;
		} catch (Exception e) {
			logger.error("Failed to fetch cubiscan logs", e);
			model.addAttribute("responseMessage", "An error occured, Please check with administrator");
			return AppConstants.View.CUBISCAN_LOG;
		}
	}

}
