package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.adapters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Branch;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.entities.BranchDocument;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.repositories.BranchReactiveMongoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.test.publisher.PublisherProbe;
import reactor.util.context.Context;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchRepositoryAdapterTest {

    @Mock
    private BranchReactiveMongoRepository repository;

    @InjectMocks
    private BranchRepositoryAdapter adapter;

    @Test
    @DisplayName("Debe guardar la sucursal asegurando la suscripción y propagando el Context")
    void save_ShouldMapAndSave_WithContextAndProbe() {
        Branch domainBranch = new Branch("b1", "Sucursal Sur", "f1");
        BranchDocument savedDocument = BranchDocument.builder()
                .id("b1")
                .name("Sucursal Sur")
                .franchiseId("f1")
                .build();

        PublisherProbe<BranchDocument> saveProbe = PublisherProbe.of(Mono.just(savedDocument));
        when(repository.save(any(BranchDocument.class))).thenReturn(saveProbe.mono());

        StepVerifierOptions options = StepVerifierOptions.create().withInitialContext(Context.of("tenant", "latam"));

        StepVerifier.create(adapter.save(domainBranch), options)
                .expectNextMatches(branch -> branch.id().equals("b1") && branch.name().equals("Sucursal Sur"))
                .verifyComplete();

        saveProbe.assertWasSubscribed();
    }

    @Test
    @DisplayName("Debe buscar por ID simulando latencia de BD con Virtual Time")
    void findById_ShouldReturnBranch_UsingVirtualTime() {
        BranchDocument document = BranchDocument.builder()
                .id("b1")
                .name("Sucursal Norte")
                .franchiseId("f1")
                .build();

        when(repository.findById("b1")).thenReturn(Mono.just(document).delayElement(Duration.ofSeconds(2)));

        StepVerifier.withVirtualTime(() -> adapter.findById("b1"))
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(2))
                .expectNextMatches(branch -> branch.name().equals("Sucursal Norte"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar Mono.empty cuando no existe el ID")
    void findById_ShouldReturnEmpty_WhenNotFound() {
        when(repository.findById("invalid-id")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById("invalid-id"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe buscar por ID de franquicia manejando Backpressure")
    void findByFranchiseId_ShouldReturnBranches_WithBackpressure() {
        BranchDocument doc1 = BranchDocument.builder().id("b1").name("Sucursal 1").franchiseId("f1").build();
        BranchDocument doc2 = BranchDocument.builder().id("b2").name("Sucursal 2").franchiseId("f1").build();
        BranchDocument doc3 = BranchDocument.builder().id("b3").name("Sucursal 3").franchiseId("f1").build();

        when(repository.findByFranchiseId("f1")).thenReturn(Flux.just(doc1, doc2, doc3));

        StepVerifier.create(adapter.findByFranchiseId("f1"), 0)
                .thenRequest(1)
                .expectNextMatches(branch -> branch.id().equals("b1"))
                .thenRequest(2)
                .expectNextMatches(branch -> branch.id().equals("b2"))
                .expectNextMatches(branch -> branch.id().equals("b3"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar correctamente un Flux vacío al buscar por franquicia sin sucursales")
    void findByFranchiseId_ShouldReturnEmptyFlux_WhenNoBranchesFound() {
        when(repository.findByFranchiseId("empty-franchise")).thenReturn(Flux.empty());

        StepVerifier.create(adapter.findByFranchiseId("empty-franchise"))
                .verifyComplete();
    }
}