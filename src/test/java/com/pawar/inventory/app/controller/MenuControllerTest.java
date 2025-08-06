package com.pawar.inventory.app.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.pawar.inventory.app.model.Menu;
import com.pawar.inventory.app.service.MenuAccessService;
import com.pawar.inventory.app.service.MenuService;

@ExtendWith(MockitoExtension.class)
public class MenuControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MenuService menuService;

    @Mock
    private MenuAccessService menuAccessService;

    @InjectMocks
    private MenuController menuController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(menuController).build();
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/api/index"))
               .andExpect(status().isOk());
    }

    @Test
    public void testShowMenu() throws Exception {
        List<Menu> menus = Arrays.asList(new Menu());
        when(menuAccessService.getAccessibleMenus(anyString())).thenReturn(menus);

        mockMvc.perform(get("/api/showMenu"))
               .andExpect(status().isOk());
    }

    @Test
    public void testCreateLpn() throws Exception {
        mockMvc.perform(get("/api/createLpn"))
               .andExpect(status().isOk());
    }

    @Test
    public void testNewLpn() throws Exception {
        mockMvc.perform(post("/api/newLpn")
                .param("lpn_name", "LPN123")
                .param("item_name", "ITEM123")
                .param("quantity", "10"))
               .andExpect(status().isOk());
    }

    @Test
    public void testLpnInquiry() throws Exception {
        mockMvc.perform(get("/api/lpnInquiry"))
               .andExpect(status().isOk());
    }

    @Test
    public void testInventoryByLocation() throws Exception {
        mockMvc.perform(get("/api/inventoryByLocation"))
               .andExpect(status().isOk());
    }

    @Test
    public void testInventoryByLpn() throws Exception {
        mockMvc.perform(get("/api/inventoryByLpn"))
               .andExpect(status().isOk());
    }

    @Test
    public void testInventoryByItem() throws Exception {
        mockMvc.perform(get("/api/inventoryByItem"))
               .andExpect(status().isOk());
    }

    @Test
    public void testItemInquiry() throws Exception {
        mockMvc.perform(get("/api/itemInquiry"))
               .andExpect(status().isOk());
    }

    @Test
    public void testItemInfo() throws Exception {
        mockMvc.perform(get("/api/itemInfo"))
               .andExpect(status().isOk());
    }

    @Test
    public void testGetItem() throws Exception {
        mockMvc.perform(get("/api/getItem/ITEM123"))
               .andExpect(status().isOk());
    }

    @Test
    public void testCategoryInfo() throws Exception {
        mockMvc.perform(get("/api/categoryInfo"))
               .andExpect(status().isOk());
    }

    @Test
    public void testCategoryAdd() throws Exception {
        mockMvc.perform(post("/api/categoryAdd/CAT123"))
               .andExpect(status().isOk());
    }

    @Test
    public void testCategoryEdit() throws Exception {
        mockMvc.perform(put("/api/categoryEdit/CAT123")
                .contentType("application/json")
                .content("{}"))
               .andExpect(status().isOk());
    }

    @Test
    public void testCategoryDelete() throws Exception {
        mockMvc.perform(delete("/api/categoryDelete/CAT123"))
               .andExpect(status().isOk());
    }

    @Test
    public void testDeleteCategory() throws Exception {
        mockMvc.perform(delete("/api/deleteCategory/1"))
               .andExpect(status().isOk());
    }

    @Test
    public void testItemAdd() throws Exception {
        mockMvc.perform(post("/api/itemAdd")
                .param("description", "ITEM123")
                .param("category", "CAT123")
                .param("length", "10.0")
                .param("width", "5.0")
                .param("height", "2.0"))
               .andExpect(status().isOk());
    }

    @Test
    public void testItemEdit() throws Exception {
        mockMvc.perform(put("/api/itemEdit")
                .param("description", "ITEM123")
                .param("category", "CAT123")
                .param("length", "10.0")
                .param("width", "5.0")
                .param("height", "2.0"))
               .andExpect(status().isOk());
    }

    @Test
    public void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/api/deleteItem/ITEM123"))
               .andExpect(status().isOk());
    }

    @Test
    public void testLocationInfo() throws Exception {
        mockMvc.perform(get("/api/locationInfo"))
               .andExpect(status().isOk());
    }

    @Test
    public void testLocationAdd() throws Exception {
        mockMvc.perform(post("/api/locationAdd")
                .param("locn_brcd", "LOC123")
                .param("grp", "GRP123")
                .param("locn_class", "CLASS123")
                .param("length", "10.0")
                .param("width", "5.0")
                .param("height", "2.0")
                .param("max_volume", "100.0")
                .param("max_qty", "50")
                .param("max_weight", "200.0"))
               .andExpect(status().isOk());
    }

    @Test
    public void testLocationEdit() throws Exception {
        mockMvc.perform(put("/api/locationEdit")
                .param("locn_brcd", "LOC123")
                .param("grp", "GRP123")
                .param("locn_class", "CLASS123")
                .param("length", "10.0")
                .param("width", "5.0")
                .param("height", "2.0")
                .param("max_volume", "100.0")
                .param("max_qty", "50")
                .param("max_weight", "200.0"))
               .andExpect(status().isOk());
    }

    @Test
    public void testDeleteLocation() throws Exception {
        mockMvc.perform(delete("/api/deleteLocation/LOC123"))
               .andExpect(status().isOk());
    }

    @Test
    public void testLpnInfo() throws Exception {
        mockMvc.perform(get("/api/lpnInfo"))
               .andExpect(status().isOk());
    }

    @Test
    public void testLpnEdit() throws Exception {
        mockMvc.perform(put("/api/lpnEdit")
                .param("lpn_name", "LPN123")
                .param("item_desc", "ITEM123")
                .param("length", "10.0")
                .param("width", "5.0")
                .param("height", "2.0")
                .param("quantity", "10")
                .param("adjustQty", "5")
                .param("lpn_facility_status", "1")
                .param("volume", "100.0"))
               .andExpect(status().isOk());
    }

    @Test
    public void testInventoryInfo() throws Exception {
        mockMvc.perform(get("/api/inventoryInfo"))
               .andExpect(status().isOk());
    }

    @Test
    public void testLocationInquiry() throws Exception {
        mockMvc.perform(get("/api/locationInquiry"))
               .andExpect(status().isOk());
    }

    @Test
    public void testPutawayLpnToReserve() throws Exception {
        mockMvc.perform(get("/api/putawayLpnToReserve"))
               .andExpect(status().isOk());
    }

    @Test
    public void testLocateLpnToReserve() throws Exception {
        mockMvc.perform(post("/api/locateLpnToReserve")
                .param("lpn_name", "LPN123")
                .param("resv_locn", "RESV123"))
               .andExpect(status().isOk());
    }

    @Test
    public void testPutawayLpnToActive() throws Exception {
        mockMvc.perform(get("/api/putawayLpnToActive"))
               .andExpect(status().isOk());
    }

    @Test
    public void testPutawayLpnToActiveSys() throws Exception {
        mockMvc.perform(get("/api/putawayLpnToActiveSys"))
               .andExpect(status().isOk());
    }

    @Test
    public void testCheckActiveInventory() throws Exception {
        mockMvc.perform(post("/api/checkActiveInventory")
                .param("lpn_name", "LPN123"))
               .andExpect(status().isOk());
    }

    @Test
    public void testLocateLpnToActive() throws Exception {
        mockMvc.perform(post("/api/locateLpnToActive")
                .param("lpn_name", "LPN123")
                .param("active_locn", "ACT123"))
               .andExpect(status().isOk());
    }

    @Test
    public void testSignIn() throws Exception {
        mockMvc.perform(post("/api/signIn")
                .param("username", "user123")
                .param("password", "pass123"))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(get("/api/logout"))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testSettings() throws Exception {
        mockMvc.perform(get("/api/settings"))
               .andExpect(status().isOk());
    }

    @Test
    public void testUserList() throws Exception {
        mockMvc.perform(get("/api/userlist"))
               .andExpect(status().isOk());
    }

    @Test
    public void testUserAdd() throws Exception {
        mockMvc.perform(post("/api/userAdd")
                .param("firstname", "John")
                .param("middlename", "Doe")
                .param("lastname", "Smith")
                .param("username", "user123")
                .param("password", "pass123")
                .param("email", "john.doe@example.com"))
               .andExpect(status().isOk());
    }

    @Test
    public void testMenuList() throws Exception {
        mockMvc.perform(get("/api/menulist"))
               .andExpect(status().isOk());
    }
}
