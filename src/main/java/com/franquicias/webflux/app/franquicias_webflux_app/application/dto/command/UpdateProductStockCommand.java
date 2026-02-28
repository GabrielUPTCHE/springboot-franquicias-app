package com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateProductStockCommand(
    @NotBlank(message = "El ID del producto es obligatorio") String productId,
    @NotNull(message = "El nuevo stock es obligatorio") 
    @Min(value = 0, message = "El stock no puede ser negativo") Integer newStock
) {}
