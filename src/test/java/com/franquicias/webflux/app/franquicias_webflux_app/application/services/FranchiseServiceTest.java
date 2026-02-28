package com.franquicias.webflux.app.franquicias_webflux_app.application.services;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateFranchiseCommand;
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
}