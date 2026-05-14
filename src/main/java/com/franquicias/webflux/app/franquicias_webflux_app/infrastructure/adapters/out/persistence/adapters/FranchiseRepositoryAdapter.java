package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.adapters;

import lombok.RequiredArgsConstructor;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.FranchiseRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.mappers.FranchiseMapper;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.repositories.FranchiseReactiveMongoRepository;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.config.ConfigValues;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseRepositoryAdapter implements FranchiseRepositoryPort {

    private final FranchiseReactiveMongoRepository repository;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return Mono.just(franchise)
                .map(FranchiseMapper::toEntity)
                .flatMap(repository::save)
                .map(FranchiseMapper::toDomain)
                .timeout(Duration.ofSeconds(ConfigValues.RETRY_WRITE_SECONDS)); 
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return repository.findById(id)
                .map(FranchiseMapper::toDomain)
                .timeout(Duration.ofSeconds(ConfigValues.RETRY_READ_SECONDS));
    }
}