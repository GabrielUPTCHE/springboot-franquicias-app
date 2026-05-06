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

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateBranchCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateNameCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.CreateBranchUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.UpdateBranchNameUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Branch;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.config.GlobalExceptionHandler;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.config.RequestValidator;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.handlers.BranchHandler;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.routers.BranchRouter;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.exceptions.CustomValidationException;

import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchHandlerTest {

    @Mock private CreateBranchUseCase createBranchUseCase;
    @Mock private UpdateBranchNameUseCase updateBranchNameUseCase;
    @Mock private RequestValidator requestValidator;

    @InjectMocks private BranchHandler branchHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        ApplicationContext mockContext = mock(ApplicationContext.class);
        when(mockContext.getClassLoader()).thenReturn(getClass().getClassLoader());

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler(
                new DefaultErrorAttributes(),
                mockContext,
                ServerCodecConfigurer.create());

        BranchRouter router = new BranchRouter();
        webTestClient = WebTestClient.bindToRouterFunction(router.branchRoutes(branchHandler))
                .webFilter((exchange, chain) -> chain.filter(exchange).onErrorResume(ex -> globalExceptionHandler.handle(exchange, ex)))
                .build();
    }

    @Test
    @DisplayName("Debe crear una sucursal y retornar HTTP 201")
    void createBranch_ShouldReturn201() {
        CreateBranchCommand command = new CreateBranchCommand("Sucursal Norte", "fran-1");
        Branch branch = new Branch("branch-1", "Sucursal Norte", "fran-1");

        when(requestValidator.validate(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(createBranchUseCase.createBranch(any(CreateBranchCommand.class))).thenReturn(Mono.just(branch));

        webTestClient.post()
                .uri("/api/v1/branches") 
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("branch-1")
                .jsonPath("$.name").isEqualTo("Sucursal Norte")
                .jsonPath("$.franchiseId").isEqualTo("fran-1");
    }

    @Test
    @DisplayName("Debe retornar HTTP 400 al intentar crear con payload vacío")
    void createBranch_WithEmptyPayload_ShouldReturn400() {
        webTestClient.post()
                .uri("/api/v1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("El cuerpo de la petición no puede estar vacío");
    }

    @Test
    @DisplayName("Debe retornar HTTP 400 cuando falla la validación al crear")
    void createBranch_WithInvalidData_ShouldReturn400() {
        CreateBranchCommand invalidCommand = new CreateBranchCommand("", "fran-1");

        when(requestValidator.validate(any())).thenReturn(Mono.error(new CustomValidationException(List.of("El nombre no puede estar vacío"))));

        webTestClient.post()
                .uri("/api/v1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidCommand)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("El nombre no puede estar vacío");
    }

    @Test
    @DisplayName("Debe actualizar el nombre de la sucursal y retornar HTTP 200")
    void updateBranchName_ShouldReturn200() {
        UpdateNameCommand command = new UpdateNameCommand(null, "Sucursal Sur");
        Branch updatedBranch = new Branch("branch-1", "Sucursal Sur", "fran-1");

        when(requestValidator.validate(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(updateBranchNameUseCase.updateBranchName(any(UpdateNameCommand.class))).thenReturn(Mono.just(updatedBranch));

        webTestClient.patch()
                .uri("/api/v1/branches/branch-1/name") // Ajusta la URI según tu router
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("branch-1")
                .jsonPath("$.name").isEqualTo("Sucursal Sur");
    }

    @Test
    @DisplayName("Debe retornar HTTP 400 al intentar actualizar con payload vacío")
    void updateBranchName_WithEmptyPayload_ShouldReturn400() {
        webTestClient.patch()
                .uri("/api/v1/branches/branch-1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("El cuerpo de la petición no puede estar vacío");
    }

    @Test
    @DisplayName("Debe retornar HTTP 400 cuando falla la validación al actualizar")
    void updateBranchName_WithInvalidData_ShouldReturn400() {
        UpdateNameCommand invalidCommand = new UpdateNameCommand(null, "");

        when(requestValidator.validate(any())).thenReturn(Mono.error(new CustomValidationException(List.of("El nombre no puede estar vacío"))));

        webTestClient.patch()
                .uri("/api/v1/branches/branch-1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidCommand)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }
}