package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.entities.BranchDocument;

public interface BranchReactiveMongoRepository extends ReactiveMongoRepository<BranchDocument, String> {
}