package br.inatel.pos.dm111.vfu.api.user.service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.inatel.pos.dm111.vfu.api.core.ApiException;
import br.inatel.pos.dm111.vfu.api.user.UserRequest;
import br.inatel.pos.dm111.vfu.api.user.UserResponse;
import br.inatel.pos.dm111.vfu.persistence.user.User;
import br.inatel.pos.dm111.vfu.persistence.user.UserRepository;

@Service
public class UserService
{
	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository repository;

	public UserService(UserRepository repository)
	{
		this.repository = repository;
	}

	public UserResponse createUser(UserRequest request) { //throws ApiException {
        // validate user uniqueness by email
        repository.getByEmail(request.email()).ifPresent( user -> {

                log.warn("Provided email already in use.");
                //return new ApiException(AppErrorCode.CONFLICTED_USER_EMAIL);
                throw new RuntimeException("Emailalerady in use.");
        });

        var user = buildUser(request);
        repository.save(user);
        log.info("User was successfully created. Id: {}", user.id());

        return buildUserResponse(user);
    }
	// public UserResponse createUser(UserRequest request) throws ApiException {
	// // validate user uniqueness by email
	// repository.getByEmail(request.email()).ifPresent(user -> {
	//
	// log.warn("Provided email already in use.");
	// return new ApiException(AppErrorCode.CONFLICTED_USER_EMAIL);
	// });
	//
	// var user = buildUser(request);
	// repository.save(user);
	// log.info("User was successfully created. Id: {}", user.id());
	//
	// return buildUserResponse(user);
	// }

	public List<UserResponse> searchUsers()
	{
		var users = repository.getAll();
		return users.stream().map(this::buildUserResponse).toList();
	}

	public UserResponse searchUser(String id)
	{
		return repository.getById(id).map(this::buildUserResponse).orElseThrow(() -> {
			log.warn("User was not found. Id: {}", id);
			return new RuntimeException("User not found!");
		});
	}

	public void removeUser(String id)
	{
		repository.delete(id);
		log.info("User was successfully deleted. id: {}", id);
	}

	public UserResponse updateUser(UserRequest request, String id)
	{
		// check user by id exist
		var userOpt = repository.getById(id);
		if (userOpt.isEmpty())
		{
			log.warn("User was not found. Id: {}", id);
			throw new RuntimeException("User not found!");
		}
		else
		{
			var user = userOpt.get();
			if (request.email() != null && !user.email().equals(request.email()))
			{
				// validate user uniqueness by email
				repository.getByEmail(request.email()).ifPresent(x -> {
					log.warn("Provided email already in use.");
					throw new RuntimeException("Email already in use.");
				});
			}
		}
		// check email was updated, if so, then check the uniqueness
		// encrypt the provided password if it was provided
		var user = buildUser(request, id);
		repository.save(user);
		return buildUserResponse(user);
	}

	private User buildUser(UserRequest request)
	{
		var encryptedPwd = encrypt(request.password());
		var userId = UUID.randomUUID().toString();
		return new User(userId, request.name(), request.email(), encryptedPwd, request.type());
	}

	private User buildUser(UserRequest request, String id)
	{
		var encryptedPwd = encrypt(request.password());
		return new User(id, request.name(), request.email(), encryptedPwd, request.type());
	}

	private UserResponse buildUserResponse(User user)
	{
		return new UserResponse(user.id(), user.name(), user.email(), user.type());
	}

	private String encrypt(String text)
	{
		MessageDigest crypt = null;
		try
		{
			crypt = MessageDigest.getInstance("SHA-1");
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException(e);
		}
		crypt.reset();
		crypt.update(text.getBytes(StandardCharsets.UTF_8));
		return new BigInteger(1, crypt.digest()).toString();
	}

	private class CONFLICTED_USER_EMAIL
	{
	}
}
