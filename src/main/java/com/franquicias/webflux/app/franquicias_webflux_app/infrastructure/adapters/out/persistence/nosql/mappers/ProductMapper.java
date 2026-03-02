package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.mappers;

import org.springframework.stereotype.Component;

import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Product;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.valueobjects.Stock;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.out.persistence.nosql.entities.ProductDocument;

@Component
public class ProductMapper {

    public static Product toDomain(ProductDocument productDocument) {
        if (productDocument == null) return null;
        
        return  new Product(
            productDocument.getId(), 
            productDocument.getName(), 
            new Stock(productDocument.getStock()), 
            productDocument.getBranchId());
    }

    public static ProductDocument toEntity(Product product) {
        if (product == null) return null;
        
        return ProductDocument.builder()
                .id(product.id())
                .name(product.name())
                .stock(product.stock().value())
                .branchId(product.branchId())
                .build();
    }
}