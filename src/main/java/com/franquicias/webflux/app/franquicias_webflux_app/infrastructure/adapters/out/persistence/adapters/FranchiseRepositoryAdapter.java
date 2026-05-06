package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.FranchiseRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.entities.FranchiseDocument;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.mappers.FranchiseMapper;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.repositories.FranchiseReactiveMongoRepository;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseRepositoryAdapter implements FranchiseRepositoryPort {

    private final FranchiseReactiveMongoRepository repository;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        FranchiseDocument document = FranchiseMapper.toEntity(franchise);

        return repository.save(document)
                .map(savedDoc -> 
                        FranchiseMapper.toDomain(savedDoc)
                );
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return repository.findById(id)
                .map(findedDoc -> 
                        FranchiseMapper.toDomain(findedDoc)
        
                );
    }
}
