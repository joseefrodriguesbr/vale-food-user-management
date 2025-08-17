package br.inatel.pos.dm111.vfu.api.promo;

public record PromotionResponse(String id, String name, String description, String restaurantId, PromotionalProductResponse product)
{
}