package com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command;

import jakarta.validation.constraints.NotBlank;

public record CreateFranchiseCommand(
    @NotBlank(message = "El nombre de la franquicia es obligatorio") 
    String name
) {}