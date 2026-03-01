package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.adapters.FranchiseRepositoryAdapter;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.entities.FranchiseDocument;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.repositories.FranchiseReactiveMongoRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchisePersistenceAdapterTest {

    @Mock
    private FranchiseReactiveMongoRepository repository;

    @InjectMocks
    private FranchiseRepositoryAdapter adapter;

    @Test
    void save_ShouldMapAndReturnDomainModel() {
        Franchise domainModel = Franchise.builder().name("KFC").build();
        FranchiseDocument savedDocument = FranchiseDocument.builder().id("1").name("KFC").build();

        when(repository.save(any(FranchiseDocument.class))).thenReturn(Mono.just(savedDocument));

        // Act & Assert
        StepVerifier.create(adapter.save(domainModel))
                .expectNextMatches(saved -> 
                        saved.getId().equals("1") && 
                        saved.getName().equals("KFC"))
                .verifyComplete();
    }
}