package com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateBranchCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Branch;

import reactor.core.publisher.Mono;

public interface CreateBranchUseCase {
    Mono<Branch> createBranch(CreateBranchCommand command);
}