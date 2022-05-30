package com.github.kriaktus.restaurantvoting.web.dish;

import com.github.kriaktus.restaurantvoting.repository.DishRepository;
import com.github.kriaktus.restaurantvoting.to.DishTo;
import com.github.kriaktus.restaurantvoting.web.GlobalExceptionHandler;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;

@Component
@AllArgsConstructor
public class UniqueDishToValidator implements Validator {
    private final DishRepository repository;
    private final HttpServletRequest request;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return DishTo.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        DishTo dish = (DishTo) target;
        String requestURI = request.getRequestURI();
        int restaurantId;
        try {
            restaurantId = Integer.parseInt(requestURI.split("/")[4]);
        } catch (NumberFormatException nfe) {
            return;
        }

        repository.findByNameAndRestaurantId(dish.getName(), restaurantId).ifPresent(dbDish -> {
            Integer id = dish.getId();
            if (request.getMethod().equals("PUT")) {
                // update itself - it's ok
                if (id != null && dbDish.id() == id) return;
                // if dish from request body hasn't id, but dish id in path equals dish.id from db - it's ok
                if (id == null && requestURI.endsWith("/" + dbDish.getId())) return;
            }
            errors.rejectValue("name", "", GlobalExceptionHandler.EXCEPTION_DUPLICATE_TITLE);
        });
    }
}
