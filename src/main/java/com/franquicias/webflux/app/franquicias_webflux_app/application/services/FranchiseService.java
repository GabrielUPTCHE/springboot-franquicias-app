package com.franquicias.webflux.app.franquicias_webflux_app.application.services;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateFranchiseCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.CreateFranchiseUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.FranchiseRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FranchiseService implements CreateFranchiseUseCase {

    private final FranchiseRepositoryPort repositoryPort;

    @Override
    public Mono<Franchise> createFranchise(CreateFranchiseCommand command) {
        return Mono.just(command)
                .map(cmd -> Franchise.builder().name(cmd.name()).build())
                .flatMap(repositoryPort::save);
    }
}