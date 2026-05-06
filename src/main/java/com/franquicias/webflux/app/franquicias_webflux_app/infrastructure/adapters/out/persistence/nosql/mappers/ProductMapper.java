package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.mappers;

import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Product;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.valueobjects.Stock;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.entities.ProductDocument;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static Product toDomain(ProductDocument productDocument) {
        return new Product(
            productDocument.getId(), 
            productDocument.getName(), 
            new Stock(productDocument.getStock()), 
            productDocument.getBranchId()
        );
    }

    public static ProductDocument toEntity(Product product) {
        return ProductDocument.builder()
                .id(product.id())
                .name(product.name())
                .stock(product.stock().value())
                .branchId(product.branchId())
                .build();
    }
}