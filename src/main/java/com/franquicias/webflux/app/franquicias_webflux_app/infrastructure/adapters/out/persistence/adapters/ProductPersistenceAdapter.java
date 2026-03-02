package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.ProductRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Product;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.valueobjects.Stock;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.entities.ProductDocument;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.mappers.ProductMapper;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.repositories.ProductReactiveMongoRepository;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final ProductReactiveMongoRepository repository;

    @Override
    public Mono<Product> save(Product product) {
        ProductDocument document = ProductMapper.toEntity(product);

        return repository.save(document)
                .map(saved -> ProductMapper.toDomain(document));
    }

    @Override
    public Mono<Product> findById(String id) {
        return repository.findById(id)
                .map(doc -> ProductMapper.toDomain(doc));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
    @Override
    public Mono<Product> findTopByBranchIdOrderByStockDesc(String branchId) {
        return repository.findFirstByBranchIdOrderByStockDesc(branchId)
                .map(doc -> ProductMapper.toDomain(doc));
    }
}