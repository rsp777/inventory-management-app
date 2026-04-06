package com.pawar.inventory.app.config;

public final class AppConstants {

	private AppConstants() {
	}

	public static final class MenuType {
		public static final String RF = "RF";
		public static final String UI = "UI";
		public static final String AUTH = "AUTH";
		public static final String PARENT = "PARENT";
		public static final String PARENT_UI = "PARENT_UI";
		public static final String CHILD = "CHILD";
		public static final String CHILD_UI = "CHILD_UI";

		private MenuType() {
		}
	}

	public static final class SopBatch {
		public static final String ACTION_ASSIGN = "ASSIGN";
		public static final String ACTION_UNASSIGN = "UNASSIGN";
		public static final String JOB_ASSIGN = "BATCHTIMEASSIGN";
		public static final String JOB_UNASSIGN = "BATCHTIMEUNASSIGN";

		private SopBatch() {
		}
	}

	public static final class ExternalApi {
		public static final String DEFAULT_URL = "http://localhost:8085";
		public static final int DEFAULT_TIMEOUT = 30000;
		public static final int DEFAULT_RETRY_ATTEMPTS = 3;

		private ExternalApi() {
		}
	}

	public static final class SessionAttribute {
		public static final String DECODED_TOKEN = "decodedtoken";
		public static final String JWT_TOKEN = "secretKey";
		public static final String USER_NAME = "user_name";
		public static final String USER_NAME_LEGACY = "userName";

		private SessionAttribute() {
		}
	}

	public static final class MenuEndpoint {
		public static final String CREATE_LPN = "CreateLpn";
		public static final String UPDATE_LPN = "UpdateLpn";
		public static final String GET_LPNS = "GetLpns";
		public static final String VALIDATE_LPN = "validateLpn";
		public static final String LOCATE_LPN_TO_RESERVE = "locateLpnToResv";
		public static final String LOCATE_LPN_TO_ACTIVE = "locateLpnToActive";
		public static final String GET_USERS = "getUsers";
		public static final String GET_ITEM = "GetItem";
		public static final String GET_ALL_CATEGORIES = "GetAllCategories";
		public static final String ADD_CATEGORY = "AddCategory";
		public static final String VALIDATE_CATEGORY = "ValidateCategory";
		public static final String EDIT_CATEGORY = "EditCategory";
		public static final String DELETE_CATEGORY = "DeleteCategory";
		public static final String DELETE_CATEGORY_BY_ID = "DeleteCategoryById";
		public static final String GET_ITEMS = "GetItems";
		public static final String ADD_ITEM = "AddItem";
		public static final String GET_ITEM_BY_DESC = "GetItemByDesc";
		public static final String GET_ITEM_BY_ID = "GetItemById";
		public static final String DELETE_ITEM_BY_ID = "DeleteItemById";
		public static final String UPDATE_ITEM = "UpdateItem";
		public static final String GET_LOCATIONS = "GetLocations";
		public static final String ADD_LOCATION = "AddLocation";
		public static final String GET_LOCATION_BY_BARCODE = "GetLocationByBarcode";
		public static final String UPDATE_LOCATION_BY_BARCODE = "UpdateLocationByBarcode";
		public static final String DELETE_LOCATION_BY_BARCODE = "DeleteLocationByBarcode";
		public static final String GET_INVENTORIES = "GetInventories";
		public static final String GET_INVENTORY_BY_LPN = "GetInventoryByLpn";
		public static final String CREATE_RESERVE = "CreateReserve";
		public static final String CREATE_ACTIVE = "CreateActive";
		public static final String LOGIN = "Login";
		public static final String SIGNOUT = "Signout";
		public static final String REGISTER = "Register";
		public static final String ASSIGN_ROLE_TO_USER = "AssignRoleToUser";
		public static final String UNASSIGN_ROLE_FROM_USER = "UnassignRoleFromUser";
		public static final String GET_USER_ROLES = "GetUserRoles";
		public static final String DELETE_USER = "DeleteUser";
		public static final String CHECK_ACTIVE_INVENTORY = "CheckActiveInventory";
		public static final String GET_GRPS = "GetGrps";
		public static final String GET_ACTION_TYPES = "GetActionTypes";
		public static final String GET_ACTION_TYPE = "GetActionType";
		public static final String GET_CATEGORIES = "GetCategories";
		public static final String ASSIGN_BATCH = "AssignBatch";
		public static final String LOCATION_RANGE = "Location Range";
		public static final String LOCATION_RANGE_ADD = "Location Range Add";
		public static final String LOCATION_RANGE_UPDATE = "LocationRangeUpdate";
		public static final String GET_ELIGIBLE_UPCS_FOR_SOP_BY_CATEGORY = "GetEligibleUpcsForSopByCategory";
		public static final String UNASSIGN_BATCH = "UnassignBatch";
		public static final String GET_CUBISCAN_LOGS = "GetCubiscanLogs";

		private MenuEndpoint() {
		}
	}
	public static final class Application {
		public static final String AUDIT_SOURCE = "System";
		public static final String AUDIT_SOURCE_SYSTEM = "SYSTEM";

		private Application() {
		}
	}

	public static final class ExternalApiResponse {
		public static final String STATUS_NOT_FOUND = "\"status\":404";
		public static final String STATUS_SERVER_ERROR = "\"status\":500";

		private ExternalApiResponse() {
		}
	}

	public static final class View {
		public static final String ROLES = "roles";
		public static final String PERMISSIONS = "permissions";
		public static final String ROLE_PERMISSIONS = "role-permissions";
		public static final String MENU_ACCESS_MGMT = "menu-access";
		public static final String CATEGORY = "category";
		public static final String ENDPOINT = "endpoint";
		public static final String ITEM = "item";
		public static final String LOCATION = "location";
		public static final String LOCATION_INQUIRY = "locationInquiry";
		public static final String LPN = "lpn";
		public static final String LPN_INQUIRY = "lpnInquiry";
		public static final String CREATE_LPN = "createLpn";
		public static final String SETTINGS = "settings";
		public static final String SOP_CONFIG = "sop-config";
		public static final String INVENTORY = "inventory";
		public static final String INVENTORY_BY_ITEM = "inventoryByItem";
		public static final String INVENTORY_BY_LOCATION = "inventoryByLocation";
		public static final String INVENTORY_BY_LPN = "inventoryByLpn";
		public static final String USER_LIST = "userlist";
		public static final String CUBISCAN_LOG = "cubiscan-log";
		public static final String PUTAWAY_RESERVE = "putawayLpnToReserve";
		public static final String PUTAWAY_ACTIVE = "putawayLpnToActive";
		public static final String PUTAWAY_ACTIVE_SYS = "putawayLpnToActiveSys";
		public static final String ERROR = "error";

		private View() {
		}
	}

	public static final class Redirect {
		public static final String CATEGORY_INFO = "redirect:/api/categoryInfo";
		public static final String ITEM_INFO = "redirect:/api/itemInfo";

		private Redirect() {
		}
	}

}
