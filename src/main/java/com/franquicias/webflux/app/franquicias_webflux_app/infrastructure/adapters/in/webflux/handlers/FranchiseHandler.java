package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateFranchiseCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateNameCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.query.ProductMaxStockResponse;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.CreateFranchiseUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.GetMaxStockProductsUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.UpdateFranchiseNameUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.config.RequestValidator;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final RequestValidator requestValidator;
    private final GetMaxStockProductsUseCase getMaxStockProductsUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(CreateFranchiseCommand.class)
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no puede estar vacío")))
                .flatMap(requestValidator::validate)
                .flatMap(createFranchiseUseCase::createFranchise)
                .flatMap(franchise -> ServerResponse.status(HttpStatus.CREATED).bodyValue(franchise));
    }

    public Mono<ServerResponse> getMaxStockProducts(ServerRequest request) {
        String franchiseId = request.pathVariable("id");
        return ServerResponse.ok()
                .body(getMaxStockProductsUseCase.getMaxStockProduct(franchiseId), ProductMaxStockResponse.class);
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(UpdateNameCommand.class)
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no puede estar vacío")))
                .map(body -> new UpdateNameCommand(id, body.name()))
                .flatMap(requestValidator::validate)
                .flatMap(updateFranchiseNameUseCase::updateFranchiseName)
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise));
    }
}