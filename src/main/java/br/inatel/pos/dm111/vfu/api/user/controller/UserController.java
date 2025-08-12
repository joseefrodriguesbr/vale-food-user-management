package br.inatel.pos.dm111.vfu.api.user.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.inatel.pos.dm111.vfu.api.user.UserRequest;
import br.inatel.pos.dm111.vfu.api.user.UserResponse;
import br.inatel.pos.dm111.vfu.api.user.service.UserService;

@RestController
@RequestMapping("/valefood/users")
public class UserController
{
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	private final UserRequestValidator validator;

	private final UserService service;

	public UserController(UserRequestValidator validator, UserService service)
	{
		this.validator = validator;
		this.service = service;
	}

	@GetMapping
	public ResponseEntity<List<UserResponse>> getAllUsers()
	{
		log.debug("Received request to list all users");
		var response = service.searchUsers();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping(value = "/{userId}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable("userId")
	String id)
	{
		log.debug("Received request to list an user by id: {}", id);
		var response = service.searchUser(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	// {
	// "name": "Edilson",
	// "email": "edilson@inatel.br",
	// "password": "admin",
	// "type": "regular"
	// }
	@PostMapping
	public ResponseEntity<UserResponse> postUser(@RequestBody
	UserRequest request, BindingResult bindingResult)
	{
		log.debug("Received request to create a new user...");
		ValidationUtils.invokeValidator(validator, request, bindingResult);
		if (bindingResult.hasErrors())
		{
			// todo: implement it whenever the globsl exception handler is done
		}
		var response = service.createUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping(value = "/{userId}")
	public ResponseEntity<UserResponse> putUser(@RequestBody
	UserRequest request, @PathVariable("userId")
	String userId, BindingResult bindingResult)
	{
		log.debug("Received request to update an user...");
		ValidationUtils.invokeValidator(validator, request, bindingResult);
		if (bindingResult.hasErrors())
		{
			// todo: implement it whenever the globsl exception handler is done
		}
		var response = service.updateUser(request, userId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping(value = "/{userId}")
	public ResponseEntity<List<UserResponse>> deleteUser(@PathVariable("userId")
	String id)
	{
		log.debug("Received request to delete an user: id {}", id);
		service.removeUser(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
