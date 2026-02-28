package com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions;

public abstract class DomainException extends RuntimeException{
    protected DomainException(String message) {
        super(message);
    }
}
