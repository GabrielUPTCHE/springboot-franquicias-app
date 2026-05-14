package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.adapters;

import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.BranchRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Branch;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.mappers.BranchMapper;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.repositories.BranchReactiveMongoRepository;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.config.ConfigValues;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
public class BranchRepositoryAdapter implements BranchRepositoryPort {

    private final BranchReactiveMongoRepository repository;
    private final CircuitBreaker mongoReadCb;
    private final CircuitBreaker mongoWriteCb;

    public BranchRepositoryAdapter(BranchReactiveMongoRepository repository,
                                   CircuitBreakerRegistry registry) {
        this.repository = repository;
        this.mongoReadCb = registry.circuitBreaker("mongoRead");
        this.mongoWriteCb = registry.circuitBreaker("mongoWrite");
    }

    @Override
    public Mono<Branch> save(Branch branch) {
        return Mono.just(branch)
                .map(BranchMapper::toEntity)
                .flatMap(repository::save)
                .map(BranchMapper::toDomain)
                .timeout(Duration.ofSeconds(ConfigValues.RETRY_WRITE_SECONDS))
                .transformDeferred(CircuitBreakerOperator.of(mongoWriteCb))
                .onErrorResume(ex -> {
                    log.error("Fallo al guardar Branch: {}", ex.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Branch> findById(String id) {
        return repository.findById(id)
                .map(BranchMapper::toDomain)
                .timeout(Duration.ofSeconds(ConfigValues.RETRY_READ_SECONDS))
                .transformDeferred(CircuitBreakerOperator.of(mongoReadCb))
                .onErrorResume(ex -> {
                    log.error("Fallo al buscar Branch por id={}: {}", id, ex.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    public Flux<Branch> findByFranchiseId(String franchiseId) {
        return repository.findByFranchiseId(franchiseId)
                .map(BranchMapper::toDomain)
                .timeout(Duration.ofSeconds(ConfigValues.RETRY_READ_SECONDS))
                .transformDeferred(CircuitBreakerOperator.of(mongoReadCb))
                .onErrorResume(ex -> {
                    log.error("Fallo al buscar Branches por franchiseId={}: {}",
                            franchiseId, ex.getMessage());
                    return Flux.empty();
                });
    }
}