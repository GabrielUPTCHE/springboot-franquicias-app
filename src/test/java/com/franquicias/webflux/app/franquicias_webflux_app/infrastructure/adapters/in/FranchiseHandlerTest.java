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

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateFranchiseCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateNameCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.query.ProductMaxStockResponse;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.CreateFranchiseUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.GetMaxStockProductsUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.UpdateFranchiseNameUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.config.GlobalExceptionHandler;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.config.RequestValidator;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.handlers.FranchiseHandler;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.routers.FranchiseRouter;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.exceptions.CustomValidationException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class FranchiseHandlerTest {

    @Mock private CreateFranchiseUseCase createFranchiseUseCase;
    @Mock private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    @Mock private GetMaxStockProductsUseCase getMaxStockProductsUseCase;
    @Mock private RequestValidator requestValidator;

    @InjectMocks
    private FranchiseHandler franchiseHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        FranchiseRouter router = new FranchiseRouter();
        
        ApplicationContext mockContext = mock(ApplicationContext.class);
        when(mockContext.getClassLoader()).thenReturn(getClass().getClassLoader());

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler(
                new DefaultErrorAttributes(), 
                mockContext, 
                ServerCodecConfigurer.create());

        webTestClient = WebTestClient.bindToRouterFunction(router.franchiseRoutes(franchiseHandler))
                .webFilter((exchange, chain) -> chain.filter(exchange).onErrorResume(ex -> globalExceptionHandler.handle(exchange, ex)))
                .build();
    }

    @Test
    @DisplayName("Debe crear una franquicia y retornar HTTP 201")
    void createFranchise_ShouldReturn201() {
        CreateFranchiseCommand command = new CreateFranchiseCommand("McDonalds");
        Franchise franchise = new Franchise("999", "McDonalds");

        when(requestValidator.validate(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(createFranchiseUseCase.createFranchise(any(CreateFranchiseCommand.class))).thenReturn(Mono.just(franchise));

        webTestClient.post()
                .uri("/api/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("999")
                .jsonPath("$.name").isEqualTo("McDonalds");
    }

    @Test
    @DisplayName("Debe retornar HTTP 400 cuando la validación falla (Camino Triste)")
    void createFranchise_WithBlankName_ShouldThrowException() {
        CreateFranchiseCommand invalidCommand = new CreateFranchiseCommand("");

        when(requestValidator.validate(any())).thenReturn(Mono.error(new CustomValidationException(List.of("El nombre es obligatorio"))));

        webTestClient.post()
                .uri("/api/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidCommand)
                .exchange()
                // Validamos que nuestro GlobalExceptionHandler atrapó el error y devolvió un 400
                .expectStatus().isBadRequest() 
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("El nombre es obligatorio"); 
    }

    @Test
    @DisplayName("Debe retornar HTTP 400 cuando el payload está vacío")
    void createFranchise_WithEmptyPayload_ShouldReturn400() {
        webTestClient.post()
                .uri("/api/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                // NO enviamos body
                .exchange()
                // Nuestro switchIfEmpty debería lanzar ResponseStatusException que el GlobalHandler traduce a 400
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("El cuerpo de la petición no puede estar vacío");
    }

    @Test
    @DisplayName("Debe actualizar el nombre de la franquicia y retornar HTTP 200")
    void updateFranchiseName_ShouldReturn200() {
        UpdateNameCommand payload = new UpdateNameCommand(null, "KFC Global");
        Franchise updatedFranchise = new Franchise("f1", "KFC Global");

        // ✅ CORRECCIÓN Validator
        when(requestValidator.validate(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(updateFranchiseNameUseCase.updateFranchiseName(any(UpdateNameCommand.class))).thenReturn(Mono.just(updatedFranchise));

        webTestClient.patch()
                .uri("/api/v1/franchises/f1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload) 
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("KFC Global");
    }

    @Test
    @DisplayName("Debe retornar HTTP 200 con el reporte de productos (Server-Sent Events / Flux Streaming)")
    void getMaxStockProducts_ShouldReturn200() {
        ProductMaxStockResponse response1 = new ProductMaxStockResponse("p1", "Pollo", 100, "b1", "Centro");
        ProductMaxStockResponse response2 = new ProductMaxStockResponse("p2", "Papas", 50, "b2", "Norte");

        when(getMaxStockProductsUseCase.getMaxStockProduct("f1")).thenReturn(Flux.just(response1, response2));

        webTestClient.get()
                .uri("/api/v1/franchises/f1/max-stock") // Ajusta esta URI a como la tengas en tu Router
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductMaxStockResponse.class)
                .hasSize(2)
                .contains(response1, response2);
    }
}