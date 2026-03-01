package com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.command.UpdateNameCommand;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.models.Product;

import reactor.core.publisher.Mono;

public interface UpdateProductNameUseCase {
    Mono<Product> updateProductName(UpdateNameCommand command);
}