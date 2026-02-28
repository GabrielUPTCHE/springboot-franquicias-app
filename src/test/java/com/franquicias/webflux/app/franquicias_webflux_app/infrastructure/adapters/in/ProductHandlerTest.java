package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateProductStockCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.CreateProductUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.DeleteProductUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.UpdateProductStockUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Product;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.valueobjects.Stock;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.config.RequestValidator;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.handlers.ProductHandler;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.routers.ProductRouter;

import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductHandlerTest {

    @Mock private CreateProductUseCase createProductUseCase;
    @Mock private DeleteProductUseCase deleteProductUseCase;
    @Mock private UpdateProductStockUseCase updateProductStockUseCase;
    @Mock private RequestValidator requestValidator;

    @InjectMocks private ProductHandler productHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        // Asumiendo que AppRouter tiene este método, sino ajusta el nombre
        ProductRouter router = new ProductRouter(); 
        webTestClient = WebTestClient.bindToRouterFunction(router.productRoutes(productHandler)).build();
    }

    @Test
    void deleteProduct_ShouldReturn204() {
        when(deleteProductUseCase.deleteProduct("prod-1")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/products/prod-1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void updateProductStock_ShouldReturn200() {
        UpdateProductStockCommand command = new UpdateProductStockCommand("prod-1", 100);
        Product updatedProduct = Product.builder().id("prod-1").stock(new Stock(100)).build();

        doNothing().when(requestValidator).validate(any());
        when(updateProductStockUseCase.updateProduct(any(UpdateProductStockCommand.class))).thenReturn(Mono.just(updatedProduct));

        webTestClient.patch()
                .uri("/api/v1/products/prod-1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stock.value").isEqualTo(100);
    }
}