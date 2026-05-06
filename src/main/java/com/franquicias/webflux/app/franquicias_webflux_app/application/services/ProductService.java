package com.franquicias.webflux.app.franquicias_webflux_app.application.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateProductCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateNameCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateProductStockCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.CreateProductUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.DeleteProductUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.UpdateProductNameUseCase;
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
@Slf4j
public class ProductService implements CreateProductUseCase, DeleteProductUseCase, UpdateProductStockUseCase, UpdateProductNameUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;

    @Override
    public Mono<Product> createProduct(CreateProductCommand command) {
        return branchRepositoryPort.findById(command.branchId())
                .switchIfEmpty(Mono.error(() -> new BranchNotFoundException(command.branchId())))
                .map(branch -> new Product(UUID.randomUUID().toString(), command.name(), new Stock(command.stock()), branch.id()))
                .flatMap(productRepositoryPort::save)
                .doOnSuccess(product -> log.info("Producto '{}' creado exitosamente con ID: {}", product.name(), product.id()))
                .doOnError(error -> log.error("Fallo al crear el Producto '{}'. Motivo: {}", command.name(), error.getMessage()));
    }

    @Override
    public Mono<Void> deleteProduct(String productId) {
        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(() -> new ProductNotFoundException(productId)))
                .flatMap(product -> productRepositoryPort.deleteById(product.id()))
                .doOnSuccess(unused -> log.info("Producto eliminado exitosamente con ID: {}", productId))
                .doOnError(error -> log.error("Fallo al eliminar el producto '{}'. Motivo: {}", productId, error.getMessage()));
    }

    @Override
    public Mono<Product> updateProduct(UpdateProductStockCommand command) {
        return productRepositoryPort.findById(command.productId())
                .switchIfEmpty(Mono.error(() -> new ProductNotFoundException(command.productId())))
                .map(product -> new Product(product.id(), product.name(), new Stock(command.newStock()), product.branchId()))
                .flatMap(productRepositoryPort::save)
                .doOnSuccess(product -> log.info("Stock de producto '{}' actualizado exitosamente con ID: {}", product.name(), product.id()))
                .doOnError(error -> log.error("Fallo al actualizar el stock del producto '{}'. Motivo: {}", command.productId(), error.getMessage()));
    }

    @Override
    public Mono<Product> updateProductName(UpdateNameCommand command) {
        return productRepositoryPort.findById(command.id())
                .switchIfEmpty(Mono.error(() -> new ProductNotFoundException(command.id())))
                .map(product -> new Product(product.id(), command.name(), product.stock(), product.branchId()))
                .flatMap(productRepositoryPort::save)
                .doOnSuccess(product -> log.info("Nombre de producto '{}' actualizado exitosamente con ID: {}", product.name(), product.id()))
                .doOnError(error -> log.error("Fallo al actualizar el nombre del producto '{}'. Motivo: {}", command.name(), error.getMessage()));
    }
}