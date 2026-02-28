package com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out;

import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Product;

import reactor.core.publisher.Mono;

public interface ProductRepositoryPort {
    Mono<Product> save(Product product);
    Mono<Product> findById(String id);
    Mono<Void> deleteById(String id);
}
