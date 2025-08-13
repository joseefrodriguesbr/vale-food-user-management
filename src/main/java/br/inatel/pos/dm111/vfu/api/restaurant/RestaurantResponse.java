package br.inatel.pos.dm111.vfu.api.restaurant;

import java.util.List;

public record RestaurantResponse(String id,
                                 String name,
                                 String address,
                                 String userId,
                                 List<String> categories,
                                 List<ProductResponse> products) {
}
