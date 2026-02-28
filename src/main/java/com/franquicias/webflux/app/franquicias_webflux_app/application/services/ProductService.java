package com.franquicias.webflux.app.franquicias_webflux_app.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateProductCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateProductStockCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.CreateProductUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.DeleteProductUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.UpdateProductStockUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.BranchRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.ProductRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.BranchNotFoundException;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.ProductNotFoundException;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Product;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.valueobjects.Stock;

import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService implements CreateProductUseCase, DeleteProductUseCase, UpdateProductStockUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;

    @Override
    public Mono<Product> createProduct(CreateProductCommand command) {
        return branchRepositoryPort.findById(command.branchId())
                .switchIfEmpty(Mono.error(new BranchNotFoundException(command.branchId())))
                .map(branch -> Product.builder()
                        .name(command.name())
                        .stock(new Stock(command.stock()))
                        .branchId(branch.getId())
                        .build())
                .flatMap(productRepositoryPort::save);
    }

    @Override
    public Mono<Void> deleteProduct(String productId) {
        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(productId)))
                .flatMap(product -> productRepositoryPort.deleteById(product.getId()));
    }

    @Override
    public Mono<Product> updateProduct(UpdateProductStockCommand command) {
        return productRepositoryPort.findById(command.productId())
                .switchIfEmpty(Mono.error(new ProductNotFoundException(command.productId())))
                .map(product -> {
                    product.setStock(new Stock(command.newStock())); // Re-asignamos el VO validado
                    return product;
                })
                .flatMap(productRepositoryPort::save);
    }
}