package com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateFranchiseCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;

import reactor.core.publisher.Mono;

public interface CreateFranchiseUseCase {
    Mono<Franchise> createFranchise(CreateFranchiseCommand command);
}