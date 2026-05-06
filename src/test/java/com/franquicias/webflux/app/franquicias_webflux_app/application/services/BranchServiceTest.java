package com.franquicias.webflux.app.franquicias_webflux_app.application.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateBranchCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateNameCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.BranchRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.FranchiseRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.BranchNotFoundException;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.FranchiseNotFoundException;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Branch;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {

    @Mock
    private BranchRepositoryPort branchRepositoryPort;
    @Mock
    private FranchiseRepositoryPort franchiseRepositoryPort;

    @InjectMocks
    private BranchService branchService;

    @Test
    @DisplayName("Debe crear una sucursal y asegurar la suscripción al repositorio cuando la franquicia existe")
    void createBranch_ShouldCreateBranch_WhenFranchiseExists() {
        CreateBranchCommand command = new CreateBranchCommand("Sucursal Norte", "fran-1");
        Franchise existingFranchise = new Franchise("fran-1", "Burger King");
        Branch expectedBranch = new Branch("branch-1", "Sucursal Norte", "fran-1");

        PublisherProbe<Branch> saveProbe = PublisherProbe.of(Mono.just(expectedBranch));

        when(franchiseRepositoryPort.findById("fran-1")).thenReturn(Mono.just(existingFranchise));
        when(branchRepositoryPort.save(any(Branch.class))).thenReturn(saveProbe.mono()); 

        StepVerifier.create(branchService.createBranch(command))
                .expectNextMatches(branch -> branch.id().equals("branch-1") && branch.name().equals("Sucursal Norte"))
                .verifyComplete();

        saveProbe.assertWasSubscribed();
    }

    @Test
    @DisplayName("Debe lanzar excepción y no guardar cuando la franquicia no existe")
    void createBranch_ShouldThrowException_WhenFranchiseDoesNotExist() {
        CreateBranchCommand command = new CreateBranchCommand("Sucursal Sur", "invalid-id");

        when(franchiseRepositoryPort.findById("invalid-id")).thenReturn(Mono.empty());

        StepVerifier.create(branchService.createBranch(command))
                .expectErrorMatches(throwable -> throwable instanceof FranchiseNotFoundException &&
                        throwable.getMessage().contains("invalid-id"))
                .verify();

        verify(branchRepositoryPort, never()).save(any(Branch.class));
    }


    @Test
    @DisplayName("Debe actualizar el nombre de la sucursal cuando existe")
    void updateBranchName_ShouldUpdate_WhenBranchExists() {
        // Arrange
        UpdateNameCommand command = new UpdateNameCommand("branch-1", "Nuevo Nombre Sur");
        Branch existingBranch = new Branch("branch-1", "Viejo Nombre", "fran-1");
        Branch updatedBranch = new Branch("branch-1", "Nuevo Nombre Sur", "fran-1");

        PublisherProbe<Branch> saveProbe = PublisherProbe.of(Mono.just(updatedBranch));

        when(branchRepositoryPort.findById("branch-1")).thenReturn(Mono.just(existingBranch));
        when(branchRepositoryPort.save(any(Branch.class))).thenReturn(saveProbe.mono());

        StepVerifier.create(branchService.updateBranchName(command))
                .expectNextMatches(branch -> branch.name().equals("Nuevo Nombre Sur") && branch.id().equals("branch-1"))
                .verifyComplete();

        saveProbe.assertWasSubscribed();
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar nombre si la sucursal no existe")
    void updateBranchName_ShouldThrowException_WhenBranchDoesNotExist() {
        UpdateNameCommand command = new UpdateNameCommand("invalid-branch", "Nuevo Nombre");

        when(branchRepositoryPort.findById("invalid-branch")).thenReturn(Mono.empty());

        StepVerifier.create(branchService.updateBranchName(command))
                .expectError(BranchNotFoundException.class)
                .verify();

        verify(branchRepositoryPort, never()).save(any(Branch.class));
    }
}