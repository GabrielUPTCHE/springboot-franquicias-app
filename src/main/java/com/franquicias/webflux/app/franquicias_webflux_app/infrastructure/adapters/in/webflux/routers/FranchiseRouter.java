package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.routers;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springdoc.core.annotations.RouterOperation;

import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.handlers.FranchiseHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

@Configuration
public class FranchiseRouter {

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/franchises",
            method = RequestMethod.POST,
            beanClass = FranchiseHandler.class,
            beanMethod = "createFranchise",
            operation = @Operation(
                operationId = "createFranchise",
                summary = "Crear una nueva franquicia",
                description = "Registra una nueva franquicia en la base de datos."
            )
        ),
        @RouterOperation(
            path = "/api/v1/franchises/{id}/max-stock",
            method = RequestMethod.GET,
            beanClass = FranchiseHandler.class,
            beanMethod = "getMaxStockProducts",
            operation = @Operation(
                operationId = "getMaxStockProducts",
                summary = "Obtener productos con mayor stock",
                description = "Devuelve una lista con el producto que tiene más stock en cada una de las sucursales de la franquicia.",
                parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID de la franquicia")
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/franchises/{id}/name",
            method = RequestMethod.PATCH,
            beanClass = FranchiseHandler.class,
            beanMethod = "updateFranchiseName",
            operation = @Operation(
                operationId = "updateFranchiseName",
                summary = "Actualizar el nombre de una franquicia",
                description = "Modifica únicamente el nombre de una franquicia existente.",
                parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID de la franquicia")
                }
            )
        )
    })
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/franchises", handler::createFranchise)
                .GET("/api/v1/franchises/{id}/max-stock", handler::getMaxStockProducts)
                .PATCH("/api/v1/franchises/{id}/name", handler::updateFranchiseName)
                .build();
    }
}