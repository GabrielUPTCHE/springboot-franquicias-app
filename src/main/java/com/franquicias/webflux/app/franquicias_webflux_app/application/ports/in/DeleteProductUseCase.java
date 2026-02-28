package com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in;

import reactor.core.publisher.Mono;

public interface DeleteProductUseCase {
    Mono<Void> deleteProduct(String productId);
}