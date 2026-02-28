package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.CreateProductCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateProductStockCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.CreateProductUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.DeleteProductUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.UpdateProductStockUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.config.RequestValidator;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final CreateProductUseCase createProductUseCase;
    private final RequestValidator requestValidator;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        return request.bodyToMono(CreateProductCommand.class)
                .doOnNext(requestValidator::validate)
                .flatMap(createProductUseCase::createProduct)
                .flatMap(product -> ServerResponse.status(HttpStatus.CREATED).bodyValue(product));
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String productId = request.pathVariable("id");
        return deleteProductUseCase.deleteProduct(productId)
                .then(ServerResponse.noContent().build()); // 204 No Content
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        String productId = request.pathVariable("id");
        
        return request.bodyToMono(UpdateProductStockCommand.class)
                .map(body -> new UpdateProductStockCommand(productId, body.newStock())) 
                .doOnNext(requestValidator::validate)
                .flatMap(updateProductStockUseCase::updateProduct)
                .flatMap(product -> ServerResponse.ok().bodyValue(product)); // 200 OK
    }
}