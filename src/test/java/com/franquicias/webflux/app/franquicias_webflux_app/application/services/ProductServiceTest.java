package com.franquicias.webflux.app.franquicias_webflux_app.application.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateProductCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateProductStockCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.BranchRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.ProductRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.BranchNotFoundException;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Branch;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Product;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.valueobjects.Stock;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;
    @Mock
    private BranchRepositoryPort branchRepositoryPort;

    @InjectMocks
    private ProductService productService;

    @Test
    void execute_ShouldCreateProduct_WhenBranchExists() {
        // Arrange
        CreateProductCommand command = new CreateProductCommand("Camiseta", 50, "branch-1");
        Branch existingBranch = Branch.builder().id("branch-1").name("Sucursal Centro").franchiseId("fran-1").build();
        Product expectedProduct = Product.builder().id("prod-1").name("Camiseta").stock(new Stock(50)).branchId("branch-1").build();

        when(branchRepositoryPort.findById("branch-1")).thenReturn(Mono.just(existingBranch));
        when(productRepositoryPort.save(any(Product.class))).thenReturn(Mono.just(expectedProduct));

        // Act & Assert
        StepVerifier.create(productService.createProduct(command))
                .expectNextMatches(product -> 
                        product.getId().equals("prod-1") && 
                        product.getStock().value().equals(50))
                .verifyComplete();
    }

    @Test
    void execute_ShouldThrowException_WhenBranchDoesNotExist() {
        // Arrange
        CreateProductCommand command = new CreateProductCommand("Camiseta", 50, "invalid-branch");

        when(branchRepositoryPort.findById("invalid-branch")).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(productService.createProduct(command))
                .expectErrorMatches(throwable -> throwable instanceof BranchNotFoundException &&
                        throwable.getMessage().contains("invalid-branch"))
                .verify();
    }

    @Test
    void deleteProduct_ShouldComplete_WhenProductExists() {
        Product existingProduct = Product.builder().id("prod-1").build();
        when(productRepositoryPort.findById("prod-1")).thenReturn(Mono.just(existingProduct));
        when(productRepositoryPort.deleteById("prod-1")).thenReturn(Mono.empty());

        StepVerifier.create(productService.deleteProduct("prod-1"))
                .verifyComplete();
    }

    @Test
    void updateStock_ShouldUpdateAndReturnProduct_WhenProductExists() {
        UpdateProductStockCommand command = new UpdateProductStockCommand("prod-1", 100);
        Product existingProduct = Product.builder().id("prod-1").stock(new Stock(10)).build();
        Product savedProduct = Product.builder().id("prod-1").stock(new Stock(100)).build();

        when(productRepositoryPort.findById("prod-1")).thenReturn(Mono.just(existingProduct));
        when(productRepositoryPort.save(any(Product.class))).thenReturn(Mono.just(savedProduct));

        StepVerifier.create(productService.updateProduct(command))
                .expectNextMatches(product -> product.getStock().value().equals(100))
                .verifyComplete();
    }
}
