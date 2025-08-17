package br.inatel.pos.dm111.vfu.persistence.promo;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

@Profile("local")
@Component
public class FirebasePromotionRepositoryImpl implements PromotionRepository
{
	private static final Logger log = LoggerFactory.getLogger(FirebasePromotionRepositoryImpl.class);

	private static final String COLLECTION_NAME = "promotions";

	private final Firestore firestore;

	public FirebasePromotionRepositoryImpl(Firestore firestore)
	{
		this.firestore = firestore;
	}

	@Override
	public List<Promotion> getAll() throws ExecutionException, InterruptedException
	{
		CollectionReference promotions = firestore.collection(COLLECTION_NAME);
		ApiFuture<QuerySnapshot> query = promotions.get();
		List<QueryDocumentSnapshot> documents = query.get().getDocuments();
		log.info("Found {} promotions in the database.", documents.size());
		return documents.stream().map(document -> document.toObject(Promotion.class)).collect(Collectors.toList());
	}

	@Override
	public Optional<Promotion> getById(String id) throws ExecutionException, InterruptedException
	{
		DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
		ApiFuture<DocumentSnapshot> future = docRef.get();
		DocumentSnapshot document = future.get();
		if (document.exists())
		{
			log.info("Promotion with ID {} was found.", id);
			return Optional.ofNullable(document.toObject(Promotion.class));
		}
		else
		{
			log.warn("Promotion with ID {} was not found.", id);
			return Optional.empty();
		}
	}

	@Override
	public Promotion save(Promotion promotion)
	{
		ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(promotion.id()).set(promotion);
		try
		{
			log.info("Promotion with ID {} saved successfully at: {}.", promotion.id(), future.get().getUpdateTime());
			return promotion;
		}
		catch (InterruptedException | ExecutionException e)
		{
			log.error("Failed to save promotion with ID {}. Error: {}", promotion.id(), e.getMessage());
			throw new RuntimeException("Failed to save promotion.", e);
		}
	}

	@Override
	public void delete(String id) throws ExecutionException, InterruptedException
	{
		ApiFuture<WriteResult> writeResult = firestore.collection(COLLECTION_NAME).document(id).delete();
		log.info("Promotion with ID {} deleted successfully at: {}.", id, writeResult.get().getUpdateTime());
	}

	@Override
	public List<Promotion> findByRestaurantId(String restaurantId) throws ExecutionException, InterruptedException
	{
		CollectionReference promotions = firestore.collection(COLLECTION_NAME);
		Query query = promotions.whereEqualTo("restaurantId", restaurantId);
		ApiFuture<QuerySnapshot> querySnapshot = query.get();
		List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
		log.info("Found {} promotions for restaurant ID {}.", documents.size(), restaurantId);
		return documents.stream().map(document -> document.toObject(Promotion.class)).collect(Collectors.toList());
	}
}