package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.ProductRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Product;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.valueobjects.Stock;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.entities.ProductDocument;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.repositories.ProductReactiveMongoRepository;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final ProductReactiveMongoRepository repository;

    @Override
    public Mono<Product> save(Product product) {
        ProductDocument document = ProductDocument.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock().value()) // Extraemos el valor del VO
                .branchId(product.getBranchId())
                .build();

        return repository.save(document)
                .map(saved -> Product.builder()
                        .id(saved.getId())
                        .name(saved.getName())
                        .stock(new Stock(saved.getStock())) // Reconstruimos el VO
                        .branchId(saved.getBranchId())
                        .build());
    }

    @Override
    public Mono<Product> findById(String id) {
        return repository.findById(id)
                .map(doc -> Product.builder()
                        .id(doc.getId())
                        .name(doc.getName())
                        .stock(new Stock(doc.getStock()))
                        .branchId(doc.getBranchId())
                        .build());
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
    @Override
    public Mono<Product> findTopByBranchIdOrderByStockDesc(String branchId) {
        return repository.findFirstByBranchIdOrderByStockDesc(branchId)
                .map(doc -> Product.builder()
                        .id(doc.getId())
                        .name(doc.getName())
                        .stock(new Stock(doc.getStock()))
                        .branchId(doc.getBranchId())
                        .build());
    }
}