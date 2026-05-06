package com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateProductCommand(
    @NotBlank(message = "El nombre del producto es obligatorio y no puede estar en blanco")
    @Size(min = 2, max = 50, message = "El nombre del producto debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^(?!^[0-9]+$)[a-zA-Z0-9\\sÁÉÍÓÚáéíóúÑñ]+$", 
            message = "El nombre del producto debe contener al menos letras y no tener caracteres especiales inválidos")
    String name,

    @NotNull(message = "El stock inicial es obligatorio")
    @Min(value = 0, message = "El stock inicial no puede ser un número negativo")
    Integer stock,

    @NotBlank(message = "El ID de la sucursal a la que pertenece el producto es obligatorio")
    @Pattern(regexp = "^\\S+$", message = "El ID de la sucursal no puede contener espacios en blanco")
    String branchId
) {}