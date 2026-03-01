package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateFranchiseCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateNameCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.CreateFranchiseUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.UpdateFranchiseNameUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.config.RequestValidator;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.handlers.FranchiseHandler;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.routers.FranchiseRouter;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.exceptions.CustomValidationException;

import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class FranchiseHandlerTest {

    @Mock
    private CreateFranchiseUseCase createFranchiseUseCase;
    @Mock
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase ;

    @Mock
    private RequestValidator requestValidator;

    @InjectMocks
    private FranchiseHandler franchiseHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        FranchiseRouter router = new FranchiseRouter();
        webTestClient = WebTestClient.bindToRouterFunction(router.franchiseRoutes(franchiseHandler)).build();
    }

    @Test
    void createFranchise_ShouldReturn201() {
        CreateFranchiseCommand command = new CreateFranchiseCommand("McDonalds");
        Franchise franchise = Franchise.builder().id("999").name("McDonalds").build();

        doNothing().when(requestValidator).validate(any());
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
    void createFranchise_WithBlankName_ShouldThrowException() {
        CreateFranchiseCommand invalidCommand = new CreateFranchiseCommand(""); 

        doThrow(new CustomValidationException(List.of("El nombre es obligatorio")))
                .when(requestValidator).validate(any());

        webTestClient.post()
                .uri("/api/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidCommand)
                .exchange()
                .expectStatus().is5xxServerError(); 
    }

    @Test
    void updateFranchiseName_ShouldReturn200() {
        UpdateNameCommand command = new UpdateNameCommand("f1", "KFC Global");
        Franchise updatedFranchise = Franchise.builder().id("f1").name("KFC Global").build();

        doNothing().when(requestValidator).validate(any());
        when(updateFranchiseNameUseCase.updateFranchiseName(any(UpdateNameCommand.class))).thenReturn(Mono.just(updatedFranchise));

        webTestClient.patch()
                .uri("/api/v1/franchises/f1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateNameCommand(null, "KFC Global")) // El handler inyecta el ID
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("KFC Global");
    }
}
