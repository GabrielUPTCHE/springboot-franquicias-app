package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBranchRequest(
    @NotBlank(message = "El nombre de la sucursal es obligatorio") String name,
    @NotBlank(message = "El ID de la franquicia es obligatorio") String franchiseId
) {

}
