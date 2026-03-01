package com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command;

import jakarta.validation.constraints.NotBlank;

public record UpdateNameCommand(
    String id, // Este lo setearemos desde el PathVariable
    @NotBlank(message = "El nuevo nombre es obligatorio") String name
) {}