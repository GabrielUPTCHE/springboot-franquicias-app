package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.adapters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Product;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.valueobjects.Stock;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.entities.ProductDocument;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.repositories.ProductReactiveMongoRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.test.publisher.PublisherProbe;
import reactor.util.context.Context;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductPersistenceAdapterTest {

    @Mock
    private ProductReactiveMongoRepository repository;

    @InjectMocks
    private ProductPersistenceAdapter adapter;

    @Test
    @DisplayName("Debe guardar el producto validando suscripción y contexto")
    void save_ShouldMapAndSave_WithContextAndProbe() {
        Product domainProduct = new Product("p1", "Hamburguesa", new Stock(50), "b1");
        ProductDocument savedDocument = ProductDocument.builder()
                .id("p1")
                .name("Hamburguesa")
                .stock(50)
                .branchId("b1")
                .build();

        PublisherProbe<ProductDocument> saveProbe = PublisherProbe.of(Mono.just(savedDocument));
        when(repository.save(any(ProductDocument.class))).thenReturn(saveProbe.mono());

        StepVerifierOptions options = StepVerifierOptions.create().withInitialContext(Context.of("userId", "admin"));

        StepVerifier.create(adapter.save(domainProduct), options)
                .expectNextMatches(product -> product.id().equals("p1") && product.stock().value() == 50)
                .verifyComplete();

        saveProbe.assertWasSubscribed();
    }

    @Test
    @DisplayName("Debe buscar por ID simulando latencia con Virtual Time")
    void findById_ShouldReturnProduct_UsingVirtualTime() {
        ProductDocument document = ProductDocument.builder()
                .id("p1")
                .name("Papas")
                .stock(100)
                .branchId("b1")
                .build();

        when(repository.findById("p1")).thenReturn(Mono.just(document).delayElement(Duration.ofSeconds(1)));

        StepVerifier.withVirtualTime(() -> adapter.findById("p1"))
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(1))
                .expectNextMatches(product -> product.name().equals("Papas") && product.stock().value() == 100)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar Mono.empty cuando el producto no existe por ID")
    void findById_ShouldReturnEmpty_WhenNotFound() {
        when(repository.findById("invalid")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById("invalid"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe eliminar por ID validando la suscripción al repositorio")
    void deleteById_ShouldComplete_WithProbe() {
        PublisherProbe<Void> deleteProbe = PublisherProbe.empty();
        when(repository.deleteById("p1")).thenReturn(deleteProbe.mono());

        StepVerifier.create(adapter.deleteById("p1"))
                .verifyComplete();

        deleteProbe.assertWasSubscribed();
    }

    @Test
    @DisplayName("Debe encontrar el producto con mayor stock por sucursal")
    void findTopByBranchIdOrderByStockDesc_ShouldReturnProduct() {
        ProductDocument document = ProductDocument.builder()
                .id("p1")
                .name("Gaseosa")
                .stock(200)
                .branchId("b1")
                .build();

        when(repository.findFirstByBranchIdOrderByStockDesc("b1")).thenReturn(Mono.just(document));

        StepVerifier.create(adapter.findTopByBranchIdOrderByStockDesc("b1"))
                .expectNextMatches(product -> product.name().equals("Gaseosa") && product.stock().value() == 200)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar Mono.empty cuando la sucursal no tiene productos")
    void findTopByBranchIdOrderByStockDesc_ShouldReturnEmpty_WhenNotFound() {
        when(repository.findFirstByBranchIdOrderByStockDesc("b1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findTopByBranchIdOrderByStockDesc("b1"))
                .verifyComplete();
    }
}