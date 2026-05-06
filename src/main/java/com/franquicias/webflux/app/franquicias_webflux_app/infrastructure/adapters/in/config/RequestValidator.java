package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.exceptions.CustomValidationException;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    public <T> Mono<T> validate(T target) {
        Set<ConstraintViolation<T>> violations = validator.validate(target);
        
        if (violations.isEmpty()) {
            return Mono.just(target);
        } else {
            List<String> errors = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();
            return Mono.error(new CustomValidationException(errors));
        }
    }
}