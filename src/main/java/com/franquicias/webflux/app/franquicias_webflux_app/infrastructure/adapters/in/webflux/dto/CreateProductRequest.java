package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
    @NotBlank(message = "El nombre del producto es obligatorio") String name,
    @NotNull(message = "El stock inicial es obligatorio") 
    @Min(value = 0, message = "El stock no puede ser negativo") 
    Integer stock,
    String branchId
) {

}
