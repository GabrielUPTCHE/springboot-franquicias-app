package com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command;


public record CreateProductCommand(
    String name,
    Integer stock,
    String branchId
) {}