package guru.sfg.brewery.web.controllers;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@Disabled
public class CustomerControllerTest extends BaseIT{

    @WithMockUser(username = "sumantakumar", password = "sumantakumar", roles = {"ADMIN"})
    @Test
    public void testListCustomerAUTHAdmin() throws Exception {
        mockMvc.perform(get("/customers").with(csrf()))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "scott", password = "tiger", roles = {"USER"})
    @Test
    public void testListCustomersAUTHUser() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testListCustomersNotLoggedIn() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isUnauthorized());
    }


    @DisplayName("Add Customer")
    @Nested
    class AddCustomer{

        @WithMockUser(username = "sumantakumar", password = "sumantakumar", roles = {"ADMIN"})
        @Rollback
        @Test
        public void processCreationForm() throws Exception {
            mockMvc.perform(post("/customers/new").with(csrf())
                    .param("customerName", "Foo Customer"))
                    .andExpect(status().is3xxRedirection());

        }


        @WithMockUser(username = "scott", password = "tiger", roles = {"USER"})
        @Rollback
        @Test
        public void processCreationAUTH() throws Exception {
            mockMvc.perform(post("/customers/new")
                    .param("customerName", "Doo Customer"))
                    .andExpect(status().isForbidden());
        }

        @Test
        public void processCreationNoAuth() throws Exception {
            mockMvc.perform(post("/customer/new").with(csrf())
                    .param("customerName", "Koo Customer"))
                    .andExpect(status().isUnauthorized());
        }
    }
}