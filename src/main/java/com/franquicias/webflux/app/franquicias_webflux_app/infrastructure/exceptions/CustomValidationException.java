package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.exceptions;

import lombok.Getter;
import java.util.List;

@Getter
public class CustomValidationException extends RuntimeException {
    private final List<String> errors;

    public CustomValidationException(List<String> errors) {
        super("Error de validación en la petición");
        this.errors = errors;
    }
}
