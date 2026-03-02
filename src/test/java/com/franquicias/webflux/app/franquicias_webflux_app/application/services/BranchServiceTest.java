package com.franquicias.webflux.app.franquicias_webflux_app.application.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateBranchCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.BranchRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.FranchiseRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.FranchiseNotFoundException;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Branch;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {

    @Mock
    private BranchRepositoryPort branchRepositoryPort;
    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;

    @InjectMocks
    private BranchService branchService;

    @Test
    void execute_ShouldCreateBranch_WhenFranchiseExists() {
        // Arrange
        CreateBranchCommand command = new CreateBranchCommand("Sucursal Norte", "fran-1");
        Franchise existingFranchise =  new Franchise("fran-1", "Burger King");
        Branch expectedBranch = new Branch("branch-1", "Sucursal Norte", "fran-1");

        when(franchiseRepositoryPort.findById("fran-1")).thenReturn(Mono.just(existingFranchise));
        when(branchRepositoryPort.save(any(Branch.class))).thenReturn(Mono.just(expectedBranch));

        // Act & Assert
        StepVerifier.create(branchService.createBranch(command))
                .expectNextMatches(branch -> branch.id().equals("branch-1") && branch.name().equals("Sucursal Norte"))
                .verifyComplete();
    }

    @Test
    void execute_ShouldThrowException_WhenFranchiseDoesNotExist() {
        // Arrange
        CreateBranchCommand command = new CreateBranchCommand("Sucursal Sur", "invalid-id");

        when(franchiseRepositoryPort.findById("invalid-id")).thenReturn(Mono.empty()); // Franquicia no existe

        // Act & Assert
        StepVerifier.create(branchService.createBranch(command))
                .expectErrorMatches(throwable -> throwable instanceof FranchiseNotFoundException &&
                        throwable.getMessage().contains("invalid-id"))
                .verify();
    }
}