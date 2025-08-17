package br.inatel.pos.dm111.vfu.api.restaurant;

public record ProductRequest(String id, String name, String description, String category, float price)
{
}
