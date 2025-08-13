package br.inatel.pos.dm111.vfu.api.restaurant.controller;

import br.inatel.pos.dm111.vfu.api.core.ApiException;
import br.inatel.pos.dm111.vfu.api.restaurant.RestaurantRequest;
import br.inatel.pos.dm111.vfu.api.restaurant.RestaurantResponse;
import br.inatel.pos.dm111.vfu.api.restaurant.service.RestaurantService;
import br.inatel.pos.dm111.vfu.api.user.UserRequest;
import br.inatel.pos.dm111.vfu.api.user.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/valefood/restaurants")
public class RestaurantController {

    private static final Logger log = LoggerFactory.getLogger(RestaurantController.class);

    private final RestaurantRequestValidator validator;
    private final RestaurantService service;

    public RestaurantController(RestaurantRequestValidator validator, RestaurantService service) {
        this.validator = validator;
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        log.debug("Received request to list all restaurants");

        var response = service.searchRestaurants();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping
    public ResponseEntity<RestaurantResponse> postRestaurant(@RequestBody RestaurantRequest request,
                                                 BindingResult bindingResult) throws ApiException {
        log.debug("Received request to create a new user...");

        //validateUserRequest(request, bindingResult);
        var response = service.createRestaurant(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
