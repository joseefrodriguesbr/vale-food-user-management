package br.inatel.pos.dm111.vfu.api.restaurant;

import java.util.List;

public record RestaurantRequest(String id, String name, String address, String userId, List<String> categories, List<ProductRequest> products)
{
}
