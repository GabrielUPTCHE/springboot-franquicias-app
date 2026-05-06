package com.franquicias.webflux.app.franquicias_webflux_app.application.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.BranchRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.FranchiseRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.ProductRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.FranchiseNotFoundException;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Branch;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Product;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.valueobjects.Stock;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseReportServiceTest {

    @Mock private FranchiseRepositoryPort franchiseRepositoryPort;
    @Mock private BranchRepositoryPort branchRepositoryPort;
    @Mock private ProductRepositoryPort productRepositoryPort;

    @InjectMocks private FranchiseReportService reportService;

    // Constantes para reusar en los tests
    private final Franchise franchise = new Franchise("f1", "KFC");
    private final Branch branch1 = new Branch("b1", "KFC Centro", "f1");
    private final Branch branch2 = new Branch("b2", "KFC Norte", "f1");
    private final Product product1 = new Product("p1", "Pollo", new Stock(100), "b1");
    private final Product product2 = new Product("p2", "Papas", new Stock(50), "b2");

    @Test
    @DisplayName("Debe manejar Backpressure correctamente solicitando 1 elemento a la vez")
    void execute_ShouldReturnMaxStockProducts_WithBackpressure() {
        when(franchiseRepositoryPort.findById("f1")).thenReturn(Mono.just(franchise));
        when(branchRepositoryPort.findByFranchiseId("f1")).thenReturn(Flux.just(branch1, branch2));
        when(productRepositoryPort.findTopByBranchIdOrderByStockDesc("b1")).thenReturn(Mono.just(product1));
        when(productRepositoryPort.findTopByBranchIdOrderByStockDesc("b2")).thenReturn(Mono.just(product2));

        StepVerifier.create(reportService.getMaxStockProduct("f1"), 0)
                .thenRequest(1) 
                .expectNextCount(1) 
                .thenRequest(1)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe procesar resultados concurrentes usando Virtual Time simulando latencia de BD")
    void execute_ShouldProcessConcurrently_UsingVirtualTime() {
        when(franchiseRepositoryPort.findById("f1")).thenReturn(Mono.just(franchise));
        when(branchRepositoryPort.findByFranchiseId("f1")).thenReturn(Flux.just(branch1, branch2));
        
        when(productRepositoryPort.findTopByBranchIdOrderByStockDesc("b1"))
                .thenReturn(Mono.just(product1).delayElement(Duration.ofSeconds(3)));
        when(productRepositoryPort.findTopByBranchIdOrderByStockDesc("b2"))
                .thenReturn(Mono.just(product2).delayElement(Duration.ofSeconds(3)));

        StepVerifier.withVirtualTime(() -> reportService.getMaxStockProduct("f1"))
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar sucursales sin productos correctamente (Camino Vacío)")
    void execute_ShouldHandleBranchesWithoutProducts() {
        when(franchiseRepositoryPort.findById("f1")).thenReturn(Mono.just(franchise));
        when(branchRepositoryPort.findByFranchiseId("f1")).thenReturn(Flux.just(branch1, branch2));
        when(productRepositoryPort.findTopByBranchIdOrderByStockDesc("b1")).thenReturn(Mono.just(product1));
        
        when(productRepositoryPort.findTopByBranchIdOrderByStockDesc("b2")).thenReturn(Mono.empty());

        StepVerifier.create(reportService.getMaxStockProduct("f1"))
                .expectNextMatches(response -> response.branchName().equals("KFC Centro"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe lanzar excepción si la franquicia no existe")
    void execute_ShouldThrowException_WhenFranchiseDoesNotExist() {
        when(franchiseRepositoryPort.findById("invalid")).thenReturn(Mono.empty());

        StepVerifier.create(reportService.getMaxStockProduct("invalid"))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }
}