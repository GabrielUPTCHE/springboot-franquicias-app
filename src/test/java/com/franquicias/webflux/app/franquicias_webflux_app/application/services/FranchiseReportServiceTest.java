package com.franquicias.webflux.app.franquicias_webflux_app.application.services;

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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseReportServiceTest {

    @Mock private FranchiseRepositoryPort franchiseRepositoryPort;
    @Mock private BranchRepositoryPort branchRepositoryPort;
    @Mock private ProductRepositoryPort productRepositoryPort;

    @InjectMocks private FranchiseReportService reportService;

    @Test
    void execute_ShouldReturnMaxStockProducts_WhenFranchiseExists() {
        // Arrange
        Franchise franchise =  new Franchise("f1", "KFC");
        Branch branch1 =new Branch("b1", "KFC Centro", "f1");
        Branch branch2 = new Branch ("b2","KFC Norte", "f1");
        Product product1 =new Product("p1", "Pollo", new Stock(100), "b1");
        Product product2 = new Product("p2", "Papas", new Stock(50), "b2");

        when(franchiseRepositoryPort.findById("f1")).thenReturn(Mono.just(franchise));
        when(branchRepositoryPort.findByFranchiseId("f1")).thenReturn(Flux.just(branch1, branch2));
        when(productRepositoryPort.findTopByBranchIdOrderByStockDesc("b1")).thenReturn(Mono.just(product1));
        when(productRepositoryPort.findTopByBranchIdOrderByStockDesc("b2")).thenReturn(Mono.just(product2));

        // Act & Assert
        StepVerifier.create(reportService.getMaxStockProduct("f1"))
                .expectNextMatches(response -> 
                        response.productName().equals("Pollo") && response.branchName().equals("KFC Centro"))
                .expectNextMatches(response -> 
                        response.productName().equals("Papas") && response.branchName().equals("KFC Norte"))
                .verifyComplete();
    }

    @Test
    void execute_ShouldThrowException_WhenFranchiseDoesNotExist() {
        when(franchiseRepositoryPort.findById("invalid")).thenReturn(Mono.empty());

        StepVerifier.create(reportService.getMaxStockProduct("invalid"))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }
}