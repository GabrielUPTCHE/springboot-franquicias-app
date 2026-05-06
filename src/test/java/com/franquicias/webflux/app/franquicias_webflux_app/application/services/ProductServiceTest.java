package com.franquicias.webflux.app.franquicias_webflux_app.application.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateProductCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateNameCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateProductStockCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.BranchRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.ProductRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.BranchNotFoundException;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.ProductNotFoundException;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Branch;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Product;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.valueobjects.Stock;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.test.publisher.PublisherProbe;
import reactor.util.context.Context;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
    void execute_ShouldCreateProduct_WhenBranchExists_WithContextAndProbe() {
        CreateProductCommand command = new CreateProductCommand("Camiseta", 50, "branch-1");
        Branch existingBranch = new Branch("branch-1", "Sucursal Centro", "fran-1");
        Product expectedProduct = new Product("prod-1", "Camiseta", new Stock(50), "branch-1");

        PublisherProbe<Product> saveProbe = PublisherProbe.of(Mono.just(expectedProduct));

        when(branchRepositoryPort.findById("branch-1")).thenReturn(Mono.just(existingBranch));
        when(productRepositoryPort.save(any(Product.class))).thenReturn(saveProbe.mono());

        // CONTEXT: Demuestra cómo propagar variables de estado (ej. un token o user ID) en el flujo reactivo del test
        StepVerifierOptions options = StepVerifierOptions.create().withInitialContext(Context.of("userId", "admin-123"));

        StepVerifier.create(productService.createProduct(command), options)
                .expectNextMatches(product -> product.id().equals("prod-1") && product.stock().value() == 50)
                .verifyComplete();

        saveProbe.assertWasSubscribed();
    }

    @Test
    void execute_ShouldThrowException_WhenBranchDoesNotExist() {
        CreateProductCommand command = new CreateProductCommand("Camiseta", 50, "invalid-branch");

        when(branchRepositoryPort.findById("invalid-branch")).thenReturn(Mono.empty());

        StepVerifier.create(productService.createProduct(command))
                .expectError(BranchNotFoundException.class)
                .verify();

        verify(productRepositoryPort, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_ShouldComplete_WhenProductExists_WithVirtualTime() {
        Product existingProduct = new Product("prod-1", "Camiseta", new Stock(10), "branch-1");
        PublisherProbe<Void> deleteProbe = PublisherProbe.empty();

        when(productRepositoryPort.findById("prod-1")).thenReturn(Mono.just(existingProduct));
        // VIRTUAL TIME: Simulamos que la BD tarda 5 segundos en borrar el registro
        when(productRepositoryPort.deleteById("prod-1"))
                .thenReturn(deleteProbe.mono().delaySubscription(Duration.ofSeconds(5)));

        StepVerifier.withVirtualTime(() -> productService.deleteProduct("prod-1"))
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(5)) // Adelantamos el reloj virtual
                .verifyComplete();

        deleteProbe.assertWasSubscribed();
    }

    @Test
    void deleteProduct_ShouldThrowException_WhenProductDoesNotExist() {
        when(productRepositoryPort.findById("invalid-prod")).thenReturn(Mono.empty());

        StepVerifier.create(productService.deleteProduct("invalid-prod"))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void updateStock_ShouldUpdateAndReturnProduct_WithBackpressure() {
        UpdateProductStockCommand command = new UpdateProductStockCommand("prod-1", 100);
        Product existingProduct = new Product("prod-1", "Camiseta", new Stock(10), "branch-1");
        Product savedProduct = new Product("prod-1", "Camiseta", new Stock(100), "branch-1");

        PublisherProbe<Product> saveProbe = PublisherProbe.of(Mono.just(savedProduct));

        when(productRepositoryPort.findById("prod-1")).thenReturn(Mono.just(existingProduct));
        when(productRepositoryPort.save(any(Product.class))).thenReturn(saveProbe.mono());

        // BACKPRESSURE: En un Mono, controlamos explícitamente la demanda solicitando 1 solo elemento.
        StepVerifier.create(productService.updateProduct(command), 0)
                .thenRequest(1)
                .expectNextMatches(product -> product.stock().value() == 100)
                .verifyComplete();

        saveProbe.assertWasSubscribed();
    }

    @Test
    void updateProductName_ShouldUpdateAndReturnProduct() {
        UpdateNameCommand command = new UpdateNameCommand("prod-1", "Camiseta Polo");
        Product existingProduct = new Product("prod-1", "Camiseta", new Stock(10), "branch-1");
        Product savedProduct = new Product("prod-1", "Camiseta Polo", new Stock(10), "branch-1");

        PublisherProbe<Product> saveProbe = PublisherProbe.of(Mono.just(savedProduct));

        when(productRepositoryPort.findById("prod-1")).thenReturn(Mono.just(existingProduct));
        when(productRepositoryPort.save(any(Product.class))).thenReturn(saveProbe.mono());

        StepVerifier.create(productService.updateProductName(command))
                .expectNextMatches(product -> product.name().equals("Camiseta Polo"))
                .verifyComplete();

        saveProbe.assertWasSubscribed();
    }

    @Test
    void updateProductName_ShouldThrowException_WhenProductDoesNotExist() {
        UpdateNameCommand command = new UpdateNameCommand("invalid-prod", "Camiseta Polo");

        when(productRepositoryPort.findById("invalid-prod")).thenReturn(Mono.empty());

        StepVerifier.create(productService.updateProductName(command))
                .expectError(ProductNotFoundException.class)
                .verify();

        verify(productRepositoryPort, never()).save(any(Product.class));
    }
}