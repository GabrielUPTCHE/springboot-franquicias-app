package com.franquicias.webflux.app.franquicias_webflux_app.application.services;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateFranchiseCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateNameCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.CreateFranchiseUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.UpdateFranchiseNameUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.FranchiseRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.FranchiseNotFoundException;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class FranchiseService implements CreateFranchiseUseCase, UpdateFranchiseNameUseCase {

    private final FranchiseRepositoryPort repositoryPort;

    @Override
    public Mono<Franchise> createFranchise(CreateFranchiseCommand command) {
        return Mono.just(command)
                .map(cmd -> Franchise.builder().name(cmd.name()).build())
                .flatMap(repositoryPort::save)
                .doOnSuccess(franchise -> log.info("Franquicia '{}' creada exitosamente con ID: {}", 
                        franchise.getName(), franchise.getId()))
                .doOnError(error -> log.error("Fallo al crear la franquicia '{}'. Motivo: {}", 
                        command.name(), error.getMessage()));
    }

    @Override
    public Mono<Franchise> updateFranchiseName(UpdateNameCommand command) {
        return repositoryPort.findById(command.id())
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(command.id())))
                .map(franchise -> {
                    franchise.setName(command.name());
                    return franchise;
                })
                .flatMap(repositoryPort::save)
                .doOnSuccess(franchise -> log.info("Franquicia '{}' se actualizo su nombre exitosamente con ID: {}", 
                        franchise.getName(), franchise.getId()))
                .doOnError(error -> log.error("Fallo al actualizar el nombre de la franquicia '{}'. Motivo: {}", 
                        command.name(), error.getMessage()));
    }
}