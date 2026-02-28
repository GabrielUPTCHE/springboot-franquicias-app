package com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out;


import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Branch;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;

import reactor.core.publisher.Mono;

public interface BranchRepositoryPort {
    Mono<Branch> save(Branch branch);

}