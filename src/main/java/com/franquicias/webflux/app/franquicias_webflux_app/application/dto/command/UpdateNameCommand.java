package com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateNameCommand(
        
    @NotBlank(message = "El ID del recurso a actualizar es obligatorio")
    @Pattern(regexp = "^\\S+$", message = "El ID no puede contener espacios en blanco")
    String id,

    @NotBlank(message = "El nuevo nombre es obligatorio y no puede estar en blanco")
    @Size(min = 2, max = 50, message = "El nuevo nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^(?!^[0-9]+$)[a-zA-Z0-9\\sÁÉÍÓÚáéíóúÑñ]+$", 
            message = "El nuevo nombre debe contener al menos letras y no tener caracteres especiales inválidos")
    String name
) {}