package br.inatel.pos.dm111.vfu.api.promo;

public record PromotionalProductResponse(String productId, float promotionalPrice, String category, String productName) {
}