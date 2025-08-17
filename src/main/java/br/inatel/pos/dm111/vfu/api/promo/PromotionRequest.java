package br.inatel.pos.dm111.vfu.api.promo;

public record PromotionRequest(String name, String description, String restaurantId, PromotionalProductRequest product)
{
}