package com.franquicias.webflux.app.franquicias_webflux_app.application.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateFranchiseCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateNameCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.FranchiseRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.FranchiseNotFoundException;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Franchise;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
        PublisherProbe<Franchise> saveProbe = PublisherProbe.of(Mono.just(expectedFranchise));

        when(repositoryPort.save(any(Franchise.class))).thenReturn(saveProbe.mono());

        StepVerifier.create(franchiseService.createFranchise(command))
                .expectNextMatches(franchise -> 
                        franchise.id().equals("123") && 
                        franchise.name().equals("Starbucks"))
                .verifyComplete();

        saveProbe.assertWasSubscribed();
    }

    @Test
    void execute_UpdateName_ShouldReturnUpdatedFranchise() {
        UpdateNameCommand command = new UpdateNameCommand("f1", "KFC Global");
        Franchise existingFranchise = new Franchise("f1", "KFC");
        Franchise savedFranchise = new Franchise("f1", "KFC Global");
        PublisherProbe<Franchise> saveProbe = PublisherProbe.of(Mono.just(savedFranchise));

        when(repositoryPort.findById("f1")).thenReturn(Mono.just(existingFranchise));
        when(repositoryPort.save(any(Franchise.class))).thenReturn(saveProbe.mono());

        StepVerifier.create(franchiseService.updateFranchiseName(command))
                .expectNextMatches(f -> f.name().equals("KFC Global"))
                .verifyComplete();

        saveProbe.assertWasSubscribed();
    }

    @Test
    void execute_UpdateName_ShouldThrowException_WhenFranchiseDoesNotExist() {
        UpdateNameCommand command = new UpdateNameCommand("invalid-id", "KFC Global");

        when(repositoryPort.findById("invalid-id")).thenReturn(Mono.empty());

        StepVerifier.create(franchiseService.updateFranchiseName(command))
                .expectError(FranchiseNotFoundException.class)
                .verify();

        verify(repositoryPort, never()).save(any(Franchise.class));
    }
}