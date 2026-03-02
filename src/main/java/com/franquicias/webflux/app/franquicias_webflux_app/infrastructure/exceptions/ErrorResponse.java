package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.exceptions;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ErrorResponse(
    int status,
    String message,
    List<String> details,
    LocalDateTime timestamp
) {}