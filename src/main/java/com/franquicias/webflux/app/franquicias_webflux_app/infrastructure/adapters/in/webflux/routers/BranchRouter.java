package com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.routers;


import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.franquicias.webflux.app.franquicias_webflux_app.infrastructure.adapters.in.webflux.handlers.BranchHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

@Configuration
public class BranchRouter {
    
    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/branches",
            method = RequestMethod.POST,
            beanClass = BranchHandler.class,
            beanMethod = "createBranch",
            operation = @Operation(
                operationId = "createBranch",
                summary = "Crear una nueva sucursal",
                description = "Crea una sucursal y la asocia a una franquicia."
            )
        ),
        @RouterOperation(
            path = "/api/v1/branches/{id}/name",
            method = RequestMethod.PATCH,
            beanClass = BranchHandler.class,
            beanMethod = "updateBranchName",
            operation = @Operation(
                operationId = "updateBranchName",
                summary = "Actualizar nombre de sucursal",
                description = "Modifica únicamente el nombre de una sucursal existente por su ID.",
                parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID de la sucursal")
                }
            )
        )
    })
    public RouterFunction<ServerResponse> branchRoutes(BranchHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/branches", handler::createBranch)
                .PATCH("/api/v1/branches/{id}/name", handler::updateBranchName)
                .build();
    }
}