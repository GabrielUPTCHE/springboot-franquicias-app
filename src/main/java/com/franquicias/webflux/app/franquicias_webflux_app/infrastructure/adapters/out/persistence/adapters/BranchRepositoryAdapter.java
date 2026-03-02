package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.BranchRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Branch;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.entities.BranchDocument;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.mappers.BranchMapper;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.repositories.BranchReactiveMongoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchRepositoryAdapter implements BranchRepositoryPort {

    private final BranchReactiveMongoRepository repository;

    @Override
    public Mono<Branch> save(Branch branch) {
        BranchDocument document = BranchDocument.builder()
                .id(branch.id().toString())
                .name(branch.name())
                .franchiseId(branch.franchiseId())
                .build();

        return repository.save(document)
                .map(saved -> 
                        BranchMapper.toDomain(saved)
                );
    }

    @Override
    public Mono<Branch> findById(String id) {
        return repository.findById(id)
                .map(finded -> 
                        BranchMapper.toDomain(finded)
                );
    }
    @Override
    public Flux<Branch> findByFranchiseId(String franchiseId) {
        return repository.findByFranchiseId(franchiseId)
                .map(doc -> 
                         BranchMapper.toDomain(doc)
                );
    }
}
