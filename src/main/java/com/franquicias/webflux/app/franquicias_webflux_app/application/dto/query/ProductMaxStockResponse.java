package com.franquicias.webflux.app.franquicias_webflux_app.application.dto.query;

public record ProductMaxStockResponse(
    String productId,
    String productName,
    Integer stock,
    String branchId,
    String branchName
) {}