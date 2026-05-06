package com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateProductStockCommand(
        
    @NotBlank(message = "El ID del producto es obligatorio")
    @Pattern(regexp = "^\\S+$", message = "El ID del producto no puede contener espacios en blanco")
    String productId,

    @NotNull(message = "El nuevo valor del stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser un número negativo")
    Integer newStock
) {}