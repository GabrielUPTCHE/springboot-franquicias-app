package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFranchiseRequest(
    @NotBlank(message = "El nombre de la franquicia es obligatorio") 
    String name
) {

}
