package com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateBranchCommand(
    @NotBlank(message = "El nombre de la sucursal es obligatorio y no puede estar en blanco")
    @Size(min = 2, max = 50, message = "El nombre de la sucursal debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^(?!^[0-9]+$)[a-zA-Z0-9\\sÁÉÍÓÚáéíóúÑñ]+$", 
                 message = "El nombre de la sucursal debe contener al menos letras y no tener caracteres especiales inválidos")
    String name,

    @NotBlank(message = "El ID de la franquicia a la que pertenece la sucursal es obligatorio")
    @Pattern(regexp = "^\\S+$", message = "El ID de la franquicia no puede contener espacios en blanco")
    String franchiseId
) {}