package com.franquicias.webflux.app.franquicias_webflux_app.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateBranchCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateNameCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.CreateBranchUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.UpdateBranchNameUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.BranchRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.FranchiseRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.BranchNotFoundException;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.FranchiseNotFoundException;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Branch;

import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BranchService implements CreateBranchUseCase, UpdateBranchNameUseCase {

    private final BranchRepositoryPort branchRepositoryPort;
    private final FranchiseRepositoryPort franchiseRepositoryPort;

    @Override
    public Mono<Branch> createBranch(CreateBranchCommand command) {
        return franchiseRepositoryPort.findById(command.franchiseId())
                // Si el Mono está vacío, lanzamos la excepción de dominio
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(command.franchiseId())))
                .map(franchise -> Branch.builder()
                        .name(command.name())
                        .franchiseId(franchise.getId())
                        .build())
                .flatMap(branchRepositoryPort::save);
    }

    @Override
    public Mono<Branch> updateBranchName(UpdateNameCommand command) {
        return branchRepositoryPort.findById(command.id())
                .switchIfEmpty(Mono.error(new BranchNotFoundException(command.id())))
                .map(branch -> {
                    branch.setName(command.name());
                    return branch;
                })
                .flatMap(branchRepositoryPort::save);
    }
}