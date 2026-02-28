package com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions;

public class FranchiseNotFoundException extends DomainException {
    public FranchiseNotFoundException(String franchiseId) {
        super("La franquicia con ID " + franchiseId + " no existe.");
    }
}