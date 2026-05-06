package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.webflux.error.DefaultErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateProductCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateNameCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateProductStockCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.CreateProductUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.DeleteProductUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.UpdateProductNameUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.UpdateProductStockUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Product;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.valueobjects.Stock;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.config.GlobalExceptionHandler;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.config.RequestValidator;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.handlers.ProductHandler;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.routers.ProductRouter;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.exceptions.CustomValidationException;

import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductHandlerTest {

    @Mock private CreateProductUseCase createProductUseCase;
    @Mock private DeleteProductUseCase deleteProductUseCase;
    @Mock private UpdateProductStockUseCase updateProductStockUseCase;
    @Mock private UpdateProductNameUseCase updateProductNameUseCase;
    @Mock private RequestValidator requestValidator;

    @InjectMocks private ProductHandler productHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        ApplicationContext mockContext = mock(ApplicationContext.class);
        when(mockContext.getClassLoader()).thenReturn(getClass().getClassLoader());

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler(
                new DefaultErrorAttributes(),
                mockContext,
                ServerCodecConfigurer.create());

        ProductRouter router = new ProductRouter();
        webTestClient = WebTestClient.bindToRouterFunction(router.productRoutes(productHandler))
                .webFilter((exchange, chain) -> chain.filter(exchange).onErrorResume(ex -> globalExceptionHandler.handle(exchange, ex)))
                .build();
    }

    @Test
    @DisplayName("Debe crear producto y retornar HTTP 201")
    void createProduct_ShouldReturn201() {
        CreateProductCommand command = new CreateProductCommand("Camiseta", 50, "branch-1");
        Product product = new Product("prod-1", "Camiseta", new Stock(50), "branch-1");

        when(requestValidator.validate(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(createProductUseCase.createProduct(any(CreateProductCommand.class))).thenReturn(Mono.just(product));

        webTestClient.post()
                .uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("prod-1")
                .jsonPath("$.name").isEqualTo("Camiseta");
    }

    @Test
    @DisplayName("Debe eliminar producto y retornar HTTP 204")
    void deleteProduct_ShouldReturn204() {
        when(deleteProductUseCase.deleteProduct("prod-1")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/products/prod-1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Debe actualizar el stock y retornar HTTP 200")
    void updateProductStock_ShouldReturn200() {
        UpdateProductStockCommand payload = new UpdateProductStockCommand(null, 100);
        Product updatedProduct = new Product("prod-1", "Camiseta", new Stock(100), "branch-1");

        when(requestValidator.validate(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(updateProductStockUseCase.updateProduct(any(UpdateProductStockCommand.class))).thenReturn(Mono.just(updatedProduct));

        webTestClient.patch()
                .uri("/api/v1/products/prod-1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stock.value").isEqualTo(100);
    }

    @Test
    @DisplayName("Debe actualizar el nombre y retornar HTTP 200")
    void updateProductName_ShouldReturn200() {
        UpdateNameCommand payload = new UpdateNameCommand(null, "Pantalón");
        Product updatedProduct = new Product("prod-1", "Pantalón", new Stock(50), "branch-1");

        when(requestValidator.validate(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(updateProductNameUseCase.updateProductName(any(UpdateNameCommand.class))).thenReturn(Mono.just(updatedProduct));

        webTestClient.patch()
                .uri("/api/v1/products/prod-1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Pantalón");
    }

    @Test
    @DisplayName("Debe retornar HTTP 400 por payload vacío")
    void updateProductStock_WithEmptyPayload_ShouldReturn400() {
        webTestClient.patch()
                .uri("/api/v1/products/prod-1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    @DisplayName("Debe retornar HTTP 400 cuando validación falla")
    void createProduct_WithInvalidData_ShouldReturn400() {
        CreateProductCommand invalidCommand = new CreateProductCommand("", -5, "branch-1");

        when(requestValidator.validate(any())).thenReturn(Mono.error(new CustomValidationException(List.of("Stock inválido"))));

        webTestClient.post()
                .uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidCommand)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }
}