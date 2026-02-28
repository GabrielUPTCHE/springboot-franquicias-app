package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateBranchCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.CreateBranchUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.config.RequestValidator;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchHandler {

    private final CreateBranchUseCase createBranchUseCase;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> createBranch(ServerRequest request) {
        return request.bodyToMono(CreateBranchCommand.class)
                .doOnNext(requestValidator::validate)
                .flatMap(createBranchUseCase::createBranch)
                .flatMap(branch -> ServerResponse.status(HttpStatus.CREATED).bodyValue(branch));
    }
}
