package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.exceptions;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ErrorResponse(
     /* @Schema(description = "Hora que se lanzo la excepción", example = "18/01/2026 08:37")
    LocalDateTime time,
    @Schema(description = "Código HTTP del error", example = "400")
    int status,
    @Schema(description = "Tipo de error", example = "Bad Request")
    String error,
    @Schema(description = "Mensaje descriptivo", example = "El precio no puede ser negativo")
    String message,
    @Schema(description = "Ruta de la solicitud", example = "/product/create")
    String path,
    @Schema(description = "Lista de detalles o errores de validación", example = "[\"price: debe ser mayor que 0\"]")
    List<String> details */
    int status,
    String message,
    List<String> details,
    LocalDateTime timestamp
) {}