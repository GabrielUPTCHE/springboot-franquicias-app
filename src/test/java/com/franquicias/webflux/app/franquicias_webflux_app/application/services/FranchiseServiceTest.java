package com.franquicias.webflux.app.franquicias_webflux_app.application.services;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateFranchiseCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateNameCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.FranchiseRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceTest {

    @Mock
    private FranchiseRepositoryPort repositoryPort;

    @InjectMocks
    private FranchiseService franchiseService;

    @Test
    void execute_ShouldCreateAndReturnFranchise() {
        CreateFranchiseCommand command = new CreateFranchiseCommand("Starbucks");
        Franchise expectedFranchise = new Franchise("123", "Starbucks");
        when(repositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(expectedFranchise));
        StepVerifier.create(franchiseService.createFranchise(command))
                .expectNextMatches(franchise -> 
                        franchise.id().equals("123") && 
                        franchise.name().equals("Starbucks"))
                .verifyComplete();
    }

    @Test
    void execute_UpdateName_ShouldReturnUpdatedFranchise() {
        UpdateNameCommand command = new UpdateNameCommand("f1", "KFC Global");
        Franchise existingFranchise = new Franchise("f1","KFC");
        Franchise savedFranchise = new Franchise("f1", "KFC Global");

        when(repositoryPort.findById("f1")).thenReturn(Mono.just(existingFranchise));
        when(repositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(savedFranchise));

        StepVerifier.create(franchiseService.updateFranchiseName(command))
                .expectNextMatches(f -> f.name().equals("KFC Global"))
                .verifyComplete();
    }
}