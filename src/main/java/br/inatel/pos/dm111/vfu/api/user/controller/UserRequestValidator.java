package br.inatel.pos.dm111.vfu.api.user.controller;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import br.inatel.pos.dm111.vfu.api.user.UserRequest;

@Component
public class UserRequestValidator implements Validator
{
	@Override
	public boolean supports(Class<?> clazz)
	{
		return UserRequest.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors)
	{
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name.empty", "Name is required!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "email.empty", "Email is required!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password.empty", "Password is required!");
	}
}
