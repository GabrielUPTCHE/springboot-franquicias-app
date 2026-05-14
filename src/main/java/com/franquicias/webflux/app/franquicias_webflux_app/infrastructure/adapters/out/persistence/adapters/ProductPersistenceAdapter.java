package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.adapters;

import lombok.RequiredArgsConstructor;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.ProductRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Product;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.mappers.ProductMapper;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.repositories.ProductReactiveMongoRepository;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.config.ConfigValues;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final ProductReactiveMongoRepository repository;

    @Override
    public Mono<Product> save(Product product) {
        return Mono.just(product)
                .map(ProductMapper::toEntity)
                .flatMap(repository::save)
                .map(ProductMapper::toDomain)
                .timeout(Duration.ofSeconds(ConfigValues.RETRY_WRITE_SECONDS))
                .retryWhen(
                    Retry.backoff(3, Duration.ofMillis(100))
                    .maxBackoff(Duration.ofSeconds(2))
                    .jitter(0.5)
                    //importante para filtrar solo errores transitorios, si no se filtran, se reintentará incluso en errores de validación o de negocio, lo cual no es deseable
                    //.filter(this::isTransient)
                    .onRetryExhaustedThrow((spec, signal) -> signal.failure())
                ); 
    }

    @Override
    public Mono<Product> findById(String id) {
        return repository.findById(id)
                .map(ProductMapper::toDomain)
                .timeout(Duration.ofSeconds(ConfigValues.RETRY_READ_SECONDS));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id)
                .timeout(Duration.ofSeconds(ConfigValues.RETRY_WRITE_SECONDS));
    }
    
    @Override
    public Mono<Product> findTopByBranchIdOrderByStockDesc(String branchId) {
        return repository.findFirstByBranchIdOrderByStockDesc(branchId)
                .map(ProductMapper::toDomain)
                .timeout(Duration.ofSeconds(ConfigValues.RETRY_READ_SECONDS));
    }
}