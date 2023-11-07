package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.security.User;
import guru.sfg.brewery.security.params.BeerOrderCreatePermission;
import guru.sfg.brewery.security.params.BeerOrderPickupPermission;
import guru.sfg.brewery.security.params.BeerOrderReadPermission;
import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;


@RestController
@RequestMapping("/api/v2/orders/")
@Slf4j
public class BeerOrderControllerV2 {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private BeerOrderService beerOrderService;

    @Autowired
    public void setBeerOrderService(BeerOrderService beerOrderService) {
        this.beerOrderService = beerOrderService;
    }

    @BeerOrderReadPermission
    @GetMapping("orders")
    public BeerOrderPagedList listOrders(@AuthenticationPrincipal User user,
                                         @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (user.getCustomer() != null) {
            return beerOrderService.listOrders(user.getCustomer().getId(), PageRequest.of(pageNumber, pageSize));
        } else {
            return beerOrderService.listOrders(PageRequest.of(pageNumber, pageSize));
        }

    }

    @BeerOrderCreatePermission
    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    public BeerOrderDto placeOrder(@PathVariable("customerId") UUID customerId, @RequestBody BeerOrderDto beerOrderDto) {
        return beerOrderService.placeOrder(customerId, beerOrderDto);
    }

    @BeerOrderReadPermission
    @GetMapping("orders/{orderId}")
    public BeerOrderDto getOrder(@PathVariable("orderId") UUID orderId) {

        BeerOrderDto beerOrderDto = beerOrderService.getOrderById(orderId);
        if(beerOrderDto == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order Not Found");
        }else{
            log.debug("Found Order : "+beerOrderDto);
            return beerOrderDto;
        }
    }

    @BeerOrderPickupPermission
    @PutMapping("orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickUpOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId) {
        beerOrderService.pickupOrder(customerId, orderId);
    }
}
