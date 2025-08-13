package br.inatel.pos.dm111.vfu.api.restaurant.controller;

import br.inatel.pos.dm111.vfu.api.restaurant.RestaurantRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RestaurantRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return RestaurantRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}
