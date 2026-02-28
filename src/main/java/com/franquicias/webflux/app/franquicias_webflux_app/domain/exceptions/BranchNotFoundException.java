package com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions;

public class BranchNotFoundException extends DomainException {
    public BranchNotFoundException(String branchId) {
        super("La sucursal con ID " + branchId + " no existe.");
    }
}