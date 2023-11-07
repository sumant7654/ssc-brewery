package guru.sfg.brewery.web.controllers.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.bootstrap.DefaultBreweryLoader;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderLineDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Disabled
public class BeerOrderControllerTest{//} extends BaseIT {

    public static final String API_ROOT = "/api/v1/customers/";
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BeerOrderRepository beerOrderRepository;

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    Customer stPeteCustomer;
    Customer dunedinCustomer;
    Customer keyWestCustomer;
    List<Beer> loadedBeers;

    @Autowired
    WebApplicationContext wac;
     MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
        stPeteCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.ST_PETE_DISTRIBUTING).orElseThrow();
        dunedinCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.DUNEDIN_DISTRIBUTING).orElseThrow();
        keyWestCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.KEY_WEST_DISTRIBUTORS).orElseThrow();
        loadedBeers = beerRepository.findAll();
    }

    @Test
    void createOrderNoAuth() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform((post(API_ROOT+stPeteCustomer.getId()+"/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(objectMapper.writeValueAsString(beerOrderDto))))
                .andExpect(status().isUnauthorized());

    }

    @Transactional
    @WithUserDetails("sumantakumar")
    @Test
    void createOrderUserAdmin() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());
        mockMvc.perform((post(API_ROOT+stPeteCustomer.getId()+"/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(objectMapper.writeValueAsString(beerOrderDto))))
                .andExpect(status().isCreated());

    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
    @Test
    void createOrderUserAuthCustomer() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());
        mockMvc.perform((post(API_ROOT+stPeteCustomer.getId()+"/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(objectMapper.writeValueAsString(beerOrderDto))))
                .andExpect(status().isCreated());

    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.KEYWEST_USER)
    @Test
    void createOrderUserNOTAuthCustomer() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());
        mockMvc.perform((post(API_ROOT+stPeteCustomer.getId()+"/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(objectMapper.writeValueAsString(beerOrderDto))))
                .andExpect(status().isForbidden());
    }

    @Test
    void listOrderNotAuth() throws Exception {
        mockMvc.perform(get(API_ROOT+stPeteCustomer.getId()+"/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @WithUserDetails(value = "sumantakumar")
    @Test
    void listOrderAdminAuth() throws Exception {
        mockMvc.perform(get(API_ROOT+stPeteCustomer.getId()+"/orders"))
                .andExpect(status().isOk());
    }

    @Transactional
    @WithUserDetails(value = DefaultBreweryLoader.STPETE_USER)
    @Test
    void listOrderCustomerAuth() throws Exception {
        mockMvc.perform(get(API_ROOT+stPeteCustomer.getId()+"/orders"))
                .andExpect(status().isOk());
    }

    @Transactional
    @WithUserDetails(value = DefaultBreweryLoader.DUNEDIN_USER)
    @Test
    void listOrderCustomerNotAuth() throws Exception {
        mockMvc.perform(get(API_ROOT+stPeteCustomer.getId()+"/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listOrderNoAuth() throws Exception {
        mockMvc.perform(get(API_ROOT+stPeteCustomer.getId()))
                .andExpect(status().isUnauthorized());
    }
    @Transactional
    @Test
    void getByOrderIdNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT+stPeteCustomer.getId()+"/orders"+beerOrder.getId()))
                .andExpect(status().isUnauthorized());
    }
    @Transactional
    @WithUserDetails("sumantakumar")
    @Test
    void getByOrderIdAdmin() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT+stPeteCustomer.getId()+"/orders/"+beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }
    @Transactional
    @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
    @Test
    void getByOrderIdCustomerAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT+stPeteCustomer.getId()+"/orders/"+beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }
    @Transactional
    @WithUserDetails(DefaultBreweryLoader.DUNEDIN_USER)
    @Test
    void getByOrderIdCustomerNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT+stPeteCustomer.getId()+"/orders/"+beerOrder.getId()))
                .andExpect(status().isForbidden());
    }



    @Transactional
    @Test
    void pickUpOrderNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findAny().orElseThrow();
        mockMvc.perform(put(API_ROOT+stPeteCustomer.getId()+"/orders"+beerOrder.getId()+"/pickup"))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @WithUserDetails("sumantakumar")
    @Test
    void pickUpOrderNotAdminUser() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findAny().orElseThrow();
        mockMvc.perform(put(API_ROOT+stPeteCustomer.getId()+"/orders/"+beerOrder.getId()+"/pickup"))
                .andExpect(status().isNoContent());
    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
    @Test
    void pickUpOrderCustomerUserAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findAny().orElseThrow();
        mockMvc.perform(put(API_ROOT+stPeteCustomer.getId()+"/orders/"+beerOrder.getId()+"/pickup"))
                .andExpect(status().isNoContent());

    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.DUNEDIN_USER)
    @Test
    void pickUpOrderCustomerUserNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findAny().orElseThrow();
        mockMvc.perform(put(API_ROOT+stPeteCustomer.getId()+"/orders/"+beerOrder.getId()+"/pickup"))
                .andExpect(status().isForbidden());
    }

    private BeerOrderDto buildOrderDto(Customer customer, UUID beerId){
        List<BeerOrderLineDto> orderLines = Arrays.asList(BeerOrderLineDto.builder()
                .id(UUID.randomUUID())
                .beerId(beerId)
                .orderQuantity(4)
                .build());

        return BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef("123")
                .orderStatusCallbackUrl("https://sumantakumar.dev")
                .beerOrderLines(orderLines)
                .build();
    }





}
