package br.inatel.pos.dm111.vfu.api.restaurant.service;

import br.inatel.pos.dm111.vfu.api.core.ApiException;
import br.inatel.pos.dm111.vfu.api.core.AppErrorCode;
import br.inatel.pos.dm111.vfu.api.restaurant.ProductRequest;
import br.inatel.pos.dm111.vfu.api.restaurant.ProductResponse;
import br.inatel.pos.dm111.vfu.api.restaurant.RestaurantRequest;
import br.inatel.pos.dm111.vfu.api.restaurant.RestaurantResponse;
import br.inatel.pos.dm111.vfu.api.user.UserRequest;
import br.inatel.pos.dm111.vfu.api.user.UserResponse;
import br.inatel.pos.dm111.vfu.persistence.restaurant.Product;
import br.inatel.pos.dm111.vfu.persistence.restaurant.Restaurant;
import br.inatel.pos.dm111.vfu.persistence.restaurant.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RestaurantService {

    private static final Logger log = LoggerFactory.getLogger(RestaurantService.class);

    private final RestaurantRepository repository;

    public RestaurantService(RestaurantRepository repository) {
        this.repository = repository;
    }

    public List<RestaurantResponse> searchRestaurants() {
        var restaurants = repository.getAll();

        return restaurants.stream()
                .map(this::buildRestaurantResponse)
                .toList();
    }

    public RestaurantResponse createRestaurant(RestaurantRequest request) throws ApiException {
        // validate user exist and its type is RESTAURANT


        var restaurant = buildRestaurant(request);
        repository.save(restaurant);
        log.info("Restaurant was successfully created. Id: {}", restaurant.id());

        return buildRestaurantResponse(restaurant);
    }

    private Restaurant buildRestaurant(RestaurantRequest request) {
        var products = request.products().stream()
                .map(this::buildProduct)
                .toList();

        var id = UUID.randomUUID().toString();
        return new Restaurant(id,
                request.name(),
                request.address(),
                request.userId(),
                request.categories(),
                products);
    }

    private Product buildProduct(ProductRequest request) {
        var id = UUID.randomUUID().toString();

        return new Product(id,
                request.name(),
                request.description(),
                request.category(),
                request.price());
    }

    private RestaurantResponse buildRestaurantResponse(Restaurant restaurant) {
        var products = restaurant.products().stream()
                .map(this::buildProductResponse)
                .toList();

        return new RestaurantResponse(restaurant.id(),
                restaurant.name(),
                restaurant.address(),
                restaurant.userId(),
                restaurant.categories(),
                products);
    }

    private ProductResponse buildProductResponse(Product product) {
        return new ProductResponse(product.id(),
                product.name(),
                product.description(),
                product.category(),
                product.price());
    }
}
