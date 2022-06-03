package com.github.kriaktus.restaurantvoting.web;

import com.github.kriaktus.restaurantvoting.repository.MenuItemRepository;
import com.github.kriaktus.restaurantvoting.repository.MenuRepository;
import com.github.kriaktus.restaurantvoting.test_data.MenuItemTestData;
import com.github.kriaktus.restaurantvoting.test_data.UserTestData;
import com.github.kriaktus.restaurantvoting.to.MenuItemTo;
import com.github.kriaktus.restaurantvoting.util.JsonUtil;
import com.github.kriaktus.restaurantvoting.util.MenuItemUtil;
import com.github.kriaktus.restaurantvoting.util.MenuUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static com.github.kriaktus.restaurantvoting.test_data.MenuItemTestData.*;
import static com.github.kriaktus.restaurantvoting.test_data.RestaurantTestData.RESTAURANT1_ID;
import static com.github.kriaktus.restaurantvoting.test_data.RestaurantTestData.RESTAURANT4_ID;
import static com.github.kriaktus.restaurantvoting.test_data.UserTestData.NOT_FOUND;
import static com.github.kriaktus.restaurantvoting.web.menuitem.AdminMenuItemController.REST_URL;

public class AdminMenuItemControllerTest extends AbstractControllerTest {
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void getFromActualMenu() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", RESTAURANT1_ID, MENU_ITEM_TO_1_3_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_ITEM_TO_MATCHER.contentJson(menuItemTo1_3));
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void getFromActualMenuNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", RESTAURANT1_ID, NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    void getFromActualMenuUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", RESTAURANT1_ID, MENU_ITEM_TO_1_3_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getFromActualMenuForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", RESTAURANT1_ID, MENU_ITEM_TO_1_3_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void createWithLocationToActualMenu() throws Exception {
        MenuItemTo expected = getNewMenuItemTo();
        ResultActions resultActions = perform(MockMvcRequestBuilders.post(REST_URL, RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getNewMenuItemTo())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
        MenuItemTo actual = MENU_ITEM_TO_MATCHER.readFromJson(resultActions);
        Integer id = actual.getId();
        expected.setId(id);
        MENU_ITEM_TO_MATCHER.assertMatch(actual, expected);
        MENU_ITEM_TO_MATCHER.assertMatch(MenuItemUtil.toMenuItemTo(menuItemRepository.findFromActiveMenuByIdAndRestaurantId(id, RESTAURANT1_ID).orElseThrow()), expected);
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void createWithLocationToActualMenuDuplicateName() throws Exception {
        MenuItemTo duplicateNameMenuItemTo = getDuplicateNameMenuItemTo();
        perform(MockMvcRequestBuilders.post(REST_URL, RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicateNameMenuItemTo)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void createWithLocationToActualMenuRestaurantNotFound() throws Exception {
        MenuItemTo menuItemTo = getNewMenuItemTo();
        perform(MockMvcRequestBuilders.post(REST_URL, NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menuItemTo)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void createWithLocationToActualMenuActiveMenuNotExist() throws Exception {
        MenuItemTo menuItemTo = getNewMenuItemTo();
        perform(MockMvcRequestBuilders.post(REST_URL, RESTAURANT4_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menuItemTo)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void updateInActualMenu() throws Exception {
        MenuItemTo expected = getUpdatedMenuItemTo();
        int id = expected.getId();
        perform(MockMvcRequestBuilders.put(REST_URL + "/{id}", RESTAURANT1_ID, MENU_ITEM_TO_1_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedMenuItemTo())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        menuItemRepository.findFromActiveMenuByIdAndRestaurantId(id, RESTAURANT1_ID);
        MENU_ITEM_TO_MATCHER.assertMatch(MenuItemUtil.toMenuItemTo(menuItemRepository.findFromActiveMenuByIdAndRestaurantId(id, RESTAURANT1_ID).orElseThrow()), expected);
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void updateInActualMenuDuplicateName() throws Exception {
        MenuItemTo duplicateTitleMenuItemTo = getDuplicateNameMenuItemTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "/{id}", RESTAURANT1_ID, MENU_ITEM_TO_1_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicateTitleMenuItemTo)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void deleteFromActualMenu() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "/{id}", RESTAURANT1_ID, MenuItemTestData.MENU_ITEM_TO_1_1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        MENU_ITEM_TO_MATCHER.assertMatch(MenuUtil.toMenuTo(menuRepository.findByDateAndRestaurantId(LocalDate.now(), RESTAURANT1_ID).orElseThrow()).getItems(),
                menuItemTo1_2, menuItemTo1_3, menuItemTo1_4);

    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void deleteFromActualMenuNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "/{id}", RESTAURANT1_ID, NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }
}
