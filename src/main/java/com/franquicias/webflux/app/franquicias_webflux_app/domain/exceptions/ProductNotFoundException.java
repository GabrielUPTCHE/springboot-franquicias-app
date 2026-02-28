package com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions;

public class ProductNotFoundException extends DomainException {
    public ProductNotFoundException(String productId) {
        super("El producto con ID " + productId + " no existe.");
    }
}
