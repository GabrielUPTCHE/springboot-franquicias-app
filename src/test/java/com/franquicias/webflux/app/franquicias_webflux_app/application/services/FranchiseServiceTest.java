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
        Franchise expectedFranchise = Franchise.builder().id("123").name("Starbucks").build();
        when(repositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(expectedFranchise));
        StepVerifier.create(franchiseService.createFranchise(command))
                .expectNextMatches(franchise -> 
                        franchise.getId().equals("123") && 
                        franchise.getName().equals("Starbucks"))
                .verifyComplete();
    }

    @Test
    void execute_UpdateName_ShouldReturnUpdatedFranchise() {
        UpdateNameCommand command = new UpdateNameCommand("f1", "KFC Global");
        Franchise existingFranchise = Franchise.builder().id("f1").name("KFC").build();
        Franchise savedFranchise = Franchise.builder().id("f1").name("KFC Global").build();

        when(repositoryPort.findById("f1")).thenReturn(Mono.just(existingFranchise));
        when(repositoryPort.save(any(Franchise.class))).thenReturn(Mono.just(savedFranchise));

        StepVerifier.create(franchiseService.updateFranchiseName(command))
                .expectNextMatches(f -> f.getName().equals("KFC Global"))
                .verifyComplete();
    }
}