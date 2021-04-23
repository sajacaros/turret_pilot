package com.bnpinnovation.turret;

import com.bnpinnovation.turret.dto.SecurityMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class UserAccessTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @DisplayName("1. user로 user페이지 접근 가능")
    @Test
    @WithMockUser(username = "user1", password = "1234", roles = {"USER"})
    void test_user_can_access_userpage() throws Exception{
        String resp = mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        SecurityMessage message = mapper.readValue(resp, SecurityMessage.class);
        assertEquals("user page", message.getMessage());
    }

    @DisplayName("2. user로 admin페이지 접근 불가")
    @Test
    @WithMockUser(username = "user1", password = "1234", roles = {"USER"})
    void test_user_cannot_access_adminpage() throws Exception{
       mockMvc.perform(get("/admin"))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("3. admin으로 admin페이지 접근 가능")
    @Test
    @WithMockUser(username = "admin", password = "12345", roles = {"ADMIN"})
    void test_admin_can_access_adminpage() throws Exception{
        String resp = mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        SecurityMessage message = mapper.readValue(resp, SecurityMessage.class);
        assertEquals("admin page", message.getMessage());
    }

    @DisplayName("3. admin으로 user페이지 접근 가능")
    @Test
    @WithMockUser(username = "admin", password = "12345", roles = {"ADMIN"})
    void test_admin_can_access_userpage() throws Exception{
        String resp = mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        SecurityMessage message = mapper.readValue(resp, SecurityMessage.class);
        assertEquals("user page", message.getMessage());
    }
}
