package com.franquicias.webflux.app.franquicias_webflux_app.application.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.franquicias.webflux.app.franquicias_webflux_app.application.dto.query.ProductMaxStockResponse;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.in.GetMaxStockProductsUseCase;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.BranchRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.FranchiseRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.application.ports.out.ProductRepositoryPort;
import com.franquicias.webflux.app.franquicias_webflux_app.domain.exceptions.FranchiseNotFoundException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class FranchiseReportService implements GetMaxStockProductsUseCase {

    private final FranchiseRepositoryPort franchiseRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    @Override
    public Flux<ProductMaxStockResponse> getMaxStockProduct(String franchiseId) {
        log.info("Generando reporte de productos con mayor stock para la franquicia ID: {}", franchiseId);
        return franchiseRepositoryPort.findById(franchiseId)
                // 1. Validamos que la franquicia exista
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Reporte fallido: No se encontró la franquicia ID {}", franchiseId);
                    return Mono.error(new FranchiseNotFoundException(franchiseId));
                }))
                // 2. Pasamos de Mono a Flux obteniendo sus sucursales
                .flatMapMany(franchise -> branchRepositoryPort.findByFranchiseId(franchise.getId()))
                // 3. Por cada sucursal, buscamos su producto top y mapeamos la respuesta
                .flatMap(branch -> productRepositoryPort.findTopByBranchIdOrderByStockDesc(branch.getId())
                        .map(product -> new ProductMaxStockResponse(
                                product.getId(),
                                product.getName(),
                                product.getStock().value(),
                                branch.getId(),
                                branch.getName()
                        ))
                )
                .doOnComplete(() -> log.info("Reporte generado exitosamente para la franquicia ID: {}", franchiseId))
                .doOnError(error -> log.error("Error inesperado durante la generación del reporte: {}", error.getMessage()));
    }
}
