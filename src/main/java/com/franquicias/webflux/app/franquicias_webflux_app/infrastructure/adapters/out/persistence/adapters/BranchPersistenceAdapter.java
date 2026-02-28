package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.BranchRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Branch;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.entities.BranchDocument;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.repositories.BranchReactiveMongoRepository;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchPersistenceAdapter implements BranchRepositoryPort {

    private final BranchReactiveMongoRepository repository;

    @Override
    public Mono<Branch> save(Branch branch) {
        BranchDocument document = BranchDocument.builder()
                .id(branch.getId())
                .name(branch.getName())
                .franchiseId(branch.getFranchiseId())
                .build();

        return repository.save(document)
                .map(saved -> Branch.builder()
                        .id(saved.getId())
                        .name(saved.getName())
                        .franchiseId(saved.getFranchiseId())
                        .build());
    }
}
