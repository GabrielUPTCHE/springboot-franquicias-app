package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.handlers;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateFranchiseCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.query.ProductMaxStockResponse;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.CreateFranchiseUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.GetMaxStockProductsUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.config.RequestValidator;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final RequestValidator requestValidator;
    private final GetMaxStockProductsUseCase getMaxStockProductsUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(CreateFranchiseCommand.class)
                .doOnNext(requestValidator::validate)
                .flatMap(createFranchiseUseCase::createFranchise)
                .flatMap(franchise -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(franchise));
    }

    public Mono<ServerResponse> getMaxStockProducts(ServerRequest request) {
        String franchiseId = request.pathVariable("id");
        return ServerResponse.ok()
                .body(getMaxStockProductsUseCase.getMaxStockProduct(franchiseId), ProductMaxStockResponse.class);
    }
}