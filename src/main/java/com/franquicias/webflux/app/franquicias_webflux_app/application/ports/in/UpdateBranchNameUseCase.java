package com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateNameCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Branch;

import reactor.core.publisher.Mono;

public interface UpdateBranchNameUseCase {
    Mono<Branch> updateBranchName(UpdateNameCommand command);
}