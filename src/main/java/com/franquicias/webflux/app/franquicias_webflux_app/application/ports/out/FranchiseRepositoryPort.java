package com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out;

import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;

import reactor.core.publisher.Mono;

public interface FranchiseRepositoryPort {
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(String id);

}