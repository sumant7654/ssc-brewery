package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {

    @WithMockUser(username = "scott", password = "tiger", roles = {"USER"}, authorities = {"beer.read"})
    @Test
    public void deleteBeerUserRole() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/65c29f32-8512-4881-a67e-93fef748ff93")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteBeerBadCred() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/65c29f32-8512-4881-a67e-93fef748ff93")
                .header("Api-Key", "sumantakumar")
                .header("Api-Secret", "sumantakumarxxx")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteBeerHttpBasic() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/65c29f32-8512-4881-a67e-93fef748ff93")
                        .header("Api-Key", "sumantakumar")
                        .header("Api-Secret", "sumantakumar"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteBeer() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/65c29f32-8512-4881-a67e-93fef748ff93")
                        .header("Api-Key", "sumantakumar")
                        .header("Api-Secret", "sumantakumar"))
                .andExpect(status().isOk());
    }


    @WithMockUser(username = "sumantakumar", password = "sumantakumar", roles = {"ADMIN"})
    @Test
    public void deleteBeerMockUser() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/198b02e5-7129-447f-9eef-fed99551b134"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "sumantakumar", password = "sumantakumar", roles = {"ADMIN"})
    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beer/"))
                .andExpect(status().isOk());
    }
    @WithMockUser(username = "sumantakumar", password = "sumantakumar", roles = {"ADMIN"})
    @Test
    void findById() throws Exception {
        mockMvc.perform(get("/api/v1/beer/65c29f32-8512-4881-a67e-93fef748ff93"))
                .andExpect(status().isOk());
    }
}
