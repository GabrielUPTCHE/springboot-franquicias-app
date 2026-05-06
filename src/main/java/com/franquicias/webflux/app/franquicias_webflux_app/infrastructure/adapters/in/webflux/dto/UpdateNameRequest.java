package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateNameRequest(
    String id,
    @NotBlank(message = "El nuevo nombre es obligatorio") String name
) {

}
