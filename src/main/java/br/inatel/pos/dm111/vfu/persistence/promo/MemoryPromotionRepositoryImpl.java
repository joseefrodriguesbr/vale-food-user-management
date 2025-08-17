package br.inatel.pos.dm111.vfu.persistence.promo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class MemoryPromotionRepositoryImpl implements PromotionRepository
{
	private final Map<String, Promotion> db = new HashMap<>();

	@Override
	public List<Promotion> getAll()
	{
		return db.values().stream().toList();
	}

	@Override
	public Optional<Promotion> getById(String id)
	{
		return Optional.ofNullable(db.get(id));
	}

	@Override
	public Promotion save(Promotion promotion)
	{
		db.put(promotion.id(), promotion);
		return promotion; // Retorna o objeto salvo
	}

	@Override
	public void delete(String id)
	{
		db.values().removeIf(promotion -> promotion.id().equals(id));
	}

	@Override
	public List<Promotion> findByRestaurantId(String restaurantId)
	{
		return db.values().stream().filter(promotion -> promotion.restaurantId().equals(restaurantId)).toList();
	}
}