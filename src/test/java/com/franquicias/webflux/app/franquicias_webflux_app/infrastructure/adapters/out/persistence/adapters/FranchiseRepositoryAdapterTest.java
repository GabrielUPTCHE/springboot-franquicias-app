package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.adapters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.entities.FranchiseDocument;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.repositories.FranchiseReactiveMongoRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.test.publisher.PublisherProbe;
import reactor.util.context.Context;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseRepositoryAdapterTest {

    @Mock
    private FranchiseReactiveMongoRepository repository;

    @InjectMocks
    private FranchiseRepositoryAdapter adapter;

    @Test
    @DisplayName("Debe guardar la franquicia validando suscripción y contexto")
    void save_ShouldMapAndSave_WithContextAndProbe() {
        Franchise domainFranchise = new Franchise("f1", "Starbucks");
        FranchiseDocument savedDocument = FranchiseDocument.builder()
                .id("f1")
                .name("Starbucks")
                .build();

        PublisherProbe<FranchiseDocument> saveProbe = PublisherProbe.of(Mono.just(savedDocument));
        when(repository.save(any(FranchiseDocument.class))).thenReturn(saveProbe.mono());

        StepVerifierOptions options = StepVerifierOptions.create().withInitialContext(Context.of("traceId", "12345"));

        StepVerifier.create(adapter.save(domainFranchise), options)
                .expectNextMatches(franchise -> franchise.id().equals("f1") && franchise.name().equals("Starbucks"))
                .verifyComplete();

        saveProbe.assertWasSubscribed();
    }

    @Test
    @DisplayName("Debe buscar por ID simulando latencia con Virtual Time")
    void findById_ShouldReturnFranchise_UsingVirtualTime() {
        FranchiseDocument document = FranchiseDocument.builder()
                .id("f1")
                .name("KFC")
                .build();

        when(repository.findById("f1")).thenReturn(Mono.just(document).delayElement(Duration.ofSeconds(1)));

        StepVerifier.withVirtualTime(() -> adapter.findById("f1"))
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(1))
                .expectNextMatches(franchise -> franchise.name().equals("KFC"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar Mono.empty cuando no existe la franquicia")
    void findById_ShouldReturnEmpty_WhenNotFound() {
        when(repository.findById("invalid")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById("invalid"))
                .verifyComplete();
    }
}