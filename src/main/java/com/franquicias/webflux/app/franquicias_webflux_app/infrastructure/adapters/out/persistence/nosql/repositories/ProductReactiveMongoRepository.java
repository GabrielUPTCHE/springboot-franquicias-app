package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.entities.ProductDocument;

import reactor.core.publisher.Mono;

public interface ProductReactiveMongoRepository extends ReactiveMongoRepository<ProductDocument, String> {
    Mono<ProductDocument> findFirstByBranchIdOrderByStockDesc(String branchId);
}