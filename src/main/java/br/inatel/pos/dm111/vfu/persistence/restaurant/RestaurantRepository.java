package br.inatel.pos.dm111.vfu.persistence.restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {

    List<Restaurant> getAll();

    Optional<Restaurant> getById(String id);

    Optional<Restaurant> getByUserId(String userId);

    Restaurant save(Restaurant restaurant);

    void delete(String id);
}
