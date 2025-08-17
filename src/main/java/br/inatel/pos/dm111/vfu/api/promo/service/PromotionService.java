package br.inatel.pos.dm111.vfu.api.promo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.inatel.pos.dm111.vfu.api.core.ApiException;
import br.inatel.pos.dm111.vfu.api.core.AppErrorCode;
import br.inatel.pos.dm111.vfu.api.promo.PromotionRequest;
import br.inatel.pos.dm111.vfu.api.promo.PromotionResponse;
import br.inatel.pos.dm111.vfu.api.promo.PromotionalProductRequest;
import br.inatel.pos.dm111.vfu.api.promo.PromotionalProductResponse;
import br.inatel.pos.dm111.vfu.persistence.promo.Promotion;
import br.inatel.pos.dm111.vfu.persistence.promo.PromotionRepository;
import br.inatel.pos.dm111.vfu.persistence.promo.PromotionalProduct;
import br.inatel.pos.dm111.vfu.persistence.restaurant.Product;
import br.inatel.pos.dm111.vfu.persistence.restaurant.Restaurant;
import br.inatel.pos.dm111.vfu.persistence.restaurant.RestaurantRepository;
import br.inatel.pos.dm111.vfu.persistence.user.User;
import br.inatel.pos.dm111.vfu.persistence.user.User.UserType;
import br.inatel.pos.dm111.vfu.persistence.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class PromotionService
{
	private static final Logger log = LoggerFactory.getLogger(PromotionService.class);

	private final PromotionRepository promotionRepository;

	private final RestaurantRepository restaurantRepository; 

	private final UserRepository userRepository; 

	private final HttpServletRequest request;
	
	public PromotionService(PromotionRepository promotionRepository, RestaurantRepository restaurantRepository, UserRepository userRepository, HttpServletRequest request)
	{
		this.promotionRepository = promotionRepository;
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
		this.request = request;
	}

	// Contexto do Restaurante (CRUD de Promoções)
	public PromotionResponse createPromotion(PromotionRequest request) throws ApiException
	{
		//valideUser(UserType.RESTAURANT);
		// Validação: 1. O restaurante existe localmente?
		var restaurantOpt = retrieveRestaurantById(request.restaurantId());
		Restaurant restaurant = restaurantOpt.get();
		// Validação: 2. Os produtos existem no cardápio local do restaurante?
		validateProduct(request.product(), restaurant.products());
		
		Promotion promotion = buildPromotion(request);
		promotionRepository.save(promotion);
		
		return buildPromotionResponse(promotion, restaurant);
	}

	public PromotionResponse searchPromotionById(String id) throws ApiException
	{
		var promotionOpt = retrievePromotionById(id);
		if (promotionOpt.isEmpty())
		{
			log.warn("Promotion was not found. Id: {}", id);
			throw new ApiException(AppErrorCode.PROMOTION_NOT_FOUND);
		}
		Promotion promotion = promotionOpt.get();
		return buildPromotionResponse(promotion);
	}

	public PromotionResponse updatePromotion(PromotionRequest request, String id) throws ApiException
	{
		valideUser(UserType.RESTAURANT);
		var promotionOpt = retrievePromotionById(id);
		if (promotionOpt.isEmpty())
		{
			log.warn("Promotion was not found. Id: {}", id);
			throw new ApiException(AppErrorCode.PROMOTION_NOT_FOUND);
		}
		var restaurantOpt = retrieveRestaurantById(request.restaurantId());
		if (restaurantOpt.isEmpty())
		{
			log.warn("Restaurant was not found for the promotion update. Id: {}", request.restaurantId());
			throw new ApiException(AppErrorCode.RESTAURANT_NOT_FOUND);
		}
		// Valida se os produtos existem no cardápio do restaurante
		validateProduct(request.product(), restaurantOpt.get().products());
		// Converte a requisição para a entidade de persistência
		var updatedPromotion = buildPromotion(request, id);
		promotionRepository.save(updatedPromotion);
		log.info("Promotion with ID {} was successfully updated.", id);
		return buildPromotionResponse(updatedPromotion);
	}

	// Método para deletar uma promoção
	public void deletePromotion(String id) throws ApiException
	{
		valideUser(UserType.RESTAURANT);
		var promotionOpt = retrievePromotionById(id);
		if (promotionOpt.isPresent())
		{
			try
			{
				promotionRepository.delete(id);
				log.info("Promotion with ID {} was successfully deleted.", id);
			}
			catch (ExecutionException | InterruptedException e)
			{
				log.error("Failed to delete promotion from DB by id {}.", id, e);
				throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
			}
		}
		else
		{
			log.info("The provided promotion id was not found. id: {}", id);
		}
	}

	public List<PromotionResponse> listRestaurantPromotions(String restaurantId) throws ApiException
	{
		var restaurantOpt = retrieveRestaurantById(restaurantId);
		if (restaurantOpt.isEmpty())
		{
			log.warn("Restaurant was not found for the promotion update. Id: {}", restaurantId);
			throw new ApiException(AppErrorCode.RESTAURANT_NOT_FOUND);
		}
		Restaurant restaurant = restaurantOpt.get();
		List<Promotion> promotions = retrievePromotionsById(restaurantId);
		return promotions.stream().map(p -> buildPromotionResponse(p, restaurant)).collect(Collectors.toList());
	}

	// Contexto do Cliente (Listagem de Promoções)
	public List<PromotionResponse> listAllPromotions() throws ApiException
	{
		List<Promotion> promotions = retrievePromotions();
		return buildPromotionResponse(promotions);
	}

	public List<PromotionResponse> listPromotionsByInterest(String userId) throws ApiException
	{
		List<PromotionResponse> listaPromotionResponse = new ArrayList<PromotionResponse>();
		var userOpt = retrieveUserById(userId);
		if (userOpt.isEmpty())
		{
			log.warn("User was not found. Id: {}", userId);
			throw new ApiException(AppErrorCode.USER_NOT_FOUND);
		}
		else
		{
			var user = userOpt.get();
			List<String> preferredCategories = user.preferredCategories();
			if (preferredCategories == null || preferredCategories.isEmpty())
			{
				return listAllPromotions();
			}
			List<Promotion> allPromotions = retrievePromotions();
			for (Promotion promotion : allPromotions)
			{
				Optional<Restaurant> restaurantOpt = retrieveRestaurantById(promotion.restaurantId());
				if (!restaurantOpt.isEmpty())
				{
					Restaurant restaurant = restaurantOpt.get();
					for (Product product : restaurant.products())
					{
						if (promotion.product().productId().equals(product.id()))
						{
							if (preferredCategories.contains(product.category()))
							{
								PromotionalProductResponse promotionalProductResponse = new PromotionalProductResponse(promotion.product().productId(), promotion.product().promotionalPrice(), product.category(), product.name());
								PromotionResponse promotionResponse = new PromotionResponse(promotion.id(), promotion.name(), promotion.description(), promotion.restaurantId(), promotionalProductResponse);
								listaPromotionResponse.add(promotionResponse);
								break;
							}
						}
					}
				}
			}
		}
		return listaPromotionResponse;
	}

	private void validateProduct(PromotionalProductRequest promotionalProduct, List<Product> restaurantProducts) throws ApiException
	{
		boolean productExists = restaurantProducts.stream().anyMatch(p -> p.id().equals(promotionalProduct.productId()));
		if (!productExists)
		{
			throw new ApiException(AppErrorCode.PRODUCT_NOT_FOUND);
		}
	}
	
	private void valideUser(UserType userType) throws ApiException {
		User user = (User) request.getAttribute("authenticatedUser");
		if (!userType.equals(user.type()))
		{
			log.info("User provided is not valid for this operation. UserId: {}", user.id());
			throw new ApiException(AppErrorCode.INVALID_USER_TYPE);
		}
	}

	private Promotion buildPromotion(PromotionRequest request)
	{
		var promotionId = UUID.randomUUID().toString();
		PromotionalProduct promotionalProduct = new PromotionalProduct(request.product().productId(), request.product().promotionalPrice());
		return new Promotion(promotionId, request.name(), request.description(), request.restaurantId(), promotionalProduct);
	}

	private PromotionResponse buildPromotionResponse(Promotion promotion, Restaurant restaurant)
	{
		PromotionalProductResponse promotionalProductResponse = null;
		for (Product product : restaurant.products())
		{
			if (product.id().equals(promotion.product().productId()))
			{
				promotionalProductResponse = new PromotionalProductResponse(promotion.product().productId(), promotion.product().promotionalPrice(), product.category(), product.name());
				break;
			}
		}
		return new PromotionResponse(promotion.id(), promotion.name(), promotion.description(), promotion.restaurantId(), promotionalProductResponse);
	}

	// Sobrecarga do método para quando o restaurante não é conhecido de imediato (ex: listAllPromotions)
	private PromotionResponse buildPromotionResponse(Promotion promotion) throws ApiException
	{
		Optional<Restaurant> restaurantOpt = retrieveRestaurantById(promotion.restaurantId());
		Restaurant restaurant = restaurantOpt.orElse(null); // Trata o caso em que o restaurante não é encontrado
		return buildPromotionResponse(promotion, restaurant);
	}

	private List<PromotionResponse> buildPromotionResponse(List<Promotion> promotions) throws ApiException
	{
		List<PromotionResponse> promotionResponses = new ArrayList<PromotionResponse>();
		for (Promotion promotion : promotions)
		{
			var restaurantOpt = retrieveRestaurantById(promotion.restaurantId());
			Restaurant restaurant = restaurantOpt.get();
			PromotionResponse promotionResponse = buildPromotionResponse(promotion, restaurant);
			promotionResponses.add(promotionResponse);
		}
		return promotionResponses;
	}

	private Optional<Restaurant> retrieveRestaurantById(String id) throws ApiException
	{
		try
		{
			return restaurantRepository.getById(id);
		}
		catch (ExecutionException | InterruptedException e)
		{
			log.error("Failed to read a restaurant from DB by id {}.", id, e);
			throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
		}
	}

	private Optional<Promotion> retrievePromotionById(String id) throws ApiException
	{
		try
		{
			return promotionRepository.getById(id);
		}
		catch (ExecutionException | InterruptedException e)
		{
			log.error("Failed to read a restaurant from DB by id {}.", id, e);
			throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
		}
	}

	private List<Promotion> retrievePromotionsById(String id) throws ApiException
	{
		try
		{
			List<Promotion> promotions = promotionRepository.findByRestaurantId(id);
			return promotions;
		}
		catch (ExecutionException | InterruptedException e)
		{
			log.error("Failed to read a restaurant from DB by id {}.", id, e);
			throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
		}
	}

	private List<Promotion> retrievePromotions() throws ApiException
	{
		try
		{
			return promotionRepository.getAll();
		}
		catch (ExecutionException | InterruptedException e)
		{
			log.error("Failed to read all restaurants from DB.", e);
			throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
		}
	}

	private Optional<User> retrieveUserById(String id) throws ApiException
	{
		try
		{
			return userRepository.getById(id);
		}
		catch (ExecutionException | InterruptedException e)
		{
			log.error("Failed to read an user from DB by id {}.", id, e);
			throw new ApiException(AppErrorCode.INTERNAL_DATABASE_COMMUNICATION_ERROR);
		}
	}

	private Promotion buildPromotion(PromotionRequest request, String id)
	{
		PromotionalProduct promotionalProduct = new PromotionalProduct(request.product().productId(), request.product().promotionalPrice());
		return new Promotion(id, request.name(), request.description(), request.restaurantId(), promotionalProduct);
	}
}