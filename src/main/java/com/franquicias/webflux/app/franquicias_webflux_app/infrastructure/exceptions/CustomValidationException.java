package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.exceptions;

import lombok.Getter;
import java.util.List;

@Getter
public class CustomValidationException extends RuntimeException {
    private final List<String> errors;

    public CustomValidationException(List<String> errors) {
        super(String.join(", ", errors));
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
