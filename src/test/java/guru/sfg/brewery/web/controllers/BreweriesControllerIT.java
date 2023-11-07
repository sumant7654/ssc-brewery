package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BreweriesControllerIT extends BaseIT{

    @WithMockUser(username="scott", password = "tiger", roles = {"CUSTOMER"})
    @Test
    public void breweriesTest() throws Exception {
        mockMvc.perform(get("/brewery/breweries/"))
                .andExpect(status().isOk());
    }

    @WithMockUser(username="sumantakumar", password = "sumantakumar", roles = {"ADMIN"})
    @Test
    public void breweriesTestAdmin() throws Exception {
        mockMvc.perform(get("/brewery/breweries/"))
                .andExpect(status().is2xxSuccessful());
    }

    @WithMockUser(username = "scott", password = "tiger", roles = {"CUSTOMER"})
    @Test
    public void breweries() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries"))
                .andExpect(status().isOk());
    }

    @Test
    public void breweriesNoAuth() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user", password = "password", roles = {"USER"})
    @Test
    public void breweriesRoleUser() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries"))
                .andExpect(status().isForbidden());
    }
    @WithMockUser(username = "sumantakumar", password = "sumantakumar", roles = {"ADMIN"})
    @Test
    public void breweriesRoleAdmin() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries"))
                .andExpect(status().is2xxSuccessful());
    }

}
