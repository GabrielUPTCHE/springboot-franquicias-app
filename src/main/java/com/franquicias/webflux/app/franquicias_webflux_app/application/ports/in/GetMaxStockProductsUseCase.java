package com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.query.ProductMaxStockResponse;

import reactor.core.publisher.Flux;

public interface GetMaxStockProductsUseCase {
    Flux<ProductMaxStockResponse> getMaxStockProduct(String franchiseId);
}
